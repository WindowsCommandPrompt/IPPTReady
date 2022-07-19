package sg.np.edu.mad.ipptready.Routine;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import sg.np.edu.mad.ipptready.FirebaseDAL.FirebaseViewItem;
import sg.np.edu.mad.ipptready.FirebaseDAL.IPPTRoutine;
import sg.np.edu.mad.ipptready.R;
import sg.np.edu.mad.ipptready.RecordActivity;

public class IPPTRoutineAdapter extends RecyclerView.Adapter<IPPTRoutineViewHolder> {
    private List<FirebaseViewItem<IPPTRoutine>> ipptRoutineList;
    private Context context;
    private String userId;
    private String cycleId;
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public IPPTRoutineAdapter(List<FirebaseViewItem<IPPTRoutine>> ipptRoutineList,
                              Context context,
                              String userId,
                              String cycleId) {
        this.ipptRoutineList = ipptRoutineList;
        this.context = context;
        this.userId = userId;
        this.cycleId = cycleId;
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
        FirebaseViewItem<IPPTRoutine> ipptRoutineViewItem = ipptRoutineList.get(position);


        holder.ipptScoreTextView.setText(String.valueOf(ipptRoutineViewItem.viewItem.IPPTScore));
        holder.DateCreatedTextView.setText(dateFormat.format(ipptRoutineViewItem.viewItem.DateCreated));
    }

    @Override
    public int getItemCount() {
        return ipptRoutineList.size();
    }
}
