package com.chanin.lincc.exdisplay.utils;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.chanin.lincc.exdisplay.service.SchedulerService;

import java.util.concurrent.TimeUnit;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SchedulerHelper {


    private static final String TAG = "SchedulerHelper";
    private static JobService mJob;
    private static JobParameters mJobParams;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void schedule(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.w(TAG, "Helpers schedule()");
            final JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            final JobInfo.Builder builder = new JobInfo.Builder(SchedulerService.JOB_ID,
                    new ComponentName(context, SchedulerService.class));

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                builder.setMinimumLatency(TimeUnit.MINUTES.toMillis(30));
                builder.setOverrideDeadline(TimeUnit.MINUTES.toMillis(30));
            } else {
                builder.setPeriodic(TimeUnit.MINUTES.toMillis(30));
            }
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(30), JobInfo.BACKOFF_POLICY_LINEAR);  //线性重试方案
            builder.setRequiresCharging(false); // 未充电状态
            scheduler.schedule(builder.build());
        }
    }


    public static void cancelJob(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        Log.w(TAG, "Helpers cancelJob()");
        final JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.cancel(SchedulerService.JOB_ID);
    }

    public static void jobFinished() {
        Log.w(TAG, "Helpers jobFinished()");
        mJob.jobFinished(mJobParams, true);
    }

    public static void enqueueJob() {
        Log.w(TAG, "Helpers enqueueJob()");
    }

    public static void doHardWork(JobService job, JobParameters params) {
        Log.w(TAG, "Helpers doHardWork()");
        mJob = job;
        mJobParams = params;
    }


}
