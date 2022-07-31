package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.uk.tastytoasty.TastyToasty;

import sg.np.edu.mad.ipptready.ExerciseTogether.ExerciseTogetherRecordScoreActivity;
import sg.np.edu.mad.ipptready.ExerciseTogether.ExerciseTogetherResultsActivity;
import sg.np.edu.mad.ipptready.ExerciseTogether.ExerciseTogetherWaitingRoomActivity;
import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;

public class SitupActivity extends AppCompatActivity {

    // Global variables
    LinearLayout startButton;
    LinearLayout resetButton;
    TextView remainingSeconds;
    CountDownTimer myCountDown;
    boolean isRunning = false;
    Vibrator vibrator;
    boolean ExerciseTogether = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situp);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Set up the global variables
        startButton =  findViewById(R.id.startSitup);
        resetButton = findViewById(R.id.stopSitup);
        remainingSeconds = findViewById(R.id.situpSecondsRemaining);
        TextView targetSitupsTextView = findViewById(R.id.targetNumberOfSitups);
        try {
            String exerciseTogether = getIntent().getStringExtra("ExerciseTogetherSession");
            if (!exerciseTogether.equals(null))
            {
                ExerciseTogether = true;
                targetSitupsTextView.setVisibility(View.GONE);
                findViewById(R.id.textViewsituptarget).setVisibility(View.GONE);
            }
        }
        catch (Exception e) {
            targetSitupsTextView.setText(String.valueOf(getIntent().getExtras().getInt("Target Situps")));
        }

        // If the start button is pressed
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If timer has not started yet
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

        // If the reset button is pressed
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if timer is running
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

    // Method called when timer completes
    private void finishSitup(){
        // Vibrate the phone for 2.5 seconds when timer completes
        final VibrationEffect vibrationEffect1;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrationEffect1 = VibrationEffect.createOneShot(2500, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect1);
        }

        // Set up alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if (!ExerciseTogether)
        {
            builder.setMessage("1 minute is up! Have you reached your target?");
            // Prepare intent to key in situp score
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
                    finish();
                }
            });
            // Return to sit-up target
            builder.setNegativeButton("Return to Sit-Up Target", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
        }
        else
        {
            builder.setMessage("1 minute is up! It's time to record your score!");
            builder.setPositiveButton("Record Results", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent exerciseFinish = new Intent(SitupActivity.this, ExerciseTogetherRecordScoreActivity.class);

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
            });
        }

        AlertDialog alert = builder.create();
        alert.setTitle("Times Up!");
        alert.show();
    }

    // Countdown Timer method
    private void countDownTimer(){

        myCountDown = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // change text every second
                remainingSeconds.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                finishSitup();
            }
        };
        myCountDown.start();
    }

    @Override
    public void onBackPressed() {
        if (ExerciseTogether)
        {
            leaveSession();
        }
    }

    public void leaveSession()
    {
        AlertDialog.Builder leaveAlert = new AlertDialog.Builder(SitupActivity.this);
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
                                            TastyToasty.blue(SitupActivity.this, "You have left the session", null).show();
                                            Intent failedIntent = new Intent(SitupActivity.this, ExerciseTogetherSession.class);
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