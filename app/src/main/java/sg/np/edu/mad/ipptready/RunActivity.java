package sg.np.edu.mad.ipptready;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class RunActivity extends AppCompatActivity implements LocationListener {

    CountDownTimer mainStopwatch;

    AtomicReference<CountDownTimer> arcdt = new AtomicReference<>(null);

    private FirebaseFirestore RESTdb = FirebaseFirestore.getInstance();

    private final int LOCATION_REQUEST_CODE = 10001;

    private ArrayList<Location> coordinateArray = new ArrayList<>();
    private ArrayList<String> currentTimeArray = new ArrayList<>();

    // When user decides to go back to previous screen
    @Override
    public void onBackPressed()  {
        AlertDialog.Builder confirmQuit = new AlertDialog.Builder(this);
        confirmQuit
                .setTitle("Confirm end cycle?")
                .setMessage("Are you sure you want to terminate the current run routine? Do note that your progress will not be saved.")
                .setPositiveButton(
                        "YES",
                        (DialogInterface di, int i) -> {
                            super.onBackPressed(); //Quits the current activity
                        }
                )
                .setNegativeButton(
                        "NO",
                        (DialogInterface di, int i) -> {
                            di.dismiss();
                        }
                )
                .setCancelable(false);
        confirmQuit.create().show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        //Try to modify the background color of the
        //((CardView) findViewById(R.id.runningDistanceCard)).setCardBackgroundColor(null);


        AlertDialog.Builder confirmTerminateCycle = new AlertDialog.Builder(this);
        AlertDialog.Builder saveCycleData = new AlertDialog.Builder(this);

        // Get bundle
        Bundle bundle = getIntent().getExtras();
        String Email = bundle.getString("Email");
        String IPPTCycleID = bundle.getString("IPPTCycleId");
        String IPPTRoutineId = bundle.getString("IPPTRoutineId");

        // Build timer
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

        // Alert dialog to save data after stopping the timer
        saveCycleData
            .setTitle("Save data?")
            .setMessage("Are you sure you would like to save your data?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes",
                (DialogInterface di, int i) -> {
                    // Get timing
                    String capturedTiming = ((TextView) findViewById(R.id.timing_indicator_text)).getText().toString();
                    Log.d("ManagedToGetTiming", "Yes I have managed to get the timing off the textview");
                    int totalSeconds = Integer.parseInt(capturedTiming.split(":")[0]) * 60 + Integer.parseInt(capturedTiming.split(":")[1]);
                    addRunToDatabase(totalSeconds, Email, IPPTCycleID, IPPTRoutineId, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // finish activity
                            Toast.makeText(RunActivity.this, "Directing to workout page", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            )
            .setNegativeButton(
                "No",
                (DialogInterface di, int i) -> {
                    Intent recordBackIntent = new Intent();

                    setResult(Activity.RESULT_OK, recordBackIntent);
                    finish();
                }
            );

        // Alert dialog to terminate the timer
        confirmTerminateCycle
            .setTitle("Terminate run?")
            .setMessage("Are you sure you want to terminate this run?")
            .setPositiveButton(
                "Yes",
                (DialogInterface di, int i) -> {
                    //If the user presses yes THEN STOP THE TIMER
                    saveCycleData.create().show();
                }
            )
            .setNegativeButton(
                "No",
                (DialogInterface di, int i) -> {
                    // If user rejects terminating cycle
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
                            ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
                                Toast.makeText(RunActivity.this, "The timer has been paused", Toast.LENGTH_SHORT);
                                this.cancel();
                                confirmTerminateCycle.create().show();
                            });
                            this.start();
                        }
                    });
                    arcdt.get().start();
                }
            )
            .setCancelable(false);

        // Start timer
        ((LinearLayout) findViewById(R.id.startCycle)).setOnClickListener(andThenFunctionAs -> {
            mainStopwatch.start();
            ((LinearLayout) findViewById(R.id.startCycle)).setClickable(false);
            ((LinearLayout) findViewById(R.id.startCycle)).setEnabled(false);
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "Stopwatch has been activated", Toast.LENGTH_SHORT).show();
            // set onclicklistener for terminating run
            ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
                mainStopwatch.cancel();
                confirmTerminateCycle.create().show();
                Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "The timer has been paused", Toast.LENGTH_SHORT).show();
            });
        });

        // onclicklistener for stop timer
        ((LinearLayout) findViewById(R.id.terminateCycle)).setOnClickListener(andThenFunctio -> {
            Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "Timer has not started yet!", Toast.LENGTH_SHORT).show();
        });
    }

    // Firestore code
    public void addRunToDatabase(int totalSeconds, String EmailAddress, String IPPTCycleId, String IPPTRoutineId, OnCompleteListener<Void> onCompleteVoidListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> Run = new HashMap<>();
        Run.put("TimeTakenFinished", totalSeconds);

        db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .document(IPPTCycleId)
                .collection("IPPTRoutine")
                .document(IPPTRoutineId)
                .collection("IPPTRecord")
                .document("RunRecord")
                .set(Run)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RunActivity.this, "Run timing recorded!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RunActivity.this, "Error recording run timing", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(onCompleteVoidListener);
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

    //Add the location services thing over here....
    //This method will be responsible for ensuring that the current location of the device has been called and that the data has been successfully appended into the correct textfield within the application
    private void getCurrentLocation() throws SecurityException {
        Log.d("FunctionEntry", "Beginning to retrieve the required location results.....");
        //Some phone models may not have
        //check for operating system and device specs
        int currentOSAPILevel = 15;
        //Build.VERSION_CODES.KITKAT = 19 Build.VERSION_CODES.N = 24
        if (currentOSAPILevel >= Build.VERSION_CODES.KITKAT && currentOSAPILevel <= Build.VERSION_CODES.N){
            LocationRequest mLocationRequestOld = LocationRequest.create();
            mLocationRequestOld.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequestOld.setInterval(1200);
            mLocationRequestOld.setFastestInterval(1000);
        }
        else {
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(150);
            mLocationRequest.setFastestInterval(300);
            Log.d("TRUE/FALSE", "" + mLocationRequest.isFastestIntervalExplicitlySet());

            getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.d("ListenerRunning?", "Yes");
                    onLocationChanged(locationResult.getLastLocation());
                }
            }, Looper.myLooper());
        }
    }

    private class Convert {
        public void ToSexagesimals(String bearing){

        }
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        //Temporarily stop the timer when the user is in multitask mode
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Put the latitudes and longitudes into their respective TextViews
        ((TextView) findViewById(R.id.latitudeBox)).setText(Double.toString(location.getLatitude()));
        ((TextView) findViewById(R.id.longitudeBox)).setText(Double.toString(location.getLongitude()));
        Toast.makeText(sg.np.edu.mad.ipptready.RunActivity.this, "This method has been called and now the location is being updated at a speed between 150ms to 950ms", Toast.LENGTH_SHORT).show();
        coordinateArray.add(location); //stores an array of Location objects...
        Log.d("TAG", "" + coordinateArray.size());
        //Unfortunately you will have to calculate the distance inside this location listener lol
        for (Location l : coordinateArray){ //Location l refers to the actual Location object....Not the index!
            if (l != null){
                if (coordinateArray.size() - 2 >= 0) {
                    float[] resultContainer = new float[4];
                    //startLatitude, startLongitude, endLatitude, endLongitude, float[] results (parameters for distanceBetween() method under the Location class)
                    Location.distanceBetween(
                            coordinateArray.get(coordinateArray.size() - 2).getLatitude(),
                            coordinateArray.get(coordinateArray.size() - 2).getLongitude(),
                            coordinateArray.get(coordinateArray.size() - 1).getLatitude(),
                            coordinateArray.get(coordinateArray.size() - 1).getLongitude(),
                            resultContainer
                    );
                    for (float f : resultContainer) {
                        Log.d("WhatIsIt?", "" + new ArrayList<Float>(Arrays.asList(resultContainer[0], resultContainer[1], resultContainer[2], resultContainer[3])));
                    }
                    //Get from the kilometer one
                    String kilometerbox = ((TextView) findViewById(R.id.kilometerIndicator)).getText().toString();
                    String meterbox = ((TextView) findViewById(R.id.meterIndicator)).getText().toString();
                    float distanceCurrent = (Float.parseFloat(kilometerbox) * 1000) + (Float.parseFloat(meterbox) * 1000) + resultContainer[0];
                    //convert it back
                    ((TextView) findViewById(R.id.kilometerIndicator)).setText(Double.toString(Math.floor(distanceCurrent / 1000)));
                    ((TextView) findViewById(R.id.meterIndicator)).setText("");

                    // Get bundle
                    Bundle bundle = getIntent().getExtras();
                    String Email = bundle.getString("Email");
                    String IPPTCycleID = bundle.getString("IPPTCycleId");
                    String IPPTRoutineId = bundle.getString("IPPTRoutineId");

                    //Make the alert dialog box show up when the
                    AlertDialog.Builder reachedRequiredDistance = new AlertDialog.Builder(this);
                    reachedRequiredDistance
                            .setTitle("Reached 2.4 km!")
                            .setMessage("Congratulations! You have successfully covered a total distance of 2.4 kilometres")
                            .setPositiveButton(
                                    "YES",
                                    (DialogInterface di, int i) -> {
                                        //Record down the timing and pass it back to the record activity via an intent
                                        // Get timing
                                        String capturedTiming = ((TextView) findViewById(R.id.timing_indicator_text)).getText().toString();
                                        Log.d("ManagedToGetTiming", "Yes I have managed to get the timing off the textview");
                                        int totalSeconds = Integer.parseInt(capturedTiming.split(":")[0]) * 60 + Integer.parseInt(capturedTiming.split(":")[1]);
                                        addRunToDatabase(totalSeconds, Email, IPPTCycleID, IPPTRoutineId, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // finish activity
                                                Toast.makeText(RunActivity.this, "Directing to workout page", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    }
                            )
                            .setNegativeButton(
                                    "NO",
                                    (DialogInterface di, int i) -> {
                                        di.dismiss();
                                    }
                            )
                            .setCancelable(false);

                    if (((TextView) findViewById(R.id.kilometerIndicator)).getText().equals("2") && ((TextView) findViewById(R.id.meterIndicator)).getText().equals("00")){
                        arcdt.get().cancel(); //Cancel the timer as in "stop" the timer
                        reachedRequiredDistance.create().show();
                        Toast.makeText(this, "The timer has been paused", Toast.LENGTH_SHORT).show();
                        String capturedTiming = ((TextView) findViewById(R.id.timing_indicator_text)).getText().toString();
                    }
                }
            }
        }
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Prompt the user to
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d("SDLJF", "An alert dialog should show up prompting the user to allow this application to access location services");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            else {
                Log.d("IsThisSleeping?", "No");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
        else {
            Log.d("ElseStatement", "CodeReachedHere");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Sleeping1?", "No");
        if (requestCode == LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Permssion has been granted
                getCurrentLocation();
                Log.d("PermissionGranted", "Permission has been granted");
            }
            else{
                //Permission is not granted
                checkPermissions();
            }
        }
    }

    //This is the thing is able to
}