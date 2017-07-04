package net.suntrans.smarthome.activity.mh;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Looney on 2017/5/23.
 */

public class LinkageGroupActivity extends BasedActivity {

    private MyAdapter adapter;
    private List<String> con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sence_dev_group);
        setUpToolbar();
        init();
    }

    private void init() {
        con = new ArrayList<>();
        con.add(getString(R.string.channel_choose_open));
        con.add(getString(R.string.channel_choose_close));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new MyAdapter();
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_linkage_group);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_dev, menu);
        MenuItem item = menu.findItem(R.id.addDev);
        item.setTitle(R.string.menu_title_add_linkage);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addDev) {
            Intent intent = new Intent(this, AddSenceLinkGrpActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class MyAdapter extends RecyclerView.Adapter {
        String[] colors = new String[]{"#f99e5b", "#d3e4ad", "#94c9d6"};
        String[] names = new String[]{"吊灯", "插座", "台灯", "洗衣机", "第六感"};

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ViewHolder(inflater.inflate(R.layout.item_sence_dev_group, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).setData(position);
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView name;
            RelativeLayout root;
            TextView value;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                value = (TextView) itemView.findViewById(R.id.value);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                root = (RelativeLayout) itemView.findViewById(R.id.root);
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddConDialog(v);
                        OptionsPickerView pickerView1 = null;
                        if (pickerView1 == null) {
                            pickerView1 = new OptionsPickerView.Builder(LinkageGroupActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                                @Override
                                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                                   value.setText(con.get(options1));
                                }
                            })
                                    .setTitleText(getString(R.string.dialog_title_choose_action))
                                    .build();
                            pickerView1.setPicker(con);
                        }
                        pickerView1.show();
                    }
                });

            }

            public void setData(int position) {
                imageView.setBackgroundColor(Color.parseColor(colors[position % 3]));
                name.setText(names[position % 4]);
            }
        }
    }


    private OptionsPickerView pickerView;

    private void showAddConDialog(final View view) {

    }

    public void commit(View v) {
        UiUtils.showToast("正在提交请稍后..");
    }
}
