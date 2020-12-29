package com.github.anevero.sms_my_gps.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.github.anevero.sms_my_gps.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.Objects;

public final class Preferences {
  private final static String DEFAULT_LOCATION_ACCURACY = "20";
  private final static String DEFAULT_ATTEMPTS_NUMBER = "20";

  public static boolean areGooglePlayServicesAvailable(Context context) {
    GoogleApiAvailability instance = GoogleApiAvailability.getInstance();
    return instance.isGooglePlayServicesAvailable(context) ==
           ConnectionResult.SUCCESS;
  }

  public static void initPreferences(Context context) {
    SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);
    if (preferences.contains(
            context.getString(R.string.preferences_initialized))) {
      return;
    }

    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(context.getString(R.string.preferences_initialized),
                      true);
    editor.putString(context.getString(R.string.location_accuracy),
                     DEFAULT_LOCATION_ACCURACY);
    editor.putString(context.getString(R.string.attempts_number),
                     DEFAULT_ATTEMPTS_NUMBER);
    editor.putBoolean(context.getString(R.string.run_on_startup_enabled),
                      true);
    editor.putString(context.getString(R.string.app_theme),
                     context.getString(R.string.app_theme_system));

    if (areGooglePlayServicesAvailable(context)) {
      editor.putBoolean(context.getString(R.string.fused_enabled), true);
    } else {
      editor.putBoolean(context.getString(R.string.system_gps_enabled), true);
    }

    editor.apply();
  }

  private static boolean isOptionEnabled(Context context, int option) {
    SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);
    return preferences.getBoolean(context.getString(option), false);
  }

  public static ArrayList<ListItem> getListItems(Context context) {
    SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);
    String json = preferences.getString(
            context.getString(R.string.list_items), null);
    try {
      return (json == null) ? new ArrayList<>() : ListItem.fromJson(json);
    } catch (RuntimeException exception) {
      return new ArrayList<>();
    }
  }

  public static void setListItems(Context context,
                                  ArrayList<ListItem> listItems) {
    String json = ListItem.toJson(listItems);
    SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(context.getString(R.string.list_items), json);
    editor.apply();
  }

  public static boolean isServiceEnabled(Context context) {
    return isOptionEnabled(context, R.string.service_enabled);
  }

  public static void setServiceEnabled(Context context, boolean enabled) {
    SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(context.getString(R.string.service_enabled), enabled);
    editor.apply();
  }

  public static boolean isFusedLocationEnabled(Context context) {
    return isOptionEnabled(context, R.string.fused_enabled);
  }

  public static boolean isFusedLastKnownEnabled(Context context) {
    return isOptionEnabled(context, R.string.fused_last_known_enabled);
  }

  public static boolean isSystemProviderEnabled(Context context,
                                                String provider) {
    int option = (provider.equals(LocationManager.GPS_PROVIDER)) ?
                 R.string.system_gps_enabled : R.string.system_network_enabled;
    return isOptionEnabled(context, option);
  }

  public static boolean isSystemProviderLastKnownEnabled(Context context,
                                                         String provider) {
    int option = (provider.equals(LocationManager.GPS_PROVIDER)) ?
                 R.string.system_gps_last_known_enabled :
                 R.string.system_network_last_known_enabled;
    return isOptionEnabled(context, option);
  }

  public static int getLocationAccuracy(Context context) {
    SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);
    return Integer.parseInt(Objects.requireNonNull(preferences.getString(
            context.getString(R.string.location_accuracy),
            DEFAULT_LOCATION_ACCURACY)));
  }

  public static int getAttemptsNumber(Context context) {
    SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);
    return Integer.parseInt(Objects.requireNonNull(preferences.getString(
            context.getString(R.string.attempts_number),
            DEFAULT_ATTEMPTS_NUMBER)));
  }

  public static boolean isRunOnStartupEnabled(Context context) {
    return isOptionEnabled(context, R.string.run_on_startup_enabled);
  }

  public static int getTheme(Context context) {
    SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);
    String mode = preferences.getString(
            context.getString(R.string.app_theme),
            context.getString(R.string.app_theme_system));
    assert mode != null;
    if (mode.equals(context.getString(R.string.app_theme_system))) {
      return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    } else if (mode.equals(context.getString(R.string.app_theme_light))) {
      return AppCompatDelegate.MODE_NIGHT_NO;
    } else if (mode.equals(context.getString(R.string.app_theme_dark))) {
      return AppCompatDelegate.MODE_NIGHT_YES;
    }
    return AppCompatDelegate.MODE_NIGHT_UNSPECIFIED;
  }
}
