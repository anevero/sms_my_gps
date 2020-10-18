package com.github.anevero.sms_my_gps.data;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public final class ListItem {
  private String sender;
  private String messagePrefix;
  private boolean sendLastKnownLocation;

  public ListItem(String sender, String message_prefix,
                  boolean sendLastKnownLocation) {
    this.sender = sender;
    this.messagePrefix = message_prefix;
    this.sendLastKnownLocation = sendLastKnownLocation;
  }

  @NonNull
  @Override
  public String toString() {
    return getSender();
  }

  public String getSender() {
    return sender;
  }

  public String getMessagePrefix() {
    return messagePrefix;
  }

  public boolean getSendLastKnownLocation() {
    return sendLastKnownLocation;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public void setMessagePrefix(String messagePrefix) {
    this.messagePrefix = messagePrefix;
  }

  public void setSendLastKnownLocation(boolean sendLastKnownLocation) {
    this.sendLastKnownLocation = sendLastKnownLocation;
  }

  public static ArrayList<ListItem> fromJson(String json) {
    ArrayList<ListItem> result;
    Gson gson = new Gson();
    result = gson.fromJson(json, new TypeToken<ArrayList<ListItem>>() {
    }.getType());
    return result;
  }

  public static String toJson(ArrayList<ListItem> arrayList) {
    return (new Gson()).toJson(arrayList);
  }

  public static ListItem getMatch(ArrayList<ListItem> listItems,
                                  String sender, String message) {
    if (sender == null || message == null || sender.isEmpty() ||
        message.isEmpty()) {
      return null;
    }

    for (ListItem item : listItems) {
      if (item.sender == null || item.sender.isEmpty() ||
          item.messagePrefix == null || item.messagePrefix.isEmpty()) {
        continue;
      }

      if (sender.endsWith(item.sender) && message.equals(item.messagePrefix)) {
        return item;
      }
    }

    return null;
  }
}
