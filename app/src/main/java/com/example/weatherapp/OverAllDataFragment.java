package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

public class OverAllDataFragment extends Fragment {

    private static final String ARG_PARAM1 = "temp";
    private static final String ARG_PARAM2 = "param2";
    private CityItem cityItem;
    private SharedViewModel viewModel;
    TextView textViewCityName,textViewLon,textViewLat,textViewTemp;
    TextClock textClock;

    private boolean mParam1;
    private String mParam2;
    View view;
    public OverAllDataFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getBoolean(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_over_all_data, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        viewModel.getCity().observe(requireActivity(), new Observer<CityItem>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(CityItem cityItem) {
                textViewCityName = view.findViewById(R.id.cityName);
                textViewLon = view.findViewById(R.id.longitudeValue);
                textViewLat = view.findViewById(R.id.latitudeValue);
                textClock = view.findViewById(R.id.timeValue);
                textViewTemp = view.findViewById(R.id.tempValue);
                textViewCityName.setText(cityItem.getCityName()+"");
                textViewLon.setText(cityItem.getLongitude()+"");
                textViewLat.setText(cityItem.getLatitude()+"");
                textClock.setTimeZone("GMT+"+cityItem.getTimezone());
                if(mParam1)
                {
                    textViewTemp.setText(Math.round(cityItem.getTemp())+"??C");
                }
                else
                {
                    textViewTemp.setText(Math.round(cityItem.getTemp()*9/5+32)+"??F");
                }

            }
        });
        this.cityItem = viewModel.getCity().getValue();

        textViewCityName = view.findViewById(R.id.cityName);
        textViewLon = view.findViewById(R.id.longitudeValue);
        textViewLat = view.findViewById(R.id.latitudeValue);
        textClock = view.findViewById(R.id.timeValue);
        textViewTemp = view.findViewById(R.id.tempValue);
        textViewCityName.setText(cityItem.getCityName()+"");
        textViewLon.setText(cityItem.getLongitude()+"");
        textViewLat.setText(cityItem.getLatitude()+"");
        textClock.setTimeZone("GMT+"+cityItem.getTimezone());
        if(mParam1)
        {
            textViewTemp.setText(Math.round(cityItem.getTemp())+"??C");
        }
        else
        {
            textViewTemp.setText(Math.round(cityItem.getTemp()*9/5+32)+"??F");
        }
    }

}