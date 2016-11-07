package me.chen_wei.permissiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends FragmentActivity {

    //申请Camera权限
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0x01;

    //请求相机
    private static final int REQUEST_IMAGE_CAPTURE = 0x02;

    //SharedPreference，记录上一次`shouldShowRequestPermissionRationale`返回的状态
    private static final String KEY_CAMERA_PERMISSION = "camera";

    //相机权限
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    RelativeLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = (RelativeLayout) findViewById(R.id.main_layout);
    }

    public void onClick(View view) {
        if (hasPermission(this, Manifest.permission.CAMERA)) {
            openCamera();
        } else {
            requestPermission(this, Manifest.permission.CAMERA);
        }
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * 检查权限
     *
     * @param context
     * @param permission
     * @return
     */
    private boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 申请权限
     *
     * @param activity
     * @param permission
     */
    private void requestPermission(Activity activity, final String permission) {
        boolean flag = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        if (getLastRequestState() && !flag) {
            //当用户勾选`不再询问`时，调用此部分
            Snackbar.make(mLayout, "需要相机权限", Snackbar.LENGTH_SHORT)
                    .setAction("去设置界面", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startAppSetting();
                        }
                    })
                    .show();
        } else if (flag) {
            //之前有过`拒绝`授权时，提醒用户需要相机权限
            SharedPrefsUtils.setBooleanPreference(getApplicationContext(), KEY_CAMERA_PERMISSION, flag);
            Snackbar.make(mLayout, "请求相机权限", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{permission},
                                    CAMERA_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .show();

        } else {
            //第一次申请权限时，直接申请权限
            ActivityCompat.requestPermissions(activity, new String[]{permission}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 获取上一次申请权限时的状态
     *
     * @return
     */
    private boolean getLastRequestState() {
        return SharedPrefsUtils.getBooleanPreference(this, KEY_CAMERA_PERMISSION, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Snackbar.make(mLayout, "未申请到权限", Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 跳转到当前应用的设置界面
     */
    private void startAppSetting() {
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
        startActivityForResult(intent, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 从设置界面返回，重新check权限，进行处理
         */
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (hasPermission(getApplicationContext(), CAMERA_PERMISSION)) {
                openCamera();
            } else {
                Snackbar.make(mLayout, "未申请到权限", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
