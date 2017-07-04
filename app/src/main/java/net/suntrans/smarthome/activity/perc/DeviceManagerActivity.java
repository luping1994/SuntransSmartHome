package net.suntrans.smarthome.activity.perc;

import android.os.Bundle;
import android.view.View;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.fragment.perc.DevicesManagerFragment;

/**
 * Created by Looney on 2017/4/20.
 */

public class DeviceManagerActivity extends BasedActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicemanager);
        DevicesManagerFragment fragment = DevicesManagerFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment).commit();
        setUpToolbar();
        init();
    }

    private void init() {

    }

    private void setUpToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(R.string.title_modify_pass);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {

    }
}
