package net.suntrans.smarthome.activity.mh;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.Msg;
import net.suntrans.smarthome.bean.SceneChannelResult;
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

public class SenceDevGroupActivity extends BasedActivity {

    private MyAdapter adapter;
    private List<String> con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sence_dev_group);
        setUpToolbar();
        init();
    }

    private void init() {
        con = new ArrayList<>();
        con.add(getString(R.string.channel_choose_close));
        con.add(getString(R.string.channel_choose_open));
        datas = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new MyAdapter(R.layout.item_scene_channel, datas);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                OptionsPickerView pickerView1 = null;
                if (pickerView1 == null) {
                    pickerView1 = new OptionsPickerView.Builder(SenceDevGroupActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            setSceneChannel(position, options1);
                        }
                    })
                            .setTitleText(getString(R.string.choose_action))
                            .build();
                    pickerView1.setPicker(con);
                }
                pickerView1.show();
            }
        });
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
               new AlertDialog.Builder(SenceDevGroupActivity.this)
                       .setMessage(R.string.tips_delete_item)
                       .setPositiveButton(R.string.queding, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               deleteSenceChannel(position);
                           }
                       }).setNegativeButton(R.string.qvxiao,null).create().show();
                return true;
            }
        });
    }

    private void deleteSenceChannel(int position) {
        RetrofitHelper.getApi().deleteSceneChannel(getIntent().getStringExtra("id"),datas.get(position).channel_id)
                .compose(this.<Msg>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Msg>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UiUtils.showToast(e.getMessage());
                    }

                    @Override
                    public void onNext(Msg msg) {
                        UiUtils.showToast(msg.msg);
                        getData();
                    }
                });
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    private void setSceneChannel(int position, int options1) {
        RetrofitHelper.getApi().setChannel(getIntent().getStringExtra("id"),datas.get(position).channel_id, options1 + "")
                .compose(this.<Msg>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Msg>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UiUtils.showToast(e.getMessage());
                    }

                    @Override
                    public void onNext(Msg msg) {
                        UiUtils.showToast(msg.msg);
                        getData();
                    }
                });
    }


    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_dev_group);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_dev, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addDev) {
            Intent intent = new Intent(this, AddSenceDevGrpActivity.class);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class MyAdapter extends BaseQuickAdapter<SceneChannelResult.SceneChannel, BaseViewHolder> {


        public MyAdapter(@LayoutRes int layoutResId, @Nullable List<SceneChannelResult.SceneChannel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SceneChannelResult.SceneChannel item) {
            helper.setText(R.id.name, item.name)
                    .setText(R.id.action, item.status.equals("1") ? getString(R.string.channel_choose_open) : getString(R.string.channel_choose_close));
            ImageView imageView = helper.getView(R.id.img);
            Glide.with(SenceDevGroupActivity.this)
                    .load(item.img)
                    .crossFade()
                    .placeholder(R.drawable.icon_default)
                    .into(imageView);
            TextView state = helper.getView(R.id.action);
            if (item.status.equals("1"))
                state.setTextColor(getResources().getColor(R.color.colorPrimary));
            else
                state.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }


    public void commit(View v) {
    }


    private List<SceneChannelResult.SceneChannel> datas;

    private void getData() {
        RetrofitHelper.getApi().getSceneChannel(getIntent().getStringExtra("id"))
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
                    }
                });
    }
}
