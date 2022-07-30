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
    private String dateJoined = "";
    private String exercise = "";
    private String sessionName = "";
    private String status = "";
    DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together_joining_process);

        Intent qrIntent = getIntent();
        String qrCode = qrIntent.getStringExtra("qrCode");
        String userId = qrIntent.getStringExtra("userId");

        if (qrCode.length() > 0)
        {
            boolean hostUserIDObtained = false;
            String hostUserId = "";
            String dateCreated = "";
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

            Internet internet = new Internet();
            if (internet.isOnline(ExerciseTogetherJoiningProcessActivity.this))
            {
                String finalHostUserId = hostUserId;
                String finalDateCreated = dateCreated;
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

                                        if (status.equals("Created"))
                                        {
                                            if (!finalHostUserId.equals(userId)) {
                                                ExerciseTogetherSession session = new ExerciseTogetherSession(finalDateCreated, sessionName, exercise, finalHostUserId);
                                                FirebaseDocChange firebaseDocChange = ExerciseTogetherSession.createNewSession(userId, session);
                                                firebaseDocChange.changeTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            String qrdetails = session.hostUserID + "&" + session.dateCreated;
                                                            Log.d("DEBUG", qrdetails);
                                                            QRCodeWriter qrCodeWriter = new QRCodeWriter();
                                                            Bitmap bitmap = null;

                                                            try {
                                                                BitMatrix bitMatrix = qrCodeWriter.encode(qrdetails, BarcodeFormat.QR_CODE, 400, 400);
                                                                bitmap = CreateImage(bitMatrix);
                                                            }
                                                            catch (WriterException we)
                                                            {
                                                                Log.e("Error", "Unable to create QR code.");
                                                                Log.d("joinfail2", "I failed!");
                                                                TastyToasty.error(ExerciseTogetherJoiningProcessActivity.this, "Unable to generate QR code.").show();
                                                                finish();
                                                            }

                                                            Bundle bundle = new Bundle();
                                                            bundle.putString("date", session.dateCreated);
                                                            bundle.putString("sessionName", session.sessionName);
                                                            bundle.putString("exercise", session.exercise);
                                                            bundle.putString("userId", userId);
                                                            bundle.putParcelable("QRImage", bitmap);
                                                            Intent beginSession = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherWaitingRoomActivity.class);
                                                            TastyToasty.makeText(ExerciseTogetherJoiningProcessActivity.this, "Joined Session: " + session.sessionName, TastyToasty.SHORT, null, R.color.success, R.color.white, false).show();
                                                            beginSession.putExtras(bundle);
                                                            startActivity(beginSession);
                                                            finish();
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
                                                String qrdetails = userId + "&" + finalDateCreated;
                                                Log.d("DEBUG", qrdetails);
                                                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                                                Bitmap bitmap = null;

                                                try {
                                                    BitMatrix bitMatrix = qrCodeWriter.encode(qrdetails, BarcodeFormat.QR_CODE, 400, 400);
                                                    bitmap = CreateImage(bitMatrix);
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

                                                Bundle bundle = new Bundle();
                                                bundle.putString("date", finalDateCreated);
                                                bundle.putString("sessionName", sessionName);
                                                bundle.putString("exercise", exercise);
                                                bundle.putString("userId", userId);
                                                bundle.putParcelable("QRImage", bitmap);
                                                Intent beginSession = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherWaitingRoomActivity.class);
                                                TastyToasty.makeText(ExerciseTogetherJoiningProcessActivity.this, "Joined Session: " + sessionName, TastyToasty.SHORT, null, R.color.success, R.color.white, false).show();
                                                beginSession.putExtras(bundle);
                                                startActivity(beginSession);
                                                finish();
                                            }
                                        }
                                        else
                                        {
                                            TastyToasty.error(ExerciseTogetherJoiningProcessActivity.this, "Unable to join session.").show();
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
            Log.d("joinfail7", "I failed!");
            Intent failedIntent = new Intent(ExerciseTogetherJoiningProcessActivity.this, ExerciseTogetherJoinActivity.class);
            failedIntent.putExtra("userId", userId);
            startActivity(failedIntent);
            finish();
        }
    }

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
}
