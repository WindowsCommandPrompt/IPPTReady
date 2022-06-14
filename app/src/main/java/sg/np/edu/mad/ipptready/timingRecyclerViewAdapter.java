package sg.np.edu.mad.ipptready;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class timingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public ArrayList<String> timings;
    public Context context;

    public timingRecyclerViewAdapter(Context context, ArrayList<String> timings){
        this.context = context;
        this.timings = timings;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.timing_viewholder, parent, false);
        return new timingRecordInflater(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //bind the data to the viewholder
        timingRecordInflater a = (timingRecordInflater) holder;
        a.timing.setText(timings.get(position));
        a.idNumber.setText(Integer.toString(position));
    }

    @Override
    public int getItemCount() {
        return timings.size();
    }
}
