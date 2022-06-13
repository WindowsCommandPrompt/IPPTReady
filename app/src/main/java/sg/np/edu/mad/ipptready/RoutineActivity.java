package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
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
        // "Email" : Email Address of the user.
        // "IPPTCycle" : Serialized IPPTCycle Object
        // "IPPTCycleId" : Id of the IPPTCycle
        // "IPPTRoutineId" : Id of the IPPTRoutine
        // List of Records in the form of key-value pairs:
        // RecordName -> Serialized Object of Record

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

        if (null != EmailAddress &&
            null != IPPTCycleId &&
            null != ipptCycle) {
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

                                            if (!ipptRoutineList.isEmpty()) {
                                                recyclerView = findViewById(R.id.routineRecyclerView);
                                                recyclerView.setVisibility(View.VISIBLE);
                                                IPPTRoutineAdapter adapter = new IPPTRoutineAdapter(ipptRoutineList);

                                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                                recyclerView.setLayoutManager(layoutManager);
                                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                                recyclerView.setAdapter(adapter);
                                            }

                                            ((TextView)findViewById(R.id.routineipptscoreText)).setText(String.valueOf(ipptRoutine.IPPTScore));
                                            ((TextView)findViewById(R.id.routinedateCreatedText)).setText(ipptRoutine.DateCreated.toString());

                                            break;
                                        }
                                    }
                                }
                                else {
                                    Log.d("RoutineActivity", "Routine Collection is empty!");

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
    }

    public class CreateCycleOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("CycleActivity", "Creating new Routine, going to RecordActivity");
            Intent RecordIntent = new Intent(RoutineActivity.this, RecordActivity.class);
            // pack data and send it to CreateCycleActivity...
            RecordIntent.putExtra("Email", EmailAddress);

            startActivity(RecordIntent);
        }
    }

    @Override
    protected  void onSaveInstanceState(@NonNull Bundle outState) {
        // write code here!
        outState.putString("Email", EmailAddress);
        outState.putString("IPPCycleId", IPPTCycleId);
        outState.putByteArray("IPPTCycle", SerializedIPPTCycle);
        // make sure to call super after writing code ...
        super.onSaveInstanceState(outState);
    }
}