package core;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


/*
 * This class is responsible for all Log features
 */
public class Logger
{
	/**
	 * Variables
	 */
	private static File           logFile;
	private static BufferedWriter logWriter;
	private static boolean        logCreated;
	private static boolean        debugMode;
	public static final String    TAG_SAT                = "SAT";
	public static final String    TAG_PARSER             = "PARSER";
	public static final String    TAG_CRSMANAGER         = "CRS MANAGER";
	public static final String    TAG_OPTIONS            = "OPTIONS";
	public static final String    TAG_FILETREE           = "FILE TREE";
	public static final String    TAG_FILTERSRESULTSTREE = "FILTER RESULTS TREE";
	public static final String    TAG_COLORPRINTER       = "COLOR PRINTER";
	public static final String    TAG_CRSCLOSER          = "CRS CLOSER";
	public static final String    TAG_DIAGCRSCLOSER      = "DIAG CRS CLOSER";
	public static final String    TAG_ALARM              = "ALARM";
	public static final String    TAG_B2G                = "BUG TO GO";
	public static final String    TAG_CONSUME            = "CONSUME";
	public static final String    TAG_DIAG               = "DIAG";
	public static final String    TAG_NORMAL             = "NORMAL";
	public static final String    TAG_SUSPICIOUS         = "SUSPICIOUS";
	public static final String    TAG_TETHER             = "TETHER";
	public static final String    TAG_ALARMITEM          = "ALARM ITEM";
	public static final String    TAG_UNZIP              = "UNZIP";
	public static final String    TAG_BUG2GOITEM         = "BUG2GO ITEM";
	public static final String    TAG_BUG2GODOWNLOADER   = "BUG2GO DOWNLOADER";
	public static final String    TAG_XMLMNGR            = "XML MANAGER";
	public static final String    TAG_SHAREDOBJS         = "SHARED OBJS";
	public static final String    TAG_GETBUG             = "GET BUG";
	public static final String    TAG_CUSTOM_FILTERS     = "CUSTOM FILTERS";
	public static final String    TAG_LOGGER             = "LOGGER";
	public static final String    TAG_CR_CHECKER         = "CR CHECKER";
	public static final String    TAG_BUGREPORT_PARSER   = "BUGREPORT PARSER";
	public static final String    TAG_MAINLOG_PARSER     = "MAINLOG PARSER";
	public static final String    TAG_BTD_PARSER         = "BTD PARSER";
	public static final String    TAG_FALSE_POSITIVE     = "FALSE_POSITIVE_CHECKER";
	
	/**
	 * Initialize class variables
	 */
	public static void initClass()
	{
		// Read debug mode on/off
		debugMode = Boolean.parseBoolean(XmlMngr.getSystemValueOf(new String[] {"configs", "debug_mode"}));
		
		if (debugMode)
		{
			// Generate log file
			Logger.log(Logger.TAG_LOGGER, "Checking if logs folder exists");
			
			if (new File(Strings.getLogsFolder()).exists())
			{
				logFile = new File(Strings.getLogsFolder() + "log_"
				                   + new Timestamp(System.currentTimeMillis()).toString().replace(":", "_")
				                   + ".log");
				Logger.log(Logger.TAG_LOGGER, "Logs folder exists");
			}
			else
			{
				new File(Strings.getLogsFolder()).mkdirs();
				logFile = new File(Strings.getLogsFolder() + "log_"
				                   + new Timestamp(System.currentTimeMillis()).toString().replace(":", "_")
				                   + ".log");
				Logger.log(Logger.TAG_LOGGER, "Logs folder created");
			}
			
			logCreated = true;
		}
		else
		{
			logCreated = false;
		}
	}
	
	/**
	 * Write a line in log file if log file exists.
	 * 
	 * @param tag String containing the tag for the logged text
	 * @param text String containing the text to be logged
	 */
	public static void log(String tag, String text)
	{
		if (logCreated)
		{
			try
			{
				openFile();
				String fTag = tag;
				
				for (int i = tag.length(); i < 20; i++)
				{
					fTag += " ";
				}
				
				logWriter.write(new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()) + "   " + fTag + "\t : "
				                + text + "\n");
				close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.out.println("Log file does not exist");
			}
		}
		
		System.out.println(text);
	}
	
	// Getters and Setters
	public boolean isLogCreated()
	{
		return logCreated;
	}
	
	/**
	 * Closes the log file.
	 */
	public static void close()
	{
		if (logCreated)
		{
			try
			{
				logWriter.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.out.println("Log file does not exist");
			}
		}
	}
	
	/**
	 * Opens log file.
	 */
	public static void openFile()
	{
		if (logCreated)
		{
			try
			{
				logWriter = new BufferedWriter(new FileWriter(logFile, true));
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.out.println("Log file does not exist");
			}
		}
	}
}
