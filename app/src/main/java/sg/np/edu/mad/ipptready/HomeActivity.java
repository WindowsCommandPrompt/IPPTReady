package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class HomeActivity extends AppCompatActivity {
    public String EmailAddress;
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Input from LoginActivity:
        // "Email", String : Email Address of the user
        // "User", Serializable : string Object to serialize back to User Object, contains
        //      info about the user

        // Output to CycleActivity:
        // "Email", String : Email Address of the user
        // "User", Serializable : serialized form of User Object

        // Note:        Make sure to save the Input data using the onSaveInstanceState(android.os.Bundle),
        //                  so that we don't need to retrieve the data again!
        //
        // Extra Note: Check saveInstanceState if Intent is empty!

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

        if (null != getIntent()) {
            Intent intent = getIntent();
            EmailAddress = intent.getStringExtra("Email");
            // Java is not a very typesafe language!
            user = (User)intent.getSerializableExtra("User");
        }
        else if (null != savedInstanceState) {
            EmailAddress = savedInstanceState.getString("Email");
            user = (User)savedInstanceState.getSerializable("User");
        }
        else {
            // If all else fails..
            GenericErrorToast.show();
            finish();
        }

        if (null != EmailAddress &&
            null != user) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentWeather, new WeatherFragment());
            ft.replace(R.id.fragmentMenu, new navFragment());
            ft.commit();

            // Onclicklistener for Cycle feature
            /*findViewById(R.id.cycleButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent CycleIntent = new Intent(HomeActivity.this, CycleActivity.class);

                    CycleIntent.putExtra("Email", EmailAddress);
                    CycleIntent.putExtra("User", user);
                    startActivity(CycleIntent);
                }
            });

            // Onclicklistener for info feature
            findViewById(R.id.infoButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent InformationIntent = new Intent(HomeActivity.this, InformationActivity.class);
                    startActivity(InformationIntent);
                }
            });

            // Onclicklistener for video feature
            findViewById(R.id.videoButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent VideoIntent = new Intent(HomeActivity.this, VideoActivity.class);
                    startActivity(VideoIntent);
                }
            });

            */


            // Onclicklistener for profile feature
            findViewById(R.id.profileButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ProfileIntent = new Intent(HomeActivity.this, ProfileActivity.class);

                    ProfileIntent.putExtra("Email", EmailAddress);
                    ProfileIntent.putExtra("User", user);
                    startActivity(ProfileIntent);
                }
            });

            // set name of user in home activity
            TextView name = findViewById(R.id.nameTextHome);
            name.setText(user.Name);
        }
        else {
            // somehow data missing from the intent or the saveInstanceState
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