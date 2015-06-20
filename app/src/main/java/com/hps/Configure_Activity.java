package com.hps;

import com.hps.constants.Constants;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

public class Configure_Activity extends Activity 
{
	Spinner sp;
	Button configSaveButton;
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	SharedPreferences preferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setResult(RESULT_CANCELED);
		setContentView(R.layout.configure_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		preferences = this.getSharedPreferences(Constants.CRIC_PREFS, MODE_WORLD_READABLE);
		
		// Set spinner to default value
		sp = (Spinner) findViewById(R.id.interval_spinner);
	    sp.setSelection(2);// 5 Minutes by default
		
	    // Set checked change listener for Checkbox
	    CheckBox cb = (CheckBox) findViewById(R.id.autoCheckbox);
	    cb.setOnCheckedChangeListener(new OnCheckedChangeListener()
    	{
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    	    {
    	        if ( isChecked )
    	        {
    	            sp.setEnabled(true);
    	        }
    	        else
    	        {
    	        	sp.setEnabled(false);
    	        }

    	    }
    	});
    	
    	
    	if ( cb.isChecked() )
        {
            sp.setEnabled(true);
        }
        else
        {
        	sp.setEnabled(false);
        }
		  
    	// Save button
		configSaveButton = (Button)findViewById(R.id.button_save);
		configSaveButton.setOnClickListener(configSaveButtonOnClickListener);
	  
	     Intent intent = getIntent();
	     Bundle extras = intent.getExtras();
	     if (extras != null) {
	         mAppWidgetId = extras.getInt(
	                 AppWidgetManager.EXTRA_APPWIDGET_ID,
	                 AppWidgetManager.INVALID_APPWIDGET_ID);
	     }
	  
	     // If they gave us an intent without the widget id, just bail.
	     if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) 
	     {
	         finish();
	     }     
	}
	
	
	private Button.OnClickListener configSaveButtonOnClickListener
	= new Button.OnClickListener()
	{

		@Override
		public void onClick(View arg0) 
		{ 
			
			
			Editor edit = preferences.edit();
			CheckBox cb = (CheckBox) findViewById(R.id.autoCheckbox);
			//UpdateWidgetService.autoRefresh = cb.isChecked();
			edit.putBoolean(Constants.AUTO_REFRESH, cb.isChecked());
		       
		    Spinner sp = (Spinner) findViewById(R.id.interval_spinner);
		    //UpdateWidgetService.refreshInterval = getMinutesFromSelectedIndex(sp.getSelectedItemPosition());
		    edit.putInt(Constants.REFRESH_INTERVAL, getMinutesFromSelectedIndex(sp.getSelectedItemPosition()));
		    
		    edit.commit();
	
			 final Context context = Configure_Activity.this;
		
			 AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		
			 //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.hellowidget_layout);
			 //appWidgetManager.updateAppWidget(mAppWidgetId, views);
			 MyWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);
					
		
			 Intent resultValue = new Intent();
			 resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			 setResult(RESULT_OK, resultValue);
			 finish();
		}
	};
	
	private int getMinutesFromSelectedIndex(int index) 
	{
		switch(index)
		{
		case 0:
			return 1;
			
		case 1:
			return 2;
			
		case 2:
			return 5;
			
		case 3:
			return 10;
			
		case 4:
			return 20;
			
		case 5:
			return 30;
		}
		return 10;
	}
}
