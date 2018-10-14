package com.chanin.lincc.exdisplay.utils;

import android.text.TextUtils;

public class RequestUtil {




    public static String createTodayCmd() {
        String order = String.format(Constants.CMD_DATA_ORDER,DateUtil.getToday());
        return String.format(Constants.CMD_REQUEST,"60",order, MD5Util.MD5(order));
    }

    public static String createDayCmd(String day) {
        String order = String.format(Constants.CMD_DATA_ORDER,day);
        return String.format(Constants.CMD_REQUEST,"60",order, MD5Util.MD5(order));
    }


    public static String createWeekCmd() {
        String order = String.format(Constants.CMD_DATA_MORE_ORDER,DateUtil.get7Today());
        return String.format(Constants.CMD_REQUEST,"70",order, MD5Util.MD5(order));
    }


    public static String createMonthCmd() {
        String order = String.format(Constants.CMD_DATA_MORE_ORDER,DateUtil.get30Today());
        return String.format(Constants.CMD_REQUEST,"80",order, MD5Util.MD5(order));
    }

    public static  String createLoginCmd(String mUserName, String mPassword) {
        String order = String.format(Constants.CMD_LOGIN_ORDER,mUserName,mPassword);
        return String.format(Constants.CMD_REQUEST,"00",order, MD5Util.MD5(order));
    }

    public static  String createRevPush(String no) {
        String order = String.format(Constants.RECE_PUSH_CONTENT,no);
        return String.format(Constants.RECE_PUSH,no,order, MD5Util.MD5(order));
    }



    public static String getRequestNo(String str){
        if(!TextUtils.isEmpty(str)){
            return str.substring(12,14);
        }
        return null;
    }

}
