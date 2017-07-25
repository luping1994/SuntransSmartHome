package net.suntrans.smarthome.activity.mh;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;

import net.suntrans.smarthome.App;
import net.suntrans.smarthome.R;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.CmdMsg;
import net.suntrans.smarthome.bean.SceneChannelResult;
import net.suntrans.smarthome.databinding.ActivityHousedetailBinding;
import net.suntrans.smarthome.utils.LogUtil;
import net.suntrans.smarthome.utils.ParseCMD;
import net.suntrans.smarthome.utils.RxBus;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.websocket.WebSocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static net.suntrans.smarthome.R.id.msg;

/**
 * Created by Looney on 2017/4/29.
 */

public class SenceDetailActivity extends BasedActivity implements View.OnClickListener {

    private final String TAG = "SenceDetailActivity";
    private ActivityHousedetailBinding binding;
    private String title;
    private String imgurl;
    private String id;
    private List<SceneChannelResult.SceneChannel> datas;
    private MyAdapter adapter;

    private String userid;

    private WebSocketService.ibinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (WebSocketService.ibinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private Subscription subscribe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_housedetail);

        Intent intent = new Intent();
        intent.setClass(this, WebSocketService.class);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        title = getIntent().getStringExtra("title");
        binding.title.setText(title);
        binding.edit.setOnClickListener(this);
        imgurl = getIntent().getStringExtra("imgurl");

        Glide.with(this)
                .load(imgurl)
                .placeholder(R.drawable.img_original_bg_sleep)
                .centerCrop()
                .into(binding.modeImg);
        id = getIntent().getStringExtra("id");
        LogUtil.i(TAG, "id=" + id);
        LogUtil.i(TAG, "imgurl=" + imgurl);
        userid = App.getSharedPreferences().getString("user_id", "-1");

        datas = new ArrayList<>();
        adapter = new MyAdapter(R.layout.item_scene_channel, datas);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.baseIvSceneBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
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
                            parseMsg(cmdMsg.msg);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.edit) {
            Intent intent = new Intent(this, EditSenceActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("imgurl", imgurl);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            intent.putExtra("count", datas.size());
            startActivity(intent);
        }
        if (v.getId() == R.id.hand_add) {

        }
    }

    private static long INTERVAL=3000L;
    private static long lastTime = 0;
    public void excute(View view) {
        long time = System.currentTimeMillis();
        if (time - lastTime>INTERVAL){
//            System.out.println("sb");
            lastTime =time;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("device", "scene");
                jsonObject.put("scene_id",Integer.valueOf(id));
                jsonObject.put("user_id",Integer.valueOf(userid));
                if (binder.sendOrder(jsonObject.toString())){
                    UiUtils.showToast("已经为您执行改场景");

                }else {
                    UiUtils.showToast("执行失败");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            UiUtils.showToast("正在执行中");
        }
//        //{ "device": "scene", "scene_id": 1,"user_id":123}

    }

    class MyAdapter extends BaseQuickAdapter<SceneChannelResult.SceneChannel, BaseViewHolder> {


        public MyAdapter(@LayoutRes int layoutResId, @Nullable List<SceneChannelResult.SceneChannel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SceneChannelResult.SceneChannel item) {
            helper.setText(R.id.name, item.name)
                    .setText(R.id.action, item.status.equals("1") ? getString(R.string.channel_choose_open) : getString(R.string.channel_choose_close));
            TextView state = helper.getView(R.id.action);
            if (item.status.equals("1"))
                state.setTextColor(getResources().getColor(R.color.colorPrimary));
            else
                state.setTextColor(getResources().getColor(R.color.colorAccent));

            ImageView imageView = helper.getView(R.id.img);
            imageView.setVisibility(View.GONE);
//
//            Glide.with(SenceDetailActivity.this)
//                    .load(item.img)
//                    .crossFade()
//                    .centerCrop()
//                    .placeholder(R.drawable.ic_light)
//                    .into(imageView);
        }
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    private void getData() {
        RetrofitHelper.getApi().getSceneChannel(id)
                .compose(this.<SceneChannelResult>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SceneChannelResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SceneChannelResult result) {
                        datas.clear();
                        datas.addAll(result.result);
                        adapter.notifyDataSetChanged();
                        LogUtil.i("场景动作的数量：" + datas.size());
                        if (datas.size() == 0) {
                            binding.tipEditModeNodevice.setVisibility(View.VISIBLE);
                        } else {
                            binding.tipEditModeNodevice.setVisibility(View.GONE);

                        }
                    }
                });
    }

    private void parseMsg(String msg1) {
        try {
            JSONObject jsonObject = new JSONObject(msg1);
            String code = jsonObject.getString("code");
            if (code.equals("200")) {
                JSONObject result = jsonObject.getJSONObject("result");
                int channel = result.getInt("channel");
                int command = result.getInt("command");
                String device = result.getString("device");
                String addr = result.getString("addr");
                Map<String, String> map = ParseCMD.check((short) channel, (short) command);

                for (int i = 0; i < datas.size(); i++) {

                }


            } else if (code.equals("404")) {

            } else if (code.equals("101")) {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        if (subscribe != null) {
            if (!subscribe.isUnsubscribed()) {
                subscribe.unsubscribe();
            }
        }
        super.onDestroy();
    }
}
