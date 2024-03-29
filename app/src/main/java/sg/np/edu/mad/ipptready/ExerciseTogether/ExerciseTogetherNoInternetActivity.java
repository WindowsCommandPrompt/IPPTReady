package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.HomeActivity;
import sg.np.edu.mad.ipptready.InternetConnectivity.Internet;
import sg.np.edu.mad.ipptready.LoginActivity;
import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherNoInternetActivity extends AppCompatActivity {
    // Exercise Together feature done by: BRYAN KOH

    // Global variables
    Internet internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together_no_internet);

        // Internet object
        internet = new Internet();

        // User can return to login page
        Button returnBtn = findViewById(R.id.returntologinExTgt);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginscreenIntent = new Intent(ExerciseTogetherNoInternetActivity.this, LoginActivity.class);
                loginscreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Toast.makeText(ExerciseTogetherNoInternetActivity.this, "Returning to Login", Toast.LENGTH_SHORT).show();
                startActivity(loginscreenIntent);
            }
        });

        // If user clicks on rejoin button, checks internet
        Button rejoinBtn = findViewById(R.id.rejoin);
        rejoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (internet.isOnline(ExerciseTogetherNoInternetActivity.this))
                {
                    ExerciseTogetherSession.getSessionsbyUserID(getIntent().getStringExtra("userId"))
                            .document(getIntent().getStringExtra("date"))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        String status = (String) document.getData().get("status");

                                        // If user comes from waiting room, bring them back to waiting room
                                        if (!status.equals("Started") && !status.equals("Completed"))
                                        {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("date", getIntent().getStringExtra("date"));
                                            bundle.putString("sessionName", getIntent().getStringExtra("sessionName"));
                                            bundle.putString("exercise", getIntent().getStringExtra("exercise"));
                                            bundle.putString("userId", getIntent().getStringExtra("userId"));
                                            bundle.putParcelable("QRImage", getIntent().getExtras().getParcelable("QRImage"));
                                            bundle.putString("QRString", getIntent().getStringExtra("QRString"));
                                            bundle.putString("hostUserId", getIntent().getStringExtra("hostUserId"));

                                            Intent beginSession = new Intent(ExerciseTogetherNoInternetActivity.this, ExerciseTogetherWaitingRoomActivity.class);
                                            beginSession.putExtras(bundle);
                                            startActivity(beginSession);
                                            finish();
                                        }
                                        else if (status.equals("Completed")) // If user comes from results page, bring user back to results page
                                        {
                                            Intent noConnectionIntent = new Intent(ExerciseTogetherNoInternetActivity.this, ExerciseTogetherResultsActivity.class);
                                            Bundle exerciseBundle = new Bundle();
                                            exerciseBundle.putString("date", getIntent().getStringExtra("date"));
                                            exerciseBundle.putString("sessionName", getIntent().getStringExtra("sessionName"));
                                            exerciseBundle.putString("exercise", getIntent().getStringExtra("exercise"));
                                            exerciseBundle.putString("userId", getIntent().getStringExtra("userId"));
                                            exerciseBundle.putParcelable("QRImage", getIntent().getExtras().getParcelable("QRImage"));
                                            exerciseBundle.putString("QRString", getIntent().getStringExtra("QRString"));
                                            exerciseBundle.putString("ExerciseTogetherSession", "yes");
                                            noConnectionIntent.putExtras(exerciseBundle);
                                            startActivity(noConnectionIntent);
                                            finish();
                                        }
                                    }
                                }
                            });
                }
                else
                {
                    // If no internet, show no connection alert dialog
                    internet.noConnectionAlert(ExerciseTogetherNoInternetActivity.this);
                }
            }
        });
    }
}