package sg.np.edu.mad.ipptready.FirebaseDAL;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IPPTUser implements Serializable {
    public static final String colFrom = "IPPTUser";
    private static final String NAME = "Name";
    private static final String DOB = "DOB";
    private static final String IMAGE_KEY = "ImageKey";
    private static final String EMAIL_ADDRESS = "EmailAddress";
    private static final String ROUTINE_TIME = "RoutineTime";

    public Date DoB;
    public String Name;

    public IPPTUser(Map<String, Object> userMap) {
        Name = (String) userMap.get(NAME);
        DoB = ((Timestamp) userMap.get(DOB)).toDate();
    }

    public static CollectionReference getUsersCollection()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("IPPTUser");
    }

    public static DocumentReference getUserDocFromId(String userDocId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(colFrom)
                .document(userDocId);
    }

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
                                        String Name,
                                        Date Dob,
                                        String imageKey,
                                        byte[] data) {

        Map<String, Object> updatedUserMap = new HashMap<>();
        updatedUserMap.put(NAME, Name);
        updatedUserMap.put(DOB, Dob);
        updatedUserMap.put(IMAGE_KEY, imageKey);



        FirebaseStorage storage;
        StorageReference storageReference;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        StorageReference userRef = storageReference.child("profilePictures/" + imageKey);

        UploadTask uploadTask = userRef.putBytes(data);


        return userDocRef.set(updatedUserMap, SetOptions.merge());
    }

    public static Task<Void> setTime(DocumentReference userDocRef,
                                     int Time) {
        Map<String, Object> updateTimeMap = new HashMap<>();
        updateTimeMap.put(ROUTINE_TIME, Time);

        return userDocRef.set(updateTimeMap, SetOptions.merge());
    }

    public static Task<Void> deleteUser(DocumentReference userDocRef) {
        return userDocRef.delete();
    }
}




















