package net.suntrans.smarthome.activity.room;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.databinding.ActivityAirBinding;
import net.suntrans.smarthome.websocket.WebSocketService;
import android.content.Context;

/**
 * Created by Looney on 2017/8/10.
 */

public class AirConditionActivity extends BasedActivity {
    private WebSocketService.ibinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (WebSocketService.ibinder) service;
//            System.out.println("绑定服务成功!");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ActivityAirBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_air);
        Intent intent1 = new Intent();
        intent1.setClass(this, WebSocketService.class);
        bindService(intent1, connection, Context.BIND_AUTO_CREATE);

    }
}
