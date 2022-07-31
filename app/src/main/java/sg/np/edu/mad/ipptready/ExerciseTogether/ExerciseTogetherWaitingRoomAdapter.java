package sg.np.edu.mad.ipptready.ExerciseTogether;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherWaitingRoomAdapter extends RecyclerView.Adapter<ExerciseTogetherWaitingRoomViewHolder> {
    // Exercise Together feature done by: BRYAN KOH

    // Global variables
    ArrayList<String> names = new ArrayList<>();
    String hostUserName;
    Context ctx;
    Boolean started;

    public ExerciseTogetherWaitingRoomAdapter(ArrayList<String> Names, String HostUserName, boolean Started, Context Ctx) {
        names = Names;
        hostUserName = HostUserName;
        started = Started;
        ctx = Ctx;
    }

    @NonNull
    @Override
    public ExerciseTogetherWaitingRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cycleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercisetogether_recycler, parent, false);
        return new ExerciseTogetherWaitingRoomViewHolder(cycleView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseTogetherWaitingRoomViewHolder holder, int position) {
        if (names.get(position).equals(hostUserName))
        {
            // For host, if he/she started his exercise, indicate on recyclerview that user has started the exercise
            if (started)
            {
                holder.sessionParticipantNameTextView.setTextColor(ContextCompat.getColor(ctx, R.color.success));
                holder.sessionParticipantNameTextView.setText("(Host has started!) - " + names.get(position));
            }
            else
            {
                holder.sessionParticipantNameTextView.setTextColor(ContextCompat.getColor(ctx, R.color.priblue));
                holder.sessionParticipantNameTextView.setText("(Host) - " + names.get(position));
            }
        }
        else
        {
            holder.sessionParticipantNameTextView.setTextColor(ContextCompat.getColor(ctx, R.color.black));
            holder.sessionParticipantNameTextView.setText(names.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
