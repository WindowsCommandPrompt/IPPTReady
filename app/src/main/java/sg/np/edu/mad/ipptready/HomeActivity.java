package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        // "userId", String : userId of User document

        // Note:        Make sure to save the Input data using the onSaveInstanceState(android.os.Bundle),
        //                  so that we don't need to retrieve the data again!
        //
        // Extra Note: Check saveInstanceState if Intent is empty!

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

        if (null != getIntent()) {
            Intent intent = getIntent();
            // Java is not a very typesafe language!

            EmailAddress = intent.getStringExtra("Email");
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
            ft.replace(R.id.fragmentMenu, new NavFragment());
            ft.commit();

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