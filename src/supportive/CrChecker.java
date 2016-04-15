package supportive;


import java.io.File;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

import supportive.preanalyzers.btdparser.BtdParser;
import supportive.preanalyzers.btdparser.BtdUptimePeriod;
import supportive.preanalyzers.btdparser.BtdWL;
import supportive.preanalyzers.logsparser.BugRepJavaWL;
import supportive.preanalyzers.logsparser.BugRepKernelWL;
import supportive.preanalyzers.logsparser.BugrepParser;
import supportive.preanalyzers.logsparser.MainParser;
import core.Logger;
import core.SharedObjs;
import customobjects.CrItem;
import customobjects.HighConsumeItem;
import customobjects.JiraQueryResult;
import filters.Consume;


@SuppressWarnings("unused")
public class CrChecker
{
	private static final String INCOMPLETE = "Incomplete";
	private static final String DUPLICATE  = "Duplicate";
	private static final String CANCELLED  = "Cancelled";
	private static final String INVALID    = "Invalid";
	private String              crPath;
	private String              falsePositiveComment;
	private String              dupComment;
	private String              tetherComment;
	private String              uptimesComment;
	private String              wakelocksComment;
	private boolean             btdParsed;
	private boolean             mainParsed;
	private boolean             bugrepParsed;
	private boolean             btdTether;
	private boolean             mainTether;
	
	private ArrayList<String>   incompleteFiles;
	private ArrayList<String>   filesNames;
	private ArrayList<File>     files;
	
	private BtdParser           btdParser;
	private BugrepParser        bugrepParser;
	private MainParser          mainParser;
	
	private CrItem              cr;
	private JiraSatApi          jira;
	
	public CrChecker(String crPath)
	{
		this.crPath = crPath;
	}
	
	public boolean checkCR()
	{
		File file = new File(crPath);
		cr = SharedObjs.getCrsList().getCrByB2gId(file.getName());
		
		SharedObjs.addLogLine("Analysing " + cr.getJiraID());
		Logger.log("CR CHECKER", "Analysing " + cr.getJiraID());
		
		if (cr != null)
		{
			long start = System.currentTimeMillis();
			
			System.out.println("\n\n");
			SharedObjs.addLogLine("Adding pre analyzed label ...");
			jira = new JiraSatApi(JiraSatApi.DEFAULT_JIRA_URL, SharedObjs.getUser(), SharedObjs.getPass());
			SharedObjs.addLogLine("CR Assignee: " + cr.getAssignee());
			if (cr.getAssignee().equals(""))
			{
				jira.assignIssue(cr.getJiraID());
				jira.addLabel(cr.getJiraID(), "sat_pre_analyzed");
				jira.unassignIssue(cr.getJiraID());
				SharedObjs.addLogLine("Unassigning");
			}
			else
			{
				SharedObjs.addLogLine("Letting assigned");
				jira.addLabel(cr.getJiraID(), "sat_pre_analyzed");
			}
			
			System.out.println("\n\n");
			SharedObjs.addLogLine("Checking if incomplete ...");
			if (checkIfIncomplete())
			{
				jira.assignIssue(cr.getJiraID());
				jira.closeIssue(cr.getJiraID(), JiraSatApi.INCOMPLETE, "The text logs are missing. Could not perform a complete analysis.");
				jira.addLabel(cr.getJiraID(), "sat_closed");
				
				cr.setResolution(INCOMPLETE);
				cr.setAssignee(SharedObjs.getUser());
				if (SharedObjs.satDB != null)
					if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
					{
						SharedObjs.satDB.updateAnalyzedCR(cr);
					}
					else
					{
						SharedObjs.satDB.insertAnalyzedCR(cr);
					}
				
				SharedObjs.addLogLine("Logs are missing. Closing CR " + cr.getJiraID() + " as incomplete");
				
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Done for " + file.getAbsolutePath() + ". Closed as incomplete");
				
				return true;
			}
			
			// Try to parse log files ---------------------------------------------------
			// Parse BTD
			btdParser = new BtdParser(crPath);
			System.out.println("\n\n");
			SharedObjs.addLogLine("Parsing BTD data ...");
			btdParsed = btdParser.parse();
			if (btdParsed)
			{
				SharedObjs.addLogLine("Done");
			}
			else
			{
				SharedObjs.addLogLine("Not possible to parse BTD");
			}
			
			// Parse Main
			mainParser = new MainParser(crPath);
			SharedObjs.addLogLine("Parsing Main log data ...");
			mainParsed = mainParser.parse();
			if (mainParsed)
			{
				SharedObjs.addLogLine("Done");
			}
			else
			{
				SharedObjs.addLogLine("Not possible to parse Main");
			}
			
			// Parse Bugreport
			bugrepParser = new BugrepParser(crPath);
			SharedObjs.addLogLine("Parsing Bugreport log data ...");
			bugrepParsed = bugrepParser.parse();
			if (bugrepParsed)
			{
				SharedObjs.addLogLine("Done");
				dupComment = "";
			}
			else
			{
				SharedObjs.addLogLine("Not possible to parse Bugreport");
			}
			
			System.out.println("\n\n");
			SharedObjs.addLogLine("Checking for wakelocks ...");
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Checking for wakelocks");
			
			if (checkIfWakelocks())
			{
				if (dupComment.length() > 5)
				{
					cr.setResolution("Duplicated");
					cr.setStatus("Closed");
					cr.setAssignee(SharedObjs.getUser());
					
					return true;
				}
				
				if (checkIfUptime())
				{
					wakelocksComment += "\\n\\n" + uptimesComment;
				}
				
				jira.addComment(cr.getJiraID(), wakelocksComment);
				
				cr.setAssignee(SharedObjs.getUser());
				
				if (SharedObjs.satDB != null)
					if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
					{
						SharedObjs.satDB.updateAnalyzedCR(cr);
					}
					else
					{
						SharedObjs.satDB.insertAnalyzedCR(cr);
					}
				
				SharedObjs.addLogLine("Could not duplicate this CR. Needs manual analysis.");
				
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Done for " + file.getAbsolutePath() + ". PMS stuck was detected. Needs manual analysis.");
				
				return false;
			}
			
			System.out.println("\n\n");
			SharedObjs.addLogLine("Checking for tethering ...");
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Checking for tethering");
			if (checkIfTethering())
			{
				jira.assignIssue(cr.getJiraID());
				jira.closeIssue(cr.getJiraID(), JiraSatApi.INVALID, tetherComment);
				jira.addLabel(cr.getJiraID(), "sat_closed");
				
				cr.setResolution(INVALID);
				cr.setAssignee(SharedObjs.getUser());
				if (SharedObjs.satDB != null)
					if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
					{
						SharedObjs.satDB.updateAnalyzedCR(cr);
					}
					else
					{
						SharedObjs.satDB.insertAnalyzedCR(cr);
					}
				
				SharedObjs.addLogLine("Tethering detected. Closing CR " + cr.getJiraID() + " as invalid");
				
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Done for " + file.getAbsolutePath() + ". Closed as invalid");
				
				return true;
			}
			
			System.out.println("\n\n");
			SharedObjs.addLogLine("Checking for uptime ...");
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Checking for uptime");
			
			if (checkIfUptime())
			{
				jira.addComment(cr.getJiraID(),
				                "Some long uptimes were detected. This CR shall be manually analized in order to ensure if there are issues or not in this CR.\\n\\n"
				                                + uptimesComment);
				
				cr.setAssignee(SharedObjs.getUser());
				if (SharedObjs.satDB != null)
					if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
					{
						SharedObjs.satDB.updateAnalyzedCR(cr);
					}
					else
					{
						SharedObjs.satDB.insertAnalyzedCR(cr);
					}
				
				SharedObjs.addLogLine("Uptimes detected. Needs manual analysis.");
				
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Done for " + file.getAbsolutePath() + ". Uptimes detected. Needs manual analysis.");
				
				return false;
			}
			
			System.out.println("\n\n");
			SharedObjs.addLogLine("Checking for high consumption issues ...");
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Checking for high consumption issues");
			
			if (checkIfHighCons())
			{
				if (dupComment.length() > 5)
				{
					cr.setResolution("Duplicated");
					cr.setStatus("Closed");
					cr.setAssignee(SharedObjs.getUser());
					
					return true;
				}
				
				// TODO Generate a comment about processes consumption ???
			}
			
			System.out.println("\n\n");
			SharedObjs.addLogLine("Checking if false positive ...");
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Checking for false positive");
			
			if (checkIfFalsePositive())
			{
				return true;
			}
			
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Nothing detected");
			SharedObjs.addLogLine("Nothing detected. " + DateTimeOperator.getTimeStringFromMillis((System.currentTimeMillis() - start)));
			
			String comment = "";
			String eblDecresed = "{panel:title=*Items that increases current drain and decreases EBL*|titleBGColor=#E9F2FF}\\n";
			if (bugrepParsed)
			{
				comment += bugrepParser.currentDrainStatistics();
				eblDecresed = eblDecresed + bugrepParser.eblDecreasedReasons();
			}
			if (btdParsed)
			{
				if (comment.equals(""))
					comment += btdParser.toJiraComment();
				
				eblDecresed = eblDecresed + btdParser.eblDecreasers();
			}
			eblDecresed = eblDecresed + "{panel}\\n";
			if (eblDecresed.split("\\\\n|\\n|\n").length > 2)
			{
				comment = comment + eblDecresed;
			}
			
			jira.addComment(cr.getJiraID(), comment);
			
			Logger.log(Logger.TAG_FALSE_POSITIVE, DateTimeOperator.getTimeStringFromMillis((System.currentTimeMillis() - start)));
			
			if (SharedObjs.satDB != null)
			{
				if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
				{
					SharedObjs.satDB.updateAnalyzedCR(cr);
				}
				else
				{
					SharedObjs.satDB.insertAnalyzedCR(cr);
				}
			}
			
			btdParser.close();
			
			SharedObjs.addLogLine("CR " + cr.getJiraID() + " not closed");
			
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Done for " + file.getAbsolutePath() + "  -  CR " + cr.getJiraID());
			
			return false;
		}
		
		Logger.log(Logger.TAG_BUG2GODOWNLOADER, file.getAbsolutePath() + " was not pre analyzed because this CR is not on the downloaded CRs list");
		return false;
	}
	
	// Checkers ------------------------------------------------------------------------------
	private boolean checkIfIncomplete()
	{
		incompleteFiles = new ArrayList<String>();
		filesNames = new ArrayList<String>();
		files = new ArrayList<File>();
		
		boolean btd = false;
		boolean bugrep = false;
		boolean main = false;
		boolean system = false;
		boolean kernel = false;
		
		File folder = new File(crPath);
		
		if (!folder.isDirectory())
		{
			Logger.log(Logger.TAG_CR_CHECKER, "Not a directory");
			return false;
		}
		
		for (File file : folder.listFiles())
		{
			filesNames.add(file.getName());
			files.add(file);
			System.out.println("file: " + file.getName() + " - " + file.length() / 1048576);
		}
		
		for (String f : filesNames)
		{
			if (f.contains(".btd"))
			{
				btd = true;
			}
			else if (f.contains("bugreport"))
			{
				bugrep = true;
			}
			else if (f.contains("aplogcat-main"))
			{
				main = true;
			}
			else if (f.contains("aplogcat-system"))
			{
				system = true;
			}
			else if (f.contains("aplogcat-kernel"))
			{
				kernel = true;
			}
		}
		
		if (!btd)
		{
			incompleteFiles.add("btd");
			Logger.log(Logger.TAG_CR_CHECKER, "BTD file is missing");
		}
		
		if (bugrep)
		{
			for (File f : files)
			{
				if (f.getName().contains("bugreport"))
				{
					if (f.length() / 1023999 < 3)
					{
						incompleteFiles.add("bugreport");
						Logger.log(Logger.TAG_CR_CHECKER, "Bugreport file is too short");
					}
					break;
				}
			}
		}
		else
		{
			incompleteFiles.add("bugreport");
			Logger.log(Logger.TAG_CR_CHECKER, "Bugreport file is missing");
		}
		
		if (main)
		{
			File f = new File(crPath + "/aplogcat-main.txt");
			if (f.length() / 1023999 < 2)
			{
				incompleteFiles.add("main");
				Logger.log(Logger.TAG_CR_CHECKER, "Main file is too short " + f.length());
			}
		}
		else
		{
			incompleteFiles.add("main");
			Logger.log(Logger.TAG_CR_CHECKER, "Main file is missing ");
		}
		
		if (system)
		{
			File f = new File(crPath + "/aplogcat-system.txt");
			if (f.length() / 1023999 < 2)
			{
				incompleteFiles.add("system");
				Logger.log(Logger.TAG_CR_CHECKER, "System file is too short");
			}
		}
		else
		{
			incompleteFiles.add("system");
			Logger.log(Logger.TAG_CR_CHECKER, "System file is missing");
		}
		
		if (kernel)
		{
			File f = new File(crPath + "/aplogcat-kernel.txt");
			if (f.length() / 1023999 < 2)
			{
				incompleteFiles.add("kernel");
				Logger.log(Logger.TAG_CR_CHECKER, "Kernel file is too short");
			}
		}
		else
		{
			incompleteFiles.add("kernel");
			Logger.log(Logger.TAG_CR_CHECKER, "Kernel file is missing");
		}
		
		if (incompleteFiles.size() >= 4)
			return true;
		else
			return false;
	}
	
	private boolean checkIfTethering()
	{
		long start = System.currentTimeMillis();
		
		System.out.println();
		Logger.log(Logger.TAG_BUG2GODOWNLOADER, "-----------------");
		System.out.println();
		
		Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Verifying BTD");
		
		if (btdParsed)
		{
			btdTether = btdParser.tethering();
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Tethering issue? " + btdTether);
		}
		else
		{
			btdTether = false;
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Could not parse BTD file");
		}
		
		Logger.log(Logger.TAG_BUG2GODOWNLOADER,
		           "\nBTD parse and tethering detection process took " + DateTimeOperator.getTimeStringFromMillis((System.currentTimeMillis() - start)) + "\n");
		
		// Check for tethering
		long now = System.currentTimeMillis();
		
		Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Verifying Main file");
		
		if (mainParsed)
		{
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Verifying Main file");
			if (btdParsed && mainParser.getTotalLogTime() < btdParser.getLongerDischargingPeriod().getDuration())
			{
				mainTether = false;
			}
			else
			{
				mainTether = mainParser.checkForTethering();
			}
			mainParser.showTetheringData();
		}
		else
		{
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Could not parse Main log");
		}
		
		Logger.log(Logger.TAG_BUG2GODOWNLOADER,
		           "\nMain parse and tethering detection process took " + DateTimeOperator.getTimeStringFromMillis((System.currentTimeMillis() - now)) + "\n");
		
		tetherComment = "The user is *tethering a Wifi* network. Thus, this CR can be considered invalid for current drain analysis.\\n\\n";
		
		// Wi-Fi tethering is enabled for 11% or more from the discharge time
		if (btdTether)
		{
			tetherComment = tetherComment + "- Following BTD file, SAT has detected that Wi-Fi tethering is enabled for " + btdParser.tetherPercentage()
			                + "% of the discharge time.\\n\\n";
		}
		// Wi-Fi tethering is enabled for 11% or more from the discharge time
		if (mainTether)
		{
			tetherComment = tetherComment + "- Following main log file, SAT has detected that Wi-Fi tethering is enabled for "
			                + mainParser.getTetherPercentage() + "% of the discharge time.\\n";
			tetherComment = tetherComment + "Tethering periods found in main log:\\n";
			for (int i = 0; i < mainParser.getWifiPeriods().size(); i++)
			{
				if (mainParser.getWifiPeriods().get(i).getDuration() > 0)
				{
					tetherComment = tetherComment + "Period " + (i + 1) + ":\\n";
					tetherComment = tetherComment + "|" + mainParser.getWifiPeriods().get(i).startLine + "|\\n";
					tetherComment = tetherComment + "|" + mainParser.getWifiPeriods().get(i).endLine + "|\\n";
					tetherComment = tetherComment + "|Duration: " + DateTimeOperator.getTimeStringFromMillis(mainParser.getWifiPeriods().get(i).getDuration())
					                + "|\\n";
				}
			}
			
			tetherComment = tetherComment + "\\n";
		}
		
		Logger.log(Logger.TAG_BUG2GODOWNLOADER, "BTD Tether: " + btdTether);
		Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Main Tether: " + mainTether);
		
		if (btdTether || mainTether)
		{
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Closed as tethering");
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkIfHighCons()
	{
		dupComment = "";
		Consume.makelog(crPath + "\\");
		
		HighConsumeItem system_server = null;
		
		if (Consume.getHCList().indexOf("system_server") >= 0)
		{
			system_server = Consume.getHCList().get(Consume.getHCList().indexOf("system_server"));
		}
		else
		{
			return false;
		}
		
		HighConsumeItem hci = null;
		boolean dupped = false;
		String dupCRs = "";
		
		SharedObjs.addLogLine("Consumption issue detected. Searching for a root CR for this issue...");
		
		for (int i = 0; i < Consume.getHCList().size(); i++)
		{
			hci = Consume.getHCList().get(i);
			
			if ((hci.getScOffConsume() >= 10 || hci.getConsumeAvg() >= 23) && hci.getOccurencesOff() >= system_server.getOccurencesOff() * 0.3) // High
			                                                                                                                                    // consumption
			{
				String project = "IKSW";
				if (cr.getBuild().equals(""))
				{
					project += "M";
				}
				else
				{
					project += cr.getBuild().toUpperCase().substring(0, 1);
				}
				
				Logger.log(Logger.TAG_CR_CHECKER, "Project to dup for: " + project);
				
				String jSONOutput = jira.query("project = " + project + " AND summary ~ \\\"" + hci.getProcess()
				                               + "\\\" AND summary ~ \\\"consuming too much CPU power\\\" AND (labels = cd_auto OR labels = cd_manual)");
				JiraQueryResult jqr = new JiraQueryResult(jSONOutput);
				
				if (jqr.getResultCount() == 1)
				{
					try
					{
						SharedObjs.addLogLine("Root CR " + jqr.getItems().get(0).getKey() + " detected. Checking if CR is valid ...");
						CrItem root = jira.getCrData(jqr.getItems().get(0).getKey());
						
						if (root.getResolution().equals("Duplicate"))
						{
							if (dupCRs.length() > 5)
							{
								dupCRs += ", " + root.getDup();
								dupComment += "\\n\\n" + hci + "Duplicated of " + root.getDup();
							}
							else
							{
								dupCRs = root.getDup();
								dupComment = "*High CPU consumption detected*\\n\\n" + hci + "Duplicated of " + root.getDup();
							}
							
							dupped = true;
						}
						else
						{
							if (dupCRs.length() > 5)
							{
								dupCRs += ", " + jqr.getItems().get(0).getKey();
								dupComment += "\\n\\n" + hci + "Duplicated of " + jqr.getItems().get(0).getKey();
							}
							else
							{
								dupCRs = jqr.getItems().get(0).getKey();
								dupComment = "*High CPU consumption detected*\\n\\n" + hci + "Duplicated of " + jqr.getItems().get(0).getKey();
							}
							
							dupped = true;
						}
					}
					catch (ParseException e)
					{
						e.printStackTrace();
					}
				}
				
				if (i == 0 && dupped == false)
				{
					break;
				}
			}
			else if (hci.getProcess().equals("system_server") && hci.getScOffConsume() < 10 && hci.getOccurencesOff() >= system_server.getOccurencesOff() * 0.7) // Too
			                                                                                                                                                     // often
			{
				String project = "IKSW";
				if (cr.getBuild().equals(""))
				{
					project += "M";
				}
				else
				{
					project += cr.getBuild().toUpperCase().substring(0, 1);
				}
				
				Logger.log(Logger.TAG_CR_CHECKER, "Project to dup for: " + project);
				
				String jSONOutput = jira.query("project = " + project + " AND summary ~ \\\"" + hci.getProcess()
				                               + "\\\" AND summary ~ \\\"running nonstop in background\\\" AND (labels = cd_auto OR labels = cd_manual)");
				JiraQueryResult jqr = new JiraQueryResult(jSONOutput);
				
				if (jqr.getResultCount() == 1)
				{
					try
					{
						SharedObjs.addLogLine("Root CR " + jqr.getItems().get(0).getKey() + " detected. Checking if CR is valid ...");
						CrItem root = jira.getCrData(jqr.getItems().get(0).getKey());
						
						if (root.getResolution().equals("Duplicate"))
						{
							if (dupCRs.length() > 5)
							{
								dupCRs += ", " + root.getDup();
								dupComment += "\\n\\n" + hci + "Duplicated of " + root.getDup();
							}
							else
							{
								dupCRs = root.getDup();
								dupComment = "*High CPU consumption detected*\\n\\n" + hci + "Duplicated of " + root.getDup();
							}
							
							dupped = true;
						}
						else
						{
							if (dupCRs.length() > 5)
							{
								dupCRs += ", " + jqr.getItems().get(0).getKey();
								dupComment += "\\n\\n" + hci + "Duplicated of " + jqr.getItems().get(0).getKey();
							}
							else
							{
								dupCRs = jqr.getItems().get(0).getKey();
								dupComment = "*High CPU consumption detected*\\n\\n" + hci + "Duplicated of " + jqr.getItems().get(0).getKey();
							}
							
							dupped = true;
						}
					}
					catch (ParseException e)
					{
						e.printStackTrace();
					}
				}
				
				if (i == 0 && dupped == false)
				{
					break;
				}
			}
		}
		
		if (dupCRs.length() > 5)
		{
			SharedObjs.addLogLine("Consumption issue root detected, duplicating CR ...");
			jira.assignIssue(cr.getJiraID());
			jira.addLabel(cr.getJiraID(), "cd_auto");
			jira.addLabel(cr.getJiraID(), "sat_dupped");
			jira.addLabel(cr.getJiraID(), "sat_closed");
			jira.dupIssue(cr.getJiraID(), dupCRs, dupComment.replace("\n", "\\n"));
			SharedObjs.addLogLine("CR duplicated to " + dupCRs);
			
			return true;
		}
		
		return false;
	}
	
	public boolean checkIfFalsePositive()
	{
		if (!btdParsed && !bugrepParsed)
		{
			Logger.log(Logger.TAG_CR_CHECKER, "Could not parse BTD nor Bugreport");
			return false;
		}
		
		System.out.println("btd parsed?: " + btdParsed);
		System.out.println("bugrep parsed?: " + bugrepParsed);
		boolean btdUptime = false;
		
		if (btdParsed)
		{
			btdUptime = btdParser.uptime();
			System.out.println("btd uptime: " + btdUptime);
			System.out.println("cd btd: " + btdParser.getAverageconsumeOff());
			System.out.println("len btd: " + btdParser.eblDecreasers().length());
			System.out.println("phone btd: " + btdParser.phoneCallPercentage());
		}
		if (bugrepParsed)
		{
			System.out.println("bugrep uptime: " + bugrepParser.checkIfWakelocks(btdUptime));
			System.out.println("cd btd: " + bugrepParser.getConsAvgOff());
			System.out.println("len btd: " + bugrepParser.eblDecreasedReasons().length());
		}
		
		// Get battery capacity from BTD file
		if (btdParsed && bugrepParsed)
		{
			if (btdParser.getBatCap() > bugrepParser.getBatCap())
			{
				bugrepParser.setBatCap(btdParser.getBatCap());
			}
			
			int cdThreashold = 100;
			String bugrepEblDrecresers = bugrepParser.eblDecreasedReasons();
			String btdEblDrecresers = bugrepParser.eblDecreasedReasons();
			Logger.log(Logger.TAG_CR_CHECKER, "BTD Threshold: " + btdParser.getThresholdInc());
			Logger.log(Logger.TAG_CR_CHECKER, "Bugrep Threshold: " + bugrepParser.getThresholdInc());
			cdThreashold += btdParser.getThresholdInc();
			cdThreashold += bugrepParser.getThresholdInc();
			if (cdThreashold > 155)
			{
				Logger.log(Logger.TAG_CR_CHECKER, "Calculated Threshold: " + cdThreashold);
				cdThreashold = 155;
				Logger.log(Logger.TAG_CR_CHECKER, "Threshold limited to 155");
			}
			else if (cdThreashold < 100)
			{
				Logger.log(Logger.TAG_CR_CHECKER, "Calculated Threshold: " + cdThreashold);
				cdThreashold = 115;
				Logger.log(Logger.TAG_CR_CHECKER, "Threshold set to 115");
			}
			else
			{
				Logger.log(Logger.TAG_CR_CHECKER, "Calculated Threshold: " + cdThreashold);
			}
			
			if ((btdParser.getAverageconsumeOff() <= cdThreashold || bugrepParser.getConsAvgOff() <= cdThreashold)
			    && (btdEblDrecresers.length() > 10 || bugrepEblDrecresers.length() > 10))
			{
				String comment = bugrepParser.currentDrainStatistics();
				comment += "\\n\\n" + btdParser.currentDrainStatistics();
				String eblDecresed = "\\n{panel:title=*Items that increases current drain and decreases EBL*|titleBGColor=#E9F2FF}\\n";
				eblDecresed += bugrepParser.eblDecreasedReasons();
				eblDecresed += btdParser.eblDecreasers();
				eblDecresed += "{panel}\\n";
				
				if (eblDecresed.split("\\\\n|\\n|\n").length > 2)
				{
					comment = comment + eblDecresed;
				}
				
				comment += "\\n- No current drain issues found in this CR.\\n\\nClosing as cancelled.";
				
				System.out.println("-- Comments:");
				System.out.println(comment.replaceAll("\\n", "\n"));
				System.out.println(btdParser.eblDecreasers());
				System.out.println(bugrepParser.eblDecreasedReasons());
				
				jira.assignIssue(cr.getJiraID());
				jira.addLabel(cr.getJiraID(), "sat_closed");
				jira.closeIssue(cr.getJiraID(), JiraSatApi.CANCELLED, comment);
				
				cr.setResolution(CANCELLED);
				cr.setAssignee(SharedObjs.getUser());
				if (SharedObjs.satDB != null)
					if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
					{
						SharedObjs.satDB.updateAnalyzedCR(cr);
					}
					else
					{
						SharedObjs.satDB.insertAnalyzedCR(cr);
					}
				
				SharedObjs.addLogLine("This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				return true;
			}
			else if ((btdParser.getAverageconsumeOff() <= 125 && bugrepParser.getConsAvgOff() <= 125) && btdParser.phoneCallPercentage() > 9)
			{
				String comment = bugrepParser.currentDrainStatistics();
				String eblDecresed = "{panel:title=*Items that increases current drain and decreases EBL*|titleBGColor=#E9F2FF}\\n";
				eblDecresed = eblDecresed + bugrepParser.eblDecreasedReasons();
				eblDecresed = eblDecresed + btdParser.eblDecreasers();
				eblDecresed = eblDecresed + "{panel}\\n";
				
				if (eblDecresed.split("\\\\n|\\n|\n").length > 2)
				{
					comment = comment + eblDecresed;
				}
				
				comment = comment + "\\n- No current drain issues found in this CR.\\n\\nClosing as cancelled.";
				
				System.out.println("-- Comments:");
				System.out.println(comment.replaceAll("\\n", "\n"));
				System.out.println(btdParser.eblDecreasers());
				System.out.println(bugrepParser.eblDecreasedReasons());
				
				jira.assignIssue(cr.getJiraID());
				jira.addLabel(cr.getJiraID(), "sat_closed");
				jira.closeIssue(cr.getJiraID(), JiraSatApi.CANCELLED, comment);
				
				cr.setResolution(CANCELLED);
				cr.setAssignee(SharedObjs.getUser());
				if (SharedObjs.satDB != null)
					if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
					{
						SharedObjs.satDB.updateAnalyzedCR(cr);
					}
					else
					{
						SharedObjs.satDB.insertAnalyzedCR(cr);
					}
				
				SharedObjs.addLogLine("This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				return true;
			}
			else if ((btdParser.getAverageconsumeOff() <= 70 || bugrepParser.getConsAvgOff() <= 70) && btdParser.uptime() == false
			         && bugrepParser.checkIfWakelocks(false) == false)
			{
				String comment = bugrepParser.currentDrainStatistics();
				
				comment = comment + "\\n- No current drain issues found in this CR.\\n\\nClosing as cancelled.";
				
				// System.out.println("-- Comments:");
				// System.out.println(comment.replaceAll("\\n", "\n"));
				
				jira.assignIssue(cr.getJiraID());
				jira.addLabel(cr.getJiraID(), "sat_closed");
				jira.closeIssue(cr.getJiraID(), JiraSatApi.CANCELLED, comment);
				
				cr.setResolution(CANCELLED);
				cr.setAssignee(SharedObjs.getUser());
				if (SharedObjs.satDB != null)
					if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
					{
						SharedObjs.satDB.updateAnalyzedCR(cr);
					}
					else
					{
						SharedObjs.satDB.insertAnalyzedCR(cr);
					}
				
				SharedObjs.addLogLine("This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				return true;
			}
		}
		else if (bugrepParsed)
		{
			if (bugrepParser.getConsAvgOff() <= 115 && bugrepParser.eblDecreasedReasons().length() > 10)
			{
				String comment = bugrepParser.currentDrainStatistics();
				String eblDecresed = "{panel:title=*Items that increases current drain and decreases EBL*|titleBGColor=#E9F2FF}\\n";
				eblDecresed = eblDecresed + bugrepParser.eblDecreasedReasons();
				eblDecresed = eblDecresed + "{panel}\\n";
				
				if (eblDecresed.split("\\\\n|\\n|\n").length > 2)
				{
					comment = comment + eblDecresed;
				}
				
				comment = comment + "\\n- No current drain issues found in this CR.\\n\\nClosing as cancelled.";
				
				jira.assignIssue(cr.getJiraID());
				jira.addLabel(cr.getJiraID(), "sat_closed");
				jira.closeIssue(cr.getJiraID(), JiraSatApi.CANCELLED, comment);
				
				cr.setResolution(CANCELLED);
				cr.setAssignee(SharedObjs.getUser());
				if (SharedObjs.satDB != null)
					if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
					{
						SharedObjs.satDB.updateAnalyzedCR(cr);
					}
					else
					{
						SharedObjs.satDB.insertAnalyzedCR(cr);
					}
				
				System.out.println("-- Comments:");
				System.out.println(comment.replaceAll("\\n", "\n"));
				
				SharedObjs.addLogLine("This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				return true;
			}
		}
		else if (btdParsed)
		{
			int cdThreashold = 110;
			String btdEblDrecresers = btdParser.eblDecreasers();
			Logger.log(Logger.TAG_CR_CHECKER, "BTD Threshold: " + btdParser.getThresholdInc());
			cdThreashold += btdParser.getThresholdInc();
			if (cdThreashold > 155)
			{
				Logger.log(Logger.TAG_CR_CHECKER, "Calculated Threshold: " + cdThreashold);
				cdThreashold = 155;
				Logger.log(Logger.TAG_CR_CHECKER, "Threshold limited to 155");
			}
			else if (cdThreashold < 100)
			{
				Logger.log(Logger.TAG_CR_CHECKER, "Calculated Threshold: " + cdThreashold);
				cdThreashold = 115;
				Logger.log(Logger.TAG_CR_CHECKER, "Threshold set to 115");
			}
			else
			{
				Logger.log(Logger.TAG_CR_CHECKER, "Calculated Threshold: " + cdThreashold);
			}
			
			if (btdParser.getAverageconsumeOff() <= cdThreashold && btdEblDrecresers.length() > 10)
			{
				String comment = "Bugreport could not be parsed or does not have device statistics\\n\\n";
				comment += btdParser.toJiraComment();
				comment += btdParser.currentDrainStatistics().replace("\n", "\\n");
				
				String eblDecresed = "{panel:title=*Items that increases current drain and decreases EBL*|titleBGColor=#E9F2FF}\\n";
				eblDecresed += btdParser.eblDecreasers().replace("\n", "\\n");
				eblDecresed += "{panel}\\n";
				
				if (eblDecresed.split("\\\\n|\\n|\n").length > 2)
				{
					comment = comment + eblDecresed;
				}
				else
				{
					Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Could not detect EBL decreasers");
				}
				
				comment += "\\n- No current drain issues found in this CR.\\n\\nClosing as cancelled.";
				
				jira.assignIssue(cr.getJiraID());
				String output = jira.closeIssue(cr.getJiraID(), JiraSatApi.CANCELLED, comment);
				if (output.contains("Illegal"))
				{
					Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Error trying to close CR");
					return false;
				}
				jira.addLabel(cr.getJiraID(), "sat_closed");
				
				cr.setResolution(CANCELLED);
				cr.setAssignee(SharedObjs.getUser());
				if (SharedObjs.satDB != null)
					if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
					{
						SharedObjs.satDB.updateAnalyzedCR(cr);
					}
					else
					{
						SharedObjs.satDB.insertAnalyzedCR(cr);
					}
				
				SharedObjs.addLogLine("This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "1 This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				return true;
			}
			else if (btdParser.getAverageconsumeOff() <= 125 && btdParser.phoneCallPercentage() > 9)
			{
				String comment = btdParser.toJiraComment();
				String eblDecresed = "{panel:title=*Items that increases current drain and decreases EBL*|titleBGColor=#E9F2FF}\\n";
				eblDecresed += btdParser.eblDecreasers();
				eblDecresed += "{panel}\\n";
				
				if (eblDecresed.split("\\\\n|\\n|\n").length > 2)
				{
					comment = comment + eblDecresed;
				}
				
				comment += "\\n- No current drain issues found in this CR.\\n\\nClosing as cancelled.";
				
				System.out.println("-- Comments:");
				System.out.println(comment.replaceAll("\\n", "\n"));
				System.out.println(btdParser.eblDecreasers());
				
				jira.assignIssue(cr.getJiraID());
				jira.addLabel(cr.getJiraID(), "sat_closed");
				jira.closeIssue(cr.getJiraID(), JiraSatApi.CANCELLED, comment);
				
				cr.setResolution(CANCELLED);
				cr.setAssignee(SharedObjs.getUser());
				if (SharedObjs.satDB != null)
					if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
					{
						SharedObjs.satDB.updateAnalyzedCR(cr);
					}
					else
					{
						SharedObjs.satDB.insertAnalyzedCR(cr);
					}
				
				SharedObjs.addLogLine("This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "2 This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				return true;
			}
			else if (btdParser.getAverageconsumeOff() <= 70 && btdParser.uptime() == false)
			{
				String comment = btdParser.toJiraComment();
				String eblDecresed = "{panel:title=*Items that increases current drain and decreases EBL*|titleBGColor=#E9F2FF}\\n";
				eblDecresed += btdParser.eblDecreasers();
				eblDecresed += "{panel}\\n";
				
				if (eblDecresed.split("\\\\n|\\n|\n").length > 2)
				{
					comment += eblDecresed;
				}
				
				comment += "\\n- No current drain issues found in this CR.\\n\\nClosing as cancelled.";
				
				// System.out.println("-- Comments:");
				// System.out.println(comment.replaceAll("\\n", "\n"));
				
				jira.assignIssue(cr.getJiraID());
				jira.addLabel(cr.getJiraID(), "sat_closed");
				jira.closeIssue(cr.getJiraID(), JiraSatApi.CANCELLED, comment);
				
				cr.setResolution(CANCELLED);
				cr.setAssignee(SharedObjs.getUser());
				if (SharedObjs.satDB != null)
					if (SharedObjs.satDB.existsAnalyzedCR(cr.getJiraID()) > 0)
					{
						SharedObjs.satDB.updateAnalyzedCR(cr);
					}
					else
					{
						SharedObjs.satDB.insertAnalyzedCR(cr);
					}
				
				SharedObjs.addLogLine("This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "3 This CR is a false positive. Closing " + cr.getJiraID() + " as cancelled");
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean checkIfUptime()
	{
		uptimesComment = "";
		if (btdParsed)
		{
			if (mainParsed && mainParser.getTotalTetherTime() >= btdParser.getUptimes().getTotalTime() * 0.7)
			{
				return false;
			}
			else
			{
				if (btdParser.uptimeScOff())
				{
					uptimesComment = "{panel:title=*Long uptimes while screen continuously OFF*|titleBGColor=#E9F2FF}\\n";
					int i = 1;
					long total = 0;
					
					for (BtdUptimePeriod ut : btdParser.getUptimesScOff())
					{
						uptimesComment += "- *Uptime " + i + "*\\n";
						uptimesComment += ut.toJiraComment();
						total = total + ut.getDuration();
						i++;
					}
					uptimesComment += "\\n\\n||TOTAL TIME|| " + DateTimeOperator.getTimeStringFromMillis(total) + "|";
					
					uptimesComment += "\\n{panel}\\n\\n";
				}
				else if (btdParser.uptime())
				{
					uptimesComment = "{panel:title=*All long uptimes detected*|titleBGColor=#E9F2FF}\\n";
					int i = 1;
					long total = 0;
					
					for (BtdUptimePeriod ut : btdParser.getUptimes())
					{
						uptimesComment += "- *Uptime " + i + "*\\n";
						uptimesComment += ut.toJiraComment();
						total = total + ut.getDuration();
						i++;
					}
					uptimesComment += "\\n\\n||TOTAL TIME|| " + DateTimeOperator.getTimeStringFromMillis(total) + "|";
					
					uptimesComment += "\\n{panel}\\n\\n";
				}
			}
			
			if (uptimesComment.length() > 80)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean checkIfWakelocks()
	{
		wakelocksComment = "";
		dupComment = "";
		boolean btdWake = false;
		
		if (btdParsed && btdParser.wakeLocks())
		{
			wakelocksComment = "{panel:title=*BTD wake locks detected*|titleBGColor=#E9F2FF}\\n";
			int i = 1;
			long total = 0;
			
			for (BtdWL ut : btdParser.getWakeLocks())
			{
				wakelocksComment += "- *Wakelock " + i + "*\\n";
				wakelocksComment += ut.toJiraComment();
				total = total + ut.getTotalTime();
				i++;
			}
			wakelocksComment += "\\n\\n*TOTAL HELD TIME:* " + DateTimeOperator.getTimeStringFromMillis(total) + "\\n";
			
			wakelocksComment += "\\n{panel}\\n\\n";
			
			btdWake = true;
		}
		
		if (bugrepParsed)
		{
			boolean bugrepWakelocksDetected = bugrepParser.checkIfWakelocks(btdWake);
			
			if (bugrepWakelocksDetected)
			{
				wakelocksComment += "\\n\\n" + bugrepParser.getWakelocksComment();
				ArrayList<BugRepJavaWL> javaWkls = bugrepParser.getJavaWLs();
				ArrayList<BugRepKernelWL> kernelWkls = bugrepParser.getKernelWLs();
				
				// Wakelock dup
				if (wakelocksComment.length() > 50)
				{
					SharedObjs.addLogLine("Wakelock detected. Searching for similar issue ...");
					String dupCRs = "";
					
					// Check for Java wake locks
					if (wakelocksComment.contains("Bugreport Java wake locks"))
					{
						boolean dupped = false;
						BugRepKernelWL pmsw = null;
						
						// Get the kernel PMS.Wakelock data
						for (int i = 0; i < 3; i++)
						{
							if (kernelWkls.get(i).getName().contains("PowerManagerService.WakeLocks"))
							{
								pmsw = kernelWkls.get(i);
							}
						}
						
						for (int i = 0; i < 4; i++)
						{
							//BATTRIAGE-243 SAT cant dup correctly when process is "media"
							if(javaWkls.get(i).getProcessName().equals("media"))
								continue;
							// ------
							
							// BATTRIAGE-249
							if (javaWkls.get(i).getTagName().length() > 110 || javaWkls.get(i).getTagName().split(" ").length >= 2)
								continue;
							// ------
							
							if (javaWkls.get(i).getDuration() > 60 * 60 * 1000 && pmsw.getDuration() > 0.5 * bugrepParser.getTimeOnBat()
							    && javaWkls.get(i).getDuration() > 0.8 * pmsw.getDuration())
							{
								BugRepJavaWL wl = javaWkls.get(i);
								
								if (wl.getTagName().contains("*sync*/gmail-ls/com.google")
											    || wl.getTagName().contains("*sync*/com.motorola.email.exchange.push/com.android.exchange/")
											    || wl.getTagName().contains("*sync*/com.yahoo.mobile.client.android.mail"))
								{
									wl.setTagName(wl.getTagName().substring(0, wl.getTagName().lastIndexOf("/")));
								}
								
								String project = "IKSW";
								System.out.println("------------ BUILD ----------------\n" + cr.getBuild() + "\n--------------------------------------");
								if (cr.getBuild().equals("") || cr.getBuild().toLowerCase().contains("analysed"))
								{
									project += "M";
								}
								else
								{
									project += cr.getBuild().toUpperCase().substring(0, 1);
								}
								
								Logger.log(Logger.TAG_CR_CHECKER, "Project to dup for: " + project);
								
								String jSONOutput;
								if (wl.getTimesAcquired() == 0)
									wl.setTimesAcquired(1);
								
								if (wl.getTimesAcquired() < 5 || wl.getDuration() / (1000 * wl.getTimesAcquired()) > 100)
								{
									jSONOutput = jira.query("project = " + project
									                        + " AND summary !~ \\\"CLONE\\\" AND (summary ~ \\\"PMS wkl stuck\\\" OR labels = java_wkl_h) AND"
									                        + " summary ~ \\\"" + wl.getProcessName() + "\\\" AND summary ~ \\\""
									                        + wl.getTagName().replace("*", "").replace("[", "").replace("]", "")
									                        + "\\\" AND (labels = cd_auto OR labels = cd_manual)");
								}
								else if (wl.getDuration() / (1000 * wl.getTimesAcquired()) < 4)
								{
									jSONOutput = jira.query("project = "
									                        + project
									                        + " AND summary !~ \\\"CLONE\\\" AND (summary ~ \\\"PMS wkl acquired/released\\\" OR labels = java_wkl_ar) AND"
									                        + " summary ~ \\\"" + wl.getProcessName() + "\\\" AND summary ~ \\\""
									                        + wl.getTagName().replace("*", "").replace("[", "").replace("]", "")
									                        + "\\\" AND (labels = cd_auto OR labels = cd_manual)");
								}
								else
								{
									continue;
								}
								
								jSONOutput = jira.query("project = " + project + " AND summary !~ \\\"CLONE\\\" AND summary ~ \\\"stuck\\\" AND summary ~ \\\""
								                        + wl.getProcessName() + "\\\" AND summary ~ \\\"" + wl.getTagName().replace("*", "")
								                        + "\\\" AND (labels = cd_auto OR labels = cd_manual)");
								JiraQueryResult jqr = new JiraQueryResult(jSONOutput);
								
								if (jqr.getResultCount() == 1)
								{
									SharedObjs.addLogLine("Root CR " + jqr.getItems().get(0).getKey() + " detected. Checking if CR is valid ...");
									CrItem root;
									
									try
									{
										root = jira.getCrData(jqr.getItems().get(0).getKey());
										
										if (root.getResolution().equals("Duplicate"))
										{
											if (dupCRs.length() > 5)
											{
												dupCRs += ", " + root.getDup();
												dupComment += "\\n\\n" + wl.toJiraComment() + "Duplicated of " + root.getDup();
											}
											else
											{
												dupCRs = root.getDup();
												dupComment = "*Wakelock detected*\\n\\n" + wl.toJiraComment() + "Duplicated of " + root.getDup();
											}
											
											dupped = true;
										}
										else
										{
											if (dupCRs.length() > 5)
											{
												dupCRs += ", " + jqr.getItems().get(0).getKey();
												dupComment += "\\n\\n" + wl.toJiraComment() + "Duplicated of " + jqr.getItems().get(0).getKey();
											}
											else
											{
												dupCRs = jqr.getItems().get(0).getKey();
												dupComment = "*Wakelock detected*\\n\\n" + wl.toJiraComment() + "Duplicated of "
												             + jqr.getItems().get(0).getKey();
											}
											
											dupped = true;
										}
									}
									catch (ParseException e)
									{
										e.printStackTrace();
									}
								}
								
								if (i == 0 && dupped == false)
								{
									break;
								}
							}
						}
					}
					
					// Check for Kernel wake locks
					if (wakelocksComment.contains("Bugreport Kernel wake locks"))
					{
						boolean dupped = false;
						BugRepKernelWL wl = kernelWkls.get(0);
						
						for (int i = 0; i < 4; i++)
						{
							if (wl.getName().contains("PowerManagerService.WakeLocks"))
								continue;
							
							if (kernelWkls.get(i).getDuration() > 1.5 * 60 * 60 * 1000 && kernelWkls.get(i).getDuration() > 0.5 * bugrepParser.getTimeOnBat())
							{
								wl = kernelWkls.get(i);
								String project = "IKSW";
								
								if (cr.getBuild().equals(""))
								{
									project += "M";
								}
								else
								{
									project += cr.getBuild().toUpperCase().substring(0, 1);
								}
								
								Logger.log(Logger.TAG_CR_CHECKER, "Project to dup for: " + project);
								
								String jSONOutput;
								if (wl.getTimesAcquired() == 0)
									wl.setTimesAcquired(1);
								
								if (wl.getTimesAcquired() < 5  || wl.getDuration() / (1000 * wl.getTimesAcquired()) > 200)
								{
									jSONOutput = jira.query("project = "
									                        + project
									                        + " AND summary ~ \\\""
									                        + wl.getName().replace("[", "").replace("]", "")
									                        + "\\\" AND (summary ~ \\\"Kernel wkl stuck\\\" OR labels = krnl_wkl_h) AND (labels = cd_auto OR labels = cd_manual)");
								}
								else if (wl.getDuration() / (1000 * wl.getTimesAcquired()) < 5)
								{
									jSONOutput = jira.query("project = "
									                        + project
									                        + " AND summary ~ \\\""
									                        + wl.getName().replace("[", "").replace("]", "")
									                        + "\\\" AND (summary ~ \\\"Kernel wkl acquired/released\\\" OR labels = krnl_wkl_ar) AND (labels = cd_auto OR labels = cd_manual)");
								}
								else
								{
									continue;
								}
								
								JiraQueryResult jqr = new JiraQueryResult(jSONOutput);
								
								if (jqr.getResultCount() == 1)
								{
									SharedObjs.addLogLine("Root CR " + jqr.getItems().get(0).getKey() + " detected. Checking if CR is valid ...");
									CrItem root;
									
									try
									{
										root = jira.getCrData(jqr.getItems().get(0).getKey());
										
										if (root.getResolution().equals("Duplicate"))
										{
											if (dupCRs.length() > 5)
											{
												dupCRs += ", " + root.getDup();
												dupComment += "\\n\\n" + wl.toJiraComment() + "Duplicated of " + root.getDup();
											}
											else
											{
												dupCRs = root.getDup();
												dupComment = "*Wakelock detected*\\n\\n" + wl.toJiraComment() + "Duplicated of " + root.getDup();
											}
											
											dupped = true;
										}
										else
										{
											if (dupCRs.length() > 5)
											{
												dupCRs += ", " + jqr.getItems().get(0).getKey();
												dupComment += "\\n\\n" + wl.toJiraComment() + "Duplicated of " + jqr.getItems().get(0).getKey();
											}
											else
											{
												dupCRs = jqr.getItems().get(0).getKey();
												dupComment = "*Wakelock detected*\\n\\n" + wl.toJiraComment() + "Duplicated of "
												             + jqr.getItems().get(0).getKey();
											}
											
											dupped = true;
										}
									}
									catch (ParseException e)
									{
										e.printStackTrace();
									}
								}
								
								if (i == 0 && dupped == false)
								{
									
									break;
								}
							}
						}
						
					}
					
					if (dupCRs.length() > 5)
					{
						SharedObjs.addLogLine("Wakelock root detected, duplicating CR ...");
						jira.assignIssue(cr.getJiraID());
						jira.addLabel(cr.getJiraID(), "cd_auto");
						jira.addLabel(cr.getJiraID(), "sat_dupped");
						jira.addLabel(cr.getJiraID(), "sat_closed");
						jira.dupIssue(cr.getJiraID(), dupCRs, dupComment);
						SharedObjs.addLogLine("CR duplicated to " + dupCRs);
						
						return true;
					}
				}
			}
		}
		
		if (wakelocksComment.length() > 100)
		{
			return true;
		}
		
		return false;
	}
	
	// Getters and Setters
	public ArrayList<String> getIncompleteFiles()
	{
		return incompleteFiles;
	}
}
