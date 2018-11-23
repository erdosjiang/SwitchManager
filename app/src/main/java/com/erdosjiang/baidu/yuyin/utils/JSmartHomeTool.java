package com.erdosjiang.baidu.yuyin.utils;

import android.content.Intent;
import android.util.Log;

import com.erdosjiang.baidu.yuyin.config.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class JSmartHomeTool {
    private static String ip = "192.168.1.200";
    private static int port = 4196;
    private static String macno = "00000000";

    private static Socket socket;

    public static String doAction(String data) {
        final String fdata = macno + data;
        new Thread() {
            @Override
            public void run() {
                byte[] bdata = toBytes(fdata);
                String rdata = "";
                try {
                    socket = new Socket(ip, port);
                    socket.setKeepAlive(true);
                    Log.d("----",new String(bdata));
                    socket.getOutputStream().write(bdata);
                    socket.getOutputStream().flush();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"GBK"));

                    while (true){
                        if((rdata = in.readLine()) !=null){
                            Log.d("------2", "run: "+rdata);
                        }
                    }

                    //socket.close();
                } catch (UnknownHostException e) {
                    JTTSTool.ttsSpeak("哎呀,连接主机失败了,你的IP地址或者端口不对");
                } catch (IOException e) {
                    JTTSTool.ttsSpeak("哎呀,连接主机失败了,网络IO有问题");
                }

                super.run();
            }
        }.start();
        return "执行成功";
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMacno() {
        return macno;
    }

    public void setMacno(String macno) {
        this.macno = macno;
    }
}
