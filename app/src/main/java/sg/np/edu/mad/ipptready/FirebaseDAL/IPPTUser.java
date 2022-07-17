package sg.np.edu.mad.ipptready.FirebaseDAL;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.type.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IPPTUser {
    private static final String colFrom = "IPPTUser";
    private static final String NAME = "Name";
    private static final String DOB = "DOB";
    private static final String EMAIL_ADDRESS = "EmailAddress";

    public static Task<DocumentSnapshot> getUserFromEmail(String EmailAddress) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(colFrom)
                .document(EmailAddress)
                .get();
    }

    public static FirebaseDocChange createNewUser(String EmailAddress,
                                                  String Name,
                                                  Date DoB) {
        FirebaseDocChange newUser = new FirebaseDocChange();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        newUser.documentReference = db.collection(colFrom)
                .document(EmailAddress);

        Map<String, Object> newUserMap = new HashMap<>();
        newUserMap.put(NAME, Name);
        newUserMap.put(DOB, DoB);

        newUser.changeTask = newUser.documentReference
                .set(newUserMap);
        return newUser;
    }

    public static Task<Void> updateUser(DocumentReference userDocRef,
                                                  String Name) {
        Map<String, Object> updatedUserMap = new HashMap<>();
        if (null != Name) {
            updatedUserMap.put(NAME, Name);
        }
        return userDocRef.set(updatedUserMap, SetOptions.merge());
    }

    public static Task<Void> deleteUser(DocumentReference userDocRef) {
        return userDocRef.delete();
    }
}
