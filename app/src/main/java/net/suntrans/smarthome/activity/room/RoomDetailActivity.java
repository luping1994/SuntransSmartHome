package net.suntrans.smarthome.activity.room;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.activity.mh.AddLinkageActivity;
import net.suntrans.smarthome.activity.mh.AddModelActivity;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.databinding.ActivityRoomdetailNewBinding;
import net.suntrans.smarthome.bean.ChannelResult;
import net.suntrans.smarthome.fragment.mh.LinkageFragment;
import net.suntrans.smarthome.fragment.mh.SenceFragment;
import net.suntrans.smarthome.fragment.room.RoomDetailFragment;
import net.suntrans.smarthome.utils.StatusBarCompat;
import net.suntrans.smarthome.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Looney on 2017/5/2.
 */

public class RoomDetailActivity extends BasedActivity {
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
    }

    private void init() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_roomdetail_new);
        house_id = getIntent().getStringExtra("house_id");
        name = getIntent().getStringExtra("name");
        subname = getIntent().getStringExtra("subname");
        String url = getIntent().getStringExtra("imgurl");

        StatusBarCompat.fixKITKATbar(this, R.color.colorPrimary);

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.img_homepage)
                .centerCrop()
                .into(binding.bg);
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        binding.collapsingToolbar.setTitle(name);


        RoomDetailFragment fragment = new RoomDetailFragment();
//        SenceFragment fragment1 = new SenceFragment();
//        LinkageFragment fragment2 = new LinkageFragment();
        fragments = new Fragment[]{fragment};
        binding.tablayout.setTabMode(TabLayout.MODE_FIXED);
        binding.tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(adapter);
        binding.tablayout.setupWithViewPager(binding.viewpager);
        binding.tablayout.setVisibility(View.GONE);
//        binding.viewpager.setOffscreenPageLimit(3);
//        RxBus.getInstance().toObserverable(String.class)
//                .compose(this.<String>bindToLifecycle())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<String>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//
//                    }
//                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sense) {
            Intent intent = new Intent(RoomDetailActivity.this, AddModelActivity.class);
            intent.putExtra("type", "sence");
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.room) {
            Intent intent = new Intent(RoomDetailActivity.this, AddRoomDevGrpActivity.class);
            intent.putExtra("type", "room");
            intent.putExtra("id",house_id);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.liandong) {
            Intent intent = new Intent(RoomDetailActivity.this, AddLinkageActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.setting) {
            Intent intent = new Intent(this, EditRoomActivity.class);
            intent.putExtra("title", name);
            intent.putExtra("imgurl", getIntent().getStringExtra("imgurl"));
            intent.putExtra("id", house_id);
            startActivity(intent);
        }else if (item.getItemId() == android.R.id.home){
            supportFinishAfterTransition();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class PagerAdapter extends FragmentPagerAdapter {

        private final String[] title = new String[]{"设备", "场景", "联动"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }
}
