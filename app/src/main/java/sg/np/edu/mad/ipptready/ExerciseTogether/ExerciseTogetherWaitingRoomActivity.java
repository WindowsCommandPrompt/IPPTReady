package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import sg.np.edu.mad.ipptready.InternetConnectivity.Internet;
import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherWaitingRoomActivity extends AppCompatActivity {
    Intent noInternetIntent = new Intent();
    Internet internet;
    CountDownTimer myCountDown;
    int secondsLeft;
    TextView secondsLeftTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercisetogetherwaitingroom);

        secondsLeft = 11;
        internet = new Internet();
        secondsLeftTextView = findViewById(R.id.refreshWaitingRoomTextView);
        countDownTimer();
    }

    private void countDownTimer(){

        myCountDown = new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsLeft--;
                secondsLeftTextView.setText("Refreshing in: " + String.valueOf(secondsLeft) + "s");
            }

            @Override
            public void onFinish() {
                if (internet.isOnline(ExerciseTogetherWaitingRoomActivity.this)) recreate();
                else
                {
                    Intent noConnectionIntent = new Intent(ExerciseTogetherWaitingRoomActivity.this, ExerciseTogetherNoInternetActivity.class);
                    startActivity(noConnectionIntent);
                    finish();
                }
            }
        };
        myCountDown.start();
    }
}