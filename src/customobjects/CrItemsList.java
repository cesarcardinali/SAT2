package customobjects;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


@SuppressWarnings("serial")
public class CrItemsList extends ArrayList<CrItem>
{
	public CrItemsList()
	{
		super();
		this.clear();
	}
	
	/**
	 * Return the index of a CR with same JiraID of given CR
	 * 
	 * @param obj Cr item
	 * @return Index of a CR with same JiraID of given CR. Return -1 if not found
	 */
	public int indexOf(CrItem obj)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (obj.getJiraID().equals(this.get(i).getJiraID()))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Return the index of a CR with having given JiraID
	 * 
	 * @param key JiraID
	 * @return Index of a CR with having given JiraID
	 */
	public CrItem getCrByKey(String key)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (key.equals(this.get(i).getJiraID()))
			{
				return this.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Return the index of a CR with having given b2gId
	 * 
	 * @param key JiraID
	 * @return Index of a CR with having given JiraID
	 */
	public CrItem getCrByB2gId(String b2gId)
	{
		for (int i = 0; i < this.size(); i++)
		{
			if (b2gId.equals(this.get(i).getB2gID()))
			{
				return this.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * CrItem comparator
	 */
	public class itensComparator implements Comparator<CrItem>
	{
		public int compare(CrItem p1, CrItem p2)
		{
			return (int) p1.getJiraID().compareTo(p2.getJiraID());
		}
	}
	
	/**
	 * Sort list
	 */
	public void sortItens()
	{
		Collections.sort(this, new itensComparator());
		Collections.reverse(this);
	}
}