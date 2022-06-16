package sg.np.edu.mad.ipptready;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SitupActivity extends AppCompatActivity {

    LinearLayout startButton = findViewById(R.id.startSitup);
    LinearLayout resetButton = findViewById(R.id.stopSitup);
    TextView remainingSeconds = findViewById(R.id.situpSecondsRemaining);
    CountDownTimer myCountDown;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situp);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
                Toast.makeText(SitupActivity.this, "Timer has started!", Toast.LENGTH_SHORT).show();
                countDownTimer();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    myCountDown.cancel();
                    remainingSeconds.setText("60");
                }
                else {
                    Toast.makeText(SitupActivity.this, "Timer has not started yet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void finishSitup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("1 minute is up!");
        builder.setCancelable(false);
        builder.setPositiveButton("Record Results", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Return to IPPT Cycles page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.setTitle("OTP Expired!");
        alert.show();
    }

    private void countDownTimer(){

        myCountDown = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingSeconds.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                finishSitup();
            }
        };
        myCountDown.start();
    }
}