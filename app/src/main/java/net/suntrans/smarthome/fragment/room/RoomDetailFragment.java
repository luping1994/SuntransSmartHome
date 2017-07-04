package net.suntrans.smarthome.fragment.room;

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
import net.suntrans.smarthome.R;
import net.suntrans.smarthome.activity.perc.detail.AddDeviceActivity;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.bean.AddSCResult;
import net.suntrans.smarthome.bean.ChannelResultNewSun;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static net.suntrans.smarthome.R.id.msg;


/**
 * Created by Looney on 2017/4/20.
 */

public class RoomDetailFragment extends RxFragment {

    private static final String TAG = "DeviceMainFragment";
    private DevicesAdapter adapter;

    private View statusBarFix;
    private SwipeRefreshLayout refreshLayout;
    private String house_id;
    private TextView tips;
    private RecyclerView recyclerView;

    private LoadingDialog dialog;
    private MyHandler mHandler;
    private String userid;


    private Observable<ChannelResultNewSun> getDataObv;
    private Observable<List<AddSCResult>> deleteObv;
    private Observable<CreateModelResult> modifyObv;

    public static RoomDetailFragment newInstance() {
        return new RoomDetailFragment();
    }


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
        View view = inflater.inflate(R.layout.fragment_room_detail, container, false);
        userid = App.getSharedPreferences().getString("user_id", "-1");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        Intent intent = new Intent();
        intent.setClass(getActivity(), WebSocketService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

        adapter = new DevicesAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
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
        house_id = getActivity().getIntent().getStringExtra("house_id");
        tips = (TextView) view.findViewById(R.id.tips);
        LogUtil.i("房间id" + house_id);

        dialog = new LoadingDialog(getContext(), R.style.loading_dialog);
        dialog.setCancelable(false);
        mHandler = new MyHandler();

        RxBus.getInstance().toObserverable(CmdMsg.class)
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
            if (code.equals("200")) {
                JSONObject result = jsonObject.getJSONObject("result");
                int channel = result.getInt("channel");
                int command = result.getInt("command");
                String device = result.getString("device");
                String addr = result.getString("addr");
                Map<String, String> map = ParseCMD.check((short) channel, (short) command);

                for (int i = 0; i < datas.size(); i++) {
//                    System.out.println("addr="+datas.get(i).getAddr());
                    if (datas.get(i).getAddr().equals(addr)) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            String number = entry.getKey();
                            String status = entry.getValue();
                            if (datas.get(i).getNumber().equals(number)) {
                                datas.get(i).setStatus(status);
                            }
                        }

                    }
                }
                mHandler.removeMessages(MSG_CON_FAILED);
                mHandler.sendEmptyMessage(MSG_CON_SUCCESS);


//                String channel_id = jsonObject.getString("channel_id");
//                String status = jsonObject.getString("status");
//
//                for (int i = 0; i < datas.size(); i++) {
//                    if (datas.get(i).getId().equals(channel_id)) {
//                        if (!datas.get(i).getStatus().equals(status)) {
//                            datas.get(i).setStatus(status);
//                            mHandler.removeCallbacksAndMessages(null);
//                            mHandler.sendEmptyMessage(MSG_CON_SUCCESS);
//                        }
//                    }
//                }

            } else if (code.equals("404")) {
                String msg = jsonObject.getString("msg");

                mHandler.removeMessages(MSG_CON_FAILED);
                mHandler.sendMessage(Message.obtain(mHandler, MSG_CON_FAILED, msg));
            } else if (code.equals("101")) {
                String msg = jsonObject.getString("msg");

                mHandler.removeMessages(MSG_CON_FAILED);
                mHandler.sendMessage(Message.obtain(mHandler, MSG_CON_FAILED, msg));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivity(new Intent(getActivity(), AddDeviceActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class DevicesAdapter extends RecyclerView.Adapter {
        String[] colors = new String[]{"#f99e5b", "#d3e4ad", "#94c9d6"};

        public DevicesAdapter() {
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ViewHolder(inflater.inflate(R.layout.item_room_detail, parent, false));
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
            AppCompatCheckBox checkbox;

            public ViewHolder(View itemView) {
                super(itemView);
                area = (TextView) itemView.findViewById(R.id.area);
                name = (TextView) itemView.findViewById(R.id.name);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                root = (RelativeLayout) itemView.findViewById(R.id.root);
                checkbox = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox);
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
                        return true;
                    }
                });

            }

            public void setData(int position) {
                Glide.with(getActivity())
                        .load(datas.get(position).getImg_url())
                        .placeholder(R.drawable.circle)
                        .crossFade()
                        .centerCrop()
                        .into(imageView);
                name.setText(datas.get(position).getName());
                checkbox.setChecked(datas.get(position).getStatus().equals("1") ? true : false);
                area.setText("暂无数据");
            }

            private void sendCmd(int position) {
                mHandler.sendEmptyMessage(MSG_START);
                mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_CON_FAILED, "请求失败,请检查你的网络"), 2000);
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("device", datas.get(position).getVtype());
                    jsonObject.put("action", "switch");
                    jsonObject.put("user_id", userid);

                    jsonObject.put("channel_id", Integer.valueOf(datas.get(position).getId()));
//                        jsonObject.put("channel_id", "234");

                    jsonObject.put("command", datas.get(position).getStatus().equals("1") ? 0 : 1);

                    binder.sendOrder(jsonObject.toString());
                    LogUtil.i("DeviceMainFragment", jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    List<ChannelResultNewSun.Channel> datas = new ArrayList<>();

    public void getData() {

        if (getDataObv == null) {
            getDataObv = RetrofitHelper.getApi().getRoomChannel(house_id)
                    .compose(this.<ChannelResultNewSun>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        getDataObv.subscribe(new Subscriber<ChannelResultNewSun>() {
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
            public void onNext(ChannelResultNewSun result) {
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(false);
                }
                if (result != null) {
                    datas.clear();
                    datas.addAll(result.result);
                    adapter.notifyDataSetChanged();

                }
                if (datas.size() == 0) {
                    tips.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tips.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        getActivity().unbindService(connection);
        mHandler.removeCallbacksAndMessages(null);
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


    String[] items = new String[]{"更改名称", "移除设备"};

    private void showSettingDialog(final int adapterPosition, final TextView name) {
        new AlertDialog.Builder(getContext())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            showModifyNameDialog(adapterPosition, name);
                        }
                        if (which == 1) {
                            showDeleteRoomChannel(adapterPosition);
                        }
                    }
                }).create().show();

    }

    private void showDeleteRoomChannel(final int position) {
        new AlertDialog.Builder(getActivity())
                .setMessage("是否从房间中移除改设备?")
                .setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRoomChannel(datas.get(position).getId());
                    }
                })
                .setNegativeButton(getString(R.string.qvxiao), null)
                .create().show();
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
                modifyChannel(name.getText().toString(), "0", datas.get(position).getId());
            }
        });
        builder.setNegativeButton(R.string.qvxiao, null);
        builder.setTitle(R.string.title_modify_channelname);
        builder.setView(view);
        builder.create().show();
    }

    private void deleteRoomChannel(String channel_id) {
        if (deleteObv == null) {
            deleteObv = RetrofitHelper.getApi().deleteRoomChannel(channel_id)
                    .compose(this.<List<AddSCResult>>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
        }

        deleteObv.subscribe(new Subscriber<List<AddSCResult>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<AddSCResult> result) {
                if (result != null) {
                    if (result.size() != 0) {
                        UiUtils.showToast(result.get(0).result);
                    } else {
                        UiUtils.showToast(getString(R.string.deleteFailed));
                    }
                }
                getData();
            }
        });
    }

    private void modifyChannel(String name, String img_id, String channel_id) {
        if (null == modifyObv) {
            modifyObv = RetrofitHelper.getApi().upDateChannel(channel_id, img_id, name)
                    .compose(this.<CreateModelResult>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        modifyObv.subscribe(new Subscriber<CreateModelResult>() {
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
