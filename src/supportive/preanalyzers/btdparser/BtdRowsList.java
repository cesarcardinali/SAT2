package supportive.preanalyzers.btdparser;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Class that defines a list of apps and its consumption
 */
@SuppressWarnings("serial")
public class BtdRowsList extends ArrayList<BtdRow>
{
	// Complementary variables
	private BtdRow lastItem;
	private BtdRow firstItem;
	
	
	// List methods override -----------------------------------
	public BtdRowsList()
	{
		super();
		lastItem = null;
		firstItem = null;
	}
	
	public boolean add(BtdRow obj)
	{
		super.add(obj);
		
		if(firstItem == null)
		{
			firstItem = obj;
			lastItem = obj;
		}
		
		if (firstItem != null && obj.getTimestamp() < firstItem.getTimestamp())
			firstItem = obj;
		
		return true;
	}
	
	public int indexOf(BtdRow obj)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (obj.getTimestamp() == this.get(i).getTimestamp())
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public Boolean contains(BtdRow obj)
	{
		for (BtdRow item : this)
		{
			if (obj.getTimestamp() == item.getTimestamp())
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	// Sort and Compare methods -----------------------------
	public class itensComparator implements Comparator<BtdRow>
	{
		public int compare(BtdRow p1, BtdRow p2)
		{
			return (int) (Float.compare(p1.getTimestamp(), p2.getTimestamp()));
		}
	}
	
	public void sortItens()
	{
		Collections.sort(this, new itensComparator());
		Collections.reverse(this);
	}
	

	// Getters and Setters -----------------------------------
	public BtdRow getLastItem()
	{
		return lastItem;
	}

	public BtdRow getFirstItem()
	{
		return firstItem;
	}

	public void setLastItem(BtdRow lastItem)
	{
		this.lastItem = lastItem;
	}

	public void setFirstItem(BtdRow firstItem)
	{
		this.firstItem = firstItem;
	}	
}