package sg.np.edu.mad.ipptready.SQLiteDAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Date;

public class SQLiteDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SQLite.db";

    public static final String TABLE_IPPTUSER = "IPPTUser";
    public static final String IPPTUSER_COLUMN_ID = "Id";
    public static final String IPPTUSER_COLUMN_NAME = "Name";
    public static final String IPPTUSER_COLUMN_DOB = "DOB";
    public static final String IPPTUSER_COLUMN_ALARM = "Alarm";

    public static final String TABLE_IPPTCYCLE = "IPPTCycle";
    public static final String IPPTCYCLE_COLUMN_IPPTUSER_ID = "IPPTUserId";
    public static final String IPPTCYCLE_COLUMN_ID = "Id";
    public static final String IPPTCYCLE_COLUMN_NAME = "Name";
    public static final String IPPTCYCLE_COLUMN_DATE_CREATED = "DateCreated";
    public static final String IPPTCYCLE_COLUMN_IS_FINISHED = "isFinished";

    public static final String TABLE_IPPTROUTINE = "IPPTRoutine";
    public static final String IPPTROUTINE_COLUMN_IPPTCYCLE_ID = "IPPTCycleId";
    public static final String IPPTROUTINE_COLUMN_ID = "Id";
    public static final String IPPTROUTINE_COLUMN_DATE_CREATED = "DateCreated";
    public static final String IPPTROUTINE_COLUMN_IPPTSCORE = "IPPTScore";

    public SQLiteDBHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_IPPTUSER_TABLE = "CREATE TABLE " +
                TABLE_IPPTUSER +
                "(" + IPPTUSER_COLUMN_ID + " TEXT PRIMARY KEY," +
                IPPTUSER_COLUMN_NAME + " TEXT," +
                IPPTUSER_COLUMN_DOB + " INTEGER," +
                IPPTUSER_COLUMN_ALARM + " INTEGER)";

        String CREATE_IPPTCYCLE_TABLE = "CREATE TABLE " +
                TABLE_IPPTCYCLE +
                "(" + IPPTCYCLE_COLUMN_IPPTUSER_ID + " TEXT," +
                IPPTCYCLE_COLUMN_ID + " TEXT PRIMARY KEY," +
                IPPTCYCLE_COLUMN_NAME + " TEXT," +
                IPPTCYCLE_COLUMN_DATE_CREATED + " INTEGER," +
                IPPTCYCLE_COLUMN_IS_FINISHED + " INTEGER)";

        String CREATE_IPPTROUTINE_TABLE = "CREATE TABLE " +
                TABLE_IPPTROUTINE +
                "(" + IPPTROUTINE_COLUMN_IPPTCYCLE_ID + " TEXT," +
                IPPTROUTINE_COLUMN_ID + " TEXT PRIMARY KEY," +
                IPPTROUTINE_COLUMN_DATE_CREATED + " INTEGER," +
                IPPTROUTINE_COLUMN_IPPTSCORE + " INTEGER)";

        sqLiteDatabase.execSQL(CREATE_IPPTUSER_TABLE);
        sqLiteDatabase.execSQL(CREATE_IPPTCYCLE_TABLE);
        sqLiteDatabase.execSQL(CREATE_IPPTROUTINE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IPPTUSER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IPPTCYCLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IPPTROUTINE);

        onCreate(sqLiteDatabase);
    }

    public void adddNewUser(IPPTUser user) {
        ContentValues values = new ContentValues();
        values.put(IPPTUSER_COLUMN_ID, user.Id);
        values.put(IPPTUSER_COLUMN_NAME, user.Name);
        values.put(IPPTUSER_COLUMN_DOB, user.DoB.getTime());
        values.put(IPPTUSER_COLUMN_ALARM, user.Alarm);
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_IPPTUSER, null, values);
        db.close();
    }

    public IPPTUser getUser(String Id) {
        String dbQuery = "SELECT * FROM " + TABLE_IPPTUSER + " WHERE " +
                IPPTUSER_COLUMN_ID +
                " = \"" + Id + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);

        IPPTUser ipptUser = new IPPTUser();
        if (cursor.moveToFirst()) {
            ipptUser.Id  = Id;
            ipptUser.Name = cursor.getString(1);
            ipptUser.DoB = new Date(cursor.getInt(2));
            ipptUser.Alarm = cursor.getInt(3);
            cursor.close();
        }
        else {
            ipptUser = null;
        }
        db.close();
        return ipptUser;
    }

    public void addNewCycle(IPPTCycle ipptCycle) {
        ContentValues values = new ContentValues();
        values.put(IPPTCYCLE_COLUMN_IPPTUSER_ID, ipptCycle.IPPTUserId);
        values.put(IPPTCYCLE_COLUMN_ID, ipptCycle.Id);
        values.put(IPPTCYCLE_COLUMN_NAME, ipptCycle.Name);
        values.put(IPPTCYCLE_COLUMN_DATE_CREATED, ipptCycle.DateCreated.getTime());
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_IPPTCYCLE, null, values);
        db.close();
    }

    public IPPTCycle getCycle(String userId, String Id) {
        String dbQuery = "SELECT * FROM " + TABLE_IPPTCYCLE + " WHERE " +
                IPPTCYCLE_COLUMN_ID +
                " = \"" + Id + "\"" + " AND " +
                IPPTCYCLE_COLUMN_IPPTUSER_ID +
                " = \"" + userId + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);

        IPPTCycle ipptCycle = new IPPTCycle();
        if (cursor.moveToFirst()) {
            ipptCycle.IPPTUserId = userId;
            ipptCycle.Id  = Id;
            ipptCycle.Name = cursor.getString(2);
            ipptCycle.DateCreated = new Date(cursor.getInt(3));
            ipptCycle.isFinished = 1 == cursor.getInt(4);
            cursor.close();
        }
        else {
            ipptCycle = null;
        }
        db.close();
        return ipptCycle;
    }

    public void addNewRoutine(IPPTRoutine ipptRoutine) {
        ContentValues values = new ContentValues();
        values.put(IPPTROUTINE_COLUMN_IPPTCYCLE_ID, ipptRoutine.IPPTCycleId);
        values.put(IPPTROUTINE_COLUMN_ID, ipptRoutine.Id);
        values.put(IPPTROUTINE_COLUMN_DATE_CREATED, ipptRoutine.DateCreated.getTime());
        values.put(IPPTROUTINE_COLUMN_IPPTSCORE, ipptRoutine.IPPTScore);
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_IPPTROUTINE, null, values);
        db.close();
    }

    public IPPTRoutine getRoutine(String cycleId, String Id) {
        String dbQuery = "SELECT * FROM " + TABLE_IPPTCYCLE + " WHERE " +
                IPPTROUTINE_COLUMN_ID +
                " = \"" + Id + "\"" + " AND " +
                IPPTROUTINE_COLUMN_IPPTCYCLE_ID +
                " = \"" + cycleId + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);

        IPPTRoutine ipptRoutine = new IPPTRoutine();
        if (cursor.moveToFirst()) {
            ipptRoutine.IPPTCycleId = cycleId;
            ipptRoutine.Id  = Id;
            ipptRoutine.DateCreated = new Date(cursor.getInt(2));
            ipptRoutine.IPPTScore = cursor.getInt(3);
            cursor.close();
        }
        else {
            ipptRoutine = null;
        }
        db.close();
        return ipptRoutine;
    }
}