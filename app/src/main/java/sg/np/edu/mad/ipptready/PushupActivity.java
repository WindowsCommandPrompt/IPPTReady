package sg.np.edu.mad.ipptready;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uk.tastytoasty.TastyToasty;

import java.util.HashMap;

import sg.np.edu.mad.ipptready.ExerciseTogether.ExerciseTogetherRecordScoreActivity;
import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;

public class PushupActivity extends AppCompatActivity implements SensorEventListener {

    Vibrator vibrator;
    boolean ExerciseTogether = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushup);

        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);

        AlertDialog.Builder timeIsUp = new AlertDialog.Builder(this);
        //AlertDialog.Builder confirmationNotSaveData = new AlertDialog.Builder(this);

        Intent intent = getIntent();
        String cycleID = intent.getStringExtra("IPPTCycleId");
        String routineID = intent.getStringExtra("IPPTRoutineId");
        String email = intent.getStringExtra("Email");
        int NumPushups = intent.getIntExtra("NumPushups", 0);

        TextView targetNumberOfPushUpsTextView = findViewById(R.id.targetNumberOfPushUps);
        try {
            String exerciseTogether = getIntent().getStringExtra("ExerciseTogetherSession");
            if (!exerciseTogether.equals(null))
            {
                ExerciseTogether = true;
                targetNumberOfPushUpsTextView.setVisibility(View.GONE);
                findViewById(R.id.textViewIdentifier).setVisibility(View.GONE);
            }
        }
        catch (Exception e) {
            targetNumberOfPushUpsTextView.setText(String.valueOf(NumPushups));
        }

        timeIsUp
            .setTitle("Times up!")
            .setMessage("It's time to key in the number of push ups that you have done for the past 1 minute")
            .setPositiveButton(
                "OK",
                (DialogInterface di, int i) -> {
                    // change layouts in order to key in completed pushups
                    ((LinearLayout) findViewById(R.id.pushUpRecordTimingInterface)).setVisibility(View.GONE);
                    ((LinearLayout) findViewById(R.id.pushUpActivityEnterRecords)).setVisibility(View.VISIBLE);

                    // When submitting completed pushups
                    ((Button) findViewById(R.id.setPushUpActivity)).setOnClickListener(function -> {
                        EditText numberOfPushUpsThatTheUserDid = findViewById(R.id.numberOfPushUpsThatTheUserDid);
                        Integer numPushUpsDone = Integer.parseInt((numberOfPushUpsThatTheUserDid).getText().toString());
                        // Check if numPushUpsDone is a valid number
                        if (numPushUpsDone >= 0 || numPushUpsDone != null){
                            // Push into the firebase...
                            addPushupToDatabase(numPushUpsDone, NumPushups, email, cycleID, routineID, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // finish activity
                                    Toast.makeText(PushupActivity.this, "Directing to workout page", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                        else {
                            Toast.makeText(this, "Uh Oh! Please try entering another number that is greater than zero!", Toast.LENGTH_SHORT).show();
                        }
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

                if (!ExerciseTogether) timeIsUp.create().show();
                else
                {
                    AlertDialog.Builder alertExTgtBuilder = new AlertDialog.Builder(PushupActivity.this);
                    alertExTgtBuilder.setCancelable(false);
                    alertExTgtBuilder.setMessage("1 minute is up! It's time to record your score!");
                    alertExTgtBuilder.setPositiveButton("Record Results", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent exerciseFinish = new Intent(PushupActivity.this, ExerciseTogetherRecordScoreActivity.class);

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
                    AlertDialog alert = alertExTgtBuilder.create();
                    alert.setTitle("Times Up!");
                    alert.show();
                }
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
                ((LinearLayout) findViewById(R.id.startTimer)).setEnabled(true);
            }
            else {
                Toast.makeText(this, "Please activate the timer before you can perform the other actions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed()  {
        if (!ExerciseTogether)
        {
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
        else
        {
            leaveSession();
        }

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
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            ((TextView) findViewById(R.id.textViewIdentifier)).setTextSize(39F);
            ((TextView) findViewById(R.id.targetNumberOfPushUps)).setTextSize(92F);
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void leaveSession()
    {
        AlertDialog.Builder leaveAlert = new AlertDialog.Builder(PushupActivity.this);
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
                                            TastyToasty.blue(PushupActivity.this, "You have left the session", null).show();
                                            Intent failedIntent = new Intent(PushupActivity.this, ExerciseTogetherSession.class);
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
