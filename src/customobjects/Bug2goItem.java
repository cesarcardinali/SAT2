package customobjects;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import supportive.Bug2goDownloader;
import core.Logger;
import core.SharedObjs;


/**
 * This class downloads a B2G file.
 */
public class Bug2goItem implements Runnable
{
	/**
	 * Variables
	 */
	public static enum DownloadStatus
	{
		STOPPED, DOWNLOADING, DONE, FAILED
	};
	
	private static final String	BASE_DOWNLOAD_LINK = "https://b2gadm-mcloud101-blur.svcmot.com/bugreport/report/downloadlog.action";
	private static final String	BUG_ID_PARAM	   = "bg_id=BUGID";
	private HttpURLConnection	connection;
	private OutputStream		out;
	private String				bugId;
	private int					sizeOfFile;
	private int					downloadProgress;
	private int 				retry;
	private boolean				running;
	private DownloadStatus		status;
	private boolean				overwrite;
	
	/**
	 * Initialize class variables
	 */
	public Bug2goItem(String bugNumber)
	{
		bugId = bugNumber;
		running = false;
		downloadProgress = 0;
		retry = 0;
		status = DownloadStatus.STOPPED;
		overwrite = false;
	}
	
	/**
	 * Remove this current B2G item from one of the b2goLists kept by Bug2goDownloader
	 */
	public void removeFromList(Bug2goItem item)
	{
		Bug2goDownloader b2gDownloader = Bug2goDownloader.getInstance();
		
		try
		{
			// Acquire the semaphore first
			b2gDownloader.getSemaphore().acquire();
			
			// Remove itself from the list
			b2gDownloader.removeBugItem(this);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		b2gDownloader.getSemaphore().release();
	}
	
	/**
	 * Start to download the B2G log
	 */
	@Override
	public void run()
	{
		URL urlDownload;
		String bugNumber;
		FileOutputStream file = null;
		int responseCode = -1;
		
		running = true;
		status = DownloadStatus.DOWNLOADING;
		bugNumber = BUG_ID_PARAM.replace("BUGID", bugId);

		// Open connection to download the CR
		try
		{
			urlDownload = new URL(BASE_DOWNLOAD_LINK);
			
			connection = (HttpURLConnection) urlDownload.openConnection();
			
			// Sets connect timeout and read timeout to 30s
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			
			// For POST only - START
			// Use the URL connection for output
			connection.setDoOutput(true);
			out = connection.getOutputStream();
			out.write(bugNumber.getBytes());
			out.flush();
			out.close();
			// For POST only - END
			
			responseCode = connection.getResponseCode();
			
			Logger.log(Logger.TAG_BUG2GOITEM, "POST Response Code :: " + responseCode);
			Logger.log(Logger.TAG_BUG2GOITEM, "URL after download POST: " + connection.getURL());
			
			if (responseCode == HttpURLConnection.HTTP_OK
				&& !connection.getURL().toString()
							  .equals("https://b2gadm-mcloud101-blur.svcmot.com/bugreport/report/verify.action"))
			{
				Logger.log(Logger.TAG_BUG2GOITEM, "BUGID " + bugId + ": Entered in the if statement.");
				
				File downloadFolder;
				
				// Get the file name to be downloaded
				String fileName = connection.getHeaderField("Content-Disposition");
				Logger.log(Logger.TAG_BUG2GOITEM, fileName + ": 1");
				fileName = fileName.replace("\"", "");
				Logger.log(Logger.TAG_BUG2GOITEM, fileName + ": 2");
				fileName = fileName.substring(fileName.lastIndexOf("=") + 1);
				Logger.log(Logger.TAG_BUG2GOITEM, fileName + ": 3");
				sizeOfFile = connection.getContentLength();
				
				Logger.log(Logger.TAG_BUG2GOITEM,
						   "Size of file: " + String.valueOf(connection.getContentLength()));
				Logger.log(Logger.TAG_BUG2GOITEM, "File being downloaded: " + fileName);
				Logger.log(Logger.TAG_BUG2GOITEM, "File being saved in: " + SharedObjs.getDownloadPath());
				
				// Get download folder
				downloadFolder = new File(SharedObjs.getDownloadPath());
				
				// If user doesn't want to overwrite an existing file with the same name, abort download
				if (!overwrite)
				{
					for (File f : downloadFolder.listFiles())
					{
						if (f.getName().equals(fileName))
						{
							Logger.log(Logger.TAG_BUG2GOITEM, "File already exists. Aborting download.");
							status = DownloadStatus.DONE;
							running = false;
							removeFromList(this);
							connection.disconnect();
							return;
						}
					}
				}
				
				// Creates a new file
				file = new FileOutputStream(SharedObjs.getDownloadPath() + "\\" + fileName);
				// Buffer
				byte[] buffer = new byte[4096];
				int len;
				InputStream reader = connection.getInputStream();
				
				Logger.log(Logger.TAG_BUG2GOITEM, "BUGID " + bugId + ": SAT will start getting the file.");
				// Getting the file from server
				while ((len = reader.read(buffer)) > 0)
				{
					file.write(buffer, 0, len);
					downloadProgress += len;
				}
				
				Logger.log(Logger.TAG_BUG2GOITEM, "BUGID " + bugId + ": Download finished. Thread will die.");
				
				reader.close();
				SharedObjs.addLogLine(fileName + " download finished");
				file.close();
				
				status = DownloadStatus.DONE;
				running = false;
				removeFromList(this);
			}
			else
			{
				Logger.log(Logger.TAG_BUG2GOITEM, "POST request did not work");
				status = DownloadStatus.FAILED;
				running = false;
				retry++;
				removeFromList(this);
				connection.disconnect();
			}
			
		}
		catch (NullPointerException | IOException e)
		{
			e.printStackTrace();
			if (file != null)
			{
				try
				{
					file.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
			running = false;
			status = DownloadStatus.FAILED;
			downloadProgress = 0;
			retry++;
			removeFromList(this);
			connection.disconnect();
			return;
		}
		
		Logger.log(Logger.TAG_BUG2GOITEM, bugId + " Thread dead");
	}
	
	/**
	 * Getters and Setters
	 */
	public String getBugId()
	{
		return bugId;
	}
	
	public void setBugId(String bugId)
	{
		this.bugId = bugId;
	}
	
	public int getSizeOfFile()
	{
		return sizeOfFile;
	}
	
	public void setSizeOfFile(int sizeOfFile)
	{
		this.sizeOfFile = sizeOfFile;
	}
	
	public int getDownloadProgress()
	{
		return downloadProgress;
	}
	
	public void setDownloadProgress(int downloadProgress)
	{
		this.downloadProgress = downloadProgress;
	}
	
	public DownloadStatus getStatus()
	{
		return status;
	}
	
	public void setStatus(DownloadStatus status)
	{
		this.status = status;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void setRunning(boolean run)
	{
		this.running = run;
	}

	public int getRetry()
	{
		return retry;
	}

	public boolean isOverwrite()
	{
		return overwrite;
	}

	public void setOverwrite(boolean overwrite)
	{
		this.overwrite = overwrite;
	}

}
