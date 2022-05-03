package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddNewCityDialog.CityDialogListener {

    private ArrayList<CityItem> cityItems = new ArrayList<>();
    private FavCityDB favCityDB = new FavCityDB(this);
    private FloatingActionButton addButton;
    private CityAdapter cityAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityItems = favCityDB.getCityList();
        cityItems.add(0,new CityItem(0,R.drawable.partlycouldy,"Your Location","0"));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        cityAdapter = new CityAdapter(cityItems,this);
        recyclerView.setAdapter(cityAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(findViewById(R.id.recyclerView));

    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if(position!=0)
            {
                cityItems.remove(position);
            }
            cityAdapter.notifyDataSetChanged();
        }
    };

    private void openDialog() {
        AddNewCityDialog addNewCityDialog = new AddNewCityDialog();
        addNewCityDialog.show(getSupportFragmentManager(),"example dialog");
    }

    @Override
    public void applyTexts(String cityName) {
        cityItems.add(new CityItem(cityItems.size(),R.drawable.cloud,cityName,"0"));
    }
}