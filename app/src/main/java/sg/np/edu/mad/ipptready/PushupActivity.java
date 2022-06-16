package sg.np.edu.mad.ipptready;

import android.content.DialogInterface;
import android.os.*;
import android.widget.*;

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

        timeIsUp
            .setTitle("You ran out of time")
            .setMessage("You ran out of time. Please remember to key in the number of push ups that you have done for the past 1 minute")
            .setPositiveButton(
                "OK",
                (DialogInterface di, int i) -> {

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
            Toast.makeText(this, "The timer has already begun", Toast.LENGTH_SHORT);
            long timeAvailable = Long.parseLong(((TextView) findViewById(R.id.timing_indicator_text)).getText().toString()) * 1000;
            CountDownTimer mainCountdownTimer = new CountDownTimer(timeAvailable, 1000){
                @Override
                public void onTick(long millisLeft) {
                    ((TextView) findViewById(R.id.timing_indicator_text)).setText(Long.toString(millisLeft / 1000));
                }
                @Override
                public void onFinish() {
                    timeIsUp.create().show();
                }
            }.start();
        });
    }
}
