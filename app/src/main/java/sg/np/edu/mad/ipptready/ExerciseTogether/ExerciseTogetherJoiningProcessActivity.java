package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.uk.tastytoasty.TastyToasty;

import org.w3c.dom.DocumentType;

import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;
import sg.np.edu.mad.ipptready.InternetConnectivity.Internet;
import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherJoiningProcessActivity extends AppCompatActivity {
    // Exercise Together feature done by: BRYAN KOH

    // Global variables
    private String dateJoined = "";
    private String exercise = "";
    private String sessionName = "";
    private String status = "";
    DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together_joining_process);

        // Get Intent
        Intent qrIntent = getIntent();
        String qrCode = qrIntent.getStringExtra("qrCode");
        String userId = qrIntent.getStringExtra("userId");

        // If QR Code is invalid, return to ExerciseTogetherJoinActivity
        try {
            if (qrCode.length() > 0)
            {
                boolean hostUserIDObtained = false;
                String hostUserId = "";
                String dateCreated = "";
                // Separate qrCode details at "&" to get hostUserId and dateCreated
                while (true)
                {
                    if (qrCode.equals("")) break;
                    else
                    {
                        String nextChar = qrCode.substring(0,1);
                        if (nextChar.equals("&") && hostUserIDObtained == false)
                        {
                            qrCode = qrCode.substring(1);
                            hostUserIDObtained = true;
                            continue;
                        }
                        else
                        {
                            if (hostUserIDObtained == false)
                            {
                                hostUserId += nextChar;
                            }
                            else
                            {
                                dateCreated += nextChar;
                            }
                            qrCode = qrCode.substring(1);
                        }
                    }
                }
                Log.d("Host UserID", hostUserId);
                Log.d("Date Created", dateCreated);

                // Check internet connection
                Internet internet = new Internet();
                if (internet.isOnline(ExerciseTogetherJoiningProcessActivity.this))
                {
                    String finalHostUserId = hostUserId;
                    String finalDateCreated = dateCreated;

                    // Check if session exists
                    ExerciseTogetherSession.getSessionsbyUserID(hostUserId)
                            .document(dateCreated)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        if (task.getResult().exists()) {
                                            documentSnapshot = task.getResult();
                                            exercise = (String) documentSnapshot.getData().get("exercise");
                                            sessionName = (String) documentSnapshot.getData().get("sessionName");
                                            status = (String) documentSnapshot.getData().get("status");

                                            // Allow joining only when session has not started or has not completed
                                            if (!status.equals("Started") && !status.equals("Completed"))
                                            {
                                                // If user is not the host user
                                                if (!finalHostUserId.equals(userId)) {
                                                    // Create new session, generate QR code and go to waiting room
                                                    ExerciseTogetherSession session = new ExerciseTogetherSession(finalDateCreated, sessionName, exercise, finalHostUserId);
                                                    String qrdetails = session.hostUserID + "&" + session.dateCreated;
                                                    session.qrString = qrdetails;
                                                    FirebaseDocChange firebaseDocChange = ExerciseTogetherSession.createNewSession(userId, session);
                                                    firebaseDocChange.changeTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("DEBUG", qrdetails);
                                                                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                                                                Bitmap bitmap = null;

                                                                try {
                                                                    BitMatrix bitMatrix = qrCodeWriter.encode(qrdetails, BarcodeFormat.QR_CODE, 400, 400);
                                                                    bitmap = CreateImage(bitMatrix);

                                                                    FirebaseDocChange firebaseDocChangeJoinSession = ExerciseTogetherSession.joinSession(userId, qrdetails);
                                                                    Bitmap finalBitmap = bitmap;
                                                                    firebaseDocChangeJoinSession.changeTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                // Go to waiting room
                                                                                Bundle bundle = new Bundle();
                                                                                bundle.putString("date", session.dateCreated);
                                                                                bundle.putString("sessionName", session.sessionName);
                                                                                bundle.putString("exercise", session.exercise);
                                                                                bundle.putString("userId", userId);
                                                                                bundle.putParcelable("QRImage", finalBitmap);
                                                                                bundle.putString("QRString", qrdetails);
                                                                                bundle.putString("hostUserId", session.hostUserID);
                                                                                Intent beginSession = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherWaitingRoomActivity.class);
                                                                                TastyToasty.makeText(ExerciseTogetherJoiningProcessActivity.this, "Joined Session: " + session.sessionName, TastyToasty.SHORT, null, R.color.success, R.color.white, false).show();
                                                                                beginSession.putExtras(bundle);
                                                                                startActivity(beginSession);
                                                                                finish();
                                                                            }
                                                                            else {
                                                                                TastyToasty.error(ExerciseTogetherJoiningProcessActivity.this, "Unexpected error occurred").show();
                                                                                finish();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                catch (WriterException we)
                                                                {
                                                                    Log.e("Error", "Unable to create QR code.");
                                                                    Log.d("joinfail2", "I failed!");
                                                                    TastyToasty.error(ExerciseTogetherJoiningProcessActivity.this, "Unable to generate QR code.").show();
                                                                    finish();
                                                                }


                                                            }
                                                            else {
                                                                TastyToasty.error(ExerciseTogetherJoiningProcessActivity.this, "Unexpected error occurred").show();
                                                                Log.d("joinfail3", "I failed!");
                                                                finish();
                                                            }
                                                        }
                                                    });
                                                }
                                                else
                                                {
                                                    // If user is host user, generate qr code and enter waiting room
                                                    String qrdetails = userId + "&" + finalDateCreated;
                                                    Log.d("DEBUG", qrdetails);
                                                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                                                    Bitmap bitmap = null;

                                                    try {
                                                        BitMatrix bitMatrix = qrCodeWriter.encode(qrdetails, BarcodeFormat.QR_CODE, 400, 400);
                                                        bitmap = CreateImage(bitMatrix);

                                                        FirebaseDocChange firebaseDocChangeJoinSessionStatus = ExerciseTogetherSession.updateJoinStatus(userId, qrdetails, "Joined");
                                                        Bitmap finalBitmap = bitmap;
                                                        firebaseDocChangeJoinSessionStatus.changeTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    Bundle bundle = new Bundle();
                                                                    bundle.putString("date", finalDateCreated);
                                                                    bundle.putString("sessionName", sessionName);
                                                                    bundle.putString("exercise", exercise);
                                                                    bundle.putString("userId", userId);
                                                                    bundle.putParcelable("QRImage", finalBitmap);
                                                                    bundle.putString("QRString", qrdetails);
                                                                    bundle.putString("hostUserId", userId);
                                                                    Intent beginSession = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherWaitingRoomActivity.class);
                                                                    TastyToasty.makeText(ExerciseTogetherJoiningProcessActivity.this, "Joined Session: " + sessionName, TastyToasty.SHORT, null, R.color.success, R.color.white, false).show();
                                                                    beginSession.putExtras(bundle);
                                                                    startActivity(beginSession);
                                                                    finish();
                                                                }
                                                                else {
                                                                    TastyToasty.error(ExerciseTogetherJoiningProcessActivity.this, "Unexpected error occurred").show();
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                                    }
                                                    catch (WriterException we)
                                                    {
                                                        Log.e("Error", "Unable to create QR code.");
                                                        Log.d("joinfail4", "I failed!");
                                                        TastyToasty.error(ExerciseTogetherJoiningProcessActivity.this, "Unable to generate QR code.").show();
                                                        Intent failedIntent = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherSession.class);
                                                        failedIntent.putExtra("userId", userId);
                                                        finish();
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                TastyToasty.error(ExerciseTogetherJoiningProcessActivity.this, "Unable to join Session. Session has already started.").show();
                                                Intent failedIntent = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherSession.class);
                                                failedIntent.putExtra("userId", userId);
                                                Log.d("joinfail5", "I failed!");
                                                Log.d("joinfail5", status);
                                                Log.d("joinfail5", exercise);
                                                Log.d("joinfail5", sessionName);
                                                finish();
                                            }
                                        }
                                        else
                                        {
                                            TastyToasty.error(ExerciseTogetherJoiningProcessActivity.this, "Session does not exist.").show();
                                            Log.d("joinfail1", "I failed!");
                                            Intent failedIntent = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherSession.class);
                                            failedIntent.putExtra("userId", userId);
                                            finish();
                                        }
                                    }
                                    else
                                    {
                                        TastyToasty.error(ExerciseTogetherJoiningProcessActivity.this, "Session does not exist.").show();
                                        Log.d("joinfail8", "I failed!");
                                        Intent failedIntent = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherSession.class);
                                        failedIntent.putExtra("userId", userId);
                                        finish();
                                    }

                                }
                            });
                }
                else
                {
                    // If no internet, show alert and bring user back to ExerciseTogetherJoinActivity
                    internet.noConnectionAlert(ExerciseTogetherJoiningProcessActivity.this);
                    Log.d("joinfail6", "I failed!");
                    Intent failedIntent = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherJoinActivity.class);
                    failedIntent.putExtra("userId", userId);
                    startActivity(failedIntent);
                    finish();
                }
            }
            else
            {
                // If qr code is empty
                Log.d("joinfail7", "I failed!");
                Intent failedIntent = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherJoinActivity.class);
                failedIntent.putExtra("userId", userId);
                startActivity(failedIntent);
                finish();
            }
        }
        catch (Exception e)
        {
            // if reading Qr code leads to error
            TastyToasty.error(ExerciseTogetherJoiningProcessActivity.this, "QR Code cannot be used in IPPTReady").show();
            Intent failedIntent = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherJoinActivity.class);
            failedIntent.putExtra("userId", userId);
            startActivity(failedIntent);
            finish();
        }

    }

    // Create Bitmap from bitMatrix
    public Bitmap CreateImage(BitMatrix bitMatrix) {
        int height = bitMatrix.getHeight();
        int width = bitMatrix.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bitmap.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }

    // User should not leave during the joining process...
    @Override
    protected void onPause()
    {
        Intent failedIntent = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherJoinActivity.class);
        failedIntent.putExtra("userId", getIntent().getStringExtra("userId"));
        startActivity(failedIntent);
        finish();
        super.onPause();
    }

}
