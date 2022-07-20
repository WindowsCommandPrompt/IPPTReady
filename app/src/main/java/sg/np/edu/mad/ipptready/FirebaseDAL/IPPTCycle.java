package sg.np.edu.mad.ipptready.FirebaseDAL;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.type.DateTime;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IPPTCycle implements Serializable {
    public static final String colFrom = "IPPTCycle";
    private static final String NAME = "Name";
    private static final String DATE_CREATED = "DateCreated";
    private static final String IS_FINISHED = "isFinished";

    public String Name;
    public Date DateCreated;
    public boolean isFinished;

    public IPPTCycle(Map<String, Object> cycleMap) {
        Name = (String) cycleMap.get(NAME);
        DateCreated = ((Timestamp)cycleMap.get(DATE_CREATED)).toDate();
        isFinished = cycleMap.containsKey(IS_FINISHED) ? (boolean) cycleMap.get(IS_FINISHED)
                : false;
    }

    public IPPTCycle(String Name, Date DateCreated) {
        this.Name = Name;
        this.DateCreated = DateCreated;
        this.isFinished = false;
    }

    public static DocumentReference getCycleDocFromId(DocumentReference userDocRef,
                                                      String cycleDocId) {
        return userDocRef.collection(colFrom).document(cycleDocId);
    }

    public static Task<QuerySnapshot> getCyclesFromUser(DocumentReference userDocRef) {
        return userDocRef.collection(colFrom)
                .get();
    }

    public static FirebaseDocChange createNewCycle(DocumentReference userDocRef,
                                                   String Name,
                                                  Date DateCreated) {
        FirebaseDocChange newCycle = new FirebaseDocChange();
        newCycle.documentReference = userDocRef.collection(colFrom)
                .document();

        Map<String, Object> newCycleMap = new HashMap<>();
        newCycleMap.put(NAME, Name);
        newCycleMap.put(DATE_CREATED, DateCreated);

        newCycle.changeTask = newCycle.documentReference
                .set(newCycleMap);
        return newCycle;
    }

    public static Task<Void> updateCycle(DocumentReference cycleDocRef,
                                        String Name) {
        Map<String, Object> updatedCycleMap = new HashMap<>();
        if (null != Name) {
            updatedCycleMap.put(NAME, Name);
        }
        return cycleDocRef.set(updatedCycleMap, SetOptions.merge());
    }

    public static Task<Void> finishCycle(DocumentReference cycleDocRef) {
        Map<String, Object> finishedCycleMap = new HashMap<>();
        finishedCycleMap.put(IS_FINISHED, true);
        return cycleDocRef.set(finishedCycleMap, SetOptions.merge());
    }
}
