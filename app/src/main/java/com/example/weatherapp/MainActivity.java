package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddNewCityDialog.CityDialogListener {

    private ArrayList<CityItem> cityItems = new ArrayList<>();
    private FavCityDB favCityDB = new FavCityDB(this);
    private FloatingActionButton addButton;
    private CityAdapter cityAdapter;
    private String url = "http://api.openweathermap.org/data/2.5/weather";
    private String appId = "4419cc9da0b7cb02657dd65732f95dbb";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityItems = favCityDB.getCityList();
        cityItems.add(0,new CityItem(0,R.drawable.partlycouldy,"Your Location","0",15.0));
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
        getWeatherDetailsOfNewCity(cityName);
    }

    public void getWeatherDetailsOfNewCity(String cityName)
    {
        String tempUrl = "";
        tempUrl = url + "?q=" + cityName + "&appid=" + appId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(String response) {
                Log.d("response",response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    cityItems.add(new CityItem(cityItems.size(),R.drawable.cloud,cityName,"0", temp));
                    cityAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}