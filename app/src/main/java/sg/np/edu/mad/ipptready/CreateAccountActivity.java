package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;

public class CreateAccountActivity extends AppCompatActivity {
    private String EmailAddress;
    private String Name;
    final Calendar myCalendar= Calendar.getInstance();
    EditText dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

        // Input from LoginActivity:
        // "Email" : Email Address of the user
        // "Name" : Name of the person derived from Google Account Login,
        //      to be used as default name of the user

        // Output to HomeActivity:
        // "Email" : Email Address of the user
        // "User" : Serializable Object of user

        Intent intent = getIntent();
        EmailAddress = intent.getStringExtra("Email");
        Name = intent.getStringExtra("Name");

        TextView emailAddress = findViewById(R.id.emailCreate);
        emailAddress.setText(EmailAddress);

        EditText name = findViewById(R.id.nameCreate);
        name.setText(Name, TextView.BufferType.EDITABLE);

        // Date picker
        dob = findViewById(R.id.dobCreate);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        // create datepickerdialog
        DatePickerDialog datePickerDialog= new DatePickerDialog(CreateAccountActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        // Create Account button
        Button createAccountBtn = findViewById(R.id.createAccountBtn);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // error checking for user details
                if((dob.getText().toString()).equalsIgnoreCase(""))
                {
                    dob.setHint("Please select date");
                    dob.setError("Please enter Date of Birth!");
                    return;
                }

                if((name.getText().toString()).equalsIgnoreCase(""))
                {
                    name.setHint("Please enter Name");
                    name.setError("Please enter a name!");
                    return;
                }
                Name = name.getText().toString();
                Date DOB = null;

                try {
                    DOB = new SimpleDateFormat("dd/MM/yyyy").parse(dob.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                FirebaseDocChange firebaseDocChange = IPPTUser.createNewUser(EmailAddress, Name, DOB);
                firebaseDocChange.changeTask
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CreateAccountActivity.this, "Directing to login page", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else {
                                    Toast.makeText(CreateAccountActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
            }
        });
    }
    // get the date and set the dob text on display
    private void updateLabel(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(dateFormat.format(myCalendar.getTime()));
    }
    // add the user to firebase method
}