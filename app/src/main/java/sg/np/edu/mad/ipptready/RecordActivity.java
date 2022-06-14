package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class RecordActivity extends AppCompatActivity {
    private String EmailAddress,
        IPPTCycleId,
        IPPTRoutineId;
    private byte[] SerializedIPPTRoutine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // "IPPTRecordId", String : Id of the IPPTRecord

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
                ipptRoutine.getRecordsList(EmailAddress,
                                        IPPTCycleId,
                                        new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (!task.getResult().isEmpty()) {
                                                        for (DocumentSnapshot document : task.getResult()) {
                                                            if ("RunRecord" == document.getId()) {
                                                                RunRecord runRecord = document.toObject(RunRecord.class);
                                                                findViewById(R.id.runrecordButton).setVisibility(View.GONE);
                                                                ((TextView)findViewById(R.id.runrecordtotaldistancetravelled)).setText(String.valueOf(runRecord.TotalDistanceTravelled) + "km");
                                                                ((TextView)findViewById(R.id.runrecordtimetakentotal)).setText(SecondstoString(runRecord.TimeTakenTotal));
                                                                ((TextView)findViewById(R.id.runrecordtimetakenfinished)).setText(SecondstoString(runRecord.TimeTakenFinished));
                                                            }
                                                            else if ("SitupRecord" == document.getId()) {
                                                                SitupRecord situpRecord = document.toObject(SitupRecord.class);
                                                                findViewById(R.id.situprecordButton).setVisibility(View.GONE);
                                                                ((TextView)findViewById(R.id.situprecordnumreps)).setText(String.valueOf(situpRecord.NumsReps));
                                                                ((TextView)findViewById(R.id.situprecordrepstarget)).setText(String.valueOf(situpRecord.RepsTarget));
                                                            }
                                                            else if ("PushupRecord" == document.getId()) {
                                                                PushupRecord pushupRecord = document.toObject(PushupRecord.class);
                                                                findViewById(R.id.pushuprecordButton).setVisibility(View.GONE);
                                                                ((TextView)findViewById(R.id.pushuprecordnumreps)).setText(String.valueOf(pushupRecord.NumsReps));
                                                                ((TextView)findViewById(R.id.pushuprecordrepstarget)).setText(String.valueOf(pushupRecord.RepsTarget));
                                                            }
                                                        }
                                                        /*if (View.GONE != findViewById(R.id.runrecordButton).getVisibility()) {
                                                            findViewById(R.id.runrecordButton).setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent recordIntent = new Intent(RecordActivity.this, RunActivity.class);

                                                                    recordIntent.putExtra("Email", EmailAddress);
                                                                    recordIntent.putExtra("IPPTCycleId", IPPTCycleId);
                                                                    recordIntent.putExtra("IPPTRoutineId", IPPTRoutineId);
                                                                    recordIntent.putExtra("IPPTRecordId", "RunRecord");

                                                                    startActivity(recordIntent);
                                                                }
                                                            });
                                                        }
                                                        if (View.GONE != findViewById(R.id.situprecordButton).getVisibility()) {
                                                            findViewById(R.id.situprecordButton).setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent recordIntent = new Intent(RecordActivity.this, SitupActivity.class);

                                                                    recordIntent.putExtra("Email", EmailAddress);
                                                                    recordIntent.putExtra("IPPTCycleId", IPPTCycleId);
                                                                    recordIntent.putExtra("IPPTRoutineId", IPPTRoutineId);
                                                                    recordIntent.putExtra("IPPTRecordId", "SitupRecord");

                                                                    startActivity(recordIntent);
                                                                }
                                                            });
                                                        }
                                                        if (View.GONE != findViewById(R.id.pushuprecordButton).getVisibility()) {
                                                            findViewById(R.id.pushuprecordButton).setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent recordIntent = new Intent(RecordActivity.this, PushupActivity.class);

                                                                    recordIntent.putExtra("Email", EmailAddress);
                                                                    recordIntent.putExtra("IPPTCycleId", IPPTCycleId);
                                                                    recordIntent.putExtra("IPPTRoutineId", IPPTRoutineId);
                                                                    recordIntent.putExtra("IPPTRecordId", "PushupRecord");

                                                                    startActivity(recordIntent);
                                                                }
                                                            });
                                                        }*/
                                                    }
                                                }
                                            }
                                        });
            }
        }
    }

    private String SecondstoString(int seconds) {
        int hour = seconds%3600,
            minute = (seconds-(3600*hour))%60,
            second = seconds - 3600*hour - 60*minute;

        return String.format("%d:%d:%d", hour, minute, second);
    }

    @Override
    protected  void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("Email", EmailAddress);
        outState.putString("IPPTCycleId", IPPTCycleId);
        outState.putString("IPPTRoutineId", IPPTRoutineId);
        outState.putByteArray("IPPTRoutine", SerializedIPPTRoutine);
        super.onSaveInstanceState(outState);
    }
}