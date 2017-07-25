package net.suntrans.smarthome.activity.mh;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.SensusResult;
import net.suntrans.smarthome.fragment.mh.Env_fragment;

import static com.iflytek.sunflower.config.a.m;

/**
 * Created by Looney on 2017/5/15.
 */

public class EnvDetailActivity extends BasedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_envdetail);
        findViewById(R.id.nav_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Env_fragment fragment = Env_fragment.newInstance((SensusResult.Sensus) getIntent().getParcelableExtra("info"));
        getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment).commit();
    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
