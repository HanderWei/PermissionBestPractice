package me.chen_wei.permissiondemo.permission;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Arrays;

/**
 * author: Chen Wei
 * time: 16/11/8
 * desc: 描述
 */

public class RationaleDialogClickListener implements Dialog.OnClickListener {

    private Object mHost;
    private RationaleDialogConfig mConfig;
    private PermissionUtils.PermissionCallbacks mCallbacks;

    RationaleDialogClickListener(RationaleDialogFragmentCompat compatDialogFragment, RationaleDialogConfig config, PermissionUtils.PermissionCallbacks callbacks) {
        mHost = compatDialogFragment.getParentFragment() != null
                ? compatDialogFragment.getParentFragment()
                : compatDialogFragment.getActivity();
        mConfig = config;
        mCallbacks = callbacks;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    RationaleDialogClickListener(RationaleDialogFragment dialogFragment,
                                 RationaleDialogConfig config,
                                 PermissionUtils.PermissionCallbacks callbacks) {
        mHost = dialogFragment.getActivity();
        mConfig= config;
        mCallbacks = callbacks;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            PermissionUtils.executePermissionsRequest(mHost, mConfig.permissions, mConfig.requestCode);
        } else {
            notifyPermissionDenied();
        }
    }

    private void notifyPermissionDenied() {
        if (mCallbacks != null) {
            mCallbacks.onPermissionDenied(mConfig.requestCode, Arrays.asList(mConfig.permissions));
        }
    }
}
