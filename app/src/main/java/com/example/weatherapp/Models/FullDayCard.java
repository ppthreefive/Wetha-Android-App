package com.example.weatherapp.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FullDayCard {
    private String name;
    private String dayImageLocation;
    private String nightImageLocation;
    private int dayTemp;
    private int nightTemp;
    private String dayShortForecast;
    private String nightShortForecast;
    private String tempUnit;
}