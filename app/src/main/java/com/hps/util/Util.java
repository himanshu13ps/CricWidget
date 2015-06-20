package com.hps.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.hps.R;
import com.hps.R.drawable;
import com.hps.object.Match;
import com.hps.object.Team;

import android.util.Log;

public class Util 
{
	public static Match MatchObjectParser(Element matchElement)
	{
		Match m = new Match();
		
		// Get attributes
		m.series = getSingleNode(matchElement, "Series").getFirstChild().getNodeValue();
		m.status = getSingleNode(matchElement, "Status").getFirstChild().getNodeValue();
		m.venue = getSingleNode(matchElement, "Venue").getFirstChild().getNodeValue();
		m.num = getSingleNode(matchElement, "Name").getFirstChild().getNodeValue();
		m.inningId = Integer.parseInt(getSingleNode(matchElement, "Status").getAttribute("inningId"));
		m.resultText = getSingleNode(matchElement, "ResultText").getFirstChild().getNodeValue();
		if (m.resultText.contains("Royal Challengers Bangalore"))
		{
			m.resultText = m.resultText.replace("Royal Challengers Bangalore", "Royal Challengers");
		}
		
		// Get Date
		String dateString = getSingleNode(matchElement, "StartDate").getFirstChild().getNodeValue();
		SimpleDateFormat ausFormat = new SimpleDateFormat("M/d/yyyy HH:mm:ss");
		SimpleDateFormat dt = new SimpleDateFormat("EEE MMM d, h:mm a");
		SimpleDateFormat ti = new SimpleDateFormat("h:mm a");
		try 
		{
			Date aus = ausFormat.parse(dateString);
			m.matchDate = new Date();
			m.matchDate.setTime(aus.getTime() -  4*60*60*1000 - 30*60*1000);
			
			m.startDate = dt.format(m.matchDate).toString()+" IST";
			m.startTime = "Starts "+ti.format(m.matchDate).toString()+" IST";
			
			m.deadline = new Date();
			m.deadline.setTime(m.matchDate.getTime() + (7*30*60*1000 + 15*60*1000+ (new Date()).getTimezoneOffset()*60*1000));
		} 
		catch (ParseException e) 
		{
			m.matchDate=null;
			m.deadline=null;
			
			m.startDate=null;
			m.startTime=null;
		}

		// Get Teams info
		Element teamsList =  getSingleNode(matchElement, "Teams");
		NodeList nlTeams  = teamsList.getElementsByTagName("Team");
		
		if(nlTeams != null && nlTeams.getLength() > 0) 
		{							
			Element team1Ele = (Element)nlTeams.item(0);
			m.team1.id = team1Ele.getAttribute("teamId"); 
			m.team1.longName = getSingleNode(team1Ele, "LongName").getFirstChild().getNodeValue();
			GetShortNameResId(m.team1.id, m.team1);
				
			Element team2Ele = (Element)nlTeams.item(1);
			m.team2.id = team2Ele.getAttribute("teamId");
			m.team2.longName = getSingleNode(team2Ele, "LongName").getFirstChild().getNodeValue();
			GetShortNameResId(m.team2.id, m.team2);
					
		}
		
		
		// Get Innings info
		Element inningsList =  getSingleNode(matchElement, "Innings");
		NodeList nlInnings  = inningsList.getElementsByTagName("Inning");
		if(nlInnings != null && nlInnings.getLength() > 0) 
		{
			switch (m.inningId)
			{
				case 1:
				{
					Element Inning1Ele = (Element)nlInnings.item(0);
					if (Inning1Ele.getAttribute("battingTeamId").equalsIgnoreCase(m.team1.id))
					{
						addInningsInfo(Inning1Ele, m.team1);	
						m.team1.batting =true;
						m.team2.batting =false;
					}
					else if (Inning1Ele.getAttribute("battingTeamId").equalsIgnoreCase(m.team2.id))
					{
						addInningsInfo(Inning1Ele, m.team2);
						m.team2.batting =true;
						m.team1.batting =false;
					}
					break;
				}
				case 2:
				{
					Element Inning1Ele = (Element)nlInnings.item(0);
					Element Inning2Ele = (Element)nlInnings.item(1);
					if (Inning1Ele.getAttribute("battingTeamId").equalsIgnoreCase(m.team1.id))
					{
						addInningsInfo(Inning1Ele, m.team1);
						addInningsInfo(Inning2Ele, m.team2);
					}
					else if (Inning1Ele.getAttribute("battingTeamId").equalsIgnoreCase(m.team2.id))
					{
						addInningsInfo(Inning1Ele, m.team2);
						addInningsInfo(Inning2Ele, m.team1);
					}	
					
					if (m.inningId == m.team1.teamInningId)
					{
						m.team1.batting =true;
						m.team2.batting =false;
					}
					else
					{
						m.team2.batting =true;
						m.team1.batting =false;
					}
					break;
				}
				default:
				{
					m.team1.batting =true;
					m.team2.batting =false;
					break;
				}
			}
		}
		
		// Get Batsmen info
		Element batsmenList =  getSingleNode(matchElement, "Batsmen");
		NodeList nlStriker  = batsmenList.getElementsByTagName("Striker");		
		if(nlStriker != null && nlStriker.getLength() > 0) 
		{
			Element strikerEle = (Element)nlStriker.item(0);
			
			m.striker.firstName = getSingleNode(strikerEle, "FirstName").getFirstChild().getNodeValue();
			m.striker.lastName = getSingleNode(strikerEle, "SurName").getFirstChild().getNodeValue();
			m.striker.shortName = getSingleNode(strikerEle, "ShortName").getFirstChild().getNodeValue();
			m.striker.runs = getSingleNode(strikerEle, "Runs").getFirstChild().getNodeValue();
			m.striker.balls = getSingleNode(strikerEle, "Balls").getFirstChild().getNodeValue();
		}
		else
		{
			m.striker = null;
		}
		
		
		NodeList nlNonStriker  = batsmenList.getElementsByTagName("NonStriker");
		if(nlNonStriker != null && nlNonStriker.getLength() > 0) 
		{
			Element nonStrikerEle = (Element)nlNonStriker.item(0);
			
			m.nonStriker.firstName = getSingleNode(nonStrikerEle, "FirstName").getFirstChild().getNodeValue();
			m.nonStriker.lastName = getSingleNode(nonStrikerEle, "SurName").getFirstChild().getNodeValue();
			m.nonStriker.shortName = getSingleNode(nonStrikerEle, "ShortName").getFirstChild().getNodeValue();
			m.nonStriker.runs = getSingleNode(nonStrikerEle, "Runs").getFirstChild().getNodeValue();
			m.nonStriker.balls = getSingleNode(nonStrikerEle, "Balls").getFirstChild().getNodeValue();
		}
		else
		{
			m.nonStriker = null;
		}
		
		
		//	Get Bowler info
		Element bowlerList =  getSingleNode(matchElement, "Bowlers");
		NodeList nlBowler  = bowlerList.getElementsByTagName("CurrentBowler");		
		if(nlBowler != null && nlBowler.getLength() > 0) 
		{
			Element bowlerEle = (Element)nlBowler.item(0);
			
			m.currBowler.firstName = getSingleNode(bowlerEle, "FirstName").getFirstChild().getNodeValue();
			m.currBowler.lastName = getSingleNode(bowlerEle, "SurName").getFirstChild().getNodeValue();
			m.currBowler.shortName = getSingleNode(bowlerEle, "ShortName").getFirstChild().getNodeValue();
			m.currBowler.wickets = getSingleNode(bowlerEle, "Wickets").getFirstChild().getNodeValue();
			m.currBowler.runs = getSingleNode(bowlerEle, "Runs").getFirstChild().getNodeValue();
			m.currBowler.overs = getSingleNode(bowlerEle, "Overs").getFirstChild().getNodeValue();
		}
		else
		{
			m.currBowler = null;
		}
		
		return m;
	}
	
	private static void addInningsInfo(Element inningEle, Team team) 
	{
		team.wickets = getSingleNode(inningEle, "Wickets").getFirstChild().getNodeValue();
		team.runs = getSingleNode(inningEle, "Runs").getFirstChild().getNodeValue();
		team.overs = getSingleNode(inningEle, "Overs").getFirstChild().getNodeValue();
		team.teamInningId = Integer.parseInt(inningEle.getAttribute("inningId"));
		
	}

	public static Element getSingleNode(Element el, String tagName) 
	{
		Element el2=null;		

		NodeList nlSeries  = el.getElementsByTagName(tagName);
		
		if(nlSeries != null && nlSeries.getLength() > 0) 
		{
			for(int j = 0 ; j < nlSeries.getLength();j++) 
			{						
				el2 = (Element)nlSeries.item(j);
			}
		}
		return el2;
	}


	public static void GetShortNameResId(String id, Team team) 
	{
		if (id.equalsIgnoreCase("190"))
		{
			team.shortName = "RCB";
			team.imageId = R.drawable.rcb;
			team.longName = "Royal Challengers";
		}
		else if (id.equalsIgnoreCase("191"))
		{
			team.shortName = "CSK";
			team.imageId = R.drawable.csk;
		}
		else if (id.equalsIgnoreCase("192"))
		{
			team.shortName = "DC";
			team.imageId = R.drawable.dc;
		}
		else if (id.equalsIgnoreCase("193"))
		{
			team.shortName = "DD";
			team.imageId = R.drawable.dd;
		}			
		else if (id.equalsIgnoreCase("194"))
		{
			team.shortName = "KXIP";
			team.imageId = R.drawable.kxip;
		}
		else if (id.equalsIgnoreCase("195"))
		{
			team.shortName = "KKR";
			team.imageId = R.drawable.kkr;
		}
		else if (id.equalsIgnoreCase("196"))
		{
			team.shortName = "MI";
			team.imageId = R.drawable.mi;
		}
		else if (id.equalsIgnoreCase("197"))
		{
			team.shortName = "RR";
			team.imageId = R.drawable.rr;
		}
		else if (id.equalsIgnoreCase("241"))
		{
			team.shortName = "PWI";
			team.imageId = R.drawable.pwi;
		}
	}
}
