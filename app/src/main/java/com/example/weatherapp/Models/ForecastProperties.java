package com.example.weatherapp.Models;

import androidx.annotation.NonNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ForecastProperties {
    private List<Period> periods;

    @NonNull
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Period p : this.periods) {
            result.append(p.toString()).append('\n');
        }

        return result.toString();
    }
}
