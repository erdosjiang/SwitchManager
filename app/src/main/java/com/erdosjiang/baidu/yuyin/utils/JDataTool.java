package com.erdosjiang.baidu.yuyin.utils;

public class JDataTool {
    public static String findByteData(String resultMsg) {
        //TODO根据语音结果查找传输报文
        String msg = "";
        switch (resultMsg){
            case "打开客厅灯":
                msg= "CC000000001";
                break;
            case "关闭客厅灯":
                msg = "CC00010001";
                break;
        }
        return msg;
    }
}
