package com.chanin.lincc.exdisplay.utils;

import com.chanin.lincc.exdisplay.R;
import com.chanin.lincc.exdisplay.app.App;
import com.chanin.lincc.exdisplay.connect.Connection;
import com.chanin.lincc.exdisplay.model.ExClass;
import com.chanin.lincc.exdisplay.model.ExGroup;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {


//    public static final String IP = "10.0.2.2";
//    public static final int PORT = 8888;
//    public static final String IP = "120.78.141.131";
//    public static final int PORT = 54000;
    public static final String UTF8 = "GBK";
    //public static final String UTF8 = "UTF-8";


    public static final String CMD_REQUEST = App.getInstance().getResources().getString(R.string.cmd_request);
    public static final String CMD_LOGIN_ORDER = App.getInstance().getResources().getString(R.string.cmd_login_order);
    public static final String CMD_DATA_ORDER = App.getInstance().getResources().getString(R.string.data_order);
    public static final String CMD_DATA_MORE_ORDER = App.getInstance().getResources().getString(R.string.data_more_order);
//    public static final String CMD_REQUEST ="";
//    public static final String CMD_LOGIN_ORDER ="";
//    public static final String CMD_DATA_ORDER = "";
//    public static final String CMD_DATA_MORE_ORDER = "";
    public static final String REGEX_IP = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])(\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)){3}$";
    public static final String PREFIX = "Start";
    public static final String SEPARATOR = ">>>";
    public static final String NODATA = "NODATA";
    public static final String SUFFIX = "*****";
    public static final String  REGEX = "\\}\\}\\{\\{";
    public static final String  REGEX_SEPARATOR = "@@";
    public static final int READ_TIMEOUT = 10;
    public static final String RECE_PUSH = "RecePush%s>>>%s%s*****";
    public static final String RECE_PUSH_CONTENT = "MessageID:%s";

    public static boolean isIP(String str){
        Pattern p = Pattern.compile(REGEX_IP);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static void main(String[] args) {
//     String str = "Start80>>>{{抽检超时@@2018-07-14@@订单号;线别;计划完成时间;@@0还未处理@@@@@@2018/7/14 20:03:16@@2@@}}{{抽检超时@@2018-07-14@@订单号;线别;计划完成时间;@@0还未处理@@@@@@2018/7/14 20:22:26@@3@@}}{{抽检超时@@2018-07-14@@订单号;线别;计划完成时间;@@0还未处理@@@@@@2018/7/14 20:22:39@@4@@}}{{全检超时@@2018-07-14@@订单号;线别;计划完成时间;@@0还未处理@@@@@@2018/7/14 15:35:47@@1@@}}{{全检超时@@2018-07-14@@订单号;线别;计划完成时间;@@0还未处理@@@@@@2018/7/14 20:22:59@@5@@}}{{全检超时@@2018-07-14@@订单号;线别;计划完成时间;@@1已经处理@@@@@@2018/7/14 20:23:09@@6@@}}DFA4A60ACE427619D23C864FC8DC2403*****";
//     System.out.println(MD5Util.MD5(ResultUtil.getContent(str)));
        boolean b = isIP("127.0.0.1000");
        System.out.println("is IP = " + b);
        // System.out.println(ResultUtil.utf8ToUnicode(str));



//        String str = "Start20>>>NODATA" + MD5Util.MD5("NODATA") + "*****";
//        Pattern pattern = Pattern.compile(RESULT_NODATA);
//        Matcher matcher = pattern.matcher(str);
//        matcher.find();
//        boolean b = ResultUtil.isNoData(str);
//        String group = matcher.group(0);
//        System.out.println(group);
//        System.out.println("result :" + b);
//
//        System.out.println(RESULT_NODATA);
//        System.out.println(ResultUtil.getNo(str));
//        System.out.println("MD5 : " + ResultUtil.getResultMD5(str));
//        String content = ResultUtil.getContent(RESULT_DATA);
//        String substring = content.substring(2, content.length() - 2);
//
//        substring.split(REGEX);
//        System.out.println("result : "+ substring);
//        System.out.println("Content : " + content);
//
//        String data =
//                "{{抽检超时@@2018-07-14@@订单号;线别;计划完成时间;@@0还未处理@@<NULL>@@<NULL>@@2018-07-14 20:03:@@1}}" +
//                "{{抽检超时@@2018-07-13@@订单号;线别;计划完成时间;@@0还未处理@@<NULL>@@<NULL>@@2018-07-14 20:03:@@2}}" +
//                "{{抽检超时@@2018-07-12@@订单号;线别;计划完成时间;@@0还未处理@@<NULL>@@<NULL>@@2018-07-14 20:03:@@3}}" +
//                "{{抽检超时@@2018-07-11@@订单号;线别;计划完成时间;@@0还未处理@@<NULL>@@<NULL>@@2018-07-14 20:03:@@4}}" +
//                "{{抽检超时@@2018-07-10@@订单号;线别;计划完成时间;@@0还未处理@@<NULL>@@<NULL>@@2018-07-14 20:03:@@5}}" +
//                "{{抽检超时@@2018-07-14@@订单号;线别;计划完成时间;@@0还未处理@@<NULL>@@<NULL>@@2018-07-14 20:03:@@6}}" +
//                "{{抽检超时@@2018-07-13@@订单号;线别;计划完成时间;@@0还未处理@@<NULL>@@<NULL>@@2018-07-14 20:03:@@7}}";
//        String result = "Start60>>>" + data + MD5Util.MD5(data) + "*****";
//        Connection instance = Connection.getInstance();
//        ArrayList<ExClass> exClasses = instance.dealDataMessage(result);
//        for (ExClass exClass : exClasses) {
//            for (ExGroup group : exClass.groups) {
//                System.out.println("group : "+group.toString());
//            }
//        }
//
//        boolean contains = data.contains(REGEX);
//        System.out.println("contains : "+contains);
//        String[] split = data.substring(2, data.length() - 2).split(REGEX);
//        for (String s: split){
//            System.out.println("data : "+ s);
//        }
//        System.out.println("verifyResult : " + ResultUtil.verifyResult(str));
//
//        String order = String.format(Constants.CMD_LOGIN_ORDER,"12345","123455");
//        String cmd = String.format(Constants.CMD_REQUEST,"00",order, MD5Util.MD5(order));
//        System.out.println("order : " + order);
//        System.out.println("cmd : " + cmd);

//        System.out.println("createTodayCmd : " + RequestUtil.createTodayCmd());
//        System.out.println("createWeekCmd : " + RequestUtil.createWeekCmd());
//        System.out.println("createMonthCmd : " + RequestUtil.createMonthCmd());
 //      System.out.println("createRevPush : " + RequestUtil.createRevPush("20"));
//        System.out.println("createLoginCmd : " + RequestUtil.createLoginCmd("12334","qweqew"));

    }


}
