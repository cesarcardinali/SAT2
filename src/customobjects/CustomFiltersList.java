package customobjects;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Defines a filters list
 */
@SuppressWarnings("serial")
public class CustomFiltersList extends ArrayList<CustomFilterItem>
{
	public CustomFiltersList()
	{
		super();
	}
	
	/**
	 * Look for a filter index in the list
	 * 
	 * @param obj Filter item
	 * @return Index as {@link int}. -1 if not found
	 */
	public int indexOf(CustomFilterItem obj)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (obj.getName().equals(this.get(i).getName()))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Return the index of a filter with the name
	 * 
	 * @param id SQL ID of this filter
	 * @return Index as {@link int}. -1 if not found
	 */
	public int indexOf(int id)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (id == this.get(i).getId())
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * @param timestamp
	 * @return Index
	 */
	public int indexOf(String timestamp)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (timestamp.equals(this.get(i).getLastUpdate()))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * @param name
	 * @return Index
	 */
	public int indexOfName(String name)
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
	
	/**
	 * @return
	 */
	public CustomFiltersList getActiveFilters(){
		CustomFiltersList aux = new CustomFiltersList();
		for (CustomFilterItem filter : this)
		{
			if (filter.isActive())
			{
				aux.add(filter);
			}
		}
		
		return aux;
	}
	
	/**
	 * FilterItem comparator by name
	 */
	public class itensNameComparator implements Comparator<CustomFilterItem>
	{
		public int compare(CustomFilterItem p1, CustomFilterItem p2)
		{
			return (int) p1.getName().compareTo(p2.getName());
		}
	}
	
	/**
	 * FilterItem comparator by date
	 */
	public class itensDateComparator implements Comparator<CustomFilterItem>
	{
		public int compare(CustomFilterItem p1, CustomFilterItem p2)
		{
			return (int) p1.getLastUpdate().compareTo(p2.getLastUpdate());
		}
	}
	
	/**
	 * FilterItem comparator by date
	 */
	public class itensOwnerComparator implements Comparator<CustomFilterItem>
	{
		public int compare(CustomFilterItem p1, CustomFilterItem p2)
		{
			return (int) p1.getOwner().compareTo(p2.getOwner());
		}
	}
	
	/**
	 * Sort list by name
	 */
	public void sortByName()
	{
		Collections.sort(this, new itensNameComparator());
		Collections.reverse(this);
	}
	
	/**
	 * Sort list by date
	 */
	public void sortByDate()
	{
		Collections.sort(this, new itensDateComparator());
		Collections.reverse(this);
	}
	
	/**
	 * Sort list by date
	 */
	public void sortByOwner()
	{
		Collections.sort(this, new itensOwnerComparator());
		Collections.reverse(this);
	}
	
	public String toString()
	{
		String toString = "";
		for (CustomFilterItem filter : this)
		{
			toString = toString + "--------------\n" + filter.toString() + "\n--------------\n";
		}
		return toString;
	}
}