package sg.np.edu.mad.ipptready.FirebaseDAL;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.type.DateTime;

import java.util.HashMap;
import java.util.Map;

public class IPPTCycle {
    public static final String colFrom = "IPPTCycle";
    public static final String NAME = "Name";
    public static final String DATE_CREATED = "DOB";

    public static Task<QuerySnapshot> getCyclesFromUser(DocumentReference userDocRef) {
        return userDocRef.collection(colFrom)
                .get();
    }

    public static FirebaseDocChange createNewCycle(DocumentReference userDocRef,
                                                   String Name,
                                                  DateTime DateCreated) {
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

    public static Task<Void> deleteCycle(DocumentReference cycleDocRef) {
        return cycleDocRef.delete();
    }
}
