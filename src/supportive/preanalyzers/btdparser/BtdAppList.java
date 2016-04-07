package supportive.preanalyzers.btdparser;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class BtdAppList extends ArrayList<BtdAppInfo>
{
	long totalRx, totalTx, totalCpu;
	
	public BtdAppList()
	{
		totalRx = 0;
		totalTx = 0;
	}
	
	public boolean update(String name, String uid, long cpu, long rx, long tx)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (name.equals(this.get(i).getName()))
			{
				get(i).update(cpu, rx, tx);
				// Logger.log("BtdAppsList", "Updating item");
				return true;
			}
		}
		
		BtdAppInfo app = new BtdAppInfo(name, uid, cpu, rx, tx);
		add(app);
		// Logger.log("BtdAppsList", "Adding new item");
		
		return false;
	}
	
	public int indexOf(String name)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (name.equals(this.get(i).getName()))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public int indexOf(BtdAppInfo item)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (item.getName().equals(this.get(i).getName()))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public BtdAppInfo get(String name)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (name.equals(this.get(i).getName()))
			{
				return this.get(i);
			}
		}
		
		return null;
	}
	
	public BtdAppInfo get(BtdAppInfo item)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (item.getName().equals(this.get(i).getName()))
			{
				return this.get(i);
			}
		}
		
		return null;
	}
	
	public long getTotalRx()
	{
		if (totalRx > 0)
			return totalRx;
		
		for (BtdAppInfo a : this)
		{
			totalRx += a.getDeltaRx();
		}
		
		return totalRx;
	}
	
	public long getTotalTx()
	{
		if (totalTx > 0)
			return totalTx;
		
		for (BtdAppInfo a : this)
		{
			totalTx += a.getDeltaTx();
		}
		
		return totalTx;
	}
	
	// Generate final statistics
	public void generateTotals()
	{
		totalRx = 0;
		totalTx = 0;
		totalCpu = 0;
		
		for (BtdAppInfo app : this)
		{
			if(app.getName().equals("root") || app.getName().equals("android.uid.system"))
				continue;
			totalRx += app.getDeltaRx();
			totalTx += app.getDeltaTx();
			totalCpu += app.getDeltaCpuTime();
		}
	}
	
	// Comparator and sort
	public class compareByName implements Comparator<BtdAppInfo>
	{
		public int compare(BtdAppInfo p1, BtdAppInfo p2)
		{
			return (int) (p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase()));
		}
	}
	
	public class compareByCpuTime implements Comparator<BtdAppInfo>
	{
		public int compare(BtdAppInfo p1, BtdAppInfo p2)
		{
			if (p1.getName().equals("root"))
				return -1;
			
			if (p1.getName().equals("android.uid.system"))
				return -1;
			
			if (p2.getName().equals("root"))
				return 99;
			
			if (p2.getName().equals("android.uid.system"))
				return 99;
			
			return (int) (Float.compare(p1.getDeltaCpuTime(), p2.getDeltaCpuTime()));
		}
	}
	
	public class compareByDataRx implements Comparator<BtdAppInfo>
	{
		public int compare(BtdAppInfo p1, BtdAppInfo p2)
		{
			return (int) (Float.compare(p1.getDeltaRx(), p2.getDeltaRx()));
		}
	}
	
	public class compareByDataTx implements Comparator<BtdAppInfo>
	{
		public int compare(BtdAppInfo p1, BtdAppInfo p2)
		{
			return (int) (Float.compare(p1.getDeltaTx(), p2.getDeltaTx()));
		}
	}
	
	public class compareByTotalData implements Comparator<BtdAppInfo>
	{
		public int compare(BtdAppInfo p1, BtdAppInfo p2)
		{
			return (int) (Float.compare(p1.getDeltaTx() + p1.getDeltaRx(), p2.getDeltaTx() + p1.getDeltaRx()));
		}
	}
	
	public void sortByName()
	{
		Collections.sort(this, new compareByName());
		Collections.reverse(this);
	}
	
	public void sortByCpu()
	{
		Collections.sort(this, new compareByCpuTime());
		Collections.reverse(this);
	}
	
	public void sortByDataRx()
	{
		Collections.sort(this, new compareByDataRx());
		Collections.reverse(this);
	}
	
	public void sortByDataTx()
	{
		Collections.sort(this, new compareByDataTx());
		Collections.reverse(this);
	}
	
	public void sortByTotalData()
	{
		Collections.sort(this, new compareByTotalData());
		Collections.reverse(this);
	}
}
