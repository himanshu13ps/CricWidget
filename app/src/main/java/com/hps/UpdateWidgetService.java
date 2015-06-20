package com.hps;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.hps.constants.Constants;
import com.hps.object.Match;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpdateWidgetService extends Service {
	private static final String LOG = "com.hps";
	public static Match liveMatch = null;
	public static Match nextMatch =null;
	public static Match completedMatch =null;
	public static String result;
	public static boolean autoRefresh=false;
	public static int refreshInterval=10;
	public static boolean isLive = false;

	public static AppWidgetManager appWidgetManager;
	public static int[] allWidgetIds2;
	public static RemoteViews remoteViews;
	public static PendingIntent service = null;  
	SharedPreferences preferences;
	
	@Override
	public void onStart(Intent intent, int startId) 
	{
		Log.i(LOG, "Called");
		// Create some random data

		appWidgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());

		int[] allWidgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		ComponentName thisWidget = new ComponentName(getApplicationContext(),
				MyWidgetProvider.class);
		allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);
		Log.w(LOG, "From Intent" + String.valueOf(allWidgetIds.length));
		Log.w(LOG, "Direct" + String.valueOf(allWidgetIds2.length));
		
		// Get Preferences
		preferences =  this.getSharedPreferences(Constants.CRIC_PREFS, MODE_WORLD_READABLE);
		autoRefresh = preferences.getBoolean(Constants.AUTO_REFRESH, false);
		refreshInterval = preferences.getInt(Constants.REFRESH_INTERVAL, 10);		

		for (int widgetId : allWidgetIds2) {

			remoteViews = new RemoteViews(this
					.getApplicationContext().getPackageName(),
					R.layout.widget_layout);
			
			remoteViews.setViewVisibility(R.id.linearLayout_middle, View.GONE);
			remoteViews.setViewVisibility(R.id.linearLayout_refresh, View.VISIBLE);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
			
			try 
			{
				
				result = CricScoreReader.httpGet(Constants.SCORE_PROVIDER_URL);
				//Log.w(LOG, "httpGet " + result);
				if (result!=null)
				{
				
					liveMatch = CricScoreReader.getMatchObject(result, Constants.MATCH_LIVE, null);
					nextMatch = CricScoreReader.getMatchObject(result, Constants.MATCH_NEXT, null);
					if (nextMatch !=null)
					{
						completedMatch = CricScoreReader.getMatchObject(result, Constants.MATCH_FINISHED, nextMatch.num);
					}
					else
					{
						completedMatch = CricScoreReader.getMatchObject(result, Constants.MATCH_FINISHED, null);
					}
					
					
					if (liveMatch != null)
					{	
						isLive = true;
						displayLiveMatchInfoOnRemoteViews(remoteViews, liveMatch);					
						
					}
					else
					{
						isLive = false;
						
						if (completedMatch!=null)
						{
							Date dl = new Date();
							Date now = new Date();
							dl.setTime(completedMatch.deadline.getTime() - (dl.getTimezoneOffset() * 60 *1000));
							if (now.before(dl))
							{
								displayLiveMatchInfoOnRemoteViews(remoteViews, completedMatch);
							}
							else
							{								
								if (nextMatch!=null)
								{
									displayNextMatchInfo(remoteViews, nextMatch);
								}
								else
								{
									displayGoodbyeMessage(remoteViews);
								}
							}
						}
						else
						{							
							if (nextMatch!=null)
							{
								displayNextMatchInfo(remoteViews, nextMatch);
							}
							else
							{
								displayGoodbyeMessage(remoteViews);
							}					
						}
						
					}
				}
				else
				{
					
					remoteViews.setTextViewText(R.id.line1,"No connectivity");
				}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.v(LOG, "Got httpGet exception");
				Toast.makeText(this.getApplicationContext(), "No connectivity", Toast.LENGTH_LONG).show();
				//remoteViews.setTextViewText(R.id.line1,"No connectivity");
			}
			
			remoteViews.setViewVisibility(R.id.linearLayout_middle, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.linearLayout_refresh, View.GONE);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
			
			//SimpleDateFormat temp = new SimpleDateFormat("h:mm:ss");
			//remoteViews.setTextViewText(R.id.line1,temp.format(Calendar.getInstance().getTime()).toString()+" "+autoRefresh+" "+refreshInterval);
			

			// Register an onClickListener
			Intent clickIntent = new Intent(this.getApplicationContext(),
					MyWidgetProvider.class); 

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
					allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getApplicationContext(), 0, clickIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.linearLayout_middle, pendingIntent);
			
			
			// Try for activity
			
			Intent launchActivity = new Intent(this.getApplicationContext(),ScoreInfo.class);
			PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(),0, launchActivity, 0);
			remoteViews.setOnClickPendingIntent(R.id.imageView1, pendingIntent2);
			remoteViews.setOnClickPendingIntent(R.id.imageView2, pendingIntent2);
			
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
		
		if (autoRefresh && isLive)
		{
			final AlarmManager m = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);  
			  
	        final Intent i = new Intent(getApplicationContext(), UpdateWidgetService.class);  
	        i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
					allWidgetIds2);
	  
	        if (service == null)  
	        {  
	            service = PendingIntent.getService(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);  
	        }  
	  
	        m.set(AlarmManager.RTC, (Calendar.getInstance().getTimeInMillis()+ 1000 * 60 * refreshInterval), service);  
		}
		else
		{
			if (UpdateWidgetService.service!=null)
	        {
				final AlarmManager m = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);   
				m.cancel(UpdateWidgetService.service); 
	        }
		}
		stopSelf();

		super.onStart(intent, startId);
	}

	private void displayGoodbyeMessage(RemoteViews remoteViews) 
	{
		remoteViews.setImageViewResource(R.id.imageView1,R.drawable.csk);
		remoteViews.setImageViewResource(R.id.imageView2,R.drawable.mi);
		
		remoteViews.setTextViewText(R.id.sn1,"CSK");					
		remoteViews.setTextViewText(R.id.sn2,"MI");
		
		remoteViews.setTextViewText(R.id.line1,"Goodbye !");
		remoteViews.setTextViewText(R.id.line2,"Tune in back");
		remoteViews.setTextViewText(R.id.line3,"for");
		remoteViews.setTextViewText(R.id.line4,"IPL 2014");			
	}

	/**
	 * @param remoteViews
	 * @param m 
	 */
	public void displayNextMatchInfo(RemoteViews remoteViews, Match m) 
	{
		if (m.num.contains("("))
		{
			String[] parts = m.num.split(" ");
			m.num=parts[0];
		}
		
		remoteViews.setImageViewResource(R.id.imageView1, m.team1.imageId);
		remoteViews.setImageViewResource(R.id.imageView2, m.team2.imageId);
		
		remoteViews.setTextViewText(R.id.sn1,m.team1.shortName);					
		remoteViews.setTextViewText(R.id.sn2,m.team2.shortName);
		
		remoteViews.setTextViewText(R.id.line1,Constants.IPL2013+" : "+m.num);
		
		if (m.team1.longName.length()>m.team2.longName.length())
		{
			remoteViews.setTextViewText(R.id.line2,m.team1.longName);
			remoteViews.setTextViewText(R.id.line3,"Vs "+m.team2.longName);
		}
		else
		{
			remoteViews.setTextViewText(R.id.line2,m.team1.longName+" Vs");
			remoteViews.setTextViewText(R.id.line3,m.team2.longName);
		}					
		
		remoteViews.setTextViewText(R.id.line4,m.startDate);
	}

	/**
	 * @param remoteViews
	 * @param m
	 */
	private void displayLiveMatchInfoOnRemoteViews(RemoteViews remoteViews, Match m) 
	{	
		String l1=null, l2=null, l3=null, l4=null;
		if (m.num.contains("("))
		{
			String[] parts = m.num.split(" ");
			m.num=parts[0];
		}
		if (m.team1.batting)
		{
			remoteViews.setImageViewResource(R.id.imageView1, m.team1.imageId);
			remoteViews.setImageViewResource(R.id.imageView2, m.team2.imageId);
			
			remoteViews.setTextViewText(R.id.sn1,m.team1.shortName);					
			remoteViews.setTextViewText(R.id.sn2,m.team2.shortName);
						
			if (m.inningId==1)
			{
				// View 3 - Live Innings 1
				
				l1 = Constants.IPL2013+" : "+m.num; 
				l2 = m.team1.shortName+" "+m.team1.runs+"/"+m.team1.wickets+" ("+m.team1.overs+" overs)";
				l3 = "Vs";
				l4 = m.team2.longName;
				//l3 = "Bat : "+m.striker.shortName+" "+m.striker.runs+"("+m.striker.balls+")";
				//l4 = "Ball: "+m.currBowler.shortName+" "+m.currBowler.wickets+"/"+m.currBowler.runs+" ("+m.currBowler.overs+")";
			}
			else if  (m.inningId==2)
			{
				// View 4 - Live - Innings Break
				
				if (m.team1.runs.equalsIgnoreCase("0")&& m.team1.wickets.equalsIgnoreCase("0")&&m.team1.overs.equalsIgnoreCase("0.0"))
				{
					l1 = Constants.IPL2013+" : "+m.num; 
					l2 = m.team2.shortName+" "+m.team2.runs+"/"+m.team2.wickets+" ("+m.team2.overs+" overs)";
					l3 = "Vs "+m.team1.shortName;
					l4 = "2nd inning begins soon";					
				}
				else
				{	
					if (m.resultText!=null && !m.resultText.trim().equalsIgnoreCase(""))
					{
						l1 = m.team1.shortName+" "+m.team1.runs+"/"+m.team1.wickets+" ("+m.team1.overs+" overs)";
						l2 = "Vs "+m.team2.shortName+" "+m.team2.runs+"/"+m.team2.wickets;
						l3=m.resultText;
						l4="";
					}
					else
					{
						l1 = Constants.IPL2013+" : "+m.num; 
						l2 = m.team1.shortName+" "+m.team1.runs+"/"+m.team1.wickets+" ("+m.team1.overs+" overs)";
						l3 = "Vs";
						l4 = m.team2.shortName+" "+m.team2.runs+"/"+m.team2.wickets;
					}
				}
				
				
				
			}
			else
			{
				l1 = Constants.IPL2013+" : "+m.num; 
				l2=m.team1.shortName+" Vs "+m.team2.shortName;
				l3="Starting soon @ "+m.venue;
				l4="";
			}
		}
		else
		{
			remoteViews.setImageViewResource(R.id.imageView1, m.team2.imageId);
			remoteViews.setImageViewResource(R.id.imageView2, m.team1.imageId);
			
			remoteViews.setTextViewText(R.id.sn1,m.team2.shortName);					
			remoteViews.setTextViewText(R.id.sn2,m.team1.shortName);
			
			
			if (m.inningId==1)
			{
				l1 = Constants.IPL2013+" : "+m.num; 
				l2 = m.team2.shortName+" "+m.team2.runs+"/"+m.team2.wickets+" ("+m.team2.overs+" overs)";
				l3 = "Vs";				
				l4 = m.team1.longName;
			}
			else if  (m.inningId==2)
			{
				if (m.team2.runs.equalsIgnoreCase("0")&& m.team2.wickets.equalsIgnoreCase("0")&&m.team2.overs.equalsIgnoreCase("0.0"))
				{
					l1 = Constants.IPL2013+" : "+m.num; 
					l2 = m.team1.shortName+" "+m.team1.runs+"/"+m.team1.wickets+" ("+m.team1.overs+" overs)";
					l3 = "Vs "+m.team2.shortName;
					l4 = "2nd inning begins soon";					
				}
				else
				{
					if (m.resultText!=null && !m.resultText.trim().equalsIgnoreCase(""))
					{
						l1 = m.team2.shortName+" "+m.team2.runs+"/"+m.team2.wickets+" ("+m.team2.overs+" overs)";
						l2 = "Vs "+m.team1.shortName+" "+m.team1.runs+"/"+m.team1.wickets;
						l3 = m.resultText;
						l4 = "";
					}
					else
					{
						l1 = Constants.IPL2013+" : "+m.num; 
						l2 = m.team2.shortName+" "+m.team2.runs+"/"+m.team2.wickets+" ("+m.team2.overs+" overs)";
						l3 = "Vs";
						l4 = m.team1.shortName+" "+m.team1.runs+"/"+m.team1.wickets;
					}
				}
				
				
			}
			else
			{
				l1 = Constants.IPL2013+" : "+m.num; 
				l2=m.team1.shortName+" Vs "+m.team2.shortName;
				l3="Starting soon @ "+m.venue;
				l4="";
			}
		}
		remoteViews.setTextViewText(R.id.line1,l1);
		remoteViews.setTextViewText(R.id.line2,l2);
		remoteViews.setTextViewText(R.id.line3,l3);
		remoteViews.setTextViewText(R.id.line4,l4);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public Match getLiveMatch() {
		return liveMatch;
	}

	public void setLiveMatch(Match liveMatch) {
		this.liveMatch = liveMatch;
	}

	public Match getNextMatch() {
		return nextMatch;
	}

	public void setNextMatch(Match nextMatch) {
		this.nextMatch = nextMatch;
	}
	
	
	
}
