package sg.np.edu.mad.ipptready;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;

public class CycleActivity extends AppCompatActivity {
    private String EmailAddress;
    //private byte[] SerializedUser;
    private User user;
    private DocumentReference userDocRef;

    // Objects loaded in the activities
    private RecyclerView recyclerView;
    private List<IPPTCycle> ipptCycleList;
    private IPPTCycle currentIpptCycle;
    private IPPTCycleAdapter ipptCycleAdapter;
    ActivityResultLauncher<Date> getCreateCycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set View temporary to a loading screen
        setContentView(R.layout.activity_load_data);
        // Input from Home Activity:
        // "Email", String : Email Address of the user.
        // "User", Serializable : To serialize back to User Object, contains
        //      info about the user.

        // Output to CreateCycleActivity:
        // "Email", String : Email Address of the user.
        // "User", Serializable : To serialize back to User Object, contains
        //      info about the user.

        // Output to RoutineActivity:
        // "Email", String : Email Address of the user.
        // "IPPTCycleId", String : Id of the IPPTCycle
        // "IPPTCycle", Serializable : Serialized IPPTCycle Object

        // Note:        Make sure to save the Input data using the onSaveInstanceState(android.os.Bundle),
        //                  so that we don't need to retrieve the data again!
        //
        // Extra Note: Check saveInstanceState if Intent is empty!

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

        if (null != getIntent()) {
            Intent intent = getIntent();
            // still not a typesafe language!

            EmailAddress = intent.getStringExtra("Email");
            userDocRef = IPPTUser.getUserDocFromId(EmailAddress);
            user = (User)intent.getSerializableExtra("User");
        }
        else if (null != savedInstanceState) {
            EmailAddress = savedInstanceState.getString("Email");
            user = (User)savedInstanceState.getSerializable("User");
        }
        else {
            // if all else fails...
            GenericErrorToast.show();
            finish();
        }

        // if got user and EmailAddress, go!
        if (null != EmailAddress &&
            null != user) {

            user.getCyclesList(EmailAddress,
                    new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                setContentView(R.layout.activity_cycle);
                                recyclerView = findViewById(R.id.cycleRecyclerView);
                                if (!task.getResult().isEmpty()) {
                                    // do RecycleView and current Cycle initialization here
                                    ipptCycleList = task.getResult().toObjects(IPPTCycle.class);

                                    for (IPPTCycle ipptCycleItem : ipptCycleList) {
                                        if (!ipptCycleItem.isFinished) {
                                            Log.d("CycleActivity", "Not finished Cycle Detected!");
                                            currentIpptCycle = ipptCycleItem;
                                            ipptCycleList.remove(ipptCycleItem);
                                            break;
                                        }
                                    }
                                    if (null == currentIpptCycle) {
                                        Log.d("CycleActivity", "No Active Cycles Found!");
                                        setCreateCycleButton();
                                    }
                                    else {
                                        DateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        ((TextView)findViewById(R.id.cyclenameText)).setText(currentIpptCycle.Name);
                                        ((TextView)findViewById(R.id.cycledateCreatedText)).setText(dateFormat.format(currentIpptCycle.DateCreated));
                                        setCompleteCycleButton();
                                    }
                                    ipptCycleAdapter = new IPPTCycleAdapter(ipptCycleList, CycleActivity.this,
                                            EmailAddress);

                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(ipptCycleAdapter);
                                }
                                else {
                                    ipptCycleList = new ArrayList<IPPTCycle>();
                                    ipptCycleAdapter = new IPPTCycleAdapter(new ArrayList<IPPTCycle>(), CycleActivity.this,
                                            EmailAddress);

                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(ipptCycleAdapter);
                                    Log.d("CycleActivity", "Collection is empty!");
                                    setCreateCycleButton();
                                }
                            }
                            else {
                                // If data retrieval fails go back to the previous activity
                                GenericErrorToast.show();
                                finish();
                            }
                        }
                    });
            getCreateCycle = registerForActivityResult(new CycleActivityResultContract(),
                    new ActivityResultCallback<IPPTCycle>() {
                        @Override
                        public void onActivityResult(IPPTCycle result) {
                            if (null != result) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("IPPTUser")
                                        .document(EmailAddress)
                                        .collection("IPPTCycle")
                                        .whereEqualTo("Name", result.Name)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (task.getResult().isEmpty()) {
                                                        // Makes sure no name is referred here
                                                        currentIpptCycle = result;
                                                        user.addNewIPPTCycleToDatabase(EmailAddress,
                                                                result,
                                                                new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                                        ((TextView)findViewById(R.id.cyclenameText)).setText(currentIpptCycle.Name);
                                                                        ((TextView)findViewById(R.id.cycledateCreatedText)).setText(dateFormat.format(currentIpptCycle.DateCreated));
                                                                        Toast.makeText(CycleActivity.this,
                                                                                result.Name + " created!",
                                                                                Toast.LENGTH_SHORT)
                                                                                .show();
                                                                    }
                                                                });
                                                        findViewById(R.id.constraintLayout2).setOnClickListener(new GoRoutineOnClickListener());
                                                    }
                                                    else {
                                                        Toast.makeText(CycleActivity.this,
                                                                "Cycle with name " + result.Name + " already exists!",
                                                                Toast.LENGTH_SHORT)
                                                                .show();
                                                        recreate();
                                                    }
                                                }
                                            }
                                        });
                            }
                            else {
                                setCreateCycleButton();
                            }
                        }
                    });
        }
        else {
            // missing data from intent or saveInstanceState
            GenericErrorToast.show();
            finish();
        }
    }

    private class CreateCycleOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            setCompleteCycleButton();

            // Go to CreateCycleActivity...
            getCreateCycle.launch(new Date());
        }
    }

    private class CompleteCycleOnClickListener implements  View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("CycleActivity", "Completing Cycle...");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("IPPTUser")
                    .document(EmailAddress)
                    .collection("IPPTCycle")
                    .whereEqualTo("Name", currentIpptCycle.Name)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            db.collection("IPPTUser")
                                    .document(EmailAddress)
                                    .collection("IPPTCycle")
                                    .document(task.getResult().iterator().next().getId())
                                    .collection("IPPTRoutine")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().isEmpty()) {
                                                    Toast.makeText(CycleActivity.this, "Please create routines", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        if (!((boolean) document.get("isFinished"))) {
                                                            Toast.makeText(CycleActivity.this, "Complete current routines first!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    }

                                                    currentIpptCycle.completeIPPTCycle(EmailAddress,
                                                            new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    currentIpptCycle.completeIPPTCycle(EmailAddress,
                                                                            new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    ipptCycleList.add(currentIpptCycle);
                                                                                    ipptCycleAdapter.notifyItemInserted(ipptCycleList.size() - 1);
                                                                                    currentIpptCycle = null;
                                                                                    ((TextView)findViewById(R.id.cyclenameText)).setText("");
                                                                                    ((TextView)findViewById(R.id.cycledateCreatedText)).setText("");
                                                                                    setCreateCycleButton();
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });
                        }
                    });
        }
    }

    private class GoRoutineOnClickListener implements  View.OnClickListener {

            @Override
            public void onClick(View v) {
                Log.d("CycleActivity", "View Clicked! Going to RoutineActivity...");
                Intent routineIntent = new Intent(CycleActivity.this, RoutineActivity.class);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("IPPTUser")
                        .document(EmailAddress)
                        .collection("IPPTCycle")
                        .whereEqualTo("Name", currentIpptCycle.Name)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        // "Email", String : Email Address of the user.
                                        // "IPPTCycleId", String : Id of the IPPTCycle
                                        // "IPPTCycle", byteArray : Serialized IPPTCycle Object
                                        routineIntent.putExtra("Email", EmailAddress);
                                        routineIntent.putExtra("IPPTCycleId", task.getResult()
                                                .getDocuments().iterator().next().getId());

                                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                        try {
                                            ObjectOutputStream oos = new ObjectOutputStream(bos);
                                            oos.writeObject(currentIpptCycle);
                                            routineIntent.putExtra("IPPTCycle", bos.toByteArray());
                                        } catch (IOException e) {
                                            // If error occurred, display friendly message to user

                                            Toast.makeText(CycleActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                            return;
                                        }
                                        startActivity(routineIntent);
                                    }
                                }
                            }
                        });
            }
    }

    private void setCreateCycleButton() {
        ((Button)findViewById(R.id.completecreatecycleButton)).setText("Create A New Cycle");
        findViewById(R.id.completecreatecycleButton).setOnClickListener(new CreateCycleOnClickListener());
        findViewById(R.id.constraintLayout2).setOnClickListener(null);
    }

    private void setCompleteCycleButton() {
        ((Button)findViewById(R.id.completecreatecycleButton)).setText("Complete Cycle");
        findViewById(R.id.completecreatecycleButton).setOnClickListener(new CompleteCycleOnClickListener());
        findViewById(R.id.constraintLayout2).setOnClickListener(new GoRoutineOnClickListener());
    }

    private class CycleActivityResultContract extends ActivityResultContract<Date, IPPTCycle> {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, @NonNull Date dateCreated) {
            Intent intent = new Intent(CycleActivity.this, CreateCycleActivity.class);

            // add Intents for CreateCycleActivity
            intent.putExtra("DateCreated", dateCreated);
            return intent;
        }

        @Override
        public IPPTCycle parseResult(int resultCode, @Nullable Intent result) {
            if (Activity.RESULT_OK != resultCode || null == result) {
                return null;
            }
            return (IPPTCycle)result.getSerializableExtra("IPPTCycle");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // write code here!
        outState.putString("Email", EmailAddress);
        outState.putSerializable("User", user);
        // make sure to call super after writing code ...
        super.onSaveInstanceState(outState);
    }

    @Override
    protected  void onDestroy() {
        if (null != getCreateCycle) {
            getCreateCycle.unregister();
        }
        super.onDestroy();
    }
}