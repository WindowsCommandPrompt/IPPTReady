package sg.np.edu.mad.ipptready;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;

public class AutoPushupActivity extends AppCompatActivity {
    // Creating variables for text view,
    // sensor manager and our sensor.
    SensorManager sensorManager;
    Sensor proximitySensor;
    Vibrator vibrator;
    private int numberOfPushUps = 0;
    int targetPushUps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_pushup);


        // calling sensor service.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // from sensor service we are
        // calling proximity sensor
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // handling the case if the proximity
        // sensor is not present in users device.
        if (proximitySensor == null) {
            Toast.makeText(this, "No proximity sensor found in device.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AutoPushupActivity.this, PushupActivity.class);
            startActivity(intent);
            finish();
        } else {
            // registering our sensor with sensor manager.
            sensorManager.registerListener(proximitySensorEventListener,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        AlertDialog.Builder timeIsUp = new AlertDialog.Builder(this);
        //AlertDialog.Builder confirmationNotSaveData = new AlertDialog.Builder(this);

        Intent intent = getIntent();
        String cycleID = intent.getStringExtra("IPPTCycleId");
        String routineID = intent.getStringExtra("IPPTRoutineId");
        String email = intent.getStringExtra("Email");
        int NumPushups = intent.getIntExtra("NumPushups", 0);

        ((TextView) findViewById(R.id.targetNumberOfPushUps)).setText(String.valueOf(NumPushups));

        timeIsUp
                .setTitle("Times up!")
                .setPositiveButton(
                        "OK",
                        (DialogInterface di, int i) -> {
                            // change layouts in order to key in completed pushups
                            ((LinearLayout) findViewById(R.id.pushUpRecordTimingInterface)).setVisibility(View.GONE);
                            ((LinearLayout) findViewById(R.id.pushUpActivityEnterRecords)).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.numberOfPushUpsThatTheUserDid)).setText(String.valueOf(numberOfPushUps));

                            // When submitting completed pushups
                            ((Button) findViewById(R.id.setPushUpActivity)).setOnClickListener(function -> {
                                Integer numPushUpsDone = numberOfPushUps;
                                // Push into the firebase...
                                addPushupToDatabase(numPushUpsDone, NumPushups, email, cycleID, routineID, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // finish activity
                                        Toast.makeText(AutoPushupActivity.this, "Directing to workout page", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            });
                        }
                )
                .setCancelable(false);

        // Countdown timer
        long timeAvailable = Long.parseLong(((TextView) findViewById(R.id.timing_indicator_text)).getText().toString()) * 1000;
        CountDownTimer mainCountdownTimer = new CountDownTimer(timeAvailable, 10){
            @Override
            public void onTick(long millisLeft) {
                ((TextView) findViewById(R.id.timing_indicator_text)).setText(Long.toString(millisLeft / 1000));
            }
            @Override
            public void onFinish() {
                final VibrationEffect vibrationEffect1;
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrationEffect1 = VibrationEffect.createOneShot(2500, VibrationEffect.DEFAULT_AMPLITUDE);
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }

                timeIsUp.create().show();
            }
        };


        // Once the timer has been activated by the user....
        ((LinearLayout) findViewById(R.id.startTimer)).setOnClickListener(function -> {
            ((LinearLayout) findViewById(R.id.startTimer)).setEnabled(false);
            Toast.makeText(this, "The timer has begun!", Toast.LENGTH_SHORT).show();
            mainCountdownTimer.start();
        });

        ((LinearLayout) findViewById(R.id.resetTimer)).setOnClickListener(function -> {
            if (!((LinearLayout) findViewById(R.id.startTimer)).isEnabled()){
                Toast.makeText(this, "The stopwatch has been reset", Toast.LENGTH_SHORT).show();
                mainCountdownTimer.cancel();
                ((TextView) findViewById(R.id.timing_indicator_text)).setText("60");
                ((TextView) findViewById(R.id.numberOfPushUpsCount)).setText("0");
                numberOfPushUps = 0;
                ((LinearLayout) findViewById(R.id.startTimer)).setEnabled(true);
            }
            else {
                Toast.makeText(this, "Please activate the timer before you can perform the other actions", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog.Builder switchToManual = new AlertDialog.Builder(this);

        switchToManual
                .setTitle("Are you sure you want to switch to manual mode?")
                .setPositiveButton("Yes", new DialogInterface
                        .OnClickListener() {
                    @Override
                public void onClick(DialogInterface dialog,
                                    int which)
                {

                    // When the user click yes button
                    // then app will move to Pushup Activity
                    Intent intent = new Intent(AutoPushupActivity.this, PushupActivity.class);
                    intent.putExtra("NumPushups", NumPushups);
                    intent.putExtra("Email", getIntent().getStringExtra("Email"));
                    intent.putExtra("IPPTCycleId", getIntent().getStringExtra("IPPTCycleId"));
                    intent.putExtra("IPPTRoutineId", getIntent().getStringExtra("IPPTRoutineId"));
                    startActivity(intent);
                    finish();
                }
                })
                .setNegativeButton(
                "No",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {

                        // If user click no
                        // then dialog box is canceled.
                        dialog.cancel();
                    }
                })
                .setCancelable(false);

        ((LinearLayout) findViewById(R.id.manualTracker)).setOnClickListener(function -> {
            switchToManual.show();
        });
    }



    @Override
    public void onBackPressed()  {
        AlertDialog.Builder confirmQuit = new AlertDialog.Builder(this);
        confirmQuit
                .setTitle("Confirm end push-ups?")
                .setMessage("Are you sure you want to terminate the current push-ups? Do note that your progress will not be saved.")
                .setPositiveButton(
                        "Yes",
                        (DialogInterface di, int i) -> {
                            finish();
                        }
                )
                .setNegativeButton(
                        "No",
                        (DialogInterface di, int i) -> {
                            di.dismiss();
                        }
                )
                .setCancelable(false);
        confirmQuit.create().show();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            /* Current config for the alignment of the elements when the phone's orientation is portrait...
             * android:textSize = "29px"  -> For textView identifier
             * android:textSize = "102px" -> For @+id/targetNumberOfPushups
             */
            ((TextView) findViewById(R.id.textViewIdentifier)).setTextSize(19F);
            ((TextView) findViewById(R.id.targetNumberOfPushUps)).setTextSize(52F);
            ((TextView) findViewById(R.id.numberOfPushUpsCountIdentifier)).setTextSize(19F);
            ((TextView) findViewById(R.id.numberOfPushUpsCount)).setTextSize(52F);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            ((TextView) findViewById(R.id.textViewIdentifier)).setTextSize(39F);
            ((TextView) findViewById(R.id.targetNumberOfPushUps)).setTextSize(92F);
            ((TextView) findViewById(R.id.numberOfPushUpsCountIdentifier)).setTextSize(39F);
            ((TextView) findViewById(R.id.numberOfPushUpsCount)).setTextSize(92F);
        }
    }

    // Firestore code
    public void addPushupToDatabase(int pushUps, int target, String EmailAddress, String IPPTCycleID, String IPPTRoutineID, OnCompleteListener<Void> onCompleteVoidListener){
        FirebaseFirestore RESTdb = FirebaseFirestore.getInstance();
        HashMap<String, Object> numOfPushupsDone = new HashMap<String, Object>();
        numOfPushupsDone.put("RepsTarget", target);
        numOfPushupsDone.put("NumsReps", pushUps);

        RESTdb.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .document(IPPTCycleID)
                .collection("IPPTRoutine")
                .document(IPPTRoutineID)
                .collection("IPPTRecord")
                .document("PushupRecord")
                .set(numOfPushupsDone)
                .addOnSuccessListener(function -> {
                    Toast.makeText(this, "Push ups recorded!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(function -> {
                    Toast.makeText(this, "Unexpected error occured", Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(onCompleteVoidListener);
    }
    // calling the sensor event class to detect
    // the change in data when sensor starts working.
    SensorEventListener proximitySensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // method to check accuracy changed in sensor.
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // check if the sensor type is proximity sensor.
            if ((event.sensor.getType() == Sensor.TYPE_PROXIMITY) && !((LinearLayout) findViewById(R.id.startTimer)).isEnabled()){
                for (int i = 0; i < 1; i++) {
                    if (event.values[0] <= 3.0f) {

                        if (proximitySensor != null) {
                            numberOfPushUps++;
                            ((TextView) findViewById(R.id.numberOfPushUpsCount)).setText(String.valueOf(numberOfPushUps));
                        }
                    }
                }
            }
        };
};}
