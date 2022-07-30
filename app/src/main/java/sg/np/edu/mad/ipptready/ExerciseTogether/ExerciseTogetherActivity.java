package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together);

        Intent homeIntent = getIntent();
        String userId = homeIntent.getStringExtra("userId");

        Button createBtn = findViewById(R.id.createExTgtSession);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(ExerciseTogetherActivity.this, ExerciseTogetherCreateActivity.class);
                createIntent.putExtra("userId", userId);
                startActivity(createIntent);
            }
        });

        Button joinBtn = findViewById(R.id.joinExTgtSession);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinIntent = new Intent(ExerciseTogetherActivity.this, ExerciseTogetherJoinActivity.class);
                joinIntent.putExtra("userId", userId);
                startActivity(joinIntent);
            }
        });

        Button returnBtn = findViewById(R.id.backtoHomefromExTgt);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}