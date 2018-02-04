package com.pamkim.sandwiches;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by toripham on 2/3/18.
 */

public class TaskChecker {


    public static String getCurrentTopActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        String currentTopActivity = taskInfo.get(0).topActivity.getPackageName();

        return currentTopActivity;
    }


}


