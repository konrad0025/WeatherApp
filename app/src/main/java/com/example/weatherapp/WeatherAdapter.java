package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    ArrayList<FutureWeatherItem> weatherItems;
    Context context;
    boolean isCelcius;

    public WeatherAdapter(ArrayList<FutureWeatherItem> weatherItems, Context context, boolean isCelcius) {
        this.weatherItems = weatherItems;
        this.context = context;
        this.isCelcius = isCelcius;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.imageView.setImageResource(weatherItems.get(position).getWeather());
        holder.textViewDescription.setText(weatherItems.get(position).getDescription());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(weatherItems.get(position).getDateTime(), formatter);
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date d = f.parse(weatherItems.get(position).getDateTime());
            DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat time = new SimpleDateFormat("hh:mm a");
            holder.textViewTime.setText(date.format(d).toString()+"\n"+time.format(d).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(isCelcius)
        {
            holder.textViewTemp.setText(Math.round(weatherItems.get(position).getTemp())+"°C");
        }
        else
        {
            holder.textViewTemp.setText(Math.round(weatherItems.get(position).getTemp()*9/5+32)+"°F");
        }

    }

    @Override
    public int getItemCount() {
        return weatherItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTemp,textViewTime,textViewDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewDescription = itemView.findViewById(R.id.description);
            textViewTemp = itemView.findViewById(R.id.temp);
            textViewTime = itemView.findViewById(R.id.time);
        }
    }
}
