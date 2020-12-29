package com.github.anevero.sms_my_gps.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.github.anevero.sms_my_gps.R;
import com.github.anevero.sms_my_gps.data.Preferences;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    AppCompatDelegate.setDefaultNightMode(Preferences.getTheme(this));
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_settings);
    Objects.requireNonNull(getSupportActionBar())
           .setDisplayHomeAsUpEnabled(true);

    getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.settings_container, new SettingsFragment())
            .commit();
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
