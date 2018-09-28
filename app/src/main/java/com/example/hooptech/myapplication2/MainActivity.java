package com.example.hooptech.myapplication2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.hooptech.myapplication2.GetRoutesQuery;
import android.widget.TextView;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import com.apollographql.apollo.fetcher.ResponseFetcher;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import com.apollographql.apollo.ApolloCallback;
import com.apollographql.apollo.ApolloClient;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;


public class MainActivity extends AppCompatActivity implements  LocationListener {

    Button getLocationBtn, clear;
    TextView locationText;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationBtn = findViewById(R.id.getLocationBtn);
        clear = findViewById(R.id.clr);
        locationText = findViewById(R.id.locationText);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }


        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationText.setText("Coordinates");
            }
        });
        gpsGetRoutesCall.enqueue(new ApolloCall.Callback<GetRoutesQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<GetRoutesQuery.Data> response) {
                GetRoutesQuery.Data data = response.data();
                Log.e("gps", " " + data);
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e("gpsf",e.getMessage());
            }
        });
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            locationText.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude());
        } catch (Exception e) {
            locationText.setText("" + e);
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        locationText.setText("Please enable the GPS");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        locationText.setText("Coordinates");

    }


    private static final String BASE_URL = "http://192.168.1.15:9000";
    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    ApolloClient apolloClient = ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .build();

    Handler uiHandler = new Handler(Looper.getMainLooper());

    GetRoutesQuery gpsQuery =  GetRoutesQuery.builder()
            .build();

    ApolloCall<GetRoutesQuery.Data> gpsGetRoutesCall = apolloClient.query(gpsQuery);



}