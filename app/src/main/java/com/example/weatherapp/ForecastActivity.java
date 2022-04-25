package com.example.weatherapp;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.os.Bundle;
import com.example.weatherapp.Models.*;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class ForecastActivity extends Activity {

    private Forecast forecast;
    private List<FullDayCard> mFullDays;
    private RecyclerView mRecyclerView;
    private ForecastActivityAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mFullDays = new ArrayList<>();

        Gson gson = new Gson();
        forecast = gson.fromJson(getIntent().getStringExtra("forecast"), Forecast.class);
        combineFullDaysInForecast();


        createRecyclerView();
    }

    private void createRecyclerView() {
        mRecyclerView = findViewById(R.id.forecastRecyclerView);
        mAdapter = new ForecastActivityAdapter(mFullDays);
        mAdapter.onClickListener(v -> {

        });
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void combineFullDaysInForecast() {
        List<Period> periods = forecast.getProperties().getPeriods();

        int cursor = 0;

        while(cursor != periods.size()) {
            String name;

            if(cursor == 0) {
                name = "Today";
            }
            else {
                name = periods.get(cursor).getName();
            }

            Period day = periods.get(cursor);
            Period night = periods.get(cursor + 1);
            cursor++;

            mFullDays.add(new FullDayCard(name, day.getIcon(), night.getIcon(), day.getTemperature(),
                    night.getTemperature(), day.getShortForecast(), night.getShortForecast(), day.getTemperatureUnit()));

            if(cursor != periods.size()){
                cursor++;
            }
        }
    }
}