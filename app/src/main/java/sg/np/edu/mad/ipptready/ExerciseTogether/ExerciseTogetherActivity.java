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

        Button createBtn = findViewById(R.id.createExTgtSession);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeintent = getIntent();
                String userId = homeintent.getStringExtra("userId");
                Intent createIntent = new Intent(ExerciseTogetherActivity.this, ExerciseTogetherCreateActivity.class);
                createIntent.putExtra("userId", userId);
                startActivity(createIntent);
            }
        });
    }
}