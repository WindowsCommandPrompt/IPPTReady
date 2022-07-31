package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uk.tastytoasty.TastyToasty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sg.np.edu.mad.ipptready.AutoPushupActivity;
import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;
import sg.np.edu.mad.ipptready.InternetConnectivity.Internet;
import sg.np.edu.mad.ipptready.PushupActivity;
import sg.np.edu.mad.ipptready.R;
import sg.np.edu.mad.ipptready.SitupActivity;
import sg.np.edu.mad.ipptready.VideoActivity;
import sg.np.edu.mad.ipptready.VideoAdapter;

public class ExerciseTogetherWaitingRoomActivity extends AppCompatActivity {
    Internet internet;
    CountDownTimer myCountDown;
    int secondsLeft;
    TextView secondsLeftTextView;
    ArrayList<String> userIds = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercisetogetherwaitingroom);

        Intent receivedIntent = getIntent();
        TextView sessionNameTextView = findViewById(R.id.SessionNameWaitingRoom);
        sessionNameTextView.setText("\"" + receivedIntent.getStringExtra("sessionName") + "\" , " + receivedIntent.getStringExtra("exercise"));
        ImageView QRCode = findViewById(R.id.QRcode);
        try {
            QRCode.setImageBitmap(receivedIntent.getExtras().getParcelable("QRImage"));
        }
        catch (Exception e) {
            QRCode.setVisibility(View.GONE);
        }

        updateParticipants(receivedIntent);

        ImageButton leaveRoomBtn = findViewById(R.id.leaveWaitingRoomButton);
        leaveRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveSession();
            }
        });

        Button startExerciseBtn = findViewById(R.id.startExerciseExTgt);
        startExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = receivedIntent.getStringExtra("userId");
                String hostUserId = receivedIntent.getStringExtra("hostUserId");
                String date = receivedIntent.getStringExtra("date");
                if (!userId.equals(hostUserId))
                {
                    ExerciseTogetherSession.getSessionsbyUserID(hostUserId)
                            .document(date)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful())
                            {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.getData().get("status").equals("Started")) startSession(receivedIntent);
                                else
                                {
                                    TastyToasty.blue(ExerciseTogetherWaitingRoomActivity.this, "Host has not started session!", null).show();
                                    return;
                                }

                            }
                        }
                    });
                }
                else startSession(receivedIntent);

            }
        });
    }

    public void updateParticipants(Intent receivedIntent)
    {
        ExerciseTogetherSession.getCurrentSessionParticipants(receivedIntent.getStringExtra("QRString")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    QuerySnapshot querySnapshot = task.getResult();
                    for (DocumentSnapshot documentSnapshot : querySnapshot)
                    {
                        if(documentSnapshot.getData().get("status").equals("Joined")) userIds.add(documentSnapshot.getId().toString());
                    }
                    IPPTUser.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                String hostUserName = "";
                                QuerySnapshot querySnapshot1 = task.getResult();
                                int completedCount = 0;
                                for (DocumentSnapshot documentSnapshot : querySnapshot1)
                                {
                                    for (String user : userIds)
                                    {
                                        if (documentSnapshot.getId().equals(user))
                                        {
                                            names.add((String) documentSnapshot.getData().get("Name"));
                                            if (documentSnapshot.getId().equals(receivedIntent.getStringExtra("hostUserId"))) hostUserName = (String) documentSnapshot.getData().get("Name");
                                            completedCount++;
                                            break;
                                        }
                                    }
                                    if (completedCount == userIds.size()) break;
                                }
                                Log.d("Names size", String.valueOf(names.size()));
                                TextView TextViewPeopleList = findViewById(R.id.TextViewPeopleList);
                                TextView SessionNameWaitingRoom = findViewById(R.id.SessionNameWaitingRoom);
                                CardView cardPeopleWaiting = findViewById(R.id.cardPeopleWaiting);

                                if (!names.isEmpty())
                                {
                                    // ExerciseTogetherWaitingRoomAdapter initialized
                                    ExerciseTogetherWaitingRoomAdapter adapter = new ExerciseTogetherWaitingRoomAdapter(names, hostUserName, ExerciseTogetherWaitingRoomActivity.this);

                                    // RecyclerView
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ExerciseTogetherWaitingRoomActivity.this);
                                    RecyclerView recyclerView = findViewById(R.id.exerciseTogetherRecyclerView);
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(adapter);

                                    secondsLeft = 11;
                                    internet = new Internet();
                                    secondsLeftTextView = findViewById(R.id.refreshWaitingRoomTextView);
                                    countDownTimer();
                                }
                                else
                                {
                                    TextViewPeopleList.setVisibility(View.GONE);
                                    SessionNameWaitingRoom.setVisibility(View.GONE);
                                    cardPeopleWaiting.setVisibility(View.GONE);

                                    secondsLeft = 11;
                                    internet = new Internet();
                                    secondsLeftTextView = findViewById(R.id.refreshWaitingRoomTextView);
                                    countDownTimer();
                                }
                            }
                        }
                    });
                }
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
                recreateActivity();
            }
        };
        myCountDown.start();
    }

    private void startSession(Intent receivedIntent)
    {
        String exercise = receivedIntent.getStringExtra("exercise");
        String userId = receivedIntent.getStringExtra("userId");
        String date = receivedIntent.getStringExtra("date");
        String qrstring = getIntent().getStringExtra("QRString");

        Bundle exerciseBundle = new Bundle();
        exerciseBundle.putString("date", date);
        exerciseBundle.putString("sessionName", getIntent().getStringExtra("sessionName"));
        exerciseBundle.putString("exercise", getIntent().getStringExtra("exercise"));
        exerciseBundle.putString("userId", userId);
        exerciseBundle.putParcelable("QRImage", getIntent().getExtras().getParcelable("QRImage"));
        exerciseBundle.putString("QRString", qrstring);
        exerciseBundle.putString("ExerciseTogetherSession", "yes");

        TastyToasty.makeText(ExerciseTogetherWaitingRoomActivity.this, "Starting Session...", TastyToasty.SHORT, null, R.color.success, R.color.white, false).show();

        ExerciseTogetherSession.startSession(userId, date)
                .changeTask
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            ExerciseTogetherSession.updateJoinStatus(userId, qrstring, "Started")
                                    .changeTask
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                if (exercise.equals("Push-ups"))
                                                {
                                                    Intent exerciseIntent = new Intent(ExerciseTogetherWaitingRoomActivity.this, PushupActivity.class);
                                                    exerciseIntent.putExtras(exerciseBundle);
                                                    startActivity(exerciseIntent);
                                                    finish();
                                                }
                                                else if (exercise.equals("Sit-ups"))
                                                {
                                                    Intent exerciseIntent = new Intent(ExerciseTogetherWaitingRoomActivity.this, SitupActivity.class);
                                                    exerciseIntent.putExtras(exerciseBundle);
                                                    startActivity(exerciseIntent);
                                                    finish();
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void recreateActivity()
    {
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
            noConnectBundle.putString("hostUserId", getIntent().getStringExtra("hostUserId"));
            noConnectionIntent.putExtras(noConnectBundle);
            startActivity(noConnectionIntent);
            finish();
        }
    }

    public void leaveSession()
    {
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
                                FirebaseDocChange firebaseDocChangeJoinSessionStatus = ExerciseTogetherSession.updateJoinStatus(getIntent().getStringExtra("userId"), getIntent().getStringExtra("QRString"), "Left");
                                firebaseDocChangeJoinSessionStatus.changeTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            TastyToasty.blue(ExerciseTogetherWaitingRoomActivity.this, "You have left the session", null).show();
                                            Intent failedIntent = new Intent(ExerciseTogetherWaitingRoomActivity.this, ExerciseTogetherSession.class);
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

    @Override
    public void onBackPressed() {
        leaveSession();
    }
}