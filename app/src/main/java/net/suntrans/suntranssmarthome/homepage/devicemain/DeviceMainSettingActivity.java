package net.suntrans.suntranssmarthome.homepage.devicemain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.suntrans.suntranssmarthome.Config;
import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.homepage.device.switchs.SwitchControlActivity;

/**
 * Created by Looney on 2017/5/20.
 */

public class DeviceMainSettingActivity extends BasedActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicemain_setting);
        setUpToolbar();
        init();
    }

    private void init() {
        findViewById(R.id.rlName).setOnClickListener(this);
        findViewById(R.id.rlRoom).setOnClickListener(this);
        findViewById(R.id.rlControl).setOnClickListener(this);
        findViewById(R.id.rlDelete).setOnClickListener(this);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设备设置");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rlName:
                Intent intent = new Intent();
                intent.setClass(this,ChooseDeviceTypeActivity.class);
                startActivity(intent);
                break;
            case R.id.rlRoom:
                Intent intent2= new Intent();
                intent2.setClass(this,ChooseDeviceRoomActivity.class);
                startActivity(intent2);
                break;
            case R.id.rlControl:
                Intent intent1 = new Intent();
                intent1.setClass(this, SwitchControlActivity.class);
                intent1.putExtra("dev_id", "38");
                intent1.putExtra("type", Config.STSLC_10);
                intent1.putExtra("name","广州04001600");
                intent1.putExtra("subname","十通道");
                startActivity(intent1);
                break;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setMessage("是否删除设备")
                        .setNegativeButton("确定",null)
                        .setPositiveButton("取消",null)
                        .create().show();
                break;
        }
    }
}
