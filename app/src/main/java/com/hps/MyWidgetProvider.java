package com.hps;

import com.hps.constants.Constants;

import android.app.AlarmManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {

	private static final String LOG = "com.hps";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		Log.w(LOG, "onUpdate method called");
		
		// Get all ids
		ComponentName thisWidget = new ComponentName(context,MyWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		// Build the intent to call the service
		Intent intent = new Intent(context.getApplicationContext(), UpdateWidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

		// Update the widgets via the service
		context.startService(intent);
	}
	
	@Override  
    public void onDisabled(Context context)  
    {  
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
  
        if (UpdateWidgetService.service!=null)
        {
        	 m.cancel(UpdateWidgetService.service); 
        }
        
    }

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		super.onReceive(context, intent);
		if (intent.getAction().equals(Constants.UPDATE_WIDGET_FROM_ACTIVITY))
		{	
			ComponentName thisWidget = new ComponentName(context,MyWidgetProvider.class);
			int[] allWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(thisWidget);

			// Build the intent to call the service
			Intent intent2 = new Intent(context.getApplicationContext(), UpdateWidgetService.class);
			intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

			// Update the widgets via the service
			context.startService(intent2);
		}
		
	}
	
	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
	{		
		int[] allWidgetIds = {appWidgetId};

		// Build the intent to call the service
		Intent intent2 = new Intent(context.getApplicationContext(), UpdateWidgetService.class);
		intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

		// Update the widgets via the service
		context.startService(intent2);
			
	}
	
	
}

