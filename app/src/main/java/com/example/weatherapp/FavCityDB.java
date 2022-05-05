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
    public static String TEMP_C = "tempC";

    public FavCityDB(Context context)
    {
        super(context,DATABASE_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CITY_NAME + " TEXT," + TEMP_C + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int insertIntoTheDatabase(String cityName, double temp,boolean isFisrtUse)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CITY_NAME, cityName);
        cv.put(TEMP_C, temp);
        if(isFisrtUse)
        {
            cv.put(KEY_ID,0);
        }
        long id = db.insert(TABLE_NAME,null,cv);
        Log.d("FavCityDB Status", cityName + ", status - " + cv);
        return (int)id;
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
        final int tempID = cursor.getColumnIndex(TEMP_C);
        final ArrayList<CityItem> cityList = new ArrayList<>();
        if(!cursor.moveToFirst())
        {
            return cityList;
        }
        do{
            final String name = cursor.getString(cityNameID);
            final String cityIdValue = cursor.getString(cityID);
            final String tempIdValue = cursor.getString(tempID);
            Log.d("checking", name + " " + cityIdValue + " " + tempIdValue);
            cityList.add(new CityItem(Integer.parseInt(cityIdValue),R.drawable.cloud,name,"1",Double.parseDouble(tempIdValue)));
        }while(cursor.moveToNext());
        return cityList;
    }
    public Cursor readAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME;
        return db.rawQuery(sql,null,null);
    }

    public void updateCity(CityItem cityItem)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CITY_NAME, cityItem.getCityName());
        cv.put(TEMP_C, cityItem.getTemp());
        db.update(TABLE_NAME, cv, "id = ?", new String[]{cityItem.getKey_id()+""});
        Log.d("FavCityDB Status", cityItem.getCityName() + ", status - " + cv);
    }

    public void removeFromTheDatabase(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "delete from " + TABLE_NAME + " where " + KEY_ID + " = " + id + "";
        db.execSQL(sql);
    }

}
