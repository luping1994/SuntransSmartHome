package net.suntrans.suntranssmarthome.homepage.myhome.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.add.CreateModelResult;
import net.suntrans.suntranssmarthome.homepage.myhome.detail.ModifyINActivity;
import net.suntrans.suntranssmarthome.utils.UiUtils;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/4/29.
 */

public class EditRoomActivity extends BasedActivity implements View.OnClickListener {

    private String imgurl;
    private String id;
    private TextView delete;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_house);
        setUpToolBar();
        imgurl = getIntent().getStringExtra("imgurl");
        id = getIntent().getStringExtra("id");
        delete = (TextView) findViewById(R.id.delete);
        name = (TextView) findViewById(R.id.name);
        name.setText(getIntent().getStringExtra("title"));
        ImageView imageView = (ImageView) findViewById(R.id.touxiang);
        Glide.with(this)
                .load(imgurl)
                .centerCrop()
                .into(imageView);
        findViewById(R.id.ll).setOnClickListener(this);
        findViewById(R.id.shebei).setOnClickListener(this);
        findViewById(R.id.rizhi).setOnClickListener(this);
        delete.setOnClickListener(this);

    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("房间设置");
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll) {
            Intent intent = new Intent();
            intent.setClass(this, ModifyINActivity.class);
            intent.putExtra("name", name.getText().toString());
            intent.putExtra("type", "room");
            intent.putExtra("imgurl", getIntent().getStringExtra("imgurl"));
            intent.putExtra("id", getIntent().getStringExtra("id"));
            startActivity(intent);
        }
        if (v.getId() == R.id.delete) {
            new AlertDialog.Builder(this).setMessage("是否删除该房间?")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete(id);
                        }
                    }).create().show();
        }
        if (v.getId() == R.id.shebei) {
            Intent intent = new Intent(this, RoomDevGroupActivity.class);
            startActivity(intent);
        }
    }

    private void delete(String id) {
        RetrofitHelper.getApi().deleteRoom(id)
                .compose(this.<CreateModelResult>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CreateModelResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CreateModelResult result) {
                        if (result != null) {
                            UiUtils.showToast(result.msg);
                        } else {
                            UiUtils.showToast("删除失败");
                        }
                    }
                });
    }
}
