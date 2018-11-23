package com.erdosjiang.baidu.yuyin.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
import com.baidu.aip.asrwakeup3.core.wakeup.IWakeupListener;
import com.baidu.aip.asrwakeup3.core.wakeup.MyWakeup;
import com.baidu.speech.asr.SpeechConstant;
import com.erdosjiang.baidu.yuyin.config.Constants;
import com.erdosjiang.baidu.yuyin.listener.JRecognizeListener;
import com.erdosjiang.baidu.yuyin.listener.JWakeupListener;
import com.erdosjiang.baidu.yuyin.recognizer.JRecognizer;
import com.erdosjiang.baidu.yuyin.utils.JDataTool;
import com.erdosjiang.baidu.yuyin.utils.JHttpTool;
import com.erdosjiang.baidu.yuyin.utils.JSmartHomeTool;
import com.erdosjiang.baidu.yuyin.utils.JTTSTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.baidu.aip.asrwakeup3.core.recog.IStatus.STATUS_WAKEUP_SUCCESS;
import static java.net.HttpURLConnection.HTTP_OK;

public class JWakeUpService extends Service {
    private static final String TAG = "JWakeUpService";
    private JTTSTool jttsTool;      //本服务专用语音合成器
    private static MyWakeup myWakeup;      //唤醒词监听
    private static JRecognizer myRecognizer;      //语音动作识别
    private Handler handler;       //消息处理
    private JBinder binder = new JBinder();

    public class JBinder extends Binder {
        public JWakeUpService getService(){
            return JWakeUpService.this;
        }
    }

    public JWakeUpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"oncreate");
        //1.初始化消息处理器
        handler = new Handler() {

            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }

        };
        //2.初始化语音动作识别
        IRecogListener recogListener = new JRecognizeListener(handler);
        // 改为 SimpleWakeupListener 后，不依赖handler，但将不会在UI界面上显示
        myRecognizer = new JRecognizer(this, recogListener);

        //3.初始化唤醒工具
        IWakeupListener listener = new JWakeupListener(handler);
        myWakeup = new MyWakeup(this, listener);

        //4.初始化语音合成工具
        jttsTool = new JTTSTool(this);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myWakeup.stop();
        Log.d(TAG,"onstartcommand");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        params.put(SpeechConstant.APP_ID,"14809951");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        // params.put(SpeechConstant.ACCEPT_AUDIO_DATA,true);
        // params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME,true);
        // params.put(SpeechConstant.IN_FILE,"res:///com/baidu/android/voicedemo/wakeup.pcm");
        // params里 "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下
        jttsTool.speak("后台唤醒服务已启动,随时为您服务!");
        myWakeup.start(params);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"服务被退出");
        jttsTool.speak("哎呀,我被退出了");
        handler = null;
        myRecognizer.release();
        myWakeup.release();
        jttsTool.release();
        super.onDestroy();
    }

    private void handleMsg(Message msg) {

        switch (msg.what){
            case STATUS_WAKEUP_SUCCESS:
                Map<String, Object> params = new LinkedHashMap<String, Object>();
                params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
                params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
                // 如识别短句，不需要需要逗号，使用1536搜索模型。其它PID参数请看文档
                params.put(SpeechConstant.PID, 1536);
//            if (backTrackInMs > 0) {
//                // 方案1  唤醒词说完后，直接接句子，中间没有停顿。开启回溯，连同唤醒词一起整句识别。
//                // System.currentTimeMillis() - backTrackInMs ,  表示识别从backTrackInMs毫秒前开始
//                params.put(SpeechConstant.AUDIO_MILLS, System.currentTimeMillis() - backTrackInMs);
//            }
                myRecognizer.cancel();
                jttsTool.speak("请说");

                myRecognizer.start(params);
                break;
            case Constants.STATUS_RECOG_SUCCESS_RESULT:
                myRecognizer.cancel();
                String resultMsg = (String) msg.obj;
                //1.根据语音结果在数据库中查找对应的报文
                String data = JDataTool.findByteData(resultMsg);
                //2.智能主机控制工具执行报文
                String rtMsg = JSmartHomeTool.doAction(data);
                //3.播放执行结果
                jttsTool.speak(rtMsg);
                Map<String, Object> params1 = new LinkedHashMap<String, Object>();
                params1.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
                params1.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
                // 如识别短句，不需要需要逗号，使用1536搜索模型。其它PID参数请看文档
                params1.put(SpeechConstant.PID, 1536);
                myRecognizer.start(params1);
                break;
        }

    }

}
