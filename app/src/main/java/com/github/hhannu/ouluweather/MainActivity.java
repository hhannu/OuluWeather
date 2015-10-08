package com.github.hhannu.ouluweather;

import android.os.Bundle;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class MainActivity extends Activity {	

	int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	static Boolean metric = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);				
    
	    Spinner spinner = (Spinner) findViewById(R.id.update_spinner);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	         R.array.intervals, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
		// TODO: get values from persisten storage
	    spinner.setSelection(2);
	}
	
	public void saveConfig(View view){
		final Context context = MainActivity.this;

		OWAppWidgetAlarm OWAppWidgetAlarm = new OWAppWidgetAlarm(context.getApplicationContext());
                
	    Spinner spinner = (Spinner) findViewById(R.id.update_spinner);
	    switch(spinner.getSelectedItemPosition()) {
	    case 0:
	    	//5min
	    	OWAppWidgetAlarm.setInterval(300000);
	    	break;
	    case 1:
	    	//15min
	    	OWAppWidgetAlarm.setInterval(900000);
	    	break;
	    case 3:
	    	//1h
	    	OWAppWidgetAlarm.setInterval(3600000);
	    	break;
	    case 4:
	    	//2h
	    	OWAppWidgetAlarm.setInterval(7200000);
	    	break;
	    default:
	    	//30min
	    	OWAppWidgetAlarm.setInterval(1800000);
	    	break;
	    }
	    
		//CheckBox cb = (CheckBox) findViewById(R.id.unit_cb);
		//metric = !cb.isChecked();
	    
		//TODO: save settings to persistent storage and set intent to update widget
		
		finish();
	}
	
	public void cancelConfig(View view){
		//setResult(RESULT_CANCELED);
		//finish();
	}

	static Boolean get_metric(){
		return(metric);
	}
}
