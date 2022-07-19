package sg.np.edu.mad.ipptready.Cycle;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sg.np.edu.mad.ipptready.R;

public class IPPTCycleViewHolder extends RecyclerView.ViewHolder {
    protected TextView nameTextView;
    protected TextView DateCreatedTextView;

    public IPPTCycleViewHolder(View v) {
        super(v);
        nameTextView = v.findViewById(R.id.cycleName);
        DateCreatedTextView = v.findViewById(R.id.cycleDateCreated);
    }
}
