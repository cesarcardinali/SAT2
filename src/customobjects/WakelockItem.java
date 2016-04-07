package customobjects;


import java.util.Date;


/**
 * Defines a wake lock item
 */
public class WakelockItem
{
	private String tag, lock, log, process, uid;
	private int	   quantity;
	private Date   begin, end;
	
	/**
	 * Constructor
	 * 
	 * @param tag Wake lock tag
	 * @param lock Wake lock lock number
	 * @param begin Initial date
	 * @param log Log line
	 */
	public WakelockItem(String tag, String lock, Date begin, String log)
	{
		this.tag = tag;
		this.lock = lock;
		this.begin = begin;
		this.log = log;
		process = "";
		end = begin;
		quantity = 0;
	}
	
	/**
	 * Constructor
	 * 
	 * @param uid Wake lock process uid
	 * @param tag Wake lock tag
	 * @param lock Wake lock lock number
	 * @param begin Initial date
	 * @param log Log line
	 */
	public WakelockItem(String uid, String tag, String lock, Date begin, String log)
	{
		this.process = "";
		this.tag = tag;
		this.lock = lock;
		this.uid = uid;
		this.begin = begin;
		this.log = log;
		end = begin;
		quantity = 0;
	}
	
	// Getters and Setters:
	public void setUid(String uid)
	{
		this.uid = uid;
	}
	
	public boolean setEnd(Date end)
	{
		this.end = end;
		return true;
	}
	
	public boolean setProcess(String process)
	{
		this.process = process;
		return true;
	}
	
	public boolean addLogLine(String log)
	{
		this.log = this.log + log;
		return true;
	}
	
	public int quantityInc()
	{
		return ++quantity;
	}
	
	public int quantityDec()
	{
		return --quantity;
	}
	
	public int getQuantity()
	{
		return quantity;
	}
	
	public String getTag()
	{
		return tag;
	}
	
	public String getLock()
	{
		return lock;
	}
	
	public String getDuration()
	{
		try
		{
			long diff = getDurationMs();
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000);
			return diffHours + "h" + diffMinutes + "m" + diffSeconds + "s";
		}
		catch (Exception e)
		{
			return "Error: " + e.toString();
		}
	}
	
	public long getDurationMs()
	{
		return end.getTime() - begin.getTime();
	}
	
	public String getLog()
	{
		return log;
	}
	
	public String getProcess()
	{
		return process;
	}
	
	public String getUid()
	{
		return uid;
	}
	
	public String toString()
	{
		return "- *(" + getProcess() + ")* causes tag *" + getTag()
			   + "* to acquire and hold PowerManagerService.Wakelock for " + getDuration()
			   + " draining battery life.\n" + "{noformat}\n" + getLog() + "\n{noformat}";
	}
}