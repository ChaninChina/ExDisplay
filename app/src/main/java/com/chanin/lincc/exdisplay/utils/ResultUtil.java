package com.chanin.lincc.exdisplay.utils;

public class ResultUtil {


    public static boolean isNoData(String str) {
        if (str.startsWith(Constants.PREFIX) && str.endsWith(Constants.NODATA + MD5Util.MD5(Constants.NODATA) + Constants.SUFFIX)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getNo(String str) {
        if (str.startsWith(Constants.PREFIX) && str.contains(Constants.SEPARATOR) && str.length() > 12) {
            return str.substring(5, 7);
        } else {
            return null;
        }
    }

    public static String getContent(String str) {

        try {
            int length = str.length();
            return str.substring(10, length - 37);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResultMD5(String str) {
        try {
            int length = str.length();
            return str.substring(length - 37, length - 5);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyResult(String str) {
        return isResult(str) &&isMD5(getResultMD5(str),getContent(str));

    }

    public static boolean isMD5(String key, String original) {
        return !isEmpty(key)&&!isEmpty(original)&&key.equalsIgnoreCase(MD5Util.MD5(original));
    }

    public static boolean isResult(String str) {
        if (str != null && str.length() > 47 && str.startsWith(Constants.PREFIX) && str.endsWith(Constants.SUFFIX) && str.contains(Constants.SEPARATOR)) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * utf-8 转换成 unicode
     * @author fanhui
     * 2007-3-15
     * @param inStr
     * @return
     */
    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(myBuffer[i]);
            if(ub == Character.UnicodeBlock.BASIC_LATIN){
                //英文及数字等
                sb.append(myBuffer[i]);
            }else if(ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS){
                //全角半角字符
                int j = (int) myBuffer[i] - 65248;
                sb.append((char)j);
            }else{
                //汉字
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u"+hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }
}
