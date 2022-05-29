package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddNewCityDialog.CityDialogListener, Serializable {

    private ArrayList<CityItem> cityItems = new ArrayList<>();
    private FavCityDB favCityDB = new FavCityDB(this);
    private FloatingActionButton addButton, menuButton, tempButton;
    private Animation rotateClose, rotateOpen, toBottom, fromBottom;
    private CityAdapter cityAdapter;
    private String url = "http://api.openweathermap.org/data/2.5/forecast";
    private String appId = "4419cc9da0b7cb02657dd65732f95dbb";
    private boolean isMenuButtonClicked = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private CityAdapter.RecyclerViewClickListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            SharedPreferences sharedpreferences = getSharedPreferences("lastUpdate", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("Time", "");
            editor.commit();
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);

        cityItems = favCityDB.getCityList();

        setOnClickListner();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        cityAdapter = new CityAdapter(cityItems, this, loadTempType(), listener);
        recyclerView.setAdapter(cityAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String prev = loadDataTime();
        if (prev.equals("")) {
            favCityDB.insertIntoTheDatabase("Your Location",0,0,0,0,0,0,0,0,new ArrayList<FutureWeatherItem>(),0,R.drawable.cloud,true);
            cityItems.add(0, new CityItem(0, R.drawable.cloud, "Your Location", "0",0,0,0,0,0,0,0,0,new ArrayList<FutureWeatherItem>(),0));
            cityAdapter.notifyDataSetChanged();
            saveDataTime();
        } else {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(LocalDateTime.parse(prev), now);
            Log.d("MINUTES", duration.toMinutes() + "");
            if (duration.toMinutes() >= 1) {
                getLocation();
                updateWeatherDetails();
                saveDataTime();
            }
        }

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
                if (isMenuButtonClicked) {
                    isMenuButtonClicked = false;
                    menuButton.startAnimation(rotateClose);
                    tempButton.startAnimation(toBottom);
                    addButton.startAnimation(toBottom);
                    tempButton.setVisibility(View.INVISIBLE);
                    menuButton.setVisibility(View.INVISIBLE);
                } else {
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

        swipeRefreshLayout = findViewById(R.id.refreshSwipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String prev = loadDataTime();
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(LocalDateTime.parse(prev), now);
                Log.d("MINUTES", duration.toMinutes() + "");
                if (duration.toMinutes() >= 1) {
                    getLocation();
                    updateWeatherDetails();
                    saveDataTime();
                } else {
                    Toast.makeText(MainActivity.this, "You have to wait minute until next refresh", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setOnClickListner() {
        listener = new CityAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this,WeatherDetails.class);
                intent.putExtra("POSITION", cityItems.get(position));
                startActivity(intent);
            }
        };
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (position != 0 && !cityItems.get(position).getFavStatus().equals("1")) {
                cityItems.remove(position);
            }
            cityAdapter.notifyDataSetChanged();
        }

    };

    private void openDialog() {
        AddNewCityDialog addNewCityDialog = new AddNewCityDialog();
        addNewCityDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String cityName) {
        getWeatherDetailsOfNewCity(cityName);
    }

    public void getWeatherDetailsOfNewCity(String cityName) {
        if(!cityName.equals(""))
        {
            String tempUrl = "";
            tempUrl = url + "?q=" + cityName + "&appid=" + appId;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(String response) {
                    Log.d("response", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("list");
                        JSONObject jsonObjectZero = jsonArray.getJSONObject(0);
                        JSONArray jsonArrayWeather = jsonObjectZero.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        String time = jsonObjectZero.getString("dt_txt");
                        JSONObject jsonObjectMain = jsonObjectZero.getJSONObject("main");
                        JSONObject jsonObjectWind = jsonObjectZero.getJSONObject("wind");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        double humidity = jsonObjectMain.getDouble("humidity");
                        double speed = jsonObjectWind.getDouble("speed");
                        double deg = jsonObjectWind.getDouble("deg");
                        double visibility = Math.round(jsonObjectZero.getDouble("visibility")/10000*100);
                        double pressure = jsonObjectMain.getDouble("pressure");

                        JSONObject jsonCity = jsonResponse.getJSONObject("city");
                        JSONObject jsonCityCoord = jsonCity.getJSONObject("coord");

                        double lon = jsonCityCoord.getDouble("lon");
                        double lat = jsonCityCoord.getDouble("lat");
                        int timezone = jsonCity.getInt("timezone")/3600;
                        int i = 1;
                        ArrayList<FutureWeatherItem> weatherItems = new ArrayList<FutureWeatherItem>();
                        while(i<40)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String timeItem = jsonObject.getString("dt_txt");
                            jsonObjectMain = jsonObject.getJSONObject("main");
                            Double tempItem = jsonObjectMain.getDouble("temp") - 273.15;
                            jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            jsonArrayWeather = jsonObject.getJSONArray("weather");
                            String descriptionItem = jsonObjectWeather.getString("description");
                            String weatherInfoItem = jsonObjectWeather.getString("main");
                            weatherItems.add(new FutureWeatherItem(tempItem,weatherInfoItem,descriptionItem,timeItem,0));
                            i++;

                        }
                        cityItems.add(new CityItem(cityItems.size(), returnPhotoId(description), cityName, "0", temp,speed,deg,humidity,visibility,lon,lat,pressure,weatherItems,timezone));
                        cityAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.toString().contains("NoConnectionError"))
                    {
                        Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Wrong data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);

            saveDataTime();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Empty input", Toast.LENGTH_SHORT).show();
        }

    }

    private int i;

    @SuppressLint("NotifyDataSetChanged")
    public void updateWeatherDetails() {
        for (i = 1; i < cityItems.size(); i++) {
            String tempUrl = "";
            tempUrl = url + "?q=" + cityItems.get(i).getCityName() + "&appid=" + appId;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                final int j = i;

                @Override
                public void onResponse(String response) {
                    Log.d("response", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("list");
                        JSONObject jsonObjectZero = jsonArray.getJSONObject(0);
                        JSONArray jsonArrayWeather = jsonObjectZero.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        String time = jsonObjectZero.getString("dt_txt");
                        JSONObject jsonObjectMain = jsonObjectZero.getJSONObject("main");
                        JSONObject jsonObjectWind = jsonObjectZero.getJSONObject("wind");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        double humidity = jsonObjectMain.getDouble("humidity");
                        double speed = jsonObjectWind.getDouble("speed");
                        double deg = jsonObjectWind.getDouble("deg");
                        double visibility = Math.round(jsonObjectZero.getDouble("visibility")/10000*100);
                        double pressure = jsonObjectMain.getDouble("pressure");

                        JSONObject jsonCity = jsonResponse.getJSONObject("city");
                        JSONObject jsonCityCoord = jsonCity.getJSONObject("coord");

                        double lon = jsonCityCoord.getDouble("lon");
                        double lat = jsonCityCoord.getDouble("lat");
                        int timezone = jsonCity.getInt("timezone")/3600;
                        Log.d("timezone",timezone+"");
                        int i = 1;
                        ArrayList<FutureWeatherItem> weatherItems = new ArrayList<FutureWeatherItem>();
                        while(i<40)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String timeItem = jsonObject.getString("dt_txt");
                            jsonObjectMain = jsonObject.getJSONObject("main");
                            Double tempItem = jsonObjectMain.getDouble("temp") - 273.15;
                            jsonArrayWeather = jsonObject.getJSONArray("weather");
                            jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String descriptionItem = jsonObjectWeather.getString("description");
                            String weatherInfoItem = jsonObjectWeather.getString("main");
                            weatherItems.add(new FutureWeatherItem(tempItem,weatherInfoItem,descriptionItem,timeItem,cityItems.get(j).getWeatherItems().get(i-1).getId()));
                            i++;

                        }
                        cityItems.get(j).setHumidity(humidity);
                        cityItems.get(j).setVisibility(visibility);
                        cityItems.get(j).setWindSpeed(speed);
                        cityItems.get(j).setWindDeg(deg);
                        cityItems.get(j).setTemp(temp);
                        cityItems.get(j).setLatitude(lat);
                        cityItems.get(j).setLongitude(lon);
                        cityItems.get(j).setPressure(pressure);
                        cityItems.get(j).setWeatherItems(weatherItems);
                        cityItems.get(j).setImageResource(returnPhotoId(description));
                        Log.d("hello", cityItems.get(j).getCityName() + " " + cityItems.get(j).getTemp() + " " + j);
                        cityAdapter.notifyDataSetChanged();
                        if (cityItems.get(j).getFavStatus().equals("1")) {
                            favCityDB.updateCity(cityItems.get(j));
                        }
                    } catch (JSONException e) {
                        Log.d("check", e.toString());
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.toString().contains("NoConnectionError"))
                    {
                        Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

    public void saveDataTime() {
        SharedPreferences sharedpreferences = getSharedPreferences("lastUpdate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        LocalDateTime now = LocalDateTime.now();
        Log.d("time", now.toString());
        editor.putString("Time", now.toString());
        editor.commit();
    }

    public String loadDataTime() {
        SharedPreferences sharedpreferences = getSharedPreferences("lastUpdate", Context.MODE_PRIVATE);
        return sharedpreferences.getString("Time", "");
    }

    public void saveTempType(boolean type) {
        SharedPreferences sharedpreferences = getSharedPreferences("lastUpdate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("temp", type);
        editor.commit();
    }

    public boolean loadTempType() {
        SharedPreferences sharedpreferences = getSharedPreferences("lastUpdate", Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean("temp", true);
    }


    @SuppressLint("MissingPermission")
    public void getLocation() {
        Log.d("response", "response=waiting");
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location!=null)
                {
                    String tempUrl = "";
                    tempUrl = url + "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=" + appId;

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(String response) {
                            Log.d("response", response);
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                JSONArray jsonArray = jsonResponse.getJSONArray("list");
                                JSONObject jsonObjectZero = jsonArray.getJSONObject(0);
                                JSONArray jsonArrayWeather = jsonObjectZero.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                String description = jsonObjectWeather.getString("description");
                                String time = jsonObjectZero.getString("dt_txt");
                                JSONObject jsonObjectMain = jsonObjectZero.getJSONObject("main");
                                JSONObject jsonObjectWind = jsonObjectZero.getJSONObject("wind");
                                double temp = jsonObjectMain.getDouble("temp") - 273.15;
                                double humidity = jsonObjectMain.getDouble("humidity");
                                double speed = jsonObjectWind.getDouble("speed");
                                double deg = jsonObjectWind.getDouble("deg");
                                double visibility = Math.round(jsonObjectZero.getDouble("visibility")/10000*100);
                                double pressure = jsonObjectMain.getDouble("pressure");

                                JSONObject jsonCity = jsonResponse.getJSONObject("city");
                                JSONObject jsonCityCoord = jsonCity.getJSONObject("coord");

                                double lon = jsonCityCoord.getDouble("lon");
                                double lat = jsonCityCoord.getDouble("lat");
                                int timezone = jsonCity.getInt("timezone")/3600;
                                int i = 1;
                                ArrayList<FutureWeatherItem> weatherItems = new ArrayList<FutureWeatherItem>();
                                while(i<40)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String timeItem = jsonObject.getString("dt_txt");
                                    jsonObjectMain = jsonObject.getJSONObject("main");
                                    Double tempItem = jsonObjectMain.getDouble("temp") - 273.15;
                                    jsonArrayWeather = jsonObject.getJSONArray("weather");
                                    jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                    String descriptionItem = jsonObjectWeather.getString("description");
                                    String weatherInfoItem = jsonObjectWeather.getString("main");
                                    if(cityItems.get(0).getWeatherItems().size()!=0)
                                    {
                                        weatherItems.add(new FutureWeatherItem(tempItem,weatherInfoItem,descriptionItem,timeItem,cityItems.get(0).getWeatherItems().get(i-1).getId()));
                                    }
                                    else
                                    {
                                        weatherItems.add(new FutureWeatherItem(tempItem,weatherInfoItem,descriptionItem,timeItem,i));
                                    }
                                    i++;

                                }
                                cityItems.get(0).setHumidity(humidity);
                                cityItems.get(0).setVisibility(visibility);
                                cityItems.get(0).setWindSpeed(speed);
                                cityItems.get(0).setWindDeg(deg);
                                cityItems.get(0).setTemp(temp);
                                cityItems.get(0).setLatitude(lat);
                                cityItems.get(0).setLongitude(lon);
                                cityItems.get(0).setPressure(pressure);
                                cityItems.get(0).setWeatherItems(weatherItems);
                                cityItems.get(0).setImageResource(returnPhotoId(description));
                                cityAdapter.notifyDataSetChanged();
                                favCityDB.updateCity(cityItems.get(0));
                                Toast.makeText(MainActivity.this, "Data refreshed successfully", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error.toString().contains("NoConnectionError"))
                            {
                                Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getLocation();
    }

    public static int returnPhotoId(String description)
    {
        switch(description)
        {
            case "clear sky":
                return R.drawable.sun;
            case "few clouds":
                return R.drawable.partlycouldy;
            case "scattered clouds":
                return R.drawable.cloud;
            case "broken clouds":
                return R.drawable.mostcloud;
            case "shower rain":
                return R.drawable.rain;
            case "rain":
                return R.drawable.rain;
            case "thunderstorm":
                return R.drawable.thunder;
            case "snow":
                return R.drawable.snow;
            case "mist":
                return R.drawable.fog;
            case "overcast clouds":
                return R.drawable.mostcloud;
            case "light rain":
                return R.drawable.rain;

        }
        return R.drawable.cloud;
    }
}