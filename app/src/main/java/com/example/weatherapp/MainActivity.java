package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.util.*;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.example.weatherapp.Models.*;
import com.example.weatherapp.Views.ViewModels.MainViewModel;
import com.example.weatherapp.Views.Widgets.ProgressButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private View mButtonEnter;
    private ProgressButton mManualProgressButton;
    private View mButtonUseGps;
    private ProgressButton mGpsProgressButton;
    private EditText editCity;
    private EditText editState;
    private MainViewModel mMainViewModel;
    private boolean mIsManual = false;
    private boolean mIsGps = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mButtonEnter = findViewById(R.id.btnEnter);
        mManualProgressButton = new ProgressButton(MainActivity.this, mButtonEnter);
        mButtonEnter.setOnClickListener(view -> manuallyEnteredAction());

        mButtonUseGps = findViewById(R.id.btnUseGps);
        mGpsProgressButton = new ProgressButton(MainActivity.this, mButtonUseGps, getString(R.string.btn_UseGps));
        mButtonUseGps.setOnClickListener(view -> gpsBtnAction());

        editCity = findViewById(R.id.editCity);
        editState = findViewById(R.id.editState);
        editState.setOnEditorActionListener((textView, id, keyEvent) -> {
            if(id == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editState.getApplicationWindowToken(), 0);
                return true;
            }

            return false;
        });

        mMainViewModel.getAllDataWithGeocoder(this,null).observe(this, forecast -> {
            if(forecast == null) {
                if(mIsManual) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.error_not_supported, Snackbar.LENGTH_SHORT).show();
                }
                resetButtons();
                return;
            }

            startForecastActivity(forecast);
        });

        mMainViewModel.getAllDataWithCoordinates(null,null).observe(this, forecast -> {
            if(forecast == null) {
                if(mIsGps) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.error_not_supported, Snackbar.LENGTH_SHORT).show();
                }
                resetButtons();
                return;
            }

            startForecastActivity(forecast);
        });
    }

    private void manuallyEnteredAction() {
        mButtonEnter.setClickable(false);
        if(!editCity.getText().toString().isEmpty() && !editState.getText().toString().isEmpty()) {
            mManualProgressButton.buttonActivated();
            mIsManual = true;

            if(!isNetworkAvailable()) {
                resetButtons();
                Snackbar.make(findViewById(android.R.id.content), R.string.error_network, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.retry, view -> manuallyEnteredAction()).show();
                return;
            }

            mMainViewModel.getAllDataWithGeocoder(this, editCity.getText().toString() + ", " + editState.getText().toString()).getValue();

            return;
        }

        if(editCity.getText().toString().isEmpty()) {
            editCity.setError(getApplicationContext().getResources().getString(R.string.error_empty_city));
        }
        if(editState.getText().toString().isEmpty()) {
            editState.setError(getApplicationContext().getResources().getString(R.string.error_empty_state));
        }

        resetButtons();
    }

    private void gpsBtnAction() {
        mButtonUseGps.setClickable(false);
        mGpsProgressButton.buttonActivated();
        mIsGps = true;

        String[] PERMISSIONS;
        PERMISSIONS = new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE };

        if(ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS[2]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 0);
            resetButtons();
            return;
        }

        Pair<Double, Double> coordinates = mMainViewModel.getLocation();

        if(coordinates == null) {
            Snackbar.make(findViewById(android.R.id.content), R.string.error_gps, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.retry, view -> gpsBtnAction()).show();
            return;
        }

        mMainViewModel.getAllDataWithCoordinates(coordinates.first, coordinates.second).getValue();
    }

    private void startForecastActivity(Forecast forecast) {
        if(mIsManual) {
            mManualProgressButton.buttonFinished();
        }
        else if(mIsGps) {
            mGpsProgressButton.buttonFinished();
        }

        Intent intent = new Intent(getApplicationContext(), ForecastActivity.class);
        intent.putExtra("forecast", new Gson().toJson(forecast));
        new Handler().postDelayed(this::resetButtons, 500);
        startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void resetButtons() {
        mIsManual = false;
        mManualProgressButton.buttonReset();
        mIsGps = false;
        mGpsProgressButton.buttonReset();
        mButtonUseGps.setClickable(true);
        mButtonEnter.setClickable(true);
    }
}