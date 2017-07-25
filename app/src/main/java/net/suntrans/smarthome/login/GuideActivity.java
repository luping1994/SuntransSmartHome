package net.suntrans.smarthome.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.suntrans.smarthome.App;
import net.suntrans.smarthome.MainActivity;
import net.suntrans.smarthome.R;
import net.suntrans.smarthome.base.BasedActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Looney on 2017/7/13.
 */

public class GuideActivity extends BasedActivity {
    private final int[] res = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        List<View> list = new ArrayList<>();
        for (int i = 0; i < res.length; i++) {
            View view = View.inflate(this, R.layout.item_guide, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.img);
            imageView.setImageResource(res[i]);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewPager.getCurrentItem() == 2) {
                        check();
                    }
                }
            });
            if (i == res.length - 1) {
                View view1 = view.findViewById(R.id.bt);
                view1.setVisibility(View.VISIBLE);
            }
            list.add(view);
        }
        PagerAdapter pagerAdapter = new PagerAdapter(list);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class PagerAdapter extends android.support.v4.view.PagerAdapter {
        private List<View> list;

        @Override
        public int getCount() {

            if (list != null && list.size() > 0) {
                return list.size();
            } else {
                return 0;
            }
        }

        public PagerAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


    }

    private boolean ischeck = false;

    private void check() {
        if (ischeck) {
            return;
        }
        ischeck = true;
        try {
            String access_token = App.getSharedPreferences().getString("access_token", "-1");
            String expires_in = App.getSharedPreferences().getString("expires_in", "-1");
            String user_id = App.getSharedPreferences().getString("user_id", "-1");

            long firsttime = App.getSharedPreferences().getLong("firsttime", -1l);
            long currenttime = System.currentTimeMillis();
            long d = (currenttime - firsttime) / 1000;

            if (access_token.equals("-1") || expires_in.equals("-1") || firsttime == -1l || user_id.equals("-1")) {
                handler.sendEmptyMessageDelayed(START_LOGIN, 0);
            } else {
                if (d > (Long.valueOf(expires_in) - 1 * 24 * 3600)) {
                    handler.sendEmptyMessageDelayed(START_LOGIN, 0);
                } else {
                    handler.sendEmptyMessageDelayed(START_MAIN, 0);
                }
            }
        } catch (Exception e) {
            handler.sendEmptyMessageDelayed(START_LOGIN, 0);
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    final int START_LOGIN = 0;
    final int START_MAIN = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == START_LOGIN) {
                startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                finish();
            }
            if (msg.what == START_MAIN) {
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                App.getSharedPreferences().edit().putBoolean("isfrist", false).commit();
                finish();
            }
        }
    };

}
