package com.github.andrew_nevero.sms_my_gps.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.github.andrew_nevero.sms_my_gps.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class EditItemActivity extends AppCompatActivity {
  private static final String ITEM_ID_KEY = "item_id";
  private static final String SENDER_KEY = "sender";
  private static final String MESSAGE_KEY = "message";
  private static final String LAST_KNOWN_LOCATION_KEY = "last_known_location";

  private static final int RESULT_ADD_CODE = 522;
  private static final int RESULT_REMOVE_CODE = 523;

  private EditText senderInput;
  private EditText messageInput;
  private TextInputLayout senderInputLayout;
  private TextInputLayout messageInputLayout;

  private Button pickContactButton;
  private CheckBox lastKnownLocationCheckbox;

  private Button deleteButton;
  private Button saveButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_item);
    Objects.requireNonNull(getSupportActionBar())
           .setDisplayHomeAsUpEnabled(true);

    senderInput = findViewById(R.id.sender_input);
    messageInput = findViewById(R.id.message_input);
    senderInputLayout = findViewById(R.id.sender_input_layout);
    messageInputLayout = findViewById(R.id.message_input_layout);
    pickContactButton = findViewById(R.id.pick_contact_button);
    lastKnownLocationCheckbox = findViewById(R.id.last_known_location_checkbox);

    deleteButton = findViewById(R.id.delete_button);
    saveButton = findViewById(R.id.save_button);

    int itemId = getIntent().getIntExtra(ITEM_ID_KEY, -1);
    if (itemId == -1) {
      // We're adding a new item, not editing existing.
      setTitle(R.string.add_item);
      deleteButton.setText(R.string.cancel_button);
    } else {
      // We're editing existing item and must fill the fields with current info.
      String sender = getIntent().getStringExtra(SENDER_KEY);
      String message = getIntent().getStringExtra(MESSAGE_KEY);
      boolean lastKnownLocation = getIntent().getBooleanExtra(
              LAST_KNOWN_LOCATION_KEY, false);

      senderInput.setText(sender);
      messageInput.setText(message);
      lastKnownLocationCheckbox.setChecked(lastKnownLocation);
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

    pickContactButton.setOnClickListener(v -> {
      Toast.makeText(EditItemActivity.this,
                     "Not implemented",
                     Toast.LENGTH_SHORT).show();
    });

    deleteButton.setOnClickListener(v -> {
      Intent result = new Intent(this, MainActivity.class);
      result.putExtra(ITEM_ID_KEY, getIntent().getIntExtra(ITEM_ID_KEY, -1));
      setResult(RESULT_REMOVE_CODE, result);
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
      result.putExtra(ITEM_ID_KEY, getIntent().getIntExtra(ITEM_ID_KEY, -1));
      result.putExtra(SENDER_KEY, senderInput.getText().toString());
      result.putExtra(MESSAGE_KEY, messageInput.getText().toString());
      result.putExtra(LAST_KNOWN_LOCATION_KEY,
                      lastKnownLocationCheckbox.isChecked());
      setResult(RESULT_ADD_CODE, result);
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
}
