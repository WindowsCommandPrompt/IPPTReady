package sg.np.edu.mad.ipptready;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class timingRecordInflater extends RecyclerView.ViewHolder{
    TextView idNumber, timing;
    public timingRecordInflater(@NonNull View itemView) {
        super(itemView);
        idNumber = itemView.findViewById(R.id.idNumber);
        timing = itemView.findViewById(R.id.timing);
    }
}
