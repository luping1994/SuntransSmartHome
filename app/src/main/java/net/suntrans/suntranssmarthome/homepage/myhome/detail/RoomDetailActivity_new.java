package net.suntrans.suntranssmarthome.homepage.myhome.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.android.ActivityEvent;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.databinding.ActivityRoomdetailBinding;
import net.suntrans.suntranssmarthome.databinding.ActivityRoomdetailNewBinding;
import net.suntrans.suntranssmarthome.databinding.ItemChannelConBinding;
import net.suntrans.suntranssmarthome.homepage.device.switchs.ChannelResult;
import net.suntrans.suntranssmarthome.homepage.myhome.HouseFragment;
import net.suntrans.suntranssmarthome.homepage.myhome.LinkageFragment;
import net.suntrans.suntranssmarthome.homepage.myhome.SenceFragment;
import net.suntrans.suntranssmarthome.homepage.myhome.add.AddLinkageActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.add.AddModelActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.edit.EditRoomActivity;
import net.suntrans.suntranssmarthome.utils.LogUtil;
import net.suntrans.suntranssmarthome.utils.UiUtils;
import net.suntrans.suntranssmarthome.websocket.SenderWebSocket;
import net.suntrans.suntranssmarthome.widget.AppBarStateChangeListener;
import net.suntrans.suntranssmarthome.widget.LoadingDialog;
import net.suntrans.suntranssmarthome.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/5/2.
 */

public class RoomDetailActivity_new extends BasedActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<ChannelResult.Channel> datas = new ArrayList<>();
    private String house_id;
    private LoadingDialog dialog;
    private String name;
    private String subname;
    private ActivityRoomdetailNewBinding binding;
    public static final String TRANSITION_SLIDE_BOTTOM = "SLIDE_BOTTOM";
    public static final String EXTRA_TRANSITION = "EXTRA_TRANSITION";
    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTransition();
        init();
    }

    private void applyTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String transition = getIntent().getStringExtra(EXTRA_TRANSITION);
            switch (transition) {
                case TRANSITION_SLIDE_BOTTOM:
                    Transition transitionSlideBottom =
                            TransitionInflater.from(this).inflateTransition(R.transition.slide_bottom);
                    getWindow().setEnterTransition(transitionSlideBottom);
                    break;
            }
        }
    }

    private void init() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_roomdetail_new);
        house_id = getIntent().getStringExtra("house_id");
        name = getIntent().getStringExtra("name");
        subname = getIntent().getStringExtra("subname");
        String url = getIntent().getStringExtra("imgurl");
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.img_homepage)
                .centerCrop()
                .into(binding.bg);
//        binding.toolbar.setTitle(name);
//        binding.toolbar.setSubtitle(subname);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        binding.collapsingToolbar.setTitle(name);


        binding.toolbar.inflateMenu(R.menu.menu_add);
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.sense) {
                    Intent intent = new Intent(RoomDetailActivity_new.this ,AddModelActivity.class);
                    intent.putExtra("type", "sence");
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.room) {
                    Intent intent = new Intent(RoomDetailActivity_new.this, AddModelActivity.class);
                    intent.putExtra("type", "room");
                    startActivity(intent);
                    return true;
                } else {
                    Intent intent = new Intent(RoomDetailActivity_new.this, AddLinkageActivity.class);
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


        HouseFragment fragment = new HouseFragment();
        SenceFragment fragment1 = new SenceFragment();
        LinkageFragment fragment2 = new LinkageFragment();
        fragments = new Fragment[]{fragment1, fragment, fragment2};
        binding.tablayout.setTabMode(TabLayout.MODE_FIXED);
        binding.tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(adapter);
        binding.tablayout.setupWithViewPager(binding.viewpager);
        binding.viewpager.setOffscreenPageLimit(3);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class PagerAdapter extends FragmentPagerAdapter {

        private final String[] title = new String[]{"场景", "设备", "联动"};

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
