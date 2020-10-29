package com.github.anevero.sms_my_gps.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.github.anevero.sms_my_gps.data.Preferences;

public class BootReceiver extends BroadcastReceiver {
  private final static String TAG = "BootReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "BootReceiver notified");
    if (intent.getAction() == null ||
        !intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
      return;
    }

    if (Preferences.isServiceEnabled(context) &&
        Preferences.isRunOnStartupEnabled(context)) {
      Log.i(TAG, "Starting foreground service");
      Intent serviceIntent = new Intent(context, ForegroundService.class);
      ContextCompat.startForegroundService(context, serviceIntent);
    }
  }
}
