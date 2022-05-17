package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class WeatherDetails extends AppCompatActivity {
    private FavCityDB favCityDB = new FavCityDB(this);
    private ArrayList<CityItem> cityItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);

        cityItems = favCityDB.getCityList();
        Bundle bundle = getIntent().getExtras();
        int position = bundle.getInt("POSITION");
        bundle.putInt("POSITION",position);
        Toast.makeText(WeatherDetails.this, cityItems.get(position).getCityName(), Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WindFragment windFragment = new WindFragment(cityItems.get(position));
        fragmentTransaction.replace(R.id.frameLayout,windFragment);
        fragmentTransaction.replace(R.id.overAllData,new OverAllDataFragment());
        fragmentTransaction.commit();

    }
}