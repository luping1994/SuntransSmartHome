package net.suntrans.suntranssmarthome.homepage.myhome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.databinding.FragmentMyhomeBinding;
import net.suntrans.suntranssmarthome.homepage.device.sensus.SensusResult;
import net.suntrans.suntranssmarthome.homepage.myhome.add.AddModelActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.bean.HomeRoomResult;
import net.suntrans.suntranssmarthome.homepage.myhome.bean.HomeSceneResult;
import net.suntrans.suntranssmarthome.homepage.myhome.detail.RoomDetailActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.edit.EditSenceActivity;
import net.suntrans.suntranssmarthome.utils.LogUtil;
import net.suntrans.suntranssmarthome.utils.UiUtils;
import net.suntrans.suntranssmarthome.widget.MyGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/4/20.
 */

public class MyHomeFragment extends RxFragment {

    private static final String TAG = "MyHomeFragment";
    private FragmentMyhomeBinding binding;
    private MyAdapter adapter;
    private RoomAdapter roomAdapter;
    private RoomAdapter bestAdapter;

    private int[] imgs = {R.drawable.pic_myhome_model_read, R.drawable.pic_myhome_model_get_up,
            R.drawable.pic_myhome_model_new, R.drawable.pic_myhome_model_sleep};

    public static MyHomeFragment newInstance() {
        return new MyHomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentMyhomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init();
    }


    private List<HomeSceneResult.Scene> datas;
    private List<HomeRoomResult.Room> roomDatas;

    private void init() {
        datas = new ArrayList<>();
        roomDatas = new ArrayList<>();
        MyGridLayoutManager manager1 = new MyGridLayoutManager(getActivity(), 3);
        MyGridLayoutManager manager2 = new MyGridLayoutManager(getActivity(), 3);
        manager1.setSmoothScrollbarEnabled(true);
        manager1.setAutoMeasureEnabled(true);
        manager2.setSmoothScrollbarEnabled(true);
        manager2.setAutoMeasureEnabled(true);
        binding.recyclerviewScene.setLayoutManager(manager1);
        binding.recyclerviewRoom.setLayoutManager(manager2);
        adapter = new MyAdapter(R.layout.item_scene, datas);
        roomAdapter = new RoomAdapter(R.layout.item_scene, roomDatas);


        binding.recyclerviewScene.setNestedScrollingEnabled(false);
        binding.recyclerviewRoom.setNestedScrollingEnabled(false);

        binding.recyclerviewScene.setAdapter(adapter);
        binding.recyclerviewRoom.setAdapter(roomAdapter);
        binding.addModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddModelActivity.class);
                startActivity(intent);
            }
        });
        binding.addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), EditSenceActivity.class);
                intent.putExtra("title", datas.get(position).name);
                startActivity(intent);
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UiUtils.showMessage(getActivity().findViewById(android.R.id.content),"模式中没有设备,请添加后执行!");
            }
        });
        roomAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), RoomDetailActivity.class);
                intent.putExtra("house_id",roomDatas.get(position).id);
                intent.putExtra("name",roomDatas.get(position).name);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }

    private void getEnvData() {
        RetrofitHelper.getApi().getHomeEnvironment()
                .compose(this.<SensusResult>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SensusResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SensusResult sensusResult) {
                        if (sensusResult.status.equals("1")) {
                            if (sensusResult.result != null) {
                                if (sensusResult.result.row != null) {
                                    binding.setInfo(sensusResult.result.row);
                                }
                                binding.recyclerviewScene.setVisibility(View.VISIBLE);
                                binding.noModel.setVisibility(View.INVISIBLE);
                            } else {
                                binding.recyclerviewScene.setVisibility(View.INVISIBLE);
                                binding.noModel.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                });
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
                    }

                    @Override
                    public void onNext(HomeSceneResult homeSceneResult) {
                        if (homeSceneResult != null) {
                            if (homeSceneResult.status.equals("1")) {
                                datas.clear();
                                datas.addAll(homeSceneResult.result.rows);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
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
                    }

                    @Override
                    public void onNext(HomeRoomResult homeSceneResult) {
                        if (homeSceneResult != null) {
                            if (homeSceneResult.status.equals("1")) {
                                binding.recyclerviewRoom.setVisibility(View.VISIBLE);
                                binding.noRoom.setVisibility(View.INVISIBLE);
                                roomDatas.clear();
                                roomDatas.addAll(homeSceneResult.result.rows);
                                roomAdapter.notifyDataSetChanged();

                            } else {
                                binding.recyclerviewRoom.setVisibility(View.INVISIBLE);
                                binding.noRoom.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    class MyAdapter extends BaseQuickAdapter<HomeSceneResult.Scene, BaseViewHolder> {

        private  List<HomeSceneResult.Scene> data;

        public MyAdapter(int layoutResId, List<HomeSceneResult.Scene> data) {
            super(layoutResId, data);
            this.data = data;

        }

        @Override
        protected void convert(final BaseViewHolder helper, HomeSceneResult.Scene item) {
            helper.setText(R.id.name, item.name);
//                    .addOnClickListener(R.id.edit_model)
//                    .setImageResource(R.id.image,imgs[helper.getAdapterPosition()%4]);
            final ImageView view = helper.getView(R.id.image);

            Glide.with(getActivity())
                    .load(item.img)
                    .asBitmap()
                    .placeholder(R.drawable.pic_myhome_model_read)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            view.setImageBitmap(resource);
                            Palette.generateAsync(resource,
                                    new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            Palette.Swatch swatch =
                                                    palette.getDarkVibrantSwatch();
                                            if (swatch != null) {
                                                helper.setTextColor(R.id.name,swatch.getRgb());
                                            }
                                        }
                                    });
                        }
                    });

        }


    }


    class RoomAdapter1 extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return roomDatas==null?0:roomDatas.size()>5?6:roomDatas.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position==5)
                return 1;
            return 0;
        }
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
            helper.setText(R.id.name, item.name);
//                    .setImageResource(R.id.image,imgs[helper.getAdapterPosition()%4]);

            ImageView view = helper.getView(R.id.image);
            Glide.with(getActivity())
                    .load(item.img)
                    .placeholder(R.drawable.pic_myhome_model_read)
                    .crossFade()
                    .into(view);
        }
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

    private void getData() {
        getSceneData();
        getRoomData();
        getEnvData();
    }
}


