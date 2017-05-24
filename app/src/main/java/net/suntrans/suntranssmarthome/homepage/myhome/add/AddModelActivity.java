package net.suntrans.suntranssmarthome.homepage.myhome.add;

import android.Manifest;
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
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import net.suntrans.suntranssmarthome.BuildConfig;
import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.api.RetrofitHelper;
import net.suntrans.suntranssmarthome.base.BasedActivity;
import net.suntrans.suntranssmarthome.databinding.ActivityAddModeBinding;
import net.suntrans.suntranssmarthome.utils.LogUtil;
import net.suntrans.suntranssmarthome.utils.UiUtils;
import net.suntrans.suntranssmarthome.widget.LoadingDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.tencent.bugly.crashreport.crash.c.i;

/**
 * Created by Looney on 2017/4/26.
 */

public class AddModelActivity extends BasedActivity implements AddContract.View, EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE_CHOOSE = 101;
    private static final int CAPTURE_RESULT = 102;
    private static final int CUT_OK = 103;
    private static final String TAG = "AddModelActivity";
    private ActivityAddModeBinding binding;
    private AddPresenter presenter;
    private ImageView imageView;
    private LoadingDialog dialog;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_mode);
        presenter = new AddPresenter(this);
        binding.setActionHandler(presenter);
        imageView = (ImageView) findViewById(R.id.mode_img);
        type = getIntent().getStringExtra("type");
        binding.baseTvSceneTitle.setText(type.equals("sence") ? getString(R.string.title_add_sence) : getString(R.string.title_add_room));
        dialog = new LoadingDialog(this, R.style.loading_dialog);
        dialog.setWaitText(getString(R.string.dialog_tips_creating));
        dialog.setCancelable(false);
    }

    @Override
    public void setPresenter(AddContract.Presenter presenter) {

    }

    @Override
    public void onCreateModel() {
        final String name = binding.modeName.getText().toString();

        if (TextUtils.isEmpty(name)) {
            UiUtils.showMessage(findViewById(android.R.id.content), getString(R.string.tips_name_empty));
            return;
        }
        dialog.show();
        if (imageUri == null) {
            createModel(name, "1");
        } else {
            upLoad(name);
        }
    }

    private void upLoad(final String name) {
        LogUtil.i(imageUri.toString());
        String path = "";
        path = Environment.getExternalStorageDirectory() + "/suntrans/temp.jpg";

        LogUtil.i(path);
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
                    }

                    @Override
                    public void onNext(UpLoadImageMessage info) {
                        if (info != null) {
                            if (info.error.equals("0")) {
                                LogUtil.i("图片上传成功！id为：" + info.id);
                                createModel(name, info.id);

                            } else {
                                UiUtils.showToast(getString(R.string.tips_upload_failed));
                            }
                        }
                    }
                });
    }

    private void createModel(String name, String imgId) {
        Observable<CreateModelResult> observable = null;
        if (type.equals("sence")) {
            observable = RetrofitHelper.getApi().addScene(name, imgId);

        } else {
            observable = RetrofitHelper.getApi().addHouse(name, imgId);
        }

        observable.compose(AddModelActivity.this.<CreateModelResult>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CreateModelResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UiUtils.showToast("创建失败!");
                        dialog.dismiss();

                    }

                    @Override
                    public void onNext(CreateModelResult result) {
                        if (result != null)
                            UiUtils.showToast(result.msg);
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void onOpenGallery() {
        camaraAndStrogetask();

    }

//    List<Uri> mSelected;

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

            cropImageUri(uri, UiUtils.dip2px(100), UiUtils.dip2px(100), CUT_OK);


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
                        uri = FileProvider.getUriForFile(getApplicationContext(), "net.suntrans.suntranssmarthome.fileProvider", tempFile);
                    } else {
                        uri = Uri.fromFile(tempFile);
                    }
                } else {
                    UiUtils.showToast("没有储存卡");
                }
            }

            cropImageUri(uri, UiUtils.dip2px(100), UiUtils.dip2px(100), CUT_OK);
        }
        if (requestCode == CUT_OK && resultCode == RESULT_OK) {
            Uri uri = imageUri;
            if (uri == null)
                return;
            LogUtil.i("裁剪成功，填充图片");
            LogUtil.i(uri.toString());

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Glide.with(AddModelActivity.this)
//                    .load(uri)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(false)
//                    .centerCrop()
//                    .override(1080, 696)
//                    .into(imageView);
        } else if (requestCode == CUT_OK && resultCode != RESULT_OK) {
            LogUtil.i("裁剪失败");
            imageUri = null;
        }
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
        } else {
        }
    }

    String imgId;


    Uri imageUri = null;
    private File path = new File(Environment.getExternalStorageDirectory(), "suntrans");

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        if (uri == null)
            return;
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");

        intent.putExtra("aspectX", 1);

        intent.putExtra("aspectY", 1);

//        intent.putra("data","uri");
        intent.putExtra("outputX", UiUtils.dip2px(100));

        intent.putExtra("outputY", UiUtils.dip2px(100));

        intent.putExtra("scale", true);

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
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imageUri = FileProvider.getUriForFile(getApplicationContext(), "net.suntrans.suntranssmarthome.fileProvider", file);
        } else {
            imageUri = Uri.fromFile(file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);


        LogUtil.i(imageUri.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
        startActivityForResult(intent, requestCode);
    }

    private static final int RC_CAMARE_STORAGE_PERM = 124;

    @AfterPermissionGranted(RC_CAMARE_STORAGE_PERM)
    public void camaraAndStrogetask() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            openButtomDialog();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
                    RC_CAMARE_STORAGE_PERM, perms);
        }
    }

    private void openButtomDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_pic, null, false);
        dialog.setContentView(view);
        view.findViewById(R.id.paizhao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_RESULT);
                dialog.dismiss();
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
        LogUtil.i("openGallery");
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
