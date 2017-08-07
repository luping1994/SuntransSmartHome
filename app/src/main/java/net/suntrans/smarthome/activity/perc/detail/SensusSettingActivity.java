package net.suntrans.smarthome.activity.perc.detail;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;

import net.suntrans.smarthome.App;
import net.suntrans.smarthome.Config;
import net.suntrans.smarthome.R;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.CmdMsg;
import net.suntrans.smarthome.bean.SensusSettingResult;
import net.suntrans.smarthome.databinding.ActivitySensussettingBinding;
import net.suntrans.smarthome.utils.LogUtil;
import net.suntrans.smarthome.utils.RxBus;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.websocket.SenderWebSocket;
import net.suntrans.smarthome.websocket.WebSocketService;
import net.suntrans.smarthome.widget.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Looney on 2017/4/27.
 */

public class SensusSettingActivity extends BasedActivity implements  CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private ActivitySensussettingBinding binding;
    private String id;
    private SensusSettingResult.Commmand commmand;
    private LoadingDialog dialog;
    private String userid;


    private WebSocketService.ibinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (WebSocketService.ibinder) service;
            getConfig();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private Subscription subscribe;
    private String addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent();
        intent.setClass(this, WebSocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        addr = getIntent().getStringExtra("addr");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sensussetting);
        setSupportActionBar(binding.toolbar);
        binding.title.setText(R.string.sensus_waring_config);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        id = getIntent().getStringExtra("id");
        dialog = new LoadingDialog(this, R.style.loading_dialog);
        dialog.setCancelable(false);
        binding.switchButtonWendu.setOnCheckedChangeListener(this);
        binding.switchButtonJiaquan.setOnCheckedChangeListener(this);
        binding.switchButtonPm25.setOnCheckedChangeListener(this);
        binding.switchButtonYanwu.setOnCheckedChangeListener(this);
        binding.switchButtonZhengdong.setOnCheckedChangeListener(this);
        binding.switchButtonRenyuan.setOnCheckedChangeListener(this);

        binding.yuzhiWendu.setOnClickListener(this);
        binding.yuzhiJiaquan.setOnClickListener(this);
        binding.yuzhiYanwu.setOnClickListener(this);
        binding.yuzhiPm25.setOnClickListener(this);
        binding.yuzhiZhengdong.setOnClickListener(this);

        binding.refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getConfig();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.refreshlayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        subscribe = RxBus.getInstance().toObserverable(CmdMsg.class)
                .compose(this.<CmdMsg>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CmdMsg>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CmdMsg cmdMsg) {
                        if (cmdMsg.status == 1) {
                            onMessage(cmdMsg.msg);
                        } else {
                        }
                    }
                });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void getConfig() {
        if (TextUtils.isEmpty(userid))
            userid = App.getSharedPreferences().getString("user_id", "-1");


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device", "6100");
            jsonObject.put("action", "rconfig");
            jsonObject.put("user_id", Integer.valueOf(userid));
            jsonObject.put("device_id", Integer.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        binder.sendOrder(jsonObject.toString());
    }

    @Override
    protected void onDestroy() {
       if (connection!=null){
           unbindService(connection);

       }
        if (subscribe!=null){
            if (!subscribe.isUnsubscribed()){
                subscribe.unsubscribe();
            }
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public void onMessage(String s) {
        SensusSettingResult result = JSON.parseObject(s, SensusSettingResult.class);
        if (result==null||result.getParam()==null||result.getCommand()==null){
            return;
        }
//

//        if (!result.getDevice().equals(Config.SENSUS)){
//            return;
//        }

        handler.removeMessages(MSG_CON_FAILED);
        handler.sendEmptyMessage(MSG_CON_SUCCESS);
        if (result.getCode().equals("200")) {
            binding.setParam(result.getParam());
            commmand = new SensusSettingResult.Commmand(result.getCommand());
            binding.setCommand(commmand);
        } else if (result.getCode().equals("404")) {
            handler.sendMessage(Message.obtain(handler, MSG_CON_FAILED, result.getMsg()));
            UiUtils.showToast("配置错误");
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                binding.refreshlayout.setRefreshing(false);

            }
        });
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        handler.sendEmptyMessage(MSG_START);
        handler.sendMessageDelayed(Message.obtain(handler, MSG_CON_FAILED, getString(R.string.tips_request_failed)), 2000);
        SensusSettingResult.Commmand commmand = binding.getCommand();
        if (commmand == null) {
            UiUtils.showToast(getString(R.string.tips_device_offline));
            return;
        }
        switch (buttonView.getId()) {
            case R.id.switchButton_jiaquan:
                commmand.setJiaquan(isChecked);
                break;
            case R.id.switchButton_pm25:
                commmand.setPm25(isChecked);
                break;
            case R.id.switchButton_renyuan:
                commmand.setRenyuan(isChecked);
                break;
            case R.id.switchButton_wendu:
                commmand.setWendu(isChecked);
                break;
            case R.id.switchButton_yanwu:
                commmand.setYanwu(isChecked);
                break;
            case R.id.switchButton_zhengdong:
                commmand.setZhengdong(isChecked);
                break;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device", "6100");
            jsonObject.put("action", "wconfig");
            jsonObject.put("user_id", Integer.valueOf(userid));
            jsonObject.put("device_id", Integer.valueOf(id));
            jsonObject.put("command", commmand.toString());
            jsonObject.put("param", binding.getParam().getJsonParm());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        binder.sendOrder(jsonObject.toString());
        LogUtil.i("sensusSettingActivity", jsonObject.toString());
    }

    private static final int MSG_START = 0;
    private static final int MSG_CON_SUCCESS = 1;
    private static final int MSG_CON_FAILED = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                    dialog.setWaitText(getString(R.string.tips_please_wait));
                    dialog.show();
                    break;
                case MSG_CON_SUCCESS:
                    dialog.dismiss();
                    break;
                case MSG_CON_FAILED:
                    dialog.setWaitText((String) msg.obj);
                    handler.sendEmptyMessageDelayed(MSG_CON_SUCCESS, 500);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        showYuzhiDialog(v);
    }

    private void showYuzhiDialog(final View v) {

        View view = LayoutInflater.from(this).inflate(R.layout.item_dialog, null, false);
        final EditText value = (EditText) view.findViewById(R.id.value);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("请输入阈值")
                .setPositiveButton(R.string.queding, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double value1 = Double.parseDouble(value.getText().toString());
                        switch (v.getId()){
                            case R.id.yuzhi_wendu:

                                binding.getParam().setV1(value1);
                                break;
                            case R.id.yuzhi_yanwu:
                                binding.getParam().setV2(value1);
                                break;

                            case R.id.yuzhi_jiaquan:
                                binding.getParam().setV3(value1);
                                break;
                            case R.id.yuzhi_pm25:
                                binding.getParam().setV4(value1);
                                break;
                            case R.id.yuzhi_zhengdong:
                                binding.getParam().setV5(value1);
                                break;

                        }
                        binding.executePendingBindings();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("device", "6100");
                            jsonObject.put("action", "wconfig");
                            jsonObject.put("user_id", Integer.valueOf(userid));
                            jsonObject.put("device_id", Integer.valueOf(id));
                            jsonObject.put("command", binding.getCommand().toString());
                            jsonObject.put("param", binding.getParam().getJsonParm());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LogUtil.i(jsonObject.toString());
                        binder.sendOrder(jsonObject.toString());

                    }
                })
                .setNegativeButton(R.string.qvxiao, null);
        builder.create().show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
