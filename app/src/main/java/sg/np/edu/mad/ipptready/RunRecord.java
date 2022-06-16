package sg.np.edu.mad.ipptready;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import com.google.firebase.firestore.*;

public class RunRecord extends AppCompatActivity implements IPPTRecord {

    // Total distance taken (can exceed 2.4km)
    public float TotalDistanceTravelled;

    // time taken to finish the entire total distance in seconds
    public int TimeTakenTotal;

    // time taken to finsih the 2.4km
    public int TimeTakenFinished;

    public RunRecord(int timeTakenTotal){
        this.TimeTakenTotal = timeTakenTotal;
    }

    @Override
    public int getIPPTRecordScore() {
        return 0;
    }
}
