package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.*;
import com.example.weatherapp.Models.*;
import com.example.weatherapp.Views.Adapters.ForecastActivityAdapter;
import com.example.weatherapp.Views.ViewModels.ForecastViewModel;
import com.google.gson.Gson;
import java.util.*;

public class ForecastActivity extends AppCompatActivity {

    private List<FullDayCard> mFullDays;
    private TextView mCurrentTemp;
    private TextView mTonightTemp;
    private TextView mDetailedForecast;
    private ImageView mTonightImage;
    private ImageView mCurrentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        ForecastViewModel mForecastViewModel = new ViewModelProvider(this).get(ForecastViewModel.class);
        mFullDays = mForecastViewModel.combineFullDaysInForecast(new Gson().fromJson(getIntent()
                .getStringExtra("forecast"), Forecast.class));

        mCurrentTemp = findViewById(R.id.currentTemp);
        mTonightTemp = findViewById(R.id.tonightTemp);
        mDetailedForecast = findViewById(R.id.detailedForecast);
        mTonightImage = findViewById(R.id.tonightIcon);
        mCurrentImage = findViewById(R.id.currentIcon);

        setUpCurrentDayViews();
        createRecyclerView();
    }

    private void setUpCurrentDayViews() {
        if(mFullDays.get(0).getDayShortForecast() == null) {
            mCurrentTemp.setText(String.format(mCurrentTemp.getText().toString(), getString(R.string.tonight),
                    mFullDays.get(0).getNightTemp(), mFullDays.get(0).getTempUnit()));
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
            mCurrentTemp.setText(String.format(mCurrentTemp.getText().toString(), getString(R.string.today),
                    mFullDays.get(0).getDayTemp(), mFullDays.get(0).getTempUnit()));
            mTonightTemp.setText(String.format(mTonightTemp.getText().toString(), mFullDays.get(0).getNightTemp(),
                    mFullDays.get(0).getTempUnit()));
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

        RecyclerView recyclerView = findViewById(R.id.forecastRecyclerView);
        ForecastActivityAdapter adapter = new ForecastActivityAdapter(mFullDays);
        adapter.onClickListener(v -> {

        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}