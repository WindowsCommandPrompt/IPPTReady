package sg.np.edu.mad.ipptready.ExerciseTogether;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherWaitingRoomAdapter extends RecyclerView.Adapter<ExerciseTogetherWaitingRoomViewHolder> {
    ArrayList<String> names = new ArrayList<>();

    public ExerciseTogetherWaitingRoomAdapter(ArrayList<String> Names) {
        names = Names;
    }

    @NonNull
    @Override
    public ExerciseTogetherWaitingRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cycleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercisetogether_recycler, parent, false);
        return new ExerciseTogetherWaitingRoomViewHolder(cycleView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseTogetherWaitingRoomViewHolder holder, int position) {
        holder.sessionParticipantNameTextView.setText(names.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
