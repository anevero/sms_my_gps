package com.github.anevero.sms_my_gps.data;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public final class ListItem {
  private final String sender;
  private String messagePrefix;

  public ListItem(String sender, String messagePrefix) {
    this.sender = sender;
    this.messagePrefix = messagePrefix;
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

  public void setMessagePrefix(String messagePrefix) {
    this.messagePrefix = messagePrefix;
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

  private static boolean senderMatches(ListItem item, String sender) {
    if (item.sender.charAt(0) == '0') {
      // Ignore the leading '0' to allow matching "sender", which is
      // supplied with the appropriate country code.
      return sender.endsWith(item.sender.substring(1));
    } else {
      return sender.endsWith(item.sender);
    }
  }

  public static ListItem getMatch(ArrayList<ListItem> listItems,
                                  String sender) {
    for (ListItem item : listItems) {
      if (senderMatches(item, sender)) {
        return item;
      }
    }

    return null;
  }

  public static ListItem getMatch(ArrayList<ListItem> listItems,
                                  String sender, String message) {
    for (ListItem item : listItems) {
      if (senderMatches(item, sender) &&
          message.startsWith(item.messagePrefix)) {
        return item;
      }
    }

    return null;
  }
}
