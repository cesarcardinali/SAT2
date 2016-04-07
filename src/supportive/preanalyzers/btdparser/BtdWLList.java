package supportive.preanalyzers.btdparser;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class BtdWLList extends ArrayList<BtdWL>
{
	public boolean update(int index, BtdWL wl)
	{
		if (index >= 0)
		{
			return get(index).update(wl);
		}
		
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
	
	public int indexOf(BtdWL item)
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
	
	public BtdWL getLongerWL()
	{
		BtdWL longer = null;
		
		for (BtdWL wl : this)
		{
			if (longer == null)
				longer = wl;
			else if (longer.getLongerPeriod() < wl.getLongerPeriod())
				longer = wl;
		}
		return longer;
	}
	
	public void finalize()
	{
		for (int i = 0; i < this.size(); i++)
		{
			this.get(i).finalize();
			
			if (this.get(i).getLongerPeriod() < 15 * 60000)
			{
				this.remove(i);
				i = -1;
			}
		}
	}
	
	// Comparator and sort
	public class itensComparator implements Comparator<BtdWL>
	{
		public int compare(BtdWL p1, BtdWL p2)
		{
			return (int) (Float.compare(p1.getLongerPeriod(), p2.getLongerPeriod()));
		}
	}
	
	public void sortItens()
	{
		Collections.sort(this, new itensComparator());
		Collections.reverse(this);
	}
}
