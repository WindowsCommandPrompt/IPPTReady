package sg.np.edu.mad.ipptready.Cycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseDoc;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseViewItem;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTUser;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTCycle;
import sg.np.edu.mad.ipptready.R;
import sg.np.edu.mad.ipptready.Routine.RoutineActivity;

public class CycleActivity extends AppCompatActivity {
    private String userId;
    private Date DOB;
    private DocumentReference userDocRef;

    private DateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // Objects loaded in the activities
    private RecyclerView recyclerView;
    private IPPTCycleAdapter ipptCycleAdapter;
    private ActivityResultLauncher<Date> createCycleActivityLauncher;

    private ArrayList<FirebaseViewItem<IPPTCycle>> finishedCycles;
    private FirebaseViewItem<IPPTCycle> notFinishedCycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);
        // set View temporary to a loading screen


        // Input from Home Activity:
        // "userId" : String, userId of User document
        // "DOB", Date of birth of user

        // Output to CreateCycleActivity:
        // "userId" : String, userId of User document
        // "DateCreated" : Date, current Date created

        // Output to RoutineActivity:
        // "userId" : String, userId of User document
        // "cycleId" : String, cycleId of Cycle document
        // "DOB" : Date, Date of birth of user

        // Note:        Make sure to save the Input data using the onSaveInstanceState(android.os.Bundle),
        //                  so that we don't need to retrieve the data again!
        //
        // Extra Note: Check saveInstanceState if Intent is empty!

        Toast GenericErrorToast = Toast.makeText(this,
                "Unexpected error occurred",
                Toast.LENGTH_SHORT);

        if (null != getIntent()) {
            Intent intent = getIntent();
            // still not a typesafe language!

            userId = intent.getStringExtra("userId");
            DOB = (Date)intent.getSerializableExtra("DOB");
            userDocRef = IPPTUser.getUserDocFromId(userId);
        }
        else if (null != savedInstanceState) {
            userId = savedInstanceState.getString("Email");
            DOB = (Date)savedInstanceState.getSerializable("DOB");
            userDocRef = IPPTUser.getUserDocFromId(userId);
        }
        else {
            // if all else fails...
            GenericErrorToast.show();
            finish();
        }

        // if got userId, go!
        if (null != userId) {
            IPPTCycle.getCyclesFromUser(userDocRef)
                    .addOnCompleteListener(
                            new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        setCycleView();
                                        QuerySnapshot querySnapshot = task.getResult();
                                        finishedCycles = new ArrayList<>();

                                        if (!querySnapshot.isEmpty()) {

                                            for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                                FirebaseViewItem<IPPTCycle> ipptCycleViewItem = new FirebaseViewItem<>(new IPPTCycle(documentSnapshot.getData()),
                                                        documentSnapshot.getReference());

                                                if (ipptCycleViewItem.viewItem.isFinished)
                                                    finishedCycles.add(ipptCycleViewItem);
                                                else
                                                    notFinishedCycle = ipptCycleViewItem;
                                            }

                                            if (null != notFinishedCycle) {
                                                setCycleTextViewFields(notFinishedCycle);
                                                setCompleteCycleButton();
                                            }
                                            else
                                                setCreateCycleButton();

                                            setRecycleViewContent(finishedCycles);
                                        }
                                        else {
                                            setRecycleViewContent(finishedCycles);
                                            setCreateCycleButton();
                                        }
                                    }
                                    else {
                                        Toast.makeText(CycleActivity.this, "Failed to retrieve IPPT Cycles!", Toast.LENGTH_SHORT)
                                                .show();
                                        finish();
                                    }
                                }
                            });

            createCycleActivityLauncher = registerForActivityResult(new CycleActivityResultContract(),
                    new CycleActivityResultCallback(this));
        }
        else {
            // missing data from intent or saveInstanceState
            GenericErrorToast.show();
            finish();
        }
    }

    private void setCycleView() {
        setContentView(R.layout.activity_cycle);
        recyclerView = findViewById(R.id.cycleRecyclerView);
    }

    private void setRecycleViewContent(List<FirebaseViewItem<IPPTCycle>> ipptCycleList) {
        ipptCycleAdapter = new IPPTCycleAdapter(ipptCycleList, CycleActivity.this,
                userId, DOB);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(ipptCycleAdapter);
    }

    private void setCycleTextViewFields(FirebaseViewItem<IPPTCycle> ipptCycleViewItem) {
        ((TextView)findViewById(R.id.cyclenameText)).setText(ipptCycleViewItem.viewItem.Name);
        ((TextView)findViewById(R.id.cycledateCreatedText)).setText(dateFormat.format(ipptCycleViewItem.viewItem.DateCreated));
    }

    private class CreateCycleOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Go to CreateCycleActivity...
            createCycleActivityLauncher.launch(new Date());
        }
    }

    private class CompleteCycleOnClickListener implements  View.OnClickListener {
        private Context context;

        public CompleteCycleOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            IPPTCycle.finishCycle(notFinishedCycle.documentReference)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (null != notFinishedCycle) {
                                    finishedCycles.add(notFinishedCycle);
                                    ipptCycleAdapter.notifyItemInserted(finishedCycles.size() - 1);
                                }
                                ((TextView)findViewById(R.id.cyclenameText)).setText("");
                                ((TextView)findViewById(R.id.cycledateCreatedText)).setText("");
                                notFinishedCycle = null;
                                setCreateCycleButton();
                            }
                            else {
                                Toast.makeText(context, "Failed to complete cycle!", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
        }
    }

    public static class RoutineOnClickListener implements  View.OnClickListener {
        public FirebaseViewItem<IPPTCycle> ipptCycleViewItem;
        public Context context;
        public String userId;
        public Date DOB;

        public RoutineOnClickListener(Context context, FirebaseViewItem<IPPTCycle> ipptCycleViewItem,
                                      String userId,
                                      Date DOB) {
            this.context =context;
            this.ipptCycleViewItem = ipptCycleViewItem;
            this.userId = userId;
            this.DOB = DOB;
        }

        @Override
        public void onClick(View v) {
            Log.d("CycleActivity", "View Clicked! Going to RoutineActivity...");

            Intent intent = new Intent(context, RoutineActivity.class);

            intent.putExtra("userId", userId);
            intent.putExtra("cycleId", ipptCycleViewItem.documentReference.getId());
            intent.putExtra("isFinished", ipptCycleViewItem.viewItem.isFinished);
            intent.putExtra("DOB", DOB);

            context.startActivity(intent);
        }
    }

    private void setCreateCycleButton() {
        ((Button)findViewById(R.id.completecreatecycleButton)).setText("Create A New Cycle");
        ((Button)findViewById(R.id.completecreatecycleButton)).setBackgroundResource(R.drawable.blueborder);
        findViewById(R.id.completecreatecycleButton).setOnClickListener(new CreateCycleOnClickListener());
        findViewById(R.id.constraintLayout2).setOnClickListener(null);
    }

    private void setCompleteCycleButton() {
        ((Button)findViewById(R.id.completecreatecycleButton)).setText("Complete Cycle");
        ((Button)findViewById(R.id.completecreatecycleButton)).setBackgroundResource(R.drawable.darkgreenborder);
        findViewById(R.id.completecreatecycleButton).setOnClickListener(new CompleteCycleOnClickListener(this));
        if (null != notFinishedCycle)
            findViewById(R.id.constraintLayout2).setOnClickListener(new RoutineOnClickListener(this, notFinishedCycle, userId, DOB));
    }

    private class CycleActivityResultContract extends ActivityResultContract<Date, FirebaseDoc<IPPTCycle>> {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, @NonNull Date dateCreated) {
            Intent intent = new Intent(CycleActivity.this, CreateCycleActivity.class);

            // add Intents for CreateCycleActivity
            intent.putExtra("userId", userId);
            intent.putExtra("DateCreated", dateCreated);
            return intent;
        }

        @Override
        public FirebaseDoc<IPPTCycle> parseResult(int resultCode, @Nullable Intent result) {
            if (Activity.RESULT_OK != resultCode || null == result) {
                return null;
            }
            return (FirebaseDoc<IPPTCycle>) result.getSerializableExtra("IPPTCycle");
        }
    }

    public class CycleActivityResultCallback implements ActivityResultCallback<FirebaseDoc<IPPTCycle>> {
        private Context context;

        public CycleActivityResultCallback(Context context) {
            this.context = context;
        }

        @Override
        public void onActivityResult(FirebaseDoc<IPPTCycle> result) {
            if (null != result) {
                notFinishedCycle = new FirebaseViewItem<>(result.item, IPPTCycle.getCycleDocFromId(userDocRef, result.documentId));
                setCycleTextViewFields(notFinishedCycle);
                setCompleteCycleButton();
            }
            else {
                setCreateCycleButton();
                Toast.makeText(context, "Failed to create IPPT Cycle, Please try again!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // make sure to call super after writing code ...
        outState.putString("userId", userId);
        outState.putSerializable("DOB", DOB);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected  void onDestroy() {
        if (null != createCycleActivityLauncher) {
            createCycleActivityLauncher.unregister();
        }
        super.onDestroy();
    }
}