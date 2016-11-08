package me.chen_wei.permissiondemo;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.List;

import me.chen_wei.permissiondemo.permission.PermissionUtils;

/**
 * author: Chen Wei
 * time: 16/11/8
 * desc: Fragment基类，覆写onRequestPermissionsResult方法，实现PermissionCallbacks接口
 */

public class BaseFragment extends Fragment implements PermissionUtils.PermissionCallbacks{

    private static final String TAG = BaseFragment.class.getCanonicalName();

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

        //此处不处理"不在询问"的状态，如果处理了会导致弹出两个Dialog
        //统一在BaseActivity中做处理
    }
}
