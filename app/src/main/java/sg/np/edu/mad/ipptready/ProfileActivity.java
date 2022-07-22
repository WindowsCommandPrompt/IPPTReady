package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;

public class ProfileActivity extends AppCompatActivity {
    private String EmailAddress;
    private IPPTUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_profile);

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

        // get user information
        if (null != getIntent()) {
            Intent intent = getIntent();
            EmailAddress = intent.getStringExtra("Email");
            // Java is not a typesafe language!
            user = (IPPTUser) intent.getSerializableExtra("User");
        }
        else if (null != savedInstanceState) {
            EmailAddress = savedInstanceState.getString("Email");
            user = (IPPTUser) savedInstanceState.getSerializable("User");
        }
        else {
            // if all else fails...
            GenericErrorToast.show();
            finish();
        }


        // Get TextViews from ProfileActivity layout
        if (null != EmailAddress &&
            null != user) {
            TextView name = findViewById(R.id.name);
            TextView email = findViewById(R.id.email);
            TextView dob = findViewById(R.id.dateOfBirth);

            SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MMM/yyyy");
            String dateOfBirth = dateFormat.format(user.DoB);

            // set name, email and dob on profile activity screen
            name.setText(user.Name);
            email.setText(EmailAddress);
            dob.setText(dateOfBirth);

            //========================================================================================================//
            //ADDED THE BELOW PART ON 15th June 2022 3:22AM
            //SEND EMAILADDRESS DATA OVER TO RunActivity.java
            Intent Runintent = new Intent();
            Runintent.setClassName("sg.np.edu.mad.ipptready.ProfileActivity.this", "sg.np.edu.mad.ipptready.RunActivity.class");
            Runintent.putExtra("EmailAddressVerifier", email.getText().toString());
            //=======================================================================================================//
            // From another member: ?????
        }
        else {
            // missing data in intent or saveInstanceState
            GenericErrorToast.show();
            finish();
        }
    }

    @Override
    protected  void onSaveInstanceState(@NonNull Bundle outState) {
        // write code here!
        outState.putString("Email", EmailAddress);
        outState.putSerializable("User", user);
        // make sure to call super after writing code ...
        super.onSaveInstanceState(outState);
    }
}