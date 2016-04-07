package filters;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import supportive.FileFinder;

import com.google.common.base.Throwables;

import core.Logger;


/**
 * Search for B2G evidences
 */
public class B2G
{
	private static String  result;          // Parser result
	private static boolean edited  = false; // If result edited by the user
	private static boolean enabled = true;  // If filter is enabled
	                                        
	public static String makelog(String path)
	{
		BufferedReader br = null;
		result = "";
		String bug2goData = "";
		String sCurrentLine = "";
		
		try
		{
			String file_report = "";
			
			FileFinder ff = new FileFinder(path);
			file_report = ff.getFilePath(FileFinder.SYSTEM);
			
			// Check if is directory exists
			if (!ff.getFound())
			{
				result = "System " + file_report + "\n";
			}
			else
			{
				Logger.log(Logger.TAG_B2G, "\nB2G: system file: " + file_report);
				
				br = new BufferedReader(new FileReader(file_report));
				
				// Search for b2g evidences
				while ((sCurrentLine = br.readLine()) != null)
				{
					if (sCurrentLine.contains("tag=\"BUG2GO-UploadWorker\"") || sCurrentLine.contains("tag=BUG2GO-UploadWorker"))
					{
						bug2goData = bug2goData + sCurrentLine + "\n";
					}
				}
				
				// Close file reader
				if (br != null)
					br.close();
				
				// If found a reasonable amount of evidences
				if (bug2goData.length() > 12)
				{
					bug2goData = "{noformat}\n" + bug2goData + "{noformat}\n";
				}
			}
			
			file_report = "";
			
			if (bug2goData.length() < 2000) // If evidences found were
			// not enough
			{
				// Look for the secondary file
				file_report = ff.getFilePath(FileFinder.MAIN);
				
				// Check if is directory exists
				if (!ff.getFound())
				{
					result += "Main " + file_report;
					return result;
				}
				
				Logger.log(Logger.TAG_B2G, "\nB2G: main file: " + file_report);
				
				// Try to open file
				if (file_report.equals(""))
				{
					result = "";
					throw new FileNotFoundException();
				}
				
				br = new BufferedReader(new FileReader(file_report));
				String newData;
				newData = "";
				
				while ((sCurrentLine = br.readLine()) != null) // Search for more evidences
				{
					if (sCurrentLine.contains("BUG2GO-DBAdapter: update"))
					{
						newData = newData + sCurrentLine + "\n";
					}
				}
				
				Logger.log(Logger.TAG_B2G, "File read");
				
				if (newData.length() > 20)
				{
					bug2goData = bug2goData + "{noformat}\n" + newData + "{noformat}\n";
				}
				
				if (br != null)
					br.close();
			}
			
			if (bug2goData.split("\n").length > 3) // If enough
				// evidences found
				result = bug2goData;
			else
				result = "- No B2G evidences were found in text logs";
		}
		catch (FileNotFoundException e)
		{
			result = "FileNotFoundException\n" + Throwables.getStackTraceAsString(e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			result = "IOException\n" + Throwables.getStackTraceAsString(e);
			e.printStackTrace();
		}
		
		try
		{
			if (br != null)
				br.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		return result;
	}
	
	// Getters and Setters
	public static String getResult()
	{
		return result;
	}
	
	public static boolean isEdited()
	{
		return edited;
	}
	
	public static boolean isEnabled()
	{
		return enabled;
	}
	
	public static void setEdited(boolean value)
	{
		edited = value;
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
