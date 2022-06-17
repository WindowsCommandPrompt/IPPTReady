package sg.np.edu.mad.ipptready;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    // sends new IPPTCycle to Database under the user
    public void addNewIPPTCycleToDatabase(String EmailAddress,
                                          IPPTCycle ipptCycle,
                                             OnCompleteListener<Void> onCompleteVoidListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("IPPTUser")
                .document(EmailAddress)
                .collection("IPPTCycle");

        // Create and add data to Map Object to set the data in the database
        Map<String, Object> dat = new HashMap<>();
        dat.put("Name", ipptCycle.Name);
        dat.put("DateCreated", ipptCycle.DateCreated);
        dat.put("isFinished", false);
        colRef.document()
                .set(dat)
                .addOnCompleteListener(onCompleteVoidListener);
    }
}