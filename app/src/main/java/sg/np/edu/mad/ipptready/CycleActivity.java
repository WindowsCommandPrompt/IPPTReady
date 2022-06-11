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
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CycleActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String EmailAddress;
    private byte[] SerializedUser;

    private final int CREATE_CYCLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle);
        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

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
        // "IPPTCycle", byteArray : Serialized IPPTCycle Object
        // "IPPTCycleId", String : Id of the IPPTCycle
        // "IPPTCycleId", String : Id of the IPPTCycle

        // Note:        Make sure to save the Input data using the onSaveInstanceState(android.os.Bundle),
        //                  so that we don't need to retrieve the data again!
        //
        // Extra Note: Check saveInstanceState if Intent is empty!


        Intent intent = getIntent();
        EmailAddress = intent.getStringExtra("Email");
        SerializedUser = intent.getByteArrayExtra("User");

        if (null == EmailAddress &&
            null == SerializedUser) {
            EmailAddress = savedInstanceState.getString("Email");
            SerializedUser = savedInstanceState.getByteArray("User");
            if (null == EmailAddress ||
                    null == SerializedUser) {
                // show generic error message if  all else fails ...
                GenericErrorToast.show();

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

        if (null != user &&
            null != EmailAddress) {
            // if got user and EmailAddress, go!
            user.getCyclesList(EmailAddress,
                    new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    // do RecycleView and current Cycle initialization here
                                    List<IPPTCycle> ipptCycles = task.getResult().toObjects(IPPTCycle.class);
                                    IPPTCycle ipptCycle = null;
                                    for (IPPTCycle ipptCycleItem : ipptCycles) {
                                        if (!ipptCycleItem.isFinished) {
                                            Log.d("CycleActivity", "Not finished Cycle Detected!");
                                            ipptCycle = ipptCycleItem;
                                            ipptCycles.remove(ipptCycleItem);

                                            ((TextView)findViewById(R.id.cyclenameText)).setText(ipptCycle.Name);
                                            ((TextView)findViewById(R.id.cycledateCreatedText)).setText(ipptCycle.DateCreated.toString());

                                            IPPTCycle finalIpptCycle = ipptCycle;
                                            findViewById(R.id.constraintLayout2).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Log.d("CycleActivity", "View Clicked! Going to RoutineActivity...");
                                                    Intent routineIntent = new Intent(CycleActivity.this, RoutineActivity.class);
                                                    // serialize Cycle Object and put extras into the Intent

                                                    startActivity(routineIntent);
                                                }
                                            });
                                            findViewById(R.id.completeButton).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Log.d("CycleActivity", "Completing Cycle...");
                                                    finalIpptCycle.completeIPPTCycle(EmailAddress,
                                                            new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Log.d("CycleActivity", "Completed Cycle, changing UI...");

                                                                    findViewById(R.id.constraintLayout2).setVisibility(View.INVISIBLE);

                                                                    findViewById(R.id.createCycleButton).setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {

                                                                        }
                                                                    });
                                                                    findViewById(R.id.createCycleButton).setVisibility(View.VISIBLE);
                                                                }
                                                            });
                                                }
                                            });
                                            findViewById(R.id.constraintLayout2).setVisibility(View.VISIBLE);
                                            break;
                                        }
                                    }
                                    if (null == ipptCycle) {
                                        Log.d("CycleActivity", "No Active Cycles Found!");
                                        findViewById(R.id.createCycleButton).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Log.d("CycleActivity", "Creating new Cycle, going to CreateCycleActivity...");
                                                Intent createCycleIntent = new Intent(CycleActivity.this, CreateCycleActivity.class);
                                                // pack data and send it to CreateCycleActivity...
                                                createCycleIntent.putExtra("Email", EmailAddress);
                                                createCycleIntent.putExtra("User", SerializedUser);

                                                startActivity(createCycleIntent);
                                            }
                                        });
                                        findViewById(R.id.createCycleButton).setVisibility(View.VISIBLE);
                                    }


                                    recyclerView = findViewById(R.id.cycleRecyclerView);
                                    IPPTCycleAdapter adapter = new IPPTCycleAdapter(ipptCycles);

                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                        }
                    });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CREATE_CYCLE == requestCode) {

        }
    }
}