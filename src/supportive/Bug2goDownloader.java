package supportive;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;

import core.Logger;
import core.SharedObjs;
import customobjects.Bug2goItem;
import customobjects.CrItem;


/**
 * This class monitor the files being downloaded.
 */
public class Bug2goDownloader implements Runnable
{
	/**
	 * Variables
	 */
	private static final String   BASE_LOGIN_LINK = "https://b2gadm-mcloud101-blur.svcmot.com/bugreport/report/verify.action";
	private static final String   LOGIN_PARAM     = "username=COREID&password=PASSWRD";
	private HttpURLConnection     connection;
	private OutputStream          out;
	private ArrayList<Bug2goItem> bug2goListSubmitted;
	private ArrayList<Bug2goItem> bug2goListInProgress;
	private ArrayList<Bug2goItem> bug2goListDone;
	private ArrayList<Bug2goItem> bug2goListFailed;
	private ArrayList<Bug2goItem> bug2goListRetry;
	private int                   errors;
	private Semaphore             semaphore;
	private ExecutorService       executor;
	
	/**
	 * Initialize class variables. The constructor is private in order to implement the Singleton design pattern
	 */
	private Bug2goDownloader()
	{
		semaphore = new Semaphore(1);
		bug2goListSubmitted = new ArrayList<Bug2goItem>();
		bug2goListInProgress = new ArrayList<Bug2goItem>();
		bug2goListDone = new ArrayList<Bug2goItem>();
		bug2goListFailed = new ArrayList<Bug2goItem>();
		bug2goListRetry = new ArrayList<Bug2goItem>();
		errors = 0;
		
		// Used to avoid the error: Security: Server SSL Error-handshake alert:unrecognized_name
		
		// You may get this SSL error if the server you are trying to access has not been properly configured.
		// For security reasons SNI extension has been enabled by default in Java 7. However, if you trust the server you are trying to
		// connect you may want to disable SNI extension.
		// Reference: http://forums.visokio.com/discussion/2614/security-server-ssl-error-handshake-alertunrecognized_name
		System.setProperty("jsse.enableSNIExtension", "false");
		
		// Used to maintain the session
		
		// CookieManager provides a concrete implementation of CookieHandler, which separates the storage of cookies from the policy
		// surrounding accepting and rejecting cookies.
		CookieManager cookieManager = new CookieManager();
		// CookieHandler is at the core of cookie management. User can call CookieHandler.setDefault to set a concrete CookieHanlder
		// implementation to be used.
		CookieHandler.setDefault(cookieManager);
		
	}
	
	/**
	 * Private inner static class
	 */
	private static class Bug2goDownloaderHolder
	{
		private static final Bug2goDownloader INSTANCE = new Bug2goDownloader();
	}
	
	/**
	 * Returns the unique instance of Bug2goDownloader
	 */
	public static Bug2goDownloader getInstance()
	{
		return Bug2goDownloaderHolder.INSTANCE;
	}
	
	/**
	 * Add Bug2go itens in the list to be downloaded
	 */
	public boolean addBugId(String[] bugIdList) throws InterruptedException
	{
		semaphore.acquire();
		for (String s : bugIdList)
		{
			if (!bug2goListSubmitted.add(new Bug2goItem(s)))
			{
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Failed to add bugID " + s + " on BugIdList.");
			}
		}
		semaphore.release();
		return true;
	}
	
	/**
	 * Add Bug2go itens in the list to be downloaded
	 */
	public boolean addBugIdList(ArrayList<String> bugIdList) throws InterruptedException
	{
		semaphore.acquire();
		for (String s : bugIdList)
		{
			if (!bug2goListSubmitted.add(new Bug2goItem(s)))
			{
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Failed to add bugID " + s + " on BugIdList.");
			}
		}
		semaphore.release();
		return true;
	}
	
	/**
	 * Remove a Bug2go item from one of the lists
	 */
	public boolean removeBugItem(Bug2goItem item)
	{
		boolean removed;
		
		removed = bug2goListInProgress.remove(item);
		if (removed)
		{
			if (item.getStatus() == Bug2goItem.DownloadStatus.DONE)
			{
				bug2goListDone.add(item);
			}
			
			else if (item.getStatus() == Bug2goItem.DownloadStatus.FAILED)
			{
				if (item.getRetry() > 0 && item.getRetry() < 5)
				{
					// Setting overwrite to true once the file may be incomplete. This item will be retried
					item.setOverwrite(true);
					bug2goListRetry.add(item);
				}
				else if (item.getRetry() == 5)
				{
					// No more retries. Delete the incomplete file if it exists.
					deleteIncompleteFile(item);
					bug2goListFailed.add(item);
				}
				// else if (item.getRetry() == 0)
				// {
				// Retry = 0 means that file was already there
				// bug2goListFailed.add(item);
				// }
			}
		}
		
		Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Was the item removed? " + removed);
		return removed;
	}
	
	/**
	 * Initiate the download execution
	 */
	public void execute()
	{
		if (executor == null)
		{
			new Thread(this).start();
		}
		else if (executor.isTerminated())
		{
			bug2goListInProgress.clear();
			bug2goListDone.clear();
			bug2goListFailed.clear();
			bug2goListRetry.clear();
			new Thread(this).start();
		}
	}
	
	/**
	 * Try to login
	 */
	private boolean login() throws IOException
	{
		String login;
		
		// Create the login string
		login = LOGIN_PARAM.replace("COREID", SharedObjs.getUser());
		login = login.replace("PASSWRD", SharedObjs.getPass());
		
		// Open the connection with request method = POST
		URL url = new URL(BASE_LOGIN_LINK);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		
		// For POST only - START
		// Use the URL connection for output
		connection.setDoOutput(true);
		out = connection.getOutputStream();
		out.write(login.getBytes());
		out.flush();
		out.close();
		// For POST only - END
		
		int responseCode = connection.getResponseCode();
		Logger.log(Logger.TAG_BUG2GODOWNLOADER, "POST Response Code :: " + responseCode);
		
		if (responseCode == HttpURLConnection.HTTP_OK)
		{
			// If URL equals to https://b2gadm-mcloud101-blur.svcmot.com/bugreport/report/verify.action, it means that login failed.
			if (connection.getURL().toString().equals(BASE_LOGIN_LINK))
			{
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Login Failed!");
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "URL after login POST: " + connection.getURL());
				return false;
			}
		}
		else
		{
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "POST request did not work");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Delete incomplete files that may exist due to failed downloads
	 */
	public void deleteIncompleteFile(Bug2goItem bug)
	{
		// Get download folder
		File downloadFolder = new File(SharedObjs.getDownloadPath());
		
		for (File f : downloadFolder.listFiles())
		{
			if (f.getName().contains(bug.getBugId()))
			{
				Logger.log(Logger.TAG_BUG2GOITEM, "Deleting incomplete file: " + bug.getBugId());
				f.delete();
			}
		}
	}
	
	/**
	 * Monitor the downloads
	 */
	@Override
	public void run()
	{
		// Try to login
		try
		{
			if (!login())
			{
				SharedObjs.addLogLine("Bug2Go login failed");
				return;
			}
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Exception trying to login.");
			return;
		}
		
		// Create a thread pool of size 5
		executor = Executors.newFixedThreadPool(5);
		SharedObjs.addLogLine("Downloading b2g files ...");
		
		// While any of the lists still have an item
		while (!bug2goListSubmitted.isEmpty() || !bug2goListInProgress.isEmpty() || !bug2goListRetry.isEmpty())
		{
			// If there are items to be submitted or retried, start all of them
			if (!bug2goListSubmitted.isEmpty() || !bug2goListRetry.isEmpty())
			{
				try
				{
					semaphore.acquire();
					for (Bug2goItem b : bug2goListSubmitted)
					{
						executor.execute(b);
						bug2goListInProgress.add(b);
					}
					
					bug2goListSubmitted.clear();
					
					for (Bug2goItem b : bug2goListRetry)
					{
						Logger.log(Logger.TAG_BUG2GODOWNLOADER, b.getBugId() + ": retry value: " + b.getRetry());
						bug2goListSubmitted.add(b);
					}
					
					bug2goListRetry.clear();
					semaphore.release();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			// Check the status of the items in progress. For debug only.
			try
			{
				semaphore.acquire();
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Checking...");
				
				for (Bug2goItem b : bug2goListInProgress)
				{
					Logger.log(Logger.TAG_BUG2GODOWNLOADER, b.getBugId() + ": status > " + b.getStatus() + " | size > " + b.getSizeOfFile()
					                                        + " | downloaded > " + b.getDownloadProgress() + " | running > " + b.isRunning());
				}
				semaphore.release();
				Thread.sleep(3000);
			}
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
		}
		
		// Shutdown the executor. No thread can be added after this point
		executor.shutdown();
		
		// The following IFs are for debug only
		if (bug2goListInProgress.isEmpty())
		{
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "bug2goList In Progress is Empty.");
		}
		else
		{
			for (Bug2goItem i : bug2goListInProgress)
			{
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "bug2goList In Progress: " + i.getBugId());
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Retry: " + i.getRetry());
			}
		}
		
		if (bug2goListDone.isEmpty())
		{
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "bug2goList Done is Empty.");
		}
		else
		{
			for (Bug2goItem i : bug2goListDone)
			{
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "bug2goList Done: " + i.getBugId());
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Retry: " + i.getRetry());
			}
		}
		
		if (bug2goListFailed.isEmpty())
		{
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "bug2goList Failed is Empty.");
		}
		else
		{
			errors = 1;
			for (Bug2goItem i : bug2goListFailed)
			{
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "bug2goList Failed: " + i.getBugId());
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Retry: " + i.getRetry());
			}
		}
		
		if (bug2goListRetry.isEmpty())
		{
			Logger.log(Logger.TAG_BUG2GODOWNLOADER, "bug2goList Retry is Empty.");
		}
		else
		{
			for (Bug2goItem i : bug2goListRetry)
			{
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "bug2goList Retry: " + i.getBugId());
				Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Retry: " + i.getRetry());
			}
		}
		
		Logger.log(Logger.TAG_BUG2GODOWNLOADER, "Downloads finished");
		SharedObjs.addLogLine("Downloads finished");
		
		// If there was any error
		if (errors == 1)
			JOptionPane.showMessageDialog(SharedObjs.satFrame, "There were errors during the download. \nFiles may be missing or corrupted.");
		
		// Ask if user wants to unzip them all
		if (SharedObjs.crsManagerPane.isChckbxUnzipSelected())
		{
			File[] filesName = new File(SharedObjs.getDownloadPath()).listFiles();
			
			for (File file : filesName)
			{
				if (SharedObjs.crsManagerPane.isChckbxUnzipSelected() && file.isFile() && file.getName().contains(".zip")
				    && file.getName().contains("_B2G_") && b2gdoneListContains(file.getName().substring(0, file.getName().indexOf("_"))))
				{
					String b2gID = file.getName().substring(0, file.getName().indexOf("_")); //Pegar b2gID pelo nome do arquivo;
					
					// Unzip
					SharedObjs.addLogLine("Unzipping " + b2gID + " ...");
					
					UnZip.unZipIt(file.getAbsolutePath(), file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 28));
					
					file = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 28));
					
					SharedObjs.addLogLine("Done");
					
					// Analyze
					if (SharedObjs.crsManagerPane.isChckbxAnalyzeSelected())
					{
						CrItem cr = SharedObjs.crsManagerModel.getIgnoredList().getCrByB2gId(b2gID);
						
						// Check if should be ignored
						if(cr != null)
						{
							SharedObjs.addLogLine("Ignoring analysis process for " + b2gID);
						}
						else
						{
							SharedObjs.addLogLine("Pre analyzing CR ...");
							
							CrChecker crChecker = new CrChecker(file.getAbsolutePath());
							
							if (crChecker.checkCR())
							{
								SharedObjs.getCrsList().getCrByB2gId(file.getName()).setStatus("Closed");
								SharedObjs.addLogLine("CR closed as " + SharedObjs.getCrsList().getCrByB2gId(file.getName()).getResolution());
							}
							
							if (!crChecker.getIncompleteFiles().contains("bugreport"))
							{
								try
								// BATTRIAGE-175
								{
									SharedObjs.runScript(file.getAbsolutePath());
								}
								catch (IOException e)
								{
									e.printStackTrace();
									Logger.log(Logger.TAG_BUG2GODOWNLOADER, e.getMessage());
								}
							}
							else
							{
								SharedObjs.addLogLine("No bugreport file. Report output not generated.");
								Logger.log(Logger.TAG_BUG2GODOWNLOADER, "No bugreport file. Report output not generated.");
							}
						}
					}
				}
			}
			
			if (SharedObjs.crsManagerPane.isChckbxAnalyzeSelected())
			{
				SharedObjs.clearCRsListPanes();
				SharedObjs.getClosedList().clear();
				SharedObjs.getOpenedList().clear();
				
				for (CrItem cr : SharedObjs.getCrsList())
				{
					if (cr.getStatus().equals("Closed") && !cr.getResolution().equals(""))
					{
						SharedObjs.getClosedList().addItemList1(cr.getJiraID());
						SharedObjs.getClosedList().addItemList2(cr.getResolution());
					}
					else
					{
						SharedObjs.getOpenedList().addItemList1(cr.getJiraID());
					}
				}
				
				if (SharedObjs.getClosedList() == null || SharedObjs.getOpenedList() == null)
				{
					JOptionPane.showMessageDialog(SharedObjs.crsManagerPane, "Error: The lists does not exist");
				}
				
				SharedObjs.getClosedList().setVisible(true);
				SharedObjs.getOpenedList().setVisible(true);
			}
			
			SharedObjs.addLogLine("All done!");
		}
		
		SharedObjs.crsManagerController.enableViewOptionsAndBtns();
	}
	
	/**
	 * Others
	 */
	public boolean b2gdoneListContains(String b2gID)
	{
		for (Bug2goItem i : bug2goListDone)
		{
			if (i.getBugId().equals(b2gID))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Getters and Setters
	 */
	public Semaphore getSemaphore()
	{
		return semaphore;
	}
	
	public void setSemaphore(Semaphore semaphore)
	{
		this.semaphore = semaphore;
	}
	
	public void setError(int value)
	{
		errors = value;
	}
	
	public ExecutorService getExecutor()
	{
		return executor;
	}
}
