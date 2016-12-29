package me.chen_wei.permissiondemo.permission;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * author: Chen Wei
 * time: 16/11/8
 * desc: 描述
 */

public class RationaleDialogFragment extends DialogFragment {

    private PermissionUtils.PermissionCallbacks mCallbacks;
    private RationaleDialogConfig mConfig;
    private RationaleDialogClickListener mClickListener;

    static RationaleDialogFragment newInstance(
            @StringRes int positiveButton, @StringRes int negativeButton,
            @NonNull String rationaleMsg, int requestCode, @NonNull String[] permissions) {
        RationaleDialogFragment dialogFragment = new RationaleDialogFragment();

        RationaleDialogConfig config = new RationaleDialogConfig(positiveButton, negativeButton, rationaleMsg, requestCode, permissions);
        dialogFragment.setArguments(config.toBundle());

        return dialogFragment;
    }

    @TargetApi(17)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        boolean isAtLeastJellyBeanMR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;

        if (isAtLeastJellyBeanMR1
                && getParentFragment() != null
                && getParentFragment() instanceof PermissionUtils.PermissionCallbacks) {
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

        return mConfig.createDialog(getActivity(), mClickListener);
    }
}
