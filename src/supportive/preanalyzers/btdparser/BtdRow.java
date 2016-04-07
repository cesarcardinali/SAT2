package supportive.preanalyzers.btdparser;


import java.util.Date;


public class BtdRow
{
	// {{ Variables
	private int	   rowID;
	private int    temperature;
	private int    batteryCapacity;
	private int    batteryLevel;
	private int    chargerPlugged;        // 2- AC | 0- None
	private int    screenOn;              // 0- On | 1- Off
	private int    backlightIntensity;
	private long   timestamp;
	private long   gpsLocationUpdates;
	private long   networkLocationUpdates;
	private long   instantCurrent;        // currentNow column
	private long   cellTx;
	private long   cellRx;
	private long   wifiTx;
	private long   wifiRx;
	private Date   date;
	private String activeKernels;
	private String realTimeTotal;
	private String realTimeOnBattery;
	private String awakeTimeTotal;
	private String awakeTimeOnBattery;
	private String screenOnTime;
	private String phoneCallTime;
	private String wifiOnTime;            // Seems the same of below
	private String wifirunningTime;       // Seems the same of above
	private String screenBrightnesses;
	private String signalLevels;
	private String perUidData;
	private String foregroundActivity;
	private String foregroundPackage;
	private String topProcesses;          // TopData column
	private String audioTime;
	
	// }}
	
	// {{ Getters and Setters
	public int getTemperature()
	{
		return temperature;
	}
	
	public int getBatteryCapacity()
	{
		return batteryCapacity;
	}
	
	public int getBatteryLevel()
	{
		return batteryLevel;
	}
	
	public int getChargerPlugged()
	{
		return chargerPlugged;
	}
	
	public int getScreenOn()
	{
		return screenOn;
	}
	
	public int getBacklightIntensity()
	{
		return backlightIntensity;
	}
	
	public long getTimestamp()
	{
		return timestamp;
	}
	
	public long getGpsLocationUpdates()
	{
		return gpsLocationUpdates;
	}
	
	public long getNetworkLocationUpdates()
	{
		return networkLocationUpdates;
	}
	
	public long getInstantCurrent()
	{
		return instantCurrent;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public String getActiveKernels()
	{
		return activeKernels;
	}
	
	public String getRealTimeTotal()
	{
		return realTimeTotal;
	}
	
	public String getRealTimeOnBattery()
	{
		return realTimeOnBattery;
	}
	
	public String getAwakeTimeTotal()
	{
		return awakeTimeTotal;
	}
	
	public String getAwakeTimeOnBattery()
	{
		return awakeTimeOnBattery;
	}
	
	public String getScreenOnTime()
	{
		return screenOnTime;
	}
	
	public String getPhoneCallTime()
	{
		return phoneCallTime;
	}
	
	public String getWifiOnTime()
	{
		return wifiOnTime;
	}
	
	public String getWifirunningTime()
	{
		return wifirunningTime;
	}
	
	public String getScreenBrightnesses()
	{
		return screenBrightnesses;
	}
	
	public String getSignalLevels()
	{
		return signalLevels;
	}
	
	public String getPerUidData()
	{
		return perUidData;
	}
	
	public String getForegroundActivity()
	{
		return foregroundActivity;
	}
	
	public String getForegroundPackage()
	{
		return foregroundPackage;
	}
	
	public String getTopProcesses()
	{
		return topProcesses;
	}
	
	public String getAudioTime()
	{
		return audioTime;
	}
	
	public int getRowID()
	{
		return rowID;
	}
	
	public void setTemperature(int temperature)
	{
		this.temperature = temperature;
	}
	
	public void setBatteryCapacity(int batteryCapacity)
	{
		this.batteryCapacity = batteryCapacity;
	}
	
	public void setBatteryLevel(int batteryLevel)
	{
		this.batteryLevel = batteryLevel;
	}
	
	public void setChargerPlugged(int chargerPlugged)
	{
		this.chargerPlugged = chargerPlugged;
	}
	
	public void setScreenOn(int screenOn)
	{
		this.screenOn = screenOn;
	}
	
	public void setBacklightIntensity(int backlightIntensity)
	{
		this.backlightIntensity = backlightIntensity;
	}
	
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public void setGpsLocationUpdates(long gpsLocationUpdates)
	{
		this.gpsLocationUpdates = gpsLocationUpdates;
	}
	
	public void setNetworkLocationUpdates(long networkLocationUpdates)
	{
		this.networkLocationUpdates = networkLocationUpdates;
	}
	
	public void setInstantCurrent(long instantCurrent)
	{
		this.instantCurrent = instantCurrent;
	}
	
	@SuppressWarnings("deprecation")
    public void setDate(Date date)
	{
		date.setHours(date.getHours() + date.getTimezoneOffset()/60);
		this.date = date;
	}
	
	public void setActiveKernels(String activeKernels)
	{
		this.activeKernels = activeKernels;
	}
	
	public void setRealTimeTotal(String realTimeTotal)
	{
		this.realTimeTotal = realTimeTotal;
	}
	
	public void setRealTimeOnBattery(String realTimeOnBattery)
	{
		this.realTimeOnBattery = realTimeOnBattery;
	}
	
	public void setAwakeTimeTotal(String awakeTimeTotal)
	{
		this.awakeTimeTotal = awakeTimeTotal;
	}
	
	public void setAwakeTimeOnBattery(String awakeTimeOnBattery)
	{
		this.awakeTimeOnBattery = awakeTimeOnBattery;
	}
	
	public void setScreenOnTime(String screenOnTime)
	{
		this.screenOnTime = screenOnTime;
	}
	
	public void setPhoneCallTime(String phoneCallTime)
	{
		this.phoneCallTime = phoneCallTime;
	}
	
	public void setWifiOnTime(String wifiOnTime)
	{
		this.wifiOnTime = wifiOnTime;
	}
	
	public void setWifirunningTime(String wifirunningTime)
	{
		this.wifirunningTime = wifirunningTime;
	}
	
	public void setScreenBrightnesses(String screenBrightnesses)
	{
		this.screenBrightnesses = screenBrightnesses;
	}
	
	public void setSignalLevels(String signalLevels)
	{
		this.signalLevels = signalLevels;
	}
	
	public void setPerUidData(String perUidData)
	{
		this.perUidData = perUidData;
	}
	
	public void setForegroundActivity(String foregroundActivity)
	{
		this.foregroundActivity = foregroundActivity;
	}
	
	public void setForegroundPackage(String foregroundPackage)
	{
		this.foregroundPackage = foregroundPackage;
	}
	
	public void setTopProcesses(String topProcesses)
	{
		this.topProcesses = topProcesses;
	}
	
	public void setAudioTime(String audioTime)
	{
		this.audioTime = audioTime;
	}
	
	public void setRowID(int value)
	{
		rowID = value;
	}
	

	public long getCellTx()
	{
		return cellTx;
	}

	public void setCellTx(long cellTx)
	{
		this.cellTx = cellTx;
	}

	public long getCellRx()
	{
		return cellRx;
	}

	public void setCellRx(long cellRx)
	{
		this.cellRx = cellRx;
	}

	public long getWifiTx()
	{
		return wifiTx;
	}

	public void setWifiTx(long wifiTx)
	{
		this.wifiTx = wifiTx;
	}

	public long getWifiRx()
	{
		return wifiRx;
	}

	public void setWifiRx(long wifiRx)
	{
		this.wifiRx = wifiRx;
	}
	// }}

	// Other methods
	public String toString()
	{
		String string = date + " - " + timestamp + " - " + temperature + " - " + batteryCapacity + " - "
		                + batteryLevel + " - " + chargerPlugged + " - " + screenOn + " - "
		                + backlightIntensity + " - " + gpsLocationUpdates + " - " + networkLocationUpdates
		                + " - " + instantCurrent + " - " + activeKernels + " - " + realTimeTotal + " - "
		                + realTimeOnBattery + " - " + awakeTimeTotal + " - " + awakeTimeOnBattery + " - "
		                + screenOnTime + " - " + phoneCallTime + " - " + wifiOnTime + " - " + wifirunningTime
		                + " - " + screenBrightnesses + " - " + signalLevels + " - " + perUidData + " - "
		                + foregroundActivity + " - " + foregroundPackage + " - " + topProcesses + " - "
		                + audioTime;
		
		string = "| date  |  timestamp  |  temperature  |  batteryCapacity  |  batteryLevel  |  chargerPlugged  |  screenOn  |  backlightIntensity  |  gpsLocationUpdates  |"
		         + "networkLocationUpdates  |  instantCurrent  |  activeKernels  |  realTimeTotal  |  realTimeOnBattery  |  awakeTimeTotal  |  awakeTimeOnBattery   -"
		         + "screenOnTime  |  phoneCallTime  |  wifiOnTime  |  wifirunningTime  |  screenBrightnesses  |  signalLevels  |  perUidData   -"
		         + " foregroundActivity  |  foregroundPackage  |  topProcesses  |  audioTime |\n" + string;
		return string;
	}
}