package com.ly.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by LY on 2016-08-28.
 */
public class AlarmHecate {

    public static AlarmBuilder with(Context ctx) {
        if (ctx == null) {
            throw new UnsupportedOperationException("context must not be null");
        }
        Context appContext = ctx.getApplicationContext();

        return new AlarmBuilder(appContext);
    }

    public static class AlarmBuilder {
        Context mAppContext;
        AlarmManager mAlarmManager;

        private AlarmBuilder(Context appContext) {
            mAppContext = appContext;
            if (mAlarmManager == null) {
                mAlarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
            }
        }

        public void set(int type, long trigger, long interval, int requestCode, Intent intent, int flag) {
            PendingIntent operation = PendingIntent.getService(mAppContext, requestCode, intent, flag);
            mAlarmManager.setRepeating(type, trigger, interval, operation);
        }

        public void cancle(int requestCode, Intent intent, int flag) {
            PendingIntent operation = PendingIntent.getService(mAppContext, requestCode, intent, flag);
            mAlarmManager.cancel(operation);
        }
    }
}
