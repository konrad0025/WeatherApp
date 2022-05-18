package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WindFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private CityItem cityItem;
    private SharedViewModel viewModel;
    TextView textViewSpeedValue,textViewDegValue,textViewHuminity,textViewVisibility;

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;

    View view;

    public WindFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt("POSITION");
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wind, container, false);

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
                textViewSpeedValue = view.findViewById(R.id.speed);
                textViewDegValue = view.findViewById(R.id.degrees);
                textViewHuminity = view.findViewById(R.id.humidityValue);
                textViewVisibility = view.findViewById(R.id.visibilityValue);
                Log.d("Hello",mParam1+"hello");
                textViewSpeedValue.setText(cityItem.getWindSpeed()+"");
                textViewDegValue.setText(cityItem.getWindDeg()+"");
                textViewHuminity.setText(cityItem.getHumidity()+"");
                textViewVisibility.setText(cityItem.getVisibility()+"");
            }
        });
        this.cityItem = viewModel.getCity().getValue();

        textViewSpeedValue = view.findViewById(R.id.speed);
        textViewDegValue = view.findViewById(R.id.degrees);
        textViewHuminity = view.findViewById(R.id.humidityValue);
        textViewVisibility = view.findViewById(R.id.visibilityValue);
        textViewSpeedValue.setText(cityItem.getWindSpeed()+"");
        textViewDegValue.setText(cityItem.getWindDeg()+"");
        textViewHuminity.setText(cityItem.getHumidity()+"");
        textViewVisibility.setText(cityItem.getVisibility()+"");
    }
}