package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import sg.np.edu.mad.ipptready.HomeActivity;
import sg.np.edu.mad.ipptready.LoginActivity;
import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherNoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together_no_internet);

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
    }
}