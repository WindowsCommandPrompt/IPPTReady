package sg.np.edu.mad.ipptready;

import androidx.annotation.*;
import androidx.appcompat.app.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.*;
import android.location.*;
import android.os.*;
import android.text.Layout;
import android.util.Log;
import android.widget.*;

import com.google.android.gms.location.*;
import com.google.android.gms.tasks.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RunActivity extends AppCompatActivity {

    public ArrayList<String> timings = new ArrayList<String>();

    CountDownTimer mainStopwatch;

    AtomicReference<CountDownTimer> arcdt = new AtomicReference<>(null);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        AlertDialog.Builder confirmTerminateCycle = new AlertDialog.Builder(this);
        AlertDialog.Builder saveCycleData = new AlertDialog.Builder(this);

        //Build the countdown
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

        saveCycleData
            .setTitle("Save data?")
            .setMessage("Are you sure you would like to save your data?")
            .setCancelable(false)
            .setPositiveButton(
                "YES",
                (DialogInterface di, int i) -> {
                    //take note of the timing that is within the TextView
                    String capturedTiming = ((TextView) findViewById(R.id.timing_indicator_text)).getText().toString();
                    Intent intent = new Intent();
                    intent.setClassName("sg.np.edu.mad.ipptready.RunActivity", "sg.np.edu.mad.ipptready.RunRecord");
                    intent.putExtra("TimeTakenForTheRun", capturedTiming);
                    finish();
                }
            )
            .setNegativeButton(
                "NO",
                (DialogInterface di, int i) -> {
                    finish();
                }
            );

        //Build the messages accordingly...
        confirmTerminateCycle
            .setTitle("Terminate cycle?")
            .setMessage("Are you sure you want to terminate this run cycle?")
            .setPositiveButton(
                "YES",
                (DialogInterface di, int i) -> {
                    //If the user presses yes THEN STOP THE TIMER
                    saveCycleData.create().show();
                }
            )
            .setNegativeButton(
                "NO",
                (DialogInterface di, int i) -> {
                    //If the user presses no then
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
                            //Add the relevant event listener to terminate cycle
                            ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
                                Toast.makeText(RunActivity.this, "The timer has been paused", Toast.LENGTH_SHORT);
                                this.cancel();
                                confirmTerminateCycle.create().show();
                            });
                            //when the orientation of the screen has been changed
                            this.start();
                        }
                    });
                    arcdt.get().start();
                }
            )
            .setCancelable(false);

        //Add the relevant event listener to start the cycle
        ((LinearLayout) findViewById(R.id.startCycle)).setOnClickListener(andThenFunctionAs -> {
            //start the timer
            mainStopwatch.start();
            ((LinearLayout) findViewById(R.id.startCycle)).setClickable(false);
            ((LinearLayout) findViewById(R.id.startCycle)).setEnabled(false);
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "Stop watch has been activated", Toast.LENGTH_SHORT).show();
            //Add the relevant event listener to terminate cycle
            ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
                mainStopwatch.cancel();
                confirmTerminateCycle.create().show();
                Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "The timer has been paused", Toast.LENGTH_SHORT).show();
            });
        });

        ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "Please start the stopwatch first before you can terminate the stopwatch. Common sense", Toast.LENGTH_SHORT).show();
        });
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