package com.github.vipulasri.timelineview.sample;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class Localtion {
    private AppCompatActivity appCompatActivity;
    private LocationManager locationManager;

    public Localtion(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        this.locationManager = (LocationManager) appCompatActivity.getSystemService(Context.LOCATION_SERVICE);
    }


    /**
     * 请求位置信息权限
     */
    public void requestPermissions(RequestBack requestBack) {
        if (ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(appCompatActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            requestBack.call(location.getLongitude(), location.getLatitude());
        }


    }

    public interface RequestBack {
        void call(double longitude, double latitude);
    }
}
