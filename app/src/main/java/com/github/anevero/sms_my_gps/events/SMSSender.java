package com.github.anevero.sms_my_gps.events;

import android.location.Location;
import android.location.LocationManager;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Locale;

public class SMSSender {
  private final String recipient;
  private final Location location;
  private final boolean lastKnownLocation;
  private final String systemProvider;

  public SMSSender(String recipient, @NonNull Location location,
                   boolean lastKnownLocation, String systemProvider) {
    this.recipient = recipient;
    this.lastKnownLocation = lastKnownLocation;
    this.location = location;
    this.systemProvider = systemProvider;
  }

  public void sendMessage() {
    SmsManager smsManager = SmsManager.getDefault();
    ArrayList<String> messages =
            smsManager.divideMessage(generateBasicMessage());
    smsManager.sendMultipartTextMessage(
            recipient, null, messages, null, null);
  }

  private String generateBasicMessage() {
    double lat = location.getLatitude();
    double lon = location.getLongitude();
    float accuracy = location.getAccuracy();
    float speed = location.getSpeed();

    String providerName;
    if (systemProvider == null) {
      providerName = "";
    } else if (systemProvider.equals(LocationManager.GPS_PROVIDER)) {
      providerName = ", GPS";
    } else if (systemProvider.equals(LocationManager.NETWORK_PROVIDER)) {
      providerName = ", network";
    } else {
      //  Impossible.
      providerName = ", unknown";
    }

    String locationType = ((lastKnownLocation) ? "Last known" : "Current") +
                          providerName;

    return String.format(
            "%1$s:\n" +
            "https://www.google.com/maps/search/?api=1&query=%2$s,%3$s\n" +
            "Accuracy: %4$s m\n" +
            "Speed: %5$s km/h",
            locationType,
            String.format(Locale.US, "%.6f", lat),
            String.format(Locale.US, "%.6f", lon),
            Math.round(accuracy),
            Math.round(3.6 * speed));
  }
}
