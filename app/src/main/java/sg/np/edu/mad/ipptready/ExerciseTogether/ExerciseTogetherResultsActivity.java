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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uk.tastytoasty.TastyToasty;

import java.util.ArrayList;

import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;
import sg.np.edu.mad.ipptready.InternetConnectivity.Internet;
import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherResultsActivity extends AppCompatActivity {
    // Exercise Together feature done by: BRYAN KOH

    // Global variables
    Internet internet;
    CountDownTimer myCountDown;
    ArrayList<String> userIds = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> scores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together_results);

        // update results (get data from Firestore and set it on RecyclerView)
        updateResults(getIntent());

        // Button to leave session
        ImageButton leaveRoomBtn = findViewById(R.id.leaveResultsRoomBtn);
        leaveRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveSession();
            }
        });
    }

    // Get results from Firestore and set it on RecyclerView
    public void updateResults(Intent receivedIntent)
    {
        ExerciseTogetherSession.getCurrentSessionParticipants(receivedIntent.getStringExtra("QRString")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    QuerySnapshot querySnapshot = task.getResult();
                    for (DocumentSnapshot documentSnapshot : querySnapshot)
                    {
                        // Append userid and score to userIds ArrayList and scores ArrayList respectively (for users who have completed the session)
                        if(documentSnapshot.getData().get("status").equals("Completed"))
                        {
                            userIds.add(documentSnapshot.getId().toString());
                            scores.add((String) documentSnapshot.getData().get("score"));
                            Log.d("userID", documentSnapshot.getId().toString());
                        }
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
                                    // Get names of users from userIds ArrayList
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
                                Log.d("Names size (results)", String.valueOf(names.size()));

                                // update RecyclerView
                                if (!names.isEmpty())
                                {
                                    // ExerciseTogetherWaitingRoomAdapter initialized
                                    ExerciseTogetherResultsAdapter adapter = new ExerciseTogetherResultsAdapter(names, scores);

                                    // Set RecyclerView
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ExerciseTogetherResultsActivity.this);
                                    RecyclerView recyclerView = findViewById(R.id.ExTgtResultsRecyclerView);
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(adapter);

                                    internet = new Internet();
                                    countDownTimer();
                                }
                                else
                                {
                                    internet = new Internet();
                                    countDownTimer();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    // Countdown to refresh
    private void countDownTimer(){

        myCountDown = new CountDownTimer(9000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                recreateActivity();
            }
        };
        myCountDown.start();
    }

    private void recreateActivity()
    {
        // recreate activity when there is Internet
        if (internet.isOnline(ExerciseTogetherResultsActivity.this)) recreate();
        else
        {
            // If no Internet connection, send user to ExerciseTogetherNoInternetActivity
            Intent noConnectionIntent = new Intent(ExerciseTogetherResultsActivity.this, ExerciseTogetherNoInternetActivity.class);
            Bundle exerciseBundle = new Bundle();
            exerciseBundle.putString("date", getIntent().getStringExtra("date"));
            exerciseBundle.putString("sessionName", getIntent().getStringExtra("sessionName"));
            exerciseBundle.putString("exercise", getIntent().getStringExtra("exercise"));
            exerciseBundle.putString("userId", getIntent().getStringExtra("userId"));
            exerciseBundle.putParcelable("QRImage", getIntent().getExtras().getParcelable("QRImage"));
            exerciseBundle.putString("QRString", getIntent().getStringExtra("QRString"));
            exerciseBundle.putString("ExerciseTogetherSession", "yes");
            noConnectionIntent.putExtras(exerciseBundle);
            startActivity(noConnectionIntent);
            finish();
        }
    }

    // If user presses the back button, user will be prompted to leave session.
    public void leaveSession()
    {
        AlertDialog.Builder leaveAlert = new AlertDialog.Builder(ExerciseTogetherResultsActivity.this);
        leaveAlert
                .setTitle("Leave Session")
                .setMessage("Are you sure you want to leave this session?")
                .setCancelable(true)
                .setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TastyToasty.blue(ExerciseTogetherResultsActivity.this, "You have left the session", null).show();
                                Intent failedIntent = new Intent(ExerciseTogetherResultsActivity.this, ExerciseTogetherSession.class);
                                failedIntent.putExtra("userId", getIntent().getStringExtra("userId"));
                                finish();
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