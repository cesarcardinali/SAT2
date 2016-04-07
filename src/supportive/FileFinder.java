package supportive;


import java.io.File;


public class FileFinder
{
	String                     path;
	Boolean                    found;
	
	public static final String MAIN          = "main";
	public static final String SYSTEM        = "system";
	public static final String KERNEL        = "kernel";
	public static final String RADIO         = "radio";
	public static final String EVENTS        = "events";
	public static final String BUGREPORT     = "bugreport";
	public static final String REPORT_OUTPUT = "report-output";
	public static final String BTD           = "bt";
	
	public FileFinder(String path)
	{
		this.path = path;
		found = false;
	}
	
	/**
	 * Search for the system log in the given path
	 * @return The file path if it is found "Not a directory" if the given path is not a directory "File not found" if the method could not
	 *         found the log file
	 */
	public String getSystemFilePath()
	{
		found = false;
		String file = "";
		
		File folder = new File(path); // CR folder
		
		// Check if is directory exists
		if (!folder.isDirectory())
		{
			return "Not a directory";
		}
		
		File[] listOfFiles = folder.listFiles(); // List of files inside CR folder
		
		// Search for system file path
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile() && (listOfFiles[i].getName().toLowerCase().endsWith(".txt") && listOfFiles[i].getName().contains("system")))
			{
				file = listOfFiles[i].getName();
				
				if (!path.equals("."))
					file = path + listOfFiles[i].getName();
				
				break;
			}
		}
		
		if (!file.equals(""))
		{
			found = true;
			return file;
		}
		
		return "File not found";
	}
	
	/**
	 * Search for the main log in the given path
	 * @return The file path if it is found "Not a directory" if the given path is not a directory "File not found" if the method could not
	 *         found the log file
	 */
	public String getMainFilePath()
	{
		found = false;
		String file = "";
		
		File folder = new File(path); // CR folder
		
		// Check if is directory exists
		if (!folder.isDirectory())
		{
			return "Not a directory";
		}
		
		File[] listOfFiles = folder.listFiles(); // List of files inside CR folder
		
		// Search for system file path
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
		
		if (!file.equals(""))
		{
			found = true;
			return file;
		}
		
		return "File not found";
	}
	
	/**
	 * Search for the kernel log in the given path
	 * @return The file path if it is found "Not a directory" if the given path is not a directory "File not found" if the method could not
	 *         found the log file
	 */
	public String getKernelFilePath()
	{
		found = false;
		String file = "";
		
		File folder = new File(path); // CR folder
		
		// Check if is directory exists
		if (!folder.isDirectory())
		{
			return "Not a directory";
		}
		
		File[] listOfFiles = folder.listFiles(); // List of files inside CR folder
		
		// Search for system file path
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile() && (listOfFiles[i].getName().toLowerCase().endsWith(".txt") && listOfFiles[i].getName().contains("kernel")))
			{
				file = listOfFiles[i].getName();
				
				if (!path.equals("."))
					file = path + listOfFiles[i].getName();
				
				break;
			}
		}
		
		if (!file.equals(""))
		{
			found = true;
			return file;
		}
		
		return "File not found";
	}
	
	/**
	 * Search for the radio log in the given path
	 * @return The file path if it is found "Not a directory" if the given path is not a directory "File not found" if the method could not
	 *         found the log file
	 */
	public String getRadioFilePath()
	{
		found = false;
		String file = "";
		
		File folder = new File(path); // CR folder
		
		// Check if is directory exists
		if (!folder.isDirectory())
		{
			return "Not a directory";
		}
		
		File[] listOfFiles = folder.listFiles(); // List of files inside CR folder
		
		// Search for system file path
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile() && (listOfFiles[i].getName().toLowerCase().endsWith(".txt") && listOfFiles[i].getName().contains("radio")))
			{
				file = listOfFiles[i].getName();
				
				if (!path.equals("."))
					file = path + listOfFiles[i].getName();
				
				break;
			}
		}
		
		if (!file.equals(""))
		{
			found = true;
			return file;
		}
		
		return "File not found";
	}
	
	/**
	 * Search for the events log in the given path
	 * @return The file path if it is found "Not a directory" if the given path is not a directory "File not found" if the method could not
	 *         found the log file
	 */
	public String getEventsFilePath()
	{
		found = false;
		String file = "";
		
		File folder = new File(path); // CR folder
		
		// Check if is directory exists
		if (!folder.isDirectory())
		{
			return "Not a directory";
		}
		
		File[] listOfFiles = folder.listFiles(); // List of files inside CR folder
		
		// Search for system file path
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile() && (listOfFiles[i].getName().toLowerCase().endsWith(".txt") && listOfFiles[i].getName().contains("event")))
			{
				file = listOfFiles[i].getName();
				
				if (!path.equals("."))
					file = path + listOfFiles[i].getName();
				
				break;
			}
		}
		
		if (!file.equals(""))
		{
			found = true;
			return file;
		}
		
		return "File not found";
	}
	
	/**
	 * Search for the bugreport log in the given path
	 * @return The file path if it is found "Not a directory" if the given path is not a directory "File not found" if the method could not
	 *         found the log file
	 */
	public String getBugreportFilePath()
	{
		found = false;
		String file = "";
		
		File folder = new File(path); // CR folder
		
		// Check if is directory exists
		if (!folder.isDirectory())
		{
			return "Not a directory";
		}
		
		File[] listOfFiles = folder.listFiles(); // List of files inside CR folder
		
		// Search for system file path
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile() && (listOfFiles[i].getName().toLowerCase().endsWith(".txt") && listOfFiles[i].getName().contains("bugreport")))
			{
				file = listOfFiles[i].getName();
				
				if (!path.equals("."))
					file = path + listOfFiles[i].getName();
				
				break;
			}
		}
		
		if (!file.equals(""))
		{
			found = true;
			return file;
		}
		
		return "File not found";
	}
	
	/**
	 * Search for the report-output log in the given path
	 * @return The file path if it is found "Not a directory" if the given path is not a directory "File not found" if the method could not
	 *         found the log file
	 */
	public String getReportOutputFilePath()
	{
		found = false;
		String file = "";
		
		File folder = new File(path); // CR folder
		
		// Check if is directory exists
		if (!folder.isDirectory())
		{
			return "Not a directory";
		}
		
		File[] listOfFiles = folder.listFiles(); // List of files inside CR folder
		
		// Search for system file path
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile() && (listOfFiles[i].getName().toLowerCase().endsWith(".txt") && listOfFiles[i].getName().contains("report-output")))
			{
				file = listOfFiles[i].getName();
				
				if (!path.equals("."))
					file = path + listOfFiles[i].getName();
				
				break;
			}
		}
		
		if (!file.equals(""))
		{
			found = true;
			return file;
		}
		
		return "File not found";
	}
	
	/**
	 * Search for the given log name in the given path
	 * @param logName<br>
	 *            Use the given Strings:<br>
	 *            FileFinder.SYSTEM<br>
	 *            FileFinder.MAIN<br>
	 *            FileFinder.KERNEL<br>
	 *            FileFinder.EVENTS<br>
	 *            FileFinder.RADIO<br>
	 *            FileFinder.BUGREPORT<br>
	 *            FileFinder.REPORTOUTPUT<br>
	 * @return The file path if it is found "Not a directory" if the given path is not a directory "File not found" if the method could not
	 *         found the log file
	 */
	public String getFilePath(String logName)
	{
		found = false;
		String file = "";
		
		File folder = new File(path); // CR folder
		
		// Check if is directory exists
		if (!folder.isDirectory())
		{
			return "Not a directory";
		}
		
		File[] listOfFiles = folder.listFiles(); // List of files inside CR folder
		
		// Search for system file path
		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile() && (listOfFiles[i].getName().toLowerCase().endsWith(".txt") || listOfFiles[i].getName().toLowerCase().endsWith(".btd"))
			    && listOfFiles[i].getName().toLowerCase().contains(logName))
			{
				file = listOfFiles[i].getName();
				
				if (!path.equals("."))
					file = path + listOfFiles[i].getName();
				
				break;
			}
		}
		
		if (!file.equals(""))
		{
			found = true;
			return file;
		}
		
		return "File not found";
	}
	
	// Getters and Setters
	public Boolean getFound()
	{
		return found;
	}
	
	public void setFound(Boolean found)
	{
		this.found = found;
	}
}
