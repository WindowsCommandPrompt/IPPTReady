package sg.np.edu.mad.ipptready.SQLiteDAL;

import static sg.np.edu.mad.ipptready.SQLiteDAL.SQLiteDBHandler.TABLE_IPPTUSER;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class IPPTUser {
    public String Id;
    public String Name;
    public Date DoB;
    public int Alarm;
}
