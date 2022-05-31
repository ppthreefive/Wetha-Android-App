package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import com.example.weatherapp.Models.*;
import com.example.weatherapp.Network.*;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import java.util.*;
import retrofit2.*;

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

        editCity = findViewById(R.id.editCity);
        editState = findViewById(R.id.editState);

        mButtonEnter = findViewById(R.id.btnEnter);
        mButtonEnter.setOnClickListener(view -> manuallyEnteredAction());

        mButtonUseGps = findViewById(R.id.btnUseGps);
        mButtonUseGps.setOnClickListener(view -> gpsBtnAction());
    }

    private void manuallyEnteredAction() {
        if(!editCity.getText().toString().isEmpty() && !editState.getText().toString().isEmpty()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                try {
                    mCoordinates = getLatLong(editCity.getText().toString() + ", " + editState.getText().toString());
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(findViewById(android.R.id.content), R.string.error_network, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.retry, view -> manuallyEnteredAction()).show();
                }
                runOnUiThread(() -> {
                    if(mCoordinates != null) {
                        callGridService(mCoordinates.first, mCoordinates.second);
                    }
                });
            });
        }
        else {
            if(editCity.getText().toString().isEmpty()) {
                editCity.setError(getApplicationContext().getResources().getString(R.string.error_empty_city));
            }
            if(editState.getText().toString().isEmpty()) {
                editState.setError(getApplicationContext().getResources().getString(R.string.error_empty_state));
            }
        }
    }

    private void gpsBtnAction() {
        String[] PERMISSIONS;
        PERMISSIONS = new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE };

        if(ActivityCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), PERMISSIONS[2]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 0);
        }
        else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                Location gpsLocation;
                Location networkLocation;

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
                    Snackbar.make(findViewById(android.R.id.content), R.string.error_gps, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.retry, view -> gpsBtnAction()).show();
                }
            });
        }
    }

    private void startForecastActivity() {
        Intent intent = new Intent(getApplicationContext(), ForecastActivity.class);
        Gson gson = new Gson();
        String forecastJson = gson.toJson(mForecast);
        intent.putExtra("forecast", forecastJson);
        startActivity(intent);
    }

    private Pair<Double, Double> getLatLong(String location) throws Exception {
        Pair<Double, Double> latLong;

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;

        addresses = geocoder.getFromLocationName(location, 1);
        latLong = new Pair<>(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        Log.d("Coordinates", latLong.toString());

        return latLong;
    }

    private void callGridService(double latitude, double longitude) {
        GetWeatherService service = RetrofitClientInstance.getRetrofitInstance().create(GetWeatherService.class);
        Call<Grid> call = service.getGridDetails(latitude, longitude);
        call.enqueue(new Callback<Grid>() {
            @Override
            public void onResponse(@NonNull Call<Grid> call, @NonNull Response<Grid> response) {
                if(!response.isSuccessful()) {
                    Log.d("Code", "" + response.code());
                }
                else {
                    Log.d("Response", "SUCCESS");
                    mGrid = response.body();

                    Log.d("Response", response.body() != null ? response.body().toString() : "");
                    callForecastService();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Grid> call, @NonNull Throwable t) {
                Log.e("Failure", t.getMessage());
            }
        });
    }

    private void callForecastService() {
        GetWeatherService service = RetrofitClientInstance.getRetrofitInstance().create(GetWeatherService.class);
        Call<Forecast> call = service.getForecast(mGrid.getProperties().getGridId(), mGrid.getProperties().getGridX(), mGrid.getProperties().getGridY());
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(@NonNull Call<Forecast> call, @NonNull Response<Forecast> response) {
                if(!response.isSuccessful()) {
                    Log.d("Code", "" + response.code());
                }
                else {
                    Log.d("Response", "SUCCESS");
                    mForecast = response.body();

                    Log.d("Response", response.body() != null ? response.body().getProperties().toString() : "");
                    startForecastActivity();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Forecast> call, @NonNull Throwable t) {
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