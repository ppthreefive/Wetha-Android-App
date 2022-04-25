package com.example.weatherapp.Models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ForecastProperties {
    private List<Period> periods;

    @Override
    public String toString() {
        String result = "";

        for (Period p : this.periods) {
            result += p.toString() + '\n';
        }

        return result;
    }
}
