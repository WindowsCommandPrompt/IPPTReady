package sg.np.edu.mad.ipptready;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class IPPTCycle {
    public String Name;
    Date DateCreated;

    public void getRoutineList(String EmailAddress,
                          OnCompleteListener<QuerySnapshot> onCompleteQuerySnapshotListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .document(Name)
                .collection("IPPTRoutine")
                .get()
                .addOnCompleteListener(onCompleteQuerySnapshotListener);
    }
}
