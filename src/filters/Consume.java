package filters;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Throwables;

import core.Logger;
import customobjects.HighConsumeItem;
import customobjects.HighConsume_List;


/**
 * Search for high consumption apps
 */
public class Consume
{
	private static HighConsume_List hcList;                    // List of apps detected
	private static String           result;                    // Final result
	private static int              totalOccurrences;          // Total occurrences of lines about app consumption
	private static boolean          enabled = true;            // If result is edited by the user
	private static Semaphore        se      = new Semaphore(1);
	
	/*
	 * Logger.log(Logger.TAG_CONSUME, "Month:\t\t" + matcher.group(1)); Logger.log(Logger.TAG_CONSUME, "Day:\t\t" + matcher.group(2)); Logger.log(Logger.TAG_CONSUME, "Hour:\t\t" + matcher.group(3)); Logger.log(Logger.TAG_CONSUME, "Minute:\t\t" +
	 * matcher.group(4)); Logger.log(Logger.TAG_CONSUME, "Seconds:\t" + matcher.group(5)); Logger.log(Logger.TAG_CONSUME, "Consume:\t" + matcher.group(6)); Logger.log(Logger.TAG_CONSUME, "PID:\t\t" + matcher.group(7)); Logger.log(Logger.TAG_CONSUME,
	 * "Process:\t" + matcher.group(8));
	 */
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
		
		result = "- *The following processes are consuming too much CPU and draining battery:*\n";
		totalOccurrences = 0;
		BufferedReader reader = null; // File reader
		
		try
		{
			hcList = new HighConsume_List(); // List of Apps with high consume
			String panel = "{panel}\n"; // Jira panel tag
			
			// Regex configuration
			String regexBTT = "([0-9]{2})-([0-9]{2}).*([0-2][0-9]):([0-5][0-9]):([0-5][0-9]).*BTTopWriter: ([1-9][0-9].*)\\%.*PID:(.+).*\\(+([A-Za-z0-9_:./\\\\]+)\\)+";
			String regexBTToff = "([0-9]{2})-([0-9]{2}).*([0-2][0-9]):([0-5][0-9]):([0-5][0-9]).*BTTopWriter: ([1-9][\\.|\\,].*)\\%.*PID:(.+).*\\(+([A-Za-z0-9_:./\\\\]+)\\)+";
			String regexScOnOff = ".*BatteryTracerSvc: Data collection.*Screen.*";
			
			Pattern patternBTT = Pattern.compile(regexBTT);
			Pattern patternBTToff = Pattern.compile(regexBTToff);
			Pattern patternScreen = Pattern.compile(regexScOnOff);
			
			Matcher matcherBTT = null;
			Matcher matcherScreen = null;
			
			String sCurrentLine; // Line to be parsed
			String screenStatus = "Unknown   : "; // Last screen status detected
			String file = ""; // File path configuration
			
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();
			
			if (!folder.isDirectory())
			{
				result = "Not a directory";
				se.release();
				return result;
			}
			
			// Search for the file to be parsed
			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isFile() && (listOfFiles[i].getName().toLowerCase().endsWith(".txt") && listOfFiles[i].getName().contains("main")))
				{
					file = listOfFiles[i].getName();
					
					if (!path.equals("."))
						file = path + listOfFiles[i].getName();
					break;
				}
			}
			
			if (file.equals(""))
			{
				throw new FileNotFoundException();
			}
			else
			{
				reader = new BufferedReader(new FileReader(file));
				Logger.log(Logger.TAG_CONSUME, "Log de sistema encontrado!" + file);
			}
			
			Logger.log(Logger.TAG_CONSUME, "Parser running ...");
			
			while ((sCurrentLine = reader.readLine()) != null)
			{
				// Screen ON/OFF parsing
				matcherScreen = patternScreen.matcher(sCurrentLine);
				if (matcherScreen.matches())
				{
					if (sCurrentLine.contains("Screen on"))
					{
						screenStatus = "Screen ON : ";
					}
					else if (sCurrentLine.contains("Screen off"))
					{
						screenStatus = "Screen OFF: ";
					}
				}
				
				// Consume line parsing:
				else
				{
					if (screenStatus.contains("OFF") || screenStatus.contains("Unknown"))
					{
						matcherBTT = patternBTToff.matcher(sCurrentLine);
						if (!matcherBTT.matches())
							matcherBTT = patternBTT.matcher(sCurrentLine);
					}
					else
						matcherBTT = patternBTT.matcher(sCurrentLine);
					
					if (matcherBTT.matches())
					{
						if (matcherBTT.group(8).contains("kworker")) // Group up kworker processes
						{
							int index = hcList.indexOf("kworker");
							
							if (index == -1)
							{
								HighConsumeItem hcItem = new HighConsumeItem("kworker",
								                                             matcherBTT.group(7),
								                                             Float.parseFloat(matcherBTT.group(6).replace(",", ".")),
								                                             screenStatus + sCurrentLine);
								hcList.add(hcItem);
							}
							else
							{
								HighConsumeItem hcItem = hcList.get(index);
								hcItem.updateItem(Float.parseFloat(matcherBTT.group(6).replace(",", ".")), screenStatus + sCurrentLine);
								hcList.set(index, hcItem);
							}
						}
						else
						{
							int index = hcList.indexOf(matcherBTT.group(8));
							
							if (index == -1) // Check if app was not detected already
							{
								HighConsumeItem hcItem = new HighConsumeItem(matcherBTT.group(8),
								                                             matcherBTT.group(7),
								                                             Float.parseFloat(matcherBTT.group(6).replace(",", ".")),
								                                             screenStatus + sCurrentLine);
								hcList.add(hcItem);
								totalOccurrences++;
							}
							else
							{
								HighConsumeItem hcItem = hcList.get(index);
								hcItem.updateItem(Float.parseFloat(matcherBTT.group(6).replace(",", ".")), screenStatus + sCurrentLine);
								hcList.set(index, hcItem);
								totalOccurrences++;
							}
						}
					}
				}
			}
			
			Logger.log(Logger.TAG_CONSUME, "Parser terminated.");
			
			try
			{
				if (reader != null)
					reader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			int hcitems = 0; // Initialize apps detected count
			Iterator<HighConsumeItem> l = hcList.listIterator();
			
			// Organize apps detected list
			while (l.hasNext())
			{
				HighConsumeItem aux = l.next();
				
				if (100.0 * (float) aux.getOccurencesTotal() / (float) totalOccurrences > 1 && !aux.getProcess().contains("motorola.tools.batterytracer"))
				{
					hcitems++;
				}
				else
				{
					l.remove();
				}
			}
			
			hcList.sortItens(); // Sort apps list
			
			for (int i = 0; i < hcList.size(); i++) // Generate final result txt
			{
				result = result + panel + hcList.get(i).toString() + panel;
			}
			
			if (hcList.size() == 0) // If no apps detected
			{
				result = "- No app high consume evidences were found in text logs";
			}
			
			// Logger.log(Logger.TAG_CONSUME, result);
			// Logger.log(Logger.TAG_CONSUME, "Apps detected: " +
			// hcList.size());
			
			Logger.log(Logger.TAG_CONSUME, "Apps detected: " + hcitems + "\nApps Selected: " + hcList.size());
		}
		catch (FileNotFoundException e)
		{
			result = "FileNotFoundException\n" + Throwables.getStackTraceAsString(e);
			e.printStackTrace();
			se.release();
			return result;
		}
		catch (IOException e)
		{
			result = "FileNotFoundException\n" + Throwables.getStackTraceAsString(e);
			e.printStackTrace();
			se.release();
			return result;
		}
		finally
		{
			try
			{
				if (reader != null)
					reader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		se.release();
		return result;
	}
	
	// Getters and Setters:
	public static HighConsume_List getHCList()
	{
		return hcList;
	}
	
	public static String getResult()
	{
		return result;
	}
	
	public static boolean isEnabled()
	{
		return enabled;
	}
	
	public static void setEnabled(boolean onoff)
	{
		enabled = onoff;
	}
	
	public static void updateResult(String editedResult)
	{
		result = editedResult;
	}
}