package core;


/**
 * This class is a keeper of all strings in the tool.
 */
public class Strings
{
	/**
	 * System String Variables
	 */
	private static String       toolName    = getSystemString("tool_name");
	private static String       toolVersion = getSystemString("tool_version");
	private static String       toolFile    = getSystemString("tool_file");
	private static String       updaterFile = getSystemString("updater");
	private static final String DATA_FOLDER = "Data/";
	private static String       picsFolder  = DATA_FOLDER + getSystemString("pics_folder");
	private static String       logsFolder  = DATA_FOLDER + getSystemString("logs_folder");
	
	/**
	 * Message Strings Variables
	 */
	private static String       newVersion  = getMessageString("new_version");
	
	/**
	 * Get Strings located in messages_cfg.xml
	 * 
	 * @param name
	 * @return
	 */
	public static String getMessageString(String name)
	{
		String value = XmlMngr.getMessageValueOf(new String[] {"messages", name});
		return value;
	}
	
	/**
	 * Get Strings located in system_cfg.xml file
	 * 
	 * @param name
	 * @return
	 */
	public static String getSystemString(String name)
	{
		String value = XmlMngr.getSystemValueOf(new String[] {"configs", name});
		return value;
	}
	
	// Getters and Setters
	public static String getToolName()
	{
		return toolName;
	}
	
	public static String getToolVersion()
	{
		return toolVersion;
	}
	
	public static String getToolFileName()
	{
		return toolFile;
	}
	
	public static String getUpdaterFileName()
	{
		return updaterFile;
	}
	
	public static String getPicsFolder()
	{
		return picsFolder;
	}
	
	public static String getLogsFolder()
	{
		return logsFolder;
	}
	
	public static String getNewVersion()
	{
		return newVersion;
	}
	
	public static void setToolName(String toolName)
	{
		Strings.toolName = toolName;
	}
	
	public static void setToolVersion(String toolVersion)
	{
		Strings.toolVersion = toolVersion;
	}
	
	public static void setToolFileName(String toolFile)
	{
		Strings.toolFile = toolFile;
	}
	
	public static void setUpdaterFileName(String updaterFile)
	{
		Strings.updaterFile = updaterFile;
	}
	
	public static void setPicsFolder(String picsFolder)
	{
		Strings.picsFolder = picsFolder;
	}
	
	public static void setLogsFolder(String logsFolder)
	{
		Strings.logsFolder = logsFolder;
	}
	
	public static void setNewVersion(String newVersion)
	{
		Strings.newVersion = newVersion;
	}
}
