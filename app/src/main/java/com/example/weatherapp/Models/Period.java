package com.example.weatherapp.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Period {
    private int number;
    private String name;
    private String startTime;
    private String endTime;
    private boolean isDayTime;
    private int temperature;
    private String temperatureUnit;
    private String temperatureTrend;
    private String windSpeed;
    private String windDirection;
    private String icon;
    private String shortForecast;
    private String detailedForecast;
}
