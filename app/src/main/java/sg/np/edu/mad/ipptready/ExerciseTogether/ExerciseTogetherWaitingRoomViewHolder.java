package sg.np.edu.mad.ipptready.ExerciseTogether;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherWaitingRoomViewHolder extends RecyclerView.ViewHolder {
    // Exercise Together feature done by: BRYAN KOH

    protected TextView sessionParticipantNameTextView;

    public ExerciseTogetherWaitingRoomViewHolder(View v) {
        super(v);
        sessionParticipantNameTextView = v.findViewById(R.id.sessionParticipantName);
    }
}