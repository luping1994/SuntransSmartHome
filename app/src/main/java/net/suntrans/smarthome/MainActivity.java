package net.suntrans.smarthome;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pgyersdk.update.PgyUpdateManager;

import net.suntrans.smarthome.activity.mh.SpeechActivity;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.fragment.mh.MyHomeFragment2;
import net.suntrans.smarthome.fragment.perc.PersonalFragment;
import net.suntrans.smarthome.fragment.room.RoomFragment;
import net.suntrans.smarthome.fragment.shop.ShopFragment;
import net.suntrans.smarthome.model.personal.PersonalPresenter;
import net.suntrans.smarthome.utils.StatusBarCompat;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.websocket.WebSocketService;

import static android.support.design.widget.TabLayout.GRAVITY_FILL;
import static android.support.design.widget.TabLayout.MODE_FIXED;

import android.content.Context;

/**
 * Created by Looney on 2017/4/19.
 */

public class MainActivity extends BasedActivity implements View.OnClickListener {
    private final int[] TAB_TITLES = new int[]{R.string.nav_myhome, R.string.nav_room, R.string.nav_find, R.string.nav_user};
    private final int[] TAB_IMGS = new int[]{R.drawable.select_home, R.drawable.selector_devices, R.drawable.selector_find, R.drawable.selector_user};
    private TabLayout tabLayout;

    private Fragment[] fragments;
    private ImageView speech;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.main_tabLayout);
        tabLayout.setTabMode(MODE_FIXED);
        tabLayout.setTabGravity(GRAVITY_FILL);
        speech = (ImageView) findViewById(R.id.tab_buttom_tx);
        speech.setOnClickListener(this);
        setTabs(tabLayout, this.getLayoutInflater(), TAB_TITLES, TAB_IMGS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompat.fixKITKATbar(this, getResources().getColor(R.color.colorPrimary));

        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 2)
                    return;
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
        PgyUpdateManager.register(this, "net.suntrans.suntranssmarthome.fileProvider");


        Intent intent = new Intent();
        intent.setClass(this, WebSocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("绑定成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("绑定失败");
        }
    };


    PersonalFragment fragment;

    private void initFragment() {
        fragment = PersonalFragment.newInstance();
        new PersonalPresenter(fragment, this);
        MyHomeFragment2 myHomeFragment = MyHomeFragment2.newInstance();
        RoomFragment devicesFragment = new RoomFragment();
        ShopFragment shopFragment = ShopFragment.newInstance();
        fragments = new Fragment[]{
                myHomeFragment, devicesFragment, shopFragment, fragment
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

    @Override
    protected void onDestroy() {
        PgyUpdateManager.unregister();
        unbindService(connection);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tab_buttom_tx) {
            startActivity(new Intent(this, SpeechActivity.class));
            overridePendingTransition(0, 0);
        }
//        ImageView imageView=null;
//        imageView.setBackgroundColor();
    }


}
