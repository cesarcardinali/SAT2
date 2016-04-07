package supportive.preanalyzers.btdparser;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Class that defines a list of apps and its consumption
 */
@SuppressWarnings("serial")
public class BtdUptimesList extends ArrayList<BtdUptimePeriod>
{
	// Complementary variables
	private BtdUptimePeriod longerPeriod;
	private long totalTime;
	
	// List methods override -----------------------------------
	public BtdUptimesList()
	{
		super();
		longerPeriod = new BtdUptimePeriod();
		totalTime = 0;
	}
	
	public boolean add(BtdUptimePeriod obj)
	{
		super.add(obj);
		
		if (obj.getDuration() > longerPeriod.getDuration())
		{
			longerPeriod = obj;
		}
		
		return true;
	}
	
	public int indexOf(BtdUptimePeriod obj)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (obj.getStart() == this.get(i).getStart())
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public Boolean contains(BtdUptimePeriod obj)
	{
		if (indexOf(obj) > -1)
		{
			return true;
		}
		
		return false;
	}
	
	// Sort and Compare methods -----------------------------
	public class itensComparator implements Comparator<BtdUptimePeriod>
	{
		public int compare(BtdUptimePeriod p1, BtdUptimePeriod p2)
		{
			return (int) (Float.compare(p1.getDuration(), p2.getDuration()));
		}
	}
	
	public void sortByDuration()
	{
		Collections.sort(this, new itensComparator());
		Collections.reverse(this);
	}
	
	// Sort and Compare methods -----------------------------
	public class itensComparator2 implements Comparator<BtdUptimePeriod>
	{
		public int compare(BtdUptimePeriod p1, BtdUptimePeriod p2)
		{
			return (int) (Float.compare(p1.getStart(), p2.getStart()));
		}
	}
	
	public void sortByStart()
	{
		Collections.sort(this, new itensComparator2());
	}
	
	// Getters and Setters -----------------------------------
	public BtdUptimePeriod getLongerPeriod()
	{
		for (BtdUptimePeriod u : this)
		{
			if(u.getDuration() > longerPeriod.getDuration())
			{
				longerPeriod = u;
			}
		}
		
		return longerPeriod;
	}
	
	public void setLongerPeriod(BtdUptimePeriod longerPeriod)
	{
		this.longerPeriod = longerPeriod;
	}
	
	public long getTotalTime()
	{
		totalTime = 0;
		for (BtdUptimePeriod u : this)
		{
			totalTime = totalTime + u.getDuration(); 
		}
		
		return totalTime;
	}
}