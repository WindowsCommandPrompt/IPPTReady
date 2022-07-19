package sg.np.edu.mad.ipptready.Cycle;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import sg.np.edu.mad.ipptready.R;
import sg.np.edu.mad.ipptready.RoutineActivity;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTCycle;
import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseViewItem;

public class IPPTCycleAdapter extends RecyclerView.Adapter<IPPTCycleViewHolder> {
    private List<FirebaseViewItem<IPPTCycle>> ipptCycleList;
    private Context ipptCycleContext;
    private String EmailAddress;
    private static DateFormat  dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public IPPTCycleAdapter(List<FirebaseViewItem<IPPTCycle>> ipptCycleList,
                            Context currentContext,
                            String EmailAddress) {
        this.ipptCycleList = ipptCycleList;
        this.ipptCycleContext = currentContext;
        this.EmailAddress = EmailAddress;
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
        FirebaseViewItem<IPPTCycle> ipptCycle = ipptCycleList.get(position);
        // set view item's onclicklistener to go to corresponding cycle
        holder.itemView.setOnClickListener(new CycleActivity.RoutineOnClickListener(ipptCycleContext, ipptCycle));

        holder.nameTextView.setText(ipptCycle.viewItem.Name);
        holder.DateCreatedTextView.setText(dateFormat.format(ipptCycle.viewItem.DateCreated));
    }

    @Override
    public int getItemCount() {
        return ipptCycleList.size();
    }
}