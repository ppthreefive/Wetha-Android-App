package com.example.weatherapp.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullDayCard {
    private String name;
    private String dayImageLocation;
    private String nightImageLocation;
    private Integer dayTemp;
    private Integer nightTemp;
    private String dayShortForecast;
    private String nightShortForecast;
    private String tempUnit;
    private String detailedNightForecast;
    private String detailedDayForecast;

    public FullDayCard(Period day, Period night, String name) {
        this.name = name;

        if(day != null) {
            this.dayImageLocation = day.getIcon();
            this.dayTemp = day.getTemperature();
            this.dayShortForecast = day.getShortForecast();
            this.detailedDayForecast = day.getDetailedForecast();
            this.tempUnit = day.getTemperatureUnit();
        }
        if(night != null) {
            this.nightImageLocation = night.getIcon();
            this.nightTemp = night.getTemperature();
            this.nightShortForecast = night.getShortForecast();
            this.detailedNightForecast = night.getDetailedForecast();
            this.tempUnit = night.getTemperatureUnit();
        }
    }
}