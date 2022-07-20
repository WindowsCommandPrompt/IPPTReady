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
import java.util.Date;
import java.util.List;


import sg.np.edu.mad.ipptready.Cycle.CycleActivity;
import sg.np.edu.mad.ipptready.Cycle.IPPTCycleAdapter;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseViewItem;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTCycle;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTRoutine;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;
import sg.np.edu.mad.ipptready.R;
import sg.np.edu.mad.ipptready.RecordActivity;
import sg.np.edu.mad.ipptready.RoutineAlertReceiver;

public class RoutineActivity extends AppCompatActivity {
    private String userId;
    private String cycleId;
    private boolean isFinished;
    private Date DOB;
    private DocumentReference cycleDocRef;

    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private RecyclerView recyclerView;
    private IPPTRoutineAdapter ipptRoutineAdapter;
    public ActivityResultLauncher<Intent> GoRoutine;

    private FirebaseViewItem<IPPTRoutine> notFinishedRoutine;
    private List<FirebaseViewItem<IPPTRoutine>> finishedRoutines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_load_data);
        // set View temporary to a loading screen

        // Input to RoutineActivity:
        // "userId" : String, userId of User document
        // "cycleId" : String, cycleId of Cycle document
        // "DOB" : Date, Date of birth of user

        //
        // Output to RecordActivity:
        // "userId", String : Email Address of the user.
        // "cycleId", String : Id of the IPPTCycle
        // "routineId", String : Id of the IPPTCycle
        // "DOB" : Date, Date of birth of user
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
            isFinished = intent.getBooleanExtra("isFinished", false);
            DOB = (Date) intent.getSerializableExtra("DOB");
            cycleDocRef = IPPTCycle.getCycleDocFromId(IPPTUser.getUserDocFromId(userId), cycleId);
        }
        else if (null != savedInstanceState) {
            userId = savedInstanceState.getString("userId");
            cycleId = savedInstanceState.getString("cycleId");
            isFinished = savedInstanceState.getBoolean("isFinished");
            DOB = (Date) savedInstanceState.getSerializable("DOB");
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
                            finishedRoutines = new ArrayList<>();

                            if (!querySnapshot.isEmpty()) {

                                for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                    FirebaseViewItem<IPPTRoutine> ipptRoutineViewItem = new FirebaseViewItem<>(new IPPTRoutine(documentSnapshot.getData()),
                                            documentSnapshot.getReference());

                                    if (-1 != ipptRoutineViewItem.viewItem.IPPTScore)
                                        finishedRoutines.add(ipptRoutineViewItem);
                                    else
                                        notFinishedRoutine = ipptRoutineViewItem;
                                }

                                if (null != notFinishedRoutine) {
                                    setRoutineTextViewFields(notFinishedRoutine);
                                    setCompleteRoutineButton();
                                }
                                else
                                    setCreateRoutineButton();

                                setRecycleViewContent(finishedRoutines);
                            }
                            else {
                                setRecycleViewContent(finishedRoutines);
                                setCreateRoutineButton();
                            }
                        }
                        else {
                            Toast.makeText(RoutineActivity.this, "Failed to retrieve IPPT Cycles!", Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        }
                    }
                });
    }

    private void setRoutineView() {
        setContentView(R.layout.activity_routine);
        recyclerView = findViewById(R.id.routineRecyclerView);

        if (isFinished)
            findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
    }

    private void setRecycleViewContent(List<FirebaseViewItem<IPPTRoutine>> ipptRoutineList) {
        ipptRoutineAdapter = new IPPTRoutineAdapter(ipptRoutineList, RoutineActivity.this,
                userId,
                cycleId,
                 DOB);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(ipptRoutineAdapter);
    }

    private void setRoutineTextViewFields(FirebaseViewItem<IPPTRoutine> ipptRoutineViewItem) {
        ((TextView)findViewById(R.id.routinedateCreatedText)).setText(dateFormat
                .format(ipptRoutineViewItem.viewItem.DateCreated));
    }

    private class CreateRoutineOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Date dateCreated = new Date();
            FirebaseDocChange firebaseDocChange = IPPTRoutine.createNewRoutine(cycleDocRef, dateCreated);
            firebaseDocChange.changeTask
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                notFinishedRoutine = new FirebaseViewItem<>(new IPPTRoutine(dateCreated), firebaseDocChange.documentReference);
                                setCompleteRoutineButton();
                                Toast.makeText(RoutineActivity.this, "Routine Creation Successful!", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            else {
                                Toast.makeText(RoutineActivity.this, "Failed to create Routine!", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
        }
    }

    public static class RecordOnClickListener implements View.OnClickListener {
        public FirebaseViewItem<IPPTRoutine> ipptRoutineViewItem;
        public Context context;
        public String userId;
        public String cycleId;
        public Date DOB;

        public RecordOnClickListener(FirebaseViewItem<IPPTRoutine> ipptRoutineViewItem,
                                     Context context,
                                     String userId,
                                     String cycleId,
                                     Date DOB) {
            this.ipptRoutineViewItem = ipptRoutineViewItem;
            this.context = context;
            this.userId = userId;
            this.cycleId = cycleId;
            this.DOB = DOB;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, RecordActivity.class);

            intent.putExtra("userId", userId);
            intent.putExtra("cycleId", cycleId);
            intent.putExtra("routineId", ipptRoutineViewItem.documentReference.getId());
            intent.putExtra("isFinished", -1 != ipptRoutineViewItem.viewItem.IPPTScore);
            intent.putExtra("DOB", DOB);

            context.startActivity(intent);
        }
    }

    // to set back the completecreateRoutineButton
    private void setCreateRoutineButton() {
        findViewById(R.id.completecreateroutineButton).setOnClickListener(new RoutineActivity.CreateRoutineOnClickListener());
        findViewById(R.id.constraintLayout2).setOnClickListener(null);
    }

    private void setCompleteRoutineButton() {
        ((TextView)findViewById(R.id.routinedateCreatedText)).setText(dateFormat.format(notFinishedRoutine.viewItem.DateCreated));
        findViewById(R.id.completecreateroutineButton).setVisibility(View.GONE);
        findViewById(R.id.constraintLayout2).setOnClickListener(new RecordOnClickListener(notFinishedRoutine, RoutineActivity.this,
                userId, cycleId, DOB));
    }

    @Override
    protected  void onSaveInstanceState(@NonNull Bundle outState) {
        // write code here!
        outState.putString("userId", userId);
        outState.putString("cycleId", cycleId);
        outState.putBoolean("isFinished", isFinished);
        outState.putSerializable("DOB", DOB);
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