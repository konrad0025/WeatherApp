package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddNewCityDialog.CityDialogListener {

    private ArrayList<CityItem> cityItems = new ArrayList<>();
    private FavCityDB favCityDB = new FavCityDB(this);
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityItems = favCityDB.getCityList();
        cityItems.add(0,new CityItem(0,R.drawable.partlycouldy,"Your Location","0"));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new CityAdapter(cityItems,this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

    }

    private void openDialog() {
        AddNewCityDialog addNewCityDialog = new AddNewCityDialog();
        addNewCityDialog.show(getSupportFragmentManager(),"example dialog");
    }

    @Override
    public void applyTexts(String cityName) {
        cityItems.add(new CityItem(cityItems.size(),R.drawable.cloud,cityName,"0"));
    }
}