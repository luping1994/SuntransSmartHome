package net.suntrans.smarthome.activity.mh;

import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.TextView;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.base.BasedActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Looney on 2017/5/24.
 */

public class ChooseLinkageConActivity extends BasedActivity {

    private List<Map<String, String>> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_linkage_con);
        setUpToolbar();
        init();
    }

    private void init() {
        datas = new ArrayList<>();
        Map<String, String> a = new HashMap<>();
        a.put("name", "时间");
        a.put("ischecked", "false");
        Map<String, String> b = new HashMap<>();
        b.put("name", "甲醛");
        b.put("ischecked", "false");
        Map<String, String> c = new HashMap<>();
        c.put("name", "烟雾");
        c.put("ischecked", "false");
        datas.add(a);
        datas.add(b);
        datas.add(c);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_choose_linkage_condition);
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
                if (datas.get(i).get("ischecked").equals("true")) {
                    s.add(datas.get(i).get("name"));
                }
            }
            String[] strings = new String[s.size()];
            s.toArray(strings);
            Intent intent = new Intent();
            intent.putExtra("data", strings);
            setResult(101, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ChooseLinkageConActivity.this).inflate(R.layout.item_linkage_group, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).setData(position);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            CheckBox cb;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                cb = (CheckBox) itemView.findViewById(R.id.checkbox);
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datas.get(getAdapterPosition()).put("ischecked",cb.isChecked()?"true":"false");
                    }
                });
            }

            public void setData(int position) {
                name.setText(datas.get(position).get("name"));
                cb.setChecked(datas.get(position).get("ischecked").equals("true"));
            }
        }
    }


}
