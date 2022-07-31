package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.InternetConnectivity.Internet;
import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherActivity extends AppCompatActivity {
    // Exercise Together feature done by: BRYAN KOH

    // Global variables
    boolean uncompletedSessionFlag; // Flag if user has an uncompleted session
    List<DocumentSnapshot> documents; // Store documents of user sessions
    ActivityResultLauncher<Intent> ExTgtActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together);

        // Internet object
        Internet internet = new Internet();

        // Get Intent
        Intent homeIntent = getIntent();
        String userId = homeIntent.getStringExtra("userId");

        // Recreate activity after coming back here from an activity
        ExTgtActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (result) -> { recreate(); });

        // If there is no Internet connection, show a no connection alert.
        // If there is an Internet connection, get user sessions from Firestore
        uncompletedSessionFlag = false;
        if (!internet.isOnline(this)){
            internet.noConnectionAlert(this);
            finish();
        }
        else
        {
            ExerciseTogetherSession.getSessionsbyUserID(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                            {
                                Log.d("Success1", "I am successful!");
                                QuerySnapshot querySnapshot = task.getResult();
                                documents = querySnapshot.getDocuments();
                                checkSessionCompletion();
                            }
                        }
                    });
        }

        // Creating a new Exercise Together Session
        Button createBtn = findViewById(R.id.createExTgtSession);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(ExerciseTogetherActivity.this, ExerciseTogetherCreateActivity.class);
                createIntent.putExtra("userId", userId);
                ExTgtActivityResultLauncher.launch(createIntent);
            }
        });

        // Joining an Exercise Together Session
        Button joinBtn = findViewById(R.id.joinExTgtSession);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinIntent = new Intent(ExerciseTogetherActivity.this, ExerciseTogetherJoinActivity.class);
                joinIntent.putExtra("userId", userId);
                ExTgtActivityResultLauncher.launch(joinIntent);
            }
        });

        // Return to home activity
        Button returnBtn = findViewById(R.id.backtoHomefromExTgt);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // This will check the status of all the session documents (obtained from Firestore)
    // If session checked is marked as not completed or has not started, alert dialog will appear, informing the user about the abrupt leaving of session
    private void checkSessionCompletion()
    {
        Log.d("DocumentSize", String.valueOf(documents.size()));
        if (documents.size() != 0)
        {
            Log.d("Success2", "I am successful");
            DocumentSnapshot ds = documents.get(0);
            String status = (String) ds.getData().get("status");
            String sessionName = (String) ds.getData().get("sessionName");
            if (!status.equals("Completed") && !status.equals("Started") && !status.equals("Left"))
            {
                // Set the status of uncompleted sessions as "Left"
                ExerciseTogetherSession.updateIndividualJoinStatus(ds.getReference(), "Left")
                        .changeTask
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Log.d("Success3", "I am successful!");
                                    Context ctx = ExerciseTogetherActivity.this;
                                    AlertDialog.Builder alertNoComplete = new AlertDialog.Builder(ctx);
                                    alertNoComplete
                                            .setTitle("Left Session: " + sessionName)
                                            .setMessage("You have left a session that has not been started or completed.")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    // After checking the document, remove the document from the list.
                                                    documents.remove(0);
                                                    checkSessionCompletion();
                                                }
                                            });
                                    alertNoComplete.create().show();
                                }
                                else Log.d("Fail2", "I am a failure!");
                            }
                        });
            }
            else
            {
                // After checking the document, remove the document from the list.
                documents.remove(0);
                checkSessionCompletion();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (null != ExTgtActivityResultLauncher) {
            ExTgtActivityResultLauncher.unregister();
        }
        super.onDestroy();
    }
}