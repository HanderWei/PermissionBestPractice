package me.chen_wei.permissiondemo;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.List;

import me.chen_wei.permissiondemo.permission.PermissionUtils;

/**
 * author: Chen Wei
 * time: 16/11/8
 * desc: 描述
 */

public class BaseFragment extends Fragment implements PermissionUtils.PermissionCallbacks{

    private static final String TAG = BaseFragment.class.getCanonicalName();

    private static final int PERMANENTLY_DENIED_REQUEST_CODE = 429;


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
