package net.suntrans.smarthome.fragment.room;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.activity.ModifyINActivity;
import net.suntrans.smarthome.activity.mh.AddModelActivity;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.bean.CreateModelResult;
import net.suntrans.smarthome.bean.HomeRoomResult;
import net.suntrans.smarthome.activity.room.RoomDetailActivity;
import net.suntrans.smarthome.activity.room.EditRoomActivity;
import net.suntrans.smarthome.utils.LogUtil;
import net.suntrans.smarthome.utils.StatusBarCompat;
import net.suntrans.smarthome.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/5/9.
 */

public class RoomFragment extends RxFragment {
    private List<HomeRoomResult.Room> roomDatas;
    private RoomAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private View statusBarFix;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_add_room);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), AddModelActivity.class);
                intent.putExtra("type", "room");
                startActivity(intent);
                return true;
            }
        });
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
        LogUtil.i("onViewCreated");

        roomDatas = new ArrayList<>();
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRoomData();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(true);
        adapter = new RoomAdapter(R.layout.item_scene1, roomDatas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                Intent intent = new Intent(getActivity(), RoomDetailActivity.class);
                intent.putExtra("house_id", roomDatas.get(position).id);
                intent.putExtra("name", roomDatas.get(position).name);
                intent.putExtra("imgurl", roomDatas.get(position).img);
                intent.putExtra(
                        RoomDetailActivity.EXTRA_TRANSITION, RoomDetailActivity.TRANSITION_SLIDE_BOTTOM);
                Pair toolbarParticipants =
                        new Pair<>(imageView, ViewCompat.getTransitionName(imageView));
                ActivityOptionsCompat transitionActivityOptions =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(), toolbarParticipants);
                ActivityCompat.startActivity(
                        getActivity(), intent, transitionActivityOptions.toBundle());

            }
        });
//        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                Intent intent = new Intent(getActivity(), EditRoomActivity.class);
//                intent.putExtra("title", roomDatas.get(position).name);
//                intent.putExtra("imgurl", roomDatas.get(position).img);
//                intent.putExtra("id", roomDatas.get(position).id);
//                startActivity(intent);
//            }
//        });

        getRoomData();

    }


    private void getRoomData() {
        RetrofitHelper.getApi().getHomeHouse()
                .compose(this.<HomeRoomResult>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HomeRoomResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(0);

                    }

                    @Override
                    public void onNext(HomeRoomResult homeSceneResult) {
                        LogUtil.i("房间获取成功！");
                        if (homeSceneResult != null) {
                            if (homeSceneResult.status.equals("1")) {
                                roomDatas.clear();
                                roomDatas.addAll(homeSceneResult.result.rows);
                                LogUtil.i("图片url为：" + homeSceneResult.result.rows.get(0).img);
                                adapter.notifyDataSetChanged();
                                handler.removeCallbacksAndMessages(null);
                                refreshLayout.setRefreshing(false);

                            } else {

                            }
                        }
                    }
                });
    }


    class RoomAdapter extends BaseQuickAdapter<HomeRoomResult.Room, BaseViewHolder> {

        public RoomAdapter(int layoutResId, List<HomeRoomResult.Room> data) {
            super(layoutResId, data);
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }

        @Override
        protected void convert(BaseViewHolder helper, HomeRoomResult.Room item) {
            helper.setText(R.id.name, item.name)
                    .addOnClickListener(R.id.imageView);
            ImageView view = helper.getView(R.id.imageView);
            Glide.with(getActivity())
                    .load(item.img)
                    .placeholder(R.drawable.pic_myhome_model_read)
                    .crossFade()
                    .into(view);
        }
    }


    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    refreshLayout.setRefreshing(false);
                    break;
            }
        }
    };


    public static void main(String[] args) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
