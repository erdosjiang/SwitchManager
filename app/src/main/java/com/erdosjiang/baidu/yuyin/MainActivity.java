package com.erdosjiang.baidu.yuyin;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baidu.aip.asrwakeup3.core.recog.IStatus;
import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
import com.baidu.aip.asrwakeup3.core.wakeup.IWakeupListener;
import com.baidu.aip.asrwakeup3.core.wakeup.MyWakeup;
import com.baidu.speech.asr.SpeechConstant;
import com.erdosjiang.baidu.yuyin.listener.JRecognizeListener;
import com.erdosjiang.baidu.yuyin.listener.JWakeupListener;
import com.erdosjiang.baidu.yuyin.recognizer.JRecognizer;
import com.erdosjiang.baidu.yuyin.service.JWakeUpService;
import com.erdosjiang.baidu.yuyin.utils.JTTSTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements IStatus {

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"oncreate");
        super.onCreate(savedInstanceState);
        //1.动态申请权限
        initPermission();
        //2.启动后台服务
        Intent intent = new Intent(this,JWakeUpService.class);
        startService(intent);
        //4.设置主界面
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"ondestory");
        super.onDestroy();
    }

    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }


}
