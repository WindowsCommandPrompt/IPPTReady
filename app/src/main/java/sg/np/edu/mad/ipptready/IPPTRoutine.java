package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IPPTRoutine implements Serializable {
    public Date DateCreated;
    public int IPPTScore;

    // for determining which IPPTRoutine goes into the RecyclerView
    public boolean isFinished;

    public void getRecordsList(String EmailAddress,
                          String IPPTCycleId,
                          OnCompleteListener<QuerySnapshot> onCompleteQuerySnapshotListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .document(IPPTCycleId)
                .collection("IPPTRoutine")
                .whereEqualTo("DateCreated", DateCreated)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                String documentId = task.getResult().iterator().next().getId();

                                db.collection("IPPTUser")
                                        .document(EmailAddress)
                                        .collection("IPPTCycle")
                                        .document(IPPTCycleId)
                                        .collection("IPPTRoutine")
                                        .document(documentId)
                                        .collection("IPPTRecord")
                                        .get()
                                        .addOnCompleteListener(onCompleteQuerySnapshotListener);
                            }
                        }
                    }
                });
    }

    public void completeIPPTRoutine(String EmailAddress,
                                    String IPPTCycleId,
                                    OnCompleteListener<Void> onCompleteVoidListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .document(IPPTCycleId)
                .collection("IPPTRoutine")
                .whereEqualTo("DateCreated", DateCreated)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                QueryDocumentSnapshot document = task.getResult().iterator().next();

                                db.collection("IPPTUser")
                                        .document(EmailAddress)
                                        .collection("IPPTCycle")
                                        .document(IPPTCycleId)
                                        .collection("IPPTRoutine")
                                        .document(document.getId())
                                        .collection("IPPTRecord")
                                        .whereEqualTo("isCompleted", false)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (task.getResult().isEmpty()) {
                                                        Map<String, Object> mergeDat = new HashMap<>();
                                                        mergeDat.put("isFinished", true);
                                                        IPPTRoutine.this.isFinished = true;

                                                        db.collection("IPPTUser")
                                                                .document(EmailAddress)
                                                                .collection("IPPTCycle")
                                                                .document(IPPTCycleId)
                                                                .collection("IPPTRoutine")
                                                                .document(document.getId())
                                                                .set(mergeDat, SetOptions.merge())
                                                                .addOnCompleteListener(onCompleteVoidListener);
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}