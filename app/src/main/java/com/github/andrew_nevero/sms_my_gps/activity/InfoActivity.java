package com.github.andrew_nevero.sms_my_gps.activity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.andrew_nevero.sms_my_gps.R;

import java.util.Objects;

public class InfoActivity extends AppCompatActivity {
  private TextView sourceCodeLabel;
  private TextView basedOnLabel;
  private TextView privacyPolicyLabel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_info);

    Objects.requireNonNull(getSupportActionBar())
           .setDisplayHomeAsUpEnabled(true);

    sourceCodeLabel = findViewById(R.id.source_code_label);
    sourceCodeLabel.setMovementMethod(LinkMovementMethod.getInstance());

    basedOnLabel = findViewById(R.id.based_on_label);
    basedOnLabel.setMovementMethod(LinkMovementMethod.getInstance());

    privacyPolicyLabel = findViewById(R.id.privacy_policy_faq_label);
    privacyPolicyLabel.setMovementMethod(LinkMovementMethod.getInstance());
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
