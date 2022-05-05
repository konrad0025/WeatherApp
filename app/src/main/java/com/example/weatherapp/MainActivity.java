package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddNewCityDialog.CityDialogListener, LocationListener {

    private ArrayList<CityItem> cityItems = new ArrayList<>();
    private FavCityDB favCityDB = new FavCityDB(this);
    private FloatingActionButton addButton,menuButton,tempButton;
    private Animation rotateClose,rotateOpen,toBottom,fromBottom;
    private CityAdapter cityAdapter;
    private String url = "http://api.openweathermap.org/data/2.5/weather";
    private String appId = "4419cc9da0b7cb02657dd65732f95dbb";
    private boolean isMenuButtonClicked = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotateClose = AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim);
        rotateOpen = AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim);
        toBottom = AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim);
        fromBottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim);

        cityItems = favCityDB.getCityList();
        cityItems.add(0,new CityItem(0,R.drawable.partlycouldy,"Your Location","0",15.0));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        cityAdapter = new CityAdapter(cityItems,this, loadTempType());
        recyclerView.setAdapter(cityAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addButton = findViewById(R.id.addButton);
        menuButton = findViewById(R.id.menuButton);
        tempButton = findViewById(R.id.tempButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTempType(!cityAdapter.getIsCelcius());
                cityAdapter.setIsCelcius(!cityAdapter.getIsCelcius());
                cityAdapter.notifyDataSetChanged();
            }
        });
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMenuButtonClicked)
                {
                    isMenuButtonClicked = false;
                    menuButton.startAnimation(rotateClose);
                    tempButton.startAnimation(toBottom);
                    addButton.startAnimation(toBottom);
                    tempButton.setVisibility(View.INVISIBLE);
                    menuButton.setVisibility(View.INVISIBLE);
                }
                else
                {
                    isMenuButtonClicked = true;
                    tempButton.setVisibility(View.VISIBLE);
                    menuButton.setVisibility(View.VISIBLE);
                    menuButton.startAnimation(rotateOpen);
                    tempButton.startAnimation(fromBottom);
                    addButton.startAnimation(fromBottom);


                }
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(findViewById(R.id.recyclerView));
        String prev = loadDataTime();
        if(prev.equals(""))
        {
            saveDataTime();
        }
        else
        {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(LocalDateTime.parse(prev), now);
            Log.d("MINUTES",duration.toMinutes()+"");
            if(duration.toMinutes() >= 30)
            {
                updateWeatherDetails();
                saveDataTime();
            }

        }
        swipeRefreshLayout = findViewById(R.id.refreshSwipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String prev = loadDataTime();
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(LocalDateTime.parse(prev), now);
                Log.d("MINUTES",duration.toMinutes()+"");
                if(duration.toMinutes() >= 1)
                {
                    updateWeatherDetails();
                    saveDataTime();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"You have to wait minute until next refresh",Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
        Log.d("helollo","/n/n/n/dasdasdasd");
        saveDataTime();
        Log.d("helollo","/n/n/n/dasdasdasd");
        Log.d("helollo",loadDataTime().toString());
    }
    private int i;
    @SuppressLint("NotifyDataSetChanged")
    public void updateWeatherDetails()
    {
        for (i = 1; i<cityItems.size();i++) {
                String tempUrl = "";
                tempUrl = url + "?q=" + cityItems.get(i).getCityName() + "&appid=" + appId;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                    final int j = i;
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
                            cityItems.get(j).setTemp(temp);
                            Log.d("hello",cityItems.get(j).getCityName()+" "+cityItems.get(j).getTemp()+" "+j);
                            cityAdapter.notifyDataSetChanged();
                            if(cityItems.get(j).getFavStatus().equals("1"))
                            {
                                favCityDB.updateCity(cityItems.get(j));
                            }
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
    public void saveDataTime()
    {
        SharedPreferences sharedpreferences = getSharedPreferences("lastUpdate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        LocalDateTime now = LocalDateTime.now();
        Log.d("time",now.toString());
        editor.putString("Time",now.toString());
        editor.commit();
    }

    public String loadDataTime()
    {
        SharedPreferences sharedpreferences = getSharedPreferences("lastUpdate", Context.MODE_PRIVATE);
        return sharedpreferences.getString("Time","");
    }

    public void saveTempType(boolean type)
    {
        SharedPreferences sharedpreferences = getSharedPreferences("lastUpdate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("temp",type);
        editor.commit();
    }

    public boolean loadTempType()
    {
        SharedPreferences sharedpreferences = getSharedPreferences("lastUpdate", Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean("temp",true);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}