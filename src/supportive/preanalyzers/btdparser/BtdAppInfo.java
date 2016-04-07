package supportive.preanalyzers.btdparser;



public class BtdAppInfo
{
	private String name;
	private String uid;
	private long   initialCpu;
	private long   initialRx;
	private long   initialTx;
	private long   deltaCpuTime;
	private long   deltaRx;
	private long   deltaTx;
	
	public BtdAppInfo(String nome, String id, long cpu, long rx, long tx)
	{
		name = nome;
		uid = id;
		initialCpu = cpu / 1000;
		initialRx = rx / 1000;
		initialTx = tx / 1000;
		deltaCpuTime = 0;
		deltaRx = 0;
		deltaTx = 0;
	}
	
	public void update(long cpu, long rx, long tx)
	{
		deltaCpuTime = cpu / 1000 - initialCpu;
		deltaRx = rx  / 1000 - initialRx;
		deltaTx = tx  / 1000 - initialTx;
	}
	
	// Getters and Setters
	public String getName()
	{
		return name;
	}

	public long getDeltaCpuTime()
	{
		return deltaCpuTime;
	}

	public long getDeltaRx()
	{
		return deltaRx;
	}

	public long getDeltaTx()
	{
		return deltaTx;
	}

	public String getUid()
	{
		return uid;
	}

	public String toString()
	{
		return "[ name=\"" + name + "\", uid:\"" + uid + "\", cpuTime=" + deltaCpuTime + ", RX=" + deltaRx + ", TX=" + deltaTx + " ]";
	}
	
	public String toJiraComment()
	{
		return "|" + name + "|\\n|" + uid + "|\\n|" + deltaCpuTime + "|\\n|" + deltaRx + "|\\n|" + deltaTx + "|\\n";
	}
}
