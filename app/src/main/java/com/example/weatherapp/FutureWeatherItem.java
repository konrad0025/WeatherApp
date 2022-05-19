package com.example.weatherapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;

public class FutureWeatherItem implements Parcelable {
    private double temp;
    private String weather;
    private String description;
    private String dateTime;
    private int id;

    public FutureWeatherItem(double temp, String weather, String description, String dateTime,int id) {
        this.temp = temp;
        this.weather = weather;
        this.description = description;
        this.dateTime = dateTime;
        this.id = id;
    }


    protected FutureWeatherItem(Parcel in) {
        temp = in.readDouble();
        weather = in.readString();
        description = in.readString();
        id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(temp);
        dest.writeString(weather);
        dest.writeString(description);
        dest.writeInt(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FutureWeatherItem> CREATOR = new Creator<FutureWeatherItem>() {
        @Override
        public FutureWeatherItem createFromParcel(Parcel in) {
            return new FutureWeatherItem(in);
        }

        @Override
        public FutureWeatherItem[] newArray(int size) {
            return new FutureWeatherItem[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
