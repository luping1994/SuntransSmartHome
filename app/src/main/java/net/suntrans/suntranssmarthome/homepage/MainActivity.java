package net.suntrans.suntranssmarthome.homepage;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.homepage.device.DevicesFragment;
import net.suntrans.suntranssmarthome.homepage.myhome.MyHomeFragment;
import net.suntrans.suntranssmarthome.homepage.myhome.MyHomeFragment2;
import net.suntrans.suntranssmarthome.homepage.personal.PersonalFragment;
import net.suntrans.suntranssmarthome.homepage.personal.PersonalPresenter;
import net.suntrans.suntranssmarthome.homepage.shop.ShopFragment;
import net.suntrans.suntranssmarthome.utils.LogUtil;
import net.suntrans.suntranssmarthome.utils.UiUtils;
import net.suntrans.suntranssmarthome.widget.MyHomeViewPager;

import static android.R.attr.fragment;
import static android.support.design.widget.TabLayout.GRAVITY_FILL;
import static android.support.design.widget.TabLayout.MODE_FIXED;

/**
 * Created by Looney on 2017/4/19.
 */

public class MainActivity extends BasedActivity {
    private final int[] TAB_TITLES = new int[]{R.string.nav_myhome, R.string.nav_device, R.string.nav_find, R.string.nav_user};
    private final int[] TAB_IMGS = new int[]{R.drawable.select_home, R.drawable.selector_devices, R.drawable.selector_find, R.drawable.selector_user};
    private TabLayout tabLayout;

    private Fragment[] fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.main_tabLayout);
        tabLayout.setTabMode(MODE_FIXED);
        tabLayout.setTabGravity(GRAVITY_FILL);
        setTabs(tabLayout, this.getLayoutInflater(), TAB_TITLES, TAB_IMGS);
//        final MyHomeViewPager pager = (MyHomeViewPager) findViewById(R.id.viewPager);
////        tabLayout.setupWithViewPager(pager);
//        pager.setOffscreenPageLimit(4);
//        pager.setScanScroll(false);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position > 1)
                    position = position - 1;
                changFragment(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        initFragment();
        //        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
        //        pager.setAdapter(adapter);
    }

    PersonalFragment fragment;

    private void initFragment() {
//        findViewById(android.R.id.content).setFitsSystemWindows(true);

        fragment = PersonalFragment.newInstance();
        new PersonalPresenter(fragment, this);
        MyHomeFragment2 myHomeFragment= MyHomeFragment2.newInstance();
        DevicesFragment devicesFragment = DevicesFragment.newInstance();
        ShopFragment shopFragment = ShopFragment.newInstance();
        fragments = new Fragment[]{
                myHomeFragment, devicesFragment,shopFragment,fragment
        };
        getSupportFragmentManager().beginTransaction().replace(R.id.content, myHomeFragment).commit();

    }

    /**
     * @description: 设置添加Tab
     */
    private void setTabs(TabLayout tabLayout, LayoutInflater inflater, int[] tabTitlees, int[] tabImgs) {
        for (int i = 0; i < tabImgs.length + 1; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = inflater.inflate(R.layout.tab, null);
            tab.setCustomView(view);

            TextView tvTitle = (TextView) view.findViewById(R.id.tv_tab);
            ImageView imgTab = (ImageView) view.findViewById(R.id.img_tab);
            if (i == 2) {
                tvTitle.setText("");
            }
            if (i < 2) {
                tvTitle.setText(tabTitlees[i]);
                imgTab.setImageResource(tabImgs[i]);
            }
            if (i > 2) {
                tvTitle.setText(tabTitlees[i - 1]);
                imgTab.setImageResource(tabImgs[i - 1]);
            }

            tabLayout.addTab(tab);

        }
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 4)
                return fragment;
            if (position == 0)
                return MyHomeFragment.newInstance();
            if (position == 1)
                return DevicesFragment.newInstance();
            if (position == 3)
                return ShopFragment.newInstance();
            return ShopFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 5;
        }
    }


    int currentIndex = 0;

    private void changFragment(int index) {
//        navView.setCheckedItem(menuId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[currentIndex]);
        if (!fragments[index].isAdded()) {
            transaction.add(R.id.content, fragments[index]);
        }
        transaction.show(fragments[index]).commit();
        currentIndex = index;
    }


    private long[] mHits = new long[2];
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - 2000)) {
                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                UiUtils.showToast(getString(R.string.tips_hit_again));
            }
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }
}
