package net.suntrans.suntranssmarthome.homepage.myhome.edit;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.add.AddLinkageFragment;
import net.suntrans.suntranssmarthome.utils.LogUtil;

import static com.tencent.bugly.crashreport.crash.c.i;

/**
 * Created by Looney on 2017/5/20.
 */

public class EditLinkageActivity extends BasedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_link);
        setUpToolbar();
        AddLinkageFragment fragment = new AddLinkageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment).commit();
    }


    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_link);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lqueding,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.queding){
            LogUtil.i("确定点击了");
        }
        return super.onOptionsItemSelected(item);
    }
}
