package sg.np.edu.mad.ipptready;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IPPTCycleAdapter extends RecyclerView.Adapter<IPPTCycleViewHolder> {
    public List<IPPTCycle> ipptCycleList;

    public IPPTCycleAdapter(List<IPPTCycle> ipptCycleList) {
        this.ipptCycleList = ipptCycleList;
    }

    @NonNull
    @Override
    public IPPTCycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cycleView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cycle_recycleview,
                        parent,
                        false);
        return new IPPTCycleViewHolder(cycleView);
    }

    @Override
    public void onBindViewHolder(@NonNull IPPTCycleViewHolder holder, int position) {
        IPPTCycle ipptCycle = ipptCycleList.get(position);
        holder.nameTextView.setText(ipptCycle.Name);
        holder.DateCreatedTextView.setText(ipptCycle.DateCreated.toString());
    }

    @Override
    public int getItemCount() {
        return ipptCycleList.size();
    }
}
