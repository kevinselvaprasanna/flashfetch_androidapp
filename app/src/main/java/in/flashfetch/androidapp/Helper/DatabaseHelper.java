package in.flashfetch.androidapp.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import in.flashfetch.androidapp.Objects.Notification;


public class DatabaseHelper {

    private static String LOG_TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "FlashFetch";
    private static final int DATABASE_VERSION = 1;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;


    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME,null,DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub _id INTEGER PRIMARY KEY
            db.execSQL("CREATE TABLE Notifications( id BIGINT PRIMARY KEY, email VARCHAR(30), category VARCHAR(30),price INT ,time BIGINT)");
            Log.d("dmydb","DATABSE CREATED");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL(" DROP TABLE IF EXISTS Notifications "  );
            Log.d("dmydb", "DATABSE dropped");
        }

    }

    public DatabaseHelper(Context c){
        ourContext = c;
    }

    public DatabaseHelper open(){
        ourHelper = new DbHelper (ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        ourHelper.close();
    }

    public long addNot(ContentValues cv) {
        open();
        long id = ourDatabase.insert("Notifications", null, cv);
        Log.d("dmydb","NOTIFICATION ADDED");
        close();
        return id;
    }
    public ArrayList<Notification> getAllNotifications () {
        open();
        String[] columns = Notification.columns;
        Cursor c = ourDatabase.query("Notifications", columns, null, null, null, null, null);
        ArrayList<Notification> arrayList = Notification.getArrayList(c);
        close();
        return arrayList;
    }




}