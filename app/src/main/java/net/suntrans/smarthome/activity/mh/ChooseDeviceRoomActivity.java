package net.suntrans.smarthome.activity.mh;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import net.suntrans.smarthome.bean.HomeRoomResult;
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

public class ChooseDeviceRoomActivity extends BasedActivity {

    private int mSelectPostion = -1;
    private List<HomeRoomResult.Room> datas = new ArrayList<>();
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
                for (int i = 0; i < datas.size(); i++) {
                    if (position == i)
                        datas.get(i).isChecked = true;
                    else
                        datas.get(i).isChecked = false;


                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_choose_room);
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
            if (mSelectPostion == -1)
                UiUtils.showToast(getString(R.string.tips_no_room_selected));
            else
                UiUtils.showToast("已选择" + datas.get(mSelectPostion).name + "。保存成功!");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        getRoomData();
        super.onResume();
    }

    class MyAdapter extends BaseQuickAdapter<HomeRoomResult.Room, BaseViewHolder> {


        public MyAdapter(@LayoutRes int layoutResId, @Nullable List<HomeRoomResult.Room> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, HomeRoomResult.Room item) {
            CheckBox checkBox = helper.getView(R.id.checkbox);
            helper.setText(R.id.name, item.name);
            checkBox.setChecked(item.isChecked);
        }
    }


    private void getRoomData() {
        RetrofitHelper.getApi().getHomeHouse()
                .compose(this.<HomeRoomResult>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HomeRoomResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(HomeRoomResult homeSceneResult) {
                        LogUtil.i("房间获取成功！");
                        if (homeSceneResult != null) {
                            if (homeSceneResult.status.equals("1")) {
                                datas.clear();
                                datas.addAll(homeSceneResult.result.rows);
                                LogUtil.i("图片url为：" + homeSceneResult.result.rows.get(0).img);
                                adapter.notifyDataSetChanged();


                            } else {

                            }
                        }
                    }
                });
    }
}
