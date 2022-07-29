package sg.np.edu.mad.ipptready.FirebaseDAL;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExerciseTogetherSession {
    public String dateCreated;
    public String sessionName;
    public String exercise;
    public String status = "Not Completed";

    public ExerciseTogetherSession(String SessionName, String Exercise) {
        dateCreated = getDate();
        sessionName = SessionName;
        exercise = Exercise;
    }

    private String getDate() {
        Date d = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM yyyy h:mm:ss a");
        return dateFormatter.format(d);
    }

    public static FirebaseDocChange createNewSession(String EmailAddress, ExerciseTogetherSession session) {
        FirebaseDocChange newSession = new FirebaseDocChange();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        newSession.documentReference = db.collection("IPPTUser").document(EmailAddress).collection("Exercise Together").document(session.dateCreated);

        Map<String, Object> newSessionMap = new HashMap<>();
        newSessionMap.put("dateCreated", session.dateCreated);
        newSessionMap.put("exercise", session.exercise);
        newSessionMap.put("sessionName", session.sessionName);
        newSessionMap.put("status", session.status);

        newSession.changeTask = newSession.documentReference.set(newSessionMap);
        return newSession;
    }
}
