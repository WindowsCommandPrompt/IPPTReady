package sg.np.edu.mad.ipptready;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PushupActivity extends AppCompatActivity {

    //This PushupActivity would be responsible for maintaining the activity_pushup.xml (sorry la my english bad as hell)-->
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushup);

        AlertDialog.Builder timeIsUp = new AlertDialog.Builder(this);
        AlertDialog.Builder confirmationNotSaveData = new AlertDialog.Builder(this);

        //METADATA
        Intent whiteHoleConnectingRecordActivityBlackHole = getIntent();
        String cycleID = whiteHoleConnectingRecordActivityBlackHole.getStringExtra("IPPTCycleId");
        String routineID = whiteHoleConnectingRecordActivityBlackHole.getStringExtra("IPPTRoutineId");
        String recordID = whiteHoleConnectingRecordActivityBlackHole.getStringExtra("IPPTRecordId");
        String email = whiteHoleConnectingRecordActivityBlackHole.getStringExtra("Email");

        Intent whiteHole = getIntent();
        ((TextView) findViewById(R.id.targetNumberOfPushUps)).setText(whiteHole.getStringExtra("NumPushups"));

        confirmationNotSaveData
            .setTitle("Are you sure?")
            .setMessage("Are you sure you do not want to save your data (at least in a really secure location)?")
            .setPositiveButton(
                "YES",
                (DialogInterface di, int i) -> {

                }
            )
            .setNegativeButton(
                "NO",
                (DialogInterface di, int i) -> {

                }
            )
            .setNeutralButton(
                "LET ME THINK FIRST",
                (DialogInterface di, int i) -> {

                }
            )
            .setCancelable(false);

        timeIsUp
            .setTitle("You ran out of time")
            .setMessage("You ran out of time. Please remember to key in the number of push ups that you have done for the past 1 minute")
            .setPositiveButton(
                "OK",
                (DialogInterface di, int i) -> {
                    ((LinearLayout) findViewById(R.id.pushUpRecordTimingInterface)).setVisibility(View.GONE);
                    ((LinearLayout) findViewById(R.id.pushUpActivityEnterRecords)).setVisibility(View.VISIBLE);
                    //Get the text from the edit text field...
                    String numPushUpsDone = ((EditText) findViewById(R.id.numberOfPushUpsThatTheUserDid)).getText().toString();

                    //If the user would like to save the data to the database....
                    ((Button) findViewById(R.id.setPushUpActivity)).setOnClickListener(function -> {
                        if (numPushUpsDone.length() > 0){

                        }
                        else if (numPushUpsDone.length() == 0){
                            Toast.makeText(this, "Uh oh! That is not a good value! Field cannot be left blank if you are submitting the number of push ups that you have done to the database!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //If the user would NOT like to save the data to the database...
                    ((Button) findViewById(R.id.dontSet)).setOnClickListener(functionAs -> {
                        confirmationNotSaveData.create().show();
                    });
                }
            )
            .setCancelable(false);

        ((LinearLayout) findViewById(R.id.resetTimer)).setOnClickListener(function -> {
            Toast.makeText(this, "The timer has not been activated, please activate the timer before you can perform the other actions", Toast.LENGTH_SHORT).show();
        });

        ((LinearLayout) findViewById(R.id.pauseTimer)).setOnClickListener(function -> {
            Toast.makeText(this, "The timer has not been activated, please activate the timer before you can perform the other actions", Toast.LENGTH_SHORT).show();
        });

        //Once the timer has been activated by the user....
        ((LinearLayout) findViewById(R.id.startTimer)).setOnClickListener(function -> {
            //We only want the timer to be clicked on once, which that means we will need to disable the layout after the
            ((LinearLayout) findViewById(R.id.startTimer)).setEnabled(false);
            Toast.makeText(this, "The timer has already begun", Toast.LENGTH_SHORT).show();
            long timeAvailable = Long.parseLong(((TextView) findViewById(R.id.timing_indicator_text)).getText().toString()) * 1000;
            CountDownTimer mainCountdownTimer = new CountDownTimer(timeAvailable, 10){
                @Override
                public void onTick(long millisLeft) {
                    ((TextView) findViewById(R.id.timing_indicator_text)).setText(Long.toString(millisLeft / 1000));
                }
                @Override
                public void onFinish() {
                    timeIsUp.create().show();
                }
            }.start();

            //If the user would like to reset the timer
            ((LinearLayout) findViewById(R.id.resetTimer)).setOnClickListener(thenFunctionAs -> {
                Toast.makeText(this, "The stopwatch has been reset", Toast.LENGTH_SHORT).show();
                mainCountdownTimer.cancel(); //The stopwatch will stop..
                ((TextView) findViewById(R.id.timing_indicator_text)).setText("60");  //initialize the amount of time remaining back to
                ((TextView) findViewById(R.id.startCycleInternalText)).setText("Resume");
                ((LinearLayout) findViewById(R.id.startTimer)).setEnabled(true); //We will have to re-enable the button again
                //The user would have to manually the start the timer by himself...OBVIOUSLY
                ((LinearLayout) findViewById(R.id.startTimer)).setOnClickListener(onUserClick -> {
                    Toast.makeText(this, "Resuming timer...", Toast.LENGTH_SHORT).show(); //Display the Toast message which states that the timer is resuming...
                    mainCountdownTimer.start(); //start the stopwatch again....
                    ((LinearLayout) findViewById(R.id.startTimer)).setEnabled(false); //Disable the button again...
                });

                ((LinearLayout) findViewById(R.id.resetTimer)).setOnClickListener(onUserClick -> {

                });
            });
        });
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
}
