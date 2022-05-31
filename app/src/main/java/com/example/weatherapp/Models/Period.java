package com.example.weatherapp.Models;

import java.util.Date;
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
    private Date startTime;
    private Date endTime;
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
