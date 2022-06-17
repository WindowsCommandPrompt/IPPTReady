package sg.np.edu.mad.ipptready;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RecordActivity extends AppCompatActivity {
    private String EmailAddress,
        IPPTCycleId,
        IPPTRoutineId;
    private byte[] SerializedIPPTRoutine;
    private int runRecordScore,
        situpRecordScore,
        pushupRecordScore;
    ActivityResultLauncher<Intent> GoRun,
        GoSitup,
        GoPushup;
    private int completed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_record);

        // Input from RecordActivity:
        // "Email", String : Email Address of the user.
        // "IPPTCycleId", String : Id of the IPPTCycle
        // "IPPTRoutineId", String : Id of the IPPTCycle
        // "IPPTRoutine", byteArray : Serialized IPPTRoutine Object

        // Output to RunActivity, SitupActivity, PushupActivity:
        // "Email", String : Email Address of the user.
        // "IPPTCycleId", String : Id of the IPPTCycle
        // "IPPTRoutineId", String : Id of the IPPTRoutine

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);
        Intent intent = getIntent();
        EmailAddress = intent.getStringExtra("Email");
        IPPTCycleId = intent.getStringExtra("IPPTCycleId");
        IPPTRoutineId = intent.getStringExtra("IPPTRoutineId");
        SerializedIPPTRoutine = intent.getByteArrayExtra("IPPTRoutine");

        if (null == EmailAddress ||
            null == IPPTCycleId ||
            null == IPPTRoutineId ||
            null == SerializedIPPTRoutine) {
            EmailAddress = savedInstanceState.getString("Email");
            IPPTCycleId = savedInstanceState.getString("IPPTCycleId");
            IPPTRoutineId = savedInstanceState.getString("IPPTRoutineId");
            SerializedIPPTRoutine = savedInstanceState.getByteArray("IPPTRoutine");
            if (null == EmailAddress ||
                null == IPPTCycleId ||
                null == IPPTRoutineId ||
                null == SerializedIPPTRoutine) {
                GenericErrorToast.show();
                finish();
            }
        }
        IPPTRoutine ipptRoutine = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(SerializedIPPTRoutine);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            // casting will work 100%! Clueless
            ipptRoutine = (IPPTRoutine) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // show generic error message ...

                    GenericErrorToast.show();
                    e.printStackTrace();
                    finish();
        }
        GoRun = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (null != result) {
                            Intent resultIntent = result.getData();
                            int totalSeconds = resultIntent.getIntExtra("Timing", 0);
                            String timeFinished = SecondstoString(totalSeconds);
                            if (null != timeFinished) {
                                ((TextView)findViewById(R.id.runrecordtimetakenfinished)).setText(timeFinished);
                                ((Button) findViewById(R.id.runrecordButton)).setVisibility(View.GONE);
                            }
                        }
                    }
                });
        GoSitup = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (null != result) {
                            Intent resultIntent = result.getData();
                            int target = resultIntent.getIntExtra("Target", -1);
                            int numberOfSitupsCompleted = resultIntent.getIntExtra("NumReps", -1);
                            if (target != -1 && numberOfSitupsCompleted != -1) {
                                findViewById(R.id.situprecordButton).setVisibility(View.GONE);
                                ((TextView)findViewById(R.id.situprecordnumreps)).setText(String.valueOf(target));
                                ((TextView)findViewById(R.id.situprecordrepstarget)).setText(String.valueOf(numberOfSitupsCompleted));
                            }
                        }
                    }
                });
        GoPushup = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (Activity.RESULT_OK == result.getResultCode() &&
                                null != result.getData()) {
                            Intent resultIntent = result.getData();
                            int numOfPushUpsDone = resultIntent.getIntExtra("NumPushUpsDone", 0);
                            String numOfPushUpsForTarget = resultIntent.getStringExtra("NumPushUpsTarget");
                            ((Button) findViewById(R.id.pushuprecordButton)).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.pushuprecordnumreps)).setText(String.valueOf(numOfPushUpsDone));
                            ((TextView) findViewById(R.id.pushuprecordrepstarget)).setText(String.valueOf(numOfPushUpsForTarget));
                        }
                    }
                });

        ipptRoutine.getRecordsList(EmailAddress,
                                IPPTCycleId,
                                new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (!task.getResult().isEmpty()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    if (document.getId().equals("RunRecord")) {
                                                        completed++;
                                                        RunRecord runRecord = document.toObject(RunRecord.class);
                                                        findViewById(R.id.runrecordButton).setVisibility(View.GONE);
                                                        ((TextView)findViewById(R.id.runrecordtimetakenfinished)).setText(SecondstoString(runRecord.TimeTakenFinished));
                                                    }
                                                    else if (document.getId().equals("SitupRecord")) {
                                                        completed++;
                                                        SitupRecord situpRecord = document.toObject(SitupRecord.class);
                                                        findViewById(R.id.situprecordButton).setVisibility(View.GONE);
                                                        ((TextView)findViewById(R.id.situprecordnumreps)).setText(String.valueOf(situpRecord.NumsReps));
                                                        ((TextView)findViewById(R.id.situprecordrepstarget)).setText(String.valueOf(situpRecord.RepsTarget));
                                                    }
                                                    else if (document.getId().equals("PushupRecord")) {
                                                        completed++;
                                                        PushupRecord pushupRecord = document.toObject(PushupRecord.class);
                                                        findViewById(R.id.pushuprecordButton).setVisibility(View.GONE);
                                                        ((TextView)findViewById(R.id.pushuprecordnumreps)).setText(String.valueOf(pushupRecord.NumsReps));
                                                        ((TextView)findViewById(R.id.pushuprecordrepstarget)).setText(String.valueOf(pushupRecord.RepsTarget));
                                                    }
                                                }
                                                if (View.GONE != findViewById(R.id.runrecordButton).getVisibility()) {
                                                    findViewById(R.id.runrecordButton).setOnClickListener(new RunRecordOnClickListener());
                                                }
                                                if (View.GONE != findViewById(R.id.situprecordButton).getVisibility()) {
                                                    findViewById(R.id.situprecordButton).setOnClickListener(new SitupRecordOnClickListener());
                                                }
                                                if (View.GONE != findViewById(R.id.pushuprecordButton).getVisibility()) {
                                                    findViewById(R.id.pushuprecordButton).setOnClickListener(new PushupRecordOnClickListener());
                                                }
                                            }
                                            else {
                                                findViewById(R.id.runrecordButton).setOnClickListener(new RunRecordOnClickListener());
                                                findViewById(R.id.situprecordButton).setOnClickListener(new SitupRecordOnClickListener());
                                                findViewById(R.id.pushuprecordButton).setOnClickListener(new PushupRecordOnClickListener());
                                            }
                                        }
                                    }
                                });
        Button completeButton = findViewById(R.id.recordcompletebutton);
        if (completed == 3 && ipptRoutine.isFinished) {
            completeButton.setVisibility(View.GONE);
        }

        IPPTRoutine finalIpptRoutine = ipptRoutine;
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalIpptRoutine.getRecordsList(EmailAddress,
                        IPPTCycleId,
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        completed = 0;
                                        int totalTimeRun = 0, totalSitups = 0, totalPushups = 0;
                                        for (DocumentSnapshot document : task.getResult()) {
                                            if (document.getId().equals("RunRecord")) {
                                                totalTimeRun = (int) document.get("TimeTakenFinished");
                                                completed++;
                                            }
                                            else if (document.getId().equals("SitupRecord")) {
                                                totalSitups = (int) document.get("NumsReps");
                                                completed++;
                                            }

                                            else if(document.getId().equals("PushupRecord")) {
                                                totalPushups = (int) document.get("NumsReps");
                                                completed++;
                                            }
                                        }
                                        if (completed == 3) {
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                                            int finalTotalTimeRun = totalTimeRun;
                                            int finalTotalSitups = totalSitups;
                                            int finalTotalPushups = totalPushups;
                                            final int[] totalScore = {0};
                                            db.collection("IPPTUser")
                                                    .document(EmailAddress)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                int YEAR = ((Date) task.getResult().get("DOB", Date.class)).getYear();
                                                                int correctedYEAR = new Date().getYear() - YEAR;
                                                                int ageGroup = 0;
                                                                if (correctedYEAR < 22) {
                                                                    ageGroup = 1;
                                                                } else if (correctedYEAR >= 22 && correctedYEAR <= 24) {
                                                                    ageGroup = 2;
                                                                } else if (correctedYEAR >= 25 && correctedYEAR <= 27) {
                                                                    ageGroup = 3;
                                                                } else if (correctedYEAR >= 28 && correctedYEAR <= 30) {
                                                                    ageGroup = 4;
                                                                } else if (correctedYEAR >= 31 && correctedYEAR <= 33) {
                                                                    ageGroup = 5;
                                                                } else if (correctedYEAR >= 34 && correctedYEAR <= 36) {
                                                                    ageGroup = 6;
                                                                } else if (correctedYEAR >= 37 && correctedYEAR <= 39) {
                                                                    ageGroup = 7;
                                                                } else if (correctedYEAR >= 40 && correctedYEAR <= 42) {
                                                                    ageGroup = 8;
                                                                } else if (correctedYEAR >= 43 && correctedYEAR <= 45) {
                                                                    ageGroup = 9;
                                                                } else if (correctedYEAR >= 46 && correctedYEAR <= 48) {
                                                                    ageGroup = 10;
                                                                } else if (correctedYEAR >= 49 && correctedYEAR <= 51) {
                                                                    ageGroup = 11;
                                                                } else if (correctedYEAR >= 52 && correctedYEAR <= 54) {
                                                                    ageGroup = 12;
                                                                } else if (correctedYEAR >= 55 && correctedYEAR <= 57) {
                                                                    ageGroup = 13;
                                                                } else if (correctedYEAR >= 58 && correctedYEAR <= 60) {
                                                                    ageGroup = 14;
                                                                }

                                                                int ageGroupIndex = ageGroup - 1;
                                                                int roundedTime = finalTotalTimeRun + (10 - finalTotalTimeRun%10);

                                                                Resources res = getResources();
                                                                InputStream is = res.openRawResource(R.raw.ipptscore);
                                                                JSONObject jsonObject = null;
                                                                try {
                                                                    byte[] resbytes = new byte[is.available()];
                                                                    is.read(resbytes);
                                                                    jsonObject = new JSONObject(new String(resbytes));
                                                                } catch (IOException | JSONException e) {
                                                                    Toast.makeText(RecordActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
                                                                    e.printStackTrace();
                                                                }

                                                                int scoreRun = 0, scoreSitup = 0, scorePushup = 0;

                                                                if (roundedTime < 510) {
                                                                    scoreRun = 50;
                                                                }
                                                                else if (roundedTime > 1100) {
                                                                    scoreRun = 0;
                                                                }
                                                                else {
                                                                    try {
                                                                        JSONArray runRecords = jsonObject.getJSONObject("RunRecord").getJSONArray(String.valueOf(roundedTime));
                                                                        if (runRecords.length() < ageGroup) {
                                                                            scoreRun = runRecords.getInt(runRecords.length() - 1);
                                                                        }
                                                                        else {
                                                                            scoreRun = runRecords.getInt(ageGroupIndex);
                                                                        }

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                if (finalTotalSitups > 60) {
                                                                    scoreSitup = 25;
                                                                }
                                                                else if (finalTotalSitups < 1 ) {
                                                                    scoreSitup = 0;
                                                                }
                                                                else {
                                                                    try {
                                                                        JSONArray situpRecords = jsonObject.getJSONObject("SitupRecord").getJSONArray(String.valueOf(finalTotalSitups));
                                                                        if (situpRecords.length() < ageGroup) {
                                                                            scoreSitup = situpRecords.getInt(situpRecords.length() - 1);
                                                                        }
                                                                        else {
                                                                            scoreSitup = situpRecords.getInt(ageGroupIndex);
                                                                        }

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                if (finalTotalPushups > 60) {
                                                                    scorePushup = 25;
                                                                }
                                                                else if (finalTotalPushups < 1 ) {
                                                                    scorePushup = 0;
                                                                }
                                                                else {
                                                                    try {
                                                                        JSONArray pushupRecords = jsonObject.getJSONObject("PushupRecord").getJSONArray(String.valueOf(finalTotalPushups));
                                                                        if (pushupRecords.length() < ageGroup) {
                                                                            scorePushup = pushupRecords.getInt(pushupRecords.length() - 1);
                                                                        }
                                                                        else {
                                                                            scorePushup = pushupRecords.getInt(ageGroupIndex);
                                                                        }

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                totalScore[0] = scorePushup + scoreRun + scoreSitup;

                                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                Map<String, Object> Score = new HashMap<>();
                                                                Score.put("IPPTScore", totalScore[0]);

                                                                db.collection("IPPTUser")
                                                                        .document(EmailAddress)
                                                                        .collection("IPPTCycle")
                                                                        .document(IPPTCycleId)
                                                                        .collection("IPPTRoutine")
                                                                        .document(IPPTRoutineId)
                                                                        .set(Score, SetOptions.merge())
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Toast.makeText(RecordActivity.this, "Successfully recorded IPPT Score: " + totalScore[0], Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(RecordActivity.this, "Error recording IPPT score", Toast.LENGTH_SHORT).show();
                                                                                return;
                                                                            }
                                                                        });


                                                            }
                                                        }
                                                    });

                                            finalIpptRoutine.completeIPPTRoutine(EmailAddress, IPPTCycleId, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(RecordActivity.this, "Well Done! Returning to Routines page", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent();
                                                    intent.putExtra("isCompleted", true);
                                                    intent.putExtra("IPPTScore", totalScore[0]);
                                                    setResult(Activity.RESULT_OK, intent);
                                                    finish();
                                                }
                                            });
                                        }
                                        else {
                                            Toast.makeText(RecordActivity.this, "You have " + String.valueOf(3 - completed) + " more activities to complete!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                });
    }});
    }

    private String SecondstoString(int seconds) {
        if (seconds == 0) {
            return null;
        }

        int minute = seconds/60, second = seconds - 60*minute;

        return String.format("%d:%02d", minute, second);
    }

    private class RunRecordOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent recordIntent = new Intent(RecordActivity.this, RunActivity.class);

            recordIntent.putExtra("Email", EmailAddress);
            recordIntent.putExtra("IPPTCycleId", IPPTCycleId);
            recordIntent.putExtra("IPPTRoutineId", IPPTRoutineId);

            GoRun.launch(recordIntent);
        }
    }

    private class SitupRecordOnClickListener implements  View.OnClickListener {

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", EmailAddress);
            bundle.putString("IPPTCycleId", IPPTCycleId);
            bundle.putString("IPPTRoutineId", IPPTRoutineId);
            bundle.putBoolean("SitupTargetSet", false);
            Intent recordIntent = new Intent(RecordActivity.this, SitupTargetActivity.class);
            recordIntent.putExtras(bundle);

            GoSitup.launch(recordIntent);
        }
    }

    private class PushupRecordOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent recordIntent = new Intent(RecordActivity.this, PushupTargetActivity.class);

            recordIntent.putExtra("Email", EmailAddress);
            recordIntent.putExtra("IPPTCycleId", IPPTCycleId);
            recordIntent.putExtra("IPPTRoutineId", IPPTRoutineId);

            GoPushup.launch(recordIntent);
        }
    }

    @Override
    protected  void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("Email", EmailAddress);
        outState.putString("IPPTCycleId", IPPTCycleId);
        outState.putString("IPPTRoutineId", IPPTRoutineId);
        outState.putByteArray("IPPTRoutine", SerializedIPPTRoutine);
        super.onSaveInstanceState(outState);
    }

    @Override
    @MainThread
    public void onBackPressed() {
        super.onBackPressed();
        Intent backButtonIntent = new Intent();
        backButtonIntent.putExtra("UpdatedScore", runRecordScore +
                situpRecordScore +
                pushupRecordScore);
        setResult(Activity.RESULT_OK, backButtonIntent);
    }

    @Override
    public void onDestroy() {
        GoRun.unregister();
        GoPushup.unregister();
        super.onDestroy();
    }
}