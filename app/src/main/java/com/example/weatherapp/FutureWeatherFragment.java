package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class FutureWeatherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "temp";
    private static final String ARG_PARAM2 = "param2";
    private CityItem cityItem;
    private WeatherAdapter weatherAdapter;
    private SharedViewModel viewModel;
    private RecyclerView recyclerView;
    // TODO: Rename and change types of parameters
    private boolean mParam1;
    private String mParam2;
    View view;
    public FutureWeatherFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_future_weather, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        this.cityItem = viewModel.getCity().getValue();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d("checlasdas",this.cityItem.getWeatherItems().get(0).getWeather()+"sprawdzam tylko");
        weatherAdapter = new WeatherAdapter(this.cityItem.getWeatherItems(), this.getContext());
        recyclerView.setAdapter(weatherAdapter);
        viewModel.getCity().observe(requireActivity(), new Observer<CityItem>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(CityItem cityItem) {


            }
        });
    }
}