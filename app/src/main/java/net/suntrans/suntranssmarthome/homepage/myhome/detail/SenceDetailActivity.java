package net.suntrans.suntranssmarthome.homepage.myhome.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.databinding.ActivityHousedetailBinding;
import net.suntrans.suntranssmarthome.homepage.myhome.edit.EditRoomActivity;
import net.suntrans.suntranssmarthome.homepage.myhome.edit.EditSenceActivity;
import net.suntrans.suntranssmarthome.utils.UiUtils;

import static net.suntrans.suntranssmarthome.R.id.imageView;

/**
 * Created by Looney on 2017/4/29.
 */

public class SenceDetailActivity extends BasedActivity implements View.OnClickListener {

    private ActivityHousedetailBinding binding;
    private String title;
    private String imgurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_housedetail);
        title = getIntent().getStringExtra("title");
        binding.title.setText(title);
        binding.edit.setOnClickListener(this);
        binding.handAdd.setOnClickListener(this);
        imgurl = getIntent().getStringExtra("imgurl");
        Glide.with(this)
                .load(imgurl)
                .placeholder(R.drawable.img_original_bg_sleep)
                .centerCrop()
                .into(binding.modeImg);

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.edit){
            Intent intent = new Intent(this, EditSenceActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("imgurl", imgurl);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            startActivity(intent);
        }
        if (v.getId() == R.id.hand_add){

        }
    }

    public void excute(View view){
        UiUtils.showToast("执行中");
    }
}
