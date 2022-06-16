package sg.np.edu.mad.ipptready;


import android.app.Activity;
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

public class IPPTRoutineAdapter extends RecyclerView.Adapter<IPPTRoutineViewHolder> {
    private List<IPPTRoutine> ipptRoutineList;
    private Context ipptRoutineContext;
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public IPPTRoutineAdapter(List<IPPTRoutine> ipptRoutineList,
                              Context currentContext) {
        this.ipptRoutineList = ipptRoutineList;
        this.ipptRoutineContext = currentContext;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recordIntent = new Intent(IPPTRoutineAdapter.this.ipptRoutineContext, RecordActivity.class);



                IPPTRoutineAdapter.this.ipptRoutineContext.startActivity(recordIntent);
            }
        });

        holder.ipptScoreTextView.setText(String.valueOf(ipptRoutine.IPPTScore));
        holder.DateCreatedTextView.setText(dateFormat.format(ipptRoutine.DateCreated));
    }

    @Override
    public int getItemCount() {
        return ipptRoutineList.size();
    }
}
