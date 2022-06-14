package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CycleActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String EmailAddress;
    private byte[] SerializedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle);

        // Input from Home Activity:
        // "Email", String : Email Address of the user.
        // "User", byteArray : To serialize back to User Object, contains
        //      info about the user.

        // Output to CreateCycleActivity:
        // "Email", String : Email Address of the user.
        // "User", byteArray : To serialize back to User Object, contains
        //      info about the user.

        // Output to RoutineActivity:
        // "Email", String : Email Address of the user.
        // "IPPTCycleId", String : Id of the IPPTCycle
        // "IPPTCycle", byteArray : Serialized IPPTCycle Object

        // Note:        Make sure to save the Input data using the onSaveInstanceState(android.os.Bundle),
        //                  so that we don't need to retrieve the data again!
        //
        // Extra Note: Check saveInstanceState if Intent is empty!

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);
        Intent intent = getIntent();
        EmailAddress = intent.getStringExtra("Email");
        SerializedUser = intent.getByteArrayExtra("User");

        if (null == EmailAddress ||
            null == SerializedUser) {
            EmailAddress = savedInstanceState.getString("Email");
            SerializedUser = savedInstanceState.getByteArray("User");
            if (null == EmailAddress ||
                    null == SerializedUser) {
                // show generic error message if  all else fails ...
                GenericErrorToast.show();
                finish();
            }
        }
        User user = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(SerializedUser);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            // casting will work 100%! Clueless
            user = (User)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // show generic error message ...
            GenericErrorToast.show();
            e.printStackTrace();
        }
        Log.d("User", user.Name);
        Log.d("User", user.DOB.toString());
        // if got user and EmailAddress, go!
        user.getCyclesList(EmailAddress,
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // do RecycleView and current Cycle initialization here
                                List<DocumentSnapshot> docSnapshots = task.getResult().getDocuments();
                                List<IPPTCycle> ipptCycleList = task.getResult().toObjects(IPPTCycle.class);
                                IPPTCycle ipptCycle = null;
                                String IPPTCycleId = null;

                                for (IPPTCycle ipptCycleItem : ipptCycleList) {
                                    if (!ipptCycleItem.isFinished) {
                                        Log.d("CycleActivity", "Not finished Cycle Detected!");
                                        ipptCycle = ipptCycleItem;
                                        IPPTCycleId = docSnapshots.get(ipptCycleList.indexOf(ipptCycle))
                                                .getId();
                                        ipptCycleList.remove(ipptCycleItem);

                                        ((TextView)findViewById(R.id.cyclenameText)).setText(ipptCycle.Name);
                                        ((TextView)findViewById(R.id.cycledateCreatedText)).setText(ipptCycle.DateCreated.toString());

                                        IPPTCycle finalIpptCycle = ipptCycle;
                                        String finalIPPTCycleId = IPPTCycleId;
                                        findViewById(R.id.constraintLayout2).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Log.d("CycleActivity", "View Clicked! Going to RoutineActivity...");
                                                Intent routineIntent = new Intent(CycleActivity.this, RoutineActivity.class);

                                                // "Email", String : Email Address of the user.
                                                // "IPPTCycleId", String : Id of the IPPTCycle
                                                // "IPPTCycle", byteArray : Serialized IPPTCycle Object
                                                routineIntent.putExtra("Email", EmailAddress);
                                                routineIntent.putExtra("IPPTCycleId", finalIPPTCycleId);

                                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                                try {
                                                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                                                    oos.writeObject(finalIpptCycle);
                                                    routineIntent.putExtra("IPPTCycle", bos.toByteArray());
                                                } catch (IOException e) {
                                                    // If error occurred, display friendly message to user

                                                    Toast.makeText(CycleActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                                                    e.printStackTrace();
                                                    return;
                                                }

                                                startActivity(routineIntent);
                                            }
                                        });
                                        findViewById(R.id.completecycleButton).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Log.d("CycleActivity", "Completing Cycle...");
                                                finalIpptCycle.completeIPPTCycle(EmailAddress,
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.d("CycleActivity", "Completed Cycle, changing UI...");

                                                                findViewById(R.id.constraintLayout2).setVisibility(View.INVISIBLE);

                                                                findViewById(R.id.createcycleButton).setOnClickListener(new CreateCycleOnClickListener());
                                                                findViewById(R.id.createcycleButton).setVisibility(View.VISIBLE);
                                                            }
                                                        });
                                            }
                                        });

                                        // Make entire sub-view visible
                                        findViewById(R.id.constraintLayout2).setVisibility(View.VISIBLE);
                                        break;
                                    }
                                }

                                if (!ipptCycleList.isEmpty()) {
                                    recyclerView = findViewById(R.id.cycleRecyclerView);
                                    IPPTCycleAdapter adapter = new IPPTCycleAdapter(ipptCycleList, CycleActivity.this,
                                            EmailAddress);

                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(adapter);
                                }

                                if (null == ipptCycle) {
                                    Log.d("CycleActivity", "No Active Cycles Found!");
                                    findViewById(R.id.createcycleButton).setOnClickListener(new CreateCycleOnClickListener());
                                    findViewById(R.id.createcycleButton).setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                Log.d("CycleActivity", "Collection is empty!");
                                findViewById(R.id.createcycleButton).setVisibility(View.VISIBLE);
                                findViewById(R.id.createcycleButton).setOnClickListener(new CreateCycleOnClickListener());
                            }
                        }
                        else {
                            // If data retrieval fails go back to the previous activity
                            GenericErrorToast.show();
                            finish();
                        }
                    }
                });
    }

    private class CreateCycleOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("CycleActivity", "Creating new Cycle, going to CreateCycleActivity...");
            Intent createCycleIntent = new Intent(CycleActivity.this, CreateCycleActivity.class);
            // pack data and send it to CreateCycleActivity...
            createCycleIntent.putExtra("Email", EmailAddress);
            createCycleIntent.putExtra("User", SerializedUser);

            startActivity(createCycleIntent);
        }
    }

    // for debugging and testing purposes
    private ArrayList<IPPTCycle> generateTestIPPTCycleList() {
        ArrayList<IPPTCycle> ipptCycleList = new ArrayList<IPPTCycle>();
        for (int i=0; i<10; i++) {
            ipptCycleList.add(new IPPTCycle("Testing...", new Date()));
        }
        return ipptCycleList;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // write code here!
        outState.putString("Email", EmailAddress);
        outState.putByteArray("User", SerializedUser);
        // make sure to call super after writing code ...
        super.onSaveInstanceState(outState);
    }
}