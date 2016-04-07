package supportive.preanalyzers.btdparser;


import java.util.ArrayList;


public class BtdStatesData extends ArrayList<BtdState>
{
	BtdStatesData()
	{
		super();
	}
	
	//Modificar o metodo "add" para verificar o maior periodo e guardar o index
	
	public BtdStatesData getChargingPeriods()
	{
		return null;
	}
	
	public BtdStatesData getDischargingPeriods()
	{
		return null;
	}
	
	public BtdState getLongerDischargingPeriod()
	{
		long longer = -1;
		int index = -1;
		
		for (int i=0; i<this.size(); i++)
		{
			if (this.get(i).getStatus() == 0 && this.get(i).getDuration() > longer)
			{
				longer = this.get(i).getDuration();
				index = i;
			}
		}
		
		if (index != -1)
			return this.get(index);
		else
			return null;
	}
}
