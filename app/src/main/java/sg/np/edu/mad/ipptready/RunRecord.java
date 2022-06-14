package sg.np.edu.mad.ipptready;

import java.util.Date;

public class RunRecord implements IPPTRecord {

    // Total distance taken (can exceed 2.4km)
    public float TotalDistanceTravelled;

    // time taken to finish the entire total distance in seconds
    public int TimeTakenTotal;

    // time taken to finsih the 2.4km
    public int TimeTakenFinished;

    @Override
    public int getIPPTRecordScore() {
        return 0;
    }
}
