package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
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

        Intent receivedIntent = getIntent();
        TextView sessionNameTextView = findViewById(R.id.SessionNameWaitingRoom);
        sessionNameTextView.setText("\"" + receivedIntent.getStringExtra("sessionName") + "\"");
        ImageView QRCode = findViewById(R.id.QRcode);
        try {
            QRCode.setImageBitmap(receivedIntent.getExtras().getParcelable("QRImage"));
        }
        catch (Exception e) {
            QRCode.setVisibility(View.GONE);
        }

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
                    Bundle noConnectBundle = new Bundle();
                    noConnectBundle.putString("date", getIntent().getStringExtra("date"));
                    noConnectBundle.putString("sessionName", getIntent().getStringExtra("sessionName"));
                    noConnectBundle.putString("exercise", getIntent().getStringExtra("exercise"));
                    noConnectBundle.putString("userId", getIntent().getStringExtra("userId"));
                    noConnectBundle.putParcelable("QRImage", getIntent().getExtras().getParcelable("QRImage"));
                    noConnectionIntent.putExtras(noConnectBundle);
                    startActivity(noConnectionIntent);
                    finish();
                }
            }
        };
        myCountDown.start();
    }
}