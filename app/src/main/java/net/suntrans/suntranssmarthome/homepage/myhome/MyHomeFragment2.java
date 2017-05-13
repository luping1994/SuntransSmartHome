package net.suntrans.suntranssmarthome.homepage.myhome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.databinding.FragmentMyhome2Binding;
import net.suntrans.suntranssmarthome.homepage.device.sensus.SensusResult;
import net.suntrans.suntranssmarthome.homepage.myhome.add.AddModelActivity;

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
        HouseFragment fragment = new HouseFragment();
        SenceFragment fragment1 = new SenceFragment();
        BestFragment fragment2 = new BestFragment();
        fragments = new Fragment[]{fragment1, fragment, fragment2};
        binding.tablayout.setTabMode(TabLayout.MODE_FIXED);
        binding.tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        binding.viewpager.setAdapter(adapter);
        binding.tablayout.setupWithViewPager(binding.viewpager);
        binding.viewpager.setOffscreenPageLimit(3);

        menu = new PopupMenu(getContext(), binding.imageAdd);
        menu.setGravity(Gravity.FILL);
        menu.getMenuInflater().inflate(R.menu.menu_add, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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
                }
                return false;
            }
        });
        binding.imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.show();
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

        private final String[] title = new String[]{"场景", "房间", "联动"};

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


