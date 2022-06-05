package com.example.weatherapp.Network;

import com.example.weatherapp.Models.Forecast;
import com.example.weatherapp.Models.Grid;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WeatherService {
    @GET("/points/{latitude},{longitude}")
    Observable<Grid> getGridDetails(@Path("latitude") double latitude,
                                    @Path("longitude") double longitude);

    @GET("/gridpoints/{gridId}/{gridX},{gridY}/forecast")
    Observable<Forecast> getForecast(@Path("gridId") String gridId,
                               @Path("gridX") String gridX,
                               @Path("gridY") String gridY);
}
