package customobjects;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Class that defines a list of apps and its consumption
 */
@SuppressWarnings("serial")
public class HighConsume_List extends ArrayList<HighConsumeItem>
{
	public HighConsume_List()
	{
		super();
	}
	
	public int indexOf(HighConsumeItem obj)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (obj.getPID().equals(this.get(i).getPID()))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public int indexOf(String proc)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (proc.equals(this.get(i).getProcess()))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public class itensComparator implements Comparator<HighConsumeItem>
	{
		public int compare(HighConsumeItem p1, HighConsumeItem p2)
		{
			return (int) (Float.compare(p1.getOccurencesTotal(), p2.getOccurencesTotal()));
		}
	}
	
	public void sortItens()
	{
		Collections.sort(this, new itensComparator());
		Collections.reverse(this);
	}
}