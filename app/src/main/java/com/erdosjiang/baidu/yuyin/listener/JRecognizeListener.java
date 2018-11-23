package com.erdosjiang.baidu.yuyin.listener;

import android.os.Handler;
import android.os.Message;

import com.baidu.aip.asrwakeup3.core.recog.RecogResult;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;
import com.baidu.aip.asrwakeup3.core.util.MyLogger;
import com.erdosjiang.baidu.yuyin.config.Constants;


public class JRecognizeListener extends MessageStatusRecogListener {
    public JRecognizeListener(Handler handler) {
        this.handler=handler;
    }

    public static String WAKEUP_WORD = "";
    private static final int HTTP_OK=200;

    private boolean needNetwork = false;

    private Handler handler;

    @Override
    public void onAsrFinalResult(String[] results, RecogResult recogResult) {
        super.onAsrFinalResult(results, recogResult);

        String resultMsg = results[0];
        MyLogger.info( "识别结束，结果是111”" + resultMsg + "”");
        resultMsg = resultMsg.replace(JRecognizeListener.WAKEUP_WORD,"");
        //MainActivity.speaker.speak("你是说:" + resultMsg+",对吗");
        MyLogger.info( "识别结束，结果是222”" + resultMsg + "”");
        Message msg = new Message();
        msg.what = Constants.STATUS_RECOG_SUCCESS_RESULT;
        msg.obj = resultMsg;
        handler.sendMessage(msg);
    }
}
