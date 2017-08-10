package net.suntrans.smarthome.fragment.mh;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.bean.SensusResult;
import net.suntrans.smarthome.databinding.FragmentMyhome2Binding;
import net.suntrans.smarthome.activity.mh.AddLinkageActivity;
import net.suntrans.smarthome.activity.mh.AddModelActivity;
import net.suntrans.smarthome.activity.mh.EnvDetailActivity;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.widget.AppBarStateChangeListener;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/4/20.
 */

public class MyHomeFragment2 extends RxFragment {

    private static final String TAG = "MyHomeFragment";
    private FragmentMyhome2Binding binding;

    private Fragment[] fragments;
    private PopupMenu menu;
    private SensusResult.Sensus info;


    public static MyHomeFragment2 newInstance() {
        return new MyHomeFragment2();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentMyhome2Binding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init();
    }


    private void init() {

        binding.toolbar.inflateMenu(R.menu.menu_add);
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.sense) {
                    Intent intent = new Intent(getActivity(), AddModelActivity.class);
                    intent.putExtra("type", "sence");
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.room) {
                    Intent intent = new Intent(getActivity(), AddModelActivity.class);
                    intent.putExtra("type", "room");
                    startActivity(intent);
                    return true;
                } else {
                    Intent intent = new Intent(getActivity(), AddLinkageActivity.class);
                    startActivity(intent);
                }
                return false;
            }

        });

        binding.collapsingToolbar.setTitle("ST HOME");
        binding.collapsingToolbar.setScrimsShown(false);
        binding.collapsingToolbar.setExpandedTitleColor(Color.WHITE);
        binding.collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        binding.collapsingToolbar.setCollapsedTitleGravity(Gravity.CENTER);
        binding.appbar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.d("STATE", state.name());
                if (state == State.EXPANDED) {
                    binding.title.setAlpha(0);
                } else if (state == State.COLLAPSED) {
                    binding.title.setAlpha(1);
                    //折叠状态
                } else {

                }
            }
        });

        DeviceMainFragment fragment = DeviceMainFragment.newInstance("1");
        DeviceMainFragment fragment3 = DeviceMainFragment.newInstance("0");
        SenceFragment fragment1 = new SenceFragment();
        fragments = new Fragment[]{fragment1, fragment, fragment3};
        binding.tablayout.setTabMode(TabLayout.MODE_FIXED);
        binding.tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        binding.viewpager.setAdapter(adapter);
        binding.tablayout.setupWithViewPager(binding.viewpager);
        binding.viewpager.setOffscreenPageLimit(3);

        binding.wenduLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info == null) {
                    UiUtils.showToast("无法获取环境信息");
                    return;
                }
//                Intent intent = new Intent(getActivity(), EnvDetailActivity.class);
//                intent.putExtra("info", info);
//                startActivity(intent);
//                getActivity().overridePendingTransition(0, 0);

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
                                    info = sensusResult.result.row;
                                }

                            } else {

                            }
                        }

                    }
                });
    }


    private void getData() {
        getEnvData();
    }

    class PagerAdapter extends FragmentPagerAdapter {

        private final String[] title = new String[]{"场景", "照明", "其他","能耗"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }
}


