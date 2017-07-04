package net.suntrans.smarthome.activity.perc.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.widget.CompoundButton;

import com.alibaba.fastjson.JSON;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.SensusSettingResult;
import net.suntrans.smarthome.databinding.ActivitySensussettingBinding;
import net.suntrans.smarthome.utils.LogUtil;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.websocket.SenderWebSocket;
import net.suntrans.smarthome.widget.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Looney on 2017/4/27.
 */

public class SensusSettingActivity extends BasedActivity implements SenderWebSocket.onReceiveListener, CompoundButton.OnCheckedChangeListener {

    private ActivitySensussettingBinding binding;
    private SenderWebSocket socket;
    private String id;
    private SensusSettingResult.Commmand commmand;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            binding = DataBindingUtil.setContentView(this, R.layout.activity_sensussetting);
        setSupportActionBar(binding.toolbar);
        binding.title.setText(R.string.sensus_waring_config);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
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
        binding.refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getConfig();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.refreshlayout.setRefreshing(false);
                    }
                },2000);
            }
        });
    }


    @Override
    protected void onResume() {
        getConfig();
        super.onResume();
    }


    private void getConfig() {
        if (socket == null)
            socket = new SenderWebSocket();
        socket.connect();
        socket.setOnReceiveListener(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device", "sensus");
            jsonObject.put("action", "rconfig");
            jsonObject.put("device_id", Integer.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.sendMessage(jsonObject.toString());
//        LogUtil.i("SensusSettingActivity", jsonObject.toString());
    }

    @Override
    protected void onDestroy() {
        if (socket != null) {
            socket.closeWebSocket();
            socket = null;
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onMessage(String s) {
        LogUtil.i(s);
        SensusSettingResult result = JSON.parseObject(s, SensusSettingResult.class);
        handler.post(new Runnable() {
            @Override
            public void run() {
                binding.refreshlayout.setRefreshing(false);

            }
        });
        handler.removeMessages(MSG_CON_FAILED);
        handler.sendEmptyMessage(MSG_CON_SUCCESS);
        if (result.getCode().equals("200")){
            binding.setParam(result.getParam());
            commmand = new SensusSettingResult.Commmand(result.getCommand());
            binding.setCommand(commmand);
        }else if (result.getCode().equals("404")){
            handler.sendMessage(Message.obtain(handler, MSG_CON_FAILED, result.getMsg()));
            UiUtils.showMessage(findViewById(android.R.id.content),result.getMsg());
        }

    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtil.i("我被点击了");
        handler.sendEmptyMessage(MSG_START);
        handler.sendMessageDelayed(Message.obtain(handler, MSG_CON_FAILED, getString(R.string.tips_request_failed)), 2000);
        SensusSettingResult.Commmand commmand = binding.getCommand();
        if (commmand==null){
            UiUtils.showToast(getString(R.string.tips_device_offline));
            return;
        }
        switch (buttonView.getId()){
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
            jsonObject.put("device","sensus");
            jsonObject.put("action","wconfig");
            jsonObject.put("device_id",id);
            jsonObject.put("command",commmand.toString());
            jsonObject.put("param",binding.getParam().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
       socket.sendMessage(jsonObject.toString());
        LogUtil.i("sensusSettingActivity",jsonObject.toString());
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
}
