package sg.np.edu.mad.ipptready;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class IPPTRoutine {
    public Date DateStarted;

    public void getRecordsList(String EmailAddress,
                          String IPPTCycleName,
                          OnCompleteListener<DocumentSnapshot> onCompleteDocumentSnapshotListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .document(IPPTCycleName)
                .collection("IPPTRoutine")
                .document(DateStarted.toString())
                .get()
                .addOnCompleteListener(onCompleteDocumentSnapshotListener);
    }
}
