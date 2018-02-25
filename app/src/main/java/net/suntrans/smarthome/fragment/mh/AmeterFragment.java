package net.suntrans.smarthome.fragment.mh;

import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.activity.mh.EditSenceActivity;
import net.suntrans.smarthome.activity.mh.JianceDetail3Activity;
import net.suntrans.smarthome.activity.mh.JianceDetailActivity;
import net.suntrans.smarthome.activity.mh.SenceDetailActivity;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.bean.AmeterEntity;
import net.suntrans.smarthome.bean.HomeSceneResult;
import net.suntrans.smarthome.widget.MyItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static net.suntrans.smarthome.R.id.imageView;

/**
 * Created by Looney on 2017/5/9.
 */

public class AmeterFragment extends RxFragment {
    private List<AmeterEntity.ResultBean> datas;
    private MyAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private Observable<AmeterEntity> getDataOb;

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

            }
        });
        adapter = new MyAdapter(R.layout.item_scene1, datas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new MyItemDecoration());

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("id", datas.get(position).getId());
                intent.putExtra("vtype", datas.get(position).getVtype());
                intent.putExtra("name", datas.get(position).getTitle());
                intent.putExtra("sno", datas.get(position).getSno());
                if (datas.get(position).getVtype().equals("1")) {

                    intent.setClass(getActivity(), JianceDetailActivity.class);
                } else {
                    intent.setClass(getActivity(), JianceDetail3Activity.class);

                }
                startActivity(intent);
            }
        });
        getSceneData();
    }

    private void getSceneData() {
        if (getDataOb == null)
            getDataOb = RetrofitHelper.getApi().getAmmeter()
                    .compose(this.<AmeterEntity>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        getDataOb.subscribe(new Subscriber<AmeterEntity>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onNext(AmeterEntity homeSceneResult) {
                if (homeSceneResult != null) {
                    if (homeSceneResult.getStatus()==1) {
                        datas.clear();
                        datas.addAll(homeSceneResult.getResult());
                        adapter.notifyDataSetChanged();
                        handler.removeCallbacksAndMessages(null);
                        refreshLayout.setRefreshing(false);

                    }
                }
            }
        });
    }

    class MyAdapter extends BaseQuickAdapter<AmeterEntity.ResultBean, BaseViewHolder> {

        public MyAdapter(int layoutResId, List<AmeterEntity.ResultBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, AmeterEntity.ResultBean item) {
            helper.setText(R.id.name, item.getTitle())
                    .addOnClickListener(imageView);
            final ImageView view = helper.getView(imageView);
            view.setImageResource(R.drawable.ic_dianbiao);
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
//        if (isVisibleToUser) {
//            if (datas != null) {
//                if (datas.size() == 0) {
//                    if (refreshLayout != null)
//                        refreshLayout.setRefreshing(true);
//                    getSceneData();
//                }
//            }
//        } else {
//
//        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
