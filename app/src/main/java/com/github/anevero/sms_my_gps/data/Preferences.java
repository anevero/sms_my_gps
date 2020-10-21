package com.github.anevero.sms_my_gps.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.github.anevero.sms_my_gps.BuildConfig;

import java.util.ArrayList;

public final class Preferences {
  private static final String PREFS_FILENAME =
          BuildConfig.APPLICATION_ID + ".preferences";

  private enum Option {
    LIST_ITEMS("list_items"),
    SERVICE_ENABLED("service_enabled"),
    FUSED_LOCATION_ENABLED("fused_location_enabled"),
    FUSED_LAST_KNOWN_LOCATION_ENABLED("fused_last_known_location_enabled"),
    SYSTEM_GPS_ENABLED("system_gps_enabled"),
    SYSTEM_LAST_KNOWN_LOCATION_ENABLED("system_last_known_location_enabled");

    private final String id;

    Option(String id) {
      this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
      return id;
    }
  }

  private static boolean isOptionEnabled(Context context, boolean defaultValue,
                                         Option option) {
    SharedPreferences sharedPreferences =
            context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
    return sharedPreferences.getBoolean(option.toString(), defaultValue);
  }

  private static void setOptionEnabled(Context context, boolean enabled,
                                       Option option) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(
            PREFS_FILENAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
    prefsEditor.putBoolean(option.toString(), enabled);
    prefsEditor.apply();
  }

  public static ArrayList<ListItem> getListItems(Context context) {
    SharedPreferences sharedPreferences =
            context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
    String json = sharedPreferences.getString(
            Option.LIST_ITEMS.toString(), null);
    try {
      return (json == null) ? new ArrayList<>() : ListItem.fromJson(json);
    } catch (RuntimeException exception) {
      return new ArrayList<>();
    }
  }

  public static void setListItems(Context context,
                                  ArrayList<ListItem> listItems) {
    String json = ListItem.toJson(listItems);
    SharedPreferences sharedPreferences =
            context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
    prefsEditor.putString(Option.LIST_ITEMS.toString(), json);
    prefsEditor.apply();
  }

  public static boolean isServiceEnabled(Context context) {
    return isOptionEnabled(context, false, Option.SERVICE_ENABLED);
  }

  public static void setServiceEnabled(Context context, boolean enabled) {
    setOptionEnabled(context, enabled, Option.SERVICE_ENABLED);
  }

  public static boolean isFusedLocationEnabled(Context context) {
    return isOptionEnabled(context, true, Option.FUSED_LOCATION_ENABLED);
  }

  public static void setFusedLocationEnabled(Context context, boolean enabled) {
    setOptionEnabled(context, enabled, Option.FUSED_LOCATION_ENABLED);
  }

  public static boolean isFusedLastKnownLocationEnabled(Context context) {
    return isOptionEnabled(context, false,
                           Option.FUSED_LAST_KNOWN_LOCATION_ENABLED);
  }

  public static void setFusedLastKnownLocationEnabled(
          Context context, boolean enabled) {
    setOptionEnabled(context, enabled,
                     Option.FUSED_LAST_KNOWN_LOCATION_ENABLED);
  }

  public static boolean isSystemGpsEnabled(Context context) {
    return isOptionEnabled(context, false, Option.SYSTEM_GPS_ENABLED);
  }

  public static void setSystemGpsEnabled(Context context, boolean enabled) {
    setOptionEnabled(context, enabled, Option.SYSTEM_GPS_ENABLED);
  }

  public static boolean isSystemLastKnownLocationEnabled(Context context) {
    return isOptionEnabled(context, false,
                           Option.SYSTEM_LAST_KNOWN_LOCATION_ENABLED);
  }

  public static void setSystemLastKnownLocationEnabled(
          Context context, boolean enabled) {
    setOptionEnabled(context, enabled,
                     Option.SYSTEM_LAST_KNOWN_LOCATION_ENABLED);
  }
}
