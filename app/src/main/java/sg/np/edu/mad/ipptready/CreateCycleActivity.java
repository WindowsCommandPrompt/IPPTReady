package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateCycleActivity extends AppCompatActivity {
    private String EmailAddress;
    private byte[] SerializedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cycle);

        // Input from CycleActivity:
        // "Email", String : Email Address of the user.
        // "User", byteArray : To serialize back to User Object, contains
        //      info about the user.

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
                // show generic error message to user
                GenericErrorToast.show();
                finish();
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
            TextView dateCreatedView = findViewById(R.id.createnewcycleDateCreated);
            Date currentDate = new Date();

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateCreatedView.setText(dateFormat.format(currentDate));

            User finalUser = user;
            findViewById(R.id.createcyclecreatenewcycleButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String IPPTCycleName = ((TextView)findViewById(R.id.createnewcycleName)).getText().toString();
                    IPPTCycle ipptCycle = new IPPTCycle(IPPTCycleName, currentDate);
                    finalUser.addNewIPPTCycleToDatabase(EmailAddress,
                                                        ipptCycle,
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(CreateCycleActivity.this, IPPTCycleName + "Created!",
                                                                        Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            }
                                                        });
                }
            });
        }
    }
}