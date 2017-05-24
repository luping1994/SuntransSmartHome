package net.suntrans.suntranssmarthome.homepage.myhome.edit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.bean.HomeRoomResult;
import net.suntrans.suntranssmarthome.model.MainDevice;
import net.suntrans.suntranssmarthome.model.PatternItem;
import net.suntrans.suntranssmarthome.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/5/23.
 */

public class AddSenceDevGrpActivity extends BasedActivity {

    private AddSenceDevGrpAdapter adapter;
    private ExpandableListView expandableListView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sence_dev_item);
        setUpToolbar();
        init();
    }

    private void init() {
        expandableListView = (ExpandableListView) findViewById(R.id.ExpandableListView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("我被点击了");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        getRoomData();

    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_add_dev_group);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lqueding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.queding) {

        }
        return super.onOptionsItemSelected(item);
    }


    private OptionsPickerView pickerView;

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
                    public void onNext(HomeRoomResult result) {
                        progressBar.setVisibility(View.GONE);

                        LogUtil.i("房间获取成功！");
                        if (result != null) {
                            if (result.status.equals("1")) {
                                List<PatternItem> datas = new ArrayList<PatternItem>();

                                List<HomeRoomResult.Room> rows = result.result.rows;
                                for (int i = 0; i < rows.size(); i++) {
                                    PatternItem patternItem = new PatternItem();
                                    patternItem.setName(rows.get(i).name);
                                    List<MainDevice> devices = new ArrayList<MainDevice>();
                                    for (int j =0;j<3;j++){
                                        MainDevice device = new MainDevice();
                                        device.setName(strings[j]);
                                        devices.add(device);
                                    }
                                    patternItem.setDevices(devices);
                                    datas.add(patternItem);
                                }
                                adapter = new AddSenceDevGrpAdapter(datas,AddSenceDevGrpActivity.this);
                                expandableListView.setAdapter(adapter);
                                progressBar.setVisibility(View.INVISIBLE);
                            } else {

                            }
                        }
                    }
                });
    }

    private final String[] strings = new String[]{"插座","吊灯","长行灯"};
}
