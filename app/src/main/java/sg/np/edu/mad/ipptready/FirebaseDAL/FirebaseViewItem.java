package sg.np.edu.mad.ipptready.FirebaseDAL;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class FirebaseViewItem<T> {
    public T viewItem;
    public DocumentReference documentReference;

    public FirebaseViewItem(T viewItem, DocumentReference documentReference) {
        this.viewItem = viewItem;
        this.documentReference = documentReference;
    }
}
