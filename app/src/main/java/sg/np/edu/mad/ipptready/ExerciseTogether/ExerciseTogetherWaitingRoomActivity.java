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

import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;
import sg.np.edu.mad.ipptready.InternetConnectivity.Internet;
import sg.np.edu.mad.ipptready.R;
import sg.np.edu.mad.ipptready.VideoActivity;
import sg.np.edu.mad.ipptready.VideoAdapter;

public class ExerciseTogetherWaitingRoomActivity extends AppCompatActivity {
    Intent noInternetIntent = new Intent();
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
                                QuerySnapshot querySnapshot1 = task.getResult();
                                int completedCount = 0;
                                for (DocumentSnapshot documentSnapshot : querySnapshot1)
                                {
                                    for (String user : userIds)
                                    {
                                        if (documentSnapshot.getId().equals(user))
                                        {
                                            names.add((String) documentSnapshot.getData().get("Name"));
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
                                    ExerciseTogetherWaitingRoomAdapter adapter = new ExerciseTogetherWaitingRoomAdapter(names);

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
                recreateActivity();
            }
        };
        myCountDown.start();
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
            noConnectionIntent.putExtras(noConnectBundle);
            startActivity(noConnectionIntent);
            finish();
        }
    }
}