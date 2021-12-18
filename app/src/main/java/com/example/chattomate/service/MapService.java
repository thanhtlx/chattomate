package com.example.chattomate.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.chattomate.database.AppPreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MapService extends Service {
    private String messageID;
    private double lastLat = 0;
    private double lastLong = 0;
    private ServiceAPI serviceAPI;
    private LocationManager mLocationManager;
    private Long timeUpdate;

    public MapService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Log.d("DEBUG", "location change !!");
            Calendar cal = Calendar.getInstance();
            if (cal.getTimeInMillis() > timeUpdate) {
                stopUpdate();
                return;
            }
            if (Math.abs(location.getLatitude() - lastLat) > 0.05 || Math.abs(location.getLongitude() - lastLong) > 0.05) {
                serviceAPI.updateLocation(location.getLatitude(), location.getLongitude(), messageID);
                lastLat = location.getLatitude();
                lastLong = location.getLongitude();
                Log.d("DEBUG","update location");
            }
        }
    };

    private void stopUpdate() {
        mLocationManager.removeUpdates(mLocationListener);
        mLocationManager = null;
        onDestroy();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {

            Bundle extras = intent.getExtras();
            messageID = extras.getString("messageID");
            String time = extras.getString("time");
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(format.parse(time));
                cal.add(Calendar.MINUTE, 30);
                timeUpdate = cal.getTimeInMillis();
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("DEBUG","error parse");
            }
            serviceAPI = new ServiceAPI(getApplicationContext(), new AppPreferenceManager(getApplicationContext()));
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, (float) 1.0, mLocationListener);
        }

        return super.onStartCommand(intent, flags, startId);
    }
}