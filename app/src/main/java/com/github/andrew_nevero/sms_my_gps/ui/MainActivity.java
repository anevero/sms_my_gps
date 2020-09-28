package com.github.andrew_nevero.sms_my_gps.ui;

import com.github.andrew_nevero.sms_my_gps.R;
import com.github.andrew_nevero.sms_my_gps.data.ListItem;
import com.github.andrew_nevero.sms_my_gps.data.Preferences;
import com.github.andrew_nevero.sms_my_gps.events.SMSReceiver;
import com.github.andrew_nevero.sms_my_gps.permissions.RuntimePermissions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.app.Dialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "PreferenceActivity";

  private SwitchCompat enableServiceSwitch;

  private ListView listView;
  private ArrayList<ListItem> listItems;
  private ArrayAdapter<ListItem> listAdapter;

  private FloatingActionButton plusButton;

  private SMSReceiver smsReceiver;

  private void handleDataSetChange() {
    listAdapter.notifyDataSetChanged();
    Preferences.setListItems(MainActivity.this, listItems);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    Log.i(TAG, "Registering broadcast receiver");
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
      showEditDialog(position);
    });

    plusButton = findViewById(R.id.plus_button);
    plusButton.setOnClickListener(v -> showEditDialog(-1));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (smsReceiver != null) {
      Log.i(TAG, "Unregistering broadcast receiver");
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
    getMenuInflater().inflate(R.menu.main_activity, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    if (menuItem.getItemId() == R.id.info_menu) {
      showInfoDialog();
      return true;
    }
    return super.onOptionsItemSelected(menuItem);
  }

  private void showEditDialog(final int position) {
    final Dialog dialog = new Dialog(MainActivity.this, R.style.AppTheme);
    dialog.setContentView(R.layout.edit_item_dialog);

    final EditText senderInput = dialog.findViewById(R.id.sender_input);
    final EditText messageInput = dialog.findViewById(R.id.message_input);
    final CheckBox lastKnownLocationCheckbox =
            dialog.findViewById(R.id.last_known_location_checkbox);

    final Button deleteButton = dialog.findViewById(R.id.delete_button);
    final Button saveButton = dialog.findViewById(R.id.save_button);

    final boolean needToAdd = (position < 0);
    final ListItem listItem = (needToAdd) ? new ListItem("", "", false)
                                          : listItems.get(position);

    senderInput.setText(listItem.getSender());
    messageInput.setText(listItem.getMessagePrefix());
    lastKnownLocationCheckbox.setChecked(listItem.getSendLastKnownLocation());

    if (needToAdd) {
      deleteButton.setText(R.string.cancel_button);
    }

    deleteButton.setOnClickListener(v -> {
      if (!needToAdd) {
        listItems.remove(position);
        handleDataSetChange();
      }
      dialog.dismiss();
    });

    saveButton.setOnClickListener(v -> {
      final String newSender = senderInput.getText().toString().trim();
      final String newMessagePrefix =
              messageInput.getText().toString().trim();
      final boolean newLastKnownLocation =
              lastKnownLocationCheckbox.isChecked();

      if (newSender.isEmpty() || newMessagePrefix.isEmpty()) {
        Toast.makeText(MainActivity.this, getResources()
                               .getString(R.string.error_missing_required_value),
                       Toast.LENGTH_SHORT).show();
        return;
      }

      final boolean sameSender = newSender.equals(listItem.getSender());
      final boolean sameMessagePrefix =
              newMessagePrefix.equals(listItem.getMessagePrefix());
      final boolean sameLastKnownLocation =
              (newLastKnownLocation == listItem.getSendLastKnownLocation());

      if (sameSender && sameMessagePrefix && sameLastKnownLocation) {
        dialog.dismiss();
        return;
      }

      listItem.setSender(newSender);
      listItem.setMessagePrefix(newMessagePrefix);
      listItem.setSendLastKnownLocation(newLastKnownLocation);

      if (needToAdd && !listItems.add(listItem)) {
        Toast.makeText(MainActivity.this,
                       getResources().getString(R.string.error_add_list_item),
                       Toast.LENGTH_SHORT).show();
        return;
      }

      handleDataSetChange();
      dialog.dismiss();
    });

    dialog.show();
  }

  private void showInfoDialog() {
    final Dialog dialog = new Dialog(MainActivity.this, R.style.AppTheme);
    dialog.setContentView(R.layout.info_dialog);

    final TextView sourceCodeLabel = dialog.findViewById(R.id.source_code_label);
    sourceCodeLabel.setMovementMethod(LinkMovementMethod.getInstance());

    final TextView basedOnLabel = dialog.findViewById(R.id.based_on_label);
    basedOnLabel.setMovementMethod(LinkMovementMethod.getInstance());

    final TextView privacyPolicyLabel =
            dialog.findViewById(R.id.privacy_policy_faq_label);
    privacyPolicyLabel.setMovementMethod(LinkMovementMethod.getInstance());

    final Button backButton = dialog.findViewById(R.id.back_button);
    backButton.setOnClickListener(v -> dialog.dismiss());

    dialog.show();
  }
}
