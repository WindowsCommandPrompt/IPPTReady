package sg.np.edu.mad.ipptready;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IPPTCycle implements Serializable {
    public Date DateCreated;
    public String Name;

    // for determining which IPPTCycle goes into the RecycleView
    public boolean isFinished;

    // constructor for testing and debugging purposes in CycleActivity
    public IPPTCycle(String Name,
                     Date DateCreated) {
        this.Name = Name;
        this.DateCreated = DateCreated;
        this.isFinished = true;
    }

    public IPPTCycle() { }

    public void getRoutineList(String EmailAddress,
                          String IPPTCycleId,
                          OnCompleteListener<QuerySnapshot> onCompleteQuerySnapshotListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .document(IPPTCycleId)
                .collection("IPPTRoutine")
                .get()
                .addOnCompleteListener(onCompleteQuerySnapshotListener);
    }

    public void addNewIPPTRoutineToDatabase(String EmailAddress,
                                            IPPTRoutine ipptRoutine,
                                            OnCompleteListener<Void> onCompleteVoidListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .whereEqualTo("Name", this.Name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                QueryDocumentSnapshot document = task.getResult().iterator().next();

                                Map<String, Object> Dat = new HashMap<>();
                                Dat.put("DateCreated", ipptRoutine.DateCreated);
                                Dat.put("IPPTScore", ipptRoutine.IPPTScore);
                                Dat.put("isFinished", ipptRoutine.isFinished);

                                db.collection("IPPTUser")
                                        .document(EmailAddress)
                                        .collection("IPPTCycle")
                                        .document(document.getId())
                                        .collection("IPPTRoutine")
                                        .document()
                                        .set(Dat)
                                        .addOnCompleteListener(onCompleteVoidListener);
                            }
                        }
                    }
                });

    }

    public void completeIPPTCycle(String EmailAddress,
                                  OnCompleteListener<Void> onCompleteVoidListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .whereEqualTo("Name", this.Name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                QueryDocumentSnapshot document = task.getResult().iterator().next();

                                Map<String, Object> mergeDat = new HashMap<>();
                                mergeDat.put("isFinished", true);
                                IPPTCycle.this.isFinished = true;

                                db.collection("IPPTUser")
                                        .document(EmailAddress)
                                        .collection("IPPTCycle")
                                        .document(document.getId())
                                        .set(mergeDat, SetOptions.merge())
                                        .addOnCompleteListener(onCompleteVoidListener);
                            }
                        }
                    }
                });
    }
}
