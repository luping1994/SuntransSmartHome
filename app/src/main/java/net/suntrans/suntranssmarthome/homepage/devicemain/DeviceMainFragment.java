package net.suntrans.suntranssmarthome.homepage.devicemain;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.homepage.device.add.AddDeviceActivity;
import net.suntrans.suntranssmarthome.utils.LogUtil;
import net.suntrans.suntranssmarthome.utils.StatusBarCompat;

/**
 * Created by Looney on 2017/4/20.
 */

public class DeviceMainFragment extends RxFragment {

    private static final String TAG = "DeviceMainFragment";
    private DevicesAdapter adapter;

    private View statusBarFix;

    public static DeviceMainFragment newInstance() {
        return new DeviceMainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        View view = inflater.inflate(R.layout.fragment_devicesmain, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        statusBarFix = view.findViewById(R.id.status_bar_fix);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statebarHeight = StatusBarCompat.getStatusBarHeight(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statebarHeight);
            statusBarFix.setLayoutParams(layoutParams);

        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter = new DevicesAdapter(getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    Handler handler = new Handler();


    @Override
    public void onResume() {
//        getData();
        super.onResume();
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_devices_add, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivity(new Intent(getActivity(), AddDeviceActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class DevicesAdapter extends RecyclerView.Adapter {
        private Context context;
        String[] colors = new String[]{"#f99e5b", "#d3e4ad", "#94c9d6"};
        String[] names = new String[]{"吊灯", "插座", "台灯", "洗衣机", "第六感"};

        String[] areas = new String[]{"书房", "客厅", "卧室", "卫生间"};

        public DevicesAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ViewHolder(inflater.inflate(R.layout.item_device_main, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).setData(position);
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView area;
            TextView name;
            RelativeLayout root;

            public ViewHolder(View itemView) {
                super(itemView);
                area = (TextView) itemView.findViewById(R.id.area);
                name = (TextView) itemView.findViewById(R.id.name);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                root = (RelativeLayout) itemView.findViewById(R.id.root);
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), DeviceMainSettingActivity.class));
                    }
                });

            }

            public void setData(int position) {
                imageView.setBackgroundColor(Color.parseColor(colors[position % 3]));
                name.setText(names[position % 4]);
                area.setText(areas[position % 4]);
            }
        }
    }

    public void getData() {

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            LogUtil.i(TAG + "可见");
        } else {
            LogUtil.i(TAG + "不可见");

        }
    }
}
