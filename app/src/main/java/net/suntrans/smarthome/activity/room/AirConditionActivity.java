package net.suntrans.smarthome.activity.room;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import net.suntrans.smarthome.App;
import net.suntrans.smarthome.Config;
import net.suntrans.smarthome.R;
import net.suntrans.smarthome.activity.perc.detail.SensusDetailActivity;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.CmdMsg;
import net.suntrans.smarthome.bean.SensusResult;
import net.suntrans.smarthome.bean.SixChannelParm;
import net.suntrans.smarthome.databinding.ActivityAirBinding;
import net.suntrans.smarthome.utils.LogUtil;
import net.suntrans.smarthome.utils.ParseCMD;
import net.suntrans.smarthome.utils.RxBus;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.websocket.WebSocketService;
import net.suntrans.smarthome.widget.LoadingDialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/8/10.
 */

public class AirConditionActivity extends BasedActivity implements View.OnClickListener {
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
    private String addr;
    private String number;
    private String status;
    private String channel_id;
    private Subscription subscribe1;
    private LoadingDialog dialog;
    private Handler handler = new Handler();
    private String userid;
    private String[] models;
    private int temp;
    private int mode;
    private boolean on;
    private String sensus_id;
    private String vtype;
    private String dev_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_air);
        Intent intent1 = new Intent();
        intent1.setClass(this, WebSocketService.class);
        bindService(intent1, connection, Context.BIND_AUTO_CREATE);

        binding.toolbar.setTitle(getIntent().getStringExtra("name"));
        setSupportActionBar(binding.toolbar);


        addr = getIntent().getStringExtra("addr");
        number = getIntent().getStringExtra("number");
        status = getIntent().getStringExtra("status");
        channel_id = getIntent().getStringExtra("channel_id");
        sensus_id = getIntent().getStringExtra("sensus_id");
        vtype = getIntent().getStringExtra("vtype");
        dev_id = getIntent().getStringExtra("dev_id");

        temp = App.getSharedPreferences().getInt("temp", 16);
        mode = App.getSharedPreferences().getInt("mode", 1);
        on = App.getSharedPreferences().getBoolean("conditionStatus", false);
        models = getResources().getStringArray(R.array.model);

        if (on) {
            applyOpenState();
        } else {
            applyCloseState();
        }
        binding.wendu.setText(temp + "℃");
        binding.mode.setText(models[mode - 1]);

        binding.dianyuan.setChecked(status.equals("1") ? true : false);

        userid = App.getSharedPreferences().getString("user_id", "-1");
        binding.llDianyuan.setOnClickListener(this);
        subscribe1 = RxBus.getInstance().toObserverable(CmdMsg.class)
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
                            parseMsg(cmdMsg.msg);
                        }
                    }
                });
        binding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AirConditionActivity.this, SensusDetailActivity.class);
                intent.putExtra("dev_id", sensus_id);
                intent.putExtra("type", Config.SENSUS);
                startActivity(intent);
                overridePendingTransition(android.support.v7.appcompat.R.anim.abc_slide_in_bottom,0);
            }
        });
    }

    private void applyCloseState() {
        binding.sub.setEnabled(false);
        binding.add.setEnabled(false);
        binding.zhileng.setEnabled(false);
        binding.zhire.setEnabled(false);
        binding.tongfeng.setEnabled(false);
        binding.chushi.setEnabled(false);
        binding.lldakai.setVisibility(View.INVISIBLE);
        binding.llguanbi.setVisibility(View.VISIBLE);
    }

    private void applyOpenState() {

        binding.sub.setEnabled(true);
        binding.add.setEnabled(true);
        binding.zhileng.setEnabled(true);
        binding.zhire.setEnabled(true);
        binding.tongfeng.setEnabled(true);
        binding.chushi.setEnabled(true);
        binding.lldakai.setVisibility(View.VISIBLE);
        binding.llguanbi.setVisibility(View.INVISIBLE);
    }

    private void parseMsg(String msg1) {
        LogUtil.i("airconditionActivity:" + msg1);
        System.out.println(msg1);
        try {
            JSONObject jsonObject = new JSONObject(msg1);
            String code = jsonObject.getString("code");
            String device = jsonObject.getString("device");
            if (!device.equals(Config.STSLC_6) && !device.equals(Config.STSLC_10))
                return;
            if (code.equals("200")) {
                JSONObject result = jsonObject.getJSONObject("result");
                int channel = result.getInt("channel");
                int command = result.getInt("command");
                String addr1 = result.getString("addr");
                Map<String, String> map = ParseCMD.check((short) channel, (short) command);
                if (addr.equals(addr1)) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        String number1 = entry.getKey();
                        String status = entry.getValue();
                        if (number1.equals(number)) {
                            if (status.equals("1")) {
                                binding.dianyuan.setChecked(true);
                            } else {
                                binding.dianyuan.setChecked(false);
                            }
                        }
                    }
                    handler.removeCallbacksAndMessages(null);
                    if (dialog != null)
                        dialog.dismiss();
                }
            } else {
                if (dialog != null)
                    dialog.dismiss();

                UiUtils.showToast("设备不在线");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void showWaitDialog() {
        if (dialog == null) {
            dialog = new LoadingDialog(this, R.style.loading_dialog);
            dialog.setCancelable(false);
            dialog.setWaitText("请稍后...");
        }
        dialog.show();
    }

    private void dissMissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        UiUtils.showToast("设备不在线");
    }


    @Override
    protected void onStop() {
        App.getSharedPreferences().edit().putBoolean("conditionStatus", on)
                .putInt("temp", temp)
                .putInt("mode", mode)
                .commit();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        if (!subscribe1.isUnsubscribed()) {
            subscribe1.unsubscribe();
        }
        if (connection != null) {
            unbindService(connection);
        }
        super.onDestroy();
    }

    public void addTemp(View view) {
        temp++;
        if (temp > 32)
            temp = 32;
        binding.wendu.setText(temp + "℃");
        sendOrder(mode, temp);

    }

    public void subTemp(View view) {
        temp--;
        if (temp < 16)
            temp = 16;
        binding.wendu.setText(temp + "℃");
        sendOrder(mode, temp);
    }

    public void shutdown(View view) {
        if (on) {
            sendOrder(0, temp);
            applyCloseState();
            on = false;
        } else {
            sendOrder(mode, temp);
            applyOpenState();
            on = true;
        }
    }


    public void switchModel(View view) {
        switch (view.getId()) {
            case R.id.zhileng:
                mode = 1;
                break;
            case R.id.zhire:
                mode = 2;
                break;
            case R.id.tongfeng:
                mode = 3;
                break;
            case R.id.chushi:
                mode = 4;
                break;
        }
        binding.mode.setText(models[mode - 1]);
        sendOrder(mode, temp);
    }

    private void sendOrder(int mode, int temp) {
        JSONObject object = new JSONObject();
        try {
            object.put("device", "6100");
            object.put("action", "conditioner");
            object.put("user_id", Integer.valueOf(userid));
            object.put("sensus_id", Integer.valueOf(sensus_id));
            object.put("mode", mode);
            object.put("temp", temp);
            binder.sendOrder(object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.llDianyuan) {

            if (!binding.dianyuan.isChecked()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("device", vtype);
                    jsonObject.put("action", "switch");
                    jsonObject.put("user_id", userid);

                    jsonObject.put("channel_id", Integer.valueOf(channel_id));

                    jsonObject.put("command", binding.dianyuan.isChecked() ? 0 : 1);

                    binder.sendOrder(jsonObject.toString());
                    showWaitDialog();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dissMissDialog();
                        }
                    }, 2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                new AlertDialog.Builder(this).setTitle("警告")
                        .setMessage("是否关闭电源?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("device", vtype);
                                    jsonObject.put("action", "switch");
                                    jsonObject.put("user_id", userid);

                                    jsonObject.put("channel_id", Integer.valueOf(channel_id));

                                    jsonObject.put("command", binding.dianyuan.isChecked() ? 0 : 1);
                                    binder.sendOrder(jsonObject.toString());
                                    showWaitDialog();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dissMissDialog();
                                        }
                                    }, 2000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).setNegativeButton("取消", null).create().show();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEnvData();
        getParm();
    }

    private void getEnvData() {
        RetrofitHelper.getApi().getSensueDetail(sensus_id)
                .compose(this.<SensusResult>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SensusResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(SensusResult sensusResult) {
                        if (sensusResult.status.equals("1")) {
                            if (sensusResult.result != null) {
                                if (sensusResult.result.row != null) {
                                    SensusResult.Sensus sensus = sensusResult.result.row;
                                    sensus.setEva();
                                    initView(sensus);
                                }
                            }
                        }

                    }
                });
    }

    private void getParm(){
        RetrofitHelper.getApi().getSixChannelPram(number,dev_id)
                .compose(this.<SixChannelParm>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SixChannelParm>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SixChannelParm sixChannelParm) {
                            binding.dianya.setText("电压:"+sixChannelParm.result.row.get(0).V+"V");
                            binding.dianl.setText("电流:"+sixChannelParm.result.row.get(0).I+"A");
                            binding.gonglv.setText("功率:"+sixChannelParm.result.row.get(0).P+"w");
                            binding.gonglvyinsu.setText("功率因数:"+sixChannelParm.result.row.get(0).PR+"");
                    }
                });
    }

    private void initView(SensusResult.Sensus sensus) {
        binding.shineiwendu.setText("温度:"+sensus.getWendu()+"℃");
        binding.shidu.setText("湿度:"+sensus.getShidu()+"%RH");
        binding.pm25.setText("PM25:"+sensus.getPm25()+"ppm");
        binding.jiaquan.setText("甲醛:"+sensus.getJiaquan()+"ppm");
    }
}


