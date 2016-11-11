package me.chen_wei.permissiondemo.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author: Chen Wei
 * time: 16/11/7
 * desc: 描述
 */

public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getCanonicalName();
    private static final String DIALOG_TAG = "RationaleDialogFragmentCompat";

    public interface PermissionCallbacks extends ActivityCompat.OnRequestPermissionsResultCallback {

        void onPermissionGranted(int requestCode, List<String> perms);

        void onPermissionDenied(int requestCode, List<String> perms);

    }

    public static boolean hasPermissions(Context context, String... perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }

        return true;
    }

    public static void requestPermissions(final Object object, String rationale, final int requestCode, final String... perms) {
        requestPermissions(object, rationale, android.R.string.ok, android.R.string.cancel, requestCode, perms);
    }

    public static void requestPermissions(final Object object, String rationale, int positiveButton, int negativeButton, final int requestCode, final String... perms) {
        checkCallingObjectSuitability(object);

        boolean shouldShowRationale = false;
        for (String perm : perms) {
            shouldShowRationale = shouldShowRequestPermissionRationale(object, perm);
        }

        if (shouldShowRationale) {
            // 提示申请权限缘由
            if (getSupportFragmentManager(object) != null) {
                showRationaleDialogFragmentCompat(getSupportFragmentManager(object),
                        rationale, positiveButton, negativeButton, requestCode, perms);
            } else if (getFragmentManager(object) != null) {
                showRationaleDialogFragment(getFragmentManager(object),
                        rationale, positiveButton, negativeButton, requestCode, perms);
            } else {
                showRationaleAlertDialog(object, rationale, positiveButton, negativeButton, requestCode, perms);
            }
        } else {
            // 直接申请权限
            executePermissionsRequest(object, perms, requestCode);
        }
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    private static void showRationaleDialogFragmentCompat(@NonNull final FragmentManager fragmentManager,
                                                          @NonNull String rationale,
                                                          @StringRes int positiveButton,
                                                          @StringRes int negativeButton,
                                                          final int requestCode,
                                                          @NonNull final String... perms) {
        RationaleDialogFragmentCompat fragment = RationaleDialogFragmentCompat
                .newInstance(positiveButton, negativeButton, rationale, requestCode, perms);
        fragment.show(fragmentManager, DIALOG_TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private static void showRationaleDialogFragment(@NonNull final android.app.FragmentManager fragmentManager,
                                                    @NonNull String rationale,
                                                    @StringRes int positiveButton,
                                                    @StringRes int negativeButton,
                                                    final int requestCode,
                                                    @NonNull final String... perms) {
        RationaleDialogFragment fragment = RationaleDialogFragment
                .newInstance(positiveButton, negativeButton, rationale, requestCode, perms);
        fragment.show(fragmentManager, DIALOG_TAG);
    }

    private static void showRationaleAlertDialog(@NonNull final Object object,
                                                 @NonNull String rationale,
                                                 @StringRes int positiveButton,
                                                 @StringRes int negativeButton,
                                                 final int requestCode,
                                                 @NonNull final String... perms) {
        Activity activity = getActivity(object);
        if (activity == null) {
            throw new IllegalStateException("Can't show rationale dialog for null Activity");
        }

        new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setMessage(rationale)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        executePermissionsRequest(object, perms, requestCode);
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (object instanceof PermissionCallbacks) {
                            ((PermissionCallbacks) object).onPermissionDenied(requestCode, Arrays.asList(perms));
                        }
                    }
                })
                .create()
                .show();
    }

    @TargetApi(23)
    static void executePermissionsRequest(Object object, String[] perms, int requestCode) {
        checkCallingObjectSuitability(object);

        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, String[] perms, int[] grantResults, Object... receivers) {
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();
        for (int i = 0; i < perms.length; i++) {
            String perm = perms[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }

        for (Object object : receivers) {
            if (!granted.isEmpty()) {
                if (object instanceof PermissionCallbacks) {
                    ((PermissionCallbacks) object).onPermissionGranted(requestCode, granted);
                }
            }

            if (!denied.isEmpty()) {
                if (object instanceof PermissionCallbacks) {
                    ((PermissionCallbacks) object).onPermissionDenied(requestCode, denied);
                }
            }

            // 所有权限都已授予
            if (!granted.isEmpty() && denied.isEmpty()) {
                runAnnotatedMethods(object, requestCode);
            }
        }
    }

    /**
     * 解析注解，执行被注解的方法
     *
     * @param object
     * @param requestCode
     */
    private static void runAnnotatedMethods(Object object, int requestCode) {
        Class clazz = object.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AfterPermissionGranted.class)) {
                AfterPermissionGranted ann = method.getAnnotation(AfterPermissionGranted.class);
                if (ann.value() == requestCode) {
                    if (method.getParameterTypes().length > 0) {
                        throw new RuntimeException(
                                "Cannot execute method" + method.getName() + " because it is non-void method and/or has input parameters."
                        );
                    }

                    try {
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(object);
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "runDefaultMethod: IllegalAccessException", e);
                    } catch (InvocationTargetException e) {
                        Log.e(TAG, "runDefaultMethod: InvocationTargetException", e);
                    }
                }
            }
        }
    }

    @TargetApi(23)
    private static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof android.support.v4.app.Fragment) {
            return ((android.support.v4.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    @TargetApi(11)
    private static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof android.support.v4.app.Fragment) {
            return ((android.support.v4.app.Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }

    private static android.support.v4.app.FragmentManager getSupportFragmentManager(Object object) {
        if (object instanceof android.support.v4.app.FragmentActivity) {
            return ((FragmentActivity) object).getSupportFragmentManager();
        } else if (object instanceof android.support.v4.app.Fragment) {
            return ((Fragment) object).getChildFragmentManager();
        }
        return null;
    }

    private static android.app.FragmentManager getFragmentManager(Object object) {
        if (object instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                return ((Activity) object).getFragmentManager();
            }
        } else if (object instanceof android.app.Fragment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return ((android.app.Fragment) object).getChildFragmentManager();
            } else {
                return ((android.app.Fragment) object).getFragmentManager();
            }
        }
        return null;
    }

    /**
     * 是否勾选了 "不再询问"
     *
     * @param object
     * @param deniedPermissions
     * @return
     */
    public static boolean somePermissionsPermanentlyDenied(@NonNull Object object,
                                                           @NonNull List<String> deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (permissionPermanentlyDenied(object, deniedPermission)) {
                return true;
            }
        }

        return false;
    }

    public static boolean permissionPermanentlyDenied(@NonNull Object object,
                                                      @NonNull String deniedPermission) {
        return !shouldShowRequestPermissionRationale(object, deniedPermission);
    }

    public static void onPermissionsPermanentlyDenied(@NonNull Activity activity,
                                                      @NonNull String rationale,
                                                      @NonNull String title,
                                                      @NonNull String positiveButton,
                                                      @NonNull String negativeButton,
                                                      int requestCode) {
        new AppSettingsDialog.Builder(activity, rationale)
                .setTitle(title)
                .setPositiveButton(positiveButton)
                .setNegativeButton(negativeButton, null)
                .setRequestCode(requestCode)
                .build()
                .show();
    }

    public static void onPermissionsPermanentlyDenied(@NonNull android.app.Fragment fragment,
                                                      @NonNull String rationale,
                                                      @NonNull String title,
                                                      @NonNull String positiveButton,
                                                      @NonNull String negativeButton,
                                                      int requestCode) {
        new AppSettingsDialog.Builder(fragment, rationale)
                .setTitle(title)
                .setPositiveButton(positiveButton)
                .setNegativeButton(negativeButton, null)
                .setRequestCode(requestCode)
                .build()
                .show();
    }

    public static void onPermissionsPermanentlyDenied(@NonNull Fragment fragment,
                                                      @NonNull String rationale,
                                                      @NonNull String title,
                                                      @NonNull String positiveButton,
                                                      @NonNull String negativeButton,
                                                      int requestCode) {
        new AppSettingsDialog.Builder(fragment, rationale)
                .setTitle(title)
                .setPositiveButton(positiveButton)
                .setNegativeButton(negativeButton, null)
                .setRequestCode(requestCode)
                .build()
                .show();
    }


    private static void checkCallingObjectSuitability(Object object) {
        if (object == null) {
            throw new NullPointerException("Activity or Fragment should not be null");
        }

        boolean isActivity = object instanceof android.app.Activity;
        boolean isSupportFragment = object instanceof android.support.v4.app.Fragment;
        boolean isAppFragment = object instanceof android.app.Fragment;
        boolean isMinSdkM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

        if (!(isSupportFragment || isActivity || (isAppFragment && isMinSdkM))) {
            if (isAppFragment) {
                throw new IllegalArgumentException(
                        "Target SDK needs to be greater than 23 if caller is android.app.Fragment");
            } else {
                throw new IllegalArgumentException(
                        "Caller must be an Activity or a Fragment");
            }
        }
    }
}
