package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class WeatherDetails extends AppCompatActivity {
    private FavCityDB favCityDB = new FavCityDB(this);
    private ArrayList<CityItem> cityItems = new ArrayList<>();
    private SharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        cityItems = favCityDB.getCityList();
        Bundle bundle = getIntent().getExtras();
        //int position = bundle.getInt("POSITION");
        //bundle.putInt("POSITION",position);
        viewModel.setCity(getIntent().getParcelableExtra("POSITION"));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WindFragment windFragment = new WindFragment();
        fragmentTransaction.replace(R.id.frameLayout,windFragment);
        fragmentTransaction.replace(R.id.overAllData,new OverAllDataFragment());
        fragmentTransaction.commit();

    }
}