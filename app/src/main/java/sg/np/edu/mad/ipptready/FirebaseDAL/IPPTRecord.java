package sg.np.edu.mad.ipptready.FirebaseDAL;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class IPPTRecord {
    public static final String colFrom = "IPPTRecord";
    public static final String PUSHUP_RECORD = "PushupRecord";
    public static final String RUN_RECORD = "RunRecord";
    public static final String SITUP_RECORD = "SitupRecord";

    public static final String NUMREPS = "NumsReps";
    public static final String REPS_TARGET = "RepsTarget";
    public static final String TIME_TAKEN_FINISHED = "TimeTakenFinished";

    public static Task<QuerySnapshot> getRecordFromRoutine(DocumentReference routineDocRef) {
        return routineDocRef.collection(colFrom)
                .get();
    }

    public static FirebaseDocChange addPushupRecord(DocumentReference routineDocRef,
                                                    int NumsReps,
                                                    int RepsTarget) {
        FirebaseDocChange newPushup = new FirebaseDocChange();
        newPushup.documentReference = routineDocRef.collection(colFrom)
                .document(PUSHUP_RECORD);

        Map<String, Object> newPushupMap = new HashMap<>();
        newPushupMap.put(NUMREPS, NumsReps);
        newPushupMap.put(REPS_TARGET, RepsTarget);

        newPushup.changeTask = newPushup.documentReference.set(newPushupMap);
        return newPushup;
    }

    public static FirebaseDocChange addRunRecord(DocumentReference routineDocRef,
                                               int TimeTakenFinished) {
        FirebaseDocChange newRun = new FirebaseDocChange();
        newRun.documentReference = routineDocRef.collection(colFrom)
                .document(RUN_RECORD);

        Map<String, Object> newRunMap = new HashMap<>();
        newRunMap.put(TIME_TAKEN_FINISHED, TimeTakenFinished);

        newRun.changeTask = newRun.changeTask = newRun.documentReference.set(newRunMap);
        return newRun;

    }

    public static FirebaseDocChange addSitupRecord(DocumentReference routineDocRef,
                                                   int NumsReps,
                                                   int RepsTarget) {
        FirebaseDocChange newSitup = new FirebaseDocChange();
        newSitup.documentReference = routineDocRef.collection(colFrom)
                .document(SITUP_RECORD);

        Map<String, Object> newSitupMap = new HashMap<>();
        newSitupMap.put(NUMREPS, NumsReps);
        newSitupMap.put(REPS_TARGET, RepsTarget);

        newSitup.changeTask = newSitup.documentReference.set(newSitupMap);
        return newSitup;
    }
}