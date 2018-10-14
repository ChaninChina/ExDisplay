package com.chanin.lincc.exdisplay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.chanin.lincc.exdisplay.app.App;
import com.chanin.lincc.exdisplay.connect.Connection;


public class PfUtils {


    private static final String app = "APP";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String INTERVAL = "INTERVAL";
    private static final String RETRY_TIME = "RETRY_TIME";
    private static final String TIMEOUT = "TIMEOUT";
    private static final String SAVESTATE = "SAVESTATE";
    private static final String ISLOGIN = "ISLOGIN";
    private static final String IP = "IP";
    private static final String PORT = "PORT";

    public static SharedPreferences spf = App.getInstance().getSharedPreferences(app, Context.MODE_PRIVATE);


    public static void saveUser(String userName, String password) {
        spf.edit().putString(USERNAME, userName).putString(PASSWORD, password).apply();
    }

    public static String getUsername() {
        return getStringFromSpf(USERNAME);
    }

    public static String getPassword() {
        return getStringFromSpf(PASSWORD);
    }

    @NonNull
    private static String getStringFromSpf(String password) {
        return spf.getString(password, "");
    }


    public static int getInterval() {
        return spf.getInt(INTERVAL, 60);
    }

    public static int getRetrytime() {
        return spf.getInt(RETRY_TIME, 3);
    }

    public static int getTimeout() {
        return spf.getInt(TIMEOUT, 20);
    }

    public static void saveInterval(int str) {
        if (str != getInterval()) {
            spf.edit().putInt(INTERVAL, str).apply();
            Connection instance = Connection.getInstance();
            instance.restartScheduler();
        }

    }

    public static void saveRetryTime(int str) {
        spf.edit().putInt(RETRY_TIME, str).apply();
    }

    public static void saveTimeout(int str) {
        spf.edit().putInt(TIMEOUT, str).apply();
    }

    public static void clearUser() {
        spf.edit().remove(USERNAME).remove(PASSWORD).apply();
    }

    public static void saveSaveSate(boolean b) {
        spf.edit().putBoolean(SAVESTATE, b).apply();
    }

    public static boolean getSaveSate() {
        //spf.edit().putBoolean(SAVESTATE,b).apply();
        return spf.getBoolean(SAVESTATE, false);
    }

    public static boolean isLogin() {
        return spf.getBoolean(ISLOGIN, false);
    }

    public static void saveLogin(boolean isLogin) {
        spf.edit().putBoolean(ISLOGIN, isLogin).apply();
    }

    public static void saveIP(String ip) {
        spf.edit().putString(IP, ip).apply();
    }

    public static String getIP() {
        return spf.getString(IP, "120.78.141.131");
    }


    public static void savePort(int port) {
        spf.edit().putInt(PORT, port).apply();
    }

    public static int getPort() {
        return spf.getInt(PORT, 54000);
    }
}
