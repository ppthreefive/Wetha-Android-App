package com.example.weatherapp.Views.ViewModels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.weatherapp.Models.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ForecastViewModel extends AndroidViewModel {

    public ForecastViewModel(@NonNull Application application) {
        super(application);
    }

    public List<FullDayCard> combineFullDaysInForecast(Forecast forecast) {
        List<FullDayCard> fullDays = new ArrayList<>();
        List<Period> periods = forecast.getProperties().getPeriods();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", Locale.US);

        for(int i = 0; i < periods.size(); i++) {
            String name;
            Period day = null;
            Period night;

            if(i != periods.size() - 1 && isTheSameDay(periods.get(i).getStartTime(), periods.get(i + 1).getStartTime())
                    && !("Overnight").equals(periods.get(i).getName())) {

                name = simpleDateformat.format(periods.get(i).getStartTime());
                day = periods.get(i);
                night = periods.get(i + 1);
                i = i + 1;
            }
            else {
                name = simpleDateformat.format(periods.get(i).getStartTime());
                night = periods.get(i);
            }

            fullDays.add(new FullDayCard(day, night, name));
        }

        if(fullDays.get(fullDays.size() - 1).getDayShortForecast() == null) {
            fullDays.remove(fullDays.size() - 1);
        }

        return fullDays;
    }

    private boolean isTheSameDay(Date first, Date second) {
        Calendar calendarOne = Calendar.getInstance();
        Calendar calendarTwo = Calendar.getInstance();

        calendarOne.setTime(first);
        calendarTwo.setTime(second);

        return calendarOne.get(Calendar.DAY_OF_WEEK) == calendarTwo.get(Calendar.DAY_OF_WEEK);
    }
}
