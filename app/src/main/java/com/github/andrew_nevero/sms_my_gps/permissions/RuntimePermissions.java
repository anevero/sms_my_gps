package com.github.andrew_nevero.sms_my_gps.permissions;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;

public final class RuntimePermissions {
  private static final int REQUEST_CODE = 0;

  public static boolean isEnabled(Activity activity) {
    PackageInfo packageInfo;
    try {
      packageInfo = activity.getPackageManager().getPackageInfo(
              activity.getPackageName(), PackageManager.GET_PERMISSIONS);
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }

    String[] requestedPermissions = packageInfo.requestedPermissions;
    if (requestedPermissions == null) {
      return true;
    }

    ArrayList<String> permissionsToRequest = new ArrayList<>();
    for (String permission_name : requestedPermissions) {
      if (activity.checkSelfPermission(permission_name) !=
          PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add(permission_name);
      }
    }

    if (permissionsToRequest.isEmpty()) {
      return true;
    }

    activity.requestPermissions(permissionsToRequest.toArray(new String[0]),
                                REQUEST_CODE);

    return false;
  }

  public static void onRequestPermissionsResult(Activity activity,
                                                int requestCode,
                                                String[] permissions,
                                                int[] grantResults) {
    if (requestCode != REQUEST_CODE || grantResults.length == 0) {
      return;
    }

    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return;
      }
    }

    activity.recreate();
  }
}
