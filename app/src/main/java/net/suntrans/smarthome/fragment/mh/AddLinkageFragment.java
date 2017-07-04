package net.suntrans.smarthome.fragment.mh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.activity.mh.ChooseLinkageConActivity;
import net.suntrans.smarthome.activity.mh.ChooseLinkageResultActivity;
import net.suntrans.smarthome.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Looney on 2017/5/20.
 */

public class AddLinkageFragment extends Fragment implements View.OnClickListener {

    private LinearLayout conditionRoot;
    private LinearLayout conditionResult;
    private OptionsPickerView addResultPickerView;
    private OptionsPickerView addConPickerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_link, container, false);
        conditionRoot = (LinearLayout) view.findViewById(R.id.conditionRoot);
        conditionResult = (LinearLayout) view.findViewById(R.id.conditionResult);
        initData();
        return view;
    }
    private List<String> con;
    private List<String> res;
    private void initData() {
        con = new ArrayList<>();
        res = new ArrayList<>();
        con.add("打开");
        res.add("污染");
        res.add("清洁");
        con.add("关闭");

        /*   模拟数据  */
//        addConName.add("时间");
//        addConName.add("PM25");
//
//        addResultName.add("电脑");
//        addResultName.add("电视");
//
//        List<String> res = new ArrayList<>();
//        res.add("打开");
//        res.add("关闭");
//        List<String> res2 = new ArrayList<>();
//        res2.add("打开");
//        res2.add("关闭");
//
//        List<String> time_1 = new ArrayList<>();
//        time_1.add("9:00~10:00");
//        time_1.add("12:00~13:00");
//        time_1.add("13:00~14:00");
//
//
//        List<String> pm252 = new ArrayList<>();
//        pm252.add("清新");
//        pm252.add("一般");
//        pm252.add("污染");
//
//
//        addConValue.add(time_1);
//        addConValue.add(pm252);
//
//        addResultValue.add(res);
//        addResultValue.add(res2);
        /*模拟数据 */
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.addCon).setOnClickListener(this);
        view.findViewById(R.id.addResult).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCon:
                startActivityForResult(new Intent(getActivity(), ChooseLinkageConActivity.class),100);
                break;
            case R.id.addResult:
                startActivityForResult(new Intent(getActivity(), ChooseLinkageResultActivity.class),102);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==101&&requestCode==100){
            String[] datas = data.getStringArrayExtra("data");
            for (int i=0;i<datas.length;i++){
                String s="";
                if (datas[i].equals("时间"))
                    s = "9:00~12:00";
                else
                    s="污染";
                View view  = getAddConUI(datas[i],"变为",s);
                conditionRoot.addView(view);
            }
        }
        if (resultCode == 103 && requestCode == 102) {
            System.out.println("我被执行了");
            String[] datas = data.getStringArrayExtra("data");
            for (int i = 0; i < datas.length; i++) {
                String s = "";
                if (datas[i].equals("时间"))
                    s = "9:00~12:00";
                else
                    s = "污染";
                View view  = getAddResultUI(datas[i],"","打开");
                conditionResult.addView(view);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    List<String> addConName = new ArrayList<>();
    List<List<String>> addConValue = new ArrayList<>();


    List<String> addResultName = new ArrayList<>();
    List<List<String>> addResultValue = new ArrayList<>();

    private void showAddConDialog() {
        if (addConPickerView==null){
            addConPickerView = new OptionsPickerView.Builder(getContext(), new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    LogUtil.i("options1=" + options1 + ",options2=" + options2 + ",options3=" + options3);
                    View view  = getAddConUI(addConName.get(options1),"变为",addConValue.get(options1).get(options2));
                    conditionRoot.addView(view);
                }
            })
                    .setTitleText("选择联动条件")
                    .build();
            addConPickerView.setPicker(addConName,addConValue);
        }

        addConPickerView.show();
    }

    private void showAddResultDialog() {
        if (addResultPickerView ==null){
            addResultPickerView = new OptionsPickerView.Builder(getContext(), new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(final int options1, final int options2, int options3, View v) {
                    LogUtil.i("options1=" + options1 + ",options2=" + options2 + ",options3=" + options3);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            View view  = getAddResultUI(addResultName.get(options1),"立即",addResultValue.get(options1).get(options2));
                            conditionResult.addView(view);
                        }
                    });
                }
            })
                    .setTitleText("选择联动结果")
                    .build();
            addResultPickerView.setPicker(addResultName, addResultValue);
        }

        addResultPickerView.show();
    }

    Handler handler = new Handler();

    private View getAddConUI(String conName,String conAction,String conValue){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_link_con,null,false);
        TextView name = (TextView) view.findViewById(R.id.link_itemCon_name);
        TextView action = (TextView) view.findViewById(R.id.link_itemCon_action);
        final TextView value = (TextView) view.findViewById(R.id.link_itemCon_value);
        name.setText(conName);
        action.setText(conAction);
        value.setText(conValue);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsPickerView pickerView1 = null;
                if (pickerView1 == null) {
                    pickerView1 = new OptionsPickerView.Builder(getActivity(), new OptionsPickerView.OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            value.setText(res.get(options1));
                        }
                    })
                            .setTitleText("选择动作")
                            .build();
                    pickerView1.setPicker(res);
                }
                pickerView1.show();
            }
        });

        return view;
    }

    private View getAddResultUI(String resName, String resAction, String resValue) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_link_con, null, false);
        TextView name = (TextView) view.findViewById(R.id.link_itemCon_name);
        TextView action = (TextView) view.findViewById(R.id.link_itemCon_action);
        final TextView value = (TextView) view.findViewById(R.id.link_itemCon_value);
        name.setText(resName);
        action.setText(resAction);
        value.setText(resValue);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsPickerView pickerView1 = null;
                if (pickerView1 == null) {
                    pickerView1 = new OptionsPickerView.Builder(getActivity(), new OptionsPickerView.OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3, View v) {
                            value.setText(con.get(options1));
                        }
                    })
                            .setTitleText("选择动作")
                            .build();
                    pickerView1.setPicker(con);
                }
                pickerView1.show();
            }
        });
        return view;
    }


    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
