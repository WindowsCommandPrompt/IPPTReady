package sg.np.edu.mad.ipptready.ExerciseTogether;

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
    boolean uncompletedSessionFlag;
    List<DocumentSnapshot> documents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together);

        Internet internet = new Internet();

        Intent homeIntent = getIntent();
        String userId = homeIntent.getStringExtra("userId");

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

        Button createBtn = findViewById(R.id.createExTgtSession);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(ExerciseTogetherActivity.this, ExerciseTogetherCreateActivity.class);
                createIntent.putExtra("userId", userId);
                startActivity(createIntent);
            }
        });

        Button joinBtn = findViewById(R.id.joinExTgtSession);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinIntent = new Intent(ExerciseTogetherActivity.this, ExerciseTogetherJoinActivity.class);
                joinIntent.putExtra("userId", userId);
                startActivity(joinIntent);
            }
        });

        Button returnBtn = findViewById(R.id.backtoHomefromExTgt);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

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
                                            .setTitle("Uncompleted Session Tracked: " + sessionName)
                                            .setMessage("You have left a session that has not been started or completed.")
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
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
                documents.remove(0);
                checkSessionCompletion();
            }
        }
    }
}