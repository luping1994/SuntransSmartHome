package net.suntrans.suntranssmarthome.homepage.device.sensus;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.trello.rxlifecycle.android.ActivityEvent;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.databinding.ActivitySensusdetailBinding;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/4/24.
 */

public class SensusDetailActivity extends BasedActivity {

    private String devId;
    private ActivitySensusdetailBinding binding;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sensusdetail);
        devId = getIntent().getStringExtra("dev_id");
        name = getIntent().getStringExtra("name");
        binding.title.setText(name);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                handler.sendEmptyMessageDelayed(MSG_FAILED_DALAYED, 2000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        RetrofitHelper.getApi().getSensueDetail(devId)
                .compose(this.<SensusResult>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SensusResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.refreshlayout.setRefreshing(false);
                            }
                        });
                    }

                    @Override
                    public void onNext(SensusResult sensusResult) {
                        if (sensusResult.status.equals("1")) {
                            if (sensusResult.result != null) {
                                if (sensusResult.result.row != null) {
//                                    binding.setInfo(sensusResult.result.row);
                                    binding.setVariable(com.android.databinding.library.baseAdapters.BR.info, sensusResult.result.row);
                                    binding.executePendingBindings();
                                }
                            }
                        }
                        handler.removeMessages(MSG_FAILED_DALAYED);
                        binding.refreshlayout.setRefreshing(false);

                    }
                });
    }

    private static final int MSG_FAILED_DALAYED = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_FAILED_DALAYED) {
                binding.refreshlayout.setRefreshing(false);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.setting){
            Intent intent =new Intent(this,SensusSettingActivity.class);
            intent.putExtra("id",devId);
            startActivity(intent);
        }
        if (item.getItemId()==android.R.id.home){
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
