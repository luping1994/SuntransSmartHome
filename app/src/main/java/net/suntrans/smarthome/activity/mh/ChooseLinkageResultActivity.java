package net.suntrans.smarthome.activity.mh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.MainDevice;
import net.suntrans.smarthome.bean.PatternItem2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Looney on 2017/5/24.
 */

public class ChooseLinkageResultActivity extends BasedActivity {
    private List<PatternItem2> datas;
    private List<Map<String, String>> datas1;
    ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_linkage_result);
        setUpToolbar();
        init();
    }

    private void init() {
        datas = new ArrayList<>();
        PatternItem2 item = new PatternItem2();

        item.setName("系统消息");
        MainDevice mainDevice = new MainDevice();
        mainDevice.setName("APP");
        MainDevice mainDevice1 = new MainDevice();
        mainDevice1.setName("Email");
        List<MainDevice> devices = new ArrayList<>();
        devices.add(mainDevice);
        devices.add(mainDevice1);

        item.setDevices(devices);


        PatternItem2 item2 = new PatternItem2();

        item2.setName(getString(R.string.all_scene));

        MainDevice mainDevice2 = new MainDevice();
        mainDevice2.setName("回家");
        MainDevice mainDevice3 = new MainDevice();
        mainDevice3.setName("离家");
        List<MainDevice> devices2 = new ArrayList<>();
        devices2.add(mainDevice2);
        devices2.add(mainDevice3);

        item2.setDevices(devices2);

        PatternItem2 item3 = new PatternItem2();

        item3.setName(getString(R.string.all_devices));
        MainDevice mainDevice5 = new MainDevice();
        mainDevice5.setName("纱窗");
        MainDevice mainDevice4 = new MainDevice();
        mainDevice4.setName("空调");
        List<MainDevice> devices3 = new ArrayList<>();
        devices3.add(mainDevice5);
        devices3.add(mainDevice4);
        item3.setDevices(devices3);

        datas.add(item);
        datas.add(item2);
        datas.add(item3);

        expandableListView = (ExpandableListView) findViewById(R.id.ExpandableListView);
        MyAdapter adapter = new MyAdapter();
        expandableListView.setAdapter(adapter);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_linkage_action);
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
            List<String> s = new ArrayList<>();
            for (int i = 0; i < datas.size(); i++) {
                for (int j = 0; j < datas.get(i).getDevices().size(); j++) {
                    if (datas.get(i).getDevices().get(j).isChecked()) {
                        s.add(datas.get(i).getDevices().get(j).getName());
                    }
                }
            }
            String[] strings = new String[s.size()];
            s.toArray(strings);
            Intent intent = new Intent();
            intent.putExtra("data", strings);
            setResult(103, intent);
            System.out.println("size=" + s.size());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return datas.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return datas.get(groupPosition).getDevices().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return datas.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return datas.get(groupPosition).getDevices().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = null;
            GroupHolder groupHolder = null;
            if (convertView != null) {
                view = convertView;
                groupHolder = (GroupHolder) view.getTag();
            } else {
                view = LayoutInflater.from(ChooseLinkageResultActivity.this).inflate(R.layout.item_group, parent, false);
                groupHolder = new GroupHolder(view);
                view.setTag(groupHolder);
            }
            groupHolder.setData(groupPosition);
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = null;
            ChildHolder holder = null;
            if (convertView != null) {
                view = convertView;
                holder = (ChildHolder) view.getTag();
            } else {
                view = LayoutInflater.from(ChooseLinkageResultActivity.this).inflate(R.layout.item_children, parent, false);
                holder = new ChildHolder(view);
                view.setTag(holder);
            }
            holder.setData(groupPosition, childPosition);
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        public class GroupHolder {
            TextView mName;
            ImageView mImage;
            CheckBox state;
            String[] colors = new String[]{"#f99e5b", "#d3e4ad", "#94c9d6"};

            public GroupHolder(View view) {
                mName = (TextView) view.findViewById(R.id.name);
                mImage = (ImageView) view.findViewById(R.id.imageView);
                state = (CheckBox) view.findViewById(R.id.checkbox);

            }

            public void setData(final int groupPosition) {
                mName.setText(datas.get(groupPosition).getName());
                state.setChecked(datas.get(groupPosition).isChecked());
                mImage.setVisibility(View.GONE);
//                mImage.setBackgroundColor(Color.parseColor(colors[groupPosition % 3]));
                state.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (state.isChecked()) {
                            for (int i = 0; i < datas.get(groupPosition).getDevices().size(); i++) {
                                datas.get(groupPosition).getDevices().get(i).setChecked(true);
                            }
                            datas.get(groupPosition).setChecked(true);
                        } else {
                            for (int i = 0; i < datas.get(groupPosition).getDevices().size(); i++) {
                                datas.get(groupPosition).getDevices().get(i).setChecked(false);
                            }
                            datas.get(groupPosition).setChecked(false);
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        }

        public class ChildHolder {
            ImageView mImage;
            TextView mText;
            CheckBox state;
            TextView area;

            public ChildHolder(View view) {
                mText = (TextView) view.findViewById(R.id.name);
                state = (CheckBox) view.findViewById(R.id.checkbox);
                area = (TextView) view.findViewById(R.id.area);
            }

            public void setData(final int groupPosition, final int childPosition) {
                if (groupPosition == 0)
                    area.setVisibility(View.GONE);
                else if (groupPosition==1)
                    area.setVisibility(View.GONE);
                else
                    area.setVisibility(View.VISIBLE);

                mText.setText(datas.get(groupPosition).getDevices().get(childPosition).getName());
                state.setChecked(datas.get(groupPosition).getDevices().get(childPosition).isChecked());
                state.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datas.get(groupPosition).getDevices().get(childPosition).setChecked(state.isChecked());
                        int checkedCount = 0;
                        for (int i = 0; i < datas.get(groupPosition).getDevices().size(); i++) {
                            if (datas.get(groupPosition).getDevices().get(i).isChecked())
                                checkedCount++;
                        }
                        if (checkedCount == datas.get(groupPosition).getDevices().size()) {
                            datas.get(groupPosition).setChecked(true);
                        } else {
                            datas.get(groupPosition).setChecked(false);

                        }
                        notifyDataSetChanged();

                    }
                });

            }
        }
    }

}
