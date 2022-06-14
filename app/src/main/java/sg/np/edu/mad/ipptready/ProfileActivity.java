package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private String EmailAddress;
    private byte[] SerializedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_profile);
        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

        Intent intent = getIntent();
        EmailAddress = intent.getStringExtra("Email");
        SerializedUser = intent.getByteArrayExtra("User");

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

        TextView name = findViewById(R.id.name);
        TextView email = findViewById(R.id.email);
        TextView dob = findViewById(R.id.dateOfBirth);

        name.setText(user.Name);
        email.setText(EmailAddress);
        dob.setText(user.DOB.toString());

        //========================================================================================================//
        //ADDED THE BELOW PART ON 15th June 2022 3:22AM
        //SEND EMAILADDRESS DATA OVER TO RunActivity.java
        Intent blackHole = new Intent();
        blackHole.setClassName("sg.np.edu.mad.ipptready.ProfileActivity.this", "sg.np.edu.mad.ipptready.RunActivity.class");
        blackHole.putExtra("EmailAddressVerifier", email.getText().toString());
        //=======================================================================================================//

    }

}