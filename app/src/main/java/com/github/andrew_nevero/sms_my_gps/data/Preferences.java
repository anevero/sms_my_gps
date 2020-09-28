package com.github.andrew_nevero.sms_my_gps.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public final class Preferences {
  private static final String PREFS_FILENAME = "PREFS";
  private static final String PREF_ENABLED = "ENABLED";
  private static final String PREF_LIST_ITEMS = "LIST_ITEMS";

  public static boolean isEnabled(Context context) {
    SharedPreferences sharedPreferences =
            context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
    return sharedPreferences.getBoolean(PREF_ENABLED, true);
  }

  public static void setEnabled(Context context, boolean enabled) {
    SharedPreferences sharedPreferences =
            context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
    prefsEditor.putBoolean(PREF_ENABLED, enabled);
    prefsEditor.apply();
  }

  public static ArrayList<ListItem> getListItems(Context context) {
    SharedPreferences sharedPreferences =
            context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
    String json = sharedPreferences.getString(PREF_LIST_ITEMS, null);
    return (json == null) ? new ArrayList<>() : ListItem.fromJson(json);
  }

  public static void setListItems(Context context,
                                  ArrayList<ListItem> listItems) {
    String json = ListItem.toJson(listItems);
    SharedPreferences sharedPreferences =
            context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
    prefsEditor.putString(PREF_LIST_ITEMS, json);
    prefsEditor.apply();
  }
}
