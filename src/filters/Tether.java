package filters;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.common.base.Throwables;

import core.SharedObjs;


/**
 * Search for tethering evidences
 */
public class Tether
{
	private static String  result;
	private static boolean enabled = true;
	
	public static String makeLog(String path)
	{
		BufferedReader br = null;
		result = "";
		
		try
		{
			String wifitetherData1 = "";
			String wifitetherData2 = "";
			String wifitetherData3 = "";
			String sCurrentLine;
			String file_report = "";
			
			// File seek and load configuration
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();
			
			if (!folder.isDirectory())
			{
				result = "Not a directory";
				return result;
			}
			
			// Look for the file
			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isFile())
				{
					String files = listOfFiles[i].getName();
					// Logger.log(Logger.TAG_TETHER,
					// listOfFiles[i].getName());
					if (((files.endsWith(".txt")) || (files.endsWith(".TXT"))) && (files.contains("system")))
					{
						if (path.equals("."))
							file_report = files;
						else
							file_report = path + "\\" + files;
						break;
					}
				}
			}
			
			// Try to open file
			if (file_report.equals(""))
			{
				result = "system not found\n";
			}
			else
			{
				br = new BufferedReader(new FileReader(file_report));
				
				while ((sCurrentLine = br.readLine()) != null)
				{
					if (sCurrentLine.contains("WiFi Tethered already"))
					{
						wifitetherData1 = wifitetherData1 + sCurrentLine + "\n";
					}
				}
				
				if (br != null)
					br.close();
			}
			
			// Look for a file
			file_report = "";
			
			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isFile())
				{
					String files = listOfFiles[i].getName();
					// Logger.log(Logger.TAG_TETHER,
					// listOfFiles[i].getName());
					if (((files.endsWith(".txt")) || (files.endsWith(".TXT"))) && (files.contains("main")))
					{
						if (path.equals("."))
							file_report = files;
						else
							file_report = path + "\\" + files;
						break;
					}
				}
			}
			
			// Try to open file
			if (file_report.equals("") && result.equals("main not found"))
			{
				result = result + "main not found\n";
			}
			else
			{
				br = new BufferedReader(new FileReader(file_report));
				String startTether = "", stopTether = "";
				
				while ((sCurrentLine = br.readLine()) != null)
				{
					if ((sCurrentLine.contains("TetherModeAliveState") && sCurrentLine.contains("what=4"))
					    || sCurrentLine.contains("processMsg: TetheredState"))
					{
						wifitetherData2 = wifitetherData2 + sCurrentLine + "\n";
					}
					else if (sCurrentLine.toLowerCase().contains("starting tether"))
					{
						// Logger.log(Logger.TAG_TETHER, sCurrentLine);
						if (!startTether.equals(""))
							startTether = startTether + "\n" + sCurrentLine;
						else
							startTether = sCurrentLine;
					}
					else if (sCurrentLine.toLowerCase().contains("stopping tether"))
					{
						// Logger.log(Logger.TAG_TETHER, sCurrentLine);
						if (!stopTether.equals(""))
							stopTether = stopTether + "\n" + sCurrentLine;
						else
							stopTether = sCurrentLine;
					}
				}
				
				if (!startTether.equals("") || !stopTether.equals(""))
				{
					if (startTether.length() > stopTether.length())
					{
						stopTether = stopTether + "\nUnknown";
						String starts[] = startTether.split("\n");
						String stops[] = stopTether.split("\n");
						
						for (int i = 0; i < starts.length; i++)
						{
							result = result + "\n|Starting Tethering at| " + starts[i] + "|";
							result = result + "\n|Stopping Tethering at| " + stops[i] + "|";
						}
					}
					else if (startTether.length() < stopTether.length())
					{
						startTether = startTether + "Unknown";
						String starts[] = startTether.split("\n");
						String stops[] = stopTether.split("\n");
						
						for (int i = 0; i < starts.length; i++)
						{
							result = result + "\n|Starting Tethering at| " + starts[i] + "|";
							result = result + "\n|Stopping Tethering at| " + stops[i] + "|";
						}
					}
					else
					{
						String starts[] = startTether.split("\n");
						String stops[] = stopTether.split("\n");
						
						for (int i = 0; i < starts.length; i++)
						{
							result = result + "\n|Starting Tethering at| " + starts[i] + "|";
							result = result + "\n|Stopping Tethering at| " + stops[i] + "|";
						}
					}
				}
				
				if (br != null)
					br.close();
			}
			
			// Look for a file
			file_report = "";
			
			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isFile())
				{
					String files = listOfFiles[i].getName();
					if (((files.endsWith(".txt")) || (files.endsWith(".TXT")))
					    && (files.contains("bugreport")))
					{
						if (path.equals("."))
							file_report = files;
						else
							file_report = path + "\\" + files;
						break;
					}
				}
			}
			
			// Try to open file
			if (file_report.equals("") && result.contains("main not found")
			    && result.contains("system not found"))
			{
				throw new FileNotFoundException();
			}
			else
			{
				br = new BufferedReader(new FileReader(file_report));
				
				while ((sCurrentLine = br.readLine()) != null)
				{
					if (sCurrentLine.contains("org=TetheringState dest=TetheredState")
					    || sCurrentLine.contains("org=TetheredState dest=<null>")
					    || sCurrentLine.contains("org=TetheredState dest=UntetheringState"))
					{
						wifitetherData3 = wifitetherData3 + sCurrentLine + "\n";
					}
				}
				
				if (br != null)
					br.close();
			}
			
			if (wifitetherData1.split("\n").length > 1)
			{
				result = result + "\n{noformat}\n" + wifitetherData1 + "{noformat}";
			}
			
			if (wifitetherData2.split("\n").length > 6)
			{
				result = result + "\n{noformat}\n" + wifitetherData2 + "{noformat}";
			}
			
			if (wifitetherData3.split("\n").length > 6)
			{
				result = result + "\n{noformat}\n" + wifitetherData3 + "{noformat}";
			}
			
			if (result.split("\n").length < 12)
			{
				result = "- No tethering evidences were found in text logs";
			}
		}
		catch (FileNotFoundException e)
		{
			result = "FileNotFoundException\n" + Throwables.getStackTraceAsString(e);
			e.printStackTrace();
			
			return result;
		}
		catch (IOException e)
		{
			result = "IOException\n" + Throwables.getStackTraceAsString(e);
			e.printStackTrace();
			
			return result;
		}
		finally
		{
			try
			{
				if (br != null)
					br.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		result = SharedObjs.optionsPane.getTextTether().replace("\\n", "\n").replace("#log#", result);
		
		return result;
	}
	
	// Getters and Setters:
	public static String getResult()
	{
		// Logger.log(Logger.TAG_TETHER, result);
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