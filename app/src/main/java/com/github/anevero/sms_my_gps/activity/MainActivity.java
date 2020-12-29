package com.github.anevero.sms_my_gps.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.github.anevero.sms_my_gps.R;
import com.github.anevero.sms_my_gps.data.Constants;
import com.github.anevero.sms_my_gps.data.ListItem;
import com.github.anevero.sms_my_gps.data.Preferences;
import com.github.anevero.sms_my_gps.events.ForegroundService;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  private SwitchCompat enableServiceSwitch;

  private ListView listView;
  private ArrayList<ListItem> listItems;
  private ArrayAdapter<ListItem> listAdapter;

  private FloatingActionButton plusButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Preferences.initPreferences(this);
    AppCompatDelegate.setDefaultNightMode(Preferences.getTheme(this));

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    enableServiceSwitch = findViewById(R.id.enable_service_switch);
    updateServiceSwitchStatus();
    enableServiceSwitch.setOnClickListener(v -> {
      boolean checked = enableServiceSwitch.isChecked();
      Preferences.setServiceEnabled(MainActivity.this, checked);
      Intent serviceIntent = new Intent(this, ForegroundService.class);
      if (checked) {
        ContextCompat.startForegroundService(this, serviceIntent);
      } else {
        stopService(serviceIntent);
      }
    });

    listView = findViewById(R.id.list_view);
    listItems = Preferences.getListItems(MainActivity.this);
    listAdapter = new ArrayAdapter<>(MainActivity.this,
                                     android.R.layout.simple_list_item_1,
                                     listItems);
    listView.setAdapter(listAdapter);
    listView.setOnItemClickListener((parent, view, position, id) -> {
      startEditActivity(position);
    });

    plusButton = findViewById(R.id.plus_button);
    plusButton.setOnClickListener(v -> startEditActivity(-1));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  private boolean areLocationAndSmsPermissionsGranted() {
    for (String permission : Constants.LOCATION_AND_SMS_PERMISSIONS) {
      if (checkSelfPermission(permission) !=
          PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }

    return true;
  }

  private void requestLocationAndSmsPermissions() {
    ArrayList<String> permissionsToRequest = new ArrayList<>();
    for (String permission : Constants.LOCATION_AND_SMS_PERMISSIONS) {
      if (checkSelfPermission(permission) !=
          PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add(permission);
      }
    }

    requestPermissions(permissionsToRequest.toArray(new String[0]),
                       Constants.LOCATION_SMS_PERMISSIONS_REQUEST_CODE);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == Constants.LOCATION_SMS_PERMISSIONS_REQUEST_CODE) {
      for (int result : grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          return;
        }
      }
      recreate();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    if (menuItem.getItemId() == R.id.info_menu) {
      startInfoActivity();
      return true;
    } else if (menuItem.getItemId() == R.id.settings_menu) {
      startSettingsActivity();
      return true;
    }
    return super.onOptionsItemSelected(menuItem);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Constants.EDIT_ITEM_REQUEST_CODE) {
      processEditActivityResult(resultCode, data);
    }
  }

  private void updateServiceSwitchStatus() {
    Intent serviceIntent = new Intent(this, ForegroundService.class);
    stopService(serviceIntent);
    enableServiceSwitch.setChecked(false);

    if (!areLocationAndSmsPermissionsGranted()) {
      enableServiceSwitch.setEnabled(false);
      enableServiceSwitch.setClickable(false);
      enableServiceSwitch.setChecked(false);
      Preferences.setServiceEnabled(MainActivity.this, false);
      requestLocationAndSmsPermissions();
    } else {
      boolean serviceEnabled = Preferences.isServiceEnabled(MainActivity.this);
      enableServiceSwitch.setChecked(serviceEnabled);
      if (serviceEnabled) {
        ContextCompat.startForegroundService(this, serviceIntent);
      }
    }
  }

  private void startEditActivity(int itemId) {
    final boolean needToAdd = (itemId < 0);

    Intent intent = new Intent(this, EditItemActivity.class);
    intent.putExtra(Constants.ITEM_ID_KEY, itemId);
    if (needToAdd) {
      intent.putExtra(Constants.SENDER_KEY, "");
      intent.putExtra(Constants.MESSAGE_KEY, "");
    } else {
      ListItem item = listItems.get(itemId);
      intent.putExtra(Constants.SENDER_KEY, item.getSender());
      intent.putExtra(Constants.MESSAGE_KEY, item.getMessagePrefix());
    }

    startActivityForResult(intent, Constants.EDIT_ITEM_REQUEST_CODE);
  }

  private void processEditActivityResult(int resultCode,
                                         @Nullable Intent data) {
    if (data == null) {
      return;
    }

    if (resultCode != Constants.EDIT_ITEM_ADD_RESULT_CODE &&
        resultCode != Constants.EDIT_ITEM_REMOVE_RESULT_CODE) {
      return;
    }

    int itemId = data.getIntExtra(Constants.ITEM_ID_KEY, -1);

    if (resultCode == Constants.EDIT_ITEM_REMOVE_RESULT_CODE) {
      if (itemId == -1) {
        return;
      }
      listItems.remove(itemId);
    } else {
      String sender = data.getStringExtra(Constants.SENDER_KEY);
      String message = data.getStringExtra(Constants.MESSAGE_KEY);

      if (itemId == -1) {
        if (ListItem.getMatch(listItems, sender) != null) {
          Snackbar.make(findViewById(android.R.id.content),
                        R.string.item_already_added_warning,
                        Snackbar.LENGTH_LONG).show();
          return;
        }
        listItems.add(new ListItem(sender, ""));
        itemId = listItems.size() - 1;
      }

      ListItem item = listItems.get(itemId);
      item.setMessagePrefix(message);
    }

    listAdapter.notifyDataSetChanged();
    Preferences.setListItems(MainActivity.this, listItems);
  }

  private void startInfoActivity() {
    startActivity(new Intent(this, InfoActivity.class));
  }

  private void startSettingsActivity() {
    startActivity(new Intent(this, SettingsActivity.class));
  }
}
