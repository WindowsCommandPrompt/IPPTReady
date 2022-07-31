package sg.np.edu.mad.ipptready;

import static android.app.AlarmManager.INTERVAL_DAY;
import static sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser.colFrom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;
import sg.np.edu.mad.ipptready.SQLiteDAL.SQLiteDBHandler;

public class ProfileActivity extends AppCompatActivity {
    private String EmailAddress;
    private IPPTUser user;
    private String Id;

    final Calendar myCalendar= Calendar.getInstance();
    CircleImageView profilePicture;
    private static final int PICK_IMAGE = 100;
    private static final int GET_IMAGE = 200;
    Uri imageUri;
    StorageReference pathReference;
    private static FirebaseStorage storage;
    private static StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_load_data);

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

        // get user information
        if (null != getIntent()) {
            Intent intent = getIntent();
            EmailAddress = intent.getStringExtra("Email");
            // Java is not a typesafe language!
            user = (IPPTUser) intent.getSerializableExtra("User");
            Id = intent.getStringExtra("Id");
        }
        else if (null != savedInstanceState) {
            EmailAddress = savedInstanceState.getString("Email");
            user = (IPPTUser) savedInstanceState.getSerializable("User");
            Id = savedInstanceState.getString("Id");
        }
        else {
            // if all else fails...

            GenericErrorToast.show();
            finish();
        }

        // Get TextViews from ProfileActivity layout
        if (null != EmailAddress &&
            null != user) {
            final String imageKey = UUID.randomUUID().toString();

            // Get TextViews to display the user information
            TextView name = findViewById(R.id.name);
            TextView email = findViewById(R.id.email);
            TextView dob = findViewById(R.id.dateOfBirth);
            TextView mode = findViewById(R.id.userMode);

            // Set subtext for user to change profile picture
            TextView profilepictext = findViewById(R.id.profilePicText);
            profilepictext.setText("Click Profile Picture To Change Picture");
            profilepictext.setVisibility(View.GONE);

            // Get ImageViews to display profile picture and edit icon
            profilePicture = findViewById(R.id.profilePicture);
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


            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Task<DocumentSnapshot> task = db.collection("IPPTUser").document(EmailAddress).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                setContentView(R.layout.activity_profile);

                                DocumentSnapshot userDoc = task.getResult();
                                if (userDoc.exists()){
                                    Map<String, Object> userMap = userDoc.getData();
                                    String imageKey = null;
                                    try{
                                        if (userMap.containsKey("ImageKey"))
                                        {
                                            imageKey = userMap.get("ImageKey").toString();
                                            Log.v("IPPTUser", "IMAGE KEY::" + imageKey);

                                            storage = FirebaseStorage.getInstance();
                                            storageReference = storage.getReference();
                                            pathReference = storageReference.child("profilePictures/" + imageKey);
                                            final long maxBytes = 1024 * 1024;
                                            pathReference.getBytes(maxBytes).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] bytes) {
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                    profilePicture.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 150, 150, false));
                                                }
                                            });
                                        }
                                        else{
                                            String cameraIcon = "@drawable/cameraicon";
                                            int imageResource = getResources().getIdentifier(cameraIcon, null, getPackageName());
                                            profilePicture.setImageDrawable(getResources().getDrawable(imageResource));
                                        }
                                    }
                                    catch (NullPointerException n){
                                        n.printStackTrace();
                                    }
                                }
                                else {
                                    Log.v("IPPTUser", "Doc does not exist::" + pathReference);
                                }
                            }
                            else {
                                Log.v("IPPTUser", "Task not successful::" + pathReference);
                            }
                        }
                    });








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

                    //Setting the Save and Cancel buttons and edittext fields to visible
                    saveButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);
                    editName.setVisibility(View.VISIBLE);
                    editDob.setVisibility(View.VISIBLE);
                    profilepictext.setVisibility(View.VISIBLE);

                    //Setting the edit icon and delete button and textview fields to gone
                    deleteButton.setVisibility(View.GONE);
                    editProfile.setVisibility(View.GONE);
                    name.setVisibility(View.GONE);
                    dob.setVisibility(View.GONE);

                    editDob.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            datePickerDialog.show();
                        }
                    });

                    // set profile picture ImageView OnClickListener to open gallery to get image
                    profilePicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openGallery();
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Setting the Save and Cancel buttons and edittext fields to gone
                            saveButton.setVisibility(View.GONE);
                            cancelButton.setVisibility(View.GONE);
                            editName.setVisibility(View.GONE);
                            editDob.setVisibility(View.GONE);
                            profilepictext.setVisibility(View.GONE);

                            //Setting the edit icon and delete button and textview fields to visible
                            deleteButton.setVisibility(View.VISIBLE);
                            editProfile.setVisibility(View.VISIBLE);
                            name.setVisibility(View.VISIBLE);
                            dob.setVisibility(View.VISIBLE);
                            mode.setText("Profile");
                            profilePicture.setClickable(false);
                        }
                    });
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveButton.setVisibility(View.GONE);
                            cancelButton.setVisibility(View.GONE);
                            editName.setVisibility(View.GONE);
                            editDob.setVisibility(View.GONE);
                            profilepictext.setVisibility(View.GONE);

                            //Setting the edit icon and delete button and textview fields to visible
                            deleteButton.setVisibility(View.VISIBLE);
                            editProfile.setVisibility(View.VISIBLE);
                            name.setVisibility(View.VISIBLE);
                            dob.setVisibility(View.VISIBLE);
                            mode.setText("Profile");
                            profilePicture.setClickable(false);

                            Date dateOfBirth = null;
                            try {
                                dateOfBirth = new SimpleDateFormat("dd/MMM/yyyy").parse(editDob.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            profilePicture.setDrawingCacheEnabled(true);
                            profilePicture.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userDocRef = db.collection("IPPTUser").document(EmailAddress);
                            Task<Void> task = IPPTUser.updateUser(userDocRef,editName.getText().toString(),dateOfBirth, imageKey,data);
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

            findViewById(R.id.imageView7)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ProfileActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                        int time = hour * 60 + minute;
                                        IPPTUser.setTime(IPPTUser.getUserDocFromId(EmailAddress),
                                                time)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ProfileActivity.this, "Time set!", Toast.LENGTH_SHORT)
                                                                .show();
                                                            RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
                                                            HashMap<String, Object> routineAlarmRequestMap = new HashMap<>();
                                                            routineAlarmRequestMap.put("IPPTUserId", Id);
                                                            routineAlarmRequestMap.put("TimeOfDay", String.valueOf(time));

                                                            JSONObject jsonObject = new JSONObject(routineAlarmRequestMap);

                                                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                                                                    "https://watelier.xyz/routine_alarm.php",
                                                                    jsonObject, new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    Log.d("ServerMessage", response.toString());
                                                                }
                                                            }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    Log.d("ServerMessage", error.getMessage());
                                                                }
                                                            });
                                                            queue.add(jsonObjectRequest);
                                                        }
                                                        else {
                                                            Toast.makeText(ProfileActivity.this, "Failed to set time! Please try again.", Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                    }
                                                });
                                    }
                                }, 12, 0, true);
                        timePickerDialog.show();
                    }
                });
        }
        else {
            // missing data in intent or saveInstanceState
            GenericErrorToast.show();
            finish();
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
        }
    }

    @Override
    protected  void onSaveInstanceState(@NonNull Bundle outState) {
        // write code here!
        outState.putString("Email", EmailAddress);
        outState.putSerializable("User", user);
        outState.putString("Id", Id);
        // make sure to call super after writing code ...
        super.onSaveInstanceState(outState);
    }

    private void updateLabel(EditText dob){
        String myFormat="dd/MMM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(dateFormat.format(myCalendar.getTime()));
    }
}