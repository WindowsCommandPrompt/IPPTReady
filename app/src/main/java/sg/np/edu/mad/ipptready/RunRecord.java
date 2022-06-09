package sg.np.edu.mad.ipptready;

import java.util.Date;

public class RunRecord extends IPPTRecord {
    public float TotalDistanceTravelled;
    public Date TimeTakenTotal;
    public Date TimeFinished;

    @Override
    public String getName() { return "2.4km"; }
}
