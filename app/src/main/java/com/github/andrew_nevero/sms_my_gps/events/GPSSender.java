package com.github.andrew_nevero.sms_my_gps.events;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public final class GPSSender {
  private static final String TAG = "GPSSender";

  @SuppressLint("MissingPermission")
  public static void notify(Context context, String recipient,
                            boolean sendLastKnownLocation) {
    FusedLocationProviderClient locationClient = LocationServices
            .getFusedLocationProviderClient(context);
    if (sendLastKnownLocation) {
      locationClient.getLastLocation().addOnCompleteListener(
              new SMSLocationListener(recipient, true));
    }
    locationClient.getLastLocation().addOnCompleteListener(
            new SMSLocationListener(recipient, false));
  }

  private static final class SMSLocationListener
          implements OnCompleteListener<Location> {
    private String recipient;
    private boolean lastKnownLocation;

    public SMSLocationListener(String recipient, boolean lastKnownLocation) {
      this.recipient = recipient;
      this.lastKnownLocation = lastKnownLocation;
    }

    @Override
    public void onComplete(@NonNull Task<Location> task) {
      if (lastKnownLocation) {
        Log.i(TAG, "onComplete() called with last known location");
      } else {
        Log.i(TAG, "onComplete() called with current location");
      }

      if (task.getResult() == null) {
        Log.i(TAG, "Location is not available");
        return;
      } else {
        sendMessage(task.getResult());
      }

      if (lastKnownLocation) {
        Log.i(TAG, "Sent message with last known location");
      } else {
        Log.i(TAG, "Sent message with current location");
      }
    }

    private void sendMessage(Location location) {
      SmsManager smsManager = SmsManager.getDefault();
      ArrayList<String> messages = smsManager.divideMessage(
              generateBasicMessage(location));
      smsManager.sendMultipartTextMessage(
              recipient, null, messages, null, null);
    }

    private String generateBasicMessage(Location location) {
      double lat = location.getLatitude();
      double lon = location.getLongitude();
      float accuracy = location.getAccuracy();
      float speed = location.getSpeed();

      return String.format(
              "[%1$s]\n" +
              "Coordinates: https://maps.google.com/?q=%2$s,%3$s\n" +
              "Accuracy (m): %4$s\n" +
              "Speed (m/sec): %5$s",
              ((lastKnownLocation) ? "last known" : "current"),
              lat, lon, accuracy, speed);
    }
  }
}
