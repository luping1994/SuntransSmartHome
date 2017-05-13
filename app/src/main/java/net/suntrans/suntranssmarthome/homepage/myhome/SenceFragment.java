package net.suntrans.suntranssmarthome.homepage.myhome;

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
import net.suntrans.suntranssmarthome.homepage.myhome.detail.SenceDetailActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.edit.EditRoomActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.edit.EditSenceActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/5/9.
 */

public class SenceFragment extends RxFragment {
    private List<HomeSceneResult.Scene> datas;
    private MyAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        datas = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSceneData();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        adapter = new MyAdapter(R.layout.item_scene1, datas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.imageView) {
                    Intent intent = new Intent(getActivity(), EditSenceActivity.class);
                    intent.putExtra("title", datas.get(position).name);
                    intent.putExtra("imgurl", datas.get(position).img);
                    intent.putExtra("id", datas.get(position).id);
                    startActivity(intent);
                }
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), SenceDetailActivity.class);
                intent.putExtra("title",datas.get(position).name);
                intent.putExtra("imgurl", datas.get(position).img);
                intent.putExtra("id", datas.get(position).id);
                startActivity(intent);
            }
        });
        getSceneData();
    }

    private void getSceneData() {
        RetrofitHelper.getApi().getHomeScene()
                .compose(this.<HomeSceneResult>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HomeSceneResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onNext(HomeSceneResult homeSceneResult) {
                        if (homeSceneResult != null) {
                            if (homeSceneResult.status.equals("1")) {
                                datas.clear();
                                datas.addAll(homeSceneResult.result.rows);
                                adapter.notifyDataSetChanged();
                                handler.removeCallbacksAndMessages(null);
                                refreshLayout.setRefreshing(false);

                            }
                        }
                    }
                });
    }

    class MyAdapter extends BaseQuickAdapter<HomeSceneResult.Scene, BaseViewHolder> {

        public MyAdapter(int layoutResId, List<HomeSceneResult.Scene> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, HomeSceneResult.Scene item) {
            helper.setText(R.id.name, item.name)
                    .addOnClickListener(R.id.imageView);
            final ImageView view = helper.getView(R.id.imageView);
            Glide.with(getActivity())
                    .load(item.img)
                    .centerCrop()
                    .placeholder(R.drawable.pic_myhome_model_read)
                    .into(view);

        }


    }


    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    //
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (datas != null) {
                if (datas.size() == 0) {
                    if (refreshLayout != null)
                        refreshLayout.setRefreshing(true);
                    getSceneData();
                }
            }
        } else {

        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
