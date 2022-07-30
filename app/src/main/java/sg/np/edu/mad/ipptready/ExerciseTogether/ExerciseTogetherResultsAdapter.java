package sg.np.edu.mad.ipptready.ExerciseTogether;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.np.edu.mad.ipptready.R;

public class ExerciseTogetherResultsAdapter extends RecyclerView.Adapter<ExerciseTogetherResultsViewHolder>{
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> results = new ArrayList<>();

    public ExerciseTogetherResultsAdapter(ArrayList<String> Names, ArrayList<String> Results) { names = Names; results = Results;}

    @NonNull
    @Override
    public ExerciseTogetherResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cycleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercisetogetherresults_recycler, parent, false);
        return new ExerciseTogetherResultsViewHolder(cycleView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseTogetherResultsViewHolder holder, int position) {
        holder.sessionParticipantNameResult.setText(names.get(position));
        holder.sessionParticipantResultResult.setText(results.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}