package com.hps;

import java.io.IOException;

import com.hps.constants.Constants;
import com.hps.object.Match;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreInfo extends Activity 
{
	public static String result;
	
	@Override
	   public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	      /* TextView tv = new TextView(this);
	       tv.setText("Details of Score");
	       setContentView(tv);*/
	       
	       setContentView(R.layout.activity2);
	       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	       
	       
	       try 
	       {
	    	   Match l = UpdateWidgetService.liveMatch;
	    	   Match n = UpdateWidgetService.nextMatch;
			   
			   // Next match info
			   
			   ImageView n_img1 = (ImageView) findViewById(R.id.n_imageView1);
			   ImageView n_img2 = (ImageView) findViewById(R.id.n_imageView2);
			   
			   TextView n_team1 = (TextView) findViewById(R.id.n_line1);
			   TextView n_vs = (TextView) findViewById(R.id.n_line2);
			   TextView n_team2 = (TextView) findViewById(R.id.n_line3);
			   
			   TextView n_venue_line1 = (TextView) findViewById(R.id.n_venue_line1);
			   TextView n_time_line2 = (TextView) findViewById(R.id.n_time_line2);
			   
			   	
			   if (n==null)
			   {
				   result = UpdateWidgetService.result;
				   if (result==null || result.equalsIgnoreCase(""))
				   {
					   result = CricScoreReader.httpGet(Constants.SCORE_PROVIDER_URL);
				   }		 
				   if (result!=null)
				   {
					   n = CricScoreReader.getMatchObject(result, Constants.MATCH_NEXT, null);
				   }				  
			   }			   
			   
			   if (n!=null)
			   {
				    n_img1.setImageResource(n.team1.imageId);
					n_img2.setImageResource(n.team2.imageId);
					
					n_team1.setText(n.team1.longName);
					n_vs.setText("Vs");
					n_team2.setText(n.team2.longName);
					
					n_venue_line1.setText("@ "+n.venue);
					n_time_line2.setText(n.startDate);
			   }
			   else
			   {
				    n_img1 = null;
					n_img2 = null;
					
					n_team1.setHeight(0);
					n_vs.setHeight(0);
					n_team2.setHeight(0);
					
					n_venue_line1.setText("No connectivity... bad, bad network, no Jalebi for you");	
					n_venue_line1.setGravity(Gravity.CENTER_HORIZONTAL);
					n_time_line2.setHeight(0);
			   }
	    	   
	    	   // Update for live match
	    	   
	    	   ImageView l_match_title = (ImageView) findViewById(R.id.match_title1);
	    	   
	    	   ImageView l_img1 = (ImageView) findViewById(R.id.l_imageView1);
			   ImageView l_img2 = (ImageView) findViewById(R.id.l_imageView2);
			   
			   TextView l_team1 = (TextView) findViewById(R.id.l_line1);
			   TextView l_vs = (TextView) findViewById(R.id.l_line2);
			   TextView l_team2 = (TextView) findViewById(R.id.l_line3);
			   
			   TextView l_score_line1 = (TextView) findViewById(R.id.l_score_line1);
			   TextView l_score_line2 = (TextView) findViewById(R.id.l_score_line2);
			   TextView l_score_line3 = (TextView) findViewById(R.id.l_score_line3);
			   TextView l_score_line4 = (TextView) findViewById(R.id.l_score_line4);
			   TextView l_score_line5 = (TextView) findViewById(R.id.l_score_line5);
	    	   
	    	   
			   
			   if (l!=null)
			   {
					displayLiveMatchInfo(l);
			   }
			   else
			   {
				   l = UpdateWidgetService.completedMatch;
				   if (l==null)
				   {
					   result = UpdateWidgetService.result;
					   if (result==null || result.equalsIgnoreCase(""))
					   {
						   result = CricScoreReader.httpGet(Constants.SCORE_PROVIDER_URL);
					   }
					   if (result!=null)
					   {
						   if (n!=null)
						   {
							   l = CricScoreReader.getMatchObject(result, Constants.MATCH_FINISHED, n.num);
						   }
						   else
						   {
							   l = CricScoreReader.getMatchObject(result, Constants.MATCH_FINISHED, null);
						   }
					   }
				   }
				   
				   if (l!=null)
				   {
						l_match_title.setImageResource(R.drawable.last_match);
					   
					   	l_img1.setImageResource(l.team1.imageId);
						l_img2.setImageResource(l.team2.imageId);
						
						l_team1.setText(l.team1.longName);
						l_vs.setText("Vs");
						l_team2.setText(l.team2.longName);
												
						l_score_line1.setText(l.resultText);
						l_score_line1.setGravity(Gravity.CENTER_HORIZONTAL);
						String lastMatchTeam1 = l.team1.shortName+" "+" "+l.team1.runs+"/"+l.team1.wickets+" ("+l.team1.overs+" overs)";
						String lastMatchTeam2 = l.team2.shortName+" "+" "+l.team2.runs+"/"+l.team2.wickets+" ("+l.team2.overs+" overs)";
						l_score_line2.setText(lastMatchTeam1);
						l_score_line2.setGravity(Gravity.CENTER_HORIZONTAL);						
						l_score_line3.setText(lastMatchTeam2);
						l_score_line3.setGravity(Gravity.CENTER_HORIZONTAL);
						l_score_line4.setHeight(0);
						l_score_line5.setHeight(0);
				   }
				   else
				   {
					    l_img1=null;
						l_img2 = null;
						
						l_team1.setHeight(0);
						l_vs.setHeight(0);
						l_team2.setHeight(0);
						
						l_score_line1.setHeight(0);
						l_score_line2.setText("No connectivity... bad, bad network, no Jalebi for you");	
						l_score_line2.setGravity(Gravity.CENTER_HORIZONTAL);
						l_score_line3.setHeight(0);
						l_score_line4.setHeight(0);
						l_score_line5.setHeight(0);
				   }
				  
			   }
			   
			   
	       } 
	       catch (IOException e) 
	       	{
	    	   
	    	   	TextView l_score_line1 = (TextView) findViewById(R.id.l_score_line1);
	    	   	l_score_line1.setText("No connectivity... bad, bad network, no Jalebi for you");	
				l_score_line1.setGravity(Gravity.CENTER_HORIZONTAL);
				
				e.printStackTrace();
			}
	}

	public void displayLiveMatchInfo(Match l) 
	{
		ImageView l_match_title = (ImageView) findViewById(R.id.match_title1);
 	   
 	   		ImageView l_img1 = (ImageView) findViewById(R.id.l_imageView1);
		   ImageView l_img2 = (ImageView) findViewById(R.id.l_imageView2);
		   
		   TextView l_team1 = (TextView) findViewById(R.id.l_line1);
		   TextView l_vs = (TextView) findViewById(R.id.l_line2);
		   TextView l_team2 = (TextView) findViewById(R.id.l_line3);
		   
		   TextView l_score_line1 = (TextView) findViewById(R.id.l_score_line1);
		   TextView l_score_line2 = (TextView) findViewById(R.id.l_score_line2);
		   TextView l_score_line3 = (TextView) findViewById(R.id.l_score_line3);
		   TextView l_score_line4 = (TextView) findViewById(R.id.l_score_line4);
		   TextView l_score_line5 = (TextView) findViewById(R.id.l_score_line5);
		
		   l_match_title.setImageResource(R.drawable.live_match2);
		
		if (l.team1.batting)
		{
			l_img1.setImageResource(l.team1.imageId);
			l_img2.setImageResource(l.team2.imageId);
			
			l_team1.setText(l.team1.longName);
			l_vs.setText("Vs");
			l_team2.setText(l.team2.longName);
						
			if (l.inningId==1)
			{
				// View 3 - Live Innings 1
				
				l_score_line1.setText("Batting: "+l.team1.shortName+" "+" "+l.team1.runs+"/"+l.team1.wickets+" ("+l.team1.overs+" overs)");
					l_score_line2.setText(l.striker.firstName+" "+l.striker.lastName+"* "+l.striker.runs+"("+l.striker.balls+")");
					l_score_line3.setText(l.nonStriker.firstName+" "+l.nonStriker.lastName+" "+l.nonStriker.runs+"("+l.nonStriker.balls+")");
				
				l_score_line4.setText("Bowling: "+l.team2.longName);
					l_score_line5.setText(l.currBowler.firstName+" "+l.currBowler.lastName+" "+l.currBowler.wickets+"W/"+l.currBowler.runs+" ("+l.currBowler.overs+")");							
				
			}
			else if  (l.inningId==2)
			{
				// View 4 - Live - Innings Break
				
				if (l.team1.runs.equalsIgnoreCase("0")&& l.team1.wickets.equalsIgnoreCase("0")&&l.team1.overs.equalsIgnoreCase("0.0"))
				{
					l_score_line1.setText("Batting: "+l.team2.shortName+" "+" "+l.team2.runs+"/"+l.team2.wickets+" ("+l.team2.overs+" overs)");
						l_score_line2.setText("2nd inning begins soon");
						l_score_line3.setHeight(0);
				
					l_score_line4.setText("Bowling: "+l.team1.longName);
						l_score_line5.setHeight(0);;						
				}
				else
				{	
					l_score_line1.setText("Batting: "+l.team1.shortName+" "+" "+l.team1.runs+"/"+l.team1.wickets+" ("+l.team1.overs+" overs)");
						l_score_line2.setText(l.striker.firstName+" "+l.striker.lastName+"* "+l.striker.runs+"("+l.striker.balls+")");
						l_score_line3.setText(l.nonStriker.firstName+" "+l.nonStriker.lastName+" "+l.nonStriker.runs+"("+l.nonStriker.balls+")");
				
					l_score_line4.setText("Bowling: "+l.team2.shortName+" "+"("+l.team2.runs+"/"+l.team2.wickets+")");
						l_score_line5.setText(l.currBowler.firstName+" "+l.currBowler.lastName+" "+l.currBowler.wickets+"W/"+l.currBowler.runs+" ("+l.currBowler.overs+")");		
				}
			}
			else
			{
				l_score_line1.setHeight(0);
				l_score_line2.setText("Starting soon @ "+l.venue);	
				l_score_line2.setGravity(Gravity.CENTER_HORIZONTAL);
				l_score_line3.setHeight(0);
				l_score_line4.setHeight(0);
				l_score_line5.setHeight(0);		
			}
		}
		else
		{
			
			l_img1.setImageResource(l.team2.imageId);
			l_img2.setImageResource(l.team1.imageId);
			
			l_team1.setText(l.team2.longName);
			l_vs.setText("Vs");
			l_team2.setText(l.team1.longName);
			
			
			if (l.inningId==1) 
			{
				l_score_line1.setText("Batting: "+l.team2.shortName+" "+" "+l.team2.runs+"/"+l.team2.wickets+" ("+l.team2.overs+" overs)");
				l_score_line2.setText(l.striker.firstName+" "+l.striker.lastName+"* "+l.striker.runs+"("+l.striker.balls+")");
				l_score_line3.setText(l.nonStriker.firstName+" "+l.nonStriker.lastName+" "+l.nonStriker.runs+"("+l.nonStriker.balls+")");
			
				l_score_line4.setText("Bowling: "+l.team1.longName);
				l_score_line5.setText(l.currBowler.firstName+" "+l.currBowler.lastName+" "+l.currBowler.wickets+"W/"+l.currBowler.runs+" ("+l.currBowler.overs+")");
			}
			else if  (l.inningId==2)
			{
				if (l.team2.runs.equalsIgnoreCase("0")&& l.team2.wickets.equalsIgnoreCase("0")&&l.team2.overs.equalsIgnoreCase("0.0"))
				{
					l_score_line1.setText("Batting: "+l.team1.shortName+" "+" "+l.team1.runs+"/"+l.team1.wickets+" ("+l.team1.overs+" overs)");
					l_score_line2.setText("2nd inning begins soon");
					l_score_line2.setGravity(Gravity.CENTER_HORIZONTAL);
					l_score_line3.setHeight(0);
			
					l_score_line4.setText("Bowling: "+l.team2.longName);
					l_score_line5.setHeight(0);	
				}
				else
				{
					l_score_line1.setText("Batting: "+l.team2.shortName+" "+" "+l.team2.runs+"/"+l.team2.wickets+" ("+l.team2.overs+" overs)");
					l_score_line2.setText(l.striker.firstName+" "+l.striker.lastName+"* "+l.striker.runs+"("+l.striker.balls+")");
					l_score_line3.setText(l.nonStriker.firstName+" "+l.nonStriker.lastName+" "+l.nonStriker.runs+"("+l.nonStriker.balls+")");
				
					l_score_line4.setText("Bowling: "+l.team1.shortName+" "+"("+l.team1.runs+"/"+l.team1.wickets+")");
					l_score_line5.setText(l.currBowler.firstName+" "+l.currBowler.lastName+" "+l.currBowler.wickets+"W/"+l.currBowler.runs+" ("+l.currBowler.overs+")");
				}
				
				
			}
			else
			{
				l_score_line1.setHeight(0);
				l_score_line2.setText("Starting soon @ "+l.venue);		
				l_score_line2.setGravity(Gravity.CENTER_HORIZONTAL);
				l_score_line3.setHeight(0);
				l_score_line4.setHeight(0);
				l_score_line5.setHeight(0);	
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.score_info_activity_menu, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		 switch (item.getItemId()) 
		 {
	        case R.id.settings:
	        {
	        	startActivity(new Intent(this, Settings_Activity.class));	 	        
	 	        return true;
	        }
	       
	        case R.id.refresh:
	        {
	        	try 
	        	{       		
	        		
	        		String res = CricScoreReader.httpGet(Constants.SCORE_PROVIDER_URL);
					
					Match liveMatch = CricScoreReader.getMatchObject(res, Constants.MATCH_LIVE, null);
					
					if (liveMatch!=null)
					{
						displayLiveMatchInfo(liveMatch);
						
						// Update widget						
						Intent updateWidgetIntent = new Intent();
					    updateWidgetIntent.setAction(Constants.UPDATE_WIDGET_FROM_ACTIVITY);
					    sendBroadcast(updateWidgetIntent);
					}
					
					  
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 return true;
	        }
	       
	       
	        default:
	        return super.onOptionsItemSelected(item);
	        }
	}
	
	
	
	
	
}
