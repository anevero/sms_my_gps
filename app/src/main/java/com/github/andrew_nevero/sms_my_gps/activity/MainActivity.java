package com.github.andrew_nevero.sms_my_gps.activity;

import com.github.andrew_nevero.sms_my_gps.R;
import com.github.andrew_nevero.sms_my_gps.data.Constants;
import com.github.andrew_nevero.sms_my_gps.data.ListItem;
import com.github.andrew_nevero.sms_my_gps.data.Preferences;
import com.github.andrew_nevero.sms_my_gps.events.SMSReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  private SwitchCompat enableServiceSwitch;

  private ListView listView;
  private ArrayList<ListItem> listItems;
  private ArrayAdapter<ListItem> listAdapter;

  private FloatingActionButton plusButton;

  private SMSReceiver smsReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    smsReceiver = new SMSReceiver();
    registerReceiver(smsReceiver, new IntentFilter(
            "android.provider.Telephony.SMS_RECEIVED"));

    enableServiceSwitch = findViewById(R.id.enable_service_switch);
    enableServiceSwitch.setOnClickListener(v -> Preferences
            .setEnabled(MainActivity.this, enableServiceSwitch.isChecked()));

    if (!areLocationAndSmsPermissionsGranted()) {
      enableServiceSwitch.setEnabled(false);
      enableServiceSwitch.setClickable(false);
      enableServiceSwitch.setChecked(false);
      Preferences.setEnabled(MainActivity.this, false);
      requestLocationAndSmsPermissions();
    } else {
      enableServiceSwitch.setChecked(Preferences.isEnabled(MainActivity.this));
    }

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
    if (smsReceiver != null) {
      unregisterReceiver(smsReceiver);
    }
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

  private void startEditActivity(int itemId) {
    final boolean needToAdd = (itemId < 0);

    Intent intent = new Intent(this, EditItemActivity.class);
    intent.putExtra(Constants.ITEM_ID_KEY, itemId);
    if (needToAdd) {
      intent.putExtra(Constants.SENDER_KEY, "");
      intent.putExtra(Constants.MESSAGE_KEY, "");
      intent.putExtra(Constants.LAST_KNOWN_LOCATION_KEY, false);
    } else {
      ListItem item = listItems.get(itemId);
      intent.putExtra(Constants.SENDER_KEY, item.getSender());
      intent.putExtra(Constants.MESSAGE_KEY, item.getMessagePrefix());
      intent.putExtra(Constants.LAST_KNOWN_LOCATION_KEY,
                      item.getSendLastKnownLocation());
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
      boolean lastKnownLocation = data.getBooleanExtra(
              Constants.LAST_KNOWN_LOCATION_KEY, false);

      if (itemId == -1) {
        listItems.add(new ListItem("", "", false));
        itemId = listItems.size() - 1;
      }

      ListItem item = listItems.get(itemId);
      item.setSender(sender);
      item.setMessagePrefix(message);
      item.setSendLastKnownLocation(lastKnownLocation);
    }

    listAdapter.notifyDataSetChanged();
    Preferences.setListItems(MainActivity.this, listItems);
  }

  private void startInfoActivity() {
    startActivity(new Intent(this, InfoActivity.class));
  }
}
