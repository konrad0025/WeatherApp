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
import java.time.LocalDateTime;
import java.util.ArrayList;


public class FavCityDB extends SQLiteOpenHelper {

    private static int DB_VERSION = 2;
    private static String DATABASE_NAME = "FavCityDBv2";
    private static String TABLE_NAME = "city";
    public static String KEY_ID = "id";
    public static String CITY_NAME = "cityName";
    public static String TEMP_C = "tempC";
    public static String WIND_SPEED = "windSpeed";
    public static String WIND_DEG = "windDeg";
    public static String VISIBILITY = "visibility";
    public static String HUMIDITY = "humidity";
    public static String LONGITUDE = "longitude";
    public static String LATITUDE = "latitude";
    public static String PRESSURE = "pressure";

    public static String TABLE_NAME_2 = "days";
    public static String DATE_TIME = "dateTime";
    public static String CITY_ID = "cityId";
    public static String DETAILS_INFO = "details";
    public static String NAME_INFO = "name";

    public FavCityDB(Context context)
    {
        super(context,DATABASE_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CITY_NAME + " TEXT," + WIND_SPEED + " TEXT," + LONGITUDE + " TEXT," + LATITUDE + " TEXT," + PRESSURE + " TEXT," + WIND_DEG + " TEXT," + VISIBILITY + " TEXT," + HUMIDITY + " TEXT," + TEMP_C + " TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME_2 + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DATE_TIME + " TEXT," + DETAILS_INFO + " TEXT," + NAME_INFO + " TEXT," + CITY_ID + " INTEGER," + TEMP_C + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int insertIntoTheDatabase(String cityName, double temp, double speed, double deg, double humidity, double visibility, double longitude, double latitude, double pressure, ArrayList<FutureWeatherItem> list ,boolean isFisrtUse)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CITY_NAME, cityName);
        cv.put(TEMP_C, temp);
        cv.put(WIND_SPEED, speed);
        cv.put(WIND_DEG, deg);
        cv.put(HUMIDITY, humidity);
        cv.put(VISIBILITY, visibility);
        cv.put(PRESSURE, pressure);
        cv.put(LONGITUDE, longitude);
        cv.put(LATITUDE, latitude);
        if(isFisrtUse) {
            cv.put(KEY_ID, 0);
        }

        long id = db.insert(TABLE_NAME,null,cv);
        Log.d("FavCityDB Status", cityName + ", status - " + cv);
        list.forEach((x)->{
            insertIntoTheDatabaseItem(x.getWeather(),x.getTemp(),x.getDescription(),x.getDateTime(),(int)id);
        });
        return (int)id;
    }

    public int insertIntoTheDatabaseItem(String weatherName, double temp, String description, String time, int id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME_INFO, weatherName);
        cv.put(TEMP_C, temp);
        cv.put(DATE_TIME, time);
        cv.put(CITY_ID, id);
        cv.put(DETAILS_INFO, description);


        long idReturn = db.insert(TABLE_NAME_2,null,cv);
        Log.d("FavCityDB2 Status", weatherName + " " + temp + ", status - " + cv);
        return (int)idReturn;
    }

    public Cursor readAllData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME + " where " + KEY_ID + " = " + id + "";
        return db.rawQuery(sql,null,null);
    }
    public Cursor readAllDataItem(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME_2 + " where " + CITY_ID + " = " + id + "";
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
        final int longitudeID = cursor.getColumnIndex(LONGITUDE);
        final int latitudeID = cursor.getColumnIndex(LATITUDE);
        final int pressureID = cursor.getColumnIndex(PRESSURE);
        final ArrayList<CityItem> cityList = new ArrayList<>();
        if(!cursor.moveToFirst())
        {
            return cityList;
        }
        do{
            final String name = cursor.getString(cityNameID);
            final String cityIdValue = cursor.getString(cityID);
            final String tempIdValue = cursor.getString(tempID);
            Log.d("hello",windSpeedID+"dasd"+windSpeedID+"dasdas"+latitudeID+"dasdasdas"+cityID+"adasdas"+name);
            final String windSpeedIDValue = cursor.getString(windSpeedID);
            final String windDegIDValue = cursor.getString(windDegID);
            final String humidityIDValue = cursor.getString(humidityID);
            final String visibilityIDValue = cursor.getString(visibilityID);
            final String longitudeIDValue = cursor.getString(longitudeID);
            final String latitudeIDValue = cursor.getString(latitudeID);
            final String pressureIDValue = cursor.getString(pressureID);
            Log.d("checking", name + " " + cityIdValue + " " + tempIdValue);

            Cursor cursor2 = readAllDataItem(Integer.parseInt(cityIdValue));
            final int weatherNameID = cursor.getColumnIndex(NAME_INFO);
            final int weatherDetailsID = cursor.getColumnIndex(DETAILS_INFO);
            final int tempItemID = cursor.getColumnIndex(TEMP_C);
            final int timeID = cursor.getColumnIndex(DATE_TIME);
            final int itemID = cursor.getColumnIndex(KEY_ID);
            ArrayList<FutureWeatherItem> weatherItems = new ArrayList<>();

            if(!cursor2.moveToFirst())
            {
                weatherItems = null;
            }
            else
            {
                do{
                    final String weatherNameIDValue = cursor2.getString(weatherNameID);
                    final String weatherDetailsIDValue = cursor2.getString(weatherDetailsID);
                    final String tempItemIDValue = cursor2.getString(tempItemID);
                    final String timeIDValue = cursor2.getString(timeID);
                    final String itemIDValue = cursor2.getString(itemID);
                    weatherItems.add(new FutureWeatherItem(Double.parseDouble(tempItemIDValue),weatherNameIDValue,weatherDetailsIDValue,timeIDValue,Integer.parseInt(itemIDValue)));
                }while(cursor2.moveToNext());
            }

            cityList.add(new CityItem(Integer.parseInt(cityIdValue),R.drawable.cloud,name,"1",Double.parseDouble(tempIdValue),Double.parseDouble(windSpeedIDValue),Double.parseDouble(windDegIDValue),Double.parseDouble(humidityIDValue),Double.parseDouble(visibilityIDValue),Double.parseDouble(longitudeIDValue),Double.parseDouble(latitudeIDValue),Double.parseDouble(pressureIDValue),weatherItems));
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
        cv.put(LONGITUDE, cityItem.getLongitude());
        cv.put(LATITUDE, cityItem.getLatitude());
        cv.put(PRESSURE, cityItem.getPressure());
        cityItem.getWeatherItems().forEach((x)->{
            updateCityItem(x);
        });
        db.update(TABLE_NAME, cv, "id = ?", new String[]{cityItem.getKey_id()+""});
        Log.d("FavCityDB Status", cityItem.getCityName() + ", status - " + cv);
    }

    public void updateCityItem(FutureWeatherItem futureWeatherItem)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME_INFO, futureWeatherItem.getWeather());
        cv.put(DETAILS_INFO, futureWeatherItem.getDescription());
        cv.put(DATE_TIME, futureWeatherItem.getDateTime());
        cv.put(TEMP_C, futureWeatherItem.getTemp());
        db.update(TABLE_NAME_2, cv, "id = ?", new String[]{futureWeatherItem.getId()+""});
    }

    public void removeFromTheDatabase(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "delete from " + TABLE_NAME + " where " + KEY_ID + " = " + id + "";
        db.execSQL(sql);
        String sql2 = "delete from " + TABLE_NAME_2 + " where " + CITY_ID + " = " + id + "";
        db.execSQL(sql2);
    }

}
