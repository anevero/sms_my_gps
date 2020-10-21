package com.github.anevero.sms_my_gps.events;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.github.anevero.sms_my_gps.R;
import com.github.anevero.sms_my_gps.activity.MainActivity;
import com.github.anevero.sms_my_gps.data.Constants;

public class ForegroundService extends Service {
  private static final String TAG = "ForegroundService";

  private SMSReceiver smsReceiver;

  @Override
  public void onCreate() {
    super.onCreate();
    smsReceiver = new SMSReceiver();
    registerReceiver(smsReceiver, new IntentFilter(
            "android.provider.Telephony.SMS_RECEIVED"));
    Log.i(TAG, "Registered broadcast receiver");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (smsReceiver != null) {
      unregisterReceiver(smsReceiver);
      Log.i(TAG, "Unregistered broadcast receiver");
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel serviceChannel = new NotificationChannel(
              Constants.NOTIFICATION_CHANNEL_ID,
              getResources().getString(R.string.service_channel),
              NotificationManager.IMPORTANCE_LOW);
      serviceChannel.setShowBadge(false);
      serviceChannel.setSound(null, null);
      serviceChannel.enableVibration(false);
      getSystemService(NotificationManager.class)
              .createNotificationChannel(serviceChannel);
    }

    PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, new Intent(this, MainActivity.class), 0);

    Notification notification = new NotificationCompat.Builder(
            this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getResources().getText(R.string.app_name))
            .setContentText(getResources().getText(R.string.service_running))
            .setSmallIcon(R.drawable.ic_location_192)
            .setContentIntent(pendingIntent)
            .build();

    startForeground(Constants.NOTIFICATION_ID, notification);

    return START_STICKY;
  }
}
