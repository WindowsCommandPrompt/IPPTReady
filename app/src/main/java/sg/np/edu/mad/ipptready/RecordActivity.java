package sg.np.edu.mad.ipptready;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTRecord;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTRoutine;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTCycle;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;

public class RecordActivity extends AppCompatActivity {
    private String userId;
    private String cycleId;
    private String routineId;
    private boolean isFinished;
    private Date DOB;

    private long totalTimeRun;
    private long totalSitups;
    private long totalPushups;

    DocumentReference routineDocRef;
    ActivityResultLauncher<Intent> recordActivityResultLauncher;
    private int completed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_load_data);

        // Input from RecordActivity:
        // "userId" : String, userId of User document
        // "cycId" : string, cycleId of Cycle document
        // "routineId" : String, routineId of Routine document
        // "isFinished" : boolean, whether the routine is finished
        // "DOB" : Date, Date of birth of user

        // Output to RunActivity, SitupActivity, PushupActivity:
        // "Email", String : Email Address of the user.
        // "IPPTCycleId", String : Id of the IPPTCycle
        // "IPPTRoutineId", String : Id of the IPPTRoutine

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

        if (null != getIntent()) {
            Intent intent = getIntent();
            userId = intent.getStringExtra("userId");
            cycleId = intent.getStringExtra("cycleId");
            routineId = intent.getStringExtra("routineId");
            isFinished = intent.getBooleanExtra("isFinished", false);
            DOB = (Date) intent.getSerializableExtra("DOB");
            routineDocRef = IPPTRoutine.getRoutineDocFromId(IPPTCycle.getCycleDocFromId(
                    IPPTUser.getUserDocFromId(userId), cycleId), routineId);
        }
        else if (null != savedInstanceState) {
            userId = savedInstanceState.getString("userId");
            cycleId = savedInstanceState.getString("cycleId");
            routineId = savedInstanceState.getString("routineId");
            isFinished = savedInstanceState.getBoolean("isFinished");
            DOB = (Date) savedInstanceState.getSerializable("DOB");
            routineDocRef = IPPTRoutine.getRoutineDocFromId(IPPTCycle.getCycleDocFromId(
                    IPPTUser.getUserDocFromId(userId), cycleId), routineId);
        }
        else {
            GenericErrorToast.show();
            finish();
        }
        IPPTRecord.getRecordFromRoutine(routineDocRef)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            setContentView(R.layout.activity_record);
                            QuerySnapshot querySnapshot = task.getResult();

                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                    if (documentSnapshot.getId().equals("RunRecord")) {
                                        completed++;
                                        RunRecord runRecord = documentSnapshot.toObject(RunRecord.class);
                                        findViewById(R.id.runrecordButton).setVisibility(View.GONE);
                                        ((TextView)findViewById(R.id.runrecordtimetakenfinished)).setText(SecondstoString(runRecord.TimeTakenFinished));
                                        totalTimeRun = documentSnapshot.getLong("TimeTakenFinished");
                                    }
                                    else if (documentSnapshot.getId().equals("SitupRecord")) {
                                        completed++;
                                        SitupRecord situpRecord = documentSnapshot.toObject(SitupRecord.class);
                                        findViewById(R.id.situprecordButton).setVisibility(View.GONE);
                                        ((TextView)findViewById(R.id.situprecordnumreps)).setText(String.valueOf(situpRecord.NumsReps));
                                        ((TextView)findViewById(R.id.situprecordrepstarget)).setText(String.valueOf(situpRecord.RepsTarget));
                                        totalSitups = documentSnapshot.getLong("NumsReps");
                                    }
                                    else if (documentSnapshot.getId().equals("PushupRecord")) {
                                        completed++;
                                        PushupRecord pushupRecord = documentSnapshot.toObject(PushupRecord.class);
                                        findViewById(R.id.pushuprecordButton).setVisibility(View.GONE);
                                        ((TextView)findViewById(R.id.pushuprecordnumreps)).setText(String.valueOf(pushupRecord.NumsReps));
                                        ((TextView)findViewById(R.id.pushuprecordrepstarget)).setText(String.valueOf(pushupRecord.RepsTarget));
                                        totalPushups = documentSnapshot.getLong("NumsReps");
                                    }
                                }
                            }

                            findViewById(R.id.runrecordButton).setOnClickListener(new RunRecordOnClickListener());
                            findViewById(R.id.situprecordButton).setOnClickListener(new SitupRecordOnClickListener());
                            findViewById(R.id.pushuprecordButton).setOnClickListener(new PushupRecordOnClickListener());

                            if (isFinished) {
                                findViewById(R.id.recordcompletebutton).setVisibility(View.GONE);
                            }
                            else {
                                findViewById(R.id.recordcompletebutton)
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                addScore();
                                            }
                                        });
                            }
                        }
                        else {
                            Toast.makeText(RecordActivity.this, "Failed to retrive records. Please try again!", Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        }
                    }
                });

        recordActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                (result) -> { recreate(); });
    }

    private void addScore() {
        if (3 != completed) {
            Toast.makeText(RecordActivity.this, String.valueOf(completed) + " left to do!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        // get the age group from the DOB of the person
        int age = new Date().getYear() - DOB.getYear();
        int ageGroup = Math.min(Math.max((age - 16)/3, 1), 14);
        // get the index based on the age group
        int ageGroupIndex = ageGroup - 1;
        long roundedTime = totalTimeRun + (10 - totalTimeRun%10);
        // get the json file and parse it
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
        // calculate the 2.4 run portion of the score
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
        // calculate the sit-up portion of the score
        if (totalSitups > 60) {
            scoreSitup = 25;
        }
        else if (totalSitups < 1 ) {
            scoreSitup = 0;
        }
        else {
            try {
                JSONArray situpRecords = jsonObject.getJSONObject("SitupRecord").getJSONArray(String.valueOf(totalSitups));
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
        // calculate push-up portion of the score
        if (totalPushups > 60) {
            scorePushup = 25;
        }
        else if (totalPushups < 1 ) {
            scorePushup = 0;
        }
        else {
            try {
                JSONArray pushupRecords = jsonObject.getJSONObject("PushupRecord").getJSONArray(String.valueOf(totalPushups));
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
        // add up and set the ippt score in firebase
        IPPTRoutine.RoutineAddScore(routineDocRef,
                scorePushup + scoreRun + scoreSitup)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            finish();
                        }
                        else {
                            Toast.makeText(RecordActivity.this, "Failed to update score. Please try again!", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    private void removeAlarm() {
        Intent routineAlertIntent = new Intent(this, RoutineAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, routineAlertIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }

    // formats the seconds to a nice string for display
    private String SecondstoString(int seconds) {
        if (seconds == 0) {
            return null;
        }

        int minute = seconds/60, second = seconds - 60*minute;

        return String.format("%d:%02d", minute, second);
    }

    // Intialize all the intents for the respective activities
    private class RunRecordOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent recordIntent = new Intent(RecordActivity.this, RunActivity.class);

            recordIntent.putExtra("Email", userId);
            recordIntent.putExtra("IPPTCycleId", cycleId);
            recordIntent.putExtra("IPPTRoutineId", routineId);

            recordActivityResultLauncher.launch(recordIntent);
        }
    }

    private class SitupRecordOnClickListener implements  View.OnClickListener {

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", userId);
            bundle.putString("IPPTCycleId", cycleId);
            bundle.putString("IPPTRoutineId", routineId);
            bundle.putBoolean("SitupTargetSet", false);
            Intent recordIntent = new Intent(RecordActivity.this, SitupTargetActivity.class);
            recordIntent.putExtras(bundle);

            recordActivityResultLauncher.launch(recordIntent);
        }
    }

    private class PushupRecordOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent recordIntent = new Intent(RecordActivity.this, PushupTargetActivity.class);

            recordIntent.putExtra("Email", userId);
            recordIntent.putExtra("IPPTCycleId", cycleId);
            recordIntent.putExtra("IPPTRoutineId", routineId);

            recordActivityResultLauncher.launch(recordIntent);
        }
    }

    @Override
    protected  void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("userId", userId);
        outState.putString("cycleId", cycleId);
        outState.putString("routineId", routineId);
        outState.putSerializable("DOB", DOB);
        super.onSaveInstanceState(outState);
    }
    // remember to clean up the launchers after the activity finishes
    @Override
    public void onDestroy() {
        if (null != recordActivityResultLauncher) {
            recordActivityResultLauncher.unregister();
        }
        super.onDestroy();
    }
}