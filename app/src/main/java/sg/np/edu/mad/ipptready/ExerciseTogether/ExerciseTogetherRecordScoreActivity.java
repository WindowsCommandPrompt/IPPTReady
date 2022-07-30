package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.R;
import sg.np.edu.mad.ipptready.SitupActivity;

public class ExerciseTogetherRecordScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together_record_score);

        com.airbnb.lottie.LottieAnimationView loading = findViewById(R.id.loadingRecordingSession);
        loading.setVisibility(View.GONE);

        EditText repetitions = findViewById(R.id.repetitionsRecordEditText);
        TextView exerciseText = findViewById(R.id.exerciseRecordExTgtTextView);
        Button recordButton = findViewById(R.id.recordResultsExTgtBtn);
        exerciseText.setText("Number of " + getIntent().getStringExtra("exercise") + " completed:");
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
            }
        });
    }
}