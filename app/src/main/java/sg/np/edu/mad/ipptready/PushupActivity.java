package sg.np.edu.mad.ipptready;

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

        AlertDialog.Builder timerNotStartedError = new AlertDialog.Builder(this);

        ((LinearLayout) findViewById(R.id.resetTimer)).setOnClickListener(function -> {
            Toast.makeText(this, "The timer has not been activated, please activate the timer before you can perform the other actions", Toast.LENGTH_SHORT).show();
        });

        ((LinearLayout) findViewById(R.id.pauseTimer)).setOnClickListener(function -> {
            Toast.makeText(this, "The timer has not been activated, please activate the timer before you can perform the other actions", Toast.LENGTH_SHORT).show();
        });

        //Once the timer has been activated by the user....
        ((LinearLayout) findViewById(R.id.startCycle)).setOnClickListener(function -> {
            Toast.makeText(this, "The timer has already begun", Toast.LENGTH_SHORT);
            CountDownTimer mainCountdownTimer = new CountDownTimer(1000, 1000){
                @Override
                public void onTick(long millisLeft) {

                }
                @Override
                public void onFinish() {

                }
            };
        });
    }
}
