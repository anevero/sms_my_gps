package com.github.anevero.sms_my_gps.events;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.anevero.sms_my_gps.data.Preferences;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public final class GPSSender {
  private static final String TAG = "GPSSender";

  @SuppressLint("MissingPermission")
  public static void notify(Context context, String recipient) {
    FusedLocationProviderClient fusedLocationProvider = LocationServices
            .getFusedLocationProviderClient(context);
    LocationManager systemLocationProvider = (LocationManager) context
            .getSystemService(Context.LOCATION_SERVICE);

    if (Preferences.isFusedLocationEnabled(context)) {
      SMSLocationListener listener =
              new SMSLocationListener(recipient, false, false);
      fusedLocationProvider
              .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
              .addOnCompleteListener(listener);
    }

    if (Preferences.isFusedLastKnownLocationEnabled(context)) {
      SMSLocationListener listener =
              new SMSLocationListener(recipient, true, false);
      fusedLocationProvider.getLastLocation().addOnCompleteListener(listener);
    }

    if (Preferences.isSystemGpsEnabled(context)) {
      SMSLocationListener listener =
              new SMSLocationListener(recipient, false, true);
      systemLocationProvider.requestSingleUpdate(
              LocationManager.GPS_PROVIDER, listener, null);
    }

    if (Preferences.isSystemLastKnownLocationEnabled(context)) {
      SMSLocationListener listener =
              new SMSLocationListener(recipient, true, true);
      Location location = systemLocationProvider.getLastKnownLocation(
              LocationManager.GPS_PROVIDER);
      if (location != null) {
        listener.onLocationChanged(location);
      }
    }
  }

  private static final class SMSLocationListener
          implements OnCompleteListener<Location>, LocationListener {
    final private String recipient;
    final private boolean lastKnownLocation;
    final private boolean systemGpsProvider;

    public SMSLocationListener(String recipient, boolean lastKnownLocation,
                               boolean systemGpsProvider) {
      this.recipient = recipient;
      this.lastKnownLocation = lastKnownLocation;
      this.systemGpsProvider = systemGpsProvider;
    }

    @Override
    public void onComplete(@NonNull Task<Location> task) {
      if (lastKnownLocation) {
        Log.i(TAG, "Received last known location from fused provider");
      } else {
        Log.i(TAG, "Received current location from fused provider");
      }

      Location location = task.getResult();
      if (location == null) {
        return;
      }

      sendMessage(location);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
      Log.i(TAG, "Received current location from system provider");
      sendMessage(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private void sendMessage(@NonNull Location location) {
      SmsManager smsManager = SmsManager.getDefault();
      ArrayList<String> messages = smsManager.divideMessage(
              generateBasicMessage(location));
      smsManager.sendMultipartTextMessage(
              recipient, null, messages, null, null);

      Log.i(TAG, "Sent message with location");
    }

    private String generateBasicMessage(Location location) {
      double lat = location.getLatitude();
      double lon = location.getLongitude();
      float accuracy = location.getAccuracy();
      float speed = location.getSpeed();
      String locationType = ((lastKnownLocation) ? "Last known" : "Current") +
                            ((systemGpsProvider) ? ", GPS" : "");

      return String.format(
              "%1$s:\n" +
              "https://www.google.com/maps/search/?api=1&query=%2$s,%3$s\n" +
              "Accuracy: %4$s m\n" +
              "Speed: %5$s m/sec",
              locationType,
              lat, lon, accuracy, speed);
    }
  }
}
