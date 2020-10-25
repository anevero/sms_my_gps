package com.github.anevero.sms_my_gps.activity;

import android.os.Bundle;

import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.github.anevero.sms_my_gps.R;

public class SettingsFragment extends PreferenceFragmentCompat {
  private SwitchPreferenceCompat fusedSwitch;
  private SwitchPreferenceCompat fusedLastKnownSwitch;
  private SwitchPreferenceCompat systemGpsSwitch;
  private SwitchPreferenceCompat systemGpsLastKnownSwitch;

  private DropDownPreference locationAccuracyPreference;
  private DropDownPreference attemptsNumberPreference;

  private SwitchPreferenceCompat runOnStartupSwitch;


  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.fragment_settings, rootKey);

    fusedSwitch = getPreferenceScreen().findPreference(
            getString(R.string.fused_location_enabled));
    fusedLastKnownSwitch = getPreferenceScreen().findPreference(
            getString(R.string.fused_last_known_location_enabled));
    systemGpsSwitch = getPreferenceScreen().findPreference(
            getString(R.string.system_gps_enabled));
    systemGpsLastKnownSwitch = getPreferenceScreen().findPreference(
            getString(R.string.system_gps_last_known_location_enabled));

    locationAccuracyPreference = getPreferenceScreen().findPreference(
            getString(R.string.location_accuracy));
    attemptsNumberPreference = getPreferenceScreen().findPreference(
            getString(R.string.attempts_number));

    runOnStartupSwitch = getPreferenceScreen().findPreference(
            getString(R.string.run_on_startup_enabled));

    fusedSwitch.setSingleLineTitle(false);
    fusedLastKnownSwitch.setSingleLineTitle(false);
    systemGpsSwitch.setSingleLineTitle(false);
    systemGpsLastKnownSwitch.setSingleLineTitle(false);

    locationAccuracyPreference.setSingleLineTitle(false);
    attemptsNumberPreference.setSingleLineTitle(false);

    runOnStartupSwitch.setSingleLineTitle(false);
  }
}
