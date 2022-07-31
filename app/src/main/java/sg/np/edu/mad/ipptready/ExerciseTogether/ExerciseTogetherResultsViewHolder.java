package sg.np.edu.mad.ipptready.ExerciseTogether;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherResultsViewHolder extends RecyclerView.ViewHolder{
    // Exercise Together feature done by: BRYAN KOH

    protected TextView sessionParticipantNameResult;
    protected TextView sessionParticipantResultResult;

    public ExerciseTogetherResultsViewHolder(View v) {
        super(v);
        sessionParticipantNameResult = v.findViewById(R.id.sessionParticipantNameResult);
        sessionParticipantResultResult = v.findViewById(R.id.sessionParticipantResultResult);
    }
}