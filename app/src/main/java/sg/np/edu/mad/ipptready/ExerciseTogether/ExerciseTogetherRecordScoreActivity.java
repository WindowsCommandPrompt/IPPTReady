package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.uk.tastytoasty.TastyToasty;

import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.R;
import sg.np.edu.mad.ipptready.SitupActivity;

public class ExerciseTogetherRecordScoreActivity extends AppCompatActivity {
    // Exercise Together feature done by: BRYAN KOH

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together_record_score);

        // Hide loading lottie animation first
        com.airbnb.lottie.LottieAnimationView loading = findViewById(R.id.loadingRecordingSession);
        loading.setVisibility(View.GONE);

        EditText repetitions = findViewById(R.id.repetitionsRecordEditText);
        TextView exerciseText = findViewById(R.id.exerciseRecordExTgtTextView);
        Button recordButton = findViewById(R.id.recordResultsExTgtBtn);
        exerciseText.setText("Number of " + getIntent().getStringExtra("exercise") + " completed:");
        // When record button is clicked, check if there is a value entered. If
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!repetitions.getText().toString().equals(""))
                {
                    loading.setVisibility(View.VISIBLE);
                    recordButton.setVisibility(View.GONE);
                    int repetitionNo = Integer.parseInt(repetitions.getText().toString());
                    String userId = getIntent().getStringExtra("userId");
                    ExerciseTogetherSession
                            .recordScore(userId, getIntent().getStringExtra("QRString"), repetitionNo)
                            .changeTask
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                ExerciseTogetherSession
                                        .updateUserSessionComplete(userId, getIntent().getStringExtra("date"), repetitionNo)
                                        .changeTask
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Intent exerciseFinish = new Intent(ExerciseTogetherRecordScoreActivity.this, ExerciseTogetherResultsActivity.class);

                                                    Bundle exerciseBundle = new Bundle();
                                                    exerciseBundle.putString("date", getIntent().getStringExtra("date"));
                                                    exerciseBundle.putString("sessionName", getIntent().getStringExtra("sessionName"));
                                                    exerciseBundle.putString("exercise", getIntent().getStringExtra("exercise"));
                                                    exerciseBundle.putString("userId", getIntent().getStringExtra("userId"));
                                                    exerciseBundle.putParcelable("QRImage", getIntent().getExtras().getParcelable("QRImage"));
                                                    exerciseBundle.putString("QRString", getIntent().getStringExtra("QRString"));
                                                    exerciseBundle.putString("ExerciseTogetherSession", "yes");

                                                    exerciseFinish.putExtras(exerciseBundle);
                                                    startActivity(exerciseFinish);
                                                    finish();
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
                else
                {
                    TastyToasty.blue(ExerciseTogetherRecordScoreActivity.this, "Please enter your repetitions", null).show();
                    return;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        leaveSession();
    }

    // If user presses the back button, user will be prompted to leave session. If user leaves session, updates the user's status to "Left" on Firestore Exercise Together collection's record of the session
    public void leaveSession()
    {
        Context ctx = ExerciseTogetherRecordScoreActivity.this;
        AlertDialog.Builder leaveAlert = new AlertDialog.Builder(ctx);
        leaveAlert
                .setTitle("Leave Session")
                .setMessage("Are you sure you want to leave this session?")
                .setCancelable(true)
                .setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDocChange firebaseDocChangeJoinSessionStatus = ExerciseTogetherSession.updateJoinStatus(getIntent().getStringExtra("userId"), getIntent().getStringExtra("QRString"), "Left");
                                firebaseDocChangeJoinSessionStatus.changeTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            TastyToasty.blue(ctx, "You have left the session", null).show();
                                            Intent failedIntent = new Intent(ctx, ExerciseTogetherSession.class);
                                            failedIntent.putExtra("userId", getIntent().getStringExtra("userId"));
                                            finish();
                                        }
                                    }
                                });
                            }
                        })
                .setNegativeButton("No", null);
        leaveAlert.create().show();
    }
}