package supportive.preanalyzers.btdparser;


import supportive.DateTimeOperator;


public class BtdUptimePeriod
{
	private long start;
	private long end;
	private long duration;
	private BtdAppList appsData;
	private String summary;
	private boolean suspicious;
	
	public BtdUptimePeriod()
	{
		start = -1;
		end = -1;
		duration = -1;
		suspicious = true;
	}
	
	public BtdUptimePeriod(long begin)
	{
		start = -1;
		end = -1;
		duration = -1;
		suspicious = true;
	}
	
	// Getters and Setters -----------------------------------------------------
	public long getStart()
	{
		return start;
	}
	
	public void setStart(long start)
	{
		this.start = start;
	}
	
	public long getEnd()
	{
		return end;
	}
	
	public void setEnd(long end)
	{
		this.end = end;
		duration = end - start;
	}
	
	public long getDuration()
	{
		duration = end - start;
		return duration;
	}
	
	public void setDuration(long duration)
	{
		this.duration = duration;
	}
	
	public boolean isSuspicious()
	{
		return suspicious;
	}

	public void setSuspicious(boolean suspicious)
	{
		this.suspicious = suspicious;
	}

	public String getSummary()
	{
		return summary;
	}

	public void setSummary(String summary)
	{
		this.summary = summary;
	}
	
	public BtdAppList getAppsData()
	{
		return appsData;
	}

	public void setAppsData(BtdAppList appsData)
	{
		this.appsData = appsData;
	}

	public String toString()
	{
		String tostring = "\tStarted: " + BtdParser.formatDate(BtdParser.generateDate(start)) + "\n\tEnded: "
		                  + BtdParser.formatDate(BtdParser.generateDate(end)) + "\n\tDuration: "
		                  + DateTimeOperator.getTimeStringFromMillis(duration);
		return tostring;
	}
	
	public String toJiraComment()
	{
		String tostring = "||Started|" + BtdParser.formatDate(BtdParser.generateDate(start)) + "|\\n||Ended|"
		                  + BtdParser.formatDate(BtdParser.generateDate(end)) + "|\\n||Duration|"
		                  + DateTimeOperator.getTimeStringFromMillis(duration) + "|\\n";
		return tostring;
	}
}
