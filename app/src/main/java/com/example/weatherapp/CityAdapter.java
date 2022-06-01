package com.example.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private ArrayList<CityItem> cityItems;
    private Context context;
    private FavCityDB favCityDB;
    private boolean isCelcius;
    private FragmentManager fragmentManager;
    private RecyclerViewClickListener listener;

    public CityAdapter(ArrayList<CityItem> cityItems, Context context, boolean isCelcius, RecyclerViewClickListener listener)
    {
        this.cityItems = cityItems;
        this.context = context;
        this.isCelcius = isCelcius;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        favCityDB = new FavCityDB(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityAdapter.ViewHolder holder, int position) {
        final CityItem cityItem = cityItems.get(position);

        holder.imageView.setImageResource(cityItem.getImageResource());
        holder.cityName.setText(cityItem.getCityName());
        holder.tempValue.setText(getTempeture(cityItem.getTemp()));
        holder.favButton.setVisibility(View.VISIBLE);
        if(position == 0)
        {
            holder.favButton.setVisibility(View.INVISIBLE);
        }
        if(cityItem.getFavStatus().equals("1"))
        {
            holder.favButton.setBackgroundResource(R.drawable.ic_baseline_yellow_24);
        }
        else if(cityItem.getFavStatus().equals("0"))
        {
            holder.favButton.setBackgroundResource(R.drawable.ic_baseline_shadow_24);
        }
    }

    private void readCursorData(CityItem cityItem, ViewHolder viewHolder) {
    }



    @Override
    public int getItemCount() {
        return cityItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView cityName,tempValue;
        Button favButton;
        FragmentManager f = fragmentManager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            cityName = itemView.findViewById(R.id.titleTextView);
            favButton = itemView.findViewById(R.id.favoriteButton);
            tempValue = itemView.findViewById(R.id.temp_value);
            itemView.setOnClickListener(this);
            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    CityItem cityItem = cityItems.get(position);

                    if(cityItem.getFavStatus().equals("0"))
                    {
                        cityItem.setFavStatus("1");
                        cityItem.setKey_id(favCityDB.insertIntoTheDatabase(cityItem.getCityName(),cityItem.getTemp(), cityItem.getWindSpeed(), cityItem.getWindDeg(), cityItem.getHumidity(),cityItem.getVisibility(),cityItem.getLongitude(),cityItem.getLatitude(), cityItem.getPressure(),cityItem.getWeatherItems(), cityItem.getTimezone(),cityItem.getImageResource(),false));
                        favButton.setBackgroundResource(R.drawable.ic_baseline_yellow_24);
                    }
                    else {
                        cityItem.setFavStatus("0");
                        {
                            favCityDB.removeFromTheDatabase(cityItem.getKey_id());
                            favButton.setBackgroundResource(R.drawable.ic_baseline_shadow_24);
                        }
                    }
                }
            });

        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
    public boolean getIsCelcius() {
        return isCelcius;
    }

    public void setIsCelcius(boolean celcius) {
        isCelcius = celcius;
    }


    private String getTempeture(double tempInC)
    {
        if(isCelcius)
        {
            return Math.round(tempInC)+"°C";
        }
        else
        {
            return Math.round(tempInC*9/5+32)+"°F";
        }
    }

    public interface RecyclerViewClickListener{
        void onClick(View view, int position);
    }
}
