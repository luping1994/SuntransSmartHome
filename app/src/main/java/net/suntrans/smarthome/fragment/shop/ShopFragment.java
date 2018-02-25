package net.suntrans.smarthome.fragment.shop;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.activity.perc.detail.SensusSettingActivity;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.bean.SensusResult;
import net.suntrans.smarthome.databinding.ActivitySensusdetailBinding;
import net.suntrans.smarthome.databinding.FragmentSensusdetailBinding;
import net.suntrans.smarthome.utils.LogUtil;
import net.suntrans.smarthome.utils.StatusBarCompat;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.iflytek.sunflower.config.a.l;

/**
 * Created by Looney on 2017/4/20.
 */

public class ShopFragment extends RxFragment {
    public static ShopFragment newInstance() {
        return new ShopFragment();
    }

    private String devId;
    private FragmentSensusdetailBinding binding;
    private String name;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private int Pwidth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sensusdetail, container, false);
//        devId = getIntent().getStringExtra("dev_id");
//        name = getIntent().getStringExtra("name");
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle("环境");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);//获取屏幕大小的信息

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statebarHeight = StatusBarCompat.getStatusBarHeight(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statebarHeight);
            binding.statusBarFix.setLayoutParams(layoutParams);

        }
        Pwidth = displayMetrics.widthPixels;   //屏幕宽度,先锋的宽度是800px，小米2a的宽度是720px
        binding.refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        initView(null);
    }

    private void initView(SensusResult.Sensus data) {
        if (data != null) {
            binding.time.setText(data.getCreated_at());
        }
        for (int i = 0; i < binding.rootLL.getChildCount(); i++) {
            if (i == 0 || i == 1 || i == 7 || i == 13)
                continue;
            initView(i, data);
        }

    }

    private void initView(int position, SensusResult.Sensus data) {


        TextView nameTx = (TextView) binding.rootLL.getChildAt(position).findViewById(R.id.name);
        TextView evaluateTx = (TextView) binding.rootLL.getChildAt(position).findViewById(R.id.evaluate);
        TextView valueTx = (TextView) binding.rootLL.getChildAt(position).findViewById(R.id.value);
        LinearLayout layout_arrow = (LinearLayout) binding.rootLL.getChildAt(position).findViewById(R.id.layout_arrow);
        ImageView standard = (ImageView) binding.rootLL.getChildAt(position).findViewById(R.id.standard);   //等级划分条
        ImageView arrow = (ImageView) binding.rootLL.getChildAt(position).findViewById(R.id.arrow);   //箭头

        switch (position) {
            case 2:
                nameTx.setText("温度");
                standard.setImageResource(R.drawable.standard_tem);
                if (data != null) {
                    valueTx.setText(data.getWendu() + "℃");
                    evaluateTx.setText(data.wenduEva);
                    LogUtil.i("SensusDetaiActivity", data.wenduPro + "," + Pwidth * data.wenduPro / 200);
                    setPading(data.wenduPro, layout_arrow, valueTx);
                }
                break;
            case 3:
                standard.setImageResource(R.drawable.standard_humidity);
                nameTx.setText("湿度");
                if (data != null) {
                    valueTx.setText(data.getShidu() + "%RH");
                    evaluateTx.setText(data.shiduEva);
                    setPading(data.shiduPro, layout_arrow, valueTx);
                }
                break;
            case 4:
                nameTx.setText("大气压");
                standard.setImageResource(R.drawable.standard_humidity);
                if (data != null) {
                    valueTx.setText(data.getDaqiya() + "kPa");
                    evaluateTx.setText(data.daqiYaEva);
                    setPading(data.daqiyaPro, layout_arrow, valueTx);
                }
                break;
            case 5:
                nameTx.setText("人员信息");
                valueTx.setVisibility(View.GONE);
                layout_arrow.setVisibility(View.INVISIBLE);
                if (data != null) {
                    evaluateTx.setText(data.getRenyuan().equals("1") ? "有人" : "没人");
                }
                break;
            case 6:
                nameTx.setText("光照强度");
                standard.setImageResource(R.drawable.standard_light);
                if (data != null) {
                    valueTx.setText(data.getGuangzhao() + "");
                    evaluateTx.setText(data.guanzhaoEva);
                    setPading(data.guanzhaoPro, layout_arrow, valueTx);
                }
                break;
            case 8:
                nameTx.setText("烟雾");
                standard.setImageResource(R.drawable.standard_smoke);
                if (data != null) {
                    valueTx.setText(data.getYanwu() + "ppm");
                    evaluateTx.setText(data.yanwuEva);
                    setPading(data.yanwuPro, layout_arrow, valueTx);
                }
                break;
            case 9:
                nameTx.setText("甲醛");
                standard.setImageResource(R.drawable.standard_smoke);
                if (data != null) {
                    valueTx.setText(data.getJiaquan() + "ppm");
                    evaluateTx.setText(data.jiaquanEva);
                    setPading(data.jiaquanPro, layout_arrow, valueTx);
                }
                break;
            case 10:
                nameTx.setText("PM1");
                standard.setImageResource(R.drawable.bg_standard);
                if (data != null) {
                    valueTx.setText(data.getPm1() + "ppm");
                    evaluateTx.setText(data.pm1Eva);
                    setPading(data.pm1Pro, layout_arrow, valueTx);
                }
                break;
            case 11:
                nameTx.setText("PM2.5");
                standard.setImageResource(R.drawable.bg_standard);
                if (data != null) {
                    valueTx.setText(data.getPm25() + "ppm");
                    evaluateTx.setText(data.pm25Eva);
                    setPading(data.pm25Pro, layout_arrow, valueTx);
                }
                break;
            case 12:
                nameTx.setText("PM10");
                standard.setImageResource(R.drawable.bg_standard);
                if (data != null) {
                    valueTx.setText(data.getPm10() + "ppm");
                    evaluateTx.setText(data.pm10Eva);
                    setPading(data.pm10Pro, layout_arrow, valueTx);
                }
                break;
            case 14:
                nameTx.setText("X轴角度");
                valueTx.setVisibility(View.GONE);
                layout_arrow.setVisibility(View.INVISIBLE);
                if (data != null) {
                    evaluateTx.setText(data.xEva);
                }
                break;
            case 15:
                valueTx.setVisibility(View.GONE);
                layout_arrow.setVisibility(View.INVISIBLE);
                nameTx.setText("Y轴角度");
                if (data != null) {
                    evaluateTx.setText(data.yEva);
                }
                break;
            case 16:
                valueTx.setVisibility(View.GONE);
                layout_arrow.setVisibility(View.INVISIBLE);
                nameTx.setText("水平夹角");
                if (data != null) {
                    evaluateTx.setText(data.zEva);
                }
                break;
            case 17:
                valueTx.setVisibility(View.GONE);
                layout_arrow.setVisibility(View.INVISIBLE);
                nameTx.setText("振动强度");
                if (data != null) {
                    evaluateTx.setText(data.getZhendong() + "G");
                }
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();

    }

    private void setPading(int progress, LinearLayout layout_arrow, TextView value) {
        value.setVisibility(View.VISIBLE);
        layout_arrow.setVisibility(View.VISIBLE);
        layout_arrow.setPadding(Pwidth * progress / 200, 0, 0, 0);
        if (progress < 50) {
            value.setGravity(Gravity.LEFT);
            value.setPadding(Pwidth * progress / 200, 0, 0, 0);   //设置左边距
        } else {
            value.setGravity(Gravity.RIGHT);
//            System.out.println(Pwidth);
            value.setPadding(0, 0, Pwidth * (90 - progress) / 200, 0);  //设置右边距
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_setting, menu);

    }

    private void getData() {
        RetrofitHelper.getApi().getHomeEnvironment()
                .compose(this.<SensusResult>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
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
                                    SensusResult.Sensus sensus = sensusResult.result.row;
                                    devId = sensus.getDev_id();
                                    sensus.setEva();
                                    initView(sensus);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            Intent intent = new Intent(getActivity(), SensusSettingActivity.class);
            intent.putExtra("id", devId);
//            intent.putExtra("addr",)
            startActivity(intent);
        }
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
