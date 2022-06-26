package sg.np.edu.mad.ipptready;

import androidx.annotation.*;
import androidx.appcompat.app.*;

import android.app.Activity;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.util.Log;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RunActivity extends AppCompatActivity {

    CountDownTimer mainStopwatch;

    AtomicReference<CountDownTimer> arcdt = new AtomicReference<>(null);

    private FirebaseFirestore RESTdb = FirebaseFirestore.getInstance();

    // When user decides to go back to previous screen
    @Override
    public void onBackPressed()  {
        AlertDialog.Builder confirmQuit = new AlertDialog.Builder(this);
        confirmQuit
                .setTitle("Confirm end cycle?")
                .setMessage("Are you sure you want to terminate the current run routine? Do note that your progress will not be saved.")
                .setPositiveButton(
                        "YES",
                        (DialogInterface di, int i) -> {
                            super.onBackPressed(); //Quits the current activity
                        }
                )
                .setNegativeButton(
                        "NO",
                        (DialogInterface di, int i) -> {
                            di.dismiss();
                        }
                )
                .setCancelable(false);
        confirmQuit.create().show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        AlertDialog.Builder confirmTerminateCycle = new AlertDialog.Builder(this);
        AlertDialog.Builder saveCycleData = new AlertDialog.Builder(this);

        // Get bundle
        Bundle bundle = getIntent().getExtras();
        String Email = bundle.getString("Email");
        String IPPTCycleID = bundle.getString("IPPTCycleId");
        String IPPTRoutineId = bundle.getString("IPPTRoutineId");

        // Build timer
        mainStopwatch = new CountDownTimer(1000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = Integer.parseInt(((TextView) findViewById(R.id.timing_indicator_text)).getText().toString().split(":")[0]);
                int seconds = Integer.parseInt(((TextView) findViewById(R.id.timing_indicator_text)).getText().toString().split(":")[1]);
                Log.d("REPEATING", Long.toString(millisUntilFinished));
                seconds++;
                String returnString = "";
                if (seconds == 60) {
                    minutes++;
                    seconds = 0;
                    if (minutes < 10) {
                        if (seconds < 10) {
                            returnString = "0" + minutes + ":0" + seconds;
                        } else {
                            returnString = "0" + minutes + ":" + seconds;
                        }
                    } else {
                        if (seconds < 10) {
                            returnString = minutes + ":0" + seconds;
                        } else {
                            returnString = minutes + ":" + seconds;
                        }
                    }
                } else {
                    if (minutes < 10) {
                        if (seconds < 10) {
                            returnString = "0" + minutes + ":0" + seconds;
                        } else {
                            returnString = "0" + minutes + ":" + seconds;
                        }
                    } else {
                        if (seconds < 10) {
                            returnString = minutes + ":0" + seconds;
                        } else {
                            returnString = minutes + ":" + seconds;
                        }
                    }
                }
                ((TextView) findViewById(R.id.timing_indicator_text)).setText(returnString);
            }
            @Override
            public void onFinish() {
                this.start();
            }
        };

        // Alert dialog to save data after stopping the timer
        saveCycleData
            .setTitle("Save data?")
            .setMessage("Are you sure you would like to save your data?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes",
                (DialogInterface di, int i) -> {
                    // Get timing
                    String capturedTiming = ((TextView) findViewById(R.id.timing_indicator_text)).getText().toString();
                    Log.d("ManagedToGetTiming", "Yes I have managed to get the timing off the textview");
                    int totalSeconds = Integer.parseInt(capturedTiming.split(":")[0]) * 60 + Integer.parseInt(capturedTiming.split(":")[1]);
                    addRunToDatabase(totalSeconds, Email, IPPTCycleID, IPPTRoutineId, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // finish activity
                            Toast.makeText(RunActivity.this, "Directing to workout page", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            )
            .setNegativeButton(
                "No",
                (DialogInterface di, int i) -> {
                    Intent recordBackIntent = new Intent();

                    setResult(Activity.RESULT_OK, recordBackIntent);
                    finish();
                }
            );

        // Alert dialog to terminate the timer
        confirmTerminateCycle
            .setTitle("Terminate run?")
            .setMessage("Are you sure you want to terminate this run?")
            .setPositiveButton(
                "Yes",
                (DialogInterface di, int i) -> {
                    //If the user presses yes THEN STOP THE TIMER
                    saveCycleData.create().show();
                }
            )
            .setNegativeButton(
                "No",
                (DialogInterface di, int i) -> {
                    // If user rejects terminating cycle
                    arcdt.set(new CountDownTimer(1000, 1000){
                        @Override
                        public void onTick(long millisUntilFinished) {
                            int minutes = Integer.parseInt(((TextView) findViewById(R.id.timing_indicator_text)).getText().toString().split(":")[0]);
                            int seconds = Integer.parseInt(((TextView) findViewById(R.id.timing_indicator_text)).getText().toString().split(":")[1]);
                            Log.d("REPEATING", Long.toString(millisUntilFinished));
                            seconds++;
                            String returnString = "";
                            if (seconds == 60) {
                                minutes++;
                                seconds = 0;
                                if (minutes < 10) {
                                    if (seconds < 10) {
                                        returnString = "0" + minutes + ":0" + seconds;
                                    } else {
                                        returnString = "0" + minutes + ":" + seconds;
                                    }
                                } else {
                                    if (seconds < 10) {
                                        returnString = minutes + ":0" + seconds;
                                    } else {
                                        returnString = minutes + ":" + seconds;
                                    }
                                }
                            } else {
                                if (minutes < 10) {
                                    if (seconds < 10) {
                                        returnString = "0" + minutes + ":0" + seconds;
                                    } else {
                                        returnString = "0" + minutes + ":" + seconds;
                                    }
                                } else {
                                    if (seconds < 10) {
                                        returnString = minutes + ":0" + seconds;
                                    } else {
                                        returnString = minutes + ":" + seconds;
                                    }
                                }
                            }
                            ((TextView) findViewById(R.id.timing_indicator_text)).setText(returnString);
                        }
                        @Override
                        public void onFinish() {
                            ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
                                Toast.makeText(RunActivity.this, "The timer has been paused", Toast.LENGTH_SHORT);
                                this.cancel();
                                confirmTerminateCycle.create().show();
                            });
                            this.start();
                        }
                    });
                    arcdt.get().start();
                }
            )
            .setCancelable(false);

        // Start timer
        ((LinearLayout) findViewById(R.id.startCycle)).setOnClickListener(andThenFunctionAs -> {
            mainStopwatch.start();
            ((LinearLayout) findViewById(R.id.startCycle)).setClickable(false);
            ((LinearLayout) findViewById(R.id.startCycle)).setEnabled(false);
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "Stopwatch has been activated", Toast.LENGTH_SHORT).show();
            // set onclicklistener for terminating run
            ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
                mainStopwatch.cancel();
                confirmTerminateCycle.create().show();
                Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "The timer has been paused", Toast.LENGTH_SHORT).show();
            });
        });

        // onclicklistener for stop timer
        ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "Timer has not started yet!", Toast.LENGTH_SHORT).show();
        });
    }

    // Firestore code
    public void addRunToDatabase(int totalSeconds, String EmailAddress, String IPPTCycleId, String IPPTRoutineId, OnCompleteListener<Void> onCompleteVoidListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> Run = new HashMap<>();
        Run.put("TimeTakenFinished", totalSeconds);

        db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .document(IPPTCycleId)
                .collection("IPPTRoutine")
                .document(IPPTRoutineId)
                .collection("IPPTRecord")
                .document("RunRecord")
                .set(Run)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RunActivity.this, "Run timing recorded!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RunActivity.this, "Error recording run timing", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(onCompleteVoidListener);
    }


    @Override
    public void onConfigurationChanged(Configuration config){
        super.onConfigurationChanged(config);
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "landscape", Toast.LENGTH_SHORT).show();
            //pause the timer for a moment
            if (((LinearLayout) findViewById(R.id.startCycle)).isPressed())
            {
                if (arcdt.get() != null) {
                    mainStopwatch.cancel();
                    arcdt.get().cancel();
                    arcdt.get().start();
                } else {
                    mainStopwatch.cancel();
                    mainStopwatch.start();
                }
            }
        }
        else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}