package supportive.preanalyzers.logsparser;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import supportive.DateTimeOperator;
import core.Logger;
import core.SharedObjs;


public class MainParser
{
	public class wifiPeriod
	{
		public long   startTime;
		public long   endTime;
		public String startLine;
		public String endLine;
		
		public wifiPeriod()
		{
			startTime = 0;
			endTime = 0;
			startLine = "";
			endLine = "";
		}
		
		public long getDuration()
		{
			return endTime - startTime;
		}
	}
	
	private String                path;
	private String                year;
	private LogState              longerDischarge;
	private LogStatesData         statesData;
	private ArrayList<wifiPeriod> wifiPeriods;
	private String                latestMonthDay;
	private long                  startTime;
	private long                  endTime;
	private long                  totalLogTime;
	private long                  totalTetherTime;
	
	public MainParser(String path)
	{
		this.path = path + "/";
		startTime = 0;
		statesData = new LogStatesData();
		wifiPeriods = new ArrayList<wifiPeriod>();
		year = getYearFromBugReport();
		latestMonthDay = "";
	}
	
	public boolean parse()
	{
		if (year.equals(""))
		{
			Logger.log(Logger.TAG_CONSUME, "Could not parse year from logs. So, SAT can not parse Main log.");
			SharedObjs.crsManagerPane.addLogLine("Could not find 'year' from bugreport. SAT will not parse main log.");
			
			return false;
		}
		System.out.println("Running at " + path);
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		String mainFile = "";
		
		if (!folder.isDirectory())
		{
			System.out.println("Not a directory");
			return false;
		}
		
		// Search for the file to be parsed
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile() && (listOfFiles[i].getName().toLowerCase().endsWith(".txt") && listOfFiles[i].getName().contains("main")))
			{
				mainFile = listOfFiles[i].getName();
				break;
			}
		}
		
		if (mainFile.equals(""))
		{
			Logger.log(Logger.TAG_CONSUME, "Main file not found:" + path + mainFile);
			return false;
		}
		
		String rxPlug = "ACTION_BATTERY_CHANGED received.+plugged: ([0-9])";
		Pattern ptPlug = Pattern.compile(rxPlug);
		Matcher mPlug;
		
		// Logger.log(Logger.TAG_CONSUME, "Main file found:" + path + mainFile);
		
		BufferedReader br;
		try
		{
			br = new BufferedReader(new FileReader(path + mainFile));
			String sCurrentLine;
			String sLastLine = "";
			LogState logState = new LogState();
			wifiPeriod wifiPeriod = new wifiPeriod();
			Pattern ptDateLine = Pattern.compile("^\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d ");// 09-27 11:32:45.622
			Matcher mDate;
			long actualTime = -1;
			long nextTime = -1;
			
			while ((sCurrentLine = br.readLine()) != null)
			{
				mDate = ptDateLine.matcher(sCurrentLine);
				if (mDate.find())
				{
					nextTime = DateTimeOperator.getMillis(parseLineDate(sCurrentLine)[0]);
					
					if ((nextTime - actualTime) < -60000 && (nextTime - actualTime) > -3600000)
					{
						System.out.println("-60sec dif ++ " + sLastLine);
						System.out.println("-60sec dif ++ " + sCurrentLine + "\n");
						sLastLine = sCurrentLine;
						continue;
					}
					
					if ((nextTime - actualTime) > 3600000L || (nextTime - actualTime) < -3600000L)
					{
						System.out.println("+/-1h dif -- " + sLastLine);
						System.out.println("+/-1h dif -- " + sCurrentLine + "\n");
						sLastLine = sCurrentLine;
						
						actualTime = nextTime;
						startTime = actualTime;
						
						if (statesData.size() > 0)
						{
							statesData.clear();
						}
						else if (logState.getStart() > 0)
						{
							logState = new LogState();
							logState.setStart(actualTime);
						}
						
						if (wifiPeriods.size() > 0)
						{
							wifiPeriods.clear();
						}
						
						if (wifiPeriod.startTime > 0)
						{
							wifiPeriod = new wifiPeriod();
						}
					}
					else
					{
						actualTime = nextTime;
						endTime = actualTime;
					}
					
					if (startTime == 0)
					{
						startTime = actualTime;
						System.out.println(parseLineDate(sCurrentLine)[0] + " - " + startTime);
					}
					
					if (logState.getStart() == -1)
					{
						logState.setStart(actualTime);
					}
					
					// Find battery status line
					mPlug = ptPlug.matcher(sCurrentLine);
					if (mPlug.find())
					{
						// System.out.println("Plugged: " + mPlug.group(1));
						
						if (logState.getStatus() == -1)
						{
							logState.setStatus(Integer.parseInt(mPlug.group(1)));
						}
						else if (Integer.parseInt(mPlug.group(1)) != logState.getStatus())
						{
							logState.setEnd(actualTime);
							statesData.add(logState);
							logState = new LogState();
						}
					}
					
					// Find tethering info
					if (sCurrentLine.contains("Tethering: Tethering wlan0"))
					{
						wifiPeriod.startTime = actualTime;
						wifiPeriod.startLine = sCurrentLine;
						// System.out.println(sCurrentLine);
					}
					else if (sCurrentLine.contains("Tethering: Untethering wlan0"))
					{
						if (wifiPeriod.startTime == 0)
						{
							wifiPeriod.startTime = startTime;
							wifiPeriod.startLine = "It has begun before this log time";
							wifiPeriod.endTime = actualTime;
							wifiPeriod.endLine = sCurrentLine;
						}
						else
						{
							wifiPeriod.endTime = actualTime;
							wifiPeriod.endLine = sCurrentLine;
						}
						
						// System.out.println(sCurrentLine);
						wifiPeriods.add(wifiPeriod);
						wifiPeriod = new wifiPeriod();
					}
				}
				else
				{
					if (sCurrentLine.equals("Finished dumping"))
						break;
				}
			}
			
			br.close();
			
			if (logState.getEnd() == -1)
			{
				logState.setEnd(actualTime);
				
				if(logState.getStatus() == -1)
				{
					Logger.log(Logger.TAG_MAINLOG_PARSER, "Could not generate status data");
					return false;
				}
				
				statesData.add(logState);
			}
			
			longerDischarge = statesData.getLongerDischargingPeriod();
			
			if (wifiPeriod.endTime == 0 && wifiPeriod.startTime > 0)
			{
				wifiPeriod.endTime = actualTime;
				wifiPeriod.endLine = "Tethering still running ...";
				wifiPeriods.add(wifiPeriod);
			}
			
			totalLogTime = endTime - startTime;
			
			return true;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			Logger.log(Logger.TAG_MAINLOG_PARSER, "Main file not found");
			return false;
		}
		catch (IOException e)
		{
			Logger.log(Logger.TAG_MAINLOG_PARSER, "Failed to close file");
			e.printStackTrace();
			return true;
		}
	}
	
	// Resolution methods
	public void showAcquiredData()
	{
		for (LogState s : statesData)
		{
			System.out.println("Periodo:\n" + "Start: " + s.getStartDate() + " > End: " + s.getEndDate() + " > Status: " + s.getStatus());
		}
		
		System.out.println("Periodo total do log:");
		System.out.println("Begins " + new Date(startTime) + "(" + startTime + ")");
		System.out.println("Ends " + new Date(endTime) + "(" + endTime + ")");
		System.out.println("Total running time " + DateTimeOperator.getTimeStringFromMillis(totalLogTime) + " or " + (totalLogTime) + "ms");
		System.out.println("Longer discharge time " + DateTimeOperator.getTimeStringFromMillis(longerDischarge.getDuration()) + " or " + (totalLogTime) + "ms");
		System.out.println("From " + longerDischarge.getStartDate() + " to " + longerDischarge.getEndDate());
	}
	
	public void showTetheringData()
	{
		for (wifiPeriod w : wifiPeriods)
		{
			System.out.println("Tethering data:");
			System.out.println("Total time: " + (w.getDuration()) + "ms  >  " + DateTimeOperator.getTimeStringFromMillis(w.getDuration()));
			System.out.println("Started at: " + w.startLine);
			System.out.println("Stopped at: " + w.endLine);
		}
		
		System.out.println("Total Tethering time " + totalTetherTime + "ms  >  " + DateTimeOperator.getTimeStringFromMillis(totalTetherTime));
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		System.out.println("Tethering running for " + getTetherPercentage() + "% of total log time");
	}
	
	public boolean checkForTethering()
	{
		totalTetherTime = 0;
		
		for (wifiPeriod w : wifiPeriods)
		{
			if (w.startTime < longerDischarge.getStart())
			{
				w.startTime = longerDischarge.getStart();
				if (w.endTime < longerDischarge.getStart())
				{
					w.endTime = longerDischarge.getStart();
				}
			}
			
			if (w.endTime > longerDischarge.getEnd())
			{
				w.endTime = longerDischarge.getEnd();
				if (w.startTime > longerDischarge.getEnd())
				{
					w.startTime = longerDischarge.getEnd();
				}
			}
			
			totalTetherTime = totalTetherTime + w.getDuration();
		}
		
		if (100.0 * totalTetherTime / longerDischarge.getDuration() > 11.0)
		{
			System.out.println("Tethering case: TRUE");
			return true;
		}
		else
		{
			System.out.println("Tethering case: FALSE");
			return false;
		}
	}
	
	// Supportive methods
	private String[] parseLineDate(String line)
	{
		String parts[] = line.split("     |    |   |  | ", 6);
		String parsed[] = new String[2];
		
		if (parts.length > 3)
		{
			if (parts[0].length() > 18)
			{
				System.out.println("---------------- " + line);
				parsed[0] = parsed[0].substring(2, 19);
			}
			parsed[1] = parts[parts.length - 1];
			
			if (!latestMonthDay.equals("") && latestMonthDay.equals("12-31") && parts[0].equals("01-01"))
			{
				year = "" + (Integer.parseInt(year) + 1);
			}
			parsed[0] = year + "-" + parts[0] + " " + parts[1];
			
			latestMonthDay = parts[0];
			
			return parsed;
		}
		else
		{
			return null;
		}
		
	}
	
	private String getYearFromBugReport()
	{
		File folder = new File(path);
		for (File file : folder.listFiles())
		{
			if (file.getName().contains("bugreport"))
			{
				return file.getName().substring(file.getName().indexOf("2"), file.getName().indexOf("2") + 4);
			}
		}
		
		// Try to get year from BTD file - BATTRIAGE-213
		for (File file : folder.listFiles())
		{
			if (file.getName().contains(".btd"))
			{
				return file.getName().substring(file.getName().indexOf("_", 10) + 1, file.getName().indexOf("_", 10) + 5);
			}
		}
		
		return "";
	}
	
	// Getters, Setters and Complementary methods
	public String getTetherPercentage()
	{
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		return df.format(100.0 * totalTetherTime / (longerDischarge.getDuration()));
	}
	
	public LogState getLongerDischarge()
	{
		return longerDischarge;
	}
	
	public LogStatesData getStatesData()
	{
		return statesData;
	}
	
	public ArrayList<wifiPeriod> getWifiPeriods()
	{
		return wifiPeriods;
	}
	
	public long getTotalLogTime()
	{
		return totalLogTime;
	}
	
	public long getTotalTetherTime()
	{
		if (totalTetherTime == 0)
		{
			if (longerDischarge == null)
			{
				if (statesData.getLongerDischargingPeriod() == null)
				{
					return -1;
				}
				else
				{
					longerDischarge = statesData.getLongerDischargingPeriod();
				}
			}
			
			for (wifiPeriod w : wifiPeriods)
			{
				if (w.startTime < longerDischarge.getStart())
				{
					w.startTime = longerDischarge.getStart();
					if (w.endTime < longerDischarge.getStart())
					{
						w.endTime = longerDischarge.getStart();
					}
				}
				
				if (w.endTime > longerDischarge.getEnd())
				{
					w.endTime = longerDischarge.getEnd();
					if (w.startTime > longerDischarge.getEnd())
					{
						w.startTime = longerDischarge.getEnd();
					}
				}
				
				totalTetherTime = totalTetherTime + w.getDuration();
			}
		}
		
		return totalTetherTime;
	}
	
	public void setLongerDischarge(LogState longerDischarge)
	{
		this.longerDischarge = longerDischarge;
	}
	
	public void setStatesData(LogStatesData statesData)
	{
		this.statesData = statesData;
	}
	
	public void setWifiPeriods(ArrayList<wifiPeriod> wifiPeriods)
	{
		this.wifiPeriods = wifiPeriods;
	}
	
	public void setTotalLogTime(long totalLogTime)
	{
		this.totalLogTime = totalLogTime;
	}
	
	public void setTotalTetherTime(long totalTetherTime)
	{
		this.totalTetherTime = totalTetherTime;
	}
}
