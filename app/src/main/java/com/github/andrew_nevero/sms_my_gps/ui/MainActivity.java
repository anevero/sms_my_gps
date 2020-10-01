package com.github.andrew_nevero.sms_my_gps.ui;

import com.github.andrew_nevero.sms_my_gps.R;
import com.github.andrew_nevero.sms_my_gps.data.ListItem;
import com.github.andrew_nevero.sms_my_gps.data.Preferences;
import com.github.andrew_nevero.sms_my_gps.events.SMSReceiver;
import com.github.andrew_nevero.sms_my_gps.permissions.RuntimePermissions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.content.IntentFilter;
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
  private static final String ITEM_ID_KEY = "item_id";
  private static final String SENDER_KEY = "sender";
  private static final String MESSAGE_KEY = "message";
  private static final String LAST_KNOWN_LOCATION_KEY = "last_known_location";

  private static final int EDIT_ITEM_REQUEST_CODE = 521;
  private static final int EDIT_ITEM_RESULT_ADD_CODE = 522;
  private static final int EDIT_ITEM_RESULT_REMOVE_CODE = 523;

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
    if (RuntimePermissions.isEnabled(MainActivity.this)) {
      enableServiceSwitch.setChecked(
              Preferences.isEnabled(MainActivity.this));
      enableServiceSwitch.setEnabled(true);
      enableServiceSwitch.setClickable(true);
      enableServiceSwitch.setOnClickListener(v -> Preferences
              .setEnabled(MainActivity.this,
                          enableServiceSwitch.isChecked()));
    } else {
      enableServiceSwitch.setChecked(false);
      enableServiceSwitch.setEnabled(false);
      enableServiceSwitch.setClickable(false);
      if (Preferences.isEnabled(MainActivity.this)) {
        Preferences.setEnabled(MainActivity.this, false);
      }
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

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    RuntimePermissions.onRequestPermissionsResult(
            MainActivity.this, requestCode, permissions, grantResults);
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
    if (requestCode == EDIT_ITEM_REQUEST_CODE) {
      processEditActivityResult(resultCode, data);
    }
  }

  private void startEditActivity(int itemId) {
    final boolean needToAdd = (itemId < 0);

    Intent intent = new Intent(this, EditItemActivity.class);
    intent.putExtra(ITEM_ID_KEY, itemId);
    if (needToAdd) {
      intent.putExtra(SENDER_KEY, "");
      intent.putExtra(MESSAGE_KEY, "");
      intent.putExtra(LAST_KNOWN_LOCATION_KEY, false);
    } else {
      ListItem item = listItems.get(itemId);
      intent.putExtra(SENDER_KEY, item.getSender());
      intent.putExtra(MESSAGE_KEY, item.getMessagePrefix());
      intent.putExtra(LAST_KNOWN_LOCATION_KEY, item.getSendLastKnownLocation());
    }

    startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);
  }

  private void processEditActivityResult(int resultCode,
                                         @Nullable Intent data) {
    if (data == null) {
      return;
    }

    if (resultCode != EDIT_ITEM_RESULT_ADD_CODE &&
        resultCode != EDIT_ITEM_RESULT_REMOVE_CODE) {
      return;
    }

    int itemId = data.getIntExtra(ITEM_ID_KEY, -1);

    if (resultCode == EDIT_ITEM_RESULT_REMOVE_CODE) {
      if (itemId == -1) {
        return;
      }
      listItems.remove(itemId);
    } else {
      String sender = data.getStringExtra(SENDER_KEY);
      String message = data.getStringExtra(MESSAGE_KEY);
      boolean lastKnownLocation = data.getBooleanExtra(
              LAST_KNOWN_LOCATION_KEY, false);

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
