package sg.np.edu.mad.ipptready;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
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

public class CycleActivity extends AppCompatActivity {
    private String EmailAddress;
    private byte[] SerializedUser;

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
            null == SerializedUser &&
            null != savedInstanceState) {
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
                            setContentView(R.layout.activity_cycle);
                            recyclerView = findViewById(R.id.cycleRecyclerView);
                            if (!task.getResult().isEmpty()) {
                                // do RecycleView and current Cycle initialization here
                                List<DocumentSnapshot> docSnapshots = task.getResult().getDocuments();
                                ipptCycleList = task.getResult().toObjects(IPPTCycle.class);
                                String IPPTCycleId = null;

                                for (IPPTCycle ipptCycleItem : ipptCycleList) {
                                    if (!ipptCycleItem.isFinished) {
                                        Log.d("CycleActivity", "Not finished Cycle Detected!");
                                        currentIpptCycle = ipptCycleItem;
                                        IPPTCycleId = docSnapshots.get(ipptCycleList.indexOf(currentIpptCycle))
                                                .getId();
                                        ipptCycleList.remove(ipptCycleItem);

                                        ((TextView)findViewById(R.id.cyclenameText)).setText(currentIpptCycle.Name);
                                        ((TextView)findViewById(R.id.cycledateCreatedText)).setText(currentIpptCycle.DateCreated.toString());
                                        ((Button)findViewById(R.id.completecreatecycleButton)).setText("Complete Cycle");

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
                                        });
                                        break;
                                    }
                                }
                                if (null == currentIpptCycle) {
                                    Log.d("CycleActivity", "No Active Cycles Found!");
                                    setCreateCycleButton();
                                }
                                else {
                                    // do Recycle Initialization if there is no current IPPT Cycles
                                    ((Button)findViewById(R.id.completecreatecycleButton)).setText("Complete Cycle");
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
        User finalUser = user;
        getCreateCycle = registerForActivityResult(new CycleActivityResultContract(),
                new ActivityResultCallback<IPPTCycle>() {
                    @Override
                    public void onActivityResult(IPPTCycle result) {
                        if (null != result) {
                            finalUser.addNewIPPTCycleToDatabase(EmailAddress,
                                    result,
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            currentIpptCycle = result;
                                            ((TextView)findViewById(R.id.cyclenameText)).setText(currentIpptCycle.Name);
                                            ((TextView)findViewById(R.id.cycledateCreatedText)).setText(dateFormat.format(currentIpptCycle.DateCreated));
                                        }
                                    });
                        }
                        else {
                            setCreateCycleButton();
                        }
                    }
                });
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
            ((TextView)findViewById(R.id.cyclenameText)).setText("");
            ((TextView)findViewById(R.id.cycledateCreatedText)).setText("");
            Log.d("CycleActivity", "Completing Cycle...");
            setCreateCycleButton();
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
                                        }
                                    });
                        }
                    });
        }
    }

    private void setCreateCycleButton() {
        ((Button)findViewById(R.id.completecreatecycleButton)).setText("Create A New Cycle");
        findViewById(R.id.completecreatecycleButton).setOnClickListener(new CreateCycleOnClickListener());
    }

    private void setCompleteCycleButton() {
        ((Button)findViewById(R.id.completecreatecycleButton)).setText("Complete Cycle");
        findViewById(R.id.completecreatecycleButton).setOnClickListener(new CompleteCycleOnClickListener());
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
        outState.putByteArray("User", SerializedUser);
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