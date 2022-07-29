package sg.np.edu.mad.ipptready.ExerciseTogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import sg.np.edu.mad.ipptready.CreateAccountActivity;
import sg.np.edu.mad.ipptready.FirebaseDAL.ExerciseTogetherSession;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDocChange;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;
import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherCreateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_together_create);

        EditText sessionName = findViewById(R.id.sessionNameEditText);

        Spinner exercisesSpinner = (Spinner) findViewById(R.id.exerciseSpinner);
        ArrayAdapter<CharSequence> exercisesArrayAdapter = ArrayAdapter.createFromResource(this, R.array.exercises_array, android.R.layout.simple_spinner_item);
        exercisesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exercisesSpinner.setAdapter(exercisesArrayAdapter);

        Button createBtn = findViewById(R.id.createSessionExTgt);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((sessionName.getText().toString()).equalsIgnoreCase(""))
                {
                    sessionName.setHint("Please enter a session name");
                    sessionName.setError("Please enter a session name!");
                    return;
                }
                String sessionNameText = sessionName.getText().toString();
                String selectedExercise = String.valueOf(exercisesSpinner.getSelectedItem());

                Intent intent = getIntent();
                String EmailAddress = intent.getStringExtra("userId");
                ExerciseTogetherSession session = new ExerciseTogetherSession("", sessionNameText, selectedExercise, EmailAddress);
                FirebaseDocChange firebaseDocChange = ExerciseTogetherSession.createNewSession(EmailAddress, session);
                firebaseDocChange.changeTask
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("date", session.dateCreated);
                                    bundle.putString("sessionName", session.sessionName);
                                    bundle.putString("exercise", session.exercise);
                                    bundle.putString("userId", EmailAddress);
                                    Intent beginSession = new Intent(ExerciseTogetherCreateActivity.this, ExerciseTogetherWaitingRoomActivity.class);
                                    beginSession.putExtras(bundle);
                                    startActivity(beginSession);
                                    finish();
                                }
                                else {
                                    Toast.makeText(ExerciseTogetherCreateActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
            }
        });

        Button returnBtn = findViewById(R.id.cancelCreateExTgt);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String item = parent.getItemAtPosition(pos).toString();
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView<?> parent) { }
}