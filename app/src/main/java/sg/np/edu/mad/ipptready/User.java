package sg.np.edu.mad.ipptready;

import com.google.type.DateTime;

public class User {
    private DateTime DOB;
    private String Name;

    public User(DateTime dOB, String name) {
        DOB = dOB;
        Name = name;
    }

    public DateTime getDOB() {
        return DOB;
    }

    public String getName() {
        return Name;
    }
}
