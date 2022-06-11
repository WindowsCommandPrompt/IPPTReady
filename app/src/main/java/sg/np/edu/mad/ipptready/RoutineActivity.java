package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RoutineActivity extends AppCompatActivity {
    private String Email,
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


    }

    @Override
    protected  void onSaveInstanceState(@NonNull Bundle outState) {
        // write code here!

        // make sure to call super after writing code ...
        super.onSaveInstanceState(outState);
    }
}