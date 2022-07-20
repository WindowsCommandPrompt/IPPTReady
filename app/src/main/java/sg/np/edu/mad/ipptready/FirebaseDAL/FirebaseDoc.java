package sg.np.edu.mad.ipptready.FirebaseDAL;

import java.io.Serializable;
import java.util.ArrayList;

public class FirebaseDoc<T> implements Serializable {
    public T item;
    public String documentId;

    public FirebaseDoc(T item, String documentId) {
        this.item = item;
        this.documentId = documentId;
    }

    public FirebaseDoc(FirebaseViewItem<T> firebaseViewItem) {
        this.item = firebaseViewItem.viewItem;
        this.documentId = firebaseViewItem.documentReference.getId();
    }

    public static <T> ArrayList<FirebaseDoc<T>> FromFirebaseViewItems(ArrayList<FirebaseViewItem<T>> firebaseViewItems) {
        ArrayList<FirebaseDoc<T>> firebaseDocs = new ArrayList<>();
        for (FirebaseViewItem<T> firebaseViewItem : firebaseViewItems)
            firebaseDocs.add(new FirebaseDoc<>(firebaseViewItem));
        return firebaseDocs;
    }
}
