package sg.np.edu.mad.ipptready;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import java.util.List;

public class IPPTCycleAdapter extends RecyclerView.Adapter<IPPTCycleViewHolder> {
    private List<IPPTCycle> ipptCycleList;
    private Context ipptCycleContext;
    private String EmailAddress;

    public IPPTCycleAdapter(List<IPPTCycle> ipptCycleList,
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
        IPPTCycle ipptCycle = ipptCycleList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent routineIntent = new Intent(ipptCycleContext, RoutineActivity.class);
                routineIntent.putExtra("Email", EmailAddress);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("IPPTUser")
                        .document(EmailAddress)
                        .collection("IPPTCycle")
                        .whereEqualTo("Name", ipptCycle.Name)
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
                                            oos.writeObject(ipptCycle);
                                            routineIntent.putExtra("IPPTCycle", bos.toByteArray());
                                        } catch (IOException e) {
                                            // If error occurred, display friendly message to user

                                            Toast.makeText(IPPTCycleAdapter.this.ipptCycleContext, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                            return;
                                        }
                                        routineIntent.putExtra("IPPTCycleId", documentSnapshot.getId());

                                        IPPTCycleAdapter.this.ipptCycleContext.startActivity(routineIntent);
                                    }
                                }
                            }
                        });
            }
        });

        holder.nameTextView.setText(ipptCycle.Name);
        holder.DateCreatedTextView.setText(ipptCycle.DateCreated.toString());
    }

    @Override
    public int getItemCount() {
        return ipptCycleList.size();
    }
}
