package com.example.weatherapp;

public class CityItem {
    private int key_id;
    private int imageResource;
    private String cityName;
    private String favStatus;
    private double temp;

    public CityItem() {
    }

    public CityItem(int key_id, int imageResource, String cityName, String favStatus, double temp) {
        this.key_id = key_id;
        this.imageResource = imageResource;
        this.cityName = cityName;
        this.favStatus = favStatus;
        this.temp = temp;
    }

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
}
