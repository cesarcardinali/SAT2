package filters;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import supportive.DateTimeOperator;
import supportive.FileFinder;

import com.google.common.base.Throwables;

import core.Logger;
import core.SharedObjs;
import customobjects.AlarmItem;
import customobjects.Alarms_List;


/**
 * Search for alarm wakeups overhead
 */
public class Alarm
{
	private static Alarms_List    alarmList;
	private static String         result;
	private static BufferedReader reader;
	private static boolean        enabled = true;
	private static Semaphore      se      = new Semaphore(1);
	
	public static String makelog(String path)
	{
		try
		{
			se.acquire();
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		
		result = SharedObjs.optionsPane.getTextAlarms() + "\n"; // Cumulative result
		reader = null; // File reader
		alarmList = new Alarms_List(); // List of Apps with high consume
		
		long now = System.currentTimeMillis();
		String sCurrentLine; // Read line to be parsed
		String file = ""; // File name to be parsed
		String panel = "{panel}\n"; // Panel tag
		String regexAlarmLine = "([0-9]{2}-[0-9]{2} [0-2][0-9]:[0-5][0-9]:[0-5][0-9]).*send.*\\*(.+)\\*:([a-z\\W_/$]*)([A-Z]+.+)\\}.*";
		
		Pattern patternAlarmLine = Pattern.compile(regexAlarmLine); // Pattern configuration
		Matcher matcherAlarmLine = null; // Matcher
		
		FileFinder ff = new FileFinder(path);
		file = ff.getFilePath(FileFinder.SYSTEM);
		
		// Check if is directory exists
		if (!ff.getFound())
		{
			result = file;
			se.release();
			return result;
		}
		
		// Initialize file reader
		try
		{
			reader = new BufferedReader(new FileReader(file));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			result = "FileNotFoundException\n" + Throwables.getStackTraceAsString(e);
			se.release();
			return result;
		}
		
		Logger.log(Logger.TAG_ALARM, "Log de sistema encontrado: " + file);
		
		int averageOccurrences = 1;
		Date parsedDate = null;
		SimpleDateFormat dateFormat = null;
		
		try
		{
			while ((sCurrentLine = reader.readLine()) != null) // Read new line
			{
				matcherAlarmLine = patternAlarmLine.matcher(sCurrentLine); // Try to match regex
				if (!sCurrentLine.contains("TIME_TICK") && matcherAlarmLine.matches())
				{
					AlarmItem alarm = null;
					
					if (matcherAlarmLine.group(3) != null)
					{
						try
						{
							dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss"); // Format date
							parsedDate = dateFormat.parse(matcherAlarmLine.group(1));
						}
						catch (Exception e)
						{
							Logger.log(Logger.TAG_ALARM, "********Error: " + Throwables.getStackTraceAsString(e));
						}
						
						// Create a Alarm item
						if (matcherAlarmLine.group(3).length() > 0)
						{
							alarm = new AlarmItem(parsedDate,
							                      matcherAlarmLine.group(2),
							                      matcherAlarmLine.group(3).substring(0, matcherAlarmLine.group(3).length() - 1),
							                      matcherAlarmLine.group(4),
							                      matcherAlarmLine.group(0));
						}
						else
						{
							alarm = new AlarmItem(parsedDate, matcherAlarmLine.group(2), "Unknown", matcherAlarmLine.group(4), matcherAlarmLine.group(0));
						}
					}
					
					if (alarm != null)
					{
						int index = alarmList.alarmIndexOf(alarm); // Try to find the Alarm in the Alarm List
						
						if (index == -1) // If not found create a new entrance
						{
							alarmList.add(alarm);
						}
						else
						// If found update the entrance
						{
							alarmList.get(index).alarmUpdate(parsedDate, sCurrentLine);
						}
						
						averageOccurrences++; // Increase occurrences count
					}
					
				}
			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			result = "IOException\n" + Throwables.getStackTraceAsString(e);
			se.release();
			return result;
		}
		
		if (alarmList.size() > 0) // If alarms detected
		{
			averageOccurrences = averageOccurrences / alarmList.size();
			
			// Sort AlarmList
			alarmList.sortItens();
			Logger.log(Logger.TAG_ALARM, String.valueOf(averageOccurrences));
			Iterator<AlarmItem> l = alarmList.listIterator();
			
			while (l.hasNext())
			{
				AlarmItem aux = l.next();
				
				if (aux.getOccurences() < averageOccurrences)
				{
					l.remove();
				}
			}
			
			result = "- *Alarm overhead issues:*\n";
			
			for (int i = 0; i < alarmList.size(); i++)
			{
				result = result + panel + alarmList.get(i).toString() + panel;
			}
			
			result += "\n" + DateTimeOperator.getTimeStringFromMillis((System.currentTimeMillis() - now));
		}
		else
		{
			result = "- No detailed alarm issue evidences were found in text logs";
			se.release();
		}
		return result;
	}
	
	// Getters and Setters
	public static int getListSize()
	{
		return alarmList.size();
	}
	
	public static Alarms_List getList()
	{
		return alarmList;
	}
	
	public static String getResult()
	{
		return result;
	}
	
	public static void updateResult(String editedResult)
	{
		result = editedResult;
	}
	
	public static boolean isEnabled()
	{
		return enabled;
	}
	
	public static void setEnabled(boolean onoff)
	{
		enabled = onoff;
	}
}