package filters;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import supportive.DateTimeOperator;

import com.google.common.base.Throwables;

import core.Logger;


/**
 * Look for suspicious wake locks
 */
public class SystemPM
{
	private String                      result;
	private ArrayList<SystemWLs>        suspiciousWakelocks;
	private ArrayList<SystemWLs>        okWakelocks;
	private ArrayList<PMWakelockPeriod> pmWakeLocks;
	private String                      dPath;
	private final long                  minHeldTime = 15 * 60 * 1000;
	
	public SystemPM(String path)
	{
		dPath = path;
		// makelog();
	}
	
	public String makelog()
	{
		// Variables
		BufferedReader br = null;
		result = "";
		
		try
		{
			// Look for needed files
			File folder = new File(dPath);
			File[] listOfFiles = folder.listFiles();
			String file_report = "";
			
			if (!folder.isDirectory()) // Test if is directory
			{
				result = "Not a directory";
				return result;
			}
			
			for (int i = 0; i < listOfFiles.length; i++) // Search for the file to be parsed
			{
				if (listOfFiles[i].isFile())
				{
					String files = listOfFiles[i].getName();
					if (((files.endsWith(".txt")) || (files.endsWith(".TXT"))) && (files.contains("system")))
					{
						if (dPath.equals("."))
							file_report = files;
						else
							file_report = dPath + files;
						break;
					}
				}
			}
			
			if (file_report.equals("")) // Verify if file exists
				throw new FileNotFoundException();
			else
				br = new BufferedReader(new FileReader(file_report));
			
			// Regex configuration
			String wlAcqRegex = "([0-9]{2})-([0-9]{2}) ([0-2][0-9]):([0-5][0-9]):([0-5][0-9]).+D PowerManagerService: acquireWakeLockInternal: lock=(.+), flags=.+, tag=(.+), ws=(.+), uid=(.+), pid=(.+)";
			String wlRelRegex = "([0-9]{2})-([0-9]{2}).*([0-2][0-9]):([0-5][0-9]):([0-5][0-9]).+D PowerManagerService: releaseWakeLockInternal: lock=(.+) \\[(.+)\\], flags=.+";
			String pmAcqRegex = "([0-9]{2})-([0-9]{2}) ([0-2][0-9]):([0-5][0-9]):([0-5][0-9]).+D PowerManagerService: Acquiring suspend blocker \"PowerManagerService.WakeLocks\".";
			String pmRelRegex = "([0-9]{2})-([0-9]{2}) ([0-2][0-9]):([0-5][0-9]):([0-5][0-9]).+D PowerManagerService: Releasing suspend blocker \"PowerManagerService.WakeLocks\".";
			
			Pattern wlAcqPattern = Pattern.compile(wlAcqRegex);
			Pattern wlRelPattern = Pattern.compile(wlRelRegex);
			Pattern pmAcqPattern = Pattern.compile(pmAcqRegex);
			Pattern pmRelPattern = Pattern.compile(pmRelRegex);
			
			Matcher wlMatcher = null;
			Matcher pmMatcher = null;
			
			TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
			Date parsedDate = null;
			Date acquiredDate = null;
			Date releasedDate = null;
			String acqD = "";
			String relD = "";
			
			String year = getYearFromBugReport();
			
			suspiciousWakelocks = new ArrayList<SystemWLs>();
			okWakelocks = new ArrayList<SystemWLs>();
			pmWakeLocks = new ArrayList<PMWakelockPeriod>();
			
			// Search for the suspicious wake locks
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null)
			{
				wlMatcher = wlAcqPattern.matcher(sCurrentLine);
				
				if (wlMatcher.matches())
				{
					String lock = wlMatcher.group(6);
					String tag = wlMatcher.group(7);
					String ws = wlMatcher.group(8).replace("WorkSource{", "").replace("}", "");
					String uid = wlMatcher.group(9);
					String pid = wlMatcher.group(10); // TODO verificar usabilidade
					
					if (sCurrentLine.length() > 8)
					{
						// TODO Contains the app package
					}
					else
					{
						// TODO Does not
						// Try to find by ID the process causing the wake lock
					}
					
					// Logger.log(Logger.TAG_SUSPICIOUS, "");
					
					// Date format
					try
					{
						SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
						parsedDate = dateFormat.parse(year + "-" + sCurrentLine.subSequence(0, 18));
						// System.out.println("**** " + getYearFromBugReport() + "-" + sCurrentLine.subSequence(0, 18) + " - " +
						// parsedDate.getTime());
					}
					catch (Exception e)
					{
						Logger.log(Logger.TAG_SUSPICIOUS, "Error: " + e.toString());
					}
					
					if (wlIndex(lock) == -1)
						suspiciousWakelocks.add(new SystemWLs(lock, tag, ws, uid, pid, parsedDate.getTime(), year + "-" + sCurrentLine.subSequence(0, 18)));
				}
				else
				{
					wlMatcher = wlRelPattern.matcher(sCurrentLine);
					
					if (wlMatcher.matches())
					{
						String lock = wlMatcher.group(6);
						// String tag = wlMatcher.group(7);
						
						// Date format
						try
						{
							SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
							// TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
							parsedDate = dateFormat.parse(year + "-" + sCurrentLine.subSequence(0, 18));
							// System.out.println("--- " + getYearFromBugReport() + "-" + sCurrentLine.subSequence(0, 18) + " - " +
							// parsedDate.getTime());
						}
						catch (Exception e)
						{
							Logger.log(Logger.TAG_SUSPICIOUS, "Error: " + e.toString());
						}
						
						int wlIndex = wlIndex(lock);
						if (wlIndex > -1)
						{
							suspiciousWakelocks.get(wlIndex).updateTime(parsedDate.getTime(), year + "-" + sCurrentLine.subSequence(0, 18));
							
							if (suspiciousWakelocks.get(wlIndex).getHeldTime() > 60000)
								okWakelocks.add(suspiciousWakelocks.get(wlIndex));
							
							if (suspiciousWakelocks.get(wlIndex).getHeldTime() < minHeldTime)
							{
								suspiciousWakelocks.remove(wlIndex);
							}
						}
					}
					else
					{
						pmMatcher = pmAcqPattern.matcher(sCurrentLine);
						
						if (pmMatcher.matches())
						{
							// Date format
							try
							{
								SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
								acquiredDate = dateFormat.parse(year + "-" + sCurrentLine.subSequence(0, 18));
								acqD = year + "-" + sCurrentLine.subSequence(0, 18);
								// System.out.println("**** " + getYearFromBugReport() + "-" + sCurrentLine.subSequence(0, 18) + " - " +
								// parsedDate.getTime());
							}
							catch (Exception e)
							{
								Logger.log(Logger.TAG_SUSPICIOUS, "Error: " + e.toString());
							}
						}
						else
						{
							pmMatcher = pmRelPattern.matcher(sCurrentLine);
							
							if (pmMatcher.matches())
							{
								// Date format
								try
								{
									SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
									releasedDate = dateFormat.parse(year + "-" + sCurrentLine.subSequence(0, 18));
									relD = year + "-" + sCurrentLine.subSequence(0, 18);
									// System.out.println("--- " + getYearFromBugReport() + "-" + sCurrentLine.subSequence(0, 18) + " - " +
									// parsedDate.getTime());
									
									if (acquiredDate != null && releasedDate.getTime() - acquiredDate.getTime() > minHeldTime)
									{
										System.out.println("PM suspend blocker held for: "
										                   + DateTimeOperator.getTimeStringFromMillis(releasedDate.getTime() - acquiredDate.getTime()));
										System.out.println("From: " + acqD + " - To: " + relD);
										System.out.println("rel" + releasedDate.getTime() + "\nacq" + acquiredDate.getTime());
										
										pmWakeLocks.add(new PMWakelockPeriod(acqD, acquiredDate, relD, releasedDate));
										
										acquiredDate = null;
										releasedDate = null;
									}
									else
									{
										
									}
								}
								catch (Exception e)
								{
									Logger.log(Logger.TAG_SUSPICIOUS, "Error: " + e.toString());
								}
							}
						}
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
			
			// Generate results
			result = "Results:\n\n";
			if (suspiciousWakelocks.size() > 0)
			{
				System.out.println("\n\n--------------- Results ---------------\n");
				
				System.out.println("-- PMS.Wakelocks suspicious --");
				for (int i = 0; i < suspiciousWakelocks.size(); i++)
				{
					System.out.println("Stuck wakelock since " + suspiciousWakelocks.get(i).getAcquiredDate() + "-  " + "\tLock: "
					                   + suspiciousWakelocks.get(i).getLock() + "\tUid: " + suspiciousWakelocks.get(i).getUid() + "\tTag: "
					                   + suspiciousWakelocks.get(i).getTag());
					result = result + "Stuck wakelock-  " + "\tLock: " + suspiciousWakelocks.get(i).getLock() + "\tUid: " + suspiciousWakelocks.get(i).getUid()
					         + "\tTag: " + suspiciousWakelocks.get(i).getTag() + "\n";
				}
				
				result = result + "\n-------------\n";
				
				System.out.println("\n-- PMS.Wakelocks suspended for more than 15 minutes --");
				for (int i = 0; i < okWakelocks.size(); i++)
				{
					if(okWakelocks.get(i).getHeldTime() > minHeldTime)
					{
						System.out.println("Wakelock held for: " + DateTimeOperator.getTimeStringFromMillis(okWakelocks.get(i).getHeldTime()) + "\t - Lock: "
						                   + okWakelocks.get(i).getLock() + "\n\tFrom: " + okWakelocks.get(i).getAcquiredDate() + "   ->  To: "
						                   + okWakelocks.get(i).getEndDate() + "\n\tUid: " + okWakelocks.get(i).getUid() + "\tTag: " + okWakelocks.get(i).getTag()
						                   + "\n");
						result += "Wakelock held for: " + DateTimeOperator.getTimeStringFromMillis(okWakelocks.get(i).getHeldTime()) + "\t - Lock: "
						          + okWakelocks.get(i).getLock() + "\tUid: " + okWakelocks.get(i).getUid() + "\tTag: " + okWakelocks.get(i).getTag() + "\n";
					}
				}
			}
			else
			{
				result = "- No detailed wake locks evidences were found in text logs";
			}
			
			System.out.println("\n\n");
			for (PMWakelockPeriod p : pmWakeLocks)
			{
				System.out.println("Wakelocks while PM suspended:\n" + p);
				for (SystemWLs w : okWakelocks)
				{
					if (w.getAcquiredTime() >= p.getAcquiredDate().getTime() - 60000 && w.getAcquiredTime() < p.getReleasedDate().getTime())
					{
						System.out.println(w);
					}
				}
				System.out.println("------");
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
	
	// Others
	private String getYearFromBugReport()
	{
		File folder = new File(dPath);
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
	
	private int wlIndex(String lock)
	{
		int index = -1;
		
		for (int i = 0; i < suspiciousWakelocks.size(); i++)
		{
			if (suspiciousWakelocks.get(i).getLock().equals(lock))
			{
				index = i;
			}
		}
		
		return index;
	}
	
	public class SystemWLs
	{
		String lock;
		String tag;
		String ws;
		String uid;
		String pid;
		long   heldTime;
		long   acquiredTime;
		String acquiredDate;
		String endDate;
		
		public SystemWLs(String lock, String tag, String ws, String uid, String pid, long time, String acquiredDate)
		{
			this.lock = lock;
			this.tag = tag;
			this.ws = ws;
			this.uid = uid;
			this.pid = pid;
			heldTime = -1;
			acquiredTime = time;
			this.acquiredDate = acquiredDate;
			endDate = "";
		}
		
		public void updateTime(long endTime, String endDate)
		{
			heldTime = endTime - acquiredTime;
			this.endDate = endDate;
		}
		
		public String toString()
		{
			return "Wakelock held for: " + DateTimeOperator.getTimeStringFromMillis(getHeldTime()) + "\t - Lock: " + getLock() + "\n\tFrom: "
			       + getAcquiredDate() + "   ->  To: " + getEndDate() + "\n\tUid: " + getUid() + "\tTag: " + getTag();
		}
		
		public long getAcquiredTime()
		{
			return acquiredTime;
		}
		
		public void setAcquiredTime(long acquiredTime)
		{
			this.acquiredTime = acquiredTime;
		}
		
		public long getHeldTime()
		{
			return heldTime;
		}
		
		public void setHeldTime(long heldTime)
		{
			this.heldTime = heldTime;
		}
		
		public String getLock()
		{
			return lock;
		}
		
		public String getEndDate()
		{
			return endDate;
		}
		
		public void setEndDate(String endDate)
		{
			this.endDate = endDate;
		}
		
		public void setLock(String lock)
		{
			this.lock = lock;
		}
		
		public String getTag()
		{
			return tag;
		}
		
		public void setTag(String tag)
		{
			this.tag = tag;
		}
		
		public String getWs()
		{
			return ws;
		}
		
		public void setWs(String ws)
		{
			this.ws = ws;
		}
		
		public String getUid()
		{
			return uid;
		}
		
		public void setUid(String uid)
		{
			this.uid = uid;
		}
		
		public String getPid()
		{
			return pid;
		}
		
		public void setPid(String pid)
		{
			this.pid = pid;
		}
		
		public String getAcquiredDate()
		{
			return acquiredDate;
		}
		
		public void setAcquiredDate(String acquiredDate)
		{
			this.acquiredDate = acquiredDate;
		}
	}
	
	public class PMWakelockPeriod
	{
		String acqDate;
		String relDate;
		Date   acquiredDate;
		Date   releasedDate;
		
		public PMWakelockPeriod(String acqD, Date acquiredDate)
		{
			this.acqDate = acqD;
			this.acquiredDate = acquiredDate;
			this.relDate = null;
			this.releasedDate = null;
		}
		
		public PMWakelockPeriod(String acqD, Date acquiredDate, String relD, Date releasedDate)
		{
			this.acqDate = acqD;
			this.acquiredDate = acquiredDate;
			this.relDate = relD;
			this.releasedDate = releasedDate;
		}
		
		public String toString()
		{
			String s = "Acquired: " + acqDate + "\n";
			s += "Released: " + relDate + "\n";
			s += "Held for: " + DateTimeOperator.getTimeStringFromMillis(releasedDate.getTime() - acquiredDate.getTime()) + "\n";
			
			return s;
		}
		
		public String getHeldTimeString()
		{
			return DateTimeOperator.getTimeStringFromMillis(releasedDate.getTime() - acquiredDate.getTime());
		}
		
		public Long getHeldTimeMillis()
		{
			return releasedDate.getTime() - acquiredDate.getTime();
		}
		
		public String getAcqDate()
		{
			return acqDate;
		}
		
		public void setAcqDate(String acqDate)
		{
			this.acqDate = acqDate;
		}
		
		public String getRelDate()
		{
			return relDate;
		}
		
		public void setRelDate(String relDate)
		{
			this.relDate = relDate;
		}
		
		public Date getAcquiredDate()
		{
			return acquiredDate;
		}
		
		public void setAcquiredDate(Date acquiredDate)
		{
			this.acquiredDate = acquiredDate;
		}
		
		public Date getReleasedDate()
		{
			return releasedDate;
		}
		
		public void setReleasedDate(Date releasedDate)
		{
			this.releasedDate = releasedDate;
		}
	}
	
	// Getters and Setters
	// static public WackLock_List getWakeLocks()
	// {
	// return suspiciousWakelocks;
	// }
	//
	// static public String getResult()
	// {
	// return result;
	// }
	//
	// public static boolean isEnabled()
	// {
	// return enabled;
	// }
	//
	// public static void setEnabled(boolean onoff)
	// {
	// enabled = onoff;
	// }
	//
	// public static void updateResult(String editedResult)
	// {
	// result = editedResult;
	// }
}