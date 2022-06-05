package com.example.weatherapp.Views.ViewModels;

import android.content.Context;
import android.location.Geocoder;
import android.util.*;
import androidx.lifecycle.*;
import com.example.weatherapp.Models.*;
import com.example.weatherapp.Repository.WeatherRepository;
import java.util.Locale;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class MainViewModel extends ViewModel {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<Forecast> mForecastGeo = new MutableLiveData<>();
    private MutableLiveData<Forecast> mForecastCoor = new MutableLiveData<>();
    private WeatherRepository mWeatherRepository = new WeatherRepository();

    public LiveData<Forecast> getAllDataWithGeocoder(Context context, String location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        BehaviorSubject<Geocoder> subject = BehaviorSubject.create();
        subject.onNext(geocoder);
        disposables.add(subject.hide()
                .map(geo -> geocoder.getFromLocationName(location, 1))
                .flatMap(response -> mWeatherRepository.executeGridDetailsApi(response.get(0).getLatitude(), response.get(0).getLongitude()))
                .flatMap(response -> mWeatherRepository.executeForecastApi(response.getProperties().getGridId(),
                        response.getProperties().getGridX(), response.getProperties().getGridY()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> mForecastGeo.setValue(result),
                        throwable -> mForecastGeo.setValue(null)
                )
        );

        return mForecastGeo;
    }

    public LiveData<Forecast> getAllDataWithCoordinates(Double latitude, Double longitude) {
        if(latitude != null && longitude != null) {
            disposables.add(mWeatherRepository.executeGridDetailsApi(latitude, longitude)
                    .flatMap(response -> mWeatherRepository.executeForecastApi(response.getProperties().getGridId(),
                            response.getProperties().getGridX(), response.getProperties().getGridY()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> mForecastCoor.setValue(result),
                            throwable -> mForecastCoor.setValue(null)
                    )
            );
        }

        return mForecastCoor;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}