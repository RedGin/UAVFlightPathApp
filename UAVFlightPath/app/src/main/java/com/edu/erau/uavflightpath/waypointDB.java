package com.edu.erau.uavflightpath;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by Michael on 4/28/2015.
 */
public class waypointDB extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "waypointDB.db";
    private static final String TABLE_WAYPOINT = "waypoint";

    public static final String COLUMN_WAYPOINTNAME = "waypointname";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ALTITUDE = "altitude";
    public static final String COLUMN_VELCOITY = "velocity";

    public waypointDB(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_WAYPOINT + "("
                + COLUMN_WAYPOINTNAME + " WAYPOINT NAME PRIMARY KEY," + COLUMN_LATITUDE
                + " DOUBLE," + COLUMN_LONGITUDE + " DOUBLE" + COLUMN_ALTITUDE + " INTEGER,"+ COLUMN_VELCOITY+ " INTEGER" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAYPOINT);
        onCreate(db);
    }

    public void addWaypoint(waypoint waypoint) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_WAYPOINTNAME, waypoint.get_waypointName());
        values.put(COLUMN_LATITUDE, waypoint.get_latitude());
        values.put(COLUMN_LONGITUDE, waypoint.get_longitude());
        values.put(COLUMN_ALTITUDE, waypoint.get_altitude());
        values.put(COLUMN_VELCOITY, waypoint.get_velocity());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_WAYPOINT, null, values);
        db.close();
    }

    public waypoint findWaypoint(String waypointname) {
        String query = "Select * FROM " + TABLE_WAYPOINT + " WHERE " + COLUMN_WAYPOINTNAME + " =  \"" + waypointname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        waypoint Waypoint = new waypoint();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            Waypoint.set_latitude(Double.parseDouble(cursor.getString(0)));
            Waypoint.set_longitude(Double.parseDouble(cursor.getString(1)));
            Waypoint.set_altitude(Integer.parseInt(cursor.getString(2)));
            Waypoint.set_velocity(Integer.parseInt(cursor.getString(3)));
            cursor.close();
        } else {
            Waypoint = null;
        }
        db.close();
        return Waypoint;
    }

    public boolean deleteWaypoint(String waypointname) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_WAYPOINT + " WHERE " + COLUMN_WAYPOINTNAME + " =  \"" + waypointname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        waypoint Waypoint = new waypoint();

        if (cursor.moveToFirst()) {
            Waypoint.set_waypointName(cursor.getString(0));
            db.delete(TABLE_WAYPOINT, COLUMN_WAYPOINTNAME + " = ?",
                    new String[] { String.valueOf(Waypoint.get_waypointName()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
}
