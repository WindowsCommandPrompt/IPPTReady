package sg.np.edu.mad.ipptready.FirebaseDAL;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.type.DateTime;

import java.util.HashMap;
import java.util.Map;

public class IPPTUser {
    public static final String colFrom = "IPPTUser";
    public static final String NAME = "Name";
    public static final String DOB = "DOB";
    public static final String EMAIL_ADDRESS = "EmailAddress";

    public static Task<QuerySnapshot> getUserFromEmail(String EmailAddress) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(colFrom)
                .whereEqualTo(EMAIL_ADDRESS, EmailAddress)
                .get();
    }

    public static FirebaseDocChange createNewUser(String Name,
                                           DateTime DoB,
                                           String EmailAddress) {
        FirebaseDocChange newUser = new FirebaseDocChange();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        newUser.documentReference = db.collection(colFrom)
                .document();

        Map<String, Object> newUserMap = new HashMap<>();
        newUserMap.put(NAME, Name);
        newUserMap.put(DOB, DoB);
        newUserMap.put(EMAIL_ADDRESS, EmailAddress);

        newUser.changeTask = newUser.documentReference
                .set(newUserMap);
        return newUser;
    }

    public static Task<Void> updateUser(DocumentReference userDocRef,
                                                  String Name,
                                                  String EmailAddress) {
        Map<String, Object> updatedUserMap = new HashMap<>();
        if (null != Name) {
            updatedUserMap.put(NAME, Name);
        }
        if (null != EmailAddress) {
            updatedUserMap.put(EMAIL_ADDRESS, EmailAddress);
        }
        return userDocRef.set(updatedUserMap, SetOptions.merge());
    }

    public static Task<Void> deleteUser(DocumentReference userDocRef) {
        return userDocRef.delete();
    }
}
