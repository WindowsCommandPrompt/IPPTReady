package sg.np.edu.mad.ipptready;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class IPPTRoutineAdapter extends RecyclerView.Adapter<IPPTRoutineViewHolder> {
    private List<IPPTRoutine> ipptRoutineList;
    private Context ipptRoutineContext;
    private String EmailAddress,
        IPPTCycleId;
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public IPPTRoutineAdapter(List<IPPTRoutine> ipptRoutineList,
                              Context currentContext,
                              String EmailAddress,
                              String IPPTCycleId) {
        this.ipptRoutineList = ipptRoutineList;
        this.ipptRoutineContext = currentContext;
        this.EmailAddress = EmailAddress;
        this.IPPTCycleId = IPPTCycleId;
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

                recordIntent.putExtra("Email", EmailAddress);
                recordIntent.putExtra("IPPTCycleId", IPPTCycleId);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("IPPTUser")
                        .document(EmailAddress)
                        .collection("IPPTCycle")
                        .document(IPPTCycleId)
                        .collection("IPPTRoutine")
                        .whereEqualTo("DateCreated", ipptRoutine.DateCreated)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult().iterator().next();
                                    if (documentSnapshot.exists()) {
                                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                        try {
                                            ObjectOutputStream oos = new ObjectOutputStream(bos);
                                            oos.writeObject(ipptRoutine);
                                            recordIntent.putExtra("IPPTRoutine", bos.toByteArray());
                                        } catch (IOException e) {
                                            // If error occurred, display friendly message to user

                                            Toast.makeText(IPPTRoutineAdapter.this.ipptRoutineContext, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                            return;
                                        }
                                        recordIntent.putExtra("IPPTRoutineId", documentSnapshot.getId());

                                        IPPTRoutineAdapter.this.ipptRoutineContext.startActivity(recordIntent);
                                    }
                                }
                            }
                        });
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
