package supportive.preanalyzers.btdparser;


import java.util.ArrayList;
import supportive.DateTimeOperator;


public class BtdWL
{
	private String            name;
	private long              longerPeriod;
	private long              totalTime;
	
	private WLdata            actualWL;
	private ArrayList<WLdata> dataList;
	
	public BtdWL(String builder, long timestamp)
	{
		dataList = new ArrayList<BtdWL.WLdata>();
		
		String[] pieces = builder.split(":");
		
		if (pieces.length < 6)
			System.out.println("-- ERROR: " + builder);
		
		name = pieces[0];
		totalTime = 0;
		longerPeriod = -1;
		
		actualWL = new WLdata();
		actualWL.setActiveSince(Long.parseLong(pieces[4]) / 1000000); // /1000000 to cast it to ms
		actualWL.setInitialCount(Long.parseLong(pieces[1]));
		actualWL.setCount(1);
		actualWL.setStart(timestamp);
		actualWL.setStop(timestamp);
	}
	
	public boolean update(BtdWL wl)
	{
		if (wl.getActualWL().getActiveSince() >= actualWL.getActiveSince()
		    && wl.getActualWL().getStop() - actualWL.getStop() < 60000)
		{
			actualWL.setActiveSince(wl.getActualWL().getActiveSince());
			actualWL.setCount(actualWL.getCount() + wl.getActualWL().getCount() - actualWL.getInitialCount());
			actualWL.setStop(wl.getActualWL().getStop());
			// System.out.print("update > ");
		}
		else if (dataList.size() >= 1)
		{
			if (actualWL.getDuration() >= 15 * 60000
			    && (actualWL.getStop() - getLastData().getStop() < 2 * 60000))
			{
				totalTime = totalTime + actualWL.getDuration();
				getLastData().setCount(actualWL.getCount() + getLastData().getCount());
				getLastData().setStop(actualWL.getStop());
				if (getLastData().getActiveSince() < actualWL.getActiveSince())
					getLastData().setActiveSince(actualWL.getActiveSince());
				
				// System.out.println("\nConcatenated > ");
			}
			else if (actualWL.getDuration() >= 30 * 60000)
			{
				dataList.add(actualWL);
				if (longerPeriod < actualWL.getDuration())
				{
					longerPeriod = actualWL.getDuration();
					// System.out.println("LONGER");
				}
				totalTime = totalTime + actualWL.getDuration();
				
				// System.out.println("\nAdded1 > ");
			}
			
			actualWL = wl.getActualWL();
		}
		else if (actualWL.getDuration() >= 30 * 60000)
		{
			dataList.add(actualWL);
			totalTime = totalTime + actualWL.getDuration();
			if (longerPeriod < actualWL.getDuration())
			{
				longerPeriod = actualWL.getDuration();
				// System.out.println("LONGER");
			}
			
			actualWL = wl.getActualWL();
			// System.out.println("\nAdded 2 > ");
		}
		else
		{
			actualWL = wl.getActualWL();
			// System.out.println("\nDiscarted > ");
		}
		
		return false;
	}
	
	public void finalize()
	{
		if (dataList.size() >= 1)
		{
			if (actualWL.getDuration() >= 15 * 60000
			    && (actualWL.getStop() - getLastData().getStop() < 2 * 60000))
			{
				totalTime = totalTime + actualWL.getDuration();
				getLastData().setCount(actualWL.getCount() + getLastData().getCount());
				getLastData().setStop(actualWL.getStop());
				if (getLastData().getActiveSince() < actualWL.getActiveSince())
					getLastData().setActiveSince(actualWL.getActiveSince());
				
				// System.out.println("\nConcatenated > ");
			}
			else if (actualWL.getDuration() >= 30 * 60000)
			{
				dataList.add(actualWL);
				if (longerPeriod < actualWL.getDuration())
				{
					longerPeriod = actualWL.getDuration();
					// System.out.println("LONGER");
				}
				totalTime = totalTime + actualWL.getDuration();
				
				// System.out.println("\nAdded1 > ");
			}
		}
		else if (actualWL.getDuration() >= 30 * 60000)
		{
			dataList.add(actualWL);
			totalTime = totalTime + actualWL.getDuration();
			if (longerPeriod < actualWL.getDuration())
			{
				longerPeriod = actualWL.getDuration();
				// System.out.println("LONGER");
			}
			// System.out.println("\nAdded 2 > ");
		}
	}
	
	public String toString()
	{
		String tostring = "-Name: " + name + "\n" + "\tLonger period: "
		                  + DateTimeOperator.getTimeStringFromMillis(longerPeriod) + "\n"
		                  + "\tTotal active: " + DateTimeOperator.getTimeStringFromMillis(totalTime) + "\n";
		tostring = tostring + "\tLonger Active Since: "
		           + DateTimeOperator.getTimeStringFromMillis(getLongerActiveSince()) + " ("
		           + getLongerActiveSince() + ")\n\n";
		
		int i = 0;
		for (WLdata d : dataList)
		{
			i++;
			tostring = tostring + "\t\tPeriod " + i + ":\n" + "\t\tStart at "
			           + BtdParser.formatDate(BtdParser.generateDate(d.getStart())) + " (" + d.getStart()
			           + ")\n" + "\t\tStop at " + BtdParser.formatDate(BtdParser.generateDate(d.getStop()))
			           + " (" + d.getStop() + ")\n\n";
		}
		
		return tostring;
	}
	
	public String toJiraComment()
	{
		String tostring = "*Name:* " + name + "\\n" + "*Longer period:* "
		                  + DateTimeOperator.getTimeStringFromMillis(longerPeriod) + "\\n"
		                  + "*Total active:* " + DateTimeOperator.getTimeStringFromMillis(totalTime) + "\\n";
		tostring += "*Periods:*\\n{quote}\\n";
		
		int i = 0;
		for (WLdata d : dataList)
		{
			i++;
			tostring = tostring + "*Period " + i + ":*\\n" + "*Start:* "
			           + BtdParser.formatDate(BtdParser.generateDate(d.getStart())) + " (" + d.getStart()
			           + ")\\n" + "*Stop:* " + BtdParser.formatDate(BtdParser.generateDate(d.getStop()))
			           + " (" + d.getStop() + ")\\n" + "*Duration:* "
			           + DateTimeOperator.getTimeStringFromMillis(d.getDuration()) + " ("
			           + d.getDuration() + ")\\n";
		}
		tostring += "{quote}\\n----\\n\\n";
		
		return tostring;
	}
	
	public class WLdata
	{
		private long initialCount;
		private long start;
		
		private long count;
		private long activeSince;
		private long stop;
		
		// Getters and Setters
		public long getInitialCount()
		{
			return initialCount;
		}
		
		public void setInitialCount(long initialCount)
		{
			this.initialCount = initialCount;
		}
		
		public long getCount()
		{
			return count;
		}
		
		public void setCount(long wakes)
		{
			count = wakes;
		}
		
		public long getActiveSince()
		{
			return activeSince;
		}
		
		public void setActiveSince(long activeSince)
		{
			this.activeSince = activeSince;
		}
		
		public long getStart()
		{
			return start;
		}
		
		public void setStart(long start)
		{
			this.start = start;
		}
		
		public long getStop()
		{
			return stop;
		}
		
		public void setStop(long stop)
		{
			this.stop = stop;
		}
		
		public long getDuration()
		{
			return stop - start;
		}
	}
	
	// Getters and Setters
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public long getLongerPeriod()
	{
		return longerPeriod;
	}
	
	public void setLongerPeriod(long longerPeriod)
	{
		this.longerPeriod = longerPeriod;
	}
	
	public ArrayList<WLdata> getDataList()
	{
		return dataList;
	}
	
	public void setDataList(ArrayList<WLdata> dataList)
	{
		this.dataList = dataList;
	}
	
	public long getTotalTime()
	{
		return totalTime;
	}
	
	public void setTotalTime(long totalTime)
	{
		this.totalTime = totalTime;
	}
	
	public WLdata getActualWL()
	{
		return actualWL;
	}
	
	public void setActualWL(WLdata actualWL)
	{
		this.actualWL = actualWL;
	}
	
	// Others
	private WLdata getLastData()
	{
		return dataList.get(dataList.size() - 1);
	}
	
	private long getLongerActiveSince()
	{
		long longer = -1;
		for (WLdata wl : dataList)
		{
			if (wl.getActiveSince() > longer)
			{
				longer = wl.getActiveSince();
			}
		}
		return longer;
	}
}