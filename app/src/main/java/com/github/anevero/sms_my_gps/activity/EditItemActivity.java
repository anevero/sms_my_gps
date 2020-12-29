package com.github.anevero.sms_my_gps.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.github.anevero.sms_my_gps.R;
import com.github.anevero.sms_my_gps.data.Constants;
import com.github.anevero.sms_my_gps.data.Preferences;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class EditItemActivity extends AppCompatActivity {
  private EditText senderInput;
  private EditText messageInput;
  private TextInputLayout senderInputLayout;
  private TextInputLayout messageInputLayout;

  private Button pickContactButton;
  private Button deleteButton;
  private Button saveButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    AppCompatDelegate.setDefaultNightMode(Preferences.getTheme(this));
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_item);
    Objects.requireNonNull(getSupportActionBar())
           .setDisplayHomeAsUpEnabled(true);

    senderInput = findViewById(R.id.sender_input);
    messageInput = findViewById(R.id.message_input);
    senderInputLayout = findViewById(R.id.sender_input_layout);
    messageInputLayout = findViewById(R.id.message_input_layout);
    pickContactButton = findViewById(R.id.pick_contact_button);

    deleteButton = findViewById(R.id.delete_button);
    saveButton = findViewById(R.id.save_button);

    if (!isContactsPermissionGranted()) {
      pickContactButton.setEnabled(false);
      requestContactsPermission();
    }
    pickContactButton.setOnClickListener(v -> startContactPickerActivity());

    int itemId = getIntent().getIntExtra(Constants.ITEM_ID_KEY, -1);
    if (itemId == -1) {
      // We're adding a new item, not editing existing.
      setTitle(R.string.add_item);
      deleteButton.setText(R.string.cancel_button);
    } else {
      // We're editing existing item and must fill the fields with current info.
      String sender = getIntent().getStringExtra(Constants.SENDER_KEY);
      String message = getIntent().getStringExtra(Constants.MESSAGE_KEY);

      senderInput.setText(sender);
      messageInput.setText(message);

      // Only changing the prefix is allowed.
      senderInput.setEnabled(false);
      pickContactButton.setEnabled(false);
    }

    senderInput.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        senderInputLayout.setError(null);
      }
    });

    messageInput.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        messageInputLayout.setError(null);
      }
    });

    deleteButton.setOnClickListener(v -> {
      Intent result = new Intent(this, MainActivity.class);
      result.putExtra(Constants.ITEM_ID_KEY, getIntent().getIntExtra(
              Constants.ITEM_ID_KEY, -1));
      setResult(Constants.EDIT_ITEM_REMOVE_RESULT_CODE, result);
      finish();
    });

    saveButton.setOnClickListener(v -> {
      if (senderInput.getText().toString().isEmpty() ||
          messageInput.getText().toString().isEmpty()) {
        if (senderInput.getText().toString().isEmpty()) {
          senderInputLayout.setError(getString(R.string.field_empty_label));
        }
        if (messageInput.getText().toString().isEmpty()) {
          messageInputLayout.setError(getString(R.string.field_empty_label));
        }
        return;
      }

      Intent result = new Intent(this, MainActivity.class);
      result.putExtra(Constants.ITEM_ID_KEY, getIntent().getIntExtra(
              Constants.ITEM_ID_KEY, -1));
      result.putExtra(Constants.SENDER_KEY,
                      senderInput.getText().toString().trim());
      result.putExtra(Constants.MESSAGE_KEY,
                      messageInput.getText().toString().trim());
      setResult(Constants.EDIT_ITEM_ADD_RESULT_CODE, result);
      finish();
    });
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private boolean isContactsPermissionGranted() {
    return checkSelfPermission(Constants.CONTACTS_PERMISSION[0]) ==
           PackageManager.PERMISSION_GRANTED;
  }

  private void requestContactsPermission() {
    if (!isContactsPermissionGranted()) {
      requestPermissions(Constants.CONTACTS_PERMISSION,
                         Constants.CONTACTS_PERMISSION_REQUEST_CODE);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == Constants.CONTACTS_PERMISSION_REQUEST_CODE) {
      if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        return;
      }
      recreate();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Constants.CONTACT_PICKER_REQUEST_CODE) {
      processContactPickerResult(resultCode, data);
    }
  }

  private void startContactPickerActivity() {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
    startActivityForResult(intent, Constants.CONTACT_PICKER_REQUEST_CODE);
  }

  private void processContactPickerResult(int resultCode,
                                          @Nullable Intent data) {
    if (resultCode != RESULT_OK || data == null) {
      return;
    }

    Uri contactUri = data.getData();
    if (contactUri == null) {
      return;
    }

    String[] projection = new String[]{
            ContactsContract.CommonDataKinds.Phone.NUMBER};
    Cursor cursor = getContentResolver().query(
            contactUri, projection, null, null, null);
    if (cursor == null) {
      return;
    }

    if (cursor.moveToFirst()) {
      int numberIndex = cursor.getColumnIndex(
              ContactsContract.CommonDataKinds.Phone.NUMBER);
      String number = cursor.getString(numberIndex);
      number = number.replaceAll("[^0-9]", "");
      senderInput.setText(number);
    }

    cursor.close();
  }
}
