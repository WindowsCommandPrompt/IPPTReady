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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RunActivity extends AppCompatActivity {

    public ArrayList<String> timings = new ArrayList<String>();

    CountDownTimer mainStopwatch;

    AtomicReference<CountDownTimer> arcdt = new AtomicReference<>(null);

    private FirebaseFirestore RESTdb = FirebaseFirestore.getInstance();

    public RunActivity() { }

    public JSONObject unparseJSON() throws JSONException {
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

    public HashMap<String, ArrayList<ArrayList<String>>> unpackagePartialJSON() throws JSONException {
        String timingPortionAll = "";
        String timingPortionFirst = "";
        String timingPortionSecond = "";
        HashMap<String, ArrayList<ArrayList<String>>> returnItem = new HashMap<>();
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> arrayListCleaningStep2 = new ArrayList<>();
        final ArrayList<String> timingListRaw = new ArrayList<>();
        final ArrayList<ArrayList<String>> scoringCriteriaRaw = new ArrayList<>();
        ArrayList<String> subArray = new ArrayList<>();
        ArrayList<String> singleElement = new ArrayList<>();
        for (String s : unparseJSON().get("RunRecord").toString().split(",")){
            arrayList.add(s);
        }
        for (int i = 0; i < arrayList.size(); i++){
            for (String s : arrayList.get(i).split(":")){
                arrayListCleaningStep2.add(s);
            }
        }
        Log.d("InitialLength", "" + arrayListCleaningStep2.size());
        for (int i = 0; i < arrayListCleaningStep2.size(); i++) {
            Log.d("DataCleansingStep2", "" + arrayListCleaningStep2.get(i));
            if (arrayListCleaningStep2.get(i).contains("\"")) {
                if (arrayListCleaningStep2.get(i + 1).contains("\"")) {
                    timingPortionFirst = Character.toString(arrayListCleaningStep2.get(i).charAt(arrayListCleaningStep2.get(i).indexOf("\"") + 1)) + (arrayListCleaningStep2.get(i).length() > arrayListCleaningStep2.get(i).indexOf("\"") + 2 ? arrayListCleaningStep2.get(i).charAt(arrayListCleaningStep2.get(i).indexOf("\"") + 2) : "");
                    timingPortionSecond = (arrayListCleaningStep2.get(i + 1).length() - 3 > -1 ? arrayListCleaningStep2.get(i + 1).charAt(arrayListCleaningStep2.get(i + 1).length() - 3) : "") + Character.toString(arrayListCleaningStep2.get(i + 1).charAt(arrayListCleaningStep2.get(i + 1).length() - 2));
                    timingPortionAll = timingPortionFirst + ":" + timingPortionSecond;
                    if (timingPortionAll.length() > 0) {
                        timingListRaw.add(timingPortionAll);
                    }
                }
            }
        }
        Log.d("TIMINGINDICATORLENGTH", "" + new ArrayList<ArrayList<String>>(Arrays.asList(timingListRaw))); //correct
        returnItem.put("Timings", new ArrayList<ArrayList<String>>(Arrays.asList(timingListRaw)));
        return returnItem;
    }

    private void calculation2Point4KMScore(String capturedTiming) throws JSONException {
        AlertDialog.Builder dataFetchFail = new AlertDialog.Builder(this);
        AlertDialog.Builder dataAppendFailed = new AlertDialog.Builder(this);
        dataAppendFailed
        .setTitle("Data append failed")
        .setMessage("We are not able to save your timing into the database")
        .setPositiveButton(
            "OK",
            (DialogInterface di, int i) -> {
                di.dismiss();
                new CountDownTimer(3000, 1000){
                    @Override
                    public void onTick(long l) { }
                    @Override
                    public void onFinish() {
                        try {
                            calculation2Point4KMScore(capturedTiming);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                try {
                    calculation2Point4KMScore(capturedTiming);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        )
        .setNegativeButton(
            "NO",
            (DialogInterface di, int i) -> {
                dataAppendFailed.create().show();
            }
        )
        .setCancelable(false);

        String RunRecordDataSegment = unparseJSON().getString("RunRecord");
        //Log.d("Current", "" + new JSONObject(RunRecordDataSegment).getString("10:20").split("[\\[,\\]]")[0]);

        HashMap<String, ArrayList<ArrayList<String>>> a = unpackagePartialJSON();
        ArrayList<ArrayList<String>> values = a.values().iterator().next();
        for (int i = 0; i < values.get(0).size(); i++){
            Log.d("TIMING", "" + values.get(0).size()); //60 CORRECT
            if (values.get(0).get(i).equals(capturedTiming)){
                RESTdb.collection("IPPTUser")
                .get()
                .addOnCompleteListener(function -> {
                    for (DocumentSnapshot ds : function.getResult().getDocuments()){
                        Log.d("EmailAddresses", "" + ds.getId()); //this field actually returns the email addresses
                        //Log.d("USERDOB", "" + ((Timestamp) ds.getData().values().iterator().next()).getSeconds()); //returns a Timestamp object
                        //get which account the user was logged in through the ProfileActivity.java
                        Intent whiteHole = getIntent();
                        Log.d("Code has reached here", "The code has reached here");
                        if (whiteHole.getStringExtra("EmailAddressVerifier") != null) {
                            if (whiteHole.getStringExtra("EmailAddressVerifier").equals(ds.getId())) {
                                //update the relevant field within the same database under the same email address that the user has used in order to sign into the platform
                                //Shift this chunk of code later
                                //whiteHole.getStringExtra("EmailAddressVerifier")
                                RESTdb.collection("IPPTUser").document(whiteHole.getStringExtra("EmailAddressVerifier"))
                                .get()
                                .addOnCompleteListener(onServerResponse -> {
                                    if (onServerResponse.isSuccessful()){
                                        //Once the server has provided us with the response, we need to get the DOB of the user that has signed into the application.
                                        //extract the POSIX seconds from the so-called timestamp
                                        int YEAR = ((Date) onServerResponse.getResult().get("DOB", Date.class)).getYear();
                                        int correctedYEAR = YEAR + 1900;
                                        Log.d("CorrectedYEAR", "" + correctedYEAR);
                                        int ageGroup = 0;
                                        if (correctedYEAR < 22){
                                            ageGroup = 1;
                                        }
                                        else if (correctedYEAR >= 22 && correctedYEAR <= 24){
                                            ageGroup = 2;
                                        }
                                        else if (correctedYEAR >= 25 && correctedYEAR <= 27){
                                            ageGroup = 3;
                                        }
                                        else if (correctedYEAR >= 28 && correctedYEAR <= 30){
                                            ageGroup = 4;
                                        }
                                        else if (correctedYEAR >= 31 && correctedYEAR <= 33){
                                            ageGroup = 5;
                                        }
                                        else if (correctedYEAR >= 34 && correctedYEAR <= 36){
                                            ageGroup = 6;
                                        }
                                        else if (correctedYEAR >= 37 && correctedYEAR <= 39){
                                            ageGroup = 7;
                                        }
                                        else if (correctedYEAR >= 40 && correctedYEAR <= 42){
                                            ageGroup = 8;
                                        }
                                        else if (correctedYEAR >= 43 && correctedYEAR <= 45){
                                            ageGroup = 9;
                                        }
                                        else if (correctedYEAR >= 46 && correctedYEAR <= 48){
                                            ageGroup = 10;
                                        }
                                        else if (correctedYEAR >= 49 && correctedYEAR <= 51){
                                            ageGroup = 11;
                                        }
                                        else if (correctedYEAR >= 52 && correctedYEAR <= 54){
                                            ageGroup = 12;
                                        }
                                        else if (correctedYEAR >= 55 && correctedYEAR <= 57){
                                            ageGroup = 13;
                                        }
                                        else if (correctedYEAR >= 58 && correctedYEAR <= 60){
                                            ageGroup = 14;
                                        }
                                        //get from the array list and then reference from it...
                                        for (int j = 0; j < a.values().iterator().next().get(0).size(); j++) {
                                            if (a.values().iterator().next().get(0).get(j).equals(capturedTiming)){
                                                String[] correspondingArray = new String[] { };
                                                try {
                                                    correspondingArray = new JSONObject(RunRecordDataSegment).getString(capturedTiming).split("[\\[,\\]]");
                                                }
                                                catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                String correspondingScoreAttainable = correspondingArray[ageGroup];
                                                RunRecord rr = new RunRecord(
                                                        Integer.parseInt(correspondingScoreAttainable)
                                                );
                                                Log.d("ISTHESCORECORRECT????", "" + rr.TimeTakenTotal);
                                                //If the timing is correct then perform a getrequest from the RESTdb again to get the details about the CycleID


                                            }
                                        }
                                    }
                                    else {
                                        //Once the server has not provided us with the response then
                                        dataFetchFail.create().show();
                                    }
                                });
                            }
                        }
                        else {
                            Log.e("UserNotSignedInError", "No user detected on this device..Please sign into the application first");
                            //Add
                            RESTdb.collection("IPPTUser")
                                .document("bryanflee01@gmail.com")
                                .collection("IPPTCycle")
                                .get()
                                .addOnCompleteListener(function1 -> {
                                    Log.d("LogITOUT", "" + function1.getResult().getDocuments());
                                });

                        }
                    }
                })
                .addOnFailureListener(function -> {
                    dataFetchFail.create().show();
                });
            }
            else {
                //if the timing does not exist within the key
                String[] ab = capturedTiming.split(":");
                if (Integer.parseInt(ab[1]) >= 0 && Integer.parseInt(ab[1]) <= 59) {
                    if (Integer.parseInt(ab[0]) > 18 && Integer.parseInt(ab[1]) >= 20) {
                        int score = 0;
                    }
                    else if(Integer.parseInt(ab[0]) < 8 && Integer.parseInt(ab[1]) <= 30){
                        int score = 50; //maximum attainable points from the running...
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        AlertDialog.Builder confirmTerminateCycle = new AlertDialog.Builder(this);
        AlertDialog.Builder saveCycleData = new AlertDialog.Builder(this);

        try {
            Log.d("TAG", "" + unpackagePartialJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            calculation2Point4KMScore("8:30");
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
                    try {
                        calculation2Point4KMScore(capturedTiming);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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