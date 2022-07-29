package sg.np.edu.mad.ipptready;

import static sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser.colFrom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;

public class ProfileActivity extends AppCompatActivity {
    private String EmailAddress;
    private IPPTUser user;
    final Calendar myCalendar= Calendar.getInstance();

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

            // Get TextViews to display the user information
            TextView name = findViewById(R.id.name);
            TextView email = findViewById(R.id.email);
            TextView dob = findViewById(R.id.dateOfBirth);
            TextView mode = findViewById(R.id.userMode);

            // Get ImageViews to display profile picture and edit icon
            ImageView profilePicture =findViewById(R.id.profilePicture);
            ImageView editProfile = findViewById(R.id.editImage);

            // Get Buttons for deleting and updating user information
            Button deleteButton = findViewById(R.id.deleteButton);
            Button saveButton = findViewById(R.id.saveButton);
            Button cancelButton = findViewById(R.id.cancelButton);

            // Set Button colour
            deleteButton.setBackgroundColor(Color.RED);
            saveButton.setBackgroundColor(Color.GREEN);
            cancelButton.setBackgroundColor(Color.RED);

            // Get EditTexts to for user inputs
            EditText editName = findViewById(R.id.editTextName);
            EditText editDob = findViewById(R.id.editTextDob);

            // Set EditTexts with user information as default
            editName.setText(user.Name);
            // formatting the date of birth to only display the date without the time
            SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MMM/yyyy");
            String dateOfBirth = dateFormat.format(user.DoB);
            editDob.setText(dateOfBirth);

            // set name, email and dob on profile activity screen
            name.setText(user.Name);
            email.setText(EmailAddress);
            dob.setText(dateOfBirth);

            // Set update buttons so that they do not show unless updating
            saveButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);

            // Set EditText fields so that they do not show unless updating
            editName.setVisibility(View.GONE);
            editDob.setVisibility(View.GONE);

            // Set OnClickListener for edit icon when user wants to update
            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mode.setText("Edit Profile");

                    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH,month);
                            myCalendar.set(Calendar.DAY_OF_MONTH,day);
                            updateLabel(editDob);
                        }
                    };
                    // create datepickerdialog
                    DatePickerDialog datePickerDialog= new DatePickerDialog(ProfileActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                    editDob.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            datePickerDialog.show();
                        }
                    });

                    //Setting the Save and Cancel buttons and edittext fields to visible
                    saveButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);
                    editName.setVisibility(View.VISIBLE);
                    editDob.setVisibility(View.VISIBLE);

                    //Setting the edit icon and delete button and textview fields to gone
                    deleteButton.setVisibility(View.GONE);
                    editProfile.setVisibility(View.GONE);
                    name.setVisibility(View.GONE);
                    dob.setVisibility(View.GONE);

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Setting the Save and Cancel buttons and edittext fields to gone
                            saveButton.setVisibility(View.GONE);
                            cancelButton.setVisibility(View.GONE);
                            editName.setVisibility(View.GONE);
                            editDob.setVisibility(View.GONE);

                            //Setting the edit icon and delete button and textview fields to visible
                            deleteButton.setVisibility(View.VISIBLE);
                            editProfile.setVisibility(View.VISIBLE);
                            name.setVisibility(View.VISIBLE);
                            dob.setVisibility(View.VISIBLE);
                            mode.setText("Profile");
                        }
                    });
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveButton.setVisibility(View.GONE);
                            cancelButton.setVisibility(View.GONE);
                            editName.setVisibility(View.GONE);
                            editDob.setVisibility(View.GONE);

                            //Setting the edit icon and delete button and textview fields to visible
                            deleteButton.setVisibility(View.VISIBLE);
                            editProfile.setVisibility(View.VISIBLE);
                            name.setVisibility(View.VISIBLE);
                            dob.setVisibility(View.VISIBLE);
                            mode.setText("Profile");

                            Date dateOfBirth = null;
                            try {
                                dateOfBirth = new SimpleDateFormat("dd/MMM/yyyy").parse(editDob.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userDocRef = db.collection("IPPTUser").document(EmailAddress);
                            Task<Void> task = IPPTUser.updateUser(userDocRef,editName.getText().toString(),dateOfBirth);
                            task.addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        GenericErrorToast.show();
                                    }
                                }
                            });

                            name.setText(editName.getText().toString());
                            dob.setText(dateFormat.format(dateOfBirth));


                        }
                    });

                }
            });


            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Delete Account Confirmation");
                    builder.setMessage("Are you sure you want to delete your account?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userDocRef = db.collection("IPPTUser").document(EmailAddress);

                            Task<Void> task = IPPTUser.deleteUser(userDocRef);
                            task.addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Profile Deleted", Toast.LENGTH_SHORT).show();
                                            Intent homeIntent = new Intent(ProfileActivity.this, LoginActivity.class);
                                            startActivity(homeIntent);
                                        }
                                        else {
                                            GenericErrorToast.show();
                                        }
                                        finish();
                                    }
                                });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    builder.show();
                }
            });




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

    private void updateLabel(EditText dob){
        String myFormat="dd/MMM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(dateFormat.format(myCalendar.getTime()));
    }
}