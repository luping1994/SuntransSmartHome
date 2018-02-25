package net.suntrans.smarthome.activity.mh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.AmeterDetailEntity;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.widget.ScrollChildSwipeRefreshLayout;


import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/7/26.
 */

public class JianceDetailActivity extends BasedActivity implements View.OnClickListener {
    private String sno;
    private String id;
    private String vtype;
    private TextView time;
    private TextView dianya;
    private TextView dianliu;
    private TextView gonglv;
    private TextView yinsu;
    private TextView power;
    private TextView title;
    private ScrollChildSwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiance_detail);
        id = getIntent().getStringExtra("id");
        sno = getIntent().getStringExtra("sno");
        vtype = getIntent().getStringExtra("vtype");

        refreshLayout = (ScrollChildSwipeRefreshLayout) findViewById(R.id.refreshlayout);
        title = (TextView) findViewById(R.id.title);
        title.setText(getIntent().getStringExtra("name"));

        time = (TextView) findViewById(R.id.time);

        dianya = (TextView) findViewById(R.id.dianya);
        dianliu = (TextView) findViewById(R.id.dianliu);
        gonglv = (TextView) findViewById(R.id.gonglv);
        yinsu = (TextView) findViewById(R.id.yinsu);
        power = (TextView) findViewById(R.id.power);

        findViewById(R.id.dianyall).setOnClickListener(this);
        findViewById(R.id.dianliull).setOnClickListener(this);
        findViewById(R.id.gonglvll).setOnClickListener(this);
        findViewById(R.id.yinsull).setOnClickListener(this);
        findViewById(R.id.powerll).setOnClickListener(this);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        findViewById(R.id.fanhui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData(){
        RetrofitHelper.getApi().getAmmeterDetail(vtype,sno)
                .compose(this.<AmeterDetailEntity>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AmeterDetailEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (refreshLayout!=null){
                            refreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onNext(AmeterDetailEntity data) {
                        datas = data;
                        if (refreshLayout!=null){
                            refreshLayout.setRefreshing(false);
                        }
                        time.setText(data.result.updated_at);
                        dianya.setText(data.result.U+"V");
                        dianliu.setText(data.result.I+"A");
                        gonglv.setText(data.result.Power+"kw");
                        yinsu.setText(data.result.PowerRate+"");
                        power.setText(data.result.EletricityValue+"kwh");
                    }
                });
    }
    AmeterDetailEntity datas;
    @Override
    public void onClick(View v) {
        if (datas==null){
            UiUtils.showToast("无法获取当前值");
            return;
        }
        if (datas.result==null){
            UiUtils.showToast("无法获取当前值");
            return;
        }
        if (datas.result.sno==null){
            UiUtils.showToast("无法获取表号");
            return;
        }
        if (datas.result.vtype == null) {
            UiUtils.showToast("无法获取电表类型");
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this,HistroyDataActivity.class);
        intent.putExtra("name",getIntent().getStringExtra("name"));
        intent.putExtra("sno",datas.result.sno);
        intent.putExtra("vtype",datas.result.vtype);
        switch (v.getId()){
            case R.id.dianyall:
                intent.putExtra("data_type","1");
                break;
            case R.id.dianliull:
                intent.putExtra("data_type","2");

                break;
            case R.id.gonglvll:
                intent.putExtra("data_type","3");

                break;
            case R.id.yinsull:
                intent.putExtra("data_type","4");

                break;
            case R.id.powerll:
                intent.putExtra("data_type","5");

                break;
        }

        startActivity(intent);

    }
}
