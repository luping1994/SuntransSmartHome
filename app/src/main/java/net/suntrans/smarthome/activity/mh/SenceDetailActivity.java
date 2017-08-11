package net.suntrans.smarthome.activity.mh;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;

import net.suntrans.smarthome.App;
import net.suntrans.smarthome.R;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.CmdMsg;
import net.suntrans.smarthome.bean.CreateModelResult;
import net.suntrans.smarthome.bean.SceneChannelResult;
import net.suntrans.smarthome.bean.UpLoadImageMessage;
import net.suntrans.smarthome.databinding.ActivityHousedetailBinding;
import net.suntrans.smarthome.utils.LogUtil;
import net.suntrans.smarthome.utils.ParseCMD;
import net.suntrans.smarthome.utils.RxBus;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.websocket.WebSocketService;
import net.suntrans.smarthome.widget.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static net.suntrans.smarthome.R.id.msg;

/**
 * Created by Looney on 2017/4/29.
 */

public class SenceDetailActivity extends BasedActivity implements View.OnClickListener ,EasyPermissions.PermissionCallbacks{

    private final String TAG = "SenceDetailActivity";
    private ActivityHousedetailBinding binding;
    private String name;
    private String imgurl;
    private String id;
    private List<SceneChannelResult.SceneChannel> datas;
    private MyAdapter adapter;
    private LoadingDialog dialog;

    private String userid;

    private WebSocketService.ibinder binder;

    private static final int REQUEST_CODE_CHOOSE = 101;
    private static final int CAPTURE_RESULT = 102;
    private static final int CUT_OK = 103;
    private Bitmap bitmap;

    private int width;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (WebSocketService.ibinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private Subscription subscribe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_housedetail);
        binding.modeImg.setOnClickListener(this);

        Intent intent = new Intent();
        intent.setClass(this, WebSocketService.class);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;


        name = getIntent().getStringExtra("title");
        binding.title.setText(name);
        binding.edit.setOnClickListener(this);
        imgurl = getIntent().getStringExtra("imgurl");

        Glide.with(this)
                .load(imgurl)
                .placeholder(R.drawable.img_original_bg_custom)
                .into(binding.modeImg);
        id = getIntent().getStringExtra("id");
        LogUtil.i(TAG, "id=" + id);
        LogUtil.i(TAG, "imgurl=" + imgurl);
        userid = App.getSharedPreferences().getString("user_id", "-1");

        datas = new ArrayList<>();
        adapter = new MyAdapter(R.layout.item_scene_channel, datas);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.baseIvSceneBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });

        subscribe = RxBus.getInstance().toObserverable(CmdMsg.class)
                .compose(this.<CmdMsg>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CmdMsg>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CmdMsg cmdMsg) {
                        if (cmdMsg.status == 1) {
                            parseMsg(cmdMsg.msg);
                        }
                    }
                });
    }

    private String[] items = {"更改背景", "修改名称"};
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.edit) {
            Intent intent = new Intent(this, EditSenceActivity.class);
            intent.putExtra("title", name);
            intent.putExtra("imgurl", imgurl);
            intent.putExtra("id", getIntent().getStringExtra("id"));
            intent.putExtra("count", datas.size());
            startActivity(intent);
        }
        if (v.getId() == R.id.hand_add) {

        }
        if (v.getId() == R.id.mode_img){
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

    private static long INTERVAL=3000L;
    private static long lastTime = 0;
    public void excute(View view) {
        long time = System.currentTimeMillis();
        if (time - lastTime>INTERVAL){
//            System.out.println("sb");
            lastTime =time;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("device", "scene");
                jsonObject.put("scene_id",Integer.valueOf(id));
                jsonObject.put("user_id",Integer.valueOf(userid));
                if (binder.sendOrder(jsonObject.toString())){
                    UiUtils.showToast("已经为您执行改场景");

                }else {
                    UiUtils.showToast("执行失败");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            UiUtils.showToast("正在执行中");
        }
//        //{ "device": "scene", "scene_id": 1,"user_id":123}

    }

    class MyAdapter extends BaseQuickAdapter<SceneChannelResult.SceneChannel, BaseViewHolder> {


        public MyAdapter(@LayoutRes int layoutResId, @Nullable List<SceneChannelResult.SceneChannel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SceneChannelResult.SceneChannel item) {
            helper.setText(R.id.name, item.name)
                    .setText(R.id.action, item.status.equals("1") ? getString(R.string.channel_choose_open) : getString(R.string.channel_choose_close));
            TextView state = helper.getView(R.id.action);
            if (item.status.equals("1"))
                state.setTextColor(getResources().getColor(R.color.colorPrimary));
            else
                state.setTextColor(getResources().getColor(R.color.colorAccent));

            ImageView imageView = helper.getView(R.id.img);
            imageView.setVisibility(View.GONE);
//
//            Glide.with(SenceDetailActivity.this)
//                    .load(item.img)
//                    .crossFade()
//                    .centerCrop()
//                    .placeholder(R.drawable.ic_light)
//                    .into(imageView);
        }
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    private void getData() {
        RetrofitHelper.getApi().getSceneChannel(id)
                .compose(this.<SceneChannelResult>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SceneChannelResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SceneChannelResult result) {
                        datas.clear();
                        datas.addAll(result.result);
                        adapter.notifyDataSetChanged();
                        LogUtil.i("场景动作的数量：" + datas.size());
                        if (datas.size() == 0) {
                            binding.tipEditModeNodevice.setVisibility(View.VISIBLE);
                        } else {
                            binding.tipEditModeNodevice.setVisibility(View.GONE);

                        }
                    }
                });
    }

    private void parseMsg(String msg1) {
        try {
            JSONObject jsonObject = new JSONObject(msg1);
            String code = jsonObject.getString("code");
            if (code.equals("200")) {
                JSONObject result = jsonObject.getJSONObject("result");
                int channel = result.getInt("channel");
                int command = result.getInt("command");
                String device = result.getString("device");
                String addr = result.getString("addr");
                Map<String, String> map = ParseCMD.check((short) channel, (short) command);

                for (int i = 0; i < datas.size(); i++) {

                }


            } else if (code.equals("404")) {

            } else if (code.equals("101")) {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        if (subscribe != null) {
            if (!subscribe.isUnsubscribed()) {
                subscribe.unsubscribe();
            }
        }
        super.onDestroy();
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

    private void modifyImage(final String imgid) {
        if (TextUtils.isEmpty(imgid)){
            UiUtils.showToast("id不能为空");
            return;
        }

        Map<String,String> map = new HashMap<>();
        map.put("scene_id",id);
        map.put("img_id",imgid);
        map.put("name",name);
        RetrofitHelper.getApi().updateScene(map)
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
                            binding.modeImg.setImageBitmap(bitmap);
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
        map.put("scene_id",id);
        map.put("name",names);
        RetrofitHelper.getApi().updateScene(map)
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
