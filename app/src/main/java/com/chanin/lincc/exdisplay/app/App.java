package com.chanin.lincc.exdisplay.app;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.chanin.lincc.exdisplay.MessageDetailActivity;
import com.chanin.lincc.exdisplay.R;
import com.chanin.lincc.exdisplay.utils.DBHelper;

import java.util.function.Consumer;

import io.reactivex.plugins.RxJavaPlugins;


public class App extends Application {

    private static App instance;


    public static App getInstance() {
        return instance;
    }

    public DBHelper dbHelper;

    public void initDBHelper(String userName){
        if(!TextUtils.isEmpty(userName)){
            dbHelper = DBHelper.getInstance(userName);
        }

    }

    public void disconnect(){
        if(dbHelper!=null){
            dbHelper.disconnect();
        }

    }


    public DBHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setRxJavaErrorHandler();
        instance = this;

    }

    private void setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(new io.reactivex.functions.Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }


    public static void noticeMessage(String str) {
        //获取PendingIntent
        NotificationManager mNotifyManager =
                (NotificationManager) instance.getSystemService(NOTIFICATION_SERVICE);
        Intent mainIntent = new Intent(instance, MessageDetailActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(instance, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //创建 Notification.Builder 对象

        NotificationCompat.Builder builder = new NotificationCompat.Builder(instance)
                .setSmallIcon(R.mipmap.ic_launcher)
                //点击通知后自动清除
                .setAutoCancel(true)
                .setContentTitle("新消息通知")
                .setContentText(str)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(mainPendingIntent);
        //发送通知
        if(mNotifyManager!=null){
            mNotifyManager.notify(3, builder.build());
        }


    }

}
