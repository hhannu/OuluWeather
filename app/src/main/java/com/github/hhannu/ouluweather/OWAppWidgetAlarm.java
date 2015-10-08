package com.github.hhannu.ouluweather;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OWAppWidgetAlarm {

    private final int ALARM_ID = 0;
    private int INTERVAL_MILLIS = 900000;

    private Context mContext;


    public OWAppWidgetAlarm(Context context) {
        mContext = context;
    }


    public void startAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, INTERVAL_MILLIS);

        Intent alarmIntent = new Intent(OWAppWidgetProvider.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), INTERVAL_MILLIS, pendingIntent);

        Log.d ("startAlarm()", "" + INTERVAL_MILLIS);
    }


    public void stopAlarm() {
        Intent alarmIntent = new Intent(OWAppWidgetProvider.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
    

    public void restartAlarm() {
    	stopAlarm();
    	startAlarm();
    }
    

    public void setInterval(int interval) {
    	INTERVAL_MILLIS = interval;
    	restartAlarm();
        Log.d ("setInterval()", "" + INTERVAL_MILLIS);
    }
    
}
