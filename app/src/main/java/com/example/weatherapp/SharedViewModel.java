package com.example.weatherapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<CityItem> city = new MutableLiveData<>();

    public void setCity(CityItem input)
    {
        city.setValue(input);
    }

    public LiveData<CityItem> getCity(){
        return city;
    }
}
