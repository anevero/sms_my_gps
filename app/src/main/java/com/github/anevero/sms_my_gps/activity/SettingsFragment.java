package com.github.anevero.sms_my_gps.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.github.anevero.sms_my_gps.R;
import com.github.anevero.sms_my_gps.data.Preferences;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {
  private SwitchPreferenceCompat fusedSwitch;
  private SwitchPreferenceCompat fusedLastKnownSwitch;
  private SwitchPreferenceCompat systemGpsSwitch;
  private SwitchPreferenceCompat systemGpsLastKnownSwitch;
  private SwitchPreferenceCompat systemNetworkSwitch;
  private SwitchPreferenceCompat systemNetworkLastKnownSwitch;

  private DropDownPreference locationAccuracyPreference;
  private DropDownPreference attemptsNumberPreference;

  private SwitchPreferenceCompat runOnStartupSwitch;
  private DropDownPreference appThemeDropdown;

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    AppCompatDelegate.setDefaultNightMode(Preferences.getTheme(getActivity()));
    setPreferencesFromResource(R.xml.fragment_settings, rootKey);

    fusedSwitch = getPreferenceScreen().findPreference(
            getString(R.string.fused_enabled));
    fusedLastKnownSwitch = getPreferenceScreen().findPreference(
            getString(R.string.fused_last_known_enabled));
    systemGpsSwitch = getPreferenceScreen().findPreference(
            getString(R.string.system_gps_enabled));
    systemGpsLastKnownSwitch = getPreferenceScreen().findPreference(
            getString(R.string.system_gps_last_known_enabled));
    systemNetworkSwitch = getPreferenceScreen().findPreference(
            getString(R.string.system_network_enabled));
    systemNetworkLastKnownSwitch = getPreferenceScreen().findPreference(
            getString(R.string.system_network_last_known_enabled));

    locationAccuracyPreference = getPreferenceScreen().findPreference(
            getString(R.string.location_accuracy));
    attemptsNumberPreference = getPreferenceScreen().findPreference(
            getString(R.string.attempts_number));

    runOnStartupSwitch = getPreferenceScreen().findPreference(
            getString(R.string.run_on_startup_enabled));
    appThemeDropdown = getPreferenceScreen().findPreference("app_theme");

    fusedSwitch.setSingleLineTitle(false);
    fusedLastKnownSwitch.setSingleLineTitle(false);
    systemGpsSwitch.setSingleLineTitle(false);
    systemGpsLastKnownSwitch.setSingleLineTitle(false);
    systemNetworkSwitch.setSingleLineTitle(false);
    systemNetworkLastKnownSwitch.setSingleLineTitle(false);

    locationAccuracyPreference.setSingleLineTitle(false);
    attemptsNumberPreference.setSingleLineTitle(false);

    runOnStartupSwitch.setSingleLineTitle(false);
    appThemeDropdown.setSingleLineTitle(false);

    if (!Preferences.areGooglePlayServicesAvailable(getActivity())) {
      fusedSwitch.setEnabled(false);
      fusedLastKnownSwitch.setEnabled(false);
    }
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                        String key) {
    if (key.equals(getString(R.string.app_theme))) {
      AppCompatDelegate
              .setDefaultNightMode(Preferences.getTheme(getActivity()));
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    getPreferenceManager().getSharedPreferences()
                          .registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onPause() {
    getPreferenceManager().getSharedPreferences()
                          .unregisterOnSharedPreferenceChangeListener(this);
    super.onPause();
  }
}
