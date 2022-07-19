package sg.np.edu.mad.ipptready.Cycle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDoc;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTCycle;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;
import sg.np.edu.mad.ipptready.R;

public class CreateCycleActivity extends AppCompatActivity {
    private Date dateCreated;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cycle);

        // Input from CycleActivity:
        // "DateCreated", Date : Date Created of the IPPT Cycle

        // Output to CycleActivity:
        // "IPPTCycle", IPPTCycle : Serialized IPPTCycle Object

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

        if (null != getIntent()) {
            Intent intent = getIntent();
            userId = intent.getStringExtra("userId");
            dateCreated = (Date)intent.getSerializableExtra("DateCreated");
        }
        else if (null != savedInstanceState) {
            userId = savedInstanceState.getString("userId");
            dateCreated = (Date)savedInstanceState.getSerializable("DateCreated");
        }
        else {
            GenericErrorToast.show();
            finish();
        }

        if (null != dateCreated) {
            TextView dateCreatedView = findViewById(R.id.createnewcycleDateCreated);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateCreatedView.setText(dateFormat.format(dateCreated));

            findViewById(R.id.createcyclecreatenewcycleButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String IPPTCycleName = ((TextView)findViewById(R.id.createnewcycleName)).getText().toString();
                    Intent backCycleIntent = new Intent();

                    FirebaseDocChange firebaseDocChange = IPPTCycle.createNewCycle(IPPTUser.getUserDocFromId(userId),
                            IPPTCycleName,
                            dateCreated);

                    FirebaseDoc<IPPTCycle> ipptCycleDoc = new FirebaseDoc<>(new IPPTCycle(IPPTCycleName,
                            dateCreated),
                            firebaseDocChange.documentReference.getId());
                    backCycleIntent.putExtra("IPPTCycle", ipptCycleDoc);

                    firebaseDocChange.changeTask
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        backCycleIntent.putExtra("IPPTCycle", ipptCycleDoc);
                                        setResult(Activity.RESULT_OK, backCycleIntent);
                                    }
                                    else {
                                        setResult(Activity.RESULT_CANCELED, backCycleIntent);
                                    }
                                    finish();
                                }
                            });
                }
            });
        }
        else {
            // Missing data in intent or saveInstanceState
            GenericErrorToast.show();
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("userId", userId);
        outState.putSerializable("DateCreated", dateCreated);
        // make sure to call super after writing code ...
        super.onSaveInstanceState(outState);
    }
}