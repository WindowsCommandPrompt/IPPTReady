package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.Date;
import java.util.List;

public class RoutineActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String EmailAddress,
        IPPTCycleId;
    private byte[] SerializedIPPTCycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

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
        ipptCycle.getRoutineList(EmailAddress,
                IPPTCycleId,
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {

                                List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                                List<IPPTRoutine> ipptRoutineList = task.getResult().toObjects(IPPTRoutine.class);
                                IPPTRoutine ipptRoutine = null;

                                for (IPPTRoutine ipptRoutineItem : ipptRoutineList) {
                                    if (!ipptRoutineItem.isFinished) {
                                        Log.d("RoutineActivity", "Routine not Completed Detected!");
                                        ipptRoutine = ipptRoutineItem;
                                        String ipptRoutineId = documentSnapshots.get(ipptRoutineList.indexOf(ipptRoutine))
                                                .getId();
                                        ipptRoutineList.remove(ipptRoutine);

                                        ((TextView)findViewById(R.id.routineipptscoreText)).setText(String.valueOf(ipptRoutine.IPPTScore));
                                        ((TextView)findViewById(R.id.routinedateCreatedText)).setText(ipptRoutine.DateCreated.toString());

                                        IPPTRoutine finalIpptRoutine = ipptRoutine;
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
                                                    oos.writeObject(finalIpptRoutine);
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
                                        findViewById(R.id.completeroutineButton).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Log.d("CycleActivity", "Completing Cycle...");
                                                finalIpptRoutine.completeIPPTRoutine(EmailAddress,
                                                        IPPTCycleId,
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.d("CycleActivity", "Completed Cycle, changing UI...");

                                                                findViewById(R.id.constraintLayout2).setVisibility(View.INVISIBLE);

                                                                findViewById(R.id.createroutineButton).setOnClickListener(new RoutineActivity.GoRecordOnClickListener());
                                                                findViewById(R.id.createroutineButton).setVisibility(View.VISIBLE);
                                                            }
                                                        });
                                            }
                                        });

                                        // Make the entire sub-view visible
                                        findViewById(R.id.constraintLayout2).setVisibility(View.VISIBLE);
                                        break;
                                    }
                                }

                                if (!ipptRoutineList.isEmpty()) {
                                    recyclerView = findViewById(R.id.routineRecyclerView);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    IPPTRoutineAdapter adapter = new IPPTRoutineAdapter(ipptRoutineList, getApplicationContext());

                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(adapter);
                                }

                                if (null == ipptRoutine) {
                                    Log.d("CycleActivity", "No Active Routines Found!");
                                    findViewById(R.id.createroutineButton).setOnClickListener(new RoutineActivity.GoRecordOnClickListener());
                                    findViewById(R.id.createroutineButton).setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                Log.d("RoutineActivity", "Routine Collection is empty!");
                                findViewById(R.id.createroutineButton).setVisibility(View.VISIBLE);
                                findViewById(R.id.createroutineButton).setOnClickListener(new RoutineActivity.GoRecordOnClickListener());
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

    private class GoRecordOnClickListener implements View.OnClickListener {
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