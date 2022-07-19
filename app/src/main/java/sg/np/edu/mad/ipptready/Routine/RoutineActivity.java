package sg.np.edu.mad.ipptready.Routine;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import sg.np.edu.mad.ipptready.Cycle.CycleActivity;
import sg.np.edu.mad.ipptready.Cycle.IPPTCycleAdapter;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseViewItem;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTCycle;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTRoutine;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;
import sg.np.edu.mad.ipptready.R;
import sg.np.edu.mad.ipptready.RoutineAlertReceiver;

public class RoutineActivity extends AppCompatActivity {
    private String userId;
    private String cycleId;
    private DocumentReference cycleDocRef;

    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private RecyclerView recyclerView;
    private IPPTRoutineAdapter ipptRoutineAdapter;
    public ActivityResultLauncher<Intent> GoRoutine;

    private FirebaseViewItem<IPPTRoutine> notFinishedRoutine = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_load_data);
        // set View temporary to a loading screen

        // Input to RoutineActivity:
        // "userId" : String, userId of User FrirebaseDocument
        // "cycleId" : String, cycleId of Cycle FirebaseDocument
        //
        // Output to RecordActivity:
        // "userId", String : Email Address of the user.
        // "cycleId", String : Id of the IPPTCycle
        // "routineId", String : Id of the IPPTCycle
        // Note:        Make sure to save the Input data using the onSaveInstanceState(android.os.Bundle),
        //                  so that we don't need to retrieve the data again!
        //
        // Extra Note: Check saveInstanceState if Intent is empty!

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

        if (null != getIntent()) {
            Intent intent =getIntent();
            userId = intent.getStringExtra("userId");
            cycleId = intent.getStringExtra("cycleId");
            cycleDocRef = IPPTCycle.getCycleDocFromId(IPPTUser.getUserDocFromId(userId), cycleId);
        }
        else if (null != savedInstanceState) {
            userId = savedInstanceState.getString("userId");
            cycleId = savedInstanceState.getString("cycleId");
            cycleDocRef = IPPTCycle.getCycleDocFromId(IPPTUser.getUserDocFromId(userId), cycleId);
        }
        else {
            GenericErrorToast.show();
            finish();
        }

        IPPTRoutine.getRoutinesFromCycle(cycleDocRef)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            setRoutineView();
                            QuerySnapshot querySnapshot = task.getResult();

                            if (!querySnapshot.isEmpty()) {
                                ArrayList<FirebaseViewItem<IPPTRoutine>> finishedRoutines = new ArrayList<>();

                                for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                    FirebaseViewItem<IPPTRoutine> ipptRoutineViewItem = new FirebaseViewItem<>(new IPPTRoutine(documentSnapshot.getData()),
                                            documentSnapshot.getReference());

                                    if (-1 != ipptRoutineViewItem.viewItem.IPPTScore)
                                        finishedRoutines.add(ipptRoutineViewItem);
                                    else
                                        notFinishedRoutine = ipptRoutineViewItem;
                                }

                                if (null != notFinishedRoutine) {

                                }
                                else
                                    setCreateRoutineButton();

                                setRecycleViewContent(finishedRoutines);
                            }
                            else {

                            }
                        }
                        else {

                        }
                    }
                });
    }

    private void setRoutineView() {
        setContentView(R.layout.activity_routine);
        recyclerView = findViewById(R.id.routineRecyclerView);
    }

    private void setRecycleViewContent(List<FirebaseViewItem<IPPTRoutine>> ipptCycleList) {
        /*ipptRoutineAdapter = new IPPTCycleAdapter(ipptCycleList, CycleActivity.this,
                userId,
                );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(ipptCycleAdapter);*/
    }

    private void setCycleTextViewFields(FirebaseViewItem<IPPTCycle> ipptCycleViewItem) {
        ((TextView)findViewById(R.id.cyclenameText)).setText(ipptCycleViewItem.viewItem.Name);
        ((TextView)findViewById(R.id.cycledateCreatedText)).setText(dateFormat.format(ipptCycleViewItem.viewItem.DateCreated));
    }

    private void addAlarm()
    {
        Intent routineAlertIntent = new Intent(getApplicationContext(), RoutineAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, routineAlertIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + 60000);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private class CreateRoutineOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("RoutineActivity", "Creating new Routine, going to RecordActivity");

        }
    }

    private class GoRecordOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("CycleActivity", "View Clicked! Going to RecordActivity...");

        }
    }

    private class GoRoutineActivityResultCallback implements ActivityResultCallback<ActivityResult> {
        @Override
        public void onActivityResult(ActivityResult result) {
            recreate();
        }
    }

    // to set back the completecreateRoutineButton
    private void setCreateRoutineButton() {
        findViewById(R.id.completecreateroutineButton).setOnClickListener(new RoutineActivity.CreateRoutineOnClickListener());
        findViewById(R.id.constraintLayout2).setOnClickListener(null);
    }

    @Override
    protected  void onSaveInstanceState(@NonNull Bundle outState) {
        // write code here!
        outState.putString("userId", userId);
        outState.putString("cycleId", cycleId);
        // make sure to call super after writing code ...
        super.onSaveInstanceState(outState);
    }

    // remember to clean up the launchers after the activity finishes
    @Override
    protected void onDestroy() {
        if (null != GoRoutine) {
            GoRoutine.unregister();
        }
        super.onDestroy();
    }
}