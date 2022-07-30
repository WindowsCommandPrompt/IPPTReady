package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.uk.tastytoasty.TastyToasty;

import sg.np.edu.mad.ipptready.CreateAccountActivity;
import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;
import sg.np.edu.mad.ipptready.R;


public class ExerciseTogetherCreateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String EmailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together_create);

        EditText sessionName = findViewById(R.id.sessionNameEditText);

        Spinner exercisesSpinner = (Spinner) findViewById(R.id.exerciseSpinner);
        ArrayAdapter<CharSequence> exercisesArrayAdapter = ArrayAdapter.createFromResource(this, R.array.exercises_array, android.R.layout.simple_spinner_item);
        exercisesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exercisesSpinner.setAdapter(exercisesArrayAdapter);

        Button createBtn = findViewById(R.id.createSessionExTgt);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((sessionName.getText().toString()).equalsIgnoreCase(""))
                {
                    sessionName.setHint("Please enter a session name");
                    sessionName.setError("Please enter a session name!");
                    return;
                }
                String sessionNameText = sessionName.getText().toString();
                String selectedExercise = String.valueOf(exercisesSpinner.getSelectedItem());

                // Get Email Address/UserID
                Intent intent = getIntent();
                EmailAddress = intent.getStringExtra("userId");
                Log.d("DEBUG", EmailAddress);

                // Get user's Name to be used in encoding for QR code
                final String[] name = {""};
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("IPPTUser").document(EmailAddress).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) name[0] = task.getResult().get("Name").toString();
                    }
                });

                // Create Exercise Together Session object and add session to firestore.
                ExerciseTogetherSession session = new ExerciseTogetherSession("", sessionNameText, selectedExercise, EmailAddress);
                FirebaseDocChange firebaseDocChange = ExerciseTogetherSession.createNewSession(EmailAddress, session);
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
                                FirebaseDocChange firebaseDocChangeJoinSession = ExerciseTogetherSession.joinSession(EmailAddress, qrdetails);
                                Bitmap finalBitmap = bitmap;
                                firebaseDocChangeJoinSession.changeTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("date", session.dateCreated);
                                            bundle.putString("sessionName", session.sessionName);
                                            bundle.putString("exercise", session.exercise);
                                            bundle.putString("userId", EmailAddress);
                                            bundle.putParcelable("QRImage", finalBitmap);
                                            bundle.putString("QRString", qrdetails);
                                            Intent beginSession = new Intent(ExerciseTogetherCreateActivity.this, ExerciseTogetherWaitingRoomActivity.class);
                                            TastyToasty.makeText(ExerciseTogetherCreateActivity.this, "Session created!", TastyToasty.SHORT, null, R.color.success, R.color.white, false).show();
                                            beginSession.putExtras(bundle);
                                            startActivity(beginSession);
                                            finish();
                                        }
                                        else {
                                            TastyToasty.error(ExerciseTogetherCreateActivity.this, "Unexpected error occurred").show();
                                            finish();
                                        }
                                    }
                                });
                            }
                            catch (WriterException we)
                            {
                                Log.e("Error", "Unable to create QR code.");
                                TastyToasty.error(ExerciseTogetherCreateActivity.this, "Unable to generate QR code. Please try again.").show();
                                return;
                            }
                        }
                        else {
                            TastyToasty.error(ExerciseTogetherCreateActivity.this, "Unexpected error occurred").show();
                            finish();
                        }
                    }
                });
            }
        });

        Button returnBtn = findViewById(R.id.cancelCreateExTgt);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String item = parent.getItemAtPosition(pos).toString();
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView<?> parent) { }

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