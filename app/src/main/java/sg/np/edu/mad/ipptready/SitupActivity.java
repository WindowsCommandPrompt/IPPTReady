package sg.np.edu.mad.ipptready;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SitupActivity extends AppCompatActivity {

    LinearLayout startButton;
    LinearLayout resetButton;
    TextView remainingSeconds;
    CountDownTimer myCountDown;
    boolean isRunning = false;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situp);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        startButton =  findViewById(R.id.startSitup);
        resetButton = findViewById(R.id.stopSitup);
        remainingSeconds = findViewById(R.id.situpSecondsRemaining);
        TextView targetSitupsTextView = findViewById(R.id.targetNumberOfSitups);
        targetSitupsTextView.setText(String.valueOf(getIntent().getExtras().getInt("Target Situps")));

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRunning) {
                    isRunning = true;
                    Toast.makeText(SitupActivity.this, "Timer has started!", Toast.LENGTH_SHORT).show();
                    countDownTimer();
                }
                else {
                    Toast.makeText(SitupActivity.this, "Timer has already started!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    isRunning = false;
                    myCountDown.cancel();
                    remainingSeconds.setText("60");
                    Toast.makeText(SitupActivity.this, "Timer has been reset", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SitupActivity.this, "Timer has not started yet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void finishSitup(){
        final VibrationEffect vibrationEffect1;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrationEffect1 = VibrationEffect.createOneShot(2500, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect1);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("1 minute is up! Have you reached your target?");
        builder.setCancelable(false);
        builder.setPositiveButton("Record Results", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SitupActivity.this, SitupTargetActivity.class);
                Bundle bundle = new Bundle();
                Bundle receivedBundle = getIntent().getExtras();
                bundle.putString("Email", receivedBundle.getString("Email"));
                bundle.putString("IPPTCycleId", receivedBundle.getString("IPPTCycleId"));
                bundle.putString("IPPTRoutineId", receivedBundle.getString("IPPTRoutineId"));
                bundle.putBoolean("SitupTargetSet", true);
                bundle.putInt("Target Situps", receivedBundle.getInt("Target Situps"));
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Return to Sit-Up Target", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.setTitle("Times Up!");
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