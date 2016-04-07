package supportive.preanalyzers.logsparser;


import java.util.ArrayList;


public class LogStatesData extends ArrayList<LogState>
{
	LogStatesData()
	{
		super();
	}
	
	// Modificar o metodo "add" para verificar o maior periodo e guardar o index
	
	public LogStatesData getChargingPeriods()
	{
		return null;
	}
	
	public LogStatesData getDischargingPeriods()
	{
		return null;
	}
	
	public LogState getLongerDischargingPeriod()
	{
		long longer = -1;
		int index = -1;
		
		for (int i = 0; i < this.size(); i++)
		{
			System.out.println("------- Periodo " + i + ":\n" + this.get(i).getStatus() + " - " + this.get(i).getDuration() + " - " + this.get(i).getStart()
			                   + " - " + this.get(i).getEnd());
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
