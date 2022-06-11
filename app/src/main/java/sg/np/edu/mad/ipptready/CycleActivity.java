package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
                                    // do RecycleView initialization here
                                    recyclerView = findViewById(R.id.cycleRecyclerView);
                                    List<IPPTCycle> IPPTCycles = task.getResult().toObjects(IPPTCycle.class);
                                    IPPTCycleAdapter adapter = new IPPTCycleAdapter(IPPTCycles);

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
    protected  void onSaveInstanceState(@NonNull Bundle outState) {
        // write code here!
        outState.putString("Email", EmailAddress);
        outState.putByteArray("User", SerializedUser);
        // make sure to call super after writing code ...
        super.onSaveInstanceState(outState);
    }
}