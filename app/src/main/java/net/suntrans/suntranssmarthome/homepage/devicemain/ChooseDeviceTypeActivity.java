package net.suntrans.suntranssmarthome.homepage.devicemain;

import android.content.Intent;
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

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.bean.DeviceType;
import net.suntrans.suntranssmarthome.homepage.myhome.detail.ModifyINActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Looney on 2017/5/23.
 */

public class ChooseDeviceTypeActivity extends BasedActivity {

    private List<DeviceType> datas = new ArrayList<>();
    private final String[] s = new String[]{"插座", "灯光", "其它"};
    private int mSelectPostion = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);
        setUpToolbar();
        init();
    }

    private void init() {

        for (int i = 0; i < 3; i++) {
            DeviceType deviceType = new DeviceType();
            deviceType.setName(s[i]);
            deviceType.setChecked(false);
            datas.add(deviceType);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        MyAdapter adapter = new MyAdapter(R.layout.item_type, datas);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mSelectPostion = position;
                for (int i = 0; i < datas.size(); i++) {
                    if (position == i)
                        datas.get(i).setChecked(true);
                    else
                        datas.get(i).setChecked(false);

                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("选择类型");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        AppBarLayout
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nextStep) {
            Intent intent = new Intent(this, ModifyINActivity.class);
            intent.putExtra("name","Name");
            intent.putExtra("imgurl","http://q1.suntrans.net:8011/sc_product/Uploads/App/2017-05-19/591eb1c19c01c.jpg");
            intent.putExtra("type","devices");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends BaseQuickAdapter<DeviceType, BaseViewHolder> {


        public MyAdapter(@LayoutRes int layoutResId, @Nullable List<DeviceType> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, DeviceType item) {
            CheckBox checkBox = helper.getView(R.id.checkbox);
            helper.setText(R.id.name,item.getName());
            checkBox.setChecked(item.ischecked());
        }
    }


}
