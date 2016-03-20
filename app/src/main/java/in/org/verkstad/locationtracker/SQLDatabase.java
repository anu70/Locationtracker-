package in.org.verkstad.locationtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by anu on 3/19/2016.
 */
public class SQLDatabase extends SQLiteOpenHelper {
    public SQLDatabase(Context context, String name, android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Location(Latitude DOUBLE,Longitude DOUBLE,Speed DOUBLE,Direction DOUBLE,Time VARCHAR)");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Location");
        onCreate(db);
    }

    public long Insert(SQLiteDatabase db,double latitude,double longitude,double speed,double direction,String time){
        ContentValues values=new ContentValues();
        values.put("Latitude",latitude);
        values.put("Longitude",longitude);
        values.put("Direction",direction);
        values.put("Speed",speed);
        values.put("Time",time);
        return  db.insert("Location",null,values);
    }
}
