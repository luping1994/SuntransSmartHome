package net.suntrans.suntranssmarthome.homepage.myhome.add;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/4/26.
 */

public class AddModelActivity extends BasedActivity implements AddContract.View {

    private static final int REQUEST_CODE_CHOOSE = 101;
    private static final int CAPTURE_RESULT = 102;
    private static final int CUT_OK = 103;
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
        String path = UiUtils.getRealFilePath(this, imageUri);
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

//                Matisse.from(AddModelActivity.this)
//                        .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
//                        .countable(true)
//                        .maxSelectable(1)
////                        .addFilter(new GifSizeFilter(UiUtils.getDisplaySize(AddModelActivity.this)[0], UiUtils.dip2px(216), 5 * Filter.K * Filter.K))
//                        .gridExpectedSize(UiUtils.getDisplaySize(getApplicationContext())[0] / 3)
//                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                        .capture(true)
//                        .captureStrategy(new CaptureStrategy(true, "net.suntrans.suntranssmarthome.xml.provider_paths"))
//                        .thumbnailScale(0.5f)
//                        .imageEngine(new GlideEngine())
//                        .forResult(REQUEST_CODE_CHOOSE);
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

            cropImageUri(uri, 200, 100, CUT_OK);


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
                    uri = Uri.fromFile(tempFile);
                } else {
                    UiUtils.showToast("没有储存卡");
                }
            }

            cropImageUri(uri, 200, 100, CUT_OK);
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
    }

    String imgId;


    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";//temp file
    Uri imageUri = null;
    private File path = new File(Environment.getExternalStorageDirectory(), "suntrans");

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        if (uri == null)
            return;
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");

        intent.putExtra("aspectX", 2);

        intent.putExtra("aspectY", 1);

//        intent.putExtra("data","uri");
        intent.putExtra("outputX", outputX);

        intent.putExtra("outputY", outputY);

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
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        imageUri = null;
        imageUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.putExtra("noFaceDetection", true); // no face detection

        startActivityForResult(intent, requestCode);

    }
}
