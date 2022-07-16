package sg.np.edu.mad.ipptready.FirebaseDAL;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

public class FirebaseDocChange {
    public Task<Void> changeTask;
    public DocumentReference documentReference;
}
