package net.suntrans.smarthome.activity.perc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import net.suntrans.smarthome.BuildConfig;
import net.suntrans.smarthome.R;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.utils.UiUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Looney on 2017/4/20.
 */

public class AboutActivity extends BasedActivity {
    private TextView guangwang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setUpToolbar();
        initView();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_about);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        guangwang = (TextView) findViewById(R.id.guangwang);
        guangwang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.suntrans.net/");
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        TextView textView = (TextView) findViewById(R.id.version);
        textView .setText("版本号:"+ BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.share:
                Uri uri = Uri.parse("https://www.pgyer.com/DPTD");
                share(uri,"分享",AboutActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    Bitmap encodeAsBitmap(String str){
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 200, 200);
            // 使用 ZXing Android Embedded 要写的代码
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (WriterException e){
            e.printStackTrace();
        } catch (IllegalArgumentException iae){ // ?
            return null;
        }

        return bitmap;
    }

    public  void saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "suntrans");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "suntrans" + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            UiUtils.showToast("图片已保存到"+file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri = Uri.fromFile(file);
        Intent mIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        getApplicationContext().sendBroadcast(mIntent);
    }

    public  void share(Uri uri, String desc, Context context)
    {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"");
        shareIntent.putExtra(Intent.EXTRA_TEXT,  "HI 推荐您使用一款软件:"+"下载地址:https://www.pgyer.com/DiiB");
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, desc));
    }

}
