package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import com.example.weatherapp.Models.Forecast;
import com.example.weatherapp.Models.Grid;
import com.example.weatherapp.Network.GetWeatherService;
import com.example.weatherapp.Network.RetrofitClientInstance;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity implements LocationListener {

    private Grid mGrid;
    private Forecast mForecast;
    private Button mButtonEnter;
    private Button mButtonUseGps;
    private EditText editCity;
    private EditText editState;
    private Pair<Double, Double> mCoordinates;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editCity = (EditText) findViewById(R.id.editCity);
        editState = (EditText) findViewById(R.id.editState);

        mButtonEnter = (Button) findViewById(R.id.btnEnter);
        mButtonEnter.setOnClickListener(view -> {
            mCoordinates = getLatLong(editCity.getText().toString() + ", " + editState.getText().toString());

            callGridService(mCoordinates.first, mCoordinates.second);
        });

        mButtonUseGps = (Button) findViewById(R.id.btnUseGps);
        mButtonUseGps.setOnClickListener(view -> {
            Location gpsLocation;
            Location networkLocation;

            String[] PERMISSIONS;
            PERMISSIONS = new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE };

            if(ActivityCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS[2]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 0);
            }
            else {
                try {
                    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    Criteria criteria = new Criteria();
                    String bestProvider = String.valueOf(mLocationManager.getBestProvider(criteria, true));

                    gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (gpsLocation != null) {
                        callGridService(gpsLocation.getLatitude(), gpsLocation.getLongitude());
                        Log.d("GPS LOCATION", "" + gpsLocation.getLatitude() + ", " + gpsLocation.getLongitude());
                    }
                    else if (networkLocation != null) {
                        callGridService(networkLocation.getLatitude(), networkLocation.getLongitude());
                        Log.d("NETWORK LOCATION", "" + networkLocation.getLatitude() + ", " + networkLocation.getLongitude());
                    }
                    else {
                        mLocationManager.requestLocationUpdates(bestProvider, 1000, 0, MainActivity.this);
                        callGridService(mCoordinates.first, mCoordinates.second);
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //callGridService(33.366451, -111.963178);
    }

    private void startForecastActivity() {
        Intent intent = new Intent(getApplicationContext(), ForecastActivity.class);
        Gson gson = new Gson();
        String forecastJson = gson.toJson(mForecast);
        intent.putExtra("forecast", forecastJson);
        startActivity(intent);
    }

    private Pair<Double, Double> getLatLong(String location) {
        Pair<Double, Double> latLong;

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocationName(location, 1);
            latLong = new Pair<>(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            Log.d("Coordinates", latLong.toString());
        } catch (IOException e) {
            e.printStackTrace();
            latLong = null;
        }

        return latLong;
    }

    private void callGridService(double latitude, double longitude) {
        GetWeatherService service = RetrofitClientInstance.getRetrofitInstance().create(GetWeatherService.class);
        Call<Grid> call = service.getGridDetails(latitude, longitude);
        call.enqueue(new Callback<Grid>() {
            @Override
            public void onResponse(Call<Grid> call, Response<Grid> response) {
                if(!response.isSuccessful()) {
                    Log.d("Code", "" + response.code());
                }
                else {
                    Log.d("Response", "SUCCESS");
                    mGrid = response.body();

                    Log.d("Response", response.body().toString());
                    callForecastService();
                }
            }

            @Override
            public void onFailure(Call<Grid> call, Throwable t) {
                Log.e("Failure", t.getMessage());
            }
        });
    }

    private void callForecastService() {
        GetWeatherService service = RetrofitClientInstance.getRetrofitInstance().create(GetWeatherService.class);
        Call<Forecast> call = service.getForecast(mGrid.getProperties().getGridId(), mGrid.getProperties().getGridX(), mGrid.getProperties().getGridY());
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                if(!response.isSuccessful()) {
                    Log.d("Code", "" + response.code());
                }
                else {
                    Log.d("Response", "SUCCESS");
                    mForecast = response.body();

                    Log.d("Response", response.body().getProperties().toString());
                    startForecastActivity();
                }
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.e("Failure", t.getMessage());
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLocationManager.removeUpdates(this);
        mCoordinates = new Pair<>(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}