package me.chen_wei.permissiondemo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import me.chen_wei.permissiondemo.permission.AfterPermissionGranted;
import me.chen_wei.permissiondemo.permission.PermissionUtils;

/**
 * author: Chen Wei
 * time: 16/11/8
 * desc: 描述
 */

public class TestFragment extends BaseFragment {

    private static final int REQUEST_PHONE_STATE = 0x01;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_test, container);

        v.findViewById(R.id.btn_phone_state).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.hasPermisssions(getContext(), Manifest.permission.READ_PHONE_STATE)) {
                    onPermissionGranted();
                } else {
                    PermissionUtils.requestPermissions(TestFragment.this, "需要读取手机信息", REQUEST_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
                }
            }
        });

        return v;
    }

    @AfterPermissionGranted(REQUEST_PHONE_STATE)
    private void onPermissionGranted() {
        Toast.makeText(getContext(), "授权成功", Toast.LENGTH_SHORT).show();
    }

}
