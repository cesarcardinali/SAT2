package supportive.preanalyzers.logsparser;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import supportive.DateTimeOperator;
import core.Logger;


public class BugrepParser
{
	// All long variables represent time in millis
	private String                    path;
	private int                       thresholdInc;
	private String                    rawStats;
	private String                    wakelocksComment;
	private long                      timeOnBat      = -1;
	private long                      scOffTime      = -1;
	private long                      scOnTime       = -1;
	private long                      remTime        = -1;
	private long                      scDark         = -1;
	private long                      scDim          = -1;
	private long                      scMedium       = -1;
	private long                      scLight        = -1;
	private long                      scBright       = -1;
	private long                      signalNone     = -1;
	private long                      signalPoor     = -1;
	private long                      signalModerate = -1;
	private long                      signalGood     = -1;
	private long                      signalGreat    = -1;
	private long                      signalScan     = -1;
	private long                      radioNone      = -1;
	private long                      radioGprs      = -1;
	private long                      radioUmts      = -1;
	private long                      radioEvdo      = -1;
	private long                      radio1xrtt     = -1;
	private long                      radioActive    = -1;
	private long                      wifiRun        = -1;
	private long                      wifiScan       = -1;
	private long                      wifiLevel0     = -1;
	private long                      wifiLevel1     = -1;
	private long                      wifiLevel2     = -1;
	private long                      wifiLevel3     = -1;
	private long                      wifiLevel4     = -1;
	private int                       connectChanges;     // Times that radio connection changed
	private float                     dischargeAmount;    // Percentage
	private float                     dischScOn;          // Percentage from discharge amount
	private float                     dischScOff;         // Percentage from discharge amount
	private float                     batCap;             // Total battery capacity
	private ArrayList<BugRepKernelWL> kernelWLs;
	private ArrayList<BugRepJavaWL>   javaWLs;
	
	public BugrepParser(String path)
	{
		this.path = path;
		kernelWLs = new ArrayList<BugRepKernelWL>();
		javaWLs = new ArrayList<BugRepJavaWL>();
	}
	
	public boolean parse()
	{
		thresholdInc = 0;
		String sCurrentLine;
		String sLastLine = "";
		String file_report = "";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		BufferedReader br = null;
		
		if (!folder.isDirectory())
		{
			return false;
		}
		
		// Look for the file to be parsed
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile())
			{
				String files = listOfFiles[i].getName();
				
				if (files.endsWith(".txt") && files.contains("bugreport"))
				{
					if (listOfFiles[i].length() > 5000000)
					{
						if (path.equals("."))
							file_report = files;
						else
							file_report = path + "/" + files;
						
						break;
					}
					else
					{
						Logger.log(Logger.TAG_BUGREPORT_PARSER, "Bugreport file too short");
						return false;
					}
				}
			}
		}
		
		Logger.log(Logger.TAG_BUGREPORT_PARSER, "\nBugreport file: " + file_report);
		
		// Try to open file
		if (!file_report.equals(""))
		{
			try
			{
				// Regex configuration
				Pattern ptTimeOnBat = Pattern.compile("Time on battery: (.+) \\((.+)%\\) realtime, (.+) \\((.+)%\\) uptime");
				Pattern ptScOffTime = Pattern.compile("Time on battery screen off: (.+) \\((.+)%\\) realtime, (.+) \\((.+)%\\) uptime");
				Pattern ptRemTime = Pattern.compile("Charge time remaining: (.+)");
				Pattern ptScDark = Pattern.compile("dark (.+) \\((.+)%\\)$");
				Pattern ptScDim = Pattern.compile("dim (.+) \\((.+)%\\)$");
				Pattern ptScMedium = Pattern.compile("medium (.+) \\((.+)%\\)$");
				Pattern ptScLight = Pattern.compile("light (.+) \\((.+)%\\)$");
				Pattern ptScBright = Pattern.compile("bright (.+) \\((.+)%\\)$");
				Pattern ptConnectChanges = Pattern.compile("Connectivity changes: (.+)");
				Pattern ptSignalNone = Pattern.compile("none (.+) \\((.+)%\\)");
				Pattern ptSignalPoor = Pattern.compile("poor (.+) \\((.+)%\\)");
				Pattern ptSignalModerate = Pattern.compile("moderate (.+) \\((.+)%\\)");
				Pattern ptSignalGood = Pattern.compile("good (.+) \\((.+)%\\)");
				Pattern ptSignalGreat = Pattern.compile("great (.+) \\((.+)%\\)");
				Pattern ptSignalScan = Pattern.compile("Signal scanning time: (.+)");
				Pattern ptRadioNone = Pattern.compile("none (.+) \\((.+)%\\) ");
				Pattern ptRadioGprs = Pattern.compile("gprs (.+) \\((.+)%\\) ");
				Pattern ptRadioUmts = Pattern.compile("umts (.+) \\((.+)%\\) ");
				Pattern ptRadioEvdo = Pattern.compile("Evdo (.+) \\((.+)%\\) ");
				Pattern ptRadio1xrtt = Pattern.compile("1xrtt (.+) \\((.+)%\\) ");
				Pattern ptRadioActive = Pattern.compile("Mobile radio active time: (.+) \\((.+)%\\) ");
				Pattern ptWifiRun = Pattern.compile(", Wifi running: (.+) \\((.+)%\\)$");
				Pattern ptWifiScan = Pattern.compile("scanning (.+) \\((.+)%\\) ");
				Pattern ptWifiLevel0 = Pattern.compile("level\\(0\\) (.+) \\((.+)%\\) ");
				Pattern ptWifiLevel1 = Pattern.compile("level\\(1\\) (.+) \\((.+)%\\) ");
				Pattern ptWifiLevel2 = Pattern.compile("level\\(2\\) (.+) \\((.+)%\\) ");
				Pattern ptWifiLevel3 = Pattern.compile("level\\(3\\) (.+) \\((.+)%\\) ");
				Pattern ptWifiLevel4 = Pattern.compile("level\\(4\\) (.+) \\((.+)%\\) ");
				Pattern ptDischargeAmount = Pattern.compile("Amount discharged \\(upper bound\\): (\\d+)");
				Pattern ptDischScOn = Pattern.compile("Amount discharged while screen on: (\\d+)");
				Pattern ptDischScOff = Pattern.compile("Amount discharged while screen off: (\\d+)");
				Pattern ptBatCap = Pattern.compile("Capacity: (\\d+), Computed drain");
				Pattern ptKernelWL = Pattern.compile("Kernel Wake lock (.+): (.+) \\((.+) times\\)");
				Pattern ptJavaWL = Pattern.compile("Wake lock (...\\d\\d?\\d?) (.+): (.+) \\((.+) times\\)");
				
				Matcher matcher;
				
				rawStats = "";
				
				br = new BufferedReader(new FileReader(file_report));
				
				// Search for b2g evidences
				while ((sCurrentLine = br.readLine()) != null)
				{
					if (sCurrentLine.contains("DUMP OF SERVICE appops:"))
					{
						br.mark((int) (new File(file_report).length() - 10));
					}
					if (sCurrentLine.contains("Statistics since last charge:"))
					{
						Logger.log(Logger.TAG_BUGREPORT_PARSER, "Statistics line found: " + sCurrentLine);
						
						// Read next line
						while ((sCurrentLine = br.readLine()) != null && !sCurrentLine.contains("DUMP OF SERVICE bluetooth_manager"))
						{
							if (sCurrentLine.contains("Cell standby: ")
							    || sCurrentLine.contains("Unaccounted: ")
							    || sCurrentLine.contains("Screen: ") || sCurrentLine.contains("Idle: "))
							{
								break;
							}
							else
							{
								rawStats = rawStats + sCurrentLine + "\\n";
								matcher = ptTimeOnBat.matcher(sCurrentLine);
								if (matcher.find())
								{
									timeOnBat = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("1 ptTimeOnBat " + timeOnBat);
									continue;
								}
								
								matcher = ptScOffTime.matcher(sCurrentLine);
								if (matcher.find())
								{
									scOffTime = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("2 ptScOffTime " + scOffTime);
									continue;
								}
								
								matcher = ptRemTime.matcher(sCurrentLine);
								if (matcher.find())
								{
									remTime = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("3 ptRemTime " + remTime);
									continue;
								}
								
								matcher = ptScDark.matcher(sCurrentLine);
								if (matcher.find())
								{
									scDark = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("4 ptScDark " + scDark);
									continue;
								}
								
								matcher = ptScDim.matcher(sCurrentLine);
								if (matcher.find())
								{
									scDim = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("5 ptScDim " + scDim);
									continue;
								}
								
								matcher = ptScMedium.matcher(sCurrentLine);
								if (matcher.find())
								{
									scMedium = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("6 ptScMedium " + scMedium);
									continue;
								}
								
								matcher = ptScLight.matcher(sCurrentLine);
								if (matcher.find())
								{
									scLight = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("7 ptScLight " + scLight);
									continue;
								}
								
								matcher = ptScBright.matcher(sCurrentLine);
								if (matcher.find())
								{
									scBright = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("8 ptScBright " + scBright);
									continue;
								}
								
								matcher = ptConnectChanges.matcher(sCurrentLine);
								if (matcher.find())
								{
									connectChanges = Integer.parseInt(matcher.group(1));
									System.out.println("9 ptConnectChanges " + connectChanges);
									continue;
								}
								
								matcher = ptSignalNone.matcher(sCurrentLine);
								if (matcher.find() && sLastLine.contains("Phone"))
								{
									signalNone = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("10 ptSignalNone " + signalNone);
									continue;
								}
								
								matcher = ptSignalPoor.matcher(sCurrentLine);
								if (matcher.find())
								{
									System.out.println("Error at: " + sCurrentLine);
									signalPoor = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("11 ptSignalPoor " + signalPoor);
									continue;
								}
								
								matcher = ptSignalModerate.matcher(sCurrentLine);
								if (matcher.find())
								{
									signalModerate = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("12 ptSignalModerate " + signalModerate);
									continue;
								}
								
								matcher = ptSignalGood.matcher(sCurrentLine);
								if (matcher.find())
								{
									signalGood = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("13 ptSignalGood " + signalGood);
									continue;
								}
								
								matcher = ptSignalGreat.matcher(sCurrentLine);
								if (matcher.find())
								{
									signalGreat = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("14 ptSignalGreat " + signalGreat);
									continue;
								}
								
								matcher = ptSignalScan.matcher(sCurrentLine);
								if (matcher.find())
								{
									signalScan = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("15 ptSignalScan " + signalScan);
									continue;
								}
								
								matcher = ptRadioNone.matcher(sCurrentLine);
								if (matcher.find() && sLastLine.contains("Radio"))
								{
									radioNone = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("16 ptRadioNone " + radioNone);
									continue;
								}
								
								matcher = ptRadioGprs.matcher(sCurrentLine);
								if (matcher.find())
								{
									radioGprs = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("17 ptRadioGprs " + radioGprs);
									continue;
								}
								
								matcher = ptRadioUmts.matcher(sCurrentLine);
								if (matcher.find())
								{
									radioUmts = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("18 ptRadioUmts " + radioUmts);
									continue;
								}
								
								matcher = ptRadio1xrtt.matcher(sCurrentLine);
								if (matcher.find())
								{
									radio1xrtt = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("19 ptRadioLxrtt " + radio1xrtt);
									continue;
								}
								
								matcher = ptRadioEvdo.matcher(sCurrentLine);
								if (matcher.find())
								{
									radioEvdo = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("20 ptRadioEvdo " + radioEvdo);
									continue;
								}
								
								matcher = ptRadioActive.matcher(sCurrentLine);
								if (matcher.find())
								{
									radioActive = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("21 ptRadioActive " + radioActive);
									continue;
								}
								
								matcher = ptWifiRun.matcher(sCurrentLine);
								if (matcher.find())
								{
									wifiRun = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("22 ptwifiRun " + wifiRun);
									continue;
								}
								
								matcher = ptWifiScan.matcher(sCurrentLine);
								if (matcher.find())
								{
									wifiScan = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("23 ptWifiScan " + wifiScan);
									continue;
								}
								
								matcher = ptWifiLevel0.matcher(sCurrentLine);
								if (matcher.find())
								{
									wifiLevel0 = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("24 ptWifiLevel0 " + wifiLevel0);
									continue;
								}
								
								matcher = ptWifiLevel1.matcher(sCurrentLine);
								if (matcher.find())
								{
									wifiLevel1 = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("25 ptWifiLevel1 " + wifiLevel1);
									continue;
								}
								
								matcher = ptWifiLevel2.matcher(sCurrentLine);
								if (matcher.find())
								{
									wifiLevel2 = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("26 ptWifiLevel2 " + wifiLevel2);
									continue;
								}
								
								matcher = ptWifiLevel3.matcher(sCurrentLine);
								if (matcher.find())
								{
									wifiLevel3 = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("27 ptWifiLevel3 " + wifiLevel3);
									continue;
								}
								
								matcher = ptWifiLevel4.matcher(sCurrentLine);
								if (matcher.find())
								{
									wifiLevel4 = DateTimeOperator.getMillisFromBtdStringDate(matcher.group(1));
									System.out.println("28 ptWifiLevel4 " + wifiLevel4);
									continue;
								}
								
								matcher = ptDischargeAmount.matcher(sCurrentLine);
								if (matcher.find())
								{
									dischargeAmount = Float.parseFloat(matcher.group(1));
									System.out.println("29 ptDischargeAmount " + dischargeAmount);
									continue;
								}
								
								matcher = ptDischScOn.matcher(sCurrentLine);
								if (matcher.find())
								{
									dischScOn = Float.parseFloat(matcher.group(1));
									System.out.println("30 ptDischScOn " + dischScOn);
									continue;
								}
								
								matcher = ptDischScOff.matcher(sCurrentLine);
								if (matcher.find())
								{
									dischScOff = Float.parseFloat(matcher.group(1));
									System.out.println("31 ptDischScOff " + dischScOff);
									continue;
								}
								
								matcher = ptBatCap.matcher(sCurrentLine);
								if (matcher.find())
								{
									batCap = Float.parseFloat(matcher.group(1));
									System.out.println("32 ptBatCap " + batCap);
									break;
								}
							}
							sLastLine = sCurrentLine;
						}
					}
					
					if (sCurrentLine.contains("All kernel wake locks:"))
					{
						for (int i = 0; i < 5; i++)
						{
							sCurrentLine = br.readLine();
							if(sCurrentLine.contains("PowerManagerService.Display"))
							{
								continue;
							}
							matcher = ptKernelWL.matcher(sCurrentLine);
							if (matcher.find())
							{
								try
								{
									kernelWLs.add(new BugRepKernelWL(matcher.group(1),
									                                 matcher.group(2),
									                                 matcher.group(3)));
									System.out.println("33 ptKernelWL " + kernelWLs.get(kernelWLs.size() - 1));
								}
								catch (ParseException e)
								{
									e.printStackTrace();
								}
							}
							else
							{
								break;
							}
						}
					}
					
					if (sCurrentLine.contains("All partial wake locks:"))
					{
						for (int i = 0; i < 5; i++)
						{
							sCurrentLine = br.readLine();
							matcher = ptJavaWL.matcher(sCurrentLine);
							if (matcher.find())
							{
								try
								{
									javaWLs.add(new BugRepJavaWL(matcher.group(1),
									                             matcher.group(2).replaceAll("\\{|\\}", ""),
									                             matcher.group(3),
									                             matcher.group(4)));
									System.out.println("34 ptJavaWL " + javaWLs.get(javaWLs.size() - 1));
								}
								catch (ParseException e)
								{
									e.printStackTrace();
								}
							}
							else
							{
								break;
							}
						}
					}
					
					if (sCurrentLine.contains("All wakeup reasons:")
					    || sCurrentLine.contains("DUMP OF SERVICE consumer_ir:"))
					{
						for (BugRepJavaWL j : javaWLs)
						{
							br.reset();
							
							while ((sCurrentLine = br.readLine()) != null
							       && !sCurrentLine.contains("DUMP OF SERVICE appwidget:"))
							{
								if (sCurrentLine.contains("  Uid " + j.getUid() + ":"))
								{
									sCurrentLine = br.readLine();
									j.setProcessName(sCurrentLine.replace(" ", "").replace(":", "")
									                             .replace("Package", ""));
									
									// TODO Identify if there are more than one package for this UID
									break;
								}
							}
						}
						
						for (BugRepJavaWL j : javaWLs)
						{
							System.out.println(j);
							System.out.println(j.getProcessName());
						}
						
						break;
					}
				}
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return false;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				scOnTime = timeOnBat - scOffTime;
				// Close file reader
				try
				{
					if (br != null)
						br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (rawStats != null && rawStats.length() < 80)
			{
				Logger.log(Logger.TAG_BUGREPORT_PARSER, "Could not find \"Statistics\" data in this bugreport");
				return false;
			}
			
			return true;
		}
		else
		{
			Logger.log(Logger.TAG_BUGREPORT_PARSER, "No bugreport file found");
			return false;
		}
	}
	
	// Issue related methods -------------------------------------------------
	public boolean checkForHighCurrentScOff()
	{
		if (getConsAvgOff() > 100.0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean checkIfWakelocks(boolean btdDetected)
	{
		wakelocksComment = "";
		float threashold;
		
		if (btdDetected) // Changed to 0.4 from 0.3
		{
			threashold = (float) 0.15;
		}
		else
		{
			threashold = (float) 0.25;
		}
		
		if (kernelWLs.size() > 0 && kernelWLs.get(0).getDuration() > getTimeOnBat() * threashold)
		{
			wakelocksComment += "{panel:title=*Bugreport Kernel wake locks:*|titleBGColor=#E9F2FF}\\n";
			wakelocksComment += kernelWLs.get(0).toJiraComment();
			if(kernelWLs.get(1).getDuration() > getTimeOnBat() * threashold)
			{
				wakelocksComment += "\\n";
				wakelocksComment += kernelWLs.get(1).toJiraComment();
			}
			wakelocksComment += "{panel}";
		}
		
		if (javaWLs.size() > 0 && wakelocksComment.contains("WakeLocks")  && javaWLs.get(0).getDuration() > getTimeOnBat() * threashold)
		{
			wakelocksComment += "{panel:title=*Bugreport Java wake locks:*|titleBGColor=#E9F2FF}\\n";
			
			if (kernelWLs.get(0).getName().contains("Service.WakeLocks"))
			{
				// TODO Probably the main issue is item 0
				wakelocksComment += javaWLs.get(0).toJiraComment();
				if(javaWLs.get(1).getDuration() > getTimeOnBat() * threashold)
				{
					wakelocksComment += "\\n";
					wakelocksComment += javaWLs.get(1).toJiraComment();
				}
			}
			else
			{
				wakelocksComment += javaWLs.get(0).toJiraComment();
				if(javaWLs.get(1).getDuration() > getTimeOnBat() * threashold)
				{
					wakelocksComment += "\\n";
					wakelocksComment += javaWLs.get(1).toJiraComment();
				}
			}
			
			if (wakelocksComment.toLowerCase().contains("spotify")
			    || javaWLs.get(0).getProcessName().toLowerCase().contains("tunein")
			    || javaWLs.get(0).getProcessName().toLowerCase().contains("slacker")
			    || javaWLs.get(0).getProcessName().toLowerCase().contains("pandora")
			    || javaWLs.get(0).getProcessName().toLowerCase().contains("sirius")
			    || javaWLs.get(0).getProcessName().toLowerCase().contains("android.music")
			    || javaWLs.get(0).getProcessName().toLowerCase().contains("saavn")
			    || javaWLs.get(0).getProcessName().toLowerCase().contains("com.audible.application"))
			{
				wakelocksComment += "\\n\\n- _Probably audio is running in background and the held wake lock does not represent an issue_\\n";
			}
			
			wakelocksComment += "{panel}";
		}
		
		if (wakelocksComment.length() > 10)
		{
			return true;
		}
		
		return false;
	}
	
	// Show all acquired data -------------------------------------------------
	public void showData()
	{
		Logger.log(Logger.TAG_BUGREPORT_PARSER, "Avg sc off consume: " + formatNumber(getConsAvgOff()));
	}
	
	// Supportive methods -----------------------------------------------------
	private String formatNumber(float number)
	{
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(number);
	}
	
	public String currentDrainStatistics()
	{
		if (rawStats != null && rawStats.length() > 80)
		{
			String comment = "{panel:title=*Statistics since last charge:*|titleBGColor=#E9F2FF}\\n {noformat}\\n"
			                 + rawStats.replaceAll("\n|\r", "\\\\n") + "{noformat}{panel}\\n";
			comment += "{panel:title=*Bugreport Current drain data*|titleBGColor=#E9F2FF}\\n";
			// TODO improve
			comment += "Total time on battery: "
			          + DateTimeOperator.getTimeStringFromMillis(timeOnBat) + "\\n";
			comment += "Screen On  time: " + DateTimeOperator.getTimeStringFromMillis(scOnTime)
			          + " (" + formatNumber(getPercentage(scOnTime, timeOnBat)) + "%)\\n";
			if (getConsAvgOn() > 740 && getPercentage(scOnTime, timeOnBat) > 15)
			{
				comment += "Screen On consume: *" + formatNumber(getConsAvgOn()) + " mAh*\\n";
			}
			else
			{
				comment += "Screen On consume: " + formatNumber(getConsAvgOn()) + " mAh\\n";
			}
			comment += "Screen Off time: " + DateTimeOperator.getTimeStringFromMillis(scOffTime)
			          + " (" + formatNumber(getPercentage(scOffTime, timeOnBat)) + "%)\\n";
			if (getConsAvgOff() < 100)
			{
				comment += "Screen Off consume: *" + formatNumber(getConsAvgOff())
				          + " mAh* --> *Low* sc off consume\\n";
			}
			else
			{
				comment += "Screen Off consume: *" + formatNumber(getConsAvgOff()) + " mAh* \\n";
			}
			
			if (remTime > 1000)
				comment += "Estimated remaining battery time: "
				          + DateTimeOperator.getTimeStringFromMillis(remTime) + "\\n";
			
			comment += "{panel}\\n";
			
			return comment;
		}
		else
		{
			System.out.println("no comment generated");
		}
		
		return "";
	}
	
	public String eblDecreasedReasons()
	{
		thresholdInc = 0;
		
		if (rawStats != null && rawStats.length() > 80)
		{
			String eblDecrease = "";
			if (getPercentage(scOnTime, timeOnBat) > 15) // BATTRIAGE-165
			{
				long screenBright = scBright + scLight + scMedium;
				if (getPercentage(screenBright, scOnTime) > 20)
				{
					eblDecrease = eblDecrease + "Screen was Bright/Light/Moderate for "
					              + DateTimeOperator.getTimeStringFromMillis(screenBright) + " ("
					              + formatNumber(getPercentage(screenBright, scOnTime)) + "%)\\n";
				}
			}
			
			long phoneBadSignal = signalNone + signalPoor + signalModerate;
			if (getPercentage(phoneBadSignal, timeOnBat) > 20)
			{
				eblDecrease = eblDecrease + "Phone signal quality was bad (None/Poor/Moderate) for "
				              + DateTimeOperator.getTimeStringFromMillis(phoneBadSignal) + " ("
				              + formatNumber(getPercentage(phoneBadSignal, timeOnBat)) + "%)\\n";
				
				thresholdInc +=  0.5 * (getPercentage(phoneBadSignal, timeOnBat) - 10);
			}
			
			long phoneBadRadio = radio1xrtt + radioEvdo + radioUmts + radioGprs;
			if (getPercentage(phoneBadRadio, radioActive) > 20)
			{
				eblDecrease = eblDecrease + "Radio network was not good for "
				              + DateTimeOperator.getTimeStringFromMillis(phoneBadRadio) + " ("
				              + formatNumber(getPercentage(phoneBadRadio, radioActive)) + "%)\\n";
				thresholdInc +=  0.7 * (getPercentage(phoneBadRadio, timeOnBat) - 10);
			}
			
			long wifiBadSignal = wifiLevel0 + wifiLevel1 + wifiLevel2;
			if (getPercentage(wifiBadSignal, timeOnBat) > 20)
			{
				eblDecrease = eblDecrease + "Wifi signal quality was bad (Level0/Level1/Level2) for "
				              + DateTimeOperator.getTimeStringFromMillis(wifiBadSignal) + " ("
				              + formatNumber(getPercentage(wifiBadSignal, timeOnBat)) + "%)\\n";
				thresholdInc +=  0.4 * (getPercentage(wifiBadSignal, timeOnBat) - 10);
			}
			if (getPercentage(wifiScan, timeOnBat) > 20)
			{
				eblDecrease = eblDecrease + "Scanning for better wifi network for "
				              + DateTimeOperator.getTimeStringFromMillis(wifiScan) + " ("
				              + formatNumber(getPercentage(wifiScan, timeOnBat)) + "%)\\n";
				thresholdInc +=  0.31 * (getPercentage(wifiScan, timeOnBat) - 10);
			}
			
			return eblDecrease;
		}
		else
		{
			System.out.println("no comment generated");
		}
		
		return "";
		
	}
	
	// Get specific data
	public float getConsAvgOff()
	{
		return (float) (dischScOff * (batCap / 100.0) / (scOffTime / 3600000.0));
	}
	
	public float getConsAvgOn()
	{
		return (float) (dischScOn * (batCap / 100.0) / (scOnTime / 3600000.0));
	}
	
	private float getPercentage(long percentageOf, long from)
	{
		return (float) (100.0 * percentageOf / from);
	}
	
	public String getWakelocksComment()
	{
		return wakelocksComment;
	}
	
	// Getters and Setters ---------------------------------------------------
	public float getBatCap()
	{
		return batCap;
	}
	
	public void setBatCap(float batCap)
	{
		this.batCap = batCap;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public long getTimeOnBat()
	{
		return timeOnBat;
	}
	
	public long getScOffTime()
	{
		return scOffTime;
	}
	
	public long getRemTime()
	{
		return remTime;
	}
	
	public long getScDark()
	{
		return scDark;
	}
	
	public long getScDim()
	{
		return scDim;
	}
	
	public long getScMedium()
	{
		return scMedium;
	}
	
	public long getScLight()
	{
		return scLight;
	}
	
	public long getScBright()
	{
		return scBright;
	}
	
	public long getSignalNone()
	{
		return signalNone;
	}
	
	public long getSignalPoor()
	{
		return signalPoor;
	}
	
	public long getSignalModerate()
	{
		return signalModerate;
	}
	
	public long getSignalGood()
	{
		return signalGood;
	}
	
	public long getSignalGreat()
	{
		return signalGreat;
	}
	
	public long getSignalScan()
	{
		return signalScan;
	}
	
	public long getRadioNone()
	{
		return radioNone;
	}
	
	public long getRadioGprs()
	{
		return radioGprs;
	}
	
	public long getRadioUmts()
	{
		return radioUmts;
	}
	
	public long getRadioEvdo()
	{
		return radioEvdo;
	}
	
	public long getRadio1xrtt()
	{
		return radio1xrtt;
	}
	
	public long getRadioActive()
	{
		return radioActive;
	}
	
	public long getWifiRun()
	{
		return wifiRun;
	}
	
	public long getWifiScan()
	{
		return wifiScan;
	}
	
	public long getWifiLevel0()
	{
		return wifiLevel0;
	}
	
	public long getWifiLevel1()
	{
		return wifiLevel1;
	}
	
	public long getWifiLevel2()
	{
		return wifiLevel2;
	}
	
	public long getWifiLevel3()
	{
		return wifiLevel3;
	}
	
	public long getWifiLevel4()
	{
		return wifiLevel4;
	}
	
	public int getConnectChanges()
	{
		return connectChanges;
	}
	
	public float getDischargeAmount()
	{
		return dischargeAmount;
	}
	
	public float getDischScOn()
	{
		return dischScOn;
	}
	
	public float getDischScOff()
	{
		return dischScOff;
	}
	
	public ArrayList<BugRepKernelWL> getKernelWLs()
	{
		return kernelWLs;
	}
	
	public ArrayList<BugRepJavaWL> getJavaWLs()
	{
		return javaWLs;
	}

	public int getThresholdInc()
	{
		return thresholdInc;
	}
}
