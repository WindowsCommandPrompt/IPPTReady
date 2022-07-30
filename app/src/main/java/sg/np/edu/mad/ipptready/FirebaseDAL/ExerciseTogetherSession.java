package sg.np.edu.mad.ipptready.FirebaseDAL;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExerciseTogetherSession {
    public String dateCreated;
    public String dateJoined;
    public String sessionName;
    public String exercise;
    public String status = "Created";
    public String hostUserID;

    public ExerciseTogetherSession(String DateCreated, String SessionName, String Exercise, String HostUserID) {
        if (DateCreated.equals("")) dateCreated = getDate(); else dateCreated = DateCreated;
        dateJoined = getDate();
        sessionName = SessionName;
        exercise = Exercise;
        hostUserID = HostUserID;
    }

    public static String getDate() {
        Date d = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM yyyy h:mm:ss a");
        return dateFormatter.format(d);
    }

    public static CollectionReference getSessionsbyUserID(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("IPPTUser").document(userID).collection("Exercise Together");
    }

    public static CollectionReference getCurrentSessionParticipants(String qrCode)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("Exercise Together").document(qrCode).collection("Users");
    }

    public static FirebaseDocChange createNewSession(String EmailAddress, ExerciseTogetherSession session) {
        FirebaseDocChange newSession = new FirebaseDocChange();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        newSession.documentReference = db.collection("IPPTUser").document(EmailAddress).collection("Exercise Together").document(session.dateCreated);

        Map<String, Object> newSessionMap = new HashMap<>();
        newSessionMap.put("dateCreated", session.dateCreated);
        newSessionMap.put("dateJoined", session.dateJoined);
        newSessionMap.put("exercise", session.exercise);
        newSessionMap.put("sessionName", session.sessionName);
        newSessionMap.put("status", session.status);
        newSessionMap.put("hostUserID", session.hostUserID);

        newSession.changeTask = newSession.documentReference.set(newSessionMap);
        return newSession;
    }

    public static FirebaseDocChange joinSession(String userid, String qrCode)
    {
        FirebaseDocChange joinASession = new FirebaseDocChange();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        joinASession.documentReference = db.collection("Exercise Together").document(qrCode).collection("Users").document(userid);

        Map<String, Object> newJoinSessionMap = new HashMap<>();
        newJoinSessionMap.put("status", "Joined");

        joinASession.changeTask = joinASession.documentReference.set(newJoinSessionMap);
        return joinASession;
    }

    public static FirebaseDocChange updateJoinStatus(String userid, String qrCode, String status)
    {
        FirebaseDocChange updateTheJoinStatus = new FirebaseDocChange();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        updateTheJoinStatus.documentReference = db.collection("Exercise Together").document(qrCode).collection("Users").document(userid);

        Map<String, Object> updateJoinSessionMap = new HashMap<>();
        updateJoinSessionMap.put("status", status);

        updateTheJoinStatus.changeTask = updateTheJoinStatus.documentReference.set(updateJoinSessionMap, SetOptions.merge());
        return updateTheJoinStatus;
    }
}
