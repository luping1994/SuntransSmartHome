package net.suntrans.smarthome.fragment.mh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.bean.LinkageResult;
import net.suntrans.smarthome.activity.mh.EditLinkageActivity;
import net.suntrans.smarthome.utils.LogUtil;
import net.suntrans.smarthome.widget.MyItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Looney on 2017/5/9.
 */

public class LinkageFragment extends Fragment {
    private List<LinkageResult> datas;

    private SwipeRefreshLayout refreshLayout;
    private MyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        datas = new ArrayList<>();
        for (int i =0;i<2;i++){
            LinkageResult result = new LinkageResult();
            result.name=i==0?"温度过高就开空调":"光线昏暗亮灯";
            datas.add(result);
        }
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLinkagerData();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        adapter = new MyAdapter(R.layout.item_linkage,datas);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new MyItemDecoration());

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                LogUtil.i("我被点击了："+((CheckBox)view).isChecked());
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LogUtil.i("item被点击了");

                Intent intent = new Intent();
                intent.setClass(getActivity(), EditLinkageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getLinkagerData() {

    }

    Handler handler = new Handler();

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    class MyAdapter extends BaseQuickAdapter<LinkageResult,BaseViewHolder>{

        public MyAdapter(@LayoutRes int layoutResId, @Nullable List<LinkageResult> data) {
            super(layoutResId, data);
        }

        public MyAdapter(@Nullable List<LinkageResult> data) {
            super(data);
        }

        public MyAdapter(@LayoutRes int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, LinkageResult item) {
            helper.setText(R.id.name,item.name)
                    .addOnClickListener(R.id.checkbox);
        }
    }
}
