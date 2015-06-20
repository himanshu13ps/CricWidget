package com.hps.object;

import java.util.Date;

public class Match 
{
	public String num;
	public String complete;
	public Date matchDate;
	public Date deadline;
	public String startDate;
	public String startTime;
	public String series;
	public int inningId;
	public String status;
	public String venue;
	public Team team1;
	public Team team2;	
	public Batsman striker;
	public Batsman nonStriker;
	public Bowler currBowler;
	public String resultText;

	public Match() 
	{
		this.team1 = new Team();
		this.team2 = new Team();
		this.striker = new Batsman();
		this.nonStriker = new Batsman();
		this.currBowler = new Bowler();
	}
	
	
}
