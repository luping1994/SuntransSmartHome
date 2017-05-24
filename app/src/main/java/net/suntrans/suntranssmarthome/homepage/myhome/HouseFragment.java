package net.suntrans.suntranssmarthome.homepage.myhome;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.homepage.myhome.bean.HomeRoomResult;
import net.suntrans.suntranssmarthome.homepage.myhome.detail.RoomDetailActivity_new;
import net.suntrans.suntranssmarthome.homepage.myhome.edit.EditRoomActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.detail.RoomDetailActivity;
import net.suntrans.suntranssmarthome.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/5/9.
 */

public class HouseFragment extends RxFragment {
    private List<HomeRoomResult.Room> roomDatas;
    private RoomAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);
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
                Intent intent = new Intent(getActivity(), RoomDetailActivity_new.class);
                intent.putExtra("house_id", roomDatas.get(position).id);
                intent.putExtra("name", roomDatas.get(position).name);
                intent.putExtra("imgurl",roomDatas.get(position).img);
                intent.putExtra(
                        RoomDetailActivity.EXTRA_TRANSITION, RoomDetailActivity.TRANSITION_SLIDE_BOTTOM);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions transitionActivity = ActivityOptions.makeSceneTransitionAnimation(getActivity());
                    startActivity(intent, transitionActivity.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), EditRoomActivity.class);
                intent.putExtra("title", roomDatas.get(position).name);
                intent.putExtra("imgurl", roomDatas.get(position).img);
                intent.putExtra("id", roomDatas.get(position).id);
                startActivity(intent);
            }
        });
    }

    private void startActivityWithOptions(Intent intent) {

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
                                LogUtil.i("图片url为："+homeSceneResult.result.rows.get(0).img);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (roomDatas != null) {
                if (roomDatas.size() == 0) {
                    if (refreshLayout != null)
                        refreshLayout.setRefreshing(true);
                    getRoomData();
                }
            }
        } else {

        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
