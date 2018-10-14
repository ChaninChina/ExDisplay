package com.chanin.lincc.exdisplay.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.util.Log;

import com.chanin.lincc.exdisplay.app.App;
import com.chanin.lincc.exdisplay.connect.Connection;
import com.chanin.lincc.exdisplay.utils.RequestUtil;
import com.chanin.lincc.exdisplay.utils.ResultUtil;

import java.util.concurrent.TimeUnit;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SchedulerService extends JobService {
    public static final int JOB_ID = 100;
    public static final long JOB_OVERDIDE_DEADLINE = 1000;
    private static final String TAG = "SchedulerService";

    public SchedulerService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"onStartJob");
        doScheduler();
        jobFinished(params,true);
        return true;
    }

    private void doScheduler() {
        Log.d(TAG,"doScheduler");
        ConnectService.start(this);
        Connection.getInstance().startSchedule();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"onStopJob");
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }





}
