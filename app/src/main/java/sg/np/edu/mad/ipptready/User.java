package sg.np.edu.mad.ipptready;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    public Date DOB;
    public String Name;

    public void getCyclesList(String EmailAddress,
            OnCompleteListener<QuerySnapshot> onCompleteQuerySnapshotListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle")
                .get()
                .addOnCompleteListener(onCompleteQuerySnapshotListener);
    }
}