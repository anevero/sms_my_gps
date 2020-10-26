package com.github.anevero.sms_my_gps.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.github.anevero.sms_my_gps.data.ListItem;
import com.github.anevero.sms_my_gps.data.Preferences;

import java.util.ArrayList;

public class SMSReceiver extends BroadcastReceiver {
  private static final String TAG = "SMSReceiver";

  public void onReceive(Context context, Intent intent) {
    if (!Preferences.isServiceEnabled(context)) {
      return;
    }

    final String action = intent.getAction();
    final Bundle extras = intent.getExtras();
    final ArrayList<ListItem> listItems = Preferences.getListItems(context);

    if (action == null || extras == null || listItems.isEmpty()) {
      return;
    }

    for (SmsMessage message : getSmsMessages(extras)) {
      if (message == null || message.getOriginatingAddress() == null) {
        continue;
      }

      String sender = message.getOriginatingAddress().trim();
      String body = message.getMessageBody().trim();
      ListItem item = ListItem.getMatch(listItems, sender, body);

      if (item == null) {
        continue;
      }

      Log.i(TAG, "SMS received.\n" +
                 "from: " + sender + "\n" +
                 "message: " + body + "\n");

      GPSSender.notify(context, sender);
      return;
    }
  }

  private static SmsMessage[] getSmsMessages(Bundle extras) {
    final Object[] pdus = (Object[]) extras.get("pdus");
    if (pdus == null) {
      return new SmsMessage[0];
    }

    final String format = extras.getString("format", "3gpp");
    final SmsMessage[] messages = new SmsMessage[pdus.length];

    for (int i = 0; i < pdus.length; ++i) {
      messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
    }

    return messages;
  }
}
