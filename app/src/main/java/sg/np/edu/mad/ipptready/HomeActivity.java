package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class HomeActivity extends AppCompatActivity {
    private String EmailAddress;
    private byte[] SerializedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Input from LoginActivity:
        // "Email", String : Email Address of the user
        // "User", byteArray : string Object to serialize back to User Object, contains
        //      info about the user

        // Output to CycleActivity:
        // "Email", String : Email Address of the user
        // "User", byteArray : serialized form of User Object

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
            finish();
        }

        if (null != EmailAddress &&
            null != user) {
            findViewById(R.id.cycleButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent CycleIntent = new Intent(HomeActivity.this, CycleActivity.class);

                    CycleIntent.putExtra("Email", EmailAddress);
                    CycleIntent.putExtra("User", SerializedUser);
                    startActivity(CycleIntent);
                }
            });
            findViewById(R.id.profileButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ProfileIntent = new Intent(HomeActivity.this, ProfileActivity.class);

                    ProfileIntent.putExtra("Email", EmailAddress);
                    ProfileIntent.putExtra("User", SerializedUser);
                    startActivity(ProfileIntent);
                }
            });
            findViewById(R.id.videoButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent VideoIntent = new Intent(HomeActivity.this, VideoActivity.class);
                    startActivity(VideoIntent);
                }
            });
            findViewById(R.id.infoButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent InformationIntent = new Intent(HomeActivity.this, InformationActivity.class);
                    startActivity(InformationIntent);
                }
            });
        }

        TextView name = findViewById(R.id.nameTextHome);
        name.setText(user.Name);
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