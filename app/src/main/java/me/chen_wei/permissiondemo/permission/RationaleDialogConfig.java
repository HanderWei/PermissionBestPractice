package me.chen_wei.permissiondemo.permission;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

/**
 * author: Chen Wei
 * time: 16/11/8
 * desc: 描述
 */

public class RationaleDialogConfig {

    private static final String KEY_POSITIVE_BUTTON = "positiveButton";
    private static final String KEY_NEGATIVE_BUTTOn = "negativeButton";
    private static final String KEY_RATIONALE_MESSAGE = "rationaleMsg";
    private static final String KEY_REQUEST_CODE = "requestCode";
    private static final String KEY_PERMISSIONS = "permissions";

    int positiveButton;
    int negativeButton;
    int requestCode;
    String rationaleMsg;
    String[] permissions;

    RationaleDialogConfig(@StringRes int positiveButton, @StringRes int negativeButton,
                          @NonNull String rationaleMsg, int requestCode,
                          @NonNull String[] permissions) {
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.rationaleMsg = rationaleMsg;
        this.requestCode = requestCode;
        this.permissions = permissions;
    }

    RationaleDialogConfig(Bundle bundle) {
        positiveButton = bundle.getInt(KEY_POSITIVE_BUTTON);
        negativeButton = bundle.getInt(KEY_NEGATIVE_BUTTOn);
        rationaleMsg = bundle.getString(KEY_RATIONALE_MESSAGE);
        requestCode = bundle.getInt(KEY_REQUEST_CODE);
        permissions = bundle.getStringArray(KEY_PERMISSIONS);
    }

    Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITIVE_BUTTON, positiveButton);
        bundle.putInt(KEY_NEGATIVE_BUTTOn, negativeButton);
        bundle.putString(KEY_RATIONALE_MESSAGE, rationaleMsg);
        bundle.putInt(KEY_REQUEST_CODE, requestCode);
        bundle.putStringArray(KEY_PERMISSIONS, permissions);

        return bundle;
    }

    AlertDialog createDialog(Context context, Dialog.OnClickListener listener) {
        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setPositiveButton(positiveButton, listener)
                .setNegativeButton(negativeButton, listener)
                .setMessage(rationaleMsg)
                .create();
    }

}
