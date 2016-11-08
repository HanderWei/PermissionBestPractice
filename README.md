# Android M 权限最佳实践

## 权限申请流程
[权限申请流程](http://7xs83t.com1.z0.glb.clouddn.com/Android%20M%20%E6%9D%83%E9%99%90%E7%94%B3%E8%AF%B7%28%E6%97%A0%E9%9C%80%E4%BF%9D%E5%AD%98%E7%8A%B6%E6%80%81%29.png)

## 封装EasePermissions

Google开源的[EasePermissions](https://github.com/googlesamples/easypermissions)的思路非常清晰，对申请权限的各种状态都做了处理。

下面是我对其做的简单封装

* BaseActivity
```
public class BaseActivity extends AppCompatActivity implements PermissionUtils.PermissionCallbacks {

    private static final String TAG = BaseActivity.class.getCanonicalName();

    private static final int PERMANENTLY_DENIED_REQUEST_CODE = 428; // Magic code

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
        // 用户勾选了“不在询问”后
        if (PermissionUtils.somePermissionsPermanentlyDenied(this, perms)) {
            PermissionUtils.onPermissionsPermanentlyDenied(this,
                    getString(R.string.rationale),
                    getString(R.string.rationale_title),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel),
                    PERMANENTLY_DENIED_REQUEST_CODE);
        }
    }
}
```
在BaseActivity中实现了*PermissionUtils.PermissionCallbacks*接口，覆写了*onRequestPermissionsResult()*方法，并在*onPermissionDenied()*中对用户勾选**不在询问**的情况作了处理（进入应用-设置界面）

* BaseFragment
```
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
```
BaseFragment同样实现了*PermissionUtils.PermissionCallbacks*接口，覆写了*onRequestPermissionsResult()*方法，但是并未处理**不在询问**的情况，原因很简单，因为BaseActivity会统一处理

