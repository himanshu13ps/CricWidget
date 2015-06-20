package com.hps;

import com.hps.constants.Constants;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

public class Settings_Activity extends Activity 
{
	 Spinner sp;
	 SharedPreferences preferences;
	@Override
	   public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       
	       setContentView(R.layout.settings_page);
	       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	       
	       preferences = this.getSharedPreferences(Constants.CRIC_PREFS, MODE_WORLD_READABLE);
	       
	       sp = (Spinner) findViewById(R.id.interval_spinner);
	       sp.setSelection(getSelectedIndexFromMinutes(preferences.getInt(Constants.REFRESH_INTERVAL, 10)));
	       
	       CheckBox cb = (CheckBox) findViewById(R.id.autoCheckbox);
	       cb.setChecked(preferences.getBoolean(Constants.AUTO_REFRESH, false));
	       
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
	       
	}
	
	

	@Override
	public void onBackPressed() 
	{
		preferences =  this.getSharedPreferences(Constants.CRIC_PREFS, MODE_WORLD_READABLE);
		Editor edit = preferences.edit();
		CheckBox cb = (CheckBox) findViewById(R.id.autoCheckbox);
		//UpdateWidgetService.autoRefresh = cb.isChecked();
		edit.putBoolean(Constants.AUTO_REFRESH, cb.isChecked());
	       
	    Spinner sp = (Spinner) findViewById(R.id.interval_spinner);
	    //UpdateWidgetService.refreshInterval = getMinutesFromSelectedIndex(sp.getSelectedItemPosition());
	    edit.putInt(Constants.REFRESH_INTERVAL, getMinutesFromSelectedIndex(sp.getSelectedItemPosition()));
	    
	    edit.commit();
	    // Update widget						
		Intent updateWidgetIntent = new Intent();
	    updateWidgetIntent.setAction(Constants.UPDATE_WIDGET_FROM_ACTIVITY);
	    sendBroadcast(updateWidgetIntent);
	    
	    super.onBackPressed();
	}



	private int getSelectedIndexFromMinutes(int refreshInterval) 
	{
		switch(refreshInterval)
		{
		case 1:
			return 0;
			
		case 2:
			return 1;
			
		case 5:
			return 2;
			
		case 10:
			return 3;
			
		case 20:
			return 4;
			
		case 30:
			return 5;
		}
		return 0;
	}
	
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
