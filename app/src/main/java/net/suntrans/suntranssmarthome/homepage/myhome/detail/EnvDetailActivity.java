package net.suntrans.suntranssmarthome.homepage.myhome.detail;

import android.os.Bundle;
import android.view.View;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.homepage.device.sensus.SensusResult;

/**
 * Created by Looney on 2017/5/15.
 */

public class EnvDetailActivity extends BasedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        overridePendingTransition(0, android.support.v7.appcompat.R.anim.abc_slide_out_bottom);
    }
}
