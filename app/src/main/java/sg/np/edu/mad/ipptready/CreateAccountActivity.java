package sg.np.edu.mad.ipptready;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Input from LoginActivity:
        // "Email" : Email Address of the user
        // "Name" : Name of the person derived from Google Account Login,
        //      to be used as default name of the user

        // Output to HomeActivity:
        // "Email" : Email Address of the user
        // "User" : Serialized using ByteArrayOutputStream and ObjectOutputStream class,
        //      see Line 109 of Login Activity for sample.
    }
}