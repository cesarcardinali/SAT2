package customobjects;


import java.util.ArrayList;


/**
 * Define a list of wake lock item
 */
public class WackLock_List extends ArrayList<WakelockItem>
{
	private static final long serialVersionUID = 9080435546773455435L;
	
	/**
	 * @param o Alarm item
	 * @return Index or -1 if not found
	 */
	public int wlIndexOf(WakelockItem o)
	{
		WakelockItem wl;
		
		for (int i = 0; i < this.size(); i++)
		{
			wl = this.get(i);
			if (wl.getTag().equals(o.getTag())
				&& (wl.getProcess().equals(o.getProcess()) || wl.getUid().equals(o.getUid())))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * If exists a wake lock with the parameters
	 * 
	 * @param uid Wake lock uid
	 * @param tag Wake lock tag
	 * @return Index of wake lock in the list or -1 if not in the list.
	 */
	public int wlMatchUidTag(String uid, String tag)
	{
		WakelockItem wl;
		
		for (int i = 0; i < this.size(); i++)
		{
			wl = this.get(i);
			if (wl.getUid().equals(uid) && wl.getTag().equals(tag))
			{
				return i;
			}
		}
		
		return -1;
	}
}