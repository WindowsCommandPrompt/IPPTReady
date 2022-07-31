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
    ArrayList<String> names = new ArrayList<>();
    String hostUserName;
    Context ctx;

    public ExerciseTogetherWaitingRoomAdapter(ArrayList<String> Names, String HostUserName, Context Ctx) {
        names = Names;
        hostUserName = HostUserName;
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
            holder.sessionParticipantNameTextView.setTextColor(ContextCompat.getColor(ctx, R.color.priblue));
            holder.sessionParticipantNameTextView.setText("(Host) - " + names.get(position));
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
