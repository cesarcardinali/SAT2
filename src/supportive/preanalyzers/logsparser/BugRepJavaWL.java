package supportive.preanalyzers.logsparser;


import java.text.ParseException;

import supportive.DateTimeOperator;


public class BugRepJavaWL
{
	String tagName;
	String processName;
	String uid;
	long   duration;
	int    timesAcquired;
	
	public BugRepJavaWL(String uid, String name, String duration, String timesAcquired) throws ParseException
	{
		this.tagName = name.replaceAll(" +$", "");
		this.uid = uid;
		this.duration = DateTimeOperator.getMillisFromBtdStringDate(duration);
		this.timesAcquired = Integer.parseInt(timesAcquired);
		processName = "Unknown";
	}
	
	public String toString()
	{
		return "[" + "tagName=" + tagName + ", uid=" + uid + ", process=" + processName + ", duration=" + duration
		       + "ms > " + DateTimeOperator.getTimeStringFromMillis(duration) + ", timesAcquired="
		       + timesAcquired + "]";
	}
	
	public String toJiraComment()
	{
		return "|*Tag*|" + tagName + "|\\n|UID|" + uid + "|\\n|Process|" + processName + "|\\n|Duration|"
		       + DateTimeOperator.getTimeStringFromMillis(duration) + " (" + duration
		       + " ms)|\\n|Times Acquired|" + timesAcquired + "|\\n";
	}
	
	public String getTagName()
	{
		return tagName;
	}
	
	public void setTagName(String tag)
	{
		tagName = tag;
	}
	
	public void setName(String name)
	{
		this.tagName = name;
	}
	
	public String getUid()
	{
		return uid;
	}
	
	public void setUid(String uid)
	{
		this.uid = uid;
	}
	
	public long getDuration()
	{
		return duration;
	}
	
	public void setDuration(long duration)
	{
		this.duration = duration;
	}
	
	public int getTimesAcquired()
	{
		return timesAcquired;
	}
	
	public void setTimesAcquired(int timesAcquired)
	{
		this.timesAcquired = timesAcquired;
	}
	
	public String getProcessName()
	{
		return processName;
	}
	
	public void setProcessName(String processName)
	{
		this.processName = processName;
	}
}
