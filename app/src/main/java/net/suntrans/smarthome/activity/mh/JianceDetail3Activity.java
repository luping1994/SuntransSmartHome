package net.suntrans.smarthome.activity.mh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.Ameter3;
import net.suntrans.smarthome.bean.AmeterDetailEntity3;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.widget.OffsetDecoration;
import net.suntrans.smarthome.widget.ScrollChildSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/7/26.
 */

public class JianceDetail3Activity extends BasedActivity {
    private String sno;
    private String id;
    private String vtype;
    private TextView title;
    private ScrollChildSwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private Map<String, String> dictionary = null;
    private Myadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiance_detail3);
        initData();
        id = getIntent().getStringExtra("id");
        sno = getIntent().getStringExtra("sno");
        vtype = getIntent().getStringExtra("vtype");
        title = (TextView) findViewById(R.id.title);
        title.setText(getIntent().getStringExtra("name"));
        refreshLayout = (ScrollChildSwipeRefreshLayout) findViewById(R.id.refreshlayout);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new Myadapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new OffsetDecoration(UiUtils.dip2px(2)));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this.getApplicationContext(),DividerItemDecoration.VERTICAL));
        findViewById(R.id.fanhui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        dictionary = new HashMap<>();
        dictionary.put("VolA", "A相电压");
        dictionary.put("VolAU", "V");
        dictionary.put("updated_at", "更新时间");
        dictionary.put("updated_atU", "");
        dictionary.put("VolB", "B相电压");
        dictionary.put("VolBU", "V");
        dictionary.put("VolC", "C相电压");
        dictionary.put("VolCU", "V");
        dictionary.put("IA", "A相电流");
        dictionary.put("IAU", "A");
        dictionary.put("IB", "B相电流");
        dictionary.put("IBU", "A");
        dictionary.put("IC", "C相电流");
        dictionary.put("ICU", "A");
        dictionary.put("ActivePower", "有功功率");
        dictionary.put("ActivePowerU", "W");
        dictionary.put("ActivePowerA", "A相有功功率");
        dictionary.put("ActivePowerAU", "W");
        dictionary.put("ActivePowerB", "B相有功功率");
        dictionary.put("ActivePowerBU", "W");
        dictionary.put("ActivePowerC", "C相有功功率");
        dictionary.put("ActivePowerCU", "W");
        dictionary.put("ReactivePower", "无功功率");
        dictionary.put("ReactivePowerU", "W");
        dictionary.put("ReactivePowerA", "A相无功功率");
        dictionary.put("ReactivePowerAU", "W");
        dictionary.put("ReactivePowerB", "B相无功功率");
        dictionary.put("ReactivePowerBU", "W");
        dictionary.put("ReactivePowerC", "C相无功功率");
        dictionary.put("ReactivePowerCU", "W");
        dictionary.put("PowerFactor", "功率因素");
        dictionary.put("PowerFactorU", "");
        dictionary.put("PowerFactorA", "A相功率因素");
        dictionary.put("PowerFactorAU", "");
        dictionary.put("PowerFactorB", "B相功率因素");
        dictionary.put("PowerFactorBU", "");
        dictionary.put("PowerFactorC", "C相功率因素");
        dictionary.put("PowerFactorCU", "");
        dictionary.put("EletricityValue", "用电量");
        dictionary.put("EletricityValueU", "度");
        dictionary.put("EletricitySharp", "尖");
        dictionary.put("EletricitySharpU", "");
        dictionary.put("EletricityPeak", "峰");
        dictionary.put("EletricityPeakU", "");
        dictionary.put("EletricityFlat", "平");
        dictionary.put("EletricityFlatU", "");
        dictionary.put("EletricityValley", "谷");
        dictionary.put("EletricityValleyU", "");
        dictionary.put("Temp", "温度");
        dictionary.put("TempU", "℃");
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        RetrofitHelper.getApi().getAmmeter3Detail(vtype, sno)
                .compose(this.<AmeterDetailEntity3>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AmeterDetailEntity3>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (refreshLayout != null) {
                            refreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onNext(AmeterDetailEntity3 data) {
                        if (refreshLayout != null) {
                            refreshLayout.setRefreshing(false);
                        }
                        Map<String, String> map = data.data;
                        datas.clear();
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            if (entry.getKey().equals("id")||entry.getKey() .equals("sno")
                                    ||entry.getKey() .equals("dev_id")||entry.getKey() .equals("user_id"))
                            {
                                continue;
                            }
                            Ameter3 ameter3 = new Ameter3(entry.getKey(),entry.getValue(),dictionary.get(entry.getKey()));
                            ameter3.unit = dictionary.get(entry.getKey()+"U");
                            datas.add(ameter3);
                        }
                        adapter.notifyDataSetChanged();

                    }
                });
    }


    private List<Ameter3> datas = new ArrayList<>();
    class Myadapter extends RecyclerView.Adapter<Myadapter.ViewHolder> {
        private Activity mActivity;

        public Myadapter(Activity mActivity) {
            this.mActivity = mActivity;
        }

        @Override

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity.getApplicationContext()).inflate(R.layout.item_ameter3, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView name;
            private TextView value;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                value = (TextView) itemView.findViewById(R.id.value);
                itemView.findViewById(R.id.dianliull).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getAdapterPosition()==0)
                            return;
                        Intent intent = new Intent(mActivity, HistroyDataActivity.class);
                        intent.putExtra("sno", sno);
                        intent.putExtra("vtype", "3");
                        intent.putExtra("name",getIntent().getStringExtra("name") );
                        intent.putExtra("data_type", datas.get(getAdapterPosition()).name);
                        intent.putExtra("shuoming", dictionary.get(datas.get(getAdapterPosition()).name));
                        startActivity(intent);
                    }
                });
            }

            public void setData(int position) {
                name.setText(datas.get(position).nameCH);
                value.setText(datas.get(position).value+datas.get(position).unit);
            }
        }
    }


}


