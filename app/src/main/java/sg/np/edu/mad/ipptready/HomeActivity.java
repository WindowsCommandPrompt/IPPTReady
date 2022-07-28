package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;

public class HomeActivity extends AppCompatActivity {
    public String EmailAddress;
    public IPPTUser user;

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
            user = (IPPTUser)intent.getSerializableExtra("User");
        }
        else if (null != savedInstanceState) {
            EmailAddress = savedInstanceState.getString("Email");
            user = (IPPTUser)savedInstanceState.getSerializable("User");
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

            View.OnClickListener profileActivityOCL = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ProfileIntent = new Intent(HomeActivity.this, ProfileActivity.class);

                    ProfileIntent.putExtra("Email", EmailAddress);
                    ProfileIntent.putExtra("User", user);
                    startActivity(ProfileIntent);
                }};
            findViewById(R.id.cardHomeWelcome).setOnClickListener(profileActivityOCL);
            findViewById(R.id.profileButton).setOnClickListener(profileActivityOCL);

            // set name of user in home activity
            TextView name = findViewById(R.id.nameTextHome);
            name.setText(user.Name);
        }
        else {
            // somehow data missing from the intent or the saveInstanceState
            GenericErrorToast.show();
            finish();
        }

        Button logoutBtn = findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                alert
                        .setTitle("Log out")
                        .setMessage("Are you sure you want to log out?")
                        .setCancelable(true)
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        signOut();
                                    }
                                })
                        .setNegativeButton("No", null);
                alert.create().show();
            }
        });
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        Toast.makeText(HomeActivity.this, "You have been logged out", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
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