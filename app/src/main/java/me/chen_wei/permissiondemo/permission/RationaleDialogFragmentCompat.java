package me.chen_wei.permissiondemo.permission;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatDialogFragment;

/**
 * author: Chen Wei
 * time: 16/11/8
 * desc: 描述
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class RationaleDialogFragmentCompat extends AppCompatDialogFragment{

    private PermissionUtils.PermissionCallbacks mCallbacks;
    private RationaleDialogConfig mConfig;
    private RationaleDialogClickListener mClickListener;

    static RationaleDialogFragmentCompat newInstance(
            @StringRes int positiveButton, @StringRes int negativeButton,
            @NonNull String rationaleMsg, int requestCode, @NonNull String[] permissions) {

        RationaleDialogFragmentCompat dialogFragment = new RationaleDialogFragmentCompat();

        RationaleDialogConfig config = new RationaleDialogConfig(
                positiveButton, negativeButton, rationaleMsg, requestCode, permissions);
        dialogFragment.setArguments(config.toBundle());

        return dialogFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null && getParentFragment() instanceof PermissionUtils.PermissionCallbacks) {
            mCallbacks = (PermissionUtils.PermissionCallbacks) getParentFragment();
        } else if (context instanceof PermissionUtils.PermissionCallbacks) {
            mCallbacks = (PermissionUtils.PermissionCallbacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);

        mConfig = new RationaleDialogConfig(getArguments());
        mClickListener = new RationaleDialogClickListener(this, mConfig, mCallbacks);
        return mConfig.createDialog(getContext(), mClickListener);
    }
}
