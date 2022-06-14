package sg.np.edu.mad.ipptready;

import androidx.annotation.*;
import androidx.appcompat.app.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.*;
import android.location.*;
import android.os.*;
import android.text.Layout;
import android.util.Log;
import android.widget.*;

//import com.google.android.gms.location.*;
//import com.google.android.gms.tasks.*;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RunActivity extends AppCompatActivity {

    public ArrayList<String> timings = new ArrayList<String>();

    CountDownTimer mainStopwatch;

    AtomicReference<CountDownTimer> arcdt = new AtomicReference<>(null);

    private FirebaseFirestore RESTdb = FirebaseFirestore.getInstance();

    private void getAndWriteToFirebase(){
        AlertDialog.Builder dataFetchFail = new AlertDialog.Builder(this);
        AlertDialog.Builder dataAppendFailed = new AlertDialog.Builder(this);

        dataAppendFailed
            .setTitle("Data append failed")
            .setMessage("We are not able to save your timing into the databse")
            .setPositiveButton(
                "OK",
                (DialogInterface di, int i) -> {
                    di.dismiss();
                    new CountDownTimer(3000, 1000){
                        @Override
                        public void onTick(long l) { }
                        @Override
                        public void onFinish() {
                            getAndWriteToFirebase();
                        }
                    }.start();
                }
            )
            .setCancelable(false);

        dataFetchFail
            .setTitle("Cannot fetch data from the database")
            .setMessage("Failed to retrieve the required data from the database, do you want to retry this process again??")
            .setPositiveButton(
                "YES",
                (DialogInterface di, int i) -> {
                    di.dismiss();
                    this.getAndWriteToFirebase();
                }
            )
            .setNegativeButton(
                "NO",
                (DialogInterface di, int i) -> {
                    dataAppendFailed.create().show();
                }
            )
            .setCancelable(false);

        RESTdb.collection("IPPTRecord").document("RunRecord")
            .get()
            .addOnSuccessListener(function ->{

            })
            .addOnFailureListener(function -> {
                //display the error message when the database is unable to fetch the required data and return it back to the user
                //dataFetchFail.create().show();
            });

        RESTdb.collection("IPPTUser").get()
                .addOnCompleteListener(function -> {
                    for (DocumentSnapshot ds : function.getResult().getDocuments()){
                        Log.d("DocumentSnapshot", "" + ds.getId()); //this field actually returns the email addresses
                        //get which account the user was logged in through the ProfileActivity.java
                        Intent whiteHole = getIntent();
                        if (whiteHole.getStringExtra("EmailAddressVerifier").equals(ds.getId())){

                        }
                    }
                });
    }

    private JSONObject calculateTotalScore(){
        InputStream is = getResources().openRawResource(R.raw.ipptscore);
        JSONObject jObject = null;
        try {
            byte[] resbytes = new byte[is.available()];
            is.read(resbytes);
            jObject = new JSONObject(new String(resbytes));
        } catch (IOException | JSONException e) {
            Toast.makeText(this, "JSONException", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return jObject;
    }

    private HashMap<String, ArrayList<String>> unpackageJSON() throws JSONException {
        String timingPortionAll = "";
        String timingPortionFirst = "";
        String timingPortionSecond = "";

        HashMap<String, ArrayList<String>> returnItem = new HashMap<>();
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> arrayListCleaningStep2 = new ArrayList<>();
        final ArrayList<String> timingListRaw = new ArrayList<>();
        final ArrayList<ArrayList<String>> scoringCriteriaRaw = new ArrayList<>();
        ArrayList<String> subArray = new ArrayList<>();
        ArrayList<String> singleElement = new ArrayList<>();
        for (String s : calculateTotalScore().get("RunRecord").toString().split(",")){
            arrayList.add(s);
        }
        for (int i = 0; i < arrayList.size(); i++){
            for (String s : arrayList.get(i).split(":")){
                arrayListCleaningStep2.add(s);
            }
        }
        Log.d("InitialLength", "" + arrayListCleaningStep2.size());
        for (int i = 0; i < arrayListCleaningStep2.size(); i++){
            Log.d("DataCleansingStep2", "" + arrayListCleaningStep2.get(i));
            if (arrayListCleaningStep2.get(i).contains("\"")) {
                if (arrayListCleaningStep2.get(i+1).contains("\"")){
                    timingPortionFirst = Character.toString(arrayListCleaningStep2.get(i).charAt(arrayListCleaningStep2.get(i).indexOf("\"") + 1)) + (arrayListCleaningStep2.get(i).length() > arrayListCleaningStep2.get(i).indexOf("\"") + 2 ? arrayListCleaningStep2.get(i).charAt(arrayListCleaningStep2.get(i).indexOf("\"") + 2) : "" );
                    timingPortionSecond = (arrayListCleaningStep2.get(i + 1).length() - 3 > -1 ? arrayListCleaningStep2.get(i + 1).charAt(arrayListCleaningStep2.get(i + 1).length() - 3) : "") + Character.toString(arrayListCleaningStep2.get(i + 1).charAt(arrayListCleaningStep2.get(i + 1).length() - 2));
                    timingPortionAll = timingPortionFirst + ":" + timingPortionSecond;
                    if (timingPortionAll.length() > 0){
                        timingListRaw.add(timingPortionAll);
                    }
                    //Log.d("DataCleansingRemoval", "" + arrayListCleaningStep2.get(i));
                }
            }
            else if (arrayListCleaningStep2.get(i).contains("[")){
                int targetIndex = i;
                if (arrayListCleaningStep2.get(i).contains("]")){ //If both symbols are located on the same row....
                    singleElement.add(arrayListCleaningStep2.get(i).replace(Character.toString(arrayListCleaningStep2.get(i).charAt(0)), "").replace(Character.toString(arrayListCleaningStep2.get(i).charAt(3)), "")); //This line of code only execute once....
                    scoringCriteriaRaw.add(singleElement);
                }
                else if(arrayListCleaningStep2.get(i + 1).contains("]")){
                    //check if the character before the second last character exist
                        subArray.add(Character.toString(arrayListCleaningStep2.get(i).charAt(arrayListCleaningStep2.get(i).indexOf("[") + 1)) + (arrayListCleaningStep2.get(i).length() > arrayListCleaningStep2.get(i).indexOf("[") + 2 ? arrayListCleaningStep2.get(i).charAt(arrayListCleaningStep2.get(i).indexOf("[") + 2) : "")); //head of the entire array
                        //Check for any sandwiched elements....
                        if (!arrayListCleaningStep2.get(i + 1).contains("]")) {
                            subArray.add(arrayListCleaningStep2.get(i + 1));
                        }
                        subArray.add((arrayListCleaningStep2.get(i + 1).length() - 3 > -1 ? arrayListCleaningStep2.get(i + 1).charAt(arrayListCleaningStep2.get(i + 1).length() - 3) : "") + Character.toString(arrayListCleaningStep2.get(i + 1).charAt(arrayListCleaningStep2.get(i + 1).length() - 2))); //tail of the entire array
                        scoringCriteriaRaw.add(subArray);
                }
            }
        }
        Log.d("LENGTH", "" + scoringCriteriaRaw);
        Log.d("TIMINGINDICATORLENGTH", "" + timingListRaw);
        Log.d("InitialLengthAfter", "" + scoringCriteriaRaw.size());
        return returnItem;
    }

    private void calculation2Point4KMScore() throws JSONException{
        HashMap<String, ArrayList<String>> a = unpackageJSON();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        AlertDialog.Builder confirmTerminateCycle = new AlertDialog.Builder(this);
        AlertDialog.Builder saveCycleData = new AlertDialog.Builder(this);

        getAndWriteToFirebase();

        try {
            Log.d("TAG", "" + unpackageJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Build the countdown
        mainStopwatch = new CountDownTimer(1000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = Integer.parseInt(((TextView) findViewById(R.id.timing_indicator_text)).getText().toString().split(":")[0]);
                int seconds = Integer.parseInt(((TextView) findViewById(R.id.timing_indicator_text)).getText().toString().split(":")[1]);
                Log.d("REPEATING", Long.toString(millisUntilFinished));
                seconds++;
                String returnString = "";
                if (seconds == 60) {
                    minutes++;
                    seconds = 0;
                    if (minutes < 10) {
                        if (seconds < 10) {
                            returnString = "0" + minutes + ":0" + seconds;
                        } else {
                            returnString = "0" + minutes + ":" + seconds;
                        }
                    } else {
                        if (seconds < 10) {
                            returnString = minutes + ":0" + seconds;
                        } else {
                            returnString = minutes + ":" + seconds;
                        }
                    }
                } else {
                    if (minutes < 10) {
                        if (seconds < 10) {
                            returnString = "0" + minutes + ":0" + seconds;
                        } else {
                            returnString = "0" + minutes + ":" + seconds;
                        }
                    } else {
                        if (seconds < 10) {
                            returnString = minutes + ":0" + seconds;
                        } else {
                            returnString = minutes + ":" + seconds;
                        }
                    }
                }
                ((TextView) findViewById(R.id.timing_indicator_text)).setText(returnString);
            }
            @Override
            public void onFinish() {
                this.start();
            }
        };

        saveCycleData
            .setTitle("Save data?")
            .setMessage("Are you sure you would like to save your data?")
            .setCancelable(false)
            .setPositiveButton(
                "YES",
                (DialogInterface di, int i) -> {
                    //take note of the timing that is within the TextView
                    String capturedTiming = ((TextView) findViewById(R.id.timing_indicator_text)).getText().toString();
                    finish();
                }
            )
            .setNegativeButton(
                "NO",
                (DialogInterface di, int i) -> {
                    finish();
                }
            );

        //Build the messages accordingly...
        confirmTerminateCycle
            .setTitle("Terminate cycle?")
            .setMessage("Are you sure you want to terminate this run cycle?")
            .setPositiveButton(
                "YES",
                (DialogInterface di, int i) -> {
                    //If the user presses yes THEN STOP THE TIMER
                    saveCycleData.create().show();
                }
            )
            .setNegativeButton(
                "NO",
                (DialogInterface di, int i) -> {
                    //If the user presses no then
                    arcdt.set(new CountDownTimer(1000, 1000){
                        @Override
                        public void onTick(long millisUntilFinished) {
                            int minutes = Integer.parseInt(((TextView) findViewById(R.id.timing_indicator_text)).getText().toString().split(":")[0]);
                            int seconds = Integer.parseInt(((TextView) findViewById(R.id.timing_indicator_text)).getText().toString().split(":")[1]);
                            Log.d("REPEATING", Long.toString(millisUntilFinished));
                            seconds++;
                            String returnString = "";
                            if (seconds == 60) {
                                minutes++;
                                seconds = 0;
                                if (minutes < 10) {
                                    if (seconds < 10) {
                                        returnString = "0" + minutes + ":0" + seconds;
                                    } else {
                                        returnString = "0" + minutes + ":" + seconds;
                                    }
                                } else {
                                    if (seconds < 10) {
                                        returnString = minutes + ":0" + seconds;
                                    } else {
                                        returnString = minutes + ":" + seconds;
                                    }
                                }
                            } else {
                                if (minutes < 10) {
                                    if (seconds < 10) {
                                        returnString = "0" + minutes + ":0" + seconds;
                                    } else {
                                        returnString = "0" + minutes + ":" + seconds;
                                    }
                                } else {
                                    if (seconds < 10) {
                                        returnString = minutes + ":0" + seconds;
                                    } else {
                                        returnString = minutes + ":" + seconds;
                                    }
                                }
                            }
                            ((TextView) findViewById(R.id.timing_indicator_text)).setText(returnString);
                        }
                        @Override
                        public void onFinish() {
                            //Add the relevant event listener to terminate cycle
                            ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
                                Toast.makeText(RunActivity.this, "The timer has been paused", Toast.LENGTH_SHORT);
                                this.cancel();
                                confirmTerminateCycle.create().show();
                            });
                            //when the orientation of the screen has been changed
                            this.start();
                        }
                    });
                    arcdt.get().start();
                }
            )
            .setCancelable(false);

        //Add the relevant event listener to start the cycle
        ((LinearLayout) findViewById(R.id.startCycle)).setOnClickListener(andThenFunctionAs -> {
            //start the timer
            mainStopwatch.start();
            ((LinearLayout) findViewById(R.id.startCycle)).setClickable(false);
            ((LinearLayout) findViewById(R.id.startCycle)).setEnabled(false);
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "Stop watch has been activated", Toast.LENGTH_SHORT).show();
            //Add the relevant event listener to terminate cycle
            ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
                mainStopwatch.cancel();
                confirmTerminateCycle.create().show();
                Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "The timer has been paused", Toast.LENGTH_SHORT).show();
            });
        });

        ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "Please start the stopwatch first before you can terminate the stopwatch. Common sense", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onConfigurationChanged(Configuration config){
        super.onConfigurationChanged(config);
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "landscape", Toast.LENGTH_SHORT).show();
            //pause the timer for a moment
            if (((LinearLayout) findViewById(R.id.startCycle)).isPressed())
            {
                if (arcdt.get() != null) {
                    mainStopwatch.cancel();
                    arcdt.get().cancel();
                    arcdt.get().start();
                } else {
                    mainStopwatch.cancel();
                    mainStopwatch.start();
                }
            }
        }
        else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}