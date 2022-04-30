package com.example.weatherapp;

import androidx.recyclerview.widget.*;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.weatherapp.Models.*;
import com.google.gson.Gson;
import java.util.*;

public class ForecastActivity extends Activity {

    private Forecast forecast;
    private List<FullDayCard> mFullDays;
    private TextView mCurrentTemp;
    private TextView mTonightTemp;
    private TextView mDetailedForecast;
    private ImageView mTonightImage;
    private ImageView mCurrentImage;
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

        mCurrentTemp = findViewById(R.id.currentTemp);
        mTonightTemp = findViewById(R.id.tonightTemp);
        mDetailedForecast = findViewById(R.id.detailedForecast);
        mTonightImage = findViewById(R.id.tonightIcon);
        mCurrentImage = findViewById(R.id.currentIcon);

        setUpCurrentDayViews();
        createRecyclerView();
    }

    private void setUpCurrentDayViews() {
        if(!mFullDays.get(0).getName().equals("Today") && !mFullDays.get(0).getName().equals("This Afternoon")) {
            mCurrentTemp.setText(String.format(mCurrentTemp.getText().toString(), mFullDays.get(0).getNightTemp(), mFullDays.get(0).getTempUnit()));
            ((ViewGroup)mTonightTemp.getParent()).removeView(mTonightTemp);
            ((ViewGroup)mTonightImage.getParent()).removeView(mTonightImage);
            mDetailedForecast.setText(mFullDays.get(0).getDetailedNightForecast());

            String shortForecast = mFullDays.get(0).getNightShortForecast();

            if(shortForecast.contains("Cloudy")) {
                mCurrentImage.setImageResource(R.drawable.night_cloudy);
            }
            else if(shortForecast.contains("Clear")) {
                mCurrentImage.setImageResource(R.drawable.night);
            }
            else if(shortForecast.contains("Rain") || shortForecast.contains("Showers")) {
                mCurrentImage.setImageResource(R.drawable.rain);
            }
        }
        else {
            mCurrentTemp.setText(String.format(mCurrentTemp.getText().toString(), mFullDays.get(0).getDayTemp(), mFullDays.get(0).getTempUnit()));
            mTonightTemp.setText(String.format(mTonightTemp.getText().toString(), mFullDays.get(0).getNightTemp(), mFullDays.get(0).getTempUnit()));
            mDetailedForecast.setText(mFullDays.get(0).getDetailedDayForecast());

            String shortForecastNight = mFullDays.get(0).getNightShortForecast();
            String shortForecastDay = mFullDays.get(0).getDayShortForecast();

            if(shortForecastNight.contains("Cloudy")) {
                mTonightImage.setImageResource(R.drawable.night_cloudy);
            }
            else if(shortForecastNight.contains("Clear")) {
                mTonightImage.setImageResource(R.drawable.night);
            }
            else if(shortForecastNight.contains("Rain") || shortForecastNight.contains("Showers")) {
                mTonightImage.setImageResource(R.drawable.rain);
            }

            if(shortForecastDay.contains("Cloudy")) {
                mCurrentImage.setImageResource(R.drawable.day_cloudy);
            }
            else if(shortForecastDay.contains("Clear") || shortForecastDay.contains("Sunny")) {
                mCurrentImage.setImageResource(R.drawable.sunny);
            }
            else if(shortForecastDay.contains("Rain") || shortForecastNight.contains("Showers")) {
                mCurrentImage.setImageResource(R.drawable.rain);
            }
        }
    }

    private void createRecyclerView() {
        mFullDays.remove(0);

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
        boolean nightStart = false;

        while(cursor != periods.size()) {
            String name;
            Period day = null;
            Period night = null;

            if(cursor == 0) {
                name = periods.get(cursor).getName();
            }
            else {
                if(periods.get(cursor).getName().contains("Night")){
                    name = periods.get(cursor - 1).getName();
                }
                else {
                    name = periods.get(cursor).getName();
                }
            }

            if(cursor == 0) {
                if(name.equals("Tonight") || name.equals("Overnight")) {
                    night = periods.get(cursor);
                    cursor++;
                    nightStart = true;
                }
                else {
                    day = periods.get(cursor);
                    night = periods.get(cursor + 1);
                    cursor++;
                }
            }
            else if (nightStart){
                day = periods.get(cursor - 1);
                night = periods.get(cursor);
                cursor++;
            }
            else {
                day = periods.get(cursor);
                night = periods.get(cursor + 1);
                cursor++;
            }

            mFullDays.add(new FullDayCard(day, night, name));

            if(cursor != periods.size()){
                cursor++;
            }
        }
    }
}