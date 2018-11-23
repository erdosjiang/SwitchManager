package com.erdosjiang.baidu.yuyin.listener;

import android.os.Handler;

import com.baidu.aip.asrwakeup3.core.recog.IStatus;
import com.baidu.aip.asrwakeup3.core.wakeup.SimpleWakeupListener;
import com.baidu.aip.asrwakeup3.core.wakeup.WakeUpResult;
import com.erdosjiang.baidu.yuyin.MainActivity;

public class JWakeupListener extends SimpleWakeupListener implements IStatus {

    private static final String TAG = "JWakeupListener";

    private Handler handler;

    public JWakeupListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onSuccess(String word, WakeUpResult result) {
        super.onSuccess(word, result);
        JRecognizeListener.WAKEUP_WORD = word;
        //MainActivity.speaker.speak("我在呢,请说!");
        handler.sendMessage(handler.obtainMessage(STATUS_WAKEUP_SUCCESS));
    }
}