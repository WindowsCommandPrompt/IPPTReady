package sg.np.edu.mad.ipptready;

import sg.np.edu.mad.ipptready.InternetConnectivity.Internet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uk.tastytoasty.TastyToasty;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;

public class LoginActivity extends AppCompatActivity {
    private static final String DEBUG = "DEBUG";
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;
    public static final String CHANNEL_ID = "Routine Notification";
    public static final String isFirstInstantiated_KEY = "isFirstInstantiated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (!sharedPref.getBoolean(isFirstInstantiated_KEY, false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Testing Notification",
                        NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean( isFirstInstantiated_KEY, true);
            editor.apply();
        }

        // Build google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        // set up google client
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Get last signedin account if there is any...
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // Set up Sign in button
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(view -> signIn());
        Internet internet = new Internet();
        if (internet.isOnline(this)) LogIn(account); else internet.noConnectionAlert(this);
    }

    // Sign in method (access google sign in intent)
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Get result from sign in intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            boolean loginSuccess = false;
            Internet internet = new Internet();
            if (result.isSuccess())
            {
                if (internet.isOnline(this))
                {
                    loginSuccess = true;
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    LogIn(task.getResult());
                }
            }
            if (!result.isSuccess() || loginSuccess == false) internet.noConnectionAlert(this);
        }
    }

    // Access Firestore on successful google login
    private void LogIn(GoogleSignInAccount account) {
        if (account != null) {
            String personEmail = account.getEmail();
            String personName = account.getDisplayName();

            HashMap<String, String> RequestMap = new HashMap<>();
            RequestMap.put("IPPTUserId", account.getId());
            FirebaseMessaging.getInstance()
                    .getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                RequestMap.put("RegisterId", task.getResult());

                                JSONObject jsonObject = new JSONObject(RequestMap);
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                                        "https://watelier.xyz/register_device.php",
                                        jsonObject, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            boolean getTime = false;
                                            if (response.getString("Response").equals("Failure"))
                                                Log.d("ServerResponse", response.getString("ErrorMessage"));
                                            else
                                                getTime = response.getBoolean("isFirstTime");

                                            IPPTUser.getUserFromEmail(personEmail)
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful())
                                                            {
                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                Map<String, Object> data =documentSnapshot.getData();
                                                                long time = data.containsKey("RoutineTime")? (long) data.get("RoutineTime") :
                                                                        -1L;
                                                                if (-1L != time) {
                                                                    int hour = (int) (time/60);
                                                                    FCMReceiver.setAlarm(LoginActivity.this, hour,
                                                                            (int)(time - 60*hour));
                                                                    FCMReceiver.setNotification(LoginActivity.this, "IPPTReady",
                                                                            "Alarm set!");
                                                                }

                                                                if (documentSnapshot.exists()) {
                                                                    goToHomePage(documentSnapshot, account.getId());
                                                                }
                                                                else {
                                                                    createAccount(personEmail, personName);
                                                                }
                                                            }
                                                            else {
                                                                Toast.makeText(LoginActivity.this, "An error occured, please try again.",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("ServerResponse", error.getMessage());
                                    }
                                });
                                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                                queue.add(jsonObjectRequest);
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "An Error occured. Please try again!", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
        }
    }


    private void goToHomePage(DocumentSnapshot userDocumentSnapshot, String id) {
        IPPTUser user = new IPPTUser(userDocumentSnapshot.getData());

        Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
        loginIntent.putExtra("Email", userDocumentSnapshot.getReference().getId());
        loginIntent.putExtra("User", user);
        loginIntent.putExtra("Id", id);

        TastyToasty.makeText(LoginActivity.this, "Hello, " + user.Name + "!", TastyToasty.SHORT,null, R.color.greendark, R.color.white, false).show();
        startActivity(loginIntent);
    }

    private void createAccount(String EmailAddress,
                               String Name) {
        Toast.makeText(LoginActivity.this, "Creating an Account...", Toast.LENGTH_SHORT).show();

        Intent createAccountIntent = new Intent(LoginActivity.this, CreateAccountActivity.class);

        createAccountIntent.putExtra("Email", EmailAddress);
        createAccountIntent.putExtra("Name", Name);

        startActivity(createAccountIntent);
    }
}