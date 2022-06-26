package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class LoginActivity extends AppCompatActivity {
    private static final String DEBUG = "DEBUG";
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login);

        // Build google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // set up google client
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Get last signedin account if there is any...
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // Set up Sign in button
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        updateUI(account);
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
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            updateUI(task.getResult());
        }
    }

    // Access Firestore on successful google login
    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            Toast.makeText(this, "Google Login Success", Toast.LENGTH_SHORT).show();
            String personEmail = account.getEmail();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("IPPTUser").document(personEmail);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        // If user has already have an account in IPPTReady
                        if (document.exists()) {
                            User user = document.toObject(User.class);

                            Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            loginIntent.putExtra("Email", personEmail);
                            loginIntent.putExtra("User", user);

                            Toast.makeText(LoginActivity.this, "Hello, " + user.Name + "!", Toast.LENGTH_SHORT).show();
                            startActivity(loginIntent);

                        } else {
                            // Create an account if user has no account
                            Toast.makeText(LoginActivity.this, "Welcome to IPPTReady!", Toast.LENGTH_SHORT).show();
                            String personName = account.getDisplayName();

                            Intent createAccountIntent = new Intent(LoginActivity.this, CreateAccountActivity.class);

                            Bundle userDetails = new Bundle();
                            userDetails.putString("Email", personEmail);
                            userDetails.putString("Name", personName);

                            createAccountIntent.putExtras(userDetails);
                            startActivity(createAccountIntent);
                        }
                    } else {
                        Log.d(DEBUG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }
}