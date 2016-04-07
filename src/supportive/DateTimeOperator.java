package supportive;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeOperator
{
	public static long getMillis(String sDate)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date;
        try
        {
	        date = dateFormat.parse(sDate);
	        return date.getTime();
        }
        catch (ParseException e)
        {
        	System.out.println("222222222-- " + sDate);
	        e.printStackTrace();
	        return 0;
        }
	}
	
	static public String getTimeStringFromMillis(long timestamp)
	{
		long days, hours, minutes, seconds, millis;
		
		millis = timestamp % 1000;
		seconds = (timestamp / 1000) % 60;
		minutes = (timestamp / (60*1000)) % 60;
		hours = (timestamp / (60*60*1000)) % 24;
		days = timestamp / (24*60*60*1000);
		
		return days + "d," + hours + "h," + minutes + "m," + seconds + "s," + millis + "ms";
	}
	
	public static int[] stringTimeParser(String stime)
	{
		stime = stime.replaceAll(" \\(.+", "");
		String timeParts[] = stime.split(" ");
		int time[] = new int[timeParts.length];
		
		for (int i = 0; i < timeParts.length; i++)
		{
			time[timeParts.length - i - 1] = Integer.parseInt(timeParts[i].replaceAll("[a-z]", ""));
//			try
//			{
//				time[timeParts.length - i - 1] = Integer.parseInt(timeParts[i].replaceAll("[a-z]", ""));
//			}
//			catch (NumberFormatException ne)
//			{
//				System.out.println("Error parsing time:\n" + stime);
//				return null;
//			}
		}
		
		return time;
	}
	
	public static long getMillisFromBtdStringDate(String time)
	{
		int[] parts = stringTimeParser(time);
		long millis = parts[0];
		
		try
		{
			millis = millis + parts[1] * 1000;
			millis = millis + parts[2] * 1000 * 60;
			millis = millis + parts[3] * 1000 * 60 * 60;
			millis = millis + parts[4] * 1000 * 60 * 60 * 24;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		
		return millis;
	}
}