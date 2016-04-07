package supportive.preanalyzers.logsparser;


import java.text.ParseException;

import supportive.DateTimeOperator;


public class BugRepKernelWL
{
	String name;
	long   duration;
	int    timesAcquired;
	
	public BugRepKernelWL(String name, String duration, String timesAcquired) throws ParseException
	{
		this.name = name.replaceAll(" +$", "");
		this.duration = DateTimeOperator.getMillisFromBtdStringDate(duration);
		this.timesAcquired = Integer.parseInt(timesAcquired);
	}
	
	public String toString()
	{
		return "[" + "name=" + name + ", duration=" + duration + "ms > "
		       + DateTimeOperator.getTimeStringFromMillis(duration) + ", timesAcquired=" + timesAcquired
		       + "]";
	}
	
	public String toJiraComment()
	{
		return "||Name|" + name + "|\\n|Duration|" 
		       + DateTimeOperator.getTimeStringFromMillis(duration) + " (" + duration + " ms)" + "|\\n|Times Acquired|" + timesAcquired
		       + "|\\n";
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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
	
	
}
