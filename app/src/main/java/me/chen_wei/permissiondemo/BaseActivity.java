package me.chen_wei.permissiondemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import me.chen_wei.permissiondemo.permission.PermissionUtils;


/**
 * author: Chen Wei
 * time: 16/11/8
 * desc: Activity基类，覆写onRequestPermissionsResult方法，实现PermissionCallbacks接口，并在onPermissionDenied()中处理用户勾选"不再询问"的情况
 */

public class BaseActivity extends AppCompatActivity implements PermissionUtils.PermissionCallbacks {

    private static final String TAG = BaseActivity.class.getCanonicalName();

    private static final int PERMANENTLY_DENIED_REQUEST_CODE = 428;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        Log.d(TAG, perms.size() + " permissions granted.");
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        Log.e(TAG, perms.size() + " permissions denied.");
        if (PermissionUtils.somePermissionsPermanentlyDenied(this, perms)) {
            PermissionUtils.onPermissionsPermanentlyDenied(this,
                    getString(R.string.rationale),
                    getString(R.string.rationale_title),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel),
                    PERMANENTLY_DENIED_REQUEST_CODE);
        }
    }
}
