package filters;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.Throwables;

import core.Logger;
import core.SharedObjs;
import customobjects.WackLock_List;
import customobjects.WakelockItem;


/**
 * Look for suspicious wake locks
 */
public class Suspicious
{
	private static String		 result;
	private static WackLock_List suspiciousWakelocks;
	private static boolean		 enabled = true;
	
	public static String makelog(String path)
	{
		// Variables
		BufferedReader br = null;
		result = "";
		
		try
		{
			// Variables initialization
			String file_report = "";
			String sCurrentLine = "";
			
			suspiciousWakelocks = new WackLock_List();
			
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();
			
			// Test if is directory
			if (!folder.isDirectory())
			{
				result = "Not a directory";
				return result;
			}
			
			// Search for the file to be parsed
			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isFile())
				{
					String files = listOfFiles[i].getName();
					if (((files.endsWith(".txt")) || (files.endsWith(".TXT"))) && (files.contains("system")))
					{
						if (path.equals("."))
							file_report = files;
						else
							file_report = path + files;
						break;
					}
				}
			}
			
			// Verify if file exists
			if (file_report.equals(""))
				throw new FileNotFoundException();
			else
				br = new BufferedReader(new FileReader(file_report));
			
			// Search for the suspicious wake locks
			while ((sCurrentLine = br.readLine()) != null)
			{
				Date parsedDate = null;
				
				if (sCurrentLine.contains("PowerManagerService: Suspicious wakelock held"))
				{
					String tag = sCurrentLine.substring(sCurrentLine.indexOf("tag=") + 4,
														sCurrentLine.indexOf(",",
																			 sCurrentLine.indexOf("tag=")));
					String lock = sCurrentLine.substring(sCurrentLine.indexOf("lock=") + 5,
														 sCurrentLine.indexOf(",",
																			  sCurrentLine.indexOf("lock=")));
					String ws;
					
					if (sCurrentLine.contains("ws=null"))
						ws = sCurrentLine.substring(sCurrentLine.indexOf("ws=") + 3,
													sCurrentLine.indexOf(",", sCurrentLine.indexOf("ws=")));
					else
						ws = sCurrentLine.substring(sCurrentLine.indexOf("ws=") + 3,
													sCurrentLine.indexOf("}", sCurrentLine.indexOf("ws="))
																					 + 1);
					
					// Logger.log(Logger.TAG_SUSPICIOUS, "WS != null:" + ws);
					
					String uid = sCurrentLine.substring(sCurrentLine.indexOf("uid=") + 4,
														sCurrentLine.indexOf(",",
																			 sCurrentLine.indexOf("uid=")));
					String process = "";
					
					// Date format
					try
					{
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
						parsedDate = dateFormat.parse((String) sCurrentLine.subSequence(0, 13));
					}
					catch (Exception e)
					{
						Logger.log(Logger.TAG_SUSPICIOUS, "Error: " + e.toString());
					}
					
					// Find by ID the process causing the wake lock
					if (!ws.equals("null"))
					{
						ws = ws.substring(ws.indexOf("{") + 1, ws.indexOf("}"));
						process = "userId=\"" + ws + "\"";
						uid = ws;
					}
					else
					{
						process = "userId=\"" + uid + "\"";
					}
					
					//Logger.log(Logger.TAG_SUSPICIOUS, "uid: " + uid);
					
					// Create new wake lock item
					WakelockItem wl = new WakelockItem(uid, tag, lock, parsedDate, sCurrentLine);
					
					// Verify if the wake lock exists already
					int index = suspiciousWakelocks.wlIndexOf(wl);
					
					if (index == -1) // If does not exists, add a new entry to list
					{
						wl.quantityInc();
						
						// Search for the process name in bugreport file
						if (uid.length() == 5)
						{
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
											file_report = path + files;
										break;
									}
								}
							}
							
							if (file_report.contains("bugreport"))
							{
								//Logger.log(Logger.TAG_SUSPICIOUS, "File opened: " + file_report);
								BufferedReader aux = new BufferedReader(new FileReader(file_report));
								aux.skip(1850000);
								String str;
								boolean found = false;
								
								while ((((str = aux.readLine()) != null) && found == false) && str != null)
								{
									if ((str.contains("uid=" + uid + " ")/*
																		  * || str. contains ("uid=" + ws + " ")
																		  */) && str.contains("packageName="))
									{
										process = str.substring(str.indexOf("packageName=") + 12,
																str.indexOf(" ", str.indexOf("packageName=")
																				 + 12));
										found = true;
									}
								}
								
								aux.close();
							}
							else
							{
								file_report = "";
								for (int i = 0; i < listOfFiles.length; i++)
								{
									if (listOfFiles[i].isFile())
									{
										String files = listOfFiles[i].getName();
										if (((files.endsWith(".btd")) || (files.endsWith(".BTD")))
											&& (files.contains("BT9")))
										{
											if (path.equals("."))
												file_report = files;
											else
												file_report = path + files;
											break;
										}
									}
								}
								
								BufferedReader aux = new BufferedReader(new FileReader(file_report));
								aux.skip(10000);
								String str;
								boolean found = false;
								// process = (String) process.substring(8, process.length()-1);
								
								while ((((str = aux.readLine()) != null) && found == false))
								{
									if (str.equals(""))
										str = aux.readLine();
									else if (str.matches("(.*):" + uid + ":(.*)"))
									{
										int start = str.indexOf(":" + uid + ":") - 1;
								
										while (str.charAt(start) != '|')
											start--;
										
										process = str.substring(start + 1, str.indexOf(":" + uid + ":"));
										found = true;
									}
								}
								
								aux.close();
							}
						}
						
						// Search for process name in BTD file
						else
						{
							for (int i = 0; i < listOfFiles.length; i++)
							{
								if (listOfFiles[i].isFile())
								{
									String files = listOfFiles[i].getName();
						
									if (((files.endsWith(".btd")) || (files.endsWith(".BTD")))
										&& (files.contains("BT9")))
									{
										if (path.equals("."))
											file_report = files;
										else
											file_report = path + files;
										break;
									}
								}
							}
							
							BufferedReader aux = new BufferedReader(new FileReader(file_report));
							aux.skip(10000);
							String str;
							boolean found = false;
							// process = (String) process.substring(8, process.length()-1);
							
							while ((((str = aux.readLine()) != null) && found == false))
							{
								if (str.equals(""))
									str = aux.readLine();
								else if (str.matches("(.*):" + uid + ":(.*)"))
								{
									int start = str.indexOf(":" + uid + ":") - 1;
							
									while (str.charAt(start) != '|')
										start--;
									
									process = str.substring(start + 1, str.indexOf(":" + uid + ":"));
									found = true;
								}
							}
							
							aux.close();
						}
						
						wl.setProcess(process);
						
						// Add the wake lock to the list
						suspiciousWakelocks.add(wl);
					}
					// If wake lock exists already, update it
					else
					{
						wl = (WakelockItem) suspiciousWakelocks.get(index);
						wl.quantityInc();
						wl.setEnd(parsedDate);
						wl.addLogLine("\n" + sCurrentLine);
						suspiciousWakelocks.set(index, wl); // Update list
					}
				}
			}
			try
			{
				br.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
			
			// Generate final results
			result = result + Issue.makelog(path) + "\n\n";
			
			if (suspiciousWakelocks.size() > 0)
			{
				result = result + SharedObjs.optionsPane.getTextSuspiciousHeader() + "\n";
				
				for (int i = 0; i < suspiciousWakelocks.size(); i++)
				{
					result = result + "{panel}\n"
							 + SharedObjs.optionsPane.getTextSuspicious()
													 .replace("#pname#",
															  suspiciousWakelocks.get(i).getProcess())
													 .replace("#tag#", suspiciousWakelocks.get(i).getTag())
													 .replace("#duration#",
															  suspiciousWakelocks.get(i).getDuration())
													 .replace("#log#", suspiciousWakelocks.get(i).getLog())
													 .replace("\\n", "\n")
							 + "\n{panel}\n";
				}
			}
			else
			{
				result = "- No detailed wake locks evidences were found in text logs";
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
		
		return result;
	}
	
	// Getters and Setters
	static public WackLock_List getWakeLocks()
	{
		return suspiciousWakelocks;
	}
	
	static public String getResult()
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