package net.suntrans.suntranssmarthome.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import net.suntrans.suntranssmarthome.App;
import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.homepage.MainActivity;
import net.suntrans.suntranssmarthome.utils.LogUtil;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Looney on 2017/5/8.
 */

public class WelcomeActivity extends BasedActivity implements EasyPermissions.PermissionCallbacks {
    private static final int CAMERA_AND_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        init();
    }

    private void init() {
        check();
//        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            LogUtil.i("有权限");
//            check();
//        } else {
//            LogUtil.i("没有权限");
//
//            EasyPermissions.requestPermissions(this, "", CAMERA_AND_STORAGE
//                    , Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
    }

    private void check() {
        try {
            String access_token = App.getSharedPreferences().getString("access_token", "-1");
            String expires_in = App.getSharedPreferences().getString("expires_in", "-1");
            long firsttime = App.getSharedPreferences().getLong("firsttime", -1l);
            long currenttime = System.currentTimeMillis();
            long d = (currenttime - firsttime) / 1000;


            if (access_token.equals("-1") || expires_in.equals("-1") || firsttime == -1l) {
                handler.sendEmptyMessageDelayed(START_LOGIN, 1800);
            } else {
                if (d > (Long.valueOf(expires_in) - 1 * 24 * 3600)) {
                    handler.sendEmptyMessageDelayed(START_LOGIN, 1800);

                } else {
                    handler.sendEmptyMessageDelayed(START_MAIN, 1800);

                }
            }
        } catch (Exception e) {
            handler.sendEmptyMessageDelayed(START_LOGIN, 1800);
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    final int START_LOGIN = 0;
    final int START_MAIN = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == START_LOGIN) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
            }
            if (msg.what == START_MAIN) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        LogUtil.i("权限不通过");
        finish();
//        check();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LogUtil.i("权限通过");
        check();
    }
}
