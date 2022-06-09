package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RoutineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        // Input to RoutineActivity:
        // "Email Address" : Email Address of the user.
        // "IPPTName" : Name of the IPPT Cycle
        // "User" : serialized form of User Object
        //
        // Output to RecordActivity:
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