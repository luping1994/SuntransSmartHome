package net.suntrans.smarthome.activity.mh;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.bigkoo.pickerview.OptionsPickerView;
import com.trello.rxlifecycle.android.ActivityEvent;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.AddSCResult;
import net.suntrans.smarthome.adapter.AddSenceDevGrpAdapter;
import net.suntrans.smarthome.bean.PatternItem;
import net.suntrans.smarthome.bean.RAC;
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

public class AddSenceDevGrpActivity extends BasedActivity {

    private AddSenceDevGrpAdapter adapter;
    private ExpandableListView expandableListView;
    private ProgressBar progressBar;
    private List<PatternItem> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sence_dev_item);
        setUpToolbar();
        init();
    }

    private void init() {
        datas = new ArrayList<>();
        expandableListView = (ExpandableListView) findViewById(R.id.ExpandableListView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        expandableListView.setGroupIndicator(null);
        expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                System.out.println("我被点击了");
            }
        });

        adapter = new AddSenceDevGrpAdapter(datas, AddSenceDevGrpActivity.this);
        expandableListView.setAdapter(adapter);
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
            String s = "";
            for (PatternItem items : datas) {
                for (SceneChannelResult.SceneChannel channel : items.channel) {
                    if (channel.isChecked()) {
                        s += channel.id + ",";
                    }
                }
            }
            if (TextUtils.isEmpty(s)) {
                UiUtils.showToast(getString(R.string.tips_choose_one_at_least));
                return true;
            }
            s = s.substring(0, s.length() - 1);

            final String finalS = s;
            new AlertDialog.Builder(this)
                    .setMessage(String.format(getString(R.string.tips_add_or_nor),s.split(",").length))
                    .setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addSenceChannel(finalS);
                        }
                    }).setNegativeButton(getString(R.string.qvxiao),null)
                    .create().show();
            return true;
        }
        if (item.getItemId() == android.R.id.home){
            supportFinishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addSenceChannel(String s) {
        LogUtil.i(getIntent().getStringExtra("id"));
        RetrofitHelper.getApi().addSceneChannel(getIntent().getStringExtra("id"), s)
                .compose(this.<List<AddSCResult>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<AddSCResult>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UiUtils.showToast(getString(R.string.add_failed));
                    }

                    @Override
                    public void onNext(List<AddSCResult> data) {
                        if (data != null) {
                            if (data.size() != 0) {
                                int count = 0;
                                for (AddSCResult result : data) {
                                    System.out.println(result.result);
                                    if (result.result.equals("成功"))
                                        count++;
                                }
                                new AlertDialog.Builder(AddSenceDevGrpActivity.this)
                                        .setMessage(String.format(getString(R.string.tips_add_count_success),count))
                                        .setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        }).setCancelable(false)
                                        .create().show();
                                return;
                            }
                        }

                        UiUtils.showToast(getString(R.string.add_failed));
                    }
                });
    }


    private OptionsPickerView pickerView;

    private void getRoomData() {
        RetrofitHelper.getApi().getChannelByHouse()
                .compose(this.<RAC>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RAC>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(RAC result) {
                        progressBar.setVisibility(View.GONE);
                        if (result != null) {
                            if (result.house.status.equals("1")) {
                                datas.clear();
                                datas.addAll(result.house.result);
//                                System.out.println("" + datas.size());
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.INVISIBLE);
                            } else {

                            }
                        }
                    }
                });
    }


}
