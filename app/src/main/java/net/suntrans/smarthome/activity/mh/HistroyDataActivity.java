package net.suntrans.smarthome.activity.mh;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.trello.rxlifecycle.android.ActivityEvent;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.Ameter3;
import net.suntrans.smarthome.bean.Ammeter3HisEneity;
import net.suntrans.smarthome.bean.AmmeterHisEneity;
import net.suntrans.smarthome.utils.UiUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Looney on 2017/7/26.
 */

public class HistroyDataActivity extends BasedActivity implements OnChartValueSelectedListener {
    LineChart mChart;
    private String dataType;
    private String vtype;
    private String sno;
    private String shuoming;
    private TextView title;
    private String name;
    private List<Ameter3> datas1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histroy);
        name = getIntent().getStringExtra("name");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name + "历史记录");

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        vtype = getIntent().getStringExtra("vtype");
        title = (TextView) findViewById(R.id.title);
        dataType = getIntent().getStringExtra("data_type");
        sno = getIntent().getStringExtra("sno");
        if (dataType.equals("1")) {
            shuoming = "电压(V)";
        } else if (dataType.equals("2")) {
            shuoming = "电流(A)";

        } else if (dataType.equals("3")) {
            shuoming = "功率(W)";

        } else if (dataType.equals("4")) {
            shuoming = "功率因数";

        } else if (dataType.equals("5")) {
            shuoming = "用电量";
        } else {
            shuoming = getIntent().getStringExtra("shuoming");
        }

        title.setText(name + shuoming + "的历史数据曲线");
        toolbar.setTitle(name + shuoming + "的历史数据曲线");
        mChart = (LineChart) findViewById(R.id.chart);
        initChart();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initChart() {
//        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setNoDataText("暂无数据...");

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart
        // x-axis limit line
        LimitLine llXAxis = null;
        if (dataType.equals("1")) {
            llXAxis = new LimitLine(250f, "电压安全线");

        } else if (dataType.equals("2")) {
            llXAxis = new LimitLine(8f, "电流安全线");

        } else if (dataType.equals("3")) {
            llXAxis = new LimitLine(0.001f, "功率安全线");


        } else if (dataType.equals("4")) {
            llXAxis = new LimitLine(0.001f, "功率因数安全线");

        } else if (dataType.equals("5")) {
            llXAxis = new LimitLine(0.001f, "用电量红线");
        } else {
            llXAxis = new LimitLine(0.001f, "用电量红线");

        }

        llXAxis.setLineWidth(4f);
        llXAxis.setLineColor(Color.RED);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMinimum(0);
        xAxis.setDrawGridLines(true);
        xAxis.setGridLineWidth(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line


//        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

//        LimitLine ll1 = new LimitLine(150f, "Upper Limit");
//        ll1.setLineWidth(4f);
//        ll1.enableDashedLine(10f, 10f, 0f);
//        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//        ll1.setTextSize(10f);
//        ll1.setTypeface(tf);

//        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
//        ll2.setLineWidth(4f);
//        ll2.enableDashedLine(10f, 10f, 0f);
//        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        ll2.setTextSize(10f);
//        ll2.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.addLimitLine(ll1);
//        leftAxis.addLimitLine(ll2);
//        leftAxis.setAxisMaximum(200f);
//        leftAxis.setAxisMinimum(0f);
        //leftAxis.setYOffset(20f);
//        leftAxis.enableGridDashedLine(10f, 10f, 0f);
//        leftAxis.setDrawZeroLine(true);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(true);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
//        setData(30, 100);

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(2500);
        //mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);

        // // dont forget to refresh the drawing
        // mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        String s;
        if (vtype.equals("1"))
            s = "时间:" + (String) (e.getData()) + ",值" + h.getY() + datas.unit;
        else {
            s = "时间:" + (String) (e.getData()) + ",值" + h.getY();

        }
        UiUtils.showToastLong(s);
    }

    @Override
    public void onNothingSelected() {

    }

    private AmmeterHisEneity datas;

    private void setData(AmmeterHisEneity data) {
        List<Long> times = new ArrayList<>();
        title.setText(name + shuoming + "的历史数据");

        List<AmmeterHisEneity.HisItem> datas = data.lists;
        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < datas.size(); i++) {
            int x = i;
            float y = Float.parseFloat(datas.get(i).data);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = dateFormat.parse(datas.get(i).created_at);
                long time = date.getTime();
                times.add(time);
                Entry entry = new Entry(x, y);
                entry.setData(datas.get(i).created_at);
                values.add(entry);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        long max = Collections.max(times);
        long min = Collections.min(times);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = dateFormat.format(new Date(min));
        String end = dateFormat.format(new Date(max));

        title.setText(start + "~" + end + ",测量值:" + shuoming);


//
        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(datas.size());
        xAxis.setAxisMinimum(0);
        xAxis.setDrawGridLines(true);
        xAxis.setGridLineWidth(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, shuoming);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawValues(false);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setDrawCircles(false);

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_liner_chart);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.GRAY);
                set1.setMode(LineDataSet.Mode.LINEAR);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data1 = new LineData(dataSets);

            // set data
            mChart.setData(data1);
        }
    }

    private void setData3(Ammeter3HisEneity data) {
        List<Long> times = new ArrayList<>();
        title.setText(shuoming + "的历史数据");

        datas1 = new ArrayList<>();
        List<Map<String, String>> maps = data.lists;
        for (Map<String, String> map :
                maps) {
            Ameter3 am = new Ameter3();
            am.name = map.get("created_at");
            am.value = map.get("data");
            datas1.add(am);
        }


        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < datas1.size(); i++) {
            int x = i;
            float y = Float.parseFloat(datas1.get(i).value);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = dateFormat.parse(datas1.get(i).name);
                long time = date.getTime();
                times.add(time);
                Entry entry = new Entry(x, y);
                entry.setData(datas1.get(i).name);
                values.add(entry);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        long max = Collections.max(times);
        long min = Collections.min(times);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = dateFormat.format(new Date(min));
        String end = dateFormat.format(new Date(max));

        title.setText(start + "~" + end + ",测量值:" + shuoming);


//
        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(datas1.size());
        xAxis.setAxisMinimum(0);
        xAxis.setDrawGridLines(true);
        xAxis.setGridLineWidth(1f);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, shuoming);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawValues(false);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_liner_chart);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.GRAY);
                set1.setMode(LineDataSet.Mode.LINEAR);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data1 = new LineData(dataSets);

            // set data
            mChart.setData(data1);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData(sno, dataType);
    }

    private void getData(String sno, String dataType) {
        if (vtype.equals("1")) {
            RetrofitHelper.getApi().getAmmeterHistroy(sno, dataType)
                    .compose(this.<AmmeterHisEneity>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<AmmeterHisEneity>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(AmmeterHisEneity data) {
                            datas = data;
                            setData(data);
                        }
                    });
        } else {
            RetrofitHelper.getApi().getAmmeter3Histroy(sno, dataType)
                    .compose(this.<Ammeter3HisEneity>bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Ammeter3HisEneity>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            UiUtils.showToast(e.getMessage());
                        }

                        @Override
                        public void onNext(Ammeter3HisEneity data) {
                            setData3(data);
                        }
                    });
        }

    }
}
