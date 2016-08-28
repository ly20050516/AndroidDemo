package com.ly.demo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.ly.alarm.AlarmHecate;
import com.ly.utils.ToastUtils;

import java.util.Calendar;

import rx.Observable;
import rx.functions.Func1;

public class ScrollingActivity extends AppCompatActivity {

    public static final String TAG = "liuyan";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Observable.from(new String[]{"url1","url2","url3"}).flatMap(new Func1<String, Observable<?>>() {
            @Override
            public Observable<?> call(String s) {
                return Observable.just(s);
            }
        }).subscribe();

        ToastUtils.show(this,"System currentTimeMillis " + System.currentTimeMillis() + " Calendar " + Calendar.getInstance().getTimeInMillis());
        Log.d(TAG, "onCreate: " + "System currentTimeMillis " + System.currentTimeMillis() + " Calendar " + Calendar.getInstance().getTimeInMillis());
        ToastUtils.show(this,"SystemClock elapsed real time " + SystemClock.elapsedRealtime());

        Intent intent = new Intent(AlarmIntentService.ACTION_ALARM);

        intent.setClass(this,AlarmIntentService.class);
        Calendar c = Calendar.getInstance();
        c.set(1970,1,1,1,10,0);
        long triggerTime =c.getTimeInMillis();
        intent.putExtra(AlarmIntentService.EXTRA_ALARM_PARAM,triggerTime);
        Log.d(TAG, "onCreate: triggerTime " + triggerTime);
        AlarmHecate.with(this).set(AlarmManager.RTC_WAKEUP,triggerTime,10 * 60 * 1000,0,intent, 0);

        Intent intent2 = new Intent(AlarmIntentService.ACTION_ALARM);
        intent2.setClass(this,AlarmIntentService.class);
        long triggerTime2 = Calendar.getInstance().getTimeInMillis();
        intent2.putExtra(AlarmIntentService.EXTRA_ALARM_PARAM,triggerTime2);
        Log.d(TAG, "onCreate: triggerTime2 " + triggerTime2);
//        AlarmHecate.with(this).set(AlarmManager.RTC_WAKEUP,triggerTime2,1 * 60 * 1000,1,intent2, 0);
//        AlarmHecate.with(this).cancle(0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmHecate.with(this).cancle(1,intent2, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
