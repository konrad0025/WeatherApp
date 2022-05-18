package com.example.weatherapp;

import android.os.Parcel;
import android.os.Parcelable;

public class CityItem implements Parcelable {
    private int key_id;
    private int imageResource;
    private String cityName;
    private String favStatus;
    private double temp;
    private double windSpeed;
    private double windDeg;
    private double humidity;
    private double visibility;
    private double longitude;
    private double latitude;
    private double pressure;

    public CityItem() {
    }

    public CityItem(int key_id, int imageResource, String cityName, String favStatus, double temp, double windSpeed, double windDeg, double humidity, double visibility, double longitude, double latitude, double pressure) {
        this.key_id = key_id;
        this.imageResource = imageResource;
        this.cityName = cityName;
        this.favStatus = favStatus;
        this.temp = temp;
        this.windSpeed = windSpeed;
        this.windDeg = windDeg;
        this.humidity = humidity;
        this.visibility = visibility;
        this.longitude = longitude;
        this.latitude = latitude;
        this.pressure = pressure;
    }

    protected CityItem(Parcel in) {
        key_id = in.readInt();
        imageResource = in.readInt();
        cityName = in.readString();
        favStatus = in.readString();
        temp = in.readDouble();
        windSpeed = in.readDouble();
        windDeg = in.readDouble();
        humidity = in.readDouble();
        visibility = in.readDouble();
        longitude = in.readDouble();
        latitude = in.readDouble();
        pressure = in.readDouble();
    }

    public static final Creator<CityItem> CREATOR = new Creator<CityItem>() {
        @Override
        public CityItem createFromParcel(Parcel in) {
            return new CityItem(in);
        }

        @Override
        public CityItem[] newArray(int size) {
            return new CityItem[size];
        }
    };

    public int getKey_id() {
        return key_id;
    }

    public void setKey_id(int key_id) {
        this.key_id = key_id;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getFavStatus() {
        return favStatus;
    }

    public void setFavStatus(String favStatus) {
        this.favStatus = favStatus;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getTemp() { return temp; }

    public void setTemp(double temp) { this.temp = temp; }

    public double getWindSpeed() { return windSpeed; }

    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public double getWindDeg() { return windDeg; }

    public void setWindDeg(double windDeg) { this.windDeg = windDeg; }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(key_id);
        parcel.writeInt(imageResource);
        parcel.writeString(cityName);
        parcel.writeString(favStatus);
        parcel.writeDouble(temp);
        parcel.writeDouble(windSpeed);
        parcel.writeDouble(windDeg);
        parcel.writeDouble(humidity);
        parcel.writeDouble(visibility);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
        parcel.writeDouble(pressure);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }
}
