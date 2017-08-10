package net.suntrans.smarthome.activity.room;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

public class RoomDetailActivity extends BasedActivity implements View.OnClickListener {

    private List<ChannelResult.Channel> datas = new ArrayList<>();
    private String house_id;
    private LoadingDialog dialog;
    private String name;
    private String subname;
    private ActivityRoomdetailNewBinding binding;
    public static final String TRANSITION_SLIDE_BOTTOM = "SLIDE_BOTTOM";
    public static final String EXTRA_TRANSITION = "EXTRA_TRANSITION";
    private Fragment[] fragments;


    private static final int REQUEST_CODE_CHOOSE = 101;
    private static final int CAPTURE_RESULT = 102;
    private static final int CUT_OK = 103;
    private static final String TAG = "RoomDetailActivity";
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
        binding.bg.setOnClickListener(this);
//        binding.collapsingToolbar.setTitle("");
//        TextView title = (TextView) findViewById(R.id.title);
//        title.setText(name);
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
            intent.putExtra("id", house_id);
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
        } else if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private String[] items = {"更改背景", "修改名称"};

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            break;
                        case 1:
                            break;
                    }
                }
            });
            builder.create().show();
            ;
        }
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

    private void openBottomDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_pic, null, false);
        dialog.setContentView(view);
        view.findViewById(R.id.paizhao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_RESULT);
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tuku).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开图库
                Intent intent = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_CHOOSE);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.canel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
