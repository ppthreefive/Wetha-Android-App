package com.example.weatherapp.Repository;

import com.example.weatherapp.Models.*;
import com.example.weatherapp.Network.*;
import io.reactivex.Observable;

public class WeatherRepository {
    private final WeatherService mService;

    public WeatherRepository() {
        this.mService = ClientInstance.getClient();
    }

    public Observable<Grid> executeGridDetailsApi(double latitude, double longitude) {
        return mService.getGridDetails(latitude, longitude);
    }

    public Observable<Forecast> executeForecastApi(String gridId, String gridX, String gridY) {
        return mService.getForecast(gridId, gridX, gridY);
    }
}