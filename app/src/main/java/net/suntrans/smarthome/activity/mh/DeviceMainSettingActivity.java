package net.suntrans.smarthome.activity.mh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.suntrans.smarthome.Config;
import net.suntrans.smarthome.R;
import net.suntrans.smarthome.activity.ModifyINActivity;
import net.suntrans.smarthome.activity.perc.detail.SwitchControlActivity;
import net.suntrans.smarthome.activity.room.RoomDetailActivity;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.ChannelEdit;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/5/20.
 */

public class DeviceMainSettingActivity extends BasedActivity implements View.OnClickListener {

    private String id;
    private String devid;
    private String roomid;
    private ImageView touxiang;

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

        TextView name = (TextView) findViewById(R.id.name);
        TextView kongzhiqi = (TextView) findViewById(R.id.kongzhiqi);
        TextView room = (TextView) findViewById(R.id.room);

        touxiang = (ImageView) findViewById(R.id.touxiang);

        id = getIntent().getStringExtra("id");
        devid = getIntent().getStringExtra("dev_id");
        roomid = getIntent().getStringExtra("house_id");
        String names = getIntent().getStringExtra("name");

        name.setText(names);
        kongzhiqi.setText(devid);
        room.setText(getIntent().getStringExtra("house_name"));
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_devices_setting);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rlName:
                Intent intent = new Intent(this, ModifyINActivity.class);
                intent.putExtra("name",getIntent().getStringExtra("name"));
                intent.putExtra("imgurl",imgUrl);
                intent.putExtra("type","channel");
                intent.putExtra("id",id);
                startActivity(intent);
                break;
            case R.id.rlRoom:
                Intent intent2= new Intent();
                intent2.putExtra("house_id", getIntent().getStringExtra("house_id"));
                intent2.putExtra("name", getIntent().getStringExtra("house_name"));
                intent2.putExtra("imgurl", getIntent().getStringExtra("imgurl"));
                intent2.setClass(this,RoomDetailActivity.class);
                startActivity(intent2);
                break;
            case R.id.rlControl:
                Intent intent1 = new Intent();
                intent1.setClass(this, SwitchControlActivity.class);
                intent1.putExtra("dev_id", devid);
                intent1.putExtra("type", getIntent().getStringExtra("vtype"));
                intent1.putExtra("name",devid);
                intent1.putExtra("subname",getIntent().getStringExtra("vtype").equals(Config.STSLC_6)?"六通道":"十通道");
                startActivity(intent1);
                break;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.tips_delete_devices)
                        .setNegativeButton(R.string.queding,null)
                        .setPositiveButton(R.string.qvxiao,null)
                        .create().show();
                break;
        }
    }

    @Override
    protected void onResume() {
        getData(id);
        super.onResume();
    }


    private String imgUrl="";
    private String img_id="";
    private void getData(String channel_id){
        RetrofitHelper.getApi().getChannelInfo(channel_id)
                .compose(this.<ChannelEdit>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ChannelEdit>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ChannelEdit s) {
                        Glide.with(DeviceMainSettingActivity.this)
                                .load(s.result.row.img)
                                .centerCrop()
                                .into(touxiang);
                        imgUrl = s.result.row.img;
                        img_id =s.result.row.img_id;
                    }
                });
    }
}
