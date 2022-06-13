package sg.np.edu.mad.ipptready;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class IPPTRoutineViewHolder extends RecyclerView.ViewHolder {
    protected TextView ipptScoreTextView;
    protected TextView DateCreatedTextView;

    public IPPTRoutineViewHolder(View v) {
        super(v);
        ipptScoreTextView = v.findViewById(R.id.routineipptScore);
        DateCreatedTextView = v.findViewById(R.id.routineDateCreated);
    }
}
