package sg.np.edu.mad.ipptready.FirebaseDAL;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.type.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IPPTRoutine {
    private static final String colFrom = "IPPTRoutine";
    private static final String DATE_CREATED = "DateCreated";
    private static final String IPPTSCORE = "IPPTScore";

    public Date DateCreated;
    public long IPPTScore;

    public IPPTRoutine(Map<String, Object> routineMap) {
        DateCreated = ((Timestamp)routineMap.get(DATE_CREATED)).toDate();
        IPPTScore = (null != routineMap.get(IPPTSCORE)) ? (long)routineMap.get(IPPTSCORE)
                : -1;
    }

    public IPPTRoutine(Date DateCreated) {
        this.DateCreated = DateCreated;
        this.IPPTScore = -1;
    }

    public static DocumentReference getRoutineDocFromId(DocumentReference cycleDocRef,
                                                      String routineDocId) {
        return cycleDocRef.collection(colFrom).document(routineDocId);
    }

    public static Task<QuerySnapshot> getRoutinesFromCycle(DocumentReference cycleDocRef) {
        return cycleDocRef.collection(colFrom)
                .get();
    }

    public static FirebaseDocChange createNewRoutine(DocumentReference cycleDocRef,
                                                     Date DateCreated) {
        FirebaseDocChange newRoutine = new FirebaseDocChange();

        newRoutine.documentReference = cycleDocRef.collection(colFrom)
                .document();

        Map<String, Object> newRoutineMap = new HashMap<>();
        newRoutineMap.put(DATE_CREATED, DateCreated);

        newRoutine.changeTask = newRoutine.documentReference
                .set(newRoutineMap);
        return newRoutine;
    }

    public static Task<Void> RoutineAddScore(DocumentReference routineDocRef,
                                           long ipptScore) {
        Map<String, Object> updateRoutineMap = new HashMap<>();
        updateRoutineMap.put(IPPTSCORE, ipptScore);

        return routineDocRef.set(updateRoutineMap, SetOptions.merge());
    }
}
