package net.suntrans.smarthome.fragment.mh;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.smarthome.App;
import net.suntrans.smarthome.Config;
import net.suntrans.smarthome.R;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.bean.AllDeviceResult;
import net.suntrans.smarthome.activity.perc.detail.AddDeviceActivity;
import net.suntrans.smarthome.activity.mh.DeviceMainSettingActivity;
import net.suntrans.smarthome.bean.CmdMsg;
import net.suntrans.smarthome.bean.CreateModelResult;
import net.suntrans.smarthome.utils.LogUtil;
import net.suntrans.smarthome.utils.ParseCMD;
import net.suntrans.smarthome.utils.RxBus;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.websocket.WebSocketService;
import net.suntrans.smarthome.widget.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static net.suntrans.smarthome.R.id.msg;

/**
 * Created by Looney on 2017/4/20.
 */

public class DeviceMainFragment extends RxFragment {


    private Subscription subscribe;
    private Observable<AllDeviceResult> getDataObj;

    public static DeviceMainFragment newInstance() {
        return new DeviceMainFragment();
    }

    private static final String TAG = "DeviceMainFragment";
    private DevicesAdapter adapter;

    private View statusBarFix;
    private SwipeRefreshLayout refreshLayout;
    private LoadingDialog dialog;
    private MyHandler mHandler;
    private String userid;

    private WebSocketService.ibinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (WebSocketService.ibinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        View view = inflater.inflate(R.layout.fragment_devicesmain, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        userid = App.getSharedPreferences().getString("user_id", "-1");

        Intent intent = new Intent();
        intent.setClass(getActivity(), WebSocketService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

        adapter = new DevicesAdapter(getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        refreshLayout.setNestedScrollingEnabled(true);

//        refreshLayout.setDistanceToTriggerSync(100);
        dialog = new LoadingDialog(getContext(), R.style.loading_dialog);
        dialog.setCancelable(false);
        mHandler = new MyHandler();


        subscribe = RxBus.getInstance().toObserverable(CmdMsg.class)
                .compose(this.<CmdMsg>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CmdMsg>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CmdMsg cmdMsg) {
                        if (cmdMsg.status == 1) {
                            parseMsg(cmdMsg.msg);
                        }
                    }
                });
    }


    private void parseMsg(String msg1) {
        try {
            JSONObject jsonObject = new JSONObject(msg1);
            String code = jsonObject.getString("code");
            String device = jsonObject.getString("device");
//            System.out.println(device);
            if (!device.equals(Config.STSLC_6) && !device.equals(Config.STSLC_10))
                return;
            if (code.equals("200")) {
                JSONObject result = jsonObject.getJSONObject("result");
                int channel = result.getInt("channel");
                int command = result.getInt("command");
                String addr = result.getString("addr");
                Map<String, String> map = ParseCMD.check((short) channel, (short) command);

                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).addr.equals(addr)) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            String number = entry.getKey();
                            String status = entry.getValue();
                            if (datas.get(i).number.equals(number)) {
                                datas.get(i).status = status;
                            }
                        }

                    }
                }

                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessage(MSG_CON_SUCCESS);

            } else if (code.equals("404")) {
                mHandler.removeMessages(MSG_CON_FAILED);
                mHandler.sendMessage(Message.obtain(mHandler, MSG_CON_FAILED, msg));
            } else if (code.equals("101")) {
                mHandler.removeMessages(MSG_CON_FAILED);
                mHandler.sendMessage(Message.obtain(mHandler, MSG_CON_FAILED, msg));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivity(new Intent(getActivity(), AddDeviceActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        getData();

        super.onResume();
    }

    class DevicesAdapter extends RecyclerView.Adapter {
        private Context context;
        String[] colors = new String[]{"#f99e5b", "#d3e4ad", "#94c9d6"};

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
            return datas.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView area;
            TextView name;
            RelativeLayout root;
            AppCompatCheckBox checkBox;

            public ViewHolder(View itemView) {
                super(itemView);
                area = (TextView) itemView.findViewById(R.id.area);
                name = (TextView) itemView.findViewById(R.id.name);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                checkBox = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox);
                root = (RelativeLayout) itemView.findViewById(R.id.root);
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendCmd(getAdapterPosition());
                    }


                });
                root.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showSettingDialog(getAdapterPosition(), name);
//                        showModifyNameDialog(getAdapterPosition(),name);
                        return true;
                    }
                });
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), DeviceMainSettingActivity.class);
                        intent.putExtra("id", datas.get(getAdapterPosition()).channel_id);
                        intent.putExtra("dev_id", datas.get(getAdapterPosition()).dev_id);
                        intent.putExtra("name", datas.get(getAdapterPosition()).name);
                        intent.putExtra("house_id", datas.get(getAdapterPosition()).houseid);
                        intent.putExtra("house_name", datas.get(getAdapterPosition()).housename);
                        intent.putExtra("vtype", datas.get(getAdapterPosition()).vtype);
                        startActivity(intent);
                    }
                });

            }

            public void setData(int position) {
                Glide.with(getActivity())
                        .load(datas.get(position).img)
                        .placeholder(R.drawable.icon_default)
                        .into(imageView);
                if (TextUtils.isEmpty(datas.get(position).img)) {
                    imageView.setBackgroundResource(R.drawable.circle);
                } else {
                    imageView.setBackground(null);
                }
                name.setText(datas.get(position).name);
                area.setText(TextUtils.isEmpty(datas.get(position).housename) ? "--" : datas.get(position).housename);
                checkBox.setChecked(datas.get(position).status.equals("1") ? true : false);
            }

            private void sendCmd(int position) {
                mHandler.sendEmptyMessage(MSG_START);
                mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_CON_FAILED, "设备不在线"), 2000);
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("device", datas.get(position).vtype);
                    jsonObject.put("action", "switch");
                    jsonObject.put("user_id", userid);

                    jsonObject.put("channel_id", Integer.valueOf(datas.get(position).channel_id));
//                        jsonObject.put("channel_id", "234");

                    jsonObject.put("command", datas.get(position).status.equals("1") ? 0 : 1);

                    binder.sendOrder(jsonObject.toString());
                    LogUtil.i("DeviceMainFragment", jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    String[] items = new String[]{"设置", "更改名称"};

    private void showSettingDialog(final int adapterPosition, final TextView name) {
        new AlertDialog.Builder(getContext())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {
                            showModifyNameDialog(adapterPosition, name);
                        } else if (which == 0) {
                            Intent intent = new Intent(getActivity(), DeviceMainSettingActivity.class);
                            intent.putExtra("id", datas.get(adapterPosition).channel_id);
                            intent.putExtra("dev_id", datas.get(adapterPosition).dev_id);
                            intent.putExtra("name", datas.get(adapterPosition).name);
                            intent.putExtra("house_id", datas.get(adapterPosition).houseid);
                            intent.putExtra("house_name", datas.get(adapterPosition).housename);
                            startActivity(intent);
                        }
                    }
                }).create().show();

    }

    private void showModifyNameDialog(final int position, TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_modify_channelname, null, false);
        final EditText name = (EditText) view.findViewById(R.id.channelName);
        name.setText(textView.getText().toString());
        name.setSelection(textView.getText().length());
        builder.setPositiveButton(R.string.queding, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modifyChannel(name.getText().toString(), datas.get(position).img_id, datas.get(position).channel_id);
            }
        });
        builder.setNegativeButton(R.string.qvxiao, null);
        builder.setTitle(R.string.title_modify_channelname);
        builder.setView(view);
        builder.create().show();
    }

    private List<AllDeviceResult.ChannelInfo> datas = new ArrayList<>();

    public void getData() {
        if (getDataObj == null){
            getDataObj = RetrofitHelper.getApi().getAllDevice()
                    .compose(this.<AllDeviceResult>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        getDataObj.subscribe(new Subscriber<AllDeviceResult>() {
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
            public void onNext(AllDeviceResult result) {
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(false);
                }
                if (result != null) {
                    datas.clear();
                    datas.addAll(result.channel.result);
                    adapter.notifyDataSetChanged();
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

    @Override
    public void onDestroy() {
        getActivity().unbindService(connection);
        mHandler.removeCallbacksAndMessages(null);
        if (subscribe != null) {
            if (!subscribe.isUnsubscribed()) {
                subscribe.unsubscribe();
            }
        }
        super.onDestroy();
    }


    private static final int MSG_START = 0;
    private static final int MSG_CON_SUCCESS = 1;
    private static final int MSG_CON_FAILED = 2;

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                    dialog.setWaitText("请稍后...");
                    dialog.show();
                    break;
                case MSG_CON_SUCCESS:
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                    break;
                case MSG_CON_FAILED:
                    dialog.setWaitText((String) msg.obj);
//                    adapter.notifyDataSetChanged();
                    mHandler.sendEmptyMessageDelayed(MSG_CON_SUCCESS, 500);
                    break;
            }
        }
    }

    private void modifyChannel(String name, String img_id, String channel_id) {
        Observable<CreateModelResult> stslcOb = null;
        stslcOb = RetrofitHelper.getApi().upDateChannel(channel_id, img_id, name);
        stslcOb.compose(this.<CreateModelResult>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CreateModelResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UiUtils.showMessage(getActivity().findViewById(android.R.id.content), "修改失败");
                    }

                    @Override
                    public void onNext(CreateModelResult channelResult) {
                        if (channelResult.status.equals("1")) {
                            UiUtils.showMessage(getActivity().findViewById(android.R.id.content), channelResult.msg);
                            getData();
                        } else {
                            UiUtils.showMessage(getActivity().findViewById(android.R.id.content), "修改失败");
                        }
                    }
                });
    }
}
