package me.chen_wei.permissiondemo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;


import me.chen_wei.permissiondemo.permission.AfterPermissionGranted;
import me.chen_wei.permissiondemo.permission.PermissionUtils;


/**
 * author: Chen Wei
 * time: 16/11/8
 * desc: 描述
 */

public class TestActivity extends BaseActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 0x01;
    private static final int REQUEST_CALENDAR_AND_CONTACTS = 0x02;

    private static final int REQUEST_OPEN_CAMERA = 0x11;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void needCamera(View view) {
        if (PermissionUtils.hasPermissions(this, Manifest.permission.CAMERA)) {
            openCamera();
        } else {
            PermissionUtils.requestPermissions(this, getString(R.string.rationale_camera), REQUEST_CAMERA_PERMISSION, Manifest.permission.CAMERA);
        }
    }

    @AfterPermissionGranted(REQUEST_CAMERA_PERMISSION)
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_OPEN_CAMERA);
        }
    }

    public void needTwoPermissions(View view) {
        String[] perms = new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.READ_CONTACTS};
        if (PermissionUtils.hasPermissions(this, perms)) {
            twoPermissionsGranted();
        } else {
            PermissionUtils.requestPermissions(this, getString(R.string.rationale_calendar_and_contacts), REQUEST_CALENDAR_AND_CONTACTS, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_CALENDAR_AND_CONTACTS)
    private void twoPermissionsGranted() {
//        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        Cursor calCur = cr.query(CalendarContract.Calendars.CONTENT_URI, null, null, null, null);
        StringBuilder toast = new StringBuilder();
        if (cur != null) {
            toast.append("有" + cur.getCount() + "条联系人信息");
        }

        if (calCur != null) {
            toast.append("\n有" + calCur.getCount() + "条日历信息");
        }
        Toast.makeText(this, toast.toString(), Toast.LENGTH_SHORT).show();
    }
}
