package com.github.vipulasri.timelineview.sample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.github.vipulasri.timelineview.sample.base.BaseActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressLint("MissingPermission")
public class TimeLineLocation implements LocationListener {
    protected String TAG = this.getClass().getName();
    private BaseActivity baseActivity;
    private RxPermissions rxPermissions;
    private LocationManager locationManager;
    private String baseProvider;
    private boolean power = false;

    public boolean isPower() {
        return power;
    }


    public TimeLineLocation(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
        this.rxPermissions = new RxPermissions(baseActivity);
        this.init();
    }

    public List<Address> getAddressList() {
        Geocoder gc = new Geocoder(baseActivity, Locale.getDefault());
        Location location = getLocation();
        if (location == null) {
            return null;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        List<Address> locationList = null;
        try {
            locationList = gc.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationList;
      /*      locationList.forEach(address -> {
                Log.i(TAG, "address =" + address);
                String countryName = address.getCountryName();//得到国家名称，比如：中国
                Log.i(TAG, "countryName = " + countryName);
                String locality = address.getLocality();//得到城市名称，比如：北京市
                Log.i(TAG, "locality = " + locality);
                for (int i = 0; address.getAddressLine(i) != null; i++) {
                    String addressLine = address.getAddressLine(i);//得到周边信息，包括街道等，i=0，得到街道名称
                    Log.i(TAG, "addressLine = " + addressLine);
                }
            });*/

    }


    public String formatAddress() {
        List<Address> locationList = getAddressList();
        if (locationList == null || locationList.isEmpty()) {
            return null;
        }

        return locationList.stream().map(address -> {
            List<String> dir = new ArrayList<>();

            Log.i(TAG, "address =" + address);
            String countryName = address.getCountryName();//得到国家名称，比如：中国
            Log.i(TAG, "countryName = " + countryName);
            String locality = address.getLocality();//得到城市名称，比如：北京市
            Log.i(TAG, "locality = " + locality);
            for (int i = 0; address.getAddressLine(i) != null; i++) {
                String addressLine = address.getAddressLine(i);//得到周边信息，包括街道等，i=0，得到街道名称
                Log.i(TAG, "addressLine = " + addressLine);
                String dress = String.format("%s-%s-%s", countryName, locality, addressLine);
                dir.add(dress);
            }
            return dir;
        }).flatMap(o -> o.stream()).reduce((a, b) -> a + "\n" + b).get();
    }

    public Location getLocation() {
        if (!power) {
            return null;
        }
        return locationManager.getLastKnownLocation(baseProvider);
    }


    @SuppressLint("CheckResult")
    private void init() {
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    power = granted;
                    if (granted) {
                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，如果设置为高精度，依然获取不了location。
                        criteria.setAltitudeRequired(false);//不要求海拔
                        criteria.setBearingRequired(false);//不要求方位
                        criteria.setCostAllowed(true);//允许有花费
                        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
                        locationManager = (LocationManager) baseActivity.getSystemService(Context.LOCATION_SERVICE);
                        baseProvider = locationManager.getBestProvider(criteria, true);
                        locationManager.requestLocationUpdates(baseProvider, 3000, 0, this);
                    }
                });
    }

    protected void logI(String msg) {
        Log.i(this.getClass().getSimpleName(), msg);
    }

    @Override
    public void onLocationChanged(Location location) {
        logI("onLocationChanged");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        logI("onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        logI("onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        logI("onProviderDisabled");
    }
}
