package me.chen_wei.permissiondemo.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

/**
 * author: Chen Wei
 * time: 16/11/8
 * desc: 描述
 */

public class AppSettingsDialog {

    public static final int DEFAULT_SETTINGS_REQ_CODE = 3249;

    private AlertDialog mAlertDialog;

    private AppSettingsDialog(@NonNull final Object activityOrFragment,
                              @NonNull final Context context,
                              @NonNull String rationale,
                              @Nullable String title,
                              @Nullable String positiveButton,
                              @Nullable String negativeButton,
                              @Nullable DialogInterface.OnClickListener negativeListener,
                              int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(rationale);
        builder.setTitle(title);
        String positiveButtonText = TextUtils.isEmpty(positiveButton) ?
                context.getString(android.R.string.ok) : positiveButton;
        String negativeButtonText = TextUtils.isEmpty(negativeButton) ?
                context.getString(android.R.string.cancel) : negativeButton;

        final int settingsRequestCode = requestCode > 0 ? requestCode : DEFAULT_SETTINGS_REQ_CODE;

        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);

                startForResult(activityOrFragment, intent, settingsRequestCode);
            }
        });

        builder.setNegativeButton(negativeButtonText, negativeListener);

        mAlertDialog = builder.create();
    }

    private void startForResult(Object object, Intent intent, int requestCode) {
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).startActivityForResult(intent, requestCode);
        }
    }

    public void show() {
        mAlertDialog.show();
    }

    public static class Builder {
        private Object mActivityOrFragment;
        private Context mContext;
        private String mRationale;
        private String mTitle;
        private String mPositiveButton;
        private String mNegativeButton;
        private DialogInterface.OnClickListener mNegativeListener;
        private int mRequestCode = -1;

        public Builder(@NonNull Activity activity, @NonNull String rationale) {
            mActivityOrFragment = activity;
            mContext = activity;
            mRationale = rationale;
        }

        public Builder(@NonNull Fragment fragment, @NonNull String rationale) {
            mActivityOrFragment = fragment;
            mContext = fragment.getContext();
            mRationale = rationale;
        }

        public Builder(@NonNull android.app.Fragment fragment, @NonNull String rationale) {
            mActivityOrFragment = fragment;
            mContext = fragment.getActivity();
            mRationale = rationale;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setPositiveButton(String positiveButton) {
            mPositiveButton = positiveButton;
            return this;
        }

        public Builder setNegativeButton(String negativeButton,
                                         DialogInterface.OnClickListener negativeListener) {
            mNegativeButton = negativeButton;
            mNegativeListener = negativeListener;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        public AppSettingsDialog build() {
            return new AppSettingsDialog(mActivityOrFragment, mContext, mRationale, mTitle, mPositiveButton, mNegativeButton, mNegativeListener, mRequestCode);
        }
    }
}
