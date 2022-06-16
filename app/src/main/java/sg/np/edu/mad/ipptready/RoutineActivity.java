package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoutineActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String EmailAddress,
        IPPTCycleId;
    private byte[] SerializedIPPTCycle;
    private List<IPPTRoutine> ipptRoutineList;
    private IPPTRoutine currentIpptRoutine;
    private IPPTRoutineAdapter ipptRoutineAdapter;

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
        IPPTCycle finalIpptCycle = ipptCycle;
        ipptCycle.getRoutineList(EmailAddress,
                IPPTCycleId,
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            setContentView(R.layout.activity_routine);
                            recyclerView = findViewById(R.id.routineRecyclerView);
                            if (finalIpptCycle.isFinished) {
                                findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
                                findViewById(R.id.previousroutinetext).setVisibility(View.GONE);
                            }
                            if (!task.getResult().isEmpty()) {
                                List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                                ipptRoutineList = task.getResult().toObjects(IPPTRoutine.class);
                                currentIpptRoutine = null;

                                for (IPPTRoutine ipptRoutineItem : ipptRoutineList) {
                                    if (!ipptRoutineItem.isFinished) {
                                        Log.d("RoutineActivity", "Routine not Completed Detected!");
                                        currentIpptRoutine = ipptRoutineItem;
                                        String ipptRoutineId = documentSnapshots.get(ipptRoutineList.indexOf(currentIpptRoutine))
                                                .getId();
                                        ipptRoutineList.remove(currentIpptRoutine);

                                        ((TextView)findViewById(R.id.routineipptscoreText)).setText(String.valueOf(currentIpptRoutine.IPPTScore));
                                        DateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        ((TextView)findViewById(R.id.routinedateCreatedText)).setText(dateFormat.format(currentIpptRoutine.DateCreated));
                                        ((Button)findViewById(R.id.completecreateroutineButton)).setText("Complete Routine");
                                        findViewById(R.id.constraintLayout2).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Log.d("CycleActivity", "View Clicked! Going to RecordActivity...");
                                                Intent routineIntent = new Intent(RoutineActivity.this, RecordActivity.class);

                                                // Output to RecordActivity:
                                                // "Email", String : Email Address of the user.
                                                // "IPPTCycleId", String : Id of the IPPTCycle
                                                // "IPPTRoutineId", String : Id of the IPPTCycle
                                                // "IPPTRoutine", byteArray : Serialized IPPTRoutine Object
                                                routineIntent.putExtra("Email", EmailAddress);
                                                routineIntent.putExtra("IPPTCycleId", IPPTCycleId);
                                                routineIntent.putExtra("IPPTRoutineId", ipptRoutineId);

                                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                                try {
                                                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                                                    oos.writeObject(currentIpptRoutine);
                                                    routineIntent.putExtra("IPPTRoutine", bos.toByteArray());
                                                } catch (IOException e) {
                                                    // If error occurred, display friendly message to user

                                                    Toast.makeText(RoutineActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                    return;
                                                }

                                                startActivity(routineIntent);
                                            }
                                        });
                                        findViewById(R.id.completecreateroutineButton).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Log.d("CycleActivity", "Completing Cycle...");
                                                currentIpptRoutine.completeIPPTRoutine(EmailAddress,
                                                        IPPTCycleId,
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.d("CycleActivity", "Completed Cycle, changing UI...");
                                                                setCreateRoutineButton();
                                                                currentIpptRoutine.completeIPPTRoutine(EmailAddress,
                                                                        IPPTCycleId,
                                                                        new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                                        break;
                                    }
                                }
                                if (null == currentIpptRoutine) {
                                    Log.d("CycleActivity", "No Active Routines Found!");
                                    setCreateRoutineButton();
                                }
                                else {
                                    setCompleteRoutineButton();
                                }
                                ipptRoutineAdapter = new IPPTRoutineAdapter(ipptRoutineList, getApplicationContext());

                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(ipptRoutineAdapter);
                            }
                            else {
                                Log.d("RoutineActivity", "Routine Collection is empty!");
                                ipptRoutineAdapter = new IPPTRoutineAdapter(new ArrayList<IPPTRoutine>(), getApplicationContext());

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
            IPPTRoutine ipptRoutine = new IPPTRoutine();
            ipptRoutine.DateCreated = new Date();
            ipptRoutine.IPPTScore = 0;
            ipptRoutine.isFinished = false;
            ipptCycle.addNewIPPTRoutineToDatabase(EmailAddress,
                                                ipptRoutine,
                                                new OnCompleteListener<Void>() {
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
                                                                                    setCompleteRoutineButton();
                                                                                    currentIpptRoutine = task.getResult().iterator().next().toObject(IPPTRoutine.class);
                                                                                    ((TextView)findViewById(R.id.routineipptscoreText))
                                                                                            .setText(String.valueOf(
                                                                                                    currentIpptRoutine.IPPTScore)
                                                                                            );
                                                                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                                                    ((TextView)findViewById(R.id.routinedateCreatedText)).setText(
                                                                                            dateFormat.format(currentIpptRoutine.DateCreated)
                                                                                    );

                                                                                    String IPPTRoutineId = task.getResult().iterator().next().getId();
                                                                                    recordIntent.putExtra("IPPTRoutineId", IPPTRoutineId);
                                                                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                                                                    try {
                                                                                        ObjectOutputStream oos = new ObjectOutputStream(bos);
                                                                                        oos.writeObject(ipptRoutine);
                                                                                        recordIntent.putExtra("IPPTRoutine", bos.toByteArray());
                                                                                    } catch (IOException e) {
                                                                                        // If error occurred, display friendly message to user

                                                                                        Toast.makeText(RoutineActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                                                                                        e.printStackTrace();
                                                                                        return;
                                                                                    }
                                                                                    startActivity(recordIntent);
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });

        }
    }

    private class CompleteRoutineOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            currentIpptRoutine.completeIPPTRoutine(EmailAddress,
                    IPPTCycleId,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ipptRoutineList.add(currentIpptRoutine);
                                ipptRoutineAdapter.notifyItemChanged(ipptRoutineList.size() - 1);

                                currentIpptRoutine = null;
                                setCreateRoutineButton();
                            }
                        }
                    });
        }
    }

    private void setCreateRoutineButton() {
        ((Button)findViewById(R.id.completecreateroutineButton)).setText("Create A New Routine");
        findViewById(R.id.completecreateroutineButton).setOnClickListener(new RoutineActivity.CreateRoutineOnClickListener());
    }

    private void setCompleteRoutineButton() {
        ((Button)findViewById(R.id.completecreateroutineButton)).setText("Complete Routine");
        findViewById(R.id.completecreateroutineButton).setOnClickListener(new RoutineActivity.CompleteRoutineOnClickListener());
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
}