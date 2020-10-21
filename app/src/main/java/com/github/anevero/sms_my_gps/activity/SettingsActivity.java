package com.github.anevero.sms_my_gps.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.os.Bundle;
import android.view.MenuItem;

import com.github.anevero.sms_my_gps.R;
import com.github.anevero.sms_my_gps.data.Preferences;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
  private AppCompatCheckBox fusedCheckbox;
  private AppCompatCheckBox fusedLastKnownCheckbox;
  private AppCompatCheckBox systemGpsCheckbox;
  private AppCompatCheckBox systemGpsLastKnownCheckbox;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    Objects.requireNonNull(getSupportActionBar())
           .setDisplayHomeAsUpEnabled(true);

    fusedCheckbox = findViewById(R.id.fused_checkbox);
    fusedLastKnownCheckbox = findViewById(R.id.fused_last_known_checkbox);
    systemGpsCheckbox = findViewById(R.id.system_gps_checkbox);
    systemGpsLastKnownCheckbox =
            findViewById(R.id.system_gps_last_known_checkbox);

    fusedCheckbox.setChecked(
            Preferences.isFusedLocationEnabled(this));
    fusedLastKnownCheckbox.setChecked(
            Preferences.isFusedLastKnownLocationEnabled(this));
    systemGpsCheckbox.setChecked(
            Preferences.isSystemGpsEnabled(this));
    systemGpsLastKnownCheckbox.setChecked(
            Preferences.isSystemLastKnownLocationEnabled(this));

    fusedCheckbox.setOnClickListener(
            v -> Preferences.setFusedLocationEnabled(
                    v.getContext(), fusedCheckbox.isChecked()));
    fusedLastKnownCheckbox.setOnClickListener(
            v -> Preferences.setFusedLastKnownLocationEnabled(
                    v.getContext(), fusedLastKnownCheckbox.isChecked()));
    systemGpsCheckbox.setOnClickListener(
            v -> Preferences.setSystemGpsEnabled(
                    v.getContext(), systemGpsCheckbox.isChecked()));
    systemGpsLastKnownCheckbox.setOnClickListener(
            v -> Preferences.setSystemLastKnownLocationEnabled(
                    v.getContext(), systemGpsLastKnownCheckbox.isChecked()));
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
