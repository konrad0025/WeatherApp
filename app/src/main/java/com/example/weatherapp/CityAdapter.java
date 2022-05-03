package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private ArrayList<CityItem> cityItems;
    private Context context;
    private FavCityDB favCityDB;

    public CityAdapter(ArrayList<CityItem> cityItems, Context context)
    {
        this.cityItems = cityItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        favCityDB = new FavCityDB(context);
        /*SharedPreferences prefs = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart",true);
        if(firstStart)
        {
            createTableOnFirstStart();
        }*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityAdapter.ViewHolder holder, int position) {
        final CityItem cityItem = cityItems.get(position);
        
        //readCursorData(cityItem,holder);
        holder.imageView.setImageResource(cityItem.getImageResource());
        holder.cityName.setText(cityItem.getCityName());
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
        /*Cursor cursor = favCityDB.readAllData(cityItem.getKey_id());
        SQLiteDatabase db = favCityDB.getReadableDatabase();
        try{
            while(cursor.moveToNext())
            {
                String itemFavStatus = cursor.getString(cursor.getColumnIndex("fStatus"));
                cityItem.setFavStatus(itemFavStatus);

                if(itemFavStatus != null && itemFavStatus.equals("1")){
                    viewHolder.favButton.setBackgroundResource(R.drawable.ic_baseline_yellow_24);
                }
                else if( itemFavStatus != null && itemFavStatus.equals("0"))
                {
                    viewHolder.favButton.setBackgroundResource(R.drawable.ic_baseline_shadow_24);
                }
            }
        } finally {
            if (cursor != null && cursor.isClosed())
            {
                cursor.close();
            }
            db.close();
        }*/
    }

    @Override
    public int getItemCount() {
        return cityItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView cityName;
        Button favButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            cityName = itemView.findViewById(R.id.titleTextView);
            favButton = itemView.findViewById(R.id.favoriteButton);

            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    CityItem cityItem = cityItems.get(position);

                    if(cityItem.getFavStatus().equals("0"))
                    {
                        cityItem.setFavStatus("1");
                        cityItem.setKey_id(favCityDB.insertIntoTheDatabase(cityItem.getCityName()));
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
    }
}
