package net.suntrans.smarthome.activity.room;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle.android.ActivityEvent;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.AllDeviceResult;
import net.suntrans.smarthome.bean.UnbindDev;
import net.suntrans.smarthome.utils.LogUtil;
import net.suntrans.smarthome.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/5/23.
 */

public class AddRoomDevGrpActivity extends BasedActivity {

    private final String TAG = "AddRoomDevGrpActivity";
    private int mSelectPostion = -1;
    private List<UnbindDev> datas = new ArrayList<>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);
        setUpToolbar();
        init();
    }

    private void init() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new MyAdapter(R.layout.item_choose_room, datas);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mSelectPostion = position;
                datas.get(position).isChecked = (!datas.get(position).isChecked);
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("选择未分类设备");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        AppBarLayout
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lqueding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.queding) {
            new AlertDialog.Builder(this)
                    .setMessage("是否添加以下设备")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addChannel2Room();
                        }
                    }).setNegativeButton("取消", null).create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addChannel2Room() {
        String s = "";
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).isChecked) {
                s += datas.get(i).channel_id + ",";
            }
        }
        if (TextUtils.isEmpty(s)) {
            UiUtils.showToast("请选择设备!");
            return;
        }

        LogUtil.i(TAG, "添加的设备id=" + s);
        s = s.substring(0, s.length() - 1);
        RetrofitHelper.getApi().bindChannel2Room(getIntent().getStringExtra("id"), s)
                .compose(this.<List<AllDeviceResult.ChannelInfo>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<AllDeviceResult.ChannelInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UiUtils.showToast("添加失败");
                    }

                    @Override
                    public void onNext(List<AllDeviceResult.ChannelInfo> data) {
                        if (data != null) {
                            if (data.size() != 0) {
                                new AlertDialog.Builder(AddRoomDevGrpActivity.this)
                                        .setMessage("添加成功")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        }).setCancelable(false)
                                        .create().show();
                                return;
                            }
                        }

                        UiUtils.showToast("添加失败");
                    }
                });

    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    class MyAdapter extends BaseQuickAdapter<UnbindDev, BaseViewHolder> {


        public MyAdapter(@LayoutRes int layoutResId, @Nullable List<UnbindDev> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, UnbindDev item) {
            CheckBox checkBox = helper.getView(R.id.checkbox);
            helper.setText(R.id.name, item.name);
            checkBox.setChecked(item.isChecked);
        }
    }


    private void getData() {
        RetrofitHelper.getApi().getUnbindChannel()
                .compose(this.<List<UnbindDev>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<UnbindDev>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(List<UnbindDev> results) {
                        datas.clear();
                        datas.addAll(results);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
