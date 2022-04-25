package com.example.weatherapp.Network;

import com.example.weatherapp.Models.Forecast;
import com.example.weatherapp.Models.Grid;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetWeatherService {
    @GET("/points/{latitude},{longitude}")
    Call<Grid> getGridDetails(@Path("latitude") double latitude,
                              @Path("longitude") double longitude);

    @GET("/gridpoints/{gridId}/{gridX},{gridY}/forecast")
    Call<Forecast> getForecast(@Path("gridId") String gridId,
                               @Path("gridX") String gridX,
                               @Path("gridY") String gridY);
}
