package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Input to :
        // "Email" : Email Address of the user
        // "User" : string Object to serialize back to User Object, contains
        //      info about the user

        // Output to CycleActivity:
        // "Email" : Email Address of the user
        // "User" : serialized form of User Object

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