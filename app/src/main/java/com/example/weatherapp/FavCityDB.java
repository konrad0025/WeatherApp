package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;


public class FavCityDB extends SQLiteOpenHelper {

    private static int DB_VERSION = 1;
    private static String DATABASE_NAME = "FavCityDB";
    private static String TABLE_NAME = "city";
    public static String KEY_ID = "id";
    public static String CITY_NAME = "cityName";

    public FavCityDB(Context context)
    {
        super(context,DATABASE_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CITY_NAME + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertIntoTheDatabase(String cityName)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CITY_NAME, cityName);
        db.insert(TABLE_NAME,null,cv);
        Log.d("FavCityDB Status", cityName + ", status - " + cv);

    }

    public Cursor readAllData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME + " where " + KEY_ID + " = " + id + "";
        return db.rawQuery(sql,null,null);
    }
    public ArrayList<CityItem> getCityList()
    {
        Cursor cursor = readAllData();
        final int cityNameID = cursor.getColumnIndex(CITY_NAME);
        final int cityID = cursor.getColumnIndex(KEY_ID);
        final ArrayList<CityItem> cityList = new ArrayList<>();
        if(!cursor.moveToFirst())
        {
            return cityList;
        }
        do{
            final String name = cursor.getString(cityNameID);
            final String cityIdValue = cursor.getString(cityID);
            Log.d("checking", name + " " + cityIdValue);
            cityList.add(new CityItem(Integer.parseInt(cityIdValue),R.drawable.cloud,name,"1"));
        }while(cursor.moveToNext());
        return cityList;
    }
    public Cursor readAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME;
        return db.rawQuery(sql,null,null);
    }

    public void removeFromTheDatabase(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "delete from " + TABLE_NAME + " where " + KEY_ID + " = " + id + "";
        db.execSQL(sql);
    }

}
