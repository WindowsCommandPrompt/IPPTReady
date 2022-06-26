package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SitupTargetActivity extends AppCompatActivity {
    String targetSitups;
    Integer targetSitupsNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situptarget);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        boolean situpsDone = false;
        TextView promptText = findViewById(R.id.situpTargetTextView);

        if (getIntent().getBooleanExtra("SitupTargetSet", false)) {
            situpsDone = true;
            promptText.setText("Enter number of sit-ups completed in 1 minute");
            Intent intent = getIntent();
            targetSitupsNumber = intent.getIntExtra("Target Situps", 0);
        }


        EditText targetSitupsEditText = findViewById(R.id.situpTargetNumber);

        if (!situpsDone) {
            findViewById(R.id.targetSitupButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    targetSitups = targetSitupsEditText.getText().toString();
                    if(targetSitups.equalsIgnoreCase("")){
                        targetSitupsEditText.setHint("Please enter target sit-ups");
                        targetSitupsEditText.setError("Please enter a number!");
                        return;
                    }
                    else {
                        targetSitupsNumber = Integer.parseInt(targetSitups);
                        Intent situpTimerIntent = new Intent(SitupTargetActivity.this, SitupActivity.class);
                        Bundle bundle = new Bundle();
                        Bundle receivedBundle = getIntent().getExtras();
                        bundle.putString("Email", receivedBundle.getString("Email"));
                        bundle.putString("IPPTCycleId", receivedBundle.getString("IPPTCycleId"));
                        bundle.putString("IPPTRoutineId", receivedBundle.getString("IPPTRoutineId"));
                        bundle.putBoolean("SitupTargetSet", true);
                        bundle.putInt("Target Situps", targetSitupsNumber);
                        situpTimerIntent.putExtras(bundle);
                        startActivity(situpTimerIntent);
                    }
                }
            });
        }
        else {
            Bundle receivedBundle = getIntent().getExtras();
            findViewById(R.id.targetSitupButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String completedSitups = targetSitupsEditText.getText().toString();
                    if(completedSitups.equalsIgnoreCase("")){
                        targetSitupsEditText.setHint("Please enter completed sit-ups");
                        targetSitupsEditText.setError("Please enter a number!");
                        return;
                    }
                    else {
                        Integer completedSitupsNumber = Integer.parseInt(completedSitups);
                        addSitupToDatabase(targetSitupsNumber, completedSitupsNumber, receivedBundle.getString("Email"), receivedBundle.getString("IPPTCycleId"), receivedBundle.getString("IPPTRoutineId"), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SitupTargetActivity.this, "Directing to workout page", Toast.LENGTH_SHORT).show();
                                    Intent recordIntent =  new Intent(SitupTargetActivity.this, RecordActivity.class);
                                    recordIntent.putExtra("Target", targetSitupsNumber);
                                    recordIntent.putExtra("NumReps", completedSitupsNumber);
                                    setResult(Activity.RESULT_OK, recordIntent);
                                    startActivity(recordIntent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(SitupTargetActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void addSitupToDatabase(int target, int completed, String EmailAddress, String IPPTCycleId, String IPPTRoutineId, OnCompleteListener<Void> onCompleteVoidListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> Situp = new HashMap<>();
        Situp.put("RepsTarget", target);
        Situp.put("NumsReps", completed);

        db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .document(IPPTCycleId)
                .collection("IPPTRoutine")
                .document(IPPTRoutineId)
                .collection("IPPTRecord")
                .document("SitupRecord")
                .set(Situp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SitupTargetActivity.this, "Sit-ups recorded!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SitupTargetActivity.this, "Error recording sit-ups", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(onCompleteVoidListener);
    }
}

