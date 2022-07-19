package sg.np.edu.mad.ipptready.FirebaseDAL;

import java.io.Serializable;

public class FirebaseDoc<T> implements Serializable {
    public T item;
    public String documentId;

    public FirebaseDoc(T item, String documentId) {
        this.item = item;
        this.documentId = documentId;
    }
}
