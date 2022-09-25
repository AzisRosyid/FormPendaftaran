package com.example.formpendaftaran.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.formpendaftaran.model.Data;
import com.example.formpendaftaran.model.Location;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    private final String TABLE_NAME = "data";

    private final String COLUMN_ID = "id";
    private final String COLUMN_NAME = "name";
    private final String COLUMN_ADDRESS = "address";
    private final String COLUMN_PHONE_NUMBER = "phone_number";
    private final String COLUMN_GENDER = "gender";
    private final String COLUMN_LATITUDE = "latitude";
    private final String COLUMN_LONGITUDE = "longitude";
    private final String COLUMN_IMAGE = "image";

    public DbHelper(Context context) { super(context, "coba.db", null, 2); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_ADDRESS + " TEXT NOT NULL, " + COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " + COLUMN_GENDER + " TEXT NOT NULL, " + COLUMN_LATITUDE + " REAL NOT NULL, " + COLUMN_LONGITUDE + " REAL NOT NULL, " + COLUMN_IMAGE + " TEXT NOT NULL" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public ArrayList<Data> getData() {
        ArrayList<Data> items = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Data data = new Data(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        new Location(cursor.getDouble(5), cursor.getDouble(6)),
                        cursor.getString(7)
                );
                items.add(data);
            } while (cursor.moveToNext());
        }
        db.close();
        return items;
    }

    public void insert(String name, String address, String phoneNumber, String gender, Location location, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_NAME + "(" + COLUMN_NAME + ", " + COLUMN_ADDRESS + ", " + COLUMN_PHONE_NUMBER + ", " + COLUMN_GENDER + ", " + COLUMN_LATITUDE + ", " + COLUMN_LONGITUDE + ", " + COLUMN_IMAGE +") VALUES('" + name + "', '" + address + "', '" + phoneNumber + "', '" + gender + "', " + location.getLatitude() + ", " + location.getLongitude() + ", '" + image + "')");
        db.close();
    }

    public void update(int id, String name, String address, String phoneNumber, String gender, Location location, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COLUMN_NAME + " = '" + name + "', " + COLUMN_ADDRESS + " = '" + address + "', " + COLUMN_PHONE_NUMBER + " = '" + phoneNumber + "', " + COLUMN_GENDER + " = '" + gender + "'";
        if (location != null) query += ", " + COLUMN_LATITUDE + " = " + location.getLatitude() + ", " + COLUMN_LONGITUDE + " = " + location.getLongitude();
        if (image != null) query += ", " + COLUMN_IMAGE + " = '" + image + "'";
        query += " WHERE " + COLUMN_ID + "=" + id;
        db.execSQL(query);
        db.close();
    }

    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "= " + id );
        db.close();
    }
}
