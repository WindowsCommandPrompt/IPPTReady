package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.HomeActivity;
import sg.np.edu.mad.ipptready.InternetConnectivity.Internet;
import sg.np.edu.mad.ipptready.LoginActivity;
import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherNoInternetActivity extends AppCompatActivity {
    Internet internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together_no_internet);

        internet = new Internet();

        Button returnBtn = findViewById(R.id.returntologinExTgt);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginscreenIntent = new Intent(ExerciseTogetherNoInternetActivity.this, LoginActivity.class);
                loginscreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Toast.makeText(ExerciseTogetherNoInternetActivity.this, "Returning to Login", Toast.LENGTH_SHORT).show();
                startActivity(loginscreenIntent);
            }
        });

        Button rejoinBtn = findViewById(R.id.rejoin);
        rejoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (internet.isOnline(ExerciseTogetherNoInternetActivity.this))
                {
                    ExerciseTogetherSession.getSessionsbyUserID(getIntent().getStringExtra("userId"))
                            .document(getIntent().getStringExtra("date"))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        String status = (String) document.getData().get("status");
                                        if (status.equals("Created"))
                                        {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("date", getIntent().getStringExtra("date"));
                                            bundle.putString("sessionName", getIntent().getStringExtra("sessionName"));
                                            bundle.putString("exercise", getIntent().getStringExtra("exercise"));
                                            bundle.putString("userId", getIntent().getStringExtra("userId"));
                                            bundle.putParcelable("QRImage", getIntent().getExtras().getParcelable("QRImage"));

                                            Intent beginSession = new Intent(ExerciseTogetherNoInternetActivity.this, ExerciseTogetherWaitingRoomActivity.class);
                                            beginSession.putExtras(bundle);
                                            startActivity(beginSession);
                                            finish();
                                        }
                                    }
                                }
                            });
                }
                else
                {
                    internet.noConnectionAlert(ExerciseTogetherNoInternetActivity.this);
                }
            }
        });
    }
}