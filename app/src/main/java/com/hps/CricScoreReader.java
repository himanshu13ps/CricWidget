package com.hps;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hps.constants.Constants;
import com.hps.object.Match;
import com.hps.util.Util;

import android.util.Log;

public class CricScoreReader 
{

	public static String httpGet(String urlStr) throws IOException {
		  URL url = new URL(urlStr);
		  HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();

		  if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }

		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line);
		  }
		  rd.close();

		  conn.disconnect();
		  return sb.toString();
		}
	
	
	public static Match getLiveMatchObject(String result)
	{
		try 
		{			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			InputStream is = new ByteArrayInputStream(result.getBytes());
			
				//Using factory get an instance of document builder
				DocumentBuilder db = dbf.newDocumentBuilder();

				//parse using builder to get DOM representation of the XML file
				Document dom = db.parse(is);	
				
				//get the root element
				Element docEle = dom.getDocumentElement();
							
				NodeList nlMatches = docEle.getElementsByTagName("Matches");
				if(nlMatches != null && nlMatches.getLength() > 0) 
				{
					
					for(int i = 0 ; i < nlMatches.getLength();i++) 
					{						
						Element el = (Element)nlMatches.item(i);
						if (el.getAttribute("statusId").equalsIgnoreCase("L"))
						{
							NodeList nlMatch = el.getElementsByTagName("Match");
							if(nlMatch != null && nlMatch.getLength() > 0) 
							{
								
								for(int j = 0 ; j < nlMatch.getLength();j++) 
								{						
									Element elMatch = (Element)nlMatch.item(j);
									Match m = Util.MatchObjectParser(elMatch);		
									
									if (m.series.equalsIgnoreCase("2013 Indian Premier League") && (m.status.contains("Live")|| m.status.contains("Break")))
									{
										return m;														
									}					
								}					
							}
						}
											
					}					
				}
		} 
		catch (IOException e) {		
			e.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}
		
		return null;
		
	}
	
	public static Match getNextMatchObject(String result)
	{
		try 
		{			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			InputStream is = new ByteArrayInputStream(result.getBytes());
			
				//Using factory get an instance of document builder
				DocumentBuilder db = dbf.newDocumentBuilder();

				//parse using builder to get DOM representation of the XML file
				Document dom = db.parse(is);	
				
				//get the root element
				Element docEle = dom.getDocumentElement();
							
				NodeList nlMatches = docEle.getElementsByTagName("Matches");
				if(nlMatches != null && nlMatches.getLength() > 0) 
				{
					
					for(int i = 0 ; i < nlMatches.getLength();i++) 
					{						
						Element el = (Element)nlMatches.item(i);
						if (el.getAttribute("statusId").equalsIgnoreCase("N"))
						{
							NodeList nlMatch = el.getElementsByTagName("Match");
							if(nlMatch != null && nlMatch.getLength() > 0) 
							{
								
								for(int j = 0 ; j < nlMatch.getLength();j++) 
								{						
									Element elMatch = (Element)nlMatch.item(j);
									Match m = Util.MatchObjectParser(elMatch);		
									
									if (m.series.equalsIgnoreCase("2013 Indian Premier League"))
									{
										return m;														
									}					
								}					
							}
						}
											
					}					
				}
		} 
		catch (IOException e) {		
			e.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}
		
		return null;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try {
			//String result = httpGet("http://static.espncricinfo.com/rss/livescores.xml"); 
			String result = httpGet("http://ca.ckt.webservice.sportsflash.com.au/WebService.asmx/GetScoreboardAppContent");
			//System.out.println(result);
			
			
			/*// Create transformer
			TransformerFactory tff = TransformerFactory.newInstance();
			Transformer tf = tff.newTransformer();
			StringReader reader = new StringReader(result);
			// Set output transformation
			StreamResult result2 = new StreamResult(System.out);
			tf.setOutputProperty(OutputKeys.INDENT,"yes");
			tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","6");
			tf.transform(new javax.xml.transform.stream.StreamSource(reader), result2);
			System.out.println();	*/
			
			
			ArrayList<Match> matchList = new ArrayList();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			InputStream is = new ByteArrayInputStream(result.getBytes());
			try 
			{

				//Using factory get an instance of document builder
				DocumentBuilder db = dbf.newDocumentBuilder();

				//parse using builder to get DOM representation of the XML file
				Document dom = db.parse(is);	
				
				//get the root element
				Element docEle = dom.getDocumentElement();
							
				NodeList nl = docEle.getElementsByTagName("Match");
				if(nl != null && nl.getLength() > 0) 
				{
					
					for(int i = 0 ; i < nl.getLength();i++) 
					{						
						Element el = (Element)nl.item(i);
						Match m = Util.MatchObjectParser(el);		
						
						if (m.series.equalsIgnoreCase("2013 Indian Premier League"))
						{
							System.out.println(m.series+" : "+m.status+" : "+m.team1+" Vs "+m.team2);
							System.out.println();
						}
					
					}
					
				}
			
			}catch(ParserConfigurationException pce) {
				pce.printStackTrace();
			}catch(SAXException se) {
				se.printStackTrace();
			}catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
			System.out.println("done");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Ecxeption");
			e.printStackTrace();
		}

	}


	public static Match getMatchObject(String result, String matchType, String num) 
	{
		try 
		{			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			InputStream is = new ByteArrayInputStream(result.getBytes());
			
				//Using factory get an instance of document builder
				DocumentBuilder db = dbf.newDocumentBuilder();

				//parse using builder to get DOM representation of the XML file
				Document dom = db.parse(is);	
				
				//get the root element
				Element docEle = dom.getDocumentElement();
							
				NodeList nlMatches = docEle.getElementsByTagName("Matches");
				
				if(nlMatches != null && nlMatches.getLength() > 0) 
				{ 
					
					for(int i = 0 ; i < nlMatches.getLength();i++) 
					{						
						Element el = (Element)nlMatches.item(i);
						if (el.getAttribute("statusId").equalsIgnoreCase(matchType))
						{
							NodeList nlMatch = el.getElementsByTagName("Match");
							
							if(nlMatch != null && nlMatch.getLength() > 0) 
							{
								
								for(int j = 0 ; j < nlMatch.getLength();j++) 
								{						
									Element elMatch = (Element)nlMatch.item(j);
									String series = Util.getSingleNode(elMatch, "Series").getFirstChild().getNodeValue();
									String matchNumber = Util.getSingleNode(elMatch, "Name").getFirstChild().getNodeValue();
									
									if (series.contains("2013 Indian Premier League"))
									{										
										if (matchType.equalsIgnoreCase(Constants.MATCH_FINISHED) && num !=null)
										{
											
											/*String[] parts = num.split(" ");
											String finMatch = "Match "+(Integer.parseInt(parts[1])-1);
											//Log.v("Parse Match", "Finished Match: "+finMatch);
											if (matchNumber.contains(finMatch))
											{*/
												Match m = Util.MatchObjectParser(elMatch);	
												return m;
											//}
										}
										else
										{											
											Match m = Util.MatchObjectParser(elMatch);	
											return m;
										}
																								
									}					
								}					
							}
						}
											
					}					
				}
		} 
		catch (IOException e) {		
			e.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}
		
		return null;
	}

	

}
