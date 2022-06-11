package sg.np.edu.mad.ipptready;

import java.util.Date;

public class RunRecord extends IPPTRecord {

    // Total distance taken (can exceed 2.4km)
    public float TotalDistanceTravelled;

    // Date and time that the run record ends.
    public Date TimeTakenTotal;

    // Date and time that 2.4km is reached.
    public Date TimeFinished;

    @Override
    public String getName() { return "2.4km"; }
}
