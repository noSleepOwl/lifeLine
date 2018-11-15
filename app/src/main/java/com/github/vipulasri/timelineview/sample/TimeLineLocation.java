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
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.github.vipulasri.timelineview.sample.base.BaseActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressLint("MissingPermission")
public class TimeLineLocation implements AMapLocationListener {
    protected String TAG = this.getClass().getName();
    private BaseActivity baseActivity;
    private RxPermissions rxPermissions;
    private LocationManager locationManager;
    private String baseProvider;
    private boolean power = false;
    private AMapLocation aMapLocation = null;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;

    public AMapLocation getaMapLocation() {
        return aMapLocation;
    }

    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                this.aMapLocation = amapLocation;
                Log.e("AmapError", aMapLocation.toString());
            } else {
                this.aMapLocation = null;
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }


    public boolean isPower() {
        return power;
    }


    public TimeLineLocation(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
        this.rxPermissions = new RxPermissions(baseActivity);
        this.init();
    }


    @SuppressLint("CheckResult")
    private void init() {
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .subscribe(granted -> {
                    power = granted;
                    if (granted) {
                        AMapLocationClient mlocationClient = new AMapLocationClient(baseActivity);
//初始化定位参数
                        mLocationOption = new AMapLocationClientOption();
//设置返回地址信息，默认为true
                        mLocationOption.setNeedAddress(true);
//设置定位监听
                        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
                        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
                        mLocationOption.setInterval(4000);
//设置定位参数
                        mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
                        mlocationClient.startLocation();
                    }
                });
    }

    protected void logI(String msg) {
        Log.i(this.getClass().getSimpleName(), msg);
    }


    public String getDir() {
        if (this.aMapLocation != null) {
            String s = aMapLocation.getProvince();//省信息
            String c = aMapLocation.getCity();//城市信息
            String d = aMapLocation.getDistrict();//城区信息
            String e = aMapLocation.getStreet();//街道信息
            String num = aMapLocation.getStreetNum();//街道门牌号信息
            return s + "" + c + "" + d + "" + e + "" + num;
        } else {
            return "no_dir";
        }
    }


}
