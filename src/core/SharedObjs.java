package core;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.SAT2;
import models.CrsManagerModel;
import models.OptionsModel;
import models.ParserModel;

import org.apache.commons.io.FileUtils;

import supportive.DBAdapter;
import views.CrsManagerPane;
import views.OptionsPane;
import views.ParserPane;
import views.secondarypanes.AdvancedOptionsPane;
import views.secondarypanes.CustomFiltersPane;
import views.secondarypanes.ParallelTextPopup;
import controllers.CrsManagerController;
import controllers.OptionsController;
import controllers.ParserController;
import customobjects.CrItem;
import customobjects.CrItemsList;
import customobjects.CustomFilterItem;
import customobjects.CustomFiltersList;


/**
 * It contains all shared variables used by SAT
 */
public class SharedObjs
{
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Variables -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Paths
	public static final String         contentFolder  = "Data/";
	private static String              crPath;
	private static String              rootFolderPath;
	private static String              downloadPath;
	public static String               updateFolder1;
	public static String               updateFolder2;
	
	// IO Files
	public static final File           sytemCfgFile   = new File(contentFolder + "cfgs/system_cfg.xml");
	public static final File           userCfgFile    = new File(contentFolder + "cfgs/user_cfg.xml");
	public static final File           messageCfgFile = new File(contentFolder + "cfgs/message.xml");
	public static final File           filtersFile    = new File(contentFolder + "cfgs/filters.xml");
	public static final File           uidsFile       = new File(contentFolder + "cfgs/uids.xml");
	public static final File           reportFile     = new File(contentFolder + "complements/report/report_cfg.xml");
	public static final File           pwdFile        = new File(contentFolder + "cfgs/pass.pwd");
	
	// User
	private static String              user;
	private static String              pass;
	
	// Parser data
	private static String              actualParserResultText;
	
	// Semaphore Control
	private static Semaphore           unzipSemaphore;
	
	// Filters
	private static CustomFiltersList   userFiltersList;
	private static CustomFiltersList   sharedFiltersList;
	private static CustomFiltersList   activeFiltersList;
	private static CustomFiltersPane   customFiltersPane;
	
	// DB
	public static DBAdapter            satDB;
	public static boolean              dbStatus;
	
	private static CrItemsList         crsList;
	private static ParallelTextPopup   closedList;
	private static ParallelTextPopup   openedList;
	
	// Views
	public static JTabbedPane          tabbedPane;
	public static ParserPane           parserPane;
	public static CrsManagerPane       crsManagerPane;
	public static OptionsPane          optionsPane;
	public static AdvancedOptionsPane  advOptions;
	public static SAT2                 satFrame;
	
	// Controllers
	public static ParserController     parserController;
	public static CrsManagerController crsManagerController;
	public static OptionsController    optionsController;
	
	// Models
	public static CrsManagerModel      crsManagerModel;
	public static ParserModel          parserModel;
	public static OptionsModel         optionsModel;
	
	// Thresholds for issue detectors:
	public static long                 threshold;
	public static boolean              isUidsDBModified;
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Model Initializer --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static void initClass()
	{
		// Set UI theme
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
		
		// Try to connect to DB
		try
		{
			satDB = new DBAdapter();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
			Logger.log(Logger.TAG_SHAREDOBJS, "Could not connect to SQL DB");
		}
		
		// Setup connection status
		if (satDB != null)
		{
			dbStatus = true;
		}
		else
		{
			dbStatus = false;
		}
		
		// Initialize variables
		crPath = "";
		isUidsDBModified = false;
		updateFolder1 = XmlMngr.getSystemValueOf(new String[] {"configs", "update_path1"});
		updateFolder2 = XmlMngr.getSystemValueOf(new String[] {"configs", "update_path2"});
		rootFolderPath = XmlMngr.getUserValueOf(new String[] {"parser_pane", "rootPath"});
		unzipSemaphore = new Semaphore(1, true);
		userFiltersList = new CustomFiltersList();
		sharedFiltersList = new CustomFiltersList();
		activeFiltersList = new CustomFiltersList();
		customFiltersPane = new CustomFiltersPane();
		crsList = new CrItemsList();
		user = XmlMngr.getUserValueOf(new String[] {"option_pane", "uname"});
		
		// User setup
		if (user.equals("user") || user.equals("null") || user.equals(""))
		{
			user = JOptionPane.showInputDialog(satFrame, "Before start using SAT, please, type your coreid");
			pass = JOptionPane.showInputDialog(satFrame, "Now, configure your password");
			SharedObjs.setUser(user);
		}
		
		// Create Panes
		parserPane = new ParserPane();
		parserModel = new ParserModel();
		parserController = new ParserController();
		parserController.startController(parserPane, parserModel);
		
		crsManagerPane = new CrsManagerPane();
		crsManagerModel = new CrsManagerModel();
		crsManagerController = new CrsManagerController();
		crsManagerController.startController(crsManagerPane, crsManagerModel);
		
		optionsPane = new OptionsPane();
		optionsModel = new OptionsModel();
		optionsController = new OptionsController();
		optionsController.startController(optionsPane, optionsModel);
		
		advOptions = new AdvancedOptionsPane();
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
			}
		});
		
		// Start SAT UI
		satFrame = new SAT2();
		
		// Inserting tabs
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=3 marginwidth=15 marginheight=5>Parser</body></html>", parserPane);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=3 marginwidth=15 marginheight=5>Downloader</body></html>", crsManagerPane);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=3 marginwidth=15 marginheight=5>Options</body></html>", optionsPane);
		
		// Load filters and update tree
		loadFilters();
		parserPane.getFiltersTree().updateFiltersTree();
		optionsPane.setServerStatus(dbStatus);
		
		//
		closedList = new ParallelTextPopup();
		openedList = new ParallelTextPopup();
		openedList.setTitle("Not closed CRs");
		openedList.setLocation(900, 250);
		closedList.setLocation(600, 250);
	}
	
	/**
	 * Check if all folders exists. If any of them does not exist, create it.
	 */
	public void checkFolder()
	{
		// TODO
	}
	
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Filters methods ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private static void loadFilters()
	{
		Logger.log(Logger.TAG_SHAREDOBJS, "Loading filters");
		checkMyFilters();
		checkSharedFilters();
		checkActiveSharedFilters();
		Logger.log(Logger.TAG_SHAREDOBJS, "Filters loaded");
	}
	
	private static void checkMyFilters()
	{
		boolean synced = false;
		
		if (satDB != null)
		{
			Logger.log(Logger.TAG_SHAREDOBJS, "Loading user filters from DB ...");
			CustomFiltersList dbFilters = satDB.myFilters();
			Logger.log(Logger.TAG_SHAREDOBJS, "Loading user filters from XML ...");
			CustomFiltersList xmlFilters = XmlMngr.getAllMyFilters();
			
			Logger.log(Logger.TAG_SHAREDOBJS, "Verifying filters consistency between local files and remote DB ...");
			
			if (dbFilters.size() != xmlFilters.size())
			{
				Logger.log(Logger.TAG_SHAREDOBJS, "Inconsistencies found ...");
				syncMyFilters(dbFilters, xmlFilters);
				synced = true;
			}
			else
			{
				boolean hasItem = true;
				
				// System.out.println("List of XML Filters: \n" + xmlFilters); System.out.println("List of DB Filters: \n" + dbFilters);
				
				for (CustomFilterItem filter : dbFilters)
				{
					if (xmlFilters.indexOf(filter) < 0)
					{
						hasItem = false;
						break;
					}
				}
				
				for (CustomFilterItem filter : xmlFilters)
				{
					if (dbFilters.indexOf(filter) < 0)
					{
						hasItem = false;
						break;
					}
				}
				
				if (hasItem == false)
				{
					Logger.log(Logger.TAG_SHAREDOBJS, "Inconsistencies found ...");
					syncMyFilters(dbFilters, xmlFilters);
					synced = true;
				}
			}
			
			if (synced == false)
			{
				Logger.log(Logger.TAG_SHAREDOBJS, "Inconsistencies not found ...");
				userFiltersList = satDB.myFilters();
			}
		}
		else
		{
			Logger.log(Logger.TAG_SHAREDOBJS, "Could not connect to SQL DB. Loading user filters from XML.");
			
			JOptionPane.showMessageDialog(satFrame, "Could not connect to SAT DB.\nClick ok to keep using SAT anyway.\n"
			                                        + "You will be able to sync your data next time you use SAT connected to DB.");
			
			userFiltersList = XmlMngr.getAllMyFilters();
		}
		
		Logger.log(Logger.TAG_SHAREDOBJS, "User filters loaded: " + userFiltersList.size());
	}
	
	private static void syncMyFilters(CustomFiltersList dbFilters, CustomFiltersList xmlFilters)
	{
		Logger.log(Logger.TAG_SHAREDOBJS, "Syncing filters between Cloud and XML");
		
		int ans = JOptionPane.showOptionDialog(SharedObjs.satFrame, "We noticed differences between your\n" + "local and your cloud filters file.\n"
		                                                            + "\n    - Your filters in DB: " + dbFilters.size() + "\n    - Your filters in XML: "
		                                                            + xmlFilters.size() + "\n\nWhat do you prefer to do?", "Filters files conflict",
		                                       JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
		                                       new String[] {"Use local file", "Use cloud file"}, "Use cloud file");
		
		Logger.log(Logger.TAG_SHAREDOBJS, "Option selected: " + ans);
		
		if (ans == 0)
		{
			Logger.log(Logger.TAG_SHAREDOBJS, "Syncing with local xml file");
			
			dbFilters = xmlFilters;
			satDB.deleteAllMyFilters();
			satDB.insertFilters(dbFilters);
			
			userFiltersList = satDB.myFilters();
			
			Logger.log(Logger.TAG_SHAREDOBJS, "Syncing done\nYour filters in DB: " + dbFilters.size() + "\nYour filters in XML: " + xmlFilters.size());
		}
		else if (ans == 1)
		{
			Logger.log(Logger.TAG_SHAREDOBJS, "Syncing with cloud data");
			
			xmlFilters = dbFilters;
			XmlMngr.removeAllMyFilters();
			XmlMngr.addMyFilters(dbFilters);
			
			userFiltersList = dbFilters;
			
			Logger.log(Logger.TAG_SHAREDOBJS, "Syncing done\nYour filters in DB: " + dbFilters.size() + "\nYour filters in XML: " + xmlFilters.size());
		}
		
		Logger.log(Logger.TAG_SHAREDOBJS, "User filters loaded: " + userFiltersList.size());
	}
	
	public static void checkSharedFilters()
	{
		if (satDB != null)
		{
			Logger.log(Logger.TAG_SHAREDOBJS, "Loading shared filters from SQL DB");
			
			sharedFiltersList.addAll(satDB.sharedFilters());
			XmlMngr.removeAllSharedFilters();
			XmlMngr.addSharedFilters(sharedFiltersList);
		}
		else
		{
			Logger.log(Logger.TAG_SHAREDOBJS, "Loading shared filters from XML");
			
			sharedFiltersList.addAll(XmlMngr.getAllSharedFilters());
		}
		
		Logger.log(Logger.TAG_SHAREDOBJS, "Shared filters loaded: " + sharedFiltersList.size());
	}
	
	public static void checkActiveSharedFilters()
	{
		if (satDB != null)
		{
			Logger.log(Logger.TAG_SHAREDOBJS, "Loading active shared filters from SQL DB");
			
			activeFiltersList.addAll(satDB.activeFilters());
			
			// If filter comes from DB, it does not have "active" field info, so we need to force it true here
			for (CustomFilterItem filter : activeFiltersList)
			{
				if (userFiltersList.indexOf(filter.getId()) >= 0)
				{
					userFiltersList.get(userFiltersList.indexOf(filter.getId())).setActive(true);
				}
				
				if (sharedFiltersList.indexOf(filter.getLastUpdate()) >= 0)
				{
					sharedFiltersList.get(sharedFiltersList.indexOf(filter.getLastUpdate())).setActive(true);
				}
			}
			
			XmlMngr.removeAllActiveFilters();
			XmlMngr.addActiveFilters(activeFiltersList);
		}
		else
		{
			Logger.log(Logger.TAG_SHAREDOBJS, "Could not connect to SAT DB. Loading active shared filters from XML");
			
			activeFiltersList.addAll(XmlMngr.getAllActiveFilters());
		}
		
		Logger.log(Logger.TAG_SHAREDOBJS, "Active filters loaded: " + activeFiltersList.size());
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Supportive Methods -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static void copyScript(File source, File dest) throws IOException
	{
		FileUtils.copyFile(source, dest);
	}
	
	public static void runScript(String folder) throws IOException
	{
		Logger.log(Logger.TAG_CRSMANAGER, "Generating report output for " + folder);
		
		// File seek and load configuration
		File f = new File(folder);
		File[] filesList = f.listFiles();
		String reportFile = null, sCurrentLine;
		String bugreport = null;
		
		addLogLine("Generating bugreport for " + f.getName() + " ...");
		
		// Look for the file
		for (int j = 0; j < filesList.length; j++)
		{
			if (filesList[j].isFile())
			{
				String files = filesList[j].getName();
				if (files.toLowerCase().endsWith(".txt") && files.toLowerCase().contains("report_info"))
				{
					reportFile = folder + "\\" + files;
					break;
				}
			}
		}
		
		// Try to open file
		BufferedReader br = null;
		
		if (reportFile == null)
		{
			Logger.log(Logger.TAG_CRSMANAGER, "Log not found: " + reportFile);
			Logger.log(Logger.TAG_CRSMANAGER, "Not possible to find product ");
			JOptionPane.showMessageDialog(null, "Could not find product ! Report output not being generated for this CR");
			return;
		}
		else
		{
			br = new BufferedReader(new FileReader(reportFile));
		}
		
		// Parse file
		boolean parsed = false;
		String bpVersion = "";
		
		while ((sCurrentLine = br.readLine()) != null)
		{
			sCurrentLine = sCurrentLine.toLowerCase();
			if (sCurrentLine.contains("bpversion"))
			{
				Logger.log(Logger.TAG_CRSMANAGER, "--- Initial line: " + sCurrentLine);
				Matcher m = Pattern.compile(".*bpversion\": \".+ (.+)\".*").matcher(sCurrentLine);
				if (m.matches())
				{
					bpVersion = m.group(1);
					Logger.log(Logger.TAG_CRSMANAGER, "bpVersion: " + bpVersion);
					bpVersion = bpVersion.substring(0, bpVersion.indexOf("_"));
				}
			}
			else if (sCurrentLine.contains("product"))
			{
				Logger.log(Logger.TAG_CRSMANAGER, "--- Initial line: " + sCurrentLine);
				sCurrentLine = sCurrentLine.replace("\"product\": \"", "").replace(" ", "");
				
				// BATTRIAGE-212
				if (sCurrentLine.indexOf("_") >= 0)
				{
					sCurrentLine = sCurrentLine.substring(0, sCurrentLine.indexOf("_"));
				}
				else if (sCurrentLine.indexOf("\"") >= 0)
				{
					sCurrentLine = sCurrentLine.substring(0, sCurrentLine.indexOf("\""));
				}
				Logger.log(Logger.TAG_CRSMANAGER, sCurrentLine);
				
				if (sCurrentLine.equals("griffin") || sCurrentLine.equals("unknown"))
				{
					sCurrentLine = bpVersion;
				}
				
				Logger.log(Logger.TAG_CRSMANAGER, "Product name detected: " + sCurrentLine);
				
				SharedObjs.copyScript(new File("Data\\scripts\\_Base.pl"), new File(folder + "\\build_report.pl"));
				
				// Configure build report battery capacity
				PrintWriter out = null;
				try
				{
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(new File(folder + "\\build_report.pl"));
					String content = scanner.useDelimiter("\\Z").next();
					scanner.close();
					
					// Get/Set battery capacity
					if (SharedObjs.advOptions.getBatCapValue(sCurrentLine) != null)
					{
						content = content.replace("#bat_cap#", SharedObjs.advOptions.getBatCapValue(sCurrentLine));
					}
					else
					{
						String pName = JOptionPane.showInputDialog("Type the product name", sCurrentLine);
						String bCap = JOptionPane.showInputDialog("Type the battery capacity");
						SharedObjs.advOptions.addNewBatCapValue(pName, bCap);
						content = content.replace("#bat_cap#", bCap);
					}
					
					out = new PrintWriter(folder + "\\build_report.pl");
					out.println(content);
					parsed = true;
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				finally
				{
					out.close();
				}
				
				break;
			}
		}
		
		if (!parsed)
		{
			PrintWriter out = null;
			try
			{
				Logger.log(Logger.TAG_CRSMANAGER, "Could not find product  or product battery capacity. Using 3000 as bat cap");
				JOptionPane.showMessageDialog(null, "Could not find product  or product battery capacity.\nUsing 3000 as battery capacity");
				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(new File(folder + "\\build_report.pl"));
				String content = scanner.useDelimiter("\\Z").next();
				content = content.replace("#bat_cap#", "3000");
				out = new PrintWriter(folder + "\\build_report.pl");
				out.println(content);
				out.close();
				parsed = true;
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (out != null)
					out.close();
			}
		}
		
		if (br != null)
			br.close();
		
		for (File file : filesList)
		{
			if (file.getName().contains("bugreport"))
			{
				bugreport = file.getName();
			}
		}
		
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd \"" + folder + "\" && build_report.pl " + bugreport + " > report-output.txt");
		Logger.log(Logger.TAG_CRSMANAGER, "Report Output file: " + bugreport);
		
		for (String c : builder.command())
		{
			Logger.log(Logger.TAG_CRSMANAGER, "Commands: " + c);
		}
		
		builder.redirectErrorStream(true);
		Process p = builder.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";
		String output = ""; // workaround for report outout 0kb
		
		while (true)
		{
			line = r.readLine();
			
			if (line == null)
			{
				break;
			}
			
			output += line + "\n";
			
			Logger.log(Logger.TAG_CRSMANAGER, line);
		}
		
		r.close();
		
		if (new File(folder + "/report-output.txt").length() < 10)
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder + "/report-output.txt")));
			bw.write(output);
			bw.close();
		}
		
		addLogLine("Report output generated for " + f.getName());
	}
	
	public static void addLogLine(String line)
	{
		if (crsManagerPane.getTextLog().split("\n").length > 150)
		{
			try
			{
				File f = new File("Data\\logs\\log_" + new Timestamp(System.currentTimeMillis()).toString().replace(":", "_") + ".txt");
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.write(crsManagerPane.getTextLog());
				bw.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			crsManagerPane.setTextLog("");
		}
		
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		// format.setTimeZone(TimeZone.getTimeZone("Brazil/East"));
		
		crsManagerPane.setTextLog(crsManagerPane.getTextLog() + format.format(date) + "\t" + line + "\n");
		crsManagerPane.setTextLogCarretPosition(crsManagerPane.getTextLog().length());
	}
	
	public static void acquireSemaphore() throws InterruptedException
	{
		unzipSemaphore.acquire();
	}
	
	public static void releaseSemaphore() throws InterruptedException
	{
		unzipSemaphore.release();
	}
	
	public static ParallelTextPopup getClosedList()
	{
		return closedList;
	}
	
	public static ParallelTextPopup getOpenedList()
	{
		return openedList;
	}
	
	public static void clearCRsListPanes()
	{
		closedList = new ParallelTextPopup();
		closedList.setLocation(600, 250);
		
		openedList = new ParallelTextPopup();
		openedList.setTitle("Not closed CRs");
		openedList.setLocation(900, 250);
	}
	
	public static void updateUidsXML()
	{
		if (isUidsDBModified)
		{
			Logger.log(Logger.TAG_SHAREDOBJS, "Updating XML UIDs File ...");
			
			HashMap<String, String> hm = satDB.getAllUids();
			for (String key : hm.keySet())
			{
				Logger.log(Logger.TAG_SHAREDOBJS, key + "\t-\t" + hm.get(key));
				XmlMngr.setUidsValueOf(new String[] {"Known", key}, hm.get(key));
			}
			
			Logger.log(Logger.TAG_SHAREDOBJS, "Update completed");
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Getters ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static String getUser()
	{
		return user;
	}
	
	public static String getPass()
	{
		return pass;
	}
	
	public static String getCrPath()
	{
		return crPath;
	}
	
	public static String getResult()
	{
		return actualParserResultText;
	}
	
	public static String getDownloadPath()
	{
		return downloadPath;
	}
	
	public static CustomFiltersList getUserFiltersList()
	{
		return userFiltersList;
	}
	
	public static CustomFiltersList getSharedFiltersList()
	{
		return sharedFiltersList;
	}
	
	public static CustomFiltersList getActiveFiltersList()
	{
		return activeFiltersList;
	}
	
	public static CustomFiltersPane getCustomFiltersPane()
	{
		return customFiltersPane;
	}
	
	public static CrItemsList getCrsList()
	{
		return crsList;
	}
	
	public static CrItem getCrByJira(String jiraID)
	{
		for (CrItem aux : crsList)
		{
			if (aux.getJiraID().equals(jiraID))
			{
				return aux;
			}
		}
		return null;
	}
	
	public static CrItem getCrByB2g(String b2gID)
	{
		for (CrItem aux : crsList)
		{
			if (aux.getJiraID().equals(b2gID))
			{
				return aux;
			}
		}
		return null;
	}
	
	public static String getRootFolderPath()
	{
		return rootFolderPath;
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Setters ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public static void setUser(String newuser)
	{
		user = newuser;
	}
	
	public static void setPass(String pass)
	{
		SharedObjs.pass = pass;
	}
	
	public static void setResult(String result)
	{
		SharedObjs.actualParserResultText = result;
	}
	
	public static void setCrPath(String crPath)
	{
		SharedObjs.crPath = crPath;
	}
	
	public static void setCrsList(CrItemsList crsList)
	{
		SharedObjs.crsList = crsList;
	}
	
	public static void addCrToList(CrItem cr)
	{
		crsList.add(cr);
	}
	
	public void setCustomFiltersList(CustomFiltersList customFiltersList)
	{
		SharedObjs.userFiltersList = customFiltersList;
	}
	
	public static void setSharedFiltersList(CustomFiltersList sharedFiltersList)
	{
		SharedObjs.sharedFiltersList = sharedFiltersList;
	}
	
	public static void setActiveFiltersList(CustomFiltersList activeFiltersList)
	{
		SharedObjs.activeFiltersList = activeFiltersList;
	}
	
	public void setCustomFiltersPane(CustomFiltersPane customFiltersPane)
	{
		SharedObjs.customFiltersPane = customFiltersPane;
	}
	
	public static void setRootFolderPath(String rootFolderPath)
	{
		SharedObjs.rootFolderPath = rootFolderPath;
	}
	
	public static void setDownloadPath(String downloadPath)
	{
		SharedObjs.downloadPath = downloadPath;
	}
}
