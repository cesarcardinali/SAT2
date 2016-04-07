package customobjects;


/**
 * Defines a app item related to its consume
 */
public class HighConsumeItem
{
	private String process;
	private String PID;
	private String Log;
	private String LogOn;
	private String LogOff;
	private float  consumePeak, sumConsOn, sumConsOff, sumConsAvg;
	private int	   occurencesOn, occurencesOff, occurencesTotal;
	
	/**
	 * Constructor
	 * 
	 * @param proc Process name
	 * @param pid PID
	 * @param cons Consume
	 * @param line Lof line
	 */
	public HighConsumeItem(String proc, String pid, float cons, String line)
	{
		process = proc;
		PID = pid;
		sumConsOff = cons;
		sumConsOn = cons;
		sumConsAvg = cons;
		occurencesTotal = 1;
		occurencesOn = 0;
		occurencesOff = 0;
		Log = line + "\n";
		LogOn = "";
		LogOff = "";
		
		if (line.charAt(8) == 'F')
		{
			LogOff = LogOff + line + "\n";
			sumConsOff = cons;
			sumConsOn = 0;
			occurencesOff++;
		}
		else if (line.charAt(8) == 'N')
		{
			LogOn = LogOn + line + "\n";
			sumConsOff = 0;
			sumConsOn = cons;
			occurencesOff++;
		}
		else
		{
			LogOff = LogOff + line + "\n";
			LogOn = LogOn + line + "\n";
			sumConsOff = cons;
			sumConsOn = cons;
		}
	}
	
	/**
	 * Update item
	 * 
	 * @param cons Consume detected
	 * @param line Add new log line
	 * @return True if done with no errors
	 */
	public boolean updateItem(float cons, String line)
	{
		Log = Log + line + "\n";
		
		if(cons > 1000.0)
			cons = (float) (cons/100.0);
		if(cons > 100.0)
			cons = (float) (cons/10.0);
		
		if (line.charAt(8) == 'F')
		{
			LogOff = LogOff + line + "\n";
			sumConsOff = sumConsOff + cons;
			sumConsAvg += cons;
			occurencesOff++;
		}
		else if (line.charAt(8) == 'N')
		{
			LogOn = LogOn + line + "\n";
			sumConsOn = sumConsOn + cons;
			sumConsAvg += cons;
			occurencesOn++;
		}
		else
		{
			LogOff = LogOff + line + "\n";
			LogOn = LogOn + line + "\n";
			sumConsAvg += cons;
		}
		
		occurencesTotal++;
		
		if (cons > consumePeak)
			consumePeak = cons;
			
		return true;
	}
	
	// Getters and Setters
	public String getProcess()
	{
		return process;
	}
	
	public void setProcess(String process)
	{
		this.process = process;
	}
	
	public String getPID()
	{
		return PID;
	}
	
	public void setPID(String pID)
	{
		PID = pID;
	}
	
	public float getSumConsOn()
	{
		return sumConsOn;
	}
	
	public float getSumConsOff()
	{
		return sumConsOff;
	}
	
	public float getSumConsAvg()
	{
		return sumConsAvg;
	}
	
	public int getOccurencesOn()
	{
		return occurencesOn;
	}
	
	public int getOccurencesOff()
	{
		return occurencesOff;
	}
	
	public int getOccurencesTotal()
	{
		return occurencesTotal;
	}
	
	public void setSumConsOn(float sumConsOn)
	{
		this.sumConsOn = sumConsOn;
	}
	
	public void setSumConsOff(float sumConsOff)
	{
		this.sumConsOff = sumConsOff;
	}
	
	public void setSumConsAvg(float sumConsAvg)
	{
		this.sumConsAvg = sumConsAvg;
	}
	
	public void setOccurencesOn(int occurencesOn)
	{
		this.occurencesOn = occurencesOn;
	}
	
	public void setOccurencesOff(int occurencesOff)
	{
		this.occurencesOff = occurencesOff;
	}
	
	public void setOccurencesTotal(int occurencesTotal)
	{
		this.occurencesTotal = occurencesTotal;
	}
	
	public String getLog()
	{
		return Log;
	}
	
	public void setLog(String log)
	{
		Log = log;
	}
	
	public int getConsumeAvg()
	{
		return (int) (sumConsAvg / occurencesTotal);
	}
	
	public int getScOffConsume()
	{
		return (int) (sumConsOff / occurencesOff);
	}
	
	public int getScOnConsume()
	{
		return (int) (sumConsOn / occurencesOn);
	}
	
	public float getConsumePeak()
	{
		return consumePeak;
	}
	
	public void setConsumePeak(float consumePeak)
	{
		this.consumePeak = consumePeak;
	}
	
	public String getLogOn()
	{
		return LogOn;
	}
	
	public void setLogOn(String logOn)
	{
		LogOn = logOn;
	}
	
	public String getLogOff()
	{
		return LogOff;
	}
	
	public void setLogOff(String logOff)
	{
		LogOff = logOff;
	}
	
	public long getEstimatedRunTime()
	{
		return 60000*occurencesTotal;
	}

	/**
	 * Class to string
	 */
	public String toString()
	{
		return "Process: *" + process + "*\nPID: " + PID + "\nTotal Occurences: " + occurencesTotal  + "\nPeak Consume: " + consumePeak
			   + "\nAvg. Consume: " + (sumConsAvg / occurencesTotal) + "\nScrOff Consume: "
			   + (sumConsOff / occurencesOff) + "\n" + "{noformat}\n" + Log + "{noformat}\n";
	}
}
