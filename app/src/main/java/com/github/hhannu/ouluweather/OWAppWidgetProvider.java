package com.github.hhannu.ouluweather;

import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

public class OWAppWidgetProvider extends AppWidgetProvider {
	
	public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";
	private static Boolean metric_units = true;

    @Override
    public void onEnabled(Context context)
    {
        Log.d ("", "onEnabled()");
        
        OWAppWidgetAlarm OWAppWidgetAlarm = new OWAppWidgetAlarm(context.getApplicationContext());
        OWAppWidgetAlarm.restartAlarm();

    }

    @Override
    public void onDisabled(Context context)
    {
        Log.d ("", "onDisabled()");

        OWAppWidgetAlarm OWAppWidgetAlarm = new OWAppWidgetAlarm(context.getApplicationContext());
        OWAppWidgetAlarm.stopAlarm();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        Log.d ("", "onDeleted()");

        OWAppWidgetAlarm OWAppWidgetAlarm = new OWAppWidgetAlarm(context.getApplicationContext());
        OWAppWidgetAlarm.stopAlarm();    	
    }
    
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        Log.d ("", "onReceive(): " + intent.getAction());
        super.onReceive(context, intent);

        if(intent.getAction().equals(ACTION_AUTO_UPDATE))
        {
            setWeatherData(context);        	 
        }        
    }
    
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.d ("", "onUpdate()");
        
        // Create an Intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
   
        // Get the layout for the App Widget and attach an on-click listener to the button
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ow_appwidget);
        views.setOnClickPendingIntent(R.id.tempView, pendingIntent);
        // Update current app widget
        appWidgetManager.updateAppWidget(appWidgetIds[0], views);
        
        setWeatherData(context);       
    }
    
	private static void setWeatherData(final Context context) {
		
		//final int N = appWidgetIds.length;
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ow_appwidget);


		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {

				Boolean fail = false;
				String temperature = "00";
				String wind = "00";
				String windmax = "00";
				String winddir = "00";
				String windchill = "00";
				String preciph = "00";
				String precipd = "00";
				String time = "00";

				try {
					URL url = new URL("http://weather.willab.fi/weather.xml");
					XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser xpp = factory.newPullParser();
					xpp.setInput(url.openStream(), null);

					while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {

						if (xpp.getEventType() == XmlPullParser.START_TAG) {
							//Log.d("", xpp.getName());
							if (xpp.getName().equals("tempnow"))
								temperature = xpp.nextText();
							else if (xpp.getName().equals("windspeed"))
								wind = xpp.nextText();
							else if (xpp.getName().equals("windspeedmax"))
								windmax = xpp.nextText();
							else if (xpp.getName().equals("winddir")) {
								Float wdir = Float.parseFloat(xpp.nextText());
								if(wdir >= 22.5 && wdir < 67.5)
									winddir = "↙";
								else if(wdir >= 67.5 && wdir < 112.5)
									winddir = "←";
								else if(wdir >= 112.5 && wdir < 157.5)
									winddir = "↖";
								else if(wdir >= 157.5 && wdir < 202.5)
									winddir = "↑";
								else if(wdir >= 202.5 && wdir < 247.5)
									winddir = "↗";
								else if(wdir >= 247.5 && wdir < 292.5)
									winddir = "→";
								else if(wdir >= 292.5 && wdir < 337.5)
									winddir = "↘";
								else
									winddir = "↓";
							}
							else if (xpp.getName().equals("precipitation") && xpp.getAttributeValue(1).equals("1h"))
								preciph = xpp.nextText();
							else if (xpp.getName().equals("precipitation") && xpp.getAttributeValue(1).equals("1d"))
								precipd = xpp.nextText();
							else if (xpp.getName().equals("time"))
								time = xpp.nextText();
							//else if (xpp.getName().equals("windchill"))
							//	windchill = xpp.nextText();
						}

						xpp.next();
					}
				} catch (Throwable t) {
					fail = true;
					Log.e("caught throwable: ", t.getMessage());
					views.setTextViewText(R.id.timeView, t.getMessage());
					//Toast.makeText(this, "Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
				}

				if(fail == false) {
					views.setTextViewText(R.id.WinddirValue, winddir);

					if(windchill.equalsIgnoreCase("00")){
						views.setTextViewTextSize(R.id.tempView, TypedValue.COMPLEX_UNIT_DIP, 50);
						views.setTextViewText(R.id.windchillView, "");
					}
					else{
						views.setTextViewTextSize(R.id.tempView, TypedValue.COMPLEX_UNIT_DIP, 36);
						if(MainActivity.get_metric())
							views.setTextViewText(R.id.windchillView, "(" + windchill + ")");
						else
							views.setTextViewText(R.id.windchillView, "(" + String.format("%.1f", Float.parseFloat(windchill) * 0.5555 + 32) + ")");
					}

					if(MainActivity.get_metric()) {
						views.setTextViewText(R.id.tempView, temperature);
						views.setTextViewText(R.id.tempUnit, "°C");
						views.setTextViewText(R.id.WindspeedValue, wind + "(" + windmax + ")m/s ");
						views.setTextViewText(R.id.PrecipitationValue, preciph + "(" + precipd + ")mm");
					}
					else {
						views.setTextViewText(R.id.tempView, "" + String.format("%.1f", Float.parseFloat(temperature) * 0.5555 + 32));
						views.setTextViewText(R.id.tempUnit, "??F");
						views.setTextViewText(R.id.WindspeedValue, String.format("%.1f", (Float.parseFloat(wind) * 3.2808)) + "(" + String.format("%.1f", (Float.parseFloat(windmax) * 3.2808)) + ")fps ");
						views.setTextViewText(R.id.PrecipitationValue, String.format("%.1f", (Float.parseFloat(preciph) * 0.0393701)) + "(" + String.format("%.1f", (Float.parseFloat(precipd) * 0.0393701)) + ")\"");
					}
					views.setTextViewText(R.id.timeView, time);
				}
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				ComponentName thiswidget = new ComponentName(context, OWAppWidgetProvider.class);
				appWidgetManager.updateAppWidget(thiswidget, views);
				//appWidgetManager.updateAppWidget(appWidgetId, views);

			}
		});

		thread.start();
	}

	public void set_metric(Boolean metric) {
		metric_units = metric;
	}
	
}
		