package net.suntrans.smarthome.activity.mh;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.trello.rxlifecycle.android.ActivityEvent;

import net.suntrans.smarthome.App;
import net.suntrans.smarthome.R;
import net.suntrans.smarthome.api.RetrofitHelper;
import net.suntrans.smarthome.base.BasedActivity;
import net.suntrans.smarthome.bean.HomeSceneResult;
import net.suntrans.smarthome.bean.SpeechBean;
import net.suntrans.smarthome.bean.VoiceResponse;
import net.suntrans.smarthome.utils.JsonParser;
import net.suntrans.smarthome.utils.UiUtils;
import net.suntrans.smarthome.websocket.WebSocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/6/17.
 */

public class SpeechActivity extends BasedActivity {
    private static String TAG = SpeechActivity.class.getSimpleName();
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private SharedPreferences mSharedPreferences;
    private List<SpeechBean> datas = new ArrayList<>();
    private MyAdapter adapter;


    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 默认发音人
    private String voicer = "nannan";


    private String userid;

    private WebSocketService.ibinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (WebSocketService.ibinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        setUpToolbar();
        init();
    }

    private void init() {
        Intent intent = new Intent();
        intent.setClass(this, WebSocketService.class);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        userid = App.getSharedPreferences().getString("user_id", "-1");

        mIat = SpeechRecognizer.createRecognizer(SpeechActivity.this, mInitListener);

        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(SpeechActivity.this, mInitListener);


        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(SpeechActivity.this, mInitListener);
        mSharedPreferences = App.getSharedPreferences();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new MyAdapter(R.layout.item_speech, datas);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_sppech);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    int ret = 0; // 函数调用返回值

    public void start(View view) {
        // 移动数据分析，收集开始听写事件
        FlowerCollector.onEvent(App.getApplication(), "iat_recognize");
        mIatResults.clear();
        // 设置参数
        // 设置参数
        setParam();
        boolean isShowDialog = mSharedPreferences.getBoolean(
                getString(R.string.pref_key_iat_show), true);
        if (isShowDialog) {
            // 显示听写对话框
            mIatDialog.setListener(mRecognizerDialogListener);
            mIatDialog.show();
            UiUtils.showToast(getString(R.string.text_begin));
        } else {
            // 不显示听写对话框
            ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                UiUtils.showToast("听写失败,错误码：" + ret);
            } else {
                UiUtils.showToast(getString(R.string.text_begin));
            }
        }

    }

    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "0"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    /**
     * 参数设置
     *
     * @param
     * @return
     */
    private void setParamTts() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                UiUtils.showToast("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            UiUtils.showToast(error.getPlainDescription(true));
        }

    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        mIat.stopListening();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        System.out.println(resultBuffer);
        SpeechBean speechBean = new SpeechBean("0", resultBuffer.toString());
        datas.add(speechBean);
        adapter.notifyDataSetChanged();

        RetrofitHelper.getApi().getSceneByVoice(resultBuffer.toString())
                .compose(this.<VoiceResponse>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<VoiceResponse>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(VoiceResponse s) {
                        String s1 = "";
                        if (s.status.equals("0")) {
                            SpeechBean speechBean = new SpeechBean("1", s.msg);
                            datas.add(speechBean);
                            s1 = s.msg;
                            adapter.notifyDataSetChanged();
                        } else if (s.status.equals("1")) {
                            HomeSceneResult.Scene result = s.result;
                            s1 = "正在为您执行" + result.name;
                            SpeechBean speechBean = new SpeechBean("1", s1);
                            datas.add(speechBean);
                            adapter.notifyDataSetChanged();
                            excuteScene(s.result.id,s.result.name);
                        }
                        // 移动数据分析，收集开始合成事件
                        FlowerCollector.onEvent(SpeechActivity.this, "tts_play");
                        // 设置参数
                        setParamTts();
                        int code = mTts.startSpeaking(s1, mTtsListener);

                        if (code != ErrorCode.SUCCESS) {

                            UiUtils.showToast("语音合成失败,错误码: " + code);
                        }

                    }
                });
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            UiUtils.showToast("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            UiUtils.showToast(error.getPlainDescription(true));
            SpeechBean speechBean2 = new SpeechBean("1", "你好像没有说话");
            datas.add(speechBean2);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            UiUtils.showToast("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
//            printResult(results);

            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            UiUtils.showToast("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };


    class MyAdapter extends BaseQuickAdapter<SpeechBean, BaseViewHolder> {

        public MyAdapter(@LayoutRes int layoutResId, @Nullable List<SpeechBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SpeechBean item) {
            TextView tv1 = helper.getView(R.id.server);
            TextView tv2 = helper.getView(R.id.my);
            if (item.getType().equals("0")) {
                tv1.setVisibility(View.GONE);
                tv2.setVisibility(View.VISIBLE);
                tv2.setText(item.getMsg());
            } else {
                tv2.setVisibility(View.GONE);
                tv1.setVisibility(View.VISIBLE);
                tv1.setText(item.getMsg());
            }
        }
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {

        }

        @Override
        public void onCompleted(SpeechError error) {

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    @Override
    protected void onDestroy() {
        mIat.cancel();
        mIat.destroy();
        mTts.stopSpeaking();
        mTts.destroy();
        unbindService(connection);
        super.onDestroy();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

    private void excuteScene(String id,String scene){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("device", "scene");
            jsonObject.put("scene_id",Integer.valueOf(id));
            jsonObject.put("user_id",Integer.valueOf(userid));
            if (binder.sendOrder(jsonObject.toString())){
                SpeechBean speechBean = new SpeechBean("1", "已经为您执行了"+scene);
                datas.add(speechBean);
                adapter.notifyDataSetChanged();

            }else {
                UiUtils.showToast("场景执行失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
