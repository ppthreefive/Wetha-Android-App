package com.example.weatherapp.Views.ViewModels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.*;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.example.weatherapp.Models.*;
import com.example.weatherapp.Repository.WeatherRepository;
import java.util.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class MainViewModel extends AndroidViewModel {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<Forecast> mForecastGeo = new MutableLiveData<>();
    private final MutableLiveData<Forecast> mForecastCoor = new MutableLiveData<>();
    private final WeatherRepository mWeatherRepository = new WeatherRepository();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Forecast> getAllDataWithGeocoder(Context context, String location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        BehaviorSubject<Geocoder> subject = BehaviorSubject.create();
        subject.onNext(geocoder);
        disposables.add(subject.hide()
                .map(geo -> geo.getFromLocationName(location, 1))
                .flatMap(response -> mWeatherRepository.executeGridDetailsApi(response.get(0).getLatitude(), response.get(0).getLongitude()))
                .flatMap(response -> mWeatherRepository.executeForecastApi(response.getProperties().getGridId(),
                        response.getProperties().getGridX(), response.getProperties().getGridY()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        mForecastGeo::setValue,
                        throwable -> mForecastGeo.setValue(null)
                )
        );

        return mForecastGeo;
    }

    public LiveData<Forecast> getAllDataWithCoordinates() {
        LocationManager locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        BehaviorSubject<LocationManager> subject = BehaviorSubject.create();
        subject.onNext(locationManager);

        disposables.add(subject.hide()
                .map(loc -> {
                    @SuppressLint("MissingPermission") Location gpsLocation = loc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    @SuppressLint("MissingPermission") Location networkLocation = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if(gpsLocation != null) { return gpsLocation; }
                    if(networkLocation != null) { return networkLocation; }

                    return null;
                })
                .flatMap(response -> mWeatherRepository.executeGridDetailsApi(response.getLatitude(), response.getLongitude()))
                .flatMap(response -> mWeatherRepository.executeForecastApi(response.getProperties().getGridId(),
                        response.getProperties().getGridX(), response.getProperties().getGridY()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        mForecastGeo::setValue,
                        throwable -> mForecastGeo.setValue(null)
                )
        );

        return mForecastCoor;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
