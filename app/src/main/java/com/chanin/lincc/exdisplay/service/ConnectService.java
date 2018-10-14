package com.chanin.lincc.exdisplay.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.chanin.lincc.exdisplay.app.App;
import com.chanin.lincc.exdisplay.connect.Connection;
import com.chanin.lincc.exdisplay.connect.IMessageCallBack;


public class ConnectService extends Service {
    private static final String TAG = "ConnectService";
    private Connection connection;

    public ConnectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }


    public void stopConnection(){
        connection.stopConnection();
    }

    public void startSchedule(){
        connection.startSchedule();
    }

    public void stopSchedule(){
        connection.stopSchedule();
    }

    public void setIsLogin(boolean b) {
        connection.setIsLogin(b);
    }

    public boolean isLogin(){
        return connection.isLogin();
    }

    public void sendCallBackMessage(String no,String cmd, IMessageCallBack messageCallBack) {
        connection.sendCallBackMessage(no,cmd,messageCallBack);
    }

    public Integer startConnection(String mUserName, String mPassword) {
        return connection.connection(mUserName, mPassword);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ConnectService.class);
        context.startService(intent);
    }

    public class ServiceBinder extends Binder {
        public ConnectService getService(){
            return ConnectService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        connection = Connection.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy=========");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG,"onLowMemory");
        stopConnection();
    }

    @Override
    public void onTrimMemory(int level) {

        super.onTrimMemory(level);
        Log.d(TAG,"onTrimMemory");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG,"onRebind");
        super.onRebind(intent);
    }
}
