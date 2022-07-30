package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.uk.tastytoasty.TastyToasty;

import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.InternetConnectivity.Internet;
import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherWaitingRoomActivity extends AppCompatActivity {
    Intent noInternetIntent = new Intent();
    Internet internet;
    CountDownTimer myCountDown;
    int secondsLeft;
    TextView secondsLeftTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercisetogetherwaitingroom);

        Intent receivedIntent = getIntent();
        TextView sessionNameTextView = findViewById(R.id.SessionNameWaitingRoom);
        sessionNameTextView.setText("\"" + receivedIntent.getStringExtra("sessionName") + "\"");
        ImageView QRCode = findViewById(R.id.QRcode);
        try {
            QRCode.setImageBitmap(receivedIntent.getExtras().getParcelable("QRImage"));
        }
        catch (Exception e) {
            QRCode.setVisibility(View.GONE);
        }

        secondsLeft = 11;
        internet = new Internet();
        secondsLeftTextView = findViewById(R.id.refreshWaitingRoomTextView);
        countDownTimer();

        ImageButton leaveRoomBtn = findViewById(R.id.leaveWaitingRoomButton);
        leaveRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder leaveAlert = new AlertDialog.Builder(ExerciseTogetherWaitingRoomActivity.this);
                leaveAlert
                        .setTitle("Leave Session")
                        .setMessage("Are you sure you want to leave this session?")
                        .setCancelable(true)
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseDocChange firebaseDocChangeJoinSessionStatus = ExerciseTogetherSession.updateJoinStatus(receivedIntent.getStringExtra("userId"), receivedIntent.getStringExtra("QRString"), "Left");
                                        firebaseDocChangeJoinSessionStatus.changeTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    TastyToasty.blue(ExerciseTogetherWaitingRoomActivity.this, "You have left the session", null).show();
                                                    Intent failedIntent = new Intent(ExerciseTogetherWaitingRoomActivity.this, ExerciseTogetherSession.class);
                                                    failedIntent.putExtra("userId", receivedIntent.getStringExtra("userId"));
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                })
                        .setNegativeButton("No", null);
                leaveAlert.create().show();
            }
        });
    }

    private void countDownTimer(){

        myCountDown = new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsLeft--;
                secondsLeftTextView.setText("Refreshing in: " + String.valueOf(secondsLeft) + "s");
            }

            @Override
            public void onFinish() {
                if (internet.isOnline(ExerciseTogetherWaitingRoomActivity.this)) recreate();
                else
                {
                    Intent noConnectionIntent = new Intent(ExerciseTogetherWaitingRoomActivity.this, ExerciseTogetherNoInternetActivity.class);
                    Bundle noConnectBundle = new Bundle();
                    noConnectBundle.putString("date", getIntent().getStringExtra("date"));
                    noConnectBundle.putString("sessionName", getIntent().getStringExtra("sessionName"));
                    noConnectBundle.putString("exercise", getIntent().getStringExtra("exercise"));
                    noConnectBundle.putString("userId", getIntent().getStringExtra("userId"));
                    noConnectBundle.putParcelable("QRImage", getIntent().getExtras().getParcelable("QRImage"));
                    noConnectBundle.putString("QRString", getIntent().getStringExtra("QRString"));
                    noConnectionIntent.putExtras(noConnectBundle);
                    startActivity(noConnectionIntent);
                    finish();
                }
            }
        };
        myCountDown.start();
    }
}