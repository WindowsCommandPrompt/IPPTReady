package sg.np.edu.mad.ipptready;

import androidx.activity.result.ActivityResultRegistry;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateCycleActivity extends AppCompatActivity {
    private Date dateCreated;

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
        Intent intent = getIntent();
        dateCreated = (Date)intent.getSerializableExtra("DateCreated");
        if (null == dateCreated &&
            null == savedInstanceState) {
            dateCreated = (Date) savedInstanceState.getSerializable("DateCreated");
            if (null == dateCreated) {
                // show generic error message to user
                GenericErrorToast.show();
                finish();
            }
        }

        if (null != dateCreated) {
            TextView dateCreatedView = findViewById(R.id.createnewcycleDateCreated);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateCreatedView.setText(dateFormat.format(dateCreated));

            findViewById(R.id.createcyclecreatenewcycleButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String IPPTCycleName = ((TextView)findViewById(R.id.createnewcycleName)).getText().toString();
                    IPPTCycle ipptCycle = new IPPTCycle(IPPTCycleName, dateCreated);
                    Intent backCycleIntent = new Intent();

                    backCycleIntent.putExtra("IPPTCycle", ipptCycle);
                    setResult(Activity.RESULT_OK, backCycleIntent);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // write code here!
        outState.putSerializable("DateCreated", dateCreated);
        // make sure to call super after writing code ...
        super.onSaveInstanceState(outState);
    }
}