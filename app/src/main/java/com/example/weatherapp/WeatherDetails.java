package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WeatherDetails extends AppCompatActivity {
    private FavCityDB favCityDB = new FavCityDB(this);
    private ArrayList<CityItem> cityItems = new ArrayList<>();
    private SharedViewModel viewModel;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        cityItems = favCityDB.getCityList();
        Bundle bundle = new Bundle();

        bundle.putBoolean("temp",loadTempType());
        viewModel.setCity(getIntent().getParcelableExtra("POSITION"));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WindFragment windFragment = new WindFragment();
        OverAllDataFragment overAllDataFragment = new OverAllDataFragment();
        overAllDataFragment.setArguments(bundle);
        FutureWeatherFragment futureWeatherFragment = new FutureWeatherFragment();
        futureWeatherFragment.setArguments(bundle);
        List<Fragment> list = new ArrayList<>();
        list.add(overAllDataFragment);
        list.add(windFragment);
        list.add(futureWeatherFragment);

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new SliderPagerAdapter(fragmentManager,list);

        viewPager.setAdapter(pagerAdapter);

    }
    public boolean loadTempType() {
        SharedPreferences sharedpreferences = getSharedPreferences("lastUpdate", Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean("temp", true);
    }
}