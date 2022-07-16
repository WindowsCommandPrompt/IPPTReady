package sg.np.edu.mad.ipptready;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RoutineActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String EmailAddress, IPPTCycleId;
    private byte[] SerializedIPPTCycle;
    private List<IPPTRoutine> ipptRoutineList;
    private IPPTRoutine currentIpptRoutine;
    private IPPTRoutineAdapter ipptRoutineAdapter;
    public ActivityResultLauncher<Intent> GoRoutine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // set View temporary to a loading screen
        setContentView(R.layout.activity_load_data);

        // Input to RoutineActivity:
        // "Email", String : Email Address of the user.
        // "IPPTCycle", byteArray : Serialized IPPTCycle Object
        // "IPPTCycleId", String : Id of the IPPTCycle
        //
        // Output to RecordActivity:
        // "Email", String : Email Address of the user.
        // "IPPTCycleId", String : Id of the IPPTCycle
        // "IPPTRoutineId", String : Id of the IPPTCycle
        // "IPPTRoutine", byteArray : Serialized IPPTRoutine Object

        // Note:        Make sure to save the Input data using the onSaveInstanceState(android.os.Bundle),
        //                  so that we don't need to retrieve the data again!
        //
        // Extra Note: Check saveInstanceState if Intent is empty!

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);
        Intent intent = getIntent();
        EmailAddress = intent.getStringExtra("Email");
        IPPTCycleId = intent.getStringExtra("IPPTCycleId");
        SerializedIPPTCycle = intent.getByteArrayExtra("IPPTCycle");

        if (null == EmailAddress ||
            null == IPPTCycleId ||
            null == SerializedIPPTCycle) {
            EmailAddress = savedInstanceState.getString("Email");
            IPPTCycleId = savedInstanceState.getString("IPPTCycleId");
            SerializedIPPTCycle = savedInstanceState.getByteArray("IPPTCycle");
            if (null == EmailAddress ||
                null == IPPTCycleId ||
                null == SerializedIPPTCycle) {

                GenericErrorToast.show();
                finish();
            }
        }
        IPPTCycle ipptCycle = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(SerializedIPPTCycle);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            // casting will work 100%! Clueless
            ipptCycle = (IPPTCycle) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // show generic error message ...

            GenericErrorToast.show();
            e.printStackTrace();
            finish();
        }
        GoRoutine = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new GoRoutineActivityResultCallback());
        IPPTCycle finalIpptCycle = ipptCycle;
        ipptCycle.getRoutineList(EmailAddress,IPPTCycleId,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // set the view back to the activity after done loading
                            setContentView(R.layout.activity_routine);
                            recyclerView = findViewById(R.id.routineRecyclerView);
                            if (finalIpptCycle.isFinished) {
                                Log.d("RoutineActivity", "IPPTCycle is already finished!");
                                findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
                                findViewById(R.id.previousroutinetext).setVisibility(View.GONE);
                            }
                            if (!task.getResult().isEmpty()) {
                                List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                                ipptRoutineList = task.getResult().toObjects(IPPTRoutine.class);
                                currentIpptRoutine = null;

                                for (IPPTRoutine ipptRoutineItem : ipptRoutineList) {
                                    if (!ipptRoutineItem.isFinished) {
                                        Log.d("RoutineActivity", "Routine Completed Detected!");
                                        currentIpptRoutine = ipptRoutineItem;
                                        ipptRoutineList.remove(currentIpptRoutine);

                                        findViewById(R.id.constraintLayout2).setOnClickListener(new GoRecordOnClickListener());
                                        break;
                                    }
                                }
                                if (null == currentIpptRoutine) {
                                    Log.d("CycleActivity", "No Active Routines Found!");
                                    setCreateRoutineButton();
                                }
                                else {
                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    ((TextView)findViewById(R.id.routinedateCreatedText)).setText(dateFormat.format(currentIpptRoutine.DateCreated));
                                    findViewById(R.id.completecreateroutineButton).setVisibility(View.GONE);
                                }
                                // set recyclerview with the list if list is not empty
                                ipptRoutineAdapter = new IPPTRoutineAdapter(ipptRoutineList, RoutineActivity.this, EmailAddress,
                                        IPPTCycleId, RoutineActivity.this);

                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(ipptRoutineAdapter);
                            }
                            else {
                                // if no routines found, set with empty list
                                Log.d("RoutineActivity", "Routine Collection is empty!");
                                ipptRoutineAdapter = new IPPTRoutineAdapter(new ArrayList<IPPTRoutine>(), RoutineActivity.this,
                                        EmailAddress, IPPTCycleId, RoutineActivity.this);

                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(ipptRoutineAdapter);
                                setCreateRoutineButton();
                            }
                        }
                        else {
                            // if the data retrieval fails go back to the previous activity
                            GenericErrorToast.show();
                            finish();
                        }
                    }
                });
    }

    private class CreateRoutineOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("RoutineActivity", "Creating new Routine, going to RecordActivity");
            Intent recordIntent = new Intent(RoutineActivity.this, RecordActivity.class);
            // pack data and send it to CreateCycleActivity...
            recordIntent.putExtra("Email", EmailAddress);
            recordIntent.putExtra("IPPTCycleId", IPPTCycleId);

            ByteArrayInputStream bis = new ByteArrayInputStream(SerializedIPPTCycle);
            IPPTCycle ipptCycle = null;
            try {
                ObjectInputStream ois = new ObjectInputStream(bis);
                // casting will work 100%! Clueless
                ipptCycle = (IPPTCycle) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                Toast.makeText(RoutineActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                finish();
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            IPPTCycle finalIpptCycle = ipptCycle;
            db.collection("IPPTUser")
                    .document(EmailAddress)
                    .collection("IPPTCycle")
                    .document(IPPTCycleId)
                    .collection("IPPTRoutine")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Date todayDate = new Date();
                                for (DocumentSnapshot document : task.getResult()) {
                                    Date documentDate = (Date) document.get("DateCreated", Date.class);
                                    // check if the routine has been done today!
                                    if (documentDate.getYear() == todayDate.getYear() && documentDate.getMonth() == todayDate.getMonth() && documentDate.getDay() == todayDate.getDay())
                                    {
                                        Toast.makeText(RoutineActivity.this, "Come back tomorrow to start a new Routine :)", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                IPPTRoutine ipptRoutine = new IPPTRoutine();
                                ipptRoutine.DateCreated = new Date();
                                ipptRoutine.IPPTScore = 0;
                                ipptRoutine.isFinished = false;

                                finalIpptCycle.addNewIPPTRoutineToDatabase(EmailAddress, ipptRoutine, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                    db.collection("IPPTUser")
                                                            .document(EmailAddress)
                                                            .collection("IPPTCycle")
                                                            .document(IPPTCycleId)
                                                            .collection("IPPTRoutine")
                                                            .whereEqualTo("DateCreated", ipptRoutine.DateCreated)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        if (!task.getResult().isEmpty()) {
                                                                            currentIpptRoutine = task.getResult().iterator().next().toObject(IPPTRoutine.class);
                                                                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                                            ((TextView)findViewById(R.id.routinedateCreatedText)).setText(
                                                                                    dateFormat.format(currentIpptRoutine.DateCreated)
                                                                            );
                                                                            findViewById(R.id.completecreateroutineButton).setVisibility(View.GONE);
                                                                            findViewById(R.id.constraintLayout2).setOnClickListener(new GoRecordOnClickListener());

                                                                            String IPPTRoutineId = task.getResult().iterator().next().getId();
                                                                            recordIntent.putExtra("IPPTRoutineId", IPPTRoutineId);
                                                                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                                                            try {
                                                                                ObjectOutputStream oos = new ObjectOutputStream(bos);
                                                                                oos.writeObject(ipptRoutine);
                                                                                recordIntent.putExtra("IPPTRoutine", bos.toByteArray());
                                                                            } catch (IOException e) {
                                                                                // If error occurred, display friendly message to user
                                                                                Toast.makeText(RoutineActivity.this, "Unexpected error occurred",
                                                                                        Toast.LENGTH_SHORT).show();
                                                                                e.printStackTrace();
                                                                                return;
                                                                            }

                                                                            addAlarm();

                                                                            if (null != GoRoutine) {
                                                                                GoRoutine.launch(recordIntent);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    private class GoRecordOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("CycleActivity", "View Clicked! Going to RecordActivity...");
            Intent recordIntent = new Intent(RoutineActivity.this, RecordActivity.class);

            // Output to RecordActivity:
            // "Email", String : Email Address of the user.
            // "IPPTCycleId", String : Id of the IPPTCycle
            // "IPPTRoutineId", String : Id of the IPPTCycle
            // "IPPTRoutine", byteArray : Serialized IPPTRoutine Object
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // get data for all the routines in the cycle
            db.collection("IPPTUser")
                    .document(EmailAddress)
                    .collection("IPPTCycle")
                    .document(IPPTCycleId)
                    .collection("IPPTRoutine")
                    .whereEqualTo("DateCreated", currentIpptRoutine.DateCreated)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult().iterator().next();
                            recordIntent.putExtra("Email", EmailAddress);
                            recordIntent.putExtra("IPPTCycleId", IPPTCycleId);
                            recordIntent.putExtra("IPPTRoutineId", documentSnapshot.getId());

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            try {
                                ObjectOutputStream oos = new ObjectOutputStream(bos);
                                oos.writeObject(currentIpptRoutine);
                                recordIntent.putExtra("IPPTRoutine", bos.toByteArray());
                            } catch (IOException e) {
                                // If error occurred, display friendly message to user

                                Toast.makeText(RoutineActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                return;
                            }
                            if (null != GoRoutine) {
                                GoRoutine.launch(recordIntent);
                            }
                        }
                    });
        }
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
        outState.putString("Email", EmailAddress);
        outState.putString("IPPTCycleId", IPPTCycleId);
        outState.putByteArray("IPPTCycle", SerializedIPPTCycle);
        // make sure to call super after writing code ...
        super.onSaveInstanceState(outState);
    }

    /* check if theres any current ippt routine, then
        check if the current ippt routine is finished
    */
    @Override
    protected void onResume() {
        Log.d("RoutineActivity", "onResume called!");
        if (null != currentIpptRoutine &&
            currentIpptRoutine.isFinished) {
            findViewById(R.id.completecreateroutineButton).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.routinedateCreatedText)).setText("");
            setCreateRoutineButton();
            ipptRoutineList.add(currentIpptRoutine);
            ipptRoutineAdapter.notifyItemChanged(ipptRoutineList.size() - 1);
        }
        super.onResume();
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