package customobjects;


import java.util.Date;


/**
 * Represents an alarm item
 */
public class AlarmItem
{
	private Date   begin, end, last; // Keeps the initial, end and last detected date
	private String type;			 // Alarm type
	private String process;			 // Process that triggered the alarm
	private String action;			 // Action triggered
	private String log;				 // Log lines
	private int	   occurences;		 // Total occurrences
	private int	   warning;			 // How bad is the alarm item
	
	/**
	 * Defines an alarm item
	 * 
	 * @param dat Initial date
	 * @param typ Alarm type
	 * @param proc Alarm process
	 * @param act Alarm action
	 * @param log Log line to keep
	 */
	public AlarmItem(Date dat, String typ, String proc, String act, String log)
	{
		begin = dat;
		end = dat;
		last = dat;
		type = typ;
		process = proc;
		action = act;
		this.log = log;
		occurences = 1;
		warning = 0;
	}
	
	/**
	 * Update an existing alarm item
	 * 
	 * @param parsedDate Update the last detected time of occurence
	 * @param line Add a new log line to the item
	 */
	public void alarmUpdate(Date parsedDate, String line)
	{
		addOccurence();
		addLogLine(line);
		last = end;
		end = parsedDate;
		long difTempo = end.getTime() - last.getTime();
		
		if (difTempo < 20000)
		{
			warning = warning + 10;
		}
		else if (difTempo < 60000)
		{
			warning = warning + 5;
		}
		else
		{
			warning = warning + 1;
		}
		
		warning = warning / 2;
	}
	
	public String toString()
	{
		return "Process: *" + process + "*\nAction: " + getAction() + "\nType: " + getType()
			   + "\nOccurrences: " + getOccurences() + "\n" + "{noformat}\n" + log + "\n{noformat}\n";
	}
	
	// Getters and Setters
	public Date getBegin()
	{
		return begin;
	}
	
	public Date getEnd()
	{
		return end;
	}
	
	public Date getInstant()
	{
		return last;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getProcess()
	{
		return process;
	}
	
	public String getAction()
	{
		return action;
	}
	
	public int getOccurences()
	{
		return occurences;
	}
	
	public int getWarning()
	{
		return warning;
	}
	
	public long getDurationMs()
	{
		return end.getTime() - begin.getTime();
	}
	
	/**
	 * A to string for the duration as 00h00m00s
	 * 
	 * @return String representing the duration formatted as 00h00m00s
	 */
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
	
	public void setEnd(Date end)
	{
		this.end = end;
	}
	
	public void setInstant(Date last)
	{
		this.last = last;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public void setProcess(String process)
	{
		this.process = process;
	}
	
	public void setAction(String action)
	{
		this.action = action;
	}
	
	public void setWarning(int w)
	{
		warning = w;
	}
	
	public void addLogLine(String log)
	{
		this.log = this.log + "\n" + log;
	}
	
	public void addOccurence()
	{
		occurences++;
	}
}