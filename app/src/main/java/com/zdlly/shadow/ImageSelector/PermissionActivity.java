package com.zdlly.shadow.ImageSelector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.zdlly.shadow.R;

public class PermissionActivity extends Activity {
    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_DENIEG = 1;
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final String EXTRA_PERMISSION = "com.lai.permissiondemo";
    private static final String PACKAGE_URL_SCHEME = "package:";
    private CheckPermission checkPermission;
    private boolean isrequestCheck;


    public static void startActivityForResult(Activity activity, int requestCode, String... permission) {
        Intent intent = new Intent(activity, PermissionActivity.class);
        intent.putExtra(EXTRA_PERMISSION, permission);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_layout);
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSION))
        {
            throw new RuntimeException("当前Activity需要使用静态的StartActivityForResult方法启动");
        }
        checkPermission = new CheckPermission(this);
        isrequestCheck = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isrequestCheck) {
            String[] permission = getPermissions();
            if (checkPermission.permissionSet(permission)) {
                requestPermissions(permission);
            } else {
                allPermissionGranted();
            }
        } else {
            isrequestCheck = true;
        }
    }

    private void allPermissionGranted() {
        setResult(PERMISSION_GRANTED);
        finish();
    }

    private void requestPermissions(String... permission) {
        ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
    }

    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PERMISSION_REQUEST_CODE == requestCode && hasAllPermissionGranted(grantResults))
        {
            isrequestCheck = true;
            allPermissionGranted();
        } else {
            isrequestCheck = false;
            showMissingPermissionDialog();
        }

    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
        builder.setTitle(R.string.help);//提示帮助
        builder.setMessage(R.string.string_help_text);


        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(PERMISSION_DENIEG);//权限不足
                finish();
            }
        });
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }


}
