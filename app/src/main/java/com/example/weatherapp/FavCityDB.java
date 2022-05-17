package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListView;

import java.sql.Date;
import java.util.ArrayList;


public class FavCityDB extends SQLiteOpenHelper {

    private static int DB_VERSION = 1;
    private static String DATABASE_NAME = "FavCityDB";
    private static String TABLE_NAME = "city";
    public static String KEY_ID = "id";
    public static String CITY_NAME = "cityName";
    public static String TEMP_C = "tempC";
    public static String WIND_SPEED = "windSpeed";
    public static String WIND_DEG = "windDeg";
    public static String VISIBILITY = "visibility";
    public static String HUMIDITY = "humidity";

    public static String TABLE_NAME_2 = "days";
    public static String DATE_TIME = "dateTime";
    public static String CITY_ID = "cityId";

    public FavCityDB(Context context)
    {
        super(context,DATABASE_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CITY_NAME + " TEXT," + WIND_SPEED + " TEXT," + WIND_DEG + " TEXT," + VISIBILITY + " TEXT," + HUMIDITY + " TEXT," + TEMP_C + " TEXT)");
        /*sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME_2 + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DATE_TIME + " TEXT," + CITY_ID + " INTEGER," + TEMP_C + " TEXT)");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int insertIntoTheDatabase(String cityName, double temp, double speed, double deg, double humidity, double visibility ,boolean isFisrtUse)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CITY_NAME, cityName);
        cv.put(TEMP_C, temp);
        cv.put(WIND_SPEED, speed);
        cv.put(WIND_DEG, deg);
        cv.put(HUMIDITY, humidity);
        cv.put(VISIBILITY, visibility);
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
        final int windSpeedID = cursor.getColumnIndex(WIND_SPEED);
        final int windDegID = cursor.getColumnIndex(WIND_DEG);
        final int humidityID = cursor.getColumnIndex(HUMIDITY);
        final int visibilityID = cursor.getColumnIndex(VISIBILITY);
        final ArrayList<CityItem> cityList = new ArrayList<>();
        if(!cursor.moveToFirst())
        {
            return cityList;
        }
        do{
            final String name = cursor.getString(cityNameID);
            final String cityIdValue = cursor.getString(cityID);
            final String tempIdValue = cursor.getString(tempID);
            final String windSpeedIDValue = cursor.getString(windSpeedID);
            final String windDegIDValue = cursor.getString(windDegID);
            final String humidityIDValue = cursor.getString(humidityID);
            final String visibilityIDValue = cursor.getString(visibilityID);
            Log.d("checking", name + " " + cityIdValue + " " + tempIdValue);
            cityList.add(new CityItem(Integer.parseInt(cityIdValue),R.drawable.cloud,name,"1",Double.parseDouble(tempIdValue),Double.parseDouble(windSpeedIDValue),Double.parseDouble(windDegIDValue),Double.parseDouble(humidityIDValue),Double.parseDouble(visibilityIDValue)));
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
        cv.put(WIND_SPEED, cityItem.getWindSpeed());
        cv.put(WIND_DEG, cityItem.getWindDeg());
        cv.put(HUMIDITY, cityItem.getHumidity());
        cv.put(VISIBILITY, cityItem.getVisibility());
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
