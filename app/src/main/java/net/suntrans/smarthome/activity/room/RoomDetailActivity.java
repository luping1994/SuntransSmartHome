package net.suntrans.smarthome.activity.room;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.suntrans.smarthome.R;
import net.suntrans.smarthome.activity.mh.AddLinkageActivity;
import net.suntrans.smarthome.activity.mh.AddModelActivity;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.CreateModelResult;
import net.suntrans.smarthome.bean.UpLoadImageMessage;
import net.suntrans.smarthome.databinding.ActivityRoomdetailNewBinding;
import net.suntrans.smarthome.bean.ChannelResult;
import net.suntrans.smarthome.fragment.mh.LinkageFragment;
import net.suntrans.smarthome.fragment.mh.SenceFragment;
import net.suntrans.smarthome.fragment.room.RoomDetailFragment;
import net.suntrans.smarthome.utils.LogUtil;
import net.suntrans.smarthome.utils.StatusBarCompat;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.widget.LoadingDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static net.suntrans.smarthome.R.id.imageView;
import static net.suntrans.smarthome.utils.UiUtils.getContext;

/**
 * Created by Looney on 2017/5/2.
 */

public class RoomDetailActivity extends BasedActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

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
    private Bitmap bitmap;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTransition();
        init();
    }

    private void applyTransition() {
    }
    private float mDensity;

    private void init() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_roomdetail_new);
        house_id = getIntent().getStringExtra("house_id");
        name = getIntent().getStringExtra("name");
        subname = getIntent().getStringExtra("subname");
        String url = getIntent().getStringExtra("imgurl");

        StatusBarCompat.fixKITKATbar(this, R.color.colorPrimary);

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.img_homepage)
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
                            openBottomDialog();
                            break;
                        case 1:
                            showModifyNameDialog();
                            break;
                    }
                }
            });
            builder.create().show();
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
                camaraAndStrogetask();
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

    private void showModifyNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_modify_channelname, null, false);
        final EditText names = (EditText) view.findViewById(R.id.channelName);
        names.setText(name);
        names.setSelection(name.length());
        builder.setPositiveButton(R.string.queding, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modifyName(names.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.qvxiao, null);
        builder.setTitle(R.string.title_modify_channelname);
        builder.setView(view);
        builder.create().show();
    }




    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            LogUtil.i("Gallery", "uri=: " + uri);
            if (uri == null) {
                Bundle extras = data.getExtras();
                Bitmap image = null;
                if (extras != null) {
                    //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                    image = extras.getParcelable("data");
                }
                // 判断存储卡是否可以用，可用进行存储
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File tempFile = new File(path, System.currentTimeMillis() + ".jpg");
                    FileOutputStream b = null;
                    try {
                        b = new FileOutputStream(tempFile);
                        image.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            b.flush();
                            b.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    uri = Uri.fromFile(tempFile);
                } else {
                    UiUtils.showToast("没有储存卡");
                    return;
                }
            }

            cropImageUri(uri, width, (int) (width/1.8), CUT_OK);


        }
        if (requestCode == CAPTURE_RESULT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            LogUtil.i("CAPTURE", "uri=: " + uri);
            if (uri == null) {
                Bundle extras = data.getExtras();
                Bitmap image = null;
                if (extras != null) {
                    //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                    image = extras.getParcelable("data");
                }
                // 判断存储卡是否可以用，可用进行存储
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File tempFile = new File(path, System.currentTimeMillis() + ".jpg");
                    FileOutputStream b = null;
                    try {
                        b = new FileOutputStream(tempFile);
                        image.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            b.flush();
                            b.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(getApplicationContext(), "net.suntrans.smarthome.fileProvider", tempFile);
                    } else {
                        uri = Uri.fromFile(tempFile);
                    }
                } else {
                    UiUtils.showToast("没有储存卡");
                }
            }

            cropImageUri(uri,  width, (int) (width/1.8), CUT_OK);
        }
        if (requestCode == CUT_OK && resultCode == RESULT_OK) {
            Uri uri = imageUri;
            if (uri == null)
                return;
            LogUtil.i(uri.toString());

            try {
                LogUtil.i("裁剪成功，填充图片");
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                imageView.setImageBitmap(bitmap);
                upLoad();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CUT_OK && resultCode != RESULT_OK) {
            LogUtil.i("裁剪失败");
            imageUri = null;
        }
    }


    Uri imageUri = null;
    private File path = new File(Environment.getExternalStorageDirectory(), "suntrans");

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        if (uri == null)
            return;
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");


        intent.putExtra("scale", false);

        intent.putExtra("aspectX", 16);

        intent.putExtra("aspectY", 9);

//        intent.putra("data","uri");
        intent.putExtra("outputX", outputX);

        intent.putExtra("outputY", outputY);

//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        intent.putExtra("return-data", false);

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        File file = new File(path, "temp.jpg");
        if (!path.exists()) {
            path.mkdirs();
        }

        if (file.exists()) {
            file.delete();
        }
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        imageUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imageUri = FileProvider.getUriForFile(getApplicationContext(), "net.suntrans.smarthome.fileProvider", file);
        } else {
            imageUri = Uri.fromFile(file);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.putExtra("noFaceDetection", true); // no face detection

        startActivityForResult(intent, requestCode);

    }


    private void upLoad() {
        if (dialog==null){
            dialog = new LoadingDialog(this,R.style.loading_dialog);
            dialog.setCancelable(false);
            dialog.setWaitText("请稍后..");
        }
        dialog.show();
        String path = Environment.getExternalStorageDirectory() + "/suntrans/temp.jpg";
        File file = new File(path);
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part imageBodyPart = MultipartBody.Part.createFormData("imgfile", file.getName(), imageBody);
        dialog.setWaitText(getString(R.string.tips_uploading));

        RetrofitHelper.getApi().upload(imageBodyPart)
                .compose(this.<UpLoadImageMessage>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UpLoadImageMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UiUtils.showToast(getString(R.string.tips_upload_failed));
                        dialog.dismiss();
                    }

                    @Override
                    public void onNext(UpLoadImageMessage info) {
                        if (info != null) {
                            if (info.error.equals("0")) {
                                LogUtil.i("图片上传成功！id为：" + info.id);
                                modifyImage(info.id);
                            } else {
                                UiUtils.showToast(getString(R.string.tips_upload_failed));
                            }
                        }
                    }
                });
    }

    private void modifyImage(final String id) {
        if (TextUtils.isEmpty(id)){
            UiUtils.showToast("id不能为空");
            return;
        }

        Map<String,String> map = new HashMap<>();
        map.put("house_id",house_id);
        map.put("img_id",id);
        map.put("name",name);
        RetrofitHelper.getApi().updateRoom(map)
                .compose(this.<CreateModelResult>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CreateModelResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();

                    }

                    @Override
                    public void onNext(CreateModelResult result) {
                        dialog.dismiss();
                        if (result.status.equals("1")){
                            UiUtils.showToast(result.msg);
                            binding.bg.setImageBitmap(bitmap);
                        }else {
                            UiUtils.showToast(result.msg);

                        }
                    }
                });
    }

    private void modifyName(final String names) {
        if (TextUtils.isEmpty(names)){
            UiUtils.showToast("名称不能为空");
            return;
        }
        if (dialog==null){
            dialog = new LoadingDialog(this,R.style.loading_dialog);
            dialog.setWaitText("请稍后..");
            dialog.setCancelable(false);
        }
        Map<String,String> map = new HashMap<>();
        map.put("house_id",house_id);
        map.put("name",names);
        RetrofitHelper.getApi().updateRoom(map)
                .compose(this.<CreateModelResult>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CreateModelResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UiUtils.showToast("更新失败");
                        dialog.dismiss();

                    }

                    @Override
                    public void onNext(CreateModelResult result) {
                        dialog.dismiss();
                        if (result.status.equals("1")){
                            UiUtils.showToast(result.msg);
                            binding.collapsingToolbar.setTitle(names);
                        }else {
                            UiUtils.showToast(result.msg);

                        }
                    }
                });
    }

    private static final int RC_CAMARE_STORAGE_PERM = 124;
    @AfterPermissionGranted(RC_CAMARE_STORAGE_PERM)
    public void camaraAndStrogetask() {
        String[] perms = { Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAPTURE_RESULT);
            dialog.dismiss();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
                    RC_CAMARE_STORAGE_PERM, perms);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        LogUtil.i(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        UiUtils.showToast("无法获取相机和存储权限");
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

}
