package supportive.preanalyzers.logsparser;

import java.util.Date;

public class LogState
{
	private long start  = -1; // timestamp
	private long end    = -1; // timestamp
	private int  status = -1; // [0=dischargin], [1=charging]
	                          
	public void resetTemporaries()
	{
		start = -1;
		end = -1;
		status = -1;
	}
	
	public long getDuration()
	{
		return end - start;
	}
	
	// Getters and Setters
	public long getStart()
	{
		return start;
	}
	
	public long getEnd()
	{
		return end;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public void setStart(long start)
	{
		this.start = start;
	}
	
	public void setEnd(long end)
	{
		this.end = end;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	// To String
	public String toString()
	{
		return "{[Start=" + start + "], [End=" + end + "], [Status=" + status + "]}";
	}

	public Date getStartDate()
    {
	    return new Date(start);
    }
	
	public Date getEndDate()
    {
	    return new Date(end);
    }	
}
