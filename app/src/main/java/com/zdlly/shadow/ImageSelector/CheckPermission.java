package com.zdlly.shadow.ImageSelector;


import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class CheckPermission {

    private final Context context;

    public CheckPermission(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean permissionSet(String... permissions) {
        for (String permission : permissions) {
            if (isLackPermission(permission)) {
                return true;
            }
        }
        return false;
    }
    private boolean isLackPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    }

}
