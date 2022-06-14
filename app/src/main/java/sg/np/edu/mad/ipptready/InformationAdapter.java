package sg.np.edu.mad.ipptready;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InformationAdapter extends RecyclerView.Adapter<InformationViewHolder> {

    String data1[], data2[];
    int images[];
    Context context;

    public InformationAdapter(Context ct, String s1[], String s2[], int img[]){
        context = ct;
        data1 = s1;
        data2 = s2;
        images = img;
    }

    @NonNull
    @Override
    public InformationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.information_row, parent, false);
        return new InformationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InformationViewHolder holder, int position) {
        holder.infoText1.setText(data1[position]);
        holder.infoText2.setText(data2[position]);
        holder.infoImage.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return data1.length;
    }
}
