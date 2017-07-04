package net.suntrans.smarthome.activity.perc.detail;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.base.BasedActivity;


/**
 * Created by Looney on 2017/4/21.
 */

public class AddDeviceActivity extends BasedActivity {
    private Animation animation;
    private ImageView imageView;
    private TextView txState;
    RelativeLayout rlNoDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        animation = AnimationUtils.loadAnimation(this, R.anim.scan_image);
        LinearInterpolator interpolator = new LinearInterpolator();
        animation.setInterpolator(interpolator);

        findViewById(R.id.re_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        rlNoDevice = (RelativeLayout) findViewById(R.id.rl_noDevice);
        txState = (TextView) findViewById(R.id.tx_state);
        imageView = (ImageView) findViewById(R.id.imageView);
        startScan();

    }

    private void startScan() {
        rlNoDevice.setVisibility(View.INVISIBLE);
        handler.removeCallbacksAndMessages(null);
        imageView.startAnimation(animation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanDevice();
            }
        }, 2000);
    }

    private void stopScanDevice() {
        imageView.clearAnimation();

        txState.setText(getString(R.string.noscanresult));
        rlNoDevice.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    Handler handler = new Handler();
}
