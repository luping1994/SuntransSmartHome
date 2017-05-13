package net.suntrans.suntranssmarthome.homepage.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.suntranssmarthome.Config;
import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.databinding.FragmentDevicesBinding;
import net.suntrans.suntranssmarthome.databinding.ItemDeviceBinding;
import net.suntrans.suntranssmarthome.homepage.device.add.AddDeviceActivity;
import net.suntrans.suntranssmarthome.homepage.device.sensus.SensusDetailActivity;
import net.suntrans.suntranssmarthome.homepage.device.switchs.SwitchControlActivity;
import net.suntrans.suntranssmarthome.utils.LogUtil;
import net.suntrans.suntranssmarthome.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.content.Context;

/**
 * Created by Looney on 2017/4/20.
 */

public class DevicesFragment extends RxFragment {

    private static final String TAG = "DevicesFragment";
    private FragmentDevicesBinding binding;
    private DevicesAdapter adapter;
//    private DeviceContract.Presenter presenter;
//    private DeviceInfoResult result;

    public static DevicesFragment newInstance() {
        return new DevicesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Toolbar toolbar = binding.toolbar;
        ((RxAppCompatActivity) getActivity()).setSupportActionBar(toolbar);

//        ActionBar ab = ((RxAppCompatActivity) getActivity()).getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(false);
//        ab.setDisplayShowHomeEnabled(false);
//        ab.setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.inflateMenu(R.menu.menu_devices_add);
//        LogUtil.e(TAG, "toolbar is inflateMenu");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            int statebarHeight = StatusBarCompat.getStatusBarHeight(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statebarHeight);
            binding.statusBarFix.setLayoutParams(layoutParams);

        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter = new DevicesAdapter(datas, getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setLayoutManager(manager);
        binding.recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        binding.recyclerview.setItemAnimator(new DefaultItemAnimator());
        binding.refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.refreshlayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    Handler handler = new Handler();

    @Override
    public void onResume() {
        getData();
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


    private List<DeviceInfoResult.DeviceInfo> datas = new ArrayList<>();

    public static class DevicesAdapter extends RecyclerView.Adapter {
        List<DeviceInfoResult.DeviceInfo> datas;
        private Context context;

        public DevicesAdapter(List<DeviceInfoResult.DeviceInfo> datas, Context context) {
            this.datas = datas;
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ViewHolder(inflater.inflate(R.layout.item_device, parent, false));
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

            ImageView imageView;
            TextView title;
            TextView name;
            LinearLayout root;
            public ViewHolder(View itemView) {
                super(itemView);
                root = (LinearLayout) itemView.findViewById(R.id.root);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                title = (TextView) itemView.findViewById(R.id.title);
                name = (TextView) itemView.findViewById(R.id.name);
                itemView.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        String type = datas.get(getAdapterPosition()).vtype;
                        if (type.equals(Config.SENSUS)) {
                            intent.setClass(context, SensusDetailActivity.class);
                            intent.putExtra("dev_id", datas.get(getAdapterPosition()).id);
                            intent.putExtra("type", Config.SENSUS);
                        } else if (type.equals(Config.STSLC_6)) {
                            intent.setClass(context, SwitchControlActivity.class);
                            intent.putExtra("dev_id", datas.get(getAdapterPosition()).id);
                            intent.putExtra("type", Config.STSLC_6);
                        } else if (type.equals(Config.STSLC_10)) {
                            intent.setClass(context, SwitchControlActivity.class);
                            intent.putExtra("dev_id", datas.get(getAdapterPosition()).id);
                            intent.putExtra("type", Config.STSLC_10);
                        }
                        intent.putExtra("name", datas.get(getAdapterPosition()).title);
                        intent.putExtra("subname", datas.get(getAdapterPosition()).name);
                        Pair toolbarParticipants =
                                new Pair<>(root, ViewCompat.getTransitionName(root));
                        ActivityOptionsCompat transitionActivityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        (Activity) context, toolbarParticipants);
                        ActivityCompat.startActivity(
                                context, intent, transitionActivityOptions.toBundle());
                    }
                });
            }

            public void setData(int position) {
                Glide.with(context)
                        .load(datas.get(position).img)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(imageView);
                title.setText(datas.get(position).name);
                name.setText(datas.get(position).title);
            }
        }
    }

    public void getData() {
        RetrofitHelper.getApi().getDevicesInfo()
                .compose(this.<DeviceInfoResult>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<DeviceInfoResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DeviceInfoResult deviceInfoResult) {
                        if (deviceInfoResult != null) {
                            datas.clear();
//                            binding.setDeviceinfo(deviceInfoResult);
                            if (deviceInfoResult.result.rows != null) {
                                datas.addAll(deviceInfoResult.result.rows);
                                adapter.notifyDataSetChanged();
                                LogUtil.i(deviceInfoResult.result.rows.get(0).toString());
                            }
                        }
                    }
                });
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
