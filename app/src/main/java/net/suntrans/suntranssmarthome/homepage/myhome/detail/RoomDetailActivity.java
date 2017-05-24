package net.suntrans.suntranssmarthome.homepage.myhome.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.android.ActivityEvent;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.databinding.ActivityRoomdetailBinding;
import net.suntrans.suntranssmarthome.databinding.ItemChannelConBinding;
import net.suntrans.suntranssmarthome.homepage.device.switchs.ChannelResult;
import net.suntrans.suntranssmarthome.homepage.myhome.edit.EditRoomActivity;
import net.suntrans.suntranssmarthome.utils.LogUtil;
import net.suntrans.suntranssmarthome.utils.UiUtils;
import net.suntrans.suntranssmarthome.websocket.SenderWebSocket;
import net.suntrans.suntranssmarthome.widget.LoadingDialog;
import net.suntrans.suntranssmarthome.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/5/2.
 */

public class RoomDetailActivity extends BasedActivity implements SenderWebSocket.onReceiveListener {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<ChannelResult.Channel> datas = new ArrayList<>();
    private String house_id;
    private Myadapter adapter;
    private LoadingDialog dialog;
    private String name;
    SenderWebSocket socket;
    private String subname;
    private ActivityRoomdetailBinding binding;
    public static final String TRANSITION_SLIDE_BOTTOM = "SLIDE_BOTTOM";
    public static final String EXTRA_TRANSITION = "EXTRA_TRANSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTransition();
        init();
    }

    private void applyTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String transition = getIntent().getStringExtra(EXTRA_TRANSITION);
            switch (transition) {
                case TRANSITION_SLIDE_BOTTOM:
                    Transition transitionSlideBottom =
                            TransitionInflater.from(this).inflateTransition(R.transition.slide_bottom);
                    getWindow().setEnterTransition(transitionSlideBottom);
                    break;
            }
        }
    }

    private void init() {
        socket = new SenderWebSocket();
        socket.connect();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_roomdetail);
        house_id = getIntent().getStringExtra("house_id");
        name = getIntent().getStringExtra("name");
        subname = getIntent().getStringExtra("subname");
        String url = getIntent().getStringExtra("imgurl");
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.img_homepage)
                .centerCrop()
                .into(binding.bg);
//        binding.toolbar.setTitle(name);
//        binding.toolbar.setSubtitle(subname);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        binding.collapsingToolbar.setTitle(name);
        recyclerView = binding.recyclerview;
        refreshLayout = binding.refreshlayout;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Myadapter(datas, this);
        adapter.setListener(new Myadapter.OnSwitchClickListener() {
            @Override
            public void onSwitchClickListener(int position, SwitchButton compat) {
                handler.sendEmptyMessage(MSG_START);
                handler.sendMessageDelayed(Message.obtain(handler, MSG_CON_FAILED, "请求失败,请检查你的网络"), 2000);
                boolean isChecked = compat.isChecked();
                LogUtil.i(position + "," + isChecked);
                if (datas.size() != 0) {
                    final JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("device", "slc");
                        jsonObject.put("action", "switch");
                        jsonObject.put("channel_id", Integer.valueOf(datas.get(position).getId()));
                        jsonObject.put("command", isChecked ? 1 : 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            if (socket != null)
                                socket.sendMessage(jsonObject.toString());
                        }
                    }.start();
                }
            }

            @Override
            public void onChannelClickListener(int position, TextView textView) {
                showModifyNameDialog(position, textView);
            }
        });
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        dialog = new LoadingDialog(this, R.style.loading_dialog);
        dialog.setCancelable(false);
        socket.setOnReceiveListener(this);

    }

    private void showModifyNameDialog(final int position, TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_modify_channelname, null, false);
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
        ;
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    @Override
    public void onMessage(String msg1) {
        LogUtil.i(msg1);
        try {
            JSONObject jsonObject = new JSONObject(msg1);
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");
            if (code.equals("200")) {
                String channel_id = jsonObject.getString("channel_id");
                String status = jsonObject.getString("status");

                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).getId().equals(channel_id)) {
                        if (!datas.get(i).getStatus().equals(status)) {
                            datas.get(i).setStatus(status);
                            handler.removeMessages(MSG_CON_FAILED);
                            handler.sendEmptyMessage(MSG_CON_SUCCESS);
                        }
                    }
                }

            } else if (code.equals("404")) {
                handler.removeMessages(MSG_CON_FAILED);
                handler.sendMessage(Message.obtain(handler, MSG_CON_FAILED, msg));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onOpen() {

    }


    private static class Myadapter extends RecyclerView.Adapter<Myadapter.ViewHolder> {
        private List<ChannelResult.Channel> datas;
        private Context context;

        public Myadapter(List<ChannelResult.Channel> datas, Context context) {
            this.datas = datas;
            this.context = context;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemChannelConBinding binding = ItemChannelConBinding.inflate(LayoutInflater.from(context), parent, false);
            ViewHolder holder = new ViewHolder(binding.getRoot());
            holder.setBind(binding);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ItemChannelConBinding getBind() {
                return bind;
            }

            public void setBind(final ItemChannelConBinding bind) {
                this.bind = bind;
                bind.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onChannelClickListener(getAdapterPosition(), bind.channelName);
                        }
                    }
                });
            }

            private ItemChannelConBinding bind;

            public ViewHolder(View itemview) {
                super(itemview);

            }

            public void setData(int position) {
                bind.switchButton.setCheckedImmediately(datas.get(position).getStatus().equals("1") ? true : false);
                bind.name.setText(datas.get(position).getName() == null ? "未命名" : datas.get(position).getName());
                bind.channelName.setText("通道"+(position+1));
                bind.switchButton.setOnCheckedChangeListener(null);
                bind.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (listener != null)
                            listener.onSwitchClickListener(getAdapterPosition(), (SwitchButton) buttonView);
                    }
                });
            }
        }


        public void setListener(OnSwitchClickListener listener) {
            this.listener = listener;
        }

        private OnSwitchClickListener listener;

        public interface OnSwitchClickListener {
            void onSwitchClickListener(int position, SwitchButton compat);

            void onChannelClickListener(int position, TextView textView);
        }
    }

    private void getData() {
        Observable<ChannelResult> stslcOb = null;
        stslcOb = RetrofitHelper.getApi().getModelDetail(house_id);

        stslcOb.compose(this.<ChannelResult>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ChannelResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        binding.setResult(null);
                    }

                    @Override
                    public void onNext(ChannelResult channelResult) {
                        if (channelResult.status.equals("1")) {
                            if (channelResult.result != null) {
                                datas.clear();
                                binding.setResult(channelResult.result.rows);
                                datas.addAll(channelResult.result.rows);
                                adapter.notifyDataSetChanged();
                                refreshLayout.setRefreshing(false);
                            }
                        } else {
                            binding.setResult(null);

                        }
                    }
                });
    }


    private static final int MSG_START = 0;
    private static final int MSG_CON_SUCCESS = 1;
    private static final int MSG_CON_FAILED = 2;
    Handler handler = new Handler() {
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
                    handler.sendEmptyMessageDelayed(MSG_CON_SUCCESS, 500);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        socket.closeWebSocket();
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        if (item.getItemId() == R.id.edit) {
            Intent intent = new Intent(this, EditRoomActivity.class);
            intent.putExtra("title", name);
            intent.putExtra("imgurl", getIntent().getStringExtra("imgurl"));
            intent.putExtra("id", house_id);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void modifyChannel(String name, String img_id, String channel_id) {
        Observable<ChannelResult> stslcOb = null;
        stslcOb = RetrofitHelper.getApi().upDateChannel(channel_id, img_id, name);
        stslcOb.compose(this.<ChannelResult>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ChannelResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UiUtils.showMessage(findViewById(android.R.id.content), "修改失败");
                    }

                    @Override
                    public void onNext(ChannelResult channelResult) {
                        if (channelResult.status.equals("1")) {
                            UiUtils.showMessage(findViewById(android.R.id.content), channelResult.msg);
                            getData();
                        } else {
                            UiUtils.showMessage(findViewById(android.R.id.content), "修改失败");
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
