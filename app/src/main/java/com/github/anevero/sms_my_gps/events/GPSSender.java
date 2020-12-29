package com.github.anevero.sms_my_gps.events;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.anevero.sms_my_gps.data.Preferences;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public final class GPSSender {
  private static final String TAG = "GPSSender";

  @SuppressLint("MissingPermission")
  public static void notify(Context context, String recipient) {
    int minimumLocationAccuracy = Preferences.getLocationAccuracy(context);
    int maximumAttemptsNumber = Preferences.getAttemptsNumber(context);

    notifyFusedProvider(context, recipient, minimumLocationAccuracy,
                        maximumAttemptsNumber);
    notifySystemProvider(context, recipient, LocationManager.GPS_PROVIDER,
                         minimumLocationAccuracy, maximumAttemptsNumber);
    notifySystemProvider(context, recipient, LocationManager.NETWORK_PROVIDER,
                         minimumLocationAccuracy, maximumAttemptsNumber);
  }

  @SuppressLint("MissingPermission")
  private static void notifyFusedProvider(Context context, String recipient,
                                          int minimumLocationAccuracy,
                                          int maximumAttemptsNumber) {
    FusedLocationProviderClient fusedLocationProvider =
            (Preferences.areGooglePlayServicesAvailable(context)) ?
            LocationServices.getFusedLocationProviderClient(context) : null;

    if (fusedLocationProvider == null) {
      return;
    }

    if (Preferences.isFusedLocationEnabled(context)) {
      LocationRequest locationRequest = new LocationRequest()
              .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
              .setInterval(5000);
      LocationCallback locationCallback = new FusedLocationCallback(
              fusedLocationProvider, recipient,
              minimumLocationAccuracy, maximumAttemptsNumber);
      fusedLocationProvider.requestLocationUpdates(
              locationRequest, locationCallback, null);
    }

    if (Preferences.isFusedLastKnownEnabled(context)) {
      OnCompleteListener<Location> listener =
              new FusedOnCompleteListener(recipient);
      fusedLocationProvider.getLastLocation().addOnCompleteListener(listener);
    }
  }

  @SuppressLint("MissingPermission")
  private static void notifySystemProvider(Context context, String recipient,
                                           String provider,
                                           int minimumLocationAccuracy,
                                           int maximumAttemptsNumber) {
    LocationManager systemLocationProvider = (LocationManager) context
            .getSystemService(Context.LOCATION_SERVICE);

    if (!systemLocationProvider.isProviderEnabled(provider)) {
      Log.i(TAG, "Provider '" + provider + "' not enabled");
      return;
    }

    if (Preferences.isSystemProviderEnabled(context, provider)) {
      LocationListener listener = new SystemGpsLocationListener(
              systemLocationProvider, recipient,
              minimumLocationAccuracy, maximumAttemptsNumber);
      systemLocationProvider.requestLocationUpdates(
              provider, 5000, 0, listener);
    }

    if (Preferences.isSystemProviderLastKnownEnabled(context, provider)) {
      Location location = systemLocationProvider.getLastKnownLocation(
              provider);
      Log.i(TAG, "Received last known location from system provider");
      if (location != null) {
        Log.i(TAG, "Sending last known location from system provider");
        (new SMSSender(recipient, location, true, provider)).sendMessage();
      }
    }
  }

  // Location callback implementation for requesting updates from fused
  // location provider.
  private static class FusedLocationCallback extends LocationCallback {
    private final FusedLocationProviderClient provider;
    private final String recipient;
    private final int minimumLocationAccuracy;
    private final int maximumAttemptsNumber;
    private int currentAttemptsNumber;

    public FusedLocationCallback(FusedLocationProviderClient provider,
                                 String recipient,
                                 int minimumLocationAccuracy,
                                 int maximumAttemptsNumber) {
      this.provider = provider;
      this.recipient = recipient;
      this.minimumLocationAccuracy = minimumLocationAccuracy;
      this.maximumAttemptsNumber = maximumAttemptsNumber;
      this.currentAttemptsNumber = 0;
    }

    @Override
    public void onLocationAvailability(
            LocationAvailability locationAvailability) {
    }

    @Override
    public void onLocationResult(LocationResult result) {
      ++currentAttemptsNumber;
      Log.i(TAG, "Received current location from fused provider, attempt " +
                 currentAttemptsNumber);

      Location location = result.getLastLocation();
      if (location == null) {
        Log.i(TAG, "Skipping location from fused provider: location is null");
        return;
      }
      if (currentAttemptsNumber < maximumAttemptsNumber &&
          location.getAccuracy() > minimumLocationAccuracy) {
        Log.i(TAG, "Skipping location from fused provider: accuracy is " +
                   location.getAccuracy());
        return;
      }

      Log.i(TAG, "Sending current location from fused provider");
      provider.removeLocationUpdates(this);
      (new SMSSender(recipient, location, false, null)).sendMessage();
    }
  }

  // OnComplete listener for sending last known location received from fused
  // location provider.
  private static final class FusedOnCompleteListener
          implements OnCompleteListener<Location> {
    private final String recipient;

    public FusedOnCompleteListener(String recipient) {
      this.recipient = recipient;
    }

    @Override
    public void onComplete(@NonNull Task<Location> task) {
      Log.i(TAG, "Received last known location from fused provider");
      Location location = task.getResult();
      if (location == null) {
        return;
      }

      Log.i(TAG, "Sending last known location from fused provider");
      (new SMSSender(recipient, location, true, null)).sendMessage();
    }
  }

  // Location listener implementation for requesting updates from system
  // GPS location provider.
  private static class SystemGpsLocationListener implements LocationListener {
    private final LocationManager provider;
    private final String recipient;
    private final int minimumLocationAccuracy;
    private final int maximumAttemptsNumber;
    private int currentAttemptsNumber;

    public SystemGpsLocationListener(LocationManager provider,
                                     String recipient,
                                     int minimumLocationAccuracy,
                                     int maximumAttemptsNumber) {
      this.provider = provider;
      this.recipient = recipient;
      this.minimumLocationAccuracy = minimumLocationAccuracy;
      this.maximumAttemptsNumber = maximumAttemptsNumber;
      this.currentAttemptsNumber = 0;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
      ++currentAttemptsNumber;
      Log.i(TAG, "Received current location from system provider, attempt " +
                 currentAttemptsNumber);

      if (currentAttemptsNumber < maximumAttemptsNumber &&
          location.getAccuracy() > minimumLocationAccuracy) {
        Log.i(TAG, "Skipping location from system provider: accuracy is " +
                   location.getAccuracy());
        return;
      }

      Log.i(TAG, "Sending current location from system provider");
      provider.removeUpdates(this);
      (new SMSSender(recipient, location, false, location.getProvider()))
              .sendMessage();
    }
  }
}
