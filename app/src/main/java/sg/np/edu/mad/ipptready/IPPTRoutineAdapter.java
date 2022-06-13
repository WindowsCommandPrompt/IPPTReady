package sg.np.edu.mad.ipptready;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IPPTRoutineAdapter extends RecyclerView.Adapter<IPPTRoutineViewHolder> {
    public List<IPPTRoutine> ipptRoutineList;

    public IPPTRoutineAdapter(List<IPPTRoutine> ipptRoutineList) {
        this.ipptRoutineList = ipptRoutineList;
    }

    @NonNull
    @Override
    public IPPTRoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cycleView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.routine_recycleview,
                        parent,
                        false);
        return new IPPTRoutineViewHolder(cycleView);
    }

    @Override
    public void onBindViewHolder(@NonNull IPPTRoutineViewHolder holder, int position) {
        IPPTRoutine ipptRoutine = ipptRoutineList.get(position);
        holder.ipptScoreTextView.setText(String.valueOf(ipptRoutine.IPPTScore));
        holder.DateCreatedTextView.setText(ipptRoutine.DateCreated.toString());
    }

    @Override
    public int getItemCount() {
        return ipptRoutineList.size();
    }
}
