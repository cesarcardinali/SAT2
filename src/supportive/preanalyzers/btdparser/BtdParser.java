package supportive.preanalyzers.btdparser;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import supportive.AppsChecker;
import supportive.DateTimeOperator;
import core.Logger;
import core.SharedObjs;


@SuppressWarnings("resource")
public class BtdParser
{
	private static Connection c;
	private static Statement  stmt;
	private static ResultSet  rs;
	private String            path;
	private int               status;
	private int               thresholdInc;
	private BtdRow            btdRow;
	private BtdRowsList       btdRows;
	private BtdState          finalState;
	private BtdStatesData     statesData;
	private BtdWLList         kernelWLs;
	private BtdUptimesList    uptimes;
	private BtdUptimesList    uptimesScOff;
	private long[]            screenData;        // 0- dark, 1- dim, 2- medium, 3- light, 4- bright
	private long[]            signalData;        // 0- none, 1- poor, 2- moderate, 3- good, 4- great
	private float[]           cpuTempData;       // 0- min, 1- max, 2- avg
	private float[]           deviceTempData;    // 0- min, 1- max, 2- avg
	private int               batCap;            // Battery capacity
	private int               bttDischarged[];   // Battery discharge from, to.
	private long              cellTX;            // Total cell data sent
	private long              cellRX;            // Total cell data received
	private long              wifiTX;            // Total wifi data sent
	private long              wifiRX;            // Total wifi data received
	private long              gpsLocation;       // Total GPS location count
	private long              networkLocation;   // Total GPS location count
	private long              consumeOn  = 0;
	private long              consumeOff = 0;
	private long              timeOff    = 0;
	private long              timeOn     = 0;
	private long              wifiOnTime;
	private long              wifiRunningTime;
	private long              realTimeOnBatt;
	private long              awakeTimeOnBatt;
	private long              phoneCall;
	private long              tetheringTime;
	private boolean           highUptime = false;
	
	// Configure parser
	public BtdParser(String path)
	{
		Logger.log(Logger.TAG_BTD_PARSER, "Initializing BTD parser");
		this.path = path;
		System.out.println("CR path: " + path);
		thresholdInc = 0;
		
		try
		{
			// File seek and load configuration
			String file_report = "";
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();
			
			if (folder.isDirectory())
			{
				// Look for the file
				for (int i = 0; i < listOfFiles.length; i++)
				{
					// Logger.log(Logger.TAG_DIAG, folder.listFiles()[i]);
					if (listOfFiles[i].isFile())
					{
						String file = listOfFiles[i].getName();
						if (file.endsWith(".btd") && listOfFiles[i].length() > 5000000)
						{
							file_report = "/" + file;
						}
					}
				}
			}
			
			if (!file_report.equals(""))
			{
				System.out.println("Using file: " + file_report);
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:" + path + file_report);
				stmt = c.createStatement();
				
				statesData = new BtdStatesData();
				btdRows = new BtdRowsList();
				
				status = 1;
				
				System.out.println("Opened database successfully");
			}
			else
			{
				status = 0;
				System.out.println("Failed to open DB\nPath: " + file_report);
				System.out.println("BTD file is too short or does not exists");
			}
			
			Logger.log(Logger.TAG_BTD_PARSER, "Initializing BTD parser - DONE");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	// Parse all data
	public boolean parse()
	{
		Logger.log(Logger.TAG_BTD_PARSER, "Starting parser");
		thresholdInc = 0;
		
		if (status == 1)
		{
			bttDischarged = new int[2];
			kernelWLs = new BtdWLList();
			
			Logger.log(Logger.TAG_BTD_PARSER, "Getting BTD periods");
			// Get charge and discharge periods
			getPeriods();
			if (statesData.size() > 0)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "Getting BTD periods - DONE");
			}
			else
			{
				Logger.log(Logger.TAG_BTD_PARSER, "BTD file could not be parsed");
				return false;
			}
			
			// Get the longer discharge period
			Logger.log(Logger.TAG_BTD_PARSER, "Getting longer period");
			finalState = getLongerDischargingPeriod();
			if (finalState == null)
			{
				return false;
			}
			
			Logger.log(Logger.TAG_BTD_PARSER, "Getting internet data");
			getDischargeInternetData(finalState);
			
			Logger.log(Logger.TAG_BTD_PARSER, "Getting general data");
			getDischargeGeneralData(finalState);
			
			Logger.log(Logger.TAG_BTD_PARSER, "Getting discharge battery data");
			getDischargeBatteryData(finalState);
			
			Logger.log(Logger.TAG_BTD_PARSER, "Getting phone signal data");
			getDischargePhoneSignalData(finalState);
			
			Logger.log(Logger.TAG_BTD_PARSER, "Getting screen bright data");
			getDischargeScreenBrightData(finalState);
			
			Logger.log(Logger.TAG_BTD_PARSER, "Getting temp data");
			getDischargeTemperatureData(finalState);
			
			Logger.log(Logger.TAG_BTD_PARSER, "Getting tether data");
			getTetheringTime();
			
			Logger.log(Logger.TAG_BTD_PARSER, "Getting discharge data");
			getDischargeBtdData(finalState, 0);
			System.out.println("Rows read: " + btdRows.size());
			
			// Print acquired data
			try
			{
				printResultToFile();
			}
			catch (IOException e)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "Error: " + e.getMessage());
				e.printStackTrace();
			}
			
			return true;
		}
		else
		{
			System.out.println("Was not possible to find BTD file or the existent is too short");
			return false;
		}
	}
	
	// Look for issues
	// {{
	public String eblDecreasers()
	{
		thresholdInc = 0;
		String reasons = "";
		
		// Call time
		if (getPercentage(phoneCall, realTimeOnBatt) > 5 && phoneCall / 60000 > 10)
		{
			reasons = reasons + "Phone calls for " + DateTimeOperator.getTimeStringFromMillis(phoneCall) + " ("
			          + formatNumber(getPercentage(phoneCall, realTimeOnBatt)) + "%)\\n";
			thresholdInc += 2.5 * (getPercentage(phoneCall, realTimeOnBatt));
		}
		
		// Tethering time
		if (getPercentage(tetheringTime, realTimeOnBatt) > 2 && tetheringTime / 60000 > 10)
		{
			reasons = reasons + "Some possible tethering for " + DateTimeOperator.getTimeStringFromMillis(tetheringTime) + " ("
			          + formatNumber(getPercentage(tetheringTime, realTimeOnBatt)) + "%)\\n";
			thresholdInc += 3 * (getPercentage(tetheringTime, realTimeOnBatt));
		}
		
		// High temperature
		if (deviceTempData[1] > 46)
		{
			reasons = reasons + "Device got hot for while: " + deviceTempData[1] + " - _Not an issue -> Game/GPS Apps/Heavy Usage_\\n";
		}
		
		// GPS service
		if (realTimeOnBatt < 3600000)
		{
			thresholdInc += 10;
			Logger.log(Logger.TAG_BTD_PARSER, "Seems that something is wrong with BTD");
		}
		else
		{
			if (gpsLocation / (realTimeOnBatt / 3600000) > 400)
			
			{
				reasons = reasons + "Heavy GPS activity: " + gpsLocation + "per hour\\n";
				thresholdInc += 0.007 * (gpsLocation / (realTimeOnBatt / 3600000));
			}
			else if (gpsLocation / (realTimeOnBatt / 3600000) > 150)
			{
				reasons = reasons + "Reasonable GPS activity: " + gpsLocation + "per hour\\n";
			}
			
			// Network location service
			if (networkLocation / (realTimeOnBatt / 3600000) > 35)
			{
				reasons = reasons + "Heavy network location activity: " + networkLocation + "per hour\\n";
				thresholdInc += 0.2 * (networkLocation / (realTimeOnBatt / 3600000));
			}
			else if (networkLocation / (realTimeOnBatt / 3600000) > 20)
			{
				reasons = reasons + "Reasonable network location activity: " + networkLocation + "per hour\\n";
				thresholdInc += 0.2 * (networkLocation / (realTimeOnBatt / 3600000));
			}
			else if (networkLocation / (realTimeOnBatt / 3600000) > 12)
			{
				reasons = reasons + "Considerable network location activity: " + networkLocation + "per hour\\n";
				thresholdInc += 0.2 * (networkLocation / (realTimeOnBatt / 3600000));
			}
		}
		
		return reasons;
	}
	
	// Uptime detected
	public boolean uptime()
	{
		if (uptimes.size() > 0 && uptimes.getLongerPeriod().getDuration() / 60000 > 60 // Update to 60 from 30
		    && getPercentage(uptimes.getLongerPeriod().getDuration(), realTimeOnBatt) > 7)
		{
			Logger.log(Logger.TAG_BTD_PARSER, "Longer Uptime: " + formatNumber(getPercentage(uptimes.getLongerPeriod().getDuration(), realTimeOnBatt)) + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "Total Uptime: " + formatNumber(getPercentage(uptimes.getTotalTime(), realTimeOnBatt)) + "%");
			
			return true;
		}
		
		return false;
	}
	
	// Uptime while screen off detected
	public boolean uptimeScOff()
	{
		if (uptimesScOff.size() > 0 && uptimesScOff.getLongerPeriod().getDuration() / 60000 > 22 // Updated to 30 from 15
		    && getPercentage(uptimesScOff.getLongerPeriod().getDuration(), realTimeOnBatt) > 6)
		{
			Logger.log(Logger.TAG_BTD_PARSER, "ScOff Longer Uptime: "
			                                  + formatNumber(getPercentage(uptimesScOff.getLongerPeriod().getDuration(), realTimeOnBatt)) + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "ScOff Total Uptime: " + formatNumber(getPercentage(uptimesScOff.getTotalTime(), realTimeOnBatt)) + "%");
			
			return true;
		}
		
		return false;
	}
	
	public boolean wakeLocks()
	{
		/*
		 * for (BtdWL item : kernelWLs) { if (item.getName().contains("PowerManagerService.Display")) continue;
		 * 
		 * if (getPercentage(item.getLongerPeriod(), realTimeOnBatt) > 5 && item.getLongerPeriod() >= 30 * 60000) return true; else return
		 * false; }
		 */
		// Updated to 45 from 30
		if (kernelWLs.getLongerWL() != null && getPercentage(kernelWLs.getLongerWL().getLongerPeriod(), realTimeOnBatt) > 7
		    && kernelWLs.getLongerWL().getLongerPeriod() >= 45 * 60000 && !kernelWLs.getLongerWL().getName().contains("PowerManagerService.Display"))
		{
			Logger.log(Logger.TAG_BTD_PARSER, "PowerManagerService wakelock detected: " + kernelWLs.size());
			Logger.log(Logger.TAG_BTD_PARSER, kernelWLs.getLongerWL().toString());
			// System.out.println(kernelWLs.getLongerWL());
			return true;
		}
		
		return false;
	}
	
	public boolean tethering()
	{
		System.out.println("Checking for tethering resolution:\nTotal on battery time: " + DateTimeOperator.getTimeStringFromMillis(realTimeOnBatt)
		                   + "\nTotal tethering time: " + DateTimeOperator.getTimeStringFromMillis(tetheringTime) + "\nProportion: "
		                   + formatNumber((float) (100.0 * tetheringTime / realTimeOnBatt)) + "%");
		if (tetheringTime >= realTimeOnBatt * 0.11)
			return true;
		else
			return false;
	}
	
	// }}
	
	// Acquire specific data -----------------------------------------------------------------------------------
	// {{
	private long getTetheringTime()
	{
		long lastCTX, actualCTX, lastWRX, actualWRX;
		long lastTime, actualTime, cumulativeTime = 0;
		
		try
		{
			rs = execQuery("select CELL_TX, WIFI_RX, timestamp from t_fgdata where timestamp BETWEEN " + finalState.getStart() + " AND " + finalState.getEnd()
			               + " AND WIFI_LABEL = ''  AND CELL_LABEL != '';");
			
			if (rs == null || rs.isClosed())
				return 0;
			
			System.out.println("-tether osiafdodsif\n" + finalState.getStart() + " AND " + finalState.getEnd());
			lastCTX = rs.getLong(1); // Received (BTD error)
			lastWRX = rs.getLong(2); // Transferred (BTD error)
			lastTime = rs.getLong(3);
			// int i =0;
			
			while (rs.next())
			{
				actualCTX = rs.getLong(1);
				actualWRX = rs.getLong(2);
				actualTime = rs.getLong(3);
				int timeCorrection = (int) ((actualTime - lastTime) / 10000);
				
				// i++;
				// System.out.println("CTX  = " + (actualCTX - lastCTX));
				// System.out.println("WTX  = " + (actualWRX - lastWRX));
				// System.out.println("Actual Time = " + actualTime + "  >  " + new Date(actualTime));
				// System.out.println("Last Time   = " + lastTime + "  >  " + new Date(lastTime));
				// System.out.println("TimeCorrection = " + timeCorrection);
				// System.out.println("Last - Actual  = " + (actualTime - lastTime));
				
				if ((timeCorrection) < 10 && (actualCTX - lastCTX) > 10000 * timeCorrection && (actualWRX - lastWRX) > 10000 * timeCorrection)
				{
					cumulativeTime = cumulativeTime + actualTime - lastTime;
					// System.out.println("tethering!");
				}
				else
				{
					// System.out.println("not tethering");
				}
				
				// System.out.println();
				
				lastCTX = actualCTX;
				lastWRX = actualWRX;
				lastTime = actualTime;
			}
			
			tetheringTime = cumulativeTime;
			
			return cumulativeTime;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	private boolean getDischargeBatteryData(BtdState finalState)
	{
		int lastRM, actualRM;
		long lastTime, actualTime;
		long lastScreenStatus;
		
		try
		{
			// Get all BTD data -------------------------------------
			rs = execQuery("SELECT RM, timestamp, ScreenOn FROM t_fgdata where timestamp BETWEEN " + finalState.getStart() + " AND " + finalState.getEnd()
			               + ";");
			
			lastRM = rs.getInt(1);
			lastTime = rs.getLong(2);
			lastScreenStatus = rs.getInt(3);
			
			while (rs.next())
			{
				actualRM = rs.getInt(1);
				actualTime = rs.getLong(2);
				if (actualTime - lastTime > 2 * 24 * 60 * 60000)
				{
					lastRM = actualRM;
					lastTime = actualTime;
					lastScreenStatus = rs.getInt(3);
					continue;
				}
				
				if (lastScreenStatus == 0) // If screen is Off
				{
					consumeOn = consumeOn + (lastRM - actualRM);
					timeOn = timeOn + (actualTime - lastTime);
					lastRM = actualRM;
				}
				else
				{
					consumeOff = consumeOff + (lastRM - actualRM);
					timeOff = timeOff + (actualTime - lastTime);
					lastRM = actualRM;
				}
				
				lastRM = actualRM;
				lastTime = actualTime;
				lastScreenStatus = rs.getInt(3);
			}
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private void getDischargeInternetData(BtdState finalState)
	{
		String[][] results = getMinMaxDiffData(new String[] {"CELL_TX", "CELL_RX", "WIFI_TX", "WIFI_RX", "GpsLocCount", "NetworkLocCount"}, new long[] {
		        finalState.getStart(), finalState.getEnd()});
		
		// RX/TX data in BTD is swapped, so, we need to swap them here
		cellRX = Long.parseLong(results[0][2]) / 1024; // divided by 1024 to change from Bytes to KBytes
		cellTX = Long.parseLong(results[1][2]) / 1024;
		wifiTX = Long.parseLong(results[2][2]) / 1024;
		wifiRX = Long.parseLong(results[3][2]) / 1024;
		gpsLocation = Long.parseLong(results[4][2]);
		networkLocation = Long.parseLong(results[5][2]);
	}
	
	private void getDischargeGeneralData(BtdState finalState)
	{
		String[][] results = getTopBottomData(new String[] {"BATTERY_LEVEL", "ActivePhoneCallTime", "RealTimeOnBatt", "AwakeTimeOnBatt", "WifiOnTime",
		        "WifiRunningTime", "FCC"}, new long[] {finalState.getStart(), finalState.getEnd()});
		
		bttDischarged[0] = Integer.parseInt(results[0][0]);
		bttDischarged[1] = Integer.parseInt(results[0][1]);
		phoneCall = getMillisFromBtdStringDate(results[1][1]) - getMillisFromBtdStringDate(results[1][0]);
		realTimeOnBatt = getMillisFromBtdStringDate(results[2][1]) - getMillisFromBtdStringDate(results[2][0]);
		awakeTimeOnBatt = getMillisFromBtdStringDate(results[3][1]) - getMillisFromBtdStringDate(results[3][0]);
		wifiOnTime = getMillisFromBtdStringDate(results[4][1]) - getMillisFromBtdStringDate(results[4][0]);
		wifiRunningTime = getMillisFromBtdStringDate(results[5][1]) - getMillisFromBtdStringDate(results[5][0]);
		batCap = Integer.parseInt(results[6][0]);
	}
	
	private void getDischargePhoneSignalData(BtdState finalState)
	{
		String[][] results = getTopBottomData(new String[] {"SignalLevels"}, new long[] {finalState.getStart(), finalState.getEnd()});
		
		String[] intialSignalParts = results[0][0].split(", ");
		String[] finalSignalParts = results[0][1].split(", ");
		
		signalData = new long[6];
		
		for (String s : finalSignalParts)
		{
			String[] data = s.split(" ");
			switch (data[0])
			{
				case "none":
					signalData[0] = getMillisFromBtdStringDate(data[1]);
					break;
				case "poor":
					signalData[1] = getMillisFromBtdStringDate(data[1]);
					break;
				case "moderate":
					signalData[2] = getMillisFromBtdStringDate(data[1]);
					break;
				case "good":
					signalData[3] = getMillisFromBtdStringDate(data[1]);
					break;
				case "great":
					signalData[4] = getMillisFromBtdStringDate(data[1]);
					break;
				default:
					break;
			}
		}
		
		for (String s : intialSignalParts)
		{
			String[] data = s.split(" ");
			switch (data[0])
			{
				case "none":
					signalData[0] = signalData[0] - getMillisFromBtdStringDate(data[1]);
					break;
				case "poor":
					signalData[1] = signalData[1] - getMillisFromBtdStringDate(data[1]);
					break;
				case "moderate":
					signalData[2] = signalData[2] - getMillisFromBtdStringDate(data[1]);
					break;
				case "good":
					signalData[3] = signalData[3] - getMillisFromBtdStringDate(data[1]);
					break;
				case "great":
					signalData[4] = signalData[4] - getMillisFromBtdStringDate(data[1]);
					break;
				default:
					break;
			}
		}
		
		signalData[5] = signalData[0] + signalData[1] + signalData[2] + signalData[3] + signalData[4];
		/*
		 * for (long d : signalData) { System.out.println(millisToHours(d)); }
		 */
	}
	
	private void getDischargeScreenBrightData(BtdState finalState)
	{
		String[][] results = getTopBottomData(new String[] {"ScreenBrightnesses"}, new long[] {finalState.getStart(), finalState.getEnd()});
		
		// dark 6s,472ms (0,9%), dim 11m,27s,264ms (99,1%)
		String[] intialScreenParts = results[0][0].split(", ");
		String[] finalScreenParts = results[0][1].split(", ");
		
		screenData = new long[6];
		
		for (String s : finalScreenParts)
		{
			String[] data = s.split(" ");
			switch (data[0])
			{
				case "dark":
					screenData[0] = getMillisFromBtdStringDate(data[1]);
					break;
				case "dim":
					screenData[1] = getMillisFromBtdStringDate(data[1]);
					break;
				case "medium":
					screenData[2] = getMillisFromBtdStringDate(data[1]);
					break;
				case "light":
					screenData[3] = getMillisFromBtdStringDate(data[1]);
					break;
				case "bright":
					screenData[4] = getMillisFromBtdStringDate(data[1]);
					break;
				default:
					break;
			}
		}
		
		for (String s : intialScreenParts)
		{
			String[] data = s.split(" ");
			switch (data[0])
			{
				case "none":
					screenData[0] = screenData[0] - getMillisFromBtdStringDate(data[1]);
					break;
				case "poor":
					screenData[1] = screenData[1] - getMillisFromBtdStringDate(data[1]);
					break;
				case "moderate":
					screenData[2] = screenData[2] - getMillisFromBtdStringDate(data[1]);
					break;
				case "good":
					screenData[3] = screenData[3] - getMillisFromBtdStringDate(data[1]);
					break;
				case "great":
					screenData[4] = screenData[4] - getMillisFromBtdStringDate(data[1]);
					break;
				default:
					break;
			}
		}
		
		screenData[5] = screenData[0] + screenData[1] + screenData[2] + screenData[3] + screenData[4];
		/*
		 * for (long d : screenData) { System.out.println(millisToHours(d)); }
		 */
	}
	
	private void getDischargeTemperatureData(BtdState finalState)
	{
		try
		{
			rs = execQuery("SELECT MAX(TEMP), MIN(TEMP), AVG(TEMP), MAX(TEMP_1), MIN(TEMP_1), AVG(TEMP_1), MAX(TEMP_2), MIN(TEMP_2), AVG(TEMP_2) FROM t_fgdata where timestamp BETWEEN "
			               + finalState.getStart() + " AND " + finalState.getEnd() + ";");
			
			cpuTempData = new float[3];
			deviceTempData = new float[3];
			
			cpuTempData[0] = rs.getFloat(2);
			cpuTempData[1] = rs.getFloat(1);
			cpuTempData[2] = rs.getFloat(3);
			
			deviceTempData[0] = rs.getFloat(5);
			deviceTempData[1] = rs.getFloat(4);
			deviceTempData[2] = rs.getFloat(6);
			/*
			 * for (float f : cpuTempData) { System.out.println(f); }
			 * 
			 * System.out.println();
			 * 
			 * for (float f : cpuTempData) { System.out.println(f); }
			 */
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public BtdRowsList getDischargeBtdData(BtdState finalState, long timezone)
	{
		btdRows = new BtdRowsList();
		kernelWLs = new BtdWLList();
		uptimes = new BtdUptimesList();
		uptimesScOff = new BtdUptimesList();
		
		try
		{
			// Get all BTD data -------------------------------------
			rs = execQuery("SELECT rowid, * FROM t_fgdata WHERE timestamp BETWEEN " + finalState.getStart() + " AND " + finalState.getEnd() + ";");
			
			btdRows = new BtdRowsList();
			BtdUptimePeriod actualUptime = new BtdUptimePeriod();
			BtdUptimePeriod uptimeOff = new BtdUptimePeriod();
			
			while (rs.next())
			{
				btdRow = new BtdRow();
				setupBtdRow(btdRow, rs, timezone);
				
				// Uptimes
				if (actualUptime.getStart() == -1)
				{
					// System.out.println("1");
					actualUptime.setStart(btdRow.getTimestamp());
				}
				else if (actualUptime.getEnd() == -1 || (btdRow.getTimestamp() - actualUptime.getEnd() < 20000))
				{
					// System.out.println("2");
					actualUptime.setEnd(btdRow.getTimestamp());
				}
				else
				{
					// System.out.println("Duration = " + actualUptime.getDuration());
					if (actualUptime.getDuration() > 300000)
					{
						// System.out.println("4");
						if (uptimes.size() <= 0)
						{
							uptimes.add(actualUptime);
							actualUptime = new BtdUptimePeriod();
						}
						else
						{
							// System.out.println("4 -");
							if (actualUptime.getStart() - uptimes.get(uptimes.size() - 1).getEnd() < 49600)
							{
								uptimes.get(uptimes.size() - 1).setEnd(actualUptime.getEnd());
								actualUptime = new BtdUptimePeriod();
							}
							else
							{
								uptimes.add(actualUptime);
								actualUptime = new BtdUptimePeriod();
							}
						}
					}
					else
					{
						// System.out.println("5 ---- " + actualUptime.getDuration());
						if (uptimes.size() > 0 && actualUptime.getDuration() > 30000
						    && (actualUptime.getStart() - uptimes.get(uptimes.size() - 1).getEnd()) < 49600
						    && uptimes.get(uptimes.size() - 1).getDuration() > 300000)
						{
							uptimes.get(uptimes.size() - 1).setEnd(actualUptime.getEnd());
						}
						actualUptime = new BtdUptimePeriod();
					}
				}
				
				// Uptimes Sc Off
				if (btdRow.getScreenOn() == 1
				    || (btdRow.getTopProcesses().contains("mediaserver") || btdRow.getTopProcesses().contains("tunein")
				        || btdRow.getTopProcesses().contains("slacker") || btdRow.getTopProcesses().contains("pandora")
				        || btdRow.getTopProcesses().contains("sirius") || btdRow.getTopProcesses().contains("android.music")
				        || btdRow.getTopProcesses().contains("saavn") || btdRow.getTopProcesses().contains("com.audible.application")
				        || btdRow.getTopProcesses().contains("spotify") || btdRow.getTopProcesses().contains("fmradio")))
				{
					// System.out.println("> ScOff: " + btdRow.getTimestamp() + " - " + uptimeOff.getEnd());
					// System.out.print(">Sc Off ");
					if (uptimeOff.getStart() == -1)
					{
						// System.out.println("1");
						uptimeOff.setStart(btdRow.getTimestamp());
						uptimeOff.setEnd(btdRow.getTimestamp());
						// System.out.println("Start: " + BtdParser.formatDate(BtdParser.generateDate(uptimeOff.getStart()),
						// "America/Chicago"));
					}
					else if (btdRow.getTimestamp() - uptimeOff.getEnd() < 20000)
					{
						// //System.out.println("2");
						uptimeOff.setEnd(btdRow.getTimestamp());
					}
					else if (uptimeOff.getDuration() < 300000)
					{
						// System.out.println("3");
						uptimeOff = new BtdUptimePeriod();
					}
					else if (uptimeOff.getDuration() >= 300000)
					{
						// System.out.println("99");
						uptimesScOff.add(uptimeOff);
						uptimeOff = new BtdUptimePeriod();
					}
				}
				else
				{
					// System.out.println("> ScOn");
					if (uptimeOff.getDuration() >= 300000)
					{
						// System.out.println("Duration long enought");
						if (uptimesScOff.size() <= 0)
						{
							// System.out.println("4");
							// System.out.println("Start: " + BtdParser.formatDate(BtdParser.generateDate(uptimeOff.getStart()),
							// "America/Chicago"));
							// System.out.println("End: " + BtdParser.formatDate(BtdParser.generateDate(uptimeOff.getEnd()),
							// "America/Chicago"));
							uptimesScOff.add(uptimeOff);
							uptimeOff = new BtdUptimePeriod();
						}
						else
						{
							if (uptimeOff.getStart() - uptimesScOff.get(uptimesScOff.size() - 1).getEnd() < 49600)
							{
								// System.out.println("5");
								// System.out.println("Start: " + BtdParser.formatDate(BtdParser.generateDate(uptimeOff.getStart()),
								// "America/Chicago"));
								// System.out.println("End: " + BtdParser.formatDate(BtdParser.generateDate(uptimeOff.getEnd()),
								// "America/Chicago"));
								uptimesScOff.get(uptimesScOff.size() - 1).setEnd(uptimeOff.getEnd());
								uptimeOff = new BtdUptimePeriod();
							}
							else
							{
								// System.out.println("6");
								uptimesScOff.add(uptimeOff);
								uptimeOff = new BtdUptimePeriod();
							}
						}
					}
					else
					{
						// System.out.println("Duration not long enought");
						if (uptimesScOff.size() > 0 && uptimeOff.getDuration() > 30000
						    && (uptimeOff.getStart() - uptimesScOff.get(uptimesScOff.size() - 1).getEnd()) < 49600)
						{
							// System.out.println("But enought to concate");
							uptimesScOff.get(uptimesScOff.size() - 1).setEnd(uptimeOff.getEnd());
						}
						// System.out.println("8 "
						// + BtdParser.formatDate(BtdParser.generateDate(btdRow.getTimestamp())) + " - " +
						// DateTimeOperator.getTimeStringFromMillis(uptimeOff.getDuration()));
						uptimeOff = new BtdUptimePeriod();
					}
				}
				
				// Look for stuck wake locks --------------------------------
				String[] kWLs = btdRow.getActiveKernels().split("\\|");
				for (String kwl : kWLs)
				{
					if (kwl.equals(""))
						continue;
					
					BtdWL wl = new BtdWL(kwl, btdRow.getTimestamp());
					int index = kernelWLs.indexOf(wl);
					// System.out.println(index);
					
					if (index >= 0)
					{
						kernelWLs.update(index, wl);
					}
					else
					{
						kernelWLs.add(wl);
					}
				}
				btdRows.add(btdRow);
			}
			
			if (actualUptime.getDuration() > 600000)
				uptimes.add(actualUptime);
			
			if (uptimeOff.getDuration() > 600000) // Tempo uptime off
				uptimesScOff.add(uptimeOff);
			
			kernelWLs.finalize();
			kernelWLs.sortItens();
			
			// for (BtdWL wl : kernelWLs)
			// {
			// System.out.println(wl);
			// }
			
			for (int i = 0; i < uptimes.size(); i++)
			{
				if (uptimes.get(i).getDuration() < 750000 || uptimes.get(i).getDuration() > 3 * 24 * 60 * 60000)
				{
					uptimes.remove(i);
					i = 0;
				}
			}
			if (uptimes.size() == 0)
			{
				uptimes.setLongerPeriod(null);
			}
			
			for (int i = 0; i < uptimesScOff.size(); i++)
			{
				if (uptimesScOff.get(i).getDuration() < 600000 || uptimesScOff.get(i).getDuration() > 3 * 24 * 60 * 60000)
				{
					uptimesScOff.remove(i);
					i--;
				}
			}
			if (uptimesScOff.size() == 0)
			{
				uptimesScOff.setLongerPeriod(new BtdUptimePeriod());
			}
			
			parseUptimes();
			
			uptimes.sortByStart();
			uptimesScOff.sortByStart();
			
			// rs.close();
			// stmt.close();
		}
		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		
		return btdRows;
	}
	
	public void parseUptimes()
	{
		int audio, gps, games, screenOn, mediaServer, bluetooth, camera, videoFootage, count, nulls;
		long deltaWTx, deltaWRx, deltaCTx, deltaCRx, deltaGps, deltaNetLocation;
		float time;
		BtdRow lastRow = null;
		AppsChecker.initClass();
		
		System.out.println("Analizing general uptimes");
		int uptimesCount = 0;
		for (BtdUptimePeriod up : getUptimes())
		{
			uptimesCount++;
			audio = 0;
			gps = 0;
			games = 0;
			screenOn = 0;
			mediaServer = 0;
			bluetooth = 0;
			camera = 0;
			videoFootage = 0;
			count = 0;
			nulls = 0;
			deltaWRx = 0;
			deltaWTx = 0;
			deltaCRx = 0;
			deltaCTx = 0;
			deltaGps = 0;
			deltaNetLocation = 0;
			time = (float) (up.getDuration() / 60000.0);
			BtdAppList appsList = new BtdAppList();
			
			Logger.log(Logger.TAG_BTD_PARSER, "Uptime " + uptimesCount + " start: " + BtdParser.formatDate(BtdParser.generateDate(up.getStart())));
			Logger.log(Logger.TAG_BTD_PARSER, "Uptime " + uptimesCount + " end: " + BtdParser.formatDate(BtdParser.generateDate(up.getEnd())));
			Logger.log(Logger.TAG_BTD_PARSER, "Uptime " + uptimesCount + " duration: " + DateTimeOperator.getTimeStringFromMillis(up.getDuration()));
			Logger.log(Logger.TAG_BTD_PARSER, "Time duration in minutes: " + formatNumber(time));
			
			for (BtdRow btdRow : getBtdRows())
			{
				if (btdRow.getTimestamp() >= up.getStart() && btdRow.getTimestamp() <= up.getEnd())
				{
					if (btdRow.getTimestamp() == up.getStart())
					{
						// divide by 1024 to convert to KB
						deltaWRx = btdRow.getWifiRx() / 1024;
						deltaWTx = btdRow.getWifiTx() / 1024;
						deltaCRx = btdRow.getCellRx() / 1024;
						deltaCTx = btdRow.getCellTx() / 1024;
						deltaGps = btdRow.getGpsLocationUpdates();
						deltaNetLocation = btdRow.getNetworkLocationUpdates();
						
						String perUidApps[] = btdRow.getPerUidData().split("\\|");
						Logger.log(Logger.TAG_BTD_PARSER, "Initial perUid len: " + perUidApps.length);
						Logger.log(Logger.TAG_BTD_PARSER, "Initial deltaCTX: " + deltaCTx);
						for (String a : perUidApps)
						{
							String appParts[] = a.split(":");
							if (!appParts[0].contains("unknown") && appParts.length >= 7)
								appsList.update(appParts[0], appParts[1], Long.parseLong(appParts[2]), Long.parseLong(appParts[3]), Long.parseLong(appParts[4]));
						}
					}
					else if (btdRow.getTimestamp() == up.getEnd())
					{
						// divide by 1024 to convert to KB
						deltaWRx = btdRow.getWifiRx() / 1024 - deltaWRx;
						deltaWTx = btdRow.getWifiTx() / 1024 - deltaWTx;
						deltaCRx = btdRow.getCellRx() / 1024 - deltaCRx;
						deltaCTx = btdRow.getCellTx() / 1024 - deltaCTx;
						deltaGps = btdRow.getGpsLocationUpdates() - deltaGps;
						deltaNetLocation = btdRow.getNetworkLocationUpdates() - deltaNetLocation;
						
						String perUidApps[] = btdRow.getPerUidData().split("\\|");
						Logger.log(Logger.TAG_BTD_PARSER, "Final perUid len: " + perUidApps.length);
						Logger.log(Logger.TAG_BTD_PARSER, "Final deltaCTX: " + deltaCTx);
						for (String a : perUidApps)
						{
							String appParts[] = a.split(":");
							if (!appParts[0].contains("unknown") && appParts.length >= 7)
								appsList.update(appParts[0], appParts[1], Long.parseLong(appParts[2]), Long.parseLong(appParts[3]), Long.parseLong(appParts[4]));
						}
					}
					else
					{
						String perUidApps[] = btdRow.getPerUidData().split("\\|");
						for (String a : perUidApps)
						{
							String appParts[] = a.split(":");
							if (!appParts[0].contains("unknown") && appParts.length >= 7)
								appsList.update(appParts[0], appParts[1], Long.parseLong(appParts[2]), Long.parseLong(appParts[3]), Long.parseLong(appParts[4]));
						}
					}
					
					if (btdRow.getTopProcesses().equals("null") || btdRow.getTopProcesses().equals(""))
					{
						nulls++;
						if (lastRow == null)
						{
							continue;
						}
						if (AppsChecker.isAudioService(lastRow.getTopProcesses()))
						{
							mediaServer++;
						}
						if (AppsChecker.isCameraService(lastRow.getTopProcesses()))
						{
							camera++;
						}
						if (AppsChecker.isCameraService(lastRow.getTopProcesses()) && AppsChecker.isAudioService(lastRow.getTopProcesses()))
						{
							videoFootage++;
						}
						if (AppsChecker.isBTService(lastRow.getTopProcesses()))
						{
							bluetooth++;
						}
						if (AppsChecker.isAudioApp(lastRow.getTopProcesses()))
						{
							audio++;
						}
						if (AppsChecker.isGpsApp(lastRow.getTopProcesses()))
						{
							gps++;
						}
						if (AppsChecker.isGameApp(lastRow.getTopProcesses()))
						{
							games++;
						}
						if (lastRow.getScreenOn() == 0)
						{
							screenOn++;
						}
					}
					else
					{
						if (AppsChecker.isAudioApp(btdRow.getTopProcesses()))
						{
							audio++;
						}
						if (AppsChecker.isGpsApp(btdRow.getTopProcesses()))
						{
							gps++;
						}
						if (AppsChecker.isGameApp(btdRow.getTopProcesses()))
						{
							games++;
						}
						if (btdRow.getScreenOn() == 0)
						{
							screenOn++;
						}
						if (AppsChecker.isBTService(btdRow.getTopProcesses()))
						{
							bluetooth++;
						}
						
						lastRow = btdRow;
					}
					
					count++;
				}
			}
			
			appsList.generateTotals();
			
			float ctxPerMin, crxPerMin, wtxPerMin, wrxPerMin, gpsPerMin, netLocationPerMin, audioPerCount, mediaPerCount, btPerCount, gpsAppsPerCount, gamesPerCount, cameraPerCount, videoPerCount, scOnPerCount;
			ctxPerMin = deltaCTx / time;
			crxPerMin = deltaCRx / time;
			wtxPerMin = deltaWTx / time;
			wrxPerMin = deltaWRx / time;
			gpsPerMin = deltaGps / time;
			netLocationPerMin = deltaNetLocation / time;
			audioPerCount = (float) (audio * 100.0 / count);
			mediaPerCount = (float) (mediaServer * 100.0 / count);
			btPerCount = (float) (bluetooth * 100.0 / count);
			gpsAppsPerCount = (float) (gps * 100.0 / count);
			gamesPerCount = (float) (games * 100.0 / count);
			cameraPerCount = (float) (camera * 100.0 / count);
			videoPerCount = (float) (videoFootage * 100.0 / count);
			scOnPerCount = (float) (screenOn * 100.0 / count);
			
			if ((audioPerCount > 80 || mediaPerCount > 65))
			{
				up.setSuspicious(false);
			}
			if (scOnPerCount > 80)
			{
				up.setSuspicious(false);
			}
			if (gpsAppsPerCount + audio > 80 || gpsAppsPerCount + mediaPerCount > 80)
			{
				up.setSuspicious(false);
			}
			if (gpsPerMin > 35)
			{
				up.setSuspicious(false);
			}
			
			Logger.log(Logger.TAG_BTD_PARSER, "\nEntries read: " + count);
			Logger.log(Logger.TAG_BTD_PARSER, "Entries null: " + nulls);
			Logger.log(Logger.TAG_BTD_PARSER, "Suspicious: " + up.isSuspicious());
			Logger.log(Logger.TAG_BTD_PARSER, "\nCellTX: " + deltaCTx + " - " + formatNumber(ctxPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "CellRX: " + deltaCRx + " - " + formatNumber(crxPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "WifiTX: " + deltaWTx + " - " + formatNumber(wtxPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "WifiRX: " + deltaWRx + " - " + formatNumber(wrxPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "GPS Location: " + deltaGps + " - " + formatNumber(gpsPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "Network Location: " + deltaNetLocation + " - " + formatNumber(netLocationPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "Audio: " + audio + " - " + audioPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "MediaServer: " + mediaServer + " - " + mediaPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "Bluetooth: " + bluetooth + " - " + btPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "GPS: " + gps + " - " + gpsAppsPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "Games: " + games + " - " + gamesPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "Camera: " + camera + " - " + cameraPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "Video: " + videoFootage + " - " + videoPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "ScreenOn: " + screenOn + " - " + scOnPerCount + "%");
			
			Logger.log(Logger.TAG_BTD_PARSER, "Apps list size: " + appsList.size());
			
			appsList.sortByCpu();
			Logger.log(Logger.TAG_BTD_PARSER, "Top Apps by CPU Time:");
			for (int i = 0; i < 5; i++)
			{
				Logger.log(Logger.TAG_BTD_PARSER, appsList.get(i) + " - CPU%: " + (100.0 * appsList.get(i).getDeltaCpuTime() / appsList.totalCpu));
			}
			
			Logger.log(Logger.TAG_BTD_PARSER, "Top Apps by DataTraffic:");
			appsList.sortByTotalData();
			for (int i = 0; i < 5; i++)
			{
				if (i < 4 && appsList.get(i + 1).getDeltaTx() + appsList.get(i + 1).getDeltaRx() == 0)
				{
					break;
				}
				Logger.log(Logger.TAG_BTD_PARSER, appsList.get(i) + " - TX%: " + (100.0 * appsList.get(i).getDeltaTx() / appsList.totalTx) + " - RX%: "
				                                  + (100.0 * appsList.get(i).getDeltaRx() / appsList.totalRx));
			}
			
			if ((crxPerMin + wrxPerMin) > 500 && scOnPerCount > 70 && audioPerCount < 20)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Online apps usage");
			}
			if ((audioPerCount > 70 || mediaPerCount > 65) && btPerCount > 40)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Streaming audio using bluetooth");
			}
			if (((ctxPerMin + wtxPerMin) > 500 || (crxPerMin + wrxPerMin) > 500) && gpsPerMin > 10 && scOnPerCount > 70)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Navigation apps usage");
			}
			if (((ctxPerMin + wtxPerMin) > 500 || (crxPerMin + wrxPerMin) > 500) && gpsPerMin > 10 && scOnPerCount < 35)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Fitness/Trackers apps usage");
			}
			if ((mediaPerCount > 70 || audioPerCount > 70) && gpsPerMin < 10 && scOnPerCount < 35)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Audio in background usage");
			}
			else if (mediaPerCount > scOnPerCount)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Audio in background for while");
			}
			if (((ctxPerMin + wtxPerMin) > 500 || (crxPerMin + wrxPerMin) > 450) && mediaPerCount > 49 && gpsPerMin < 10 && scOnPerCount > 65)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Heavy apps plus audio service usage: Chrome, Facebook, Youtube, Whatsapp etc");
			}
			else if (((ctxPerMin + wtxPerMin) > 500 || (crxPerMin + wrxPerMin) > 450) && gpsPerMin < 10 && scOnPerCount > 65)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Heavy Apps usage, Chrome, Facebook, Youtube etc");
			}
			if (((ctxPerMin + wtxPerMin) > 500 || (crxPerMin + wrxPerMin) > 700) && gpsPerMin < 10 && scOnPerCount > 70 && videoFootage > 50)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Video streaming (Hangouts etc)");
			}
			if (videoPerCount > 60 && scOnPerCount > 70)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Video footage");
			}
			if (scOnPerCount > 90)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-90%+ screen on uptime");
			}
			
			Logger.log(Logger.TAG_BTD_PARSER, "\n----------------------------------------------------------------");
			
			up.setAppsData(appsList);
		}
		
		Logger.log(Logger.TAG_BTD_PARSER, "Analizing screen off uptimes");
		uptimesCount = 0;
		for (BtdUptimePeriod up : getUptimesScOff())
		{
			uptimesCount++;
			audio = 0;
			gps = 0;
			games = 0;
			screenOn = 0;
			mediaServer = 0;
			bluetooth = 0;
			camera = 0;
			videoFootage = 0;
			count = 0;
			nulls = 0;
			deltaWRx = 0;
			deltaWTx = 0;
			deltaCRx = 0;
			deltaCTx = 0;
			deltaGps = 0;
			deltaNetLocation = 0;
			time = (float) (up.getDuration() / 60000.0);
			BtdAppList appsList = new BtdAppList();
			
			Logger.log(Logger.TAG_BTD_PARSER, "Uptime " + uptimesCount + " start: " + BtdParser.formatDate(BtdParser.generateDate(up.getStart())));
			Logger.log(Logger.TAG_BTD_PARSER, "Uptime " + uptimesCount + " end: " + BtdParser.formatDate(BtdParser.generateDate(up.getEnd())));
			Logger.log(Logger.TAG_BTD_PARSER, "Uptime " + uptimesCount + " duration: " + DateTimeOperator.getTimeStringFromMillis(up.getDuration()));
			Logger.log(Logger.TAG_BTD_PARSER, "Time Duration in minutes: " + formatNumber(time));
			
			for (BtdRow btdRow : getBtdRows())
			{
				if (btdRow.getTimestamp() >= up.getStart() && btdRow.getTimestamp() <= up.getEnd())
				{
					if (btdRow.getTimestamp() == up.getStart())
					{
						// divide by 1024 to convert to KB
						deltaWRx = btdRow.getWifiRx() / 1024;
						deltaWTx = btdRow.getWifiTx() / 1024;
						deltaCRx = btdRow.getCellRx() / 1024;
						deltaCTx = btdRow.getCellTx() / 1024;
						deltaGps = btdRow.getGpsLocationUpdates();
						deltaNetLocation = btdRow.getNetworkLocationUpdates();
						
						String perUidApps[] = btdRow.getPerUidData().split("\\|");
						Logger.log(Logger.TAG_BTD_PARSER, "Initial perUid len: " + perUidApps.length);
						Logger.log(Logger.TAG_BTD_PARSER, "Initial deltaCTX: " + deltaCTx);
						for (String a : perUidApps)
						{
							String appParts[] = a.split(":");
							if (!appParts[0].contains("unknown") && appParts.length >= 7)
								appsList.update(appParts[0], appParts[1], Long.parseLong(appParts[2]), Long.parseLong(appParts[3]), Long.parseLong(appParts[4]));
						}
					}
					else if (btdRow.getTimestamp() == up.getEnd())
					{
						// divide by 1024 to convert to KB
						deltaWRx = btdRow.getWifiRx() / 1024 - deltaWRx;
						deltaWTx = btdRow.getWifiTx() / 1024 - deltaWTx;
						deltaCRx = btdRow.getCellRx() / 1024 - deltaCRx;
						deltaCTx = btdRow.getCellTx() / 1024 - deltaCTx;
						deltaGps = btdRow.getGpsLocationUpdates() - deltaGps;
						deltaNetLocation = btdRow.getNetworkLocationUpdates() - deltaNetLocation;
						
						String perUidApps[] = btdRow.getPerUidData().split("\\|");
						Logger.log(Logger.TAG_BTD_PARSER, "Final perUid len: " + perUidApps.length);
						Logger.log(Logger.TAG_BTD_PARSER, "Final deltaCTX: " + deltaCTx);
						for (String a : perUidApps)
						{
							String appParts[] = a.split(":");
							if (!appParts[0].contains("unknown") && appParts.length >= 7)
								appsList.update(appParts[0], appParts[1], Long.parseLong(appParts[2]), Long.parseLong(appParts[3]), Long.parseLong(appParts[4]));
						}
					}
					else
					{
						String perUidApps[] = btdRow.getPerUidData().split("\\|");
						for (String a : perUidApps)
						{
							String appParts[] = a.split(":");
							if (!appParts[0].contains("unknown") && appParts.length >= 7)
								appsList.update(appParts[0], appParts[1], Long.parseLong(appParts[2]), Long.parseLong(appParts[3]), Long.parseLong(appParts[4]));
						}
					}
					
					if (btdRow.getTopProcesses().equals("null") || btdRow.getTopProcesses().equals(""))
					{
						nulls++;
						if (lastRow == null)
						{
							continue;
						}
						if (AppsChecker.isAudioService(lastRow.getTopProcesses()))
						{
							mediaServer++;
						}
						if (AppsChecker.isCameraService(lastRow.getTopProcesses()))
						{
							camera++;
						}
						if (AppsChecker.isCameraService(lastRow.getTopProcesses()) && AppsChecker.isAudioService(lastRow.getTopProcesses()))
						{
							videoFootage++;
						}
						if (AppsChecker.isBTService(lastRow.getTopProcesses()))
						{
							bluetooth++;
						}
						if (AppsChecker.isAudioApp(lastRow.getTopProcesses()))
						{
							audio++;
						}
						if (AppsChecker.isGpsApp(lastRow.getTopProcesses()))
						{
							gps++;
						}
						if (AppsChecker.isGameApp(lastRow.getTopProcesses()))
						{
							games++;
						}
						if (lastRow.getScreenOn() == 0)
						{
							screenOn++;
						}
					}
					else
					{
						if (AppsChecker.isAudioApp(btdRow.getTopProcesses()))
						{
							audio++;
						}
						if (AppsChecker.isGpsApp(btdRow.getTopProcesses()))
						{
							gps++;
						}
						if (AppsChecker.isGameApp(btdRow.getTopProcesses()))
						{
							games++;
						}
						if (btdRow.getScreenOn() == 0)
						{
							screenOn++;
						}
						if (AppsChecker.isBTService(btdRow.getTopProcesses()))
						{
							bluetooth++;
						}
						
						lastRow = btdRow;
					}
					
					count++;
				}
			}
			
			appsList.generateTotals();
			
			float ctxPerMin, crxPerMin, wtxPerMin, wrxPerMin, gpsPerMin, netLocationPerMin, audioPerCount, mediaPerCount, btPerCount, gpsAppsPerCount, gamesPerCount, cameraPerCount, videoPerCount, scOnPerCount;
			ctxPerMin = deltaCTx / time;
			crxPerMin = deltaCRx / time;
			wtxPerMin = deltaWTx / time;
			wrxPerMin = deltaWRx / time;
			gpsPerMin = deltaGps / time;
			netLocationPerMin = deltaNetLocation / time;
			audioPerCount = (float) (audio * 100.0 / count);
			mediaPerCount = (float) (mediaServer * 100.0 / count);
			btPerCount = (float) (bluetooth * 100.0 / count);
			gpsAppsPerCount = (float) (gps * 100.0 / count);
			gamesPerCount = (float) (games * 100.0 / count);
			cameraPerCount = (float) (camera * 100.0 / count);
			videoPerCount = (float) (videoFootage * 100.0 / count);
			scOnPerCount = (float) (screenOn * 100.0 / count);
			
			if ((audioPerCount > 80 || mediaPerCount > 65))
			{
				up.setSuspicious(false);
			}
			if (scOnPerCount > 80)
			{
				up.setSuspicious(false);
			}
			if (gpsAppsPerCount + audio > 80 || gpsAppsPerCount + mediaPerCount > 80)
			{
				up.setSuspicious(false);
			}
			if (gpsPerMin > 35)
			{
				up.setSuspicious(false);
			}
			
			Logger.log(Logger.TAG_BTD_PARSER, "\nEntries read: " + count);
			Logger.log(Logger.TAG_BTD_PARSER, "Entries null: " + nulls);
			Logger.log(Logger.TAG_BTD_PARSER, "Suspicious: " + up.isSuspicious());
			Logger.log(Logger.TAG_BTD_PARSER, "\nCellTX: " + deltaCTx + " - " + formatNumber(ctxPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "CellRX: " + deltaCRx + " - " + formatNumber(crxPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "WifiTX: " + deltaWTx + " - " + formatNumber(wtxPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "WifiRX: " + deltaWRx + " - " + formatNumber(wrxPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "GPS Location: " + deltaGps + " - " + formatNumber(gpsPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "Network Location: " + deltaNetLocation + " - " + formatNumber(netLocationPerMin) + " per minute");
			Logger.log(Logger.TAG_BTD_PARSER, "Audio: " + audio + " - " + audioPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "MediaServer: " + mediaServer + " - " + mediaPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "Bluetooth: " + bluetooth + " - " + btPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "GPS: " + gps + " - " + gpsAppsPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "Games: " + games + " - " + gamesPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "Camera: " + camera + " - " + cameraPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "Video: " + videoFootage + " - " + videoPerCount + "%");
			Logger.log(Logger.TAG_BTD_PARSER, "ScreenOn: " + screenOn + " - " + scOnPerCount + "%");
			
			Logger.log(Logger.TAG_BTD_PARSER, "Apps list size: " + appsList.size());
			
			appsList.sortByCpu();
			Logger.log(Logger.TAG_BTD_PARSER, "Top Apps by CPU Time:");
			for (int i = 0; i < 5; i++)
			{
				Logger.log(Logger.TAG_BTD_PARSER, appsList.get(i) + " - CPU%: " + (100 * appsList.get(i).getDeltaCpuTime() / appsList.totalCpu));
			}
			
			Logger.log(Logger.TAG_BTD_PARSER, "Top Apps by DataTraffic:");
			appsList.sortByTotalData();
			for (int i = 0; i < 5; i++)
			{
				if (i < 4 && appsList.get(i + 1).getDeltaTx() + appsList.get(i + 1).getDeltaRx() == 0)
				{
					break;
				}
				Logger.log(Logger.TAG_BTD_PARSER, appsList.get(i) + " - TX%: " + (100 * appsList.get(i).getDeltaTx() / appsList.totalTx) + " - RX%: "
				                                  + (100 * appsList.get(i).getDeltaRx() / appsList.totalRx));
			}
			
			if ((crxPerMin + wrxPerMin) > 500 && scOnPerCount > 70 && audioPerCount < 20)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Online apps usage");
			}
			if ((audioPerCount > 70 || mediaPerCount > 65) && btPerCount > 40)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Streaming audio using bluetooth");
			}
			if (((ctxPerMin + wtxPerMin) > 500 || (crxPerMin + wrxPerMin) > 500) && gpsPerMin > 10 && scOnPerCount > 70)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Navigation apps usage");
			}
			if (((ctxPerMin + wtxPerMin) > 500 || (crxPerMin + wrxPerMin) > 500) && gpsPerMin > 10 && scOnPerCount < 35)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Fitness/Trackers apps usage");
			}
			if ((mediaPerCount > 70 || audioPerCount > 70) && gpsPerMin < 10 && scOnPerCount < 35)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Audio in background usage");
			}
			else if (mediaPerCount > scOnPerCount)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Audio in background for while");
			}
			if (((ctxPerMin + wtxPerMin) > 500 || (crxPerMin + wrxPerMin) > 450) && mediaPerCount > 49 && gpsPerMin < 10 && scOnPerCount > 65)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Heavy apps plus audio service usage: Chrome, Facebook, Youtube, Whatsapp etc");
			}
			else if (((ctxPerMin + wtxPerMin) > 500 || (crxPerMin + wrxPerMin) > 450) && gpsPerMin < 10 && scOnPerCount > 65)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Heavy Apps usage, Chrome, Facebook, Youtube etc");
			}
			if (((ctxPerMin + wtxPerMin) > 500 || (crxPerMin + wrxPerMin) > 700) && gpsPerMin < 10 && scOnPerCount > 70 && videoFootage > 50)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Video streaming (Hangouts etc)");
			}
			if (videoPerCount > 60 && scOnPerCount > 70)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-Video footage");
			}
			if (scOnPerCount > 90)
			{
				Logger.log(Logger.TAG_BTD_PARSER, "-90%+ screen on uptime");
			}
			
			Logger.log(Logger.TAG_BTD_PARSER, "\n----------------------------------------------------------------");
		}
		
		for (int i = 0; i < uptimes.size(); i++)
		{
			if (uptimes.get(i).isSuspicious() == false)
			{
				uptimes.remove(i);
				i--;
			}
		}
		
		for (int i = 0; i < uptimesScOff.size(); i++)
		{
			if (uptimesScOff.get(i).isSuspicious() == false)
			{
				uptimesScOff.remove(i);
				i--;
			}
		}
	}
	
	public void getPeriods()
	{
		try
		{
			BtdState btdState = new BtdState();
			int rowid = 0;
			rs = null;
			
			do
			{
				// Pega valor inicial
				Logger.log(Logger.TAG_BTD_PARSER, "- Getting BTD periods - Exec'ing query");
				rs = execQuery("select timestamp, PLUG_TYPE, rowid from t_fgdata where rowid > " + rowid + " ORDER BY timestamp ASC LIMIT 1;");
				
				if (rs != null)
				{
					btdState = new BtdState();
					btdState.setStart(rs.getLong(1));
					btdState.setStatus(rs.getInt(2));
					rowid = rs.getInt(3);
					
					// Busca uma mudança de estado
					if (btdState.getStatus() == 0)
					{
						Logger.log(Logger.TAG_BTD_PARSER, "Exec'ing 1");
						rs = execQuery("select timestamp, rowid from t_fgdata where rowid = (select (rowid-1) from t_fgdata where rowid > " + rowid
						               + " AND PLUG_TYPE != 0 ORDER BY timestamp ASC LIMIT 1);");
					}
					else
					{
						Logger.log(Logger.TAG_BTD_PARSER, "Exec'ing 2:");
						rs = execQuery("select timestamp, rowid from t_fgdata where rowid = (select (rowid-1) from t_fgdata where rowid > " + rowid
						               + " AND PLUG_TYPE = 0 ORDER BY timestamp ASC LIMIT 1);");
					}
					
					// Se existe mudança, pega o valor final do estado atual, adiciona o estado na lista e busca pelo novo estado.
					if (!rs.isAfterLast() || !rs.isClosed())
					{
						btdState.setEnd(rs.getLong(1));
						rowid = rs.getInt(2);
						statesData.add(btdState);
						Logger.log(Logger.TAG_BTD_PARSER, "Getting BTD periods - " + rs.getLong(1));
					}
					// Se nao existir mais mudanças de estado, finaliza o atual com o ponto final do log e para a busca.
					else
					{
						rs = execQuery("select timestamp, rowid from t_fgdata where rowid = (select MAX(rowid) as oldRow from t_fgdata);");
						btdState.setEnd(rs.getLong(1));
						rowid = rs.getInt(2);
						
						if (btdState.getEnd() < btdState.getStart() || btdState.getDuration() < 0)
						{
							SharedObjs.crsManagerPane.addLogLine("This BTD contains data errors. Some inconsistent periods will be ignored.");
							SharedObjs.crsManagerPane.addLogLine("Is recommended to check it for date errors and periods without data.");
							rs = null;
						}
						else
						{
							statesData.add(btdState);
							// rs.close();
							rs = null;
						}
					}
					
					Logger.log(Logger.TAG_BTD_PARSER, "ROW: \t" + rowid);
					Logger.log(Logger.TAG_BTD_PARSER, "START:\t" + btdState.getStart());
					Logger.log(Logger.TAG_BTD_PARSER, "END: \t" + btdState.getEnd());
					Logger.log(Logger.TAG_BTD_PARSER, "DURAT:\t" + DateTimeOperator.getTimeStringFromMillis(btdState.getDuration()));
					Logger.log(Logger.TAG_BTD_PARSER, "STATUS:\t" + btdState.getStatus() + "\n-\n");
				}
				else
				{
					Logger.log(Logger.TAG_BTD_PARSER, "- BTD file is corrupted!");
				}
			}
			while (rs != null);
			
			if (statesData.size() == 0)
			{
				rs = execQuery("select MIN(timestamp), MAX(timestamp), PLUG_TYPE from t_fgdata;");
				if (rs != null)
				{
					btdState = new BtdState();
					btdState.setStart(rs.getLong(1));
					btdState.setEnd(rs.getLong(2));
					btdState.setStatus(rs.getInt(3));
					statesData.add(btdState);
				}
				else
				{
					Logger.log(Logger.TAG_BTD_PARSER, "- BTD file is corrupted!");
				}
			}
		}
		catch (SQLException e)
		{
			Logger.log(Logger.TAG_BTD_PARSER, "- BTD file SQL error:");
			Logger.log(Logger.TAG_BTD_PARSER, e.getMessage());
			e.printStackTrace();
		}
	}
	
	public BtdState getLongerDischargingPeriod()
	{
		if (statesData.size() == 0)
			getPeriods();
		
		System.out.println("");
		
		return statesData.getLongerDischargingPeriod();
	}
	
	public BtdWL getLongerWakeLock()
	{
		return kernelWLs.getLongerWL();
	}
	
	public BtdUptimePeriod getLongerUptime()
	{
		return uptimes.getLongerPeriod();
	}
	
	// }}
	
	// Supportive ----------------------------------------------------------------------------------------------
	// {{
	public double millisToHours(long millis)
	{
		return (double) (millis / 3600000.0);
	}
	
	public long getMillisFromBtdStringDate(String time)
	{
		int[] parts = timeParser(time);
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
			// e.printStackTrace();
		}
		
		return millis;
	}
	
	public String[][] getMinMaxDiffData(String[] minsMaxs, long[] fromToTimestamp)
	{
		String minMaxResults[][] = new String[minsMaxs.length + 1][3];
		int id = 0;
		
		try
		{
			for (String field : minsMaxs)
			{
				if (fromToTimestamp != null)
					rs = execQuery("SELECT MIN(" + field + "), MAX(" + field + "), (MAX(" + field + ") - MIN(" + field
					               + ")) as Diff FROM t_fgdata WHERE timestamp BETWEEN " + fromToTimestamp[0] + " AND " + fromToTimestamp[1] + ";");
				else
					rs = execQuery("SELECT MIN(" + field + "), MAX(" + field + ") FROM t_fgdata;");
				
				while (rs.next())
				{
					/*
					 * System.out.println("Campo: " + field); System.out.println("Min= " + rs.getString(1)); System.out.println("Max= " +
					 * rs.getString(2));
					 */
					
					minMaxResults[id][0] = rs.getString(1);
					minMaxResults[id][1] = rs.getString(2);
					minMaxResults[id][2] = rs.getString(3);
				}
				// System.out.println();
				
				id++;
			}
			
			// rs.close();
			// stmt.close();
			return minMaxResults;
		}
		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}
	}
	
	public String[][] getTopBottomData(String[] topBottoms, long[] fromToTimestamp)
	{
		String topBottomResults[][] = new String[topBottoms.length + 1][2];
		int id = 0;
		
		try
		{
			for (String field : topBottoms)
			{
				
				rs = execQuery("SELECT " + field + " FROM t_fgdata WHERE timestamp = " + fromToTimestamp[0] + " OR timestamp = " + fromToTimestamp[1] + ";");
				
				// System.out.println("Campo: " + field);
				
				rs.next();
				// System.out.println("Top= " + rs.getString(1));
				topBottomResults[id][0] = rs.getString(1);
				
				rs.next();
				// System.out.println("Bottom= " + rs.getString(1));
				topBottomResults[id][1] = rs.getString(1);
				
				// System.out.println();
				
				id++;
			}
			
			// rs.close();
			// stmt.close();
			return topBottomResults;
		}
		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}
	}
	
	public String dateDiff(Date dateStart, Date dateEnd)
	{
		long timestampDiff = dateEnd.getTime() - dateStart.getTime();
		
		return getDateStringFromBtdStringMillis(timestampDiff);
	}
	
	public String timeDiff(long dateStart, long dateEnd)
	{
		long timestampDiff = dateEnd - dateStart;
		
		return getDateStringFromBtdStringMillis(timestampDiff);
	}
	
	public String getDateStringFromBtdStringMillis(long timestamp)
	{
		long days, hours, minutes, seconds, millis;
		
		millis = timestamp % 1000;
		seconds = (timestamp / 1000) % 60;
		minutes = (timestamp / (60 * 1000)) % 60;
		hours = (timestamp / (60 * 60 * 1000)) % 24;
		days = timestamp / (24 * 60 * 60 * 1000);
		
		return days + "d," + hours + "h," + minutes + "m," + seconds + "s," + millis + "ms";
	}
	
	public static Date generateDate(long timestamp)
	{
		return new Date(timestamp);
	}
	
	public static String formatDate(Date date)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone(getTimeZoneName()));
		return format.format(date);
	}
	
	public static String formatDate(Date date, String timezone)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone(timezone));
		return format.format(date);
	}
	
	public int[] timeParser(String stime)
	{
		stime = stime.replaceAll(" \\(.+", "");
		String timeParts[] = stime.split(",");
		int time[] = new int[timeParts.length];
		
		for (int i = 0; i < timeParts.length; i++)
		{
			time[timeParts.length - i - 1] = Integer.parseInt(timeParts[i].replaceAll("[a-z]", ""));
		}
		
		return time;
	}
	
	private float getPercentage(long percentageOf, long from)
	{
		return (float) (100.0 * percentageOf / from);
	}
	
	private String formatNumber(float number)
	{
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(number);
	}
	
	public long getTimeZoneMillis()
	{
		try
		{
			long timezone = -1;
			
			rs = execQuery("SELECT VALUE FROM t_phoneinfo WHERE NAME LIKE 'TZ_CURRENT_OFFSET';");
			
			while (rs.next())
			{
				timezone = rs.getLong(1);
			}
			
			// rs.close();
			// stmt.close();
			
			return timezone;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public static String getTimeZoneName()
	{
		try
		{
			String timezone;
			
			Statement localStmt;
			localStmt = c.createStatement();
			ResultSet rs = localStmt.executeQuery("SELECT VALUE FROM t_phoneinfo WHERE NAME LIKE 'TZ_ID';");
			if (!rs.isClosed() && rs.getString(1) != null)
			{
				timezone = rs.getString(1);
				return timezone;
			}
			
			return "0";
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "0";
		}
	}
	
	public ResultSet execQuery(String query)
	{
		try
		{
			if (!stmt.isClosed())
				stmt.close();
			
			stmt = c.createStatement();
			rs = stmt.executeQuery(query);
			
			return rs;
		}
		catch (SQLException sqlE)
		{
			sqlE.printStackTrace();
			Logger.log(Logger.TAG_BTD_PARSER, "Error exec'ing query:");
			Logger.log(Logger.TAG_BTD_PARSER, sqlE.getMessage());
			
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.log(Logger.TAG_BTD_PARSER, "Error exec'ing query:");
			Logger.log(Logger.TAG_BTD_PARSER, e.getMessage());
			
			return null;
		}
	}
	
	public void close()
	{
		try
		{
			rs.close();
			stmt.close();
			c.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setupBtdRow(BtdRow btd, ResultSet rs, long timezone) throws SQLException
	{
		btd.setActiveKernels(rs.getString("ActiveKernelWL"));
		btd.setAudioTime(rs.getString("AudioTime"));
		btd.setAwakeTimeOnBattery(rs.getString("AwakeTimeOnBatt"));
		btd.setAwakeTimeTotal(rs.getString("AwakeTimeTotal"));
		btd.setBacklightIntensity(rs.getInt("BacklightIntensity"));
		btd.setBatteryCapacity(rs.getInt("FCC"));
		btd.setBatteryLevel(rs.getInt("BATTERY_LEVEL"));
		btd.setChargerPlugged(rs.getInt("PLUG_TYPE"));
		btd.setDate(new Date(rs.getLong("timestamp") + timezone));
		btd.setForegroundActivity(rs.getString("FgActivity"));
		btd.setForegroundPackage(rs.getString("FgPackage"));
		btd.setGpsLocationUpdates(rs.getLong("GpsLocCount"));
		btd.setInstantCurrent(rs.getLong("CurrentNow"));
		btd.setNetworkLocationUpdates(rs.getLong("NetworkLocCount"));
		btd.setPerUidData(rs.getString("PerUidData"));
		btd.setPhoneCallTime(rs.getString("ActivePhoneCallTime"));
		btd.setRealTimeOnBattery(rs.getString("RealTimeOnBatt"));
		btd.setRealTimeTotal(rs.getString("RealTimeTotal"));
		btd.setScreenBrightnesses(rs.getString("ScreenBrightnesses"));
		btd.setScreenOn(rs.getInt("ScreenOn"));
		btd.setScreenOnTime(rs.getString("ScreenOnTime"));
		btd.setSignalLevels(rs.getString("SignalLevels"));
		btd.setTemperature(rs.getInt("TEMP"));
		btd.setTimestamp(rs.getLong("timestamp") + timezone);
		btd.setTopProcesses(rs.getString("TopData"));
		btd.setWifiOnTime(rs.getString("WifiOnTime"));
		btd.setCellTx(rs.getLong("CELL_RX"));
		btd.setCellRx(rs.getLong("CELL_TX"));
		btd.setWifiTx(rs.getLong("WIFI_RX"));
		btd.setWifiRx(rs.getLong("WIFI_TX"));
	}
	
	// }}
	
	// Printers ------------------------------------------------------------------------------------------------
	// {{
	public void showParseResults()
	{
		System.out.println("The longer discharge period is from " + finalState.getStart() + " (" + formatDate(finalState.getStartDate()) + ") to "
		                   + finalState.getEnd() + " (" + formatDate(finalState.getEndDate()) + ")\nA total time of "
		                   + dateDiff(finalState.getStartDate(), finalState.getEndDate()) + " or "
		                   + getMillisFromBtdStringDate(dateDiff(finalState.getStartDate(), finalState.getEndDate())) + "ms\n");
		System.out.println("Cell Rx: " + cellRX + " KBytes  ||  Cell TX: " + cellTX + " KBytes");
		System.out.println("Wifi Rx: " + wifiRX + " KBytes  ||  Wifi TX: " + wifiTX + " KBytes");
		System.out.println("Btt: " + bttDischarged[0] + "% --> " + bttDischarged[1] + "%");
		System.out.println("GPS Location: " + gpsLocation);
		System.out.println("Network Location: " + networkLocation);
		System.out.println("Screen ON: " + getDateStringFromBtdStringMillis(timeOn) + " or " + timeOn + "ms");
		System.out.println("Screen OFF: " + getDateStringFromBtdStringMillis(timeOff) + " or " + timeOff + "ms");
		System.out.println("Total time: " + getDateStringFromBtdStringMillis(finalState.getDuration()) + " or " + finalState.getDuration() + "ms");
		System.out.println("Phonecalls time: " + getDateStringFromBtdStringMillis(phoneCall) + " - " + phoneCall + "ms");
		System.out.println("Time on battery: " + getDateStringFromBtdStringMillis(realTimeOnBatt) + " - " + realTimeOnBatt + "ms");
		System.out.println("Time awake: " + getDateStringFromBtdStringMillis(awakeTimeOnBatt) + " - " + awakeTimeOnBatt + "ms");
		System.out.println("Wifi On time: " + getDateStringFromBtdStringMillis(wifiOnTime) + " - " + wifiOnTime + "ms");
		System.out.println("Wifi Running time: " + getDateStringFromBtdStringMillis(wifiRunningTime) + " - " + wifiRunningTime + "ms");
		
		System.out.println("Total discharge ms: " + (timeOn + timeOff));
		System.out.println("Total discharge hours: " + (millisToHours(timeOn) + millisToHours(timeOff)));
		System.out.println("Total discharge capacity: " + (consumeOn + consumeOff));
		
		System.out.print("Total On mAh: " + consumeOn + " - Total ms: " + timeOn);
		System.out.println(" - Average On mAh: " + getAverageconsumeOn() + " - " + millisToHours(timeOn));
		
		System.out.print("Total Off mAh: " + consumeOff + " - Total ms: " + timeOff);
		System.out.println(" - Average Off mAh: " + getAverageconsumeOff() + " - " + millisToHours(timeOff));
		
		System.out.println("Signal data:");
		System.out.println("\tnone: \t\t" + millisToHours(signalData[0]) + "\t" + getDateStringFromBtdStringMillis(signalData[0]));
		System.out.println("\tpoor: \t\t" + millisToHours(signalData[1]) + "\t" + getDateStringFromBtdStringMillis(signalData[1]));
		System.out.println("\tmoderate:\t" + millisToHours(signalData[2]) + "\t" + getDateStringFromBtdStringMillis(signalData[2]));
		System.out.println("\tgood: \t\t" + millisToHours(signalData[3]) + "\t" + getDateStringFromBtdStringMillis(signalData[3]));
		System.out.println("\tgreat: \t\t" + millisToHours(signalData[4]) + "\t" + getDateStringFromBtdStringMillis(signalData[4]));
		
		System.out.println("Screen brightnesses:");
		System.out.println("\tdark: \t\t" + millisToHours(screenData[0]) + "\t" + getDateStringFromBtdStringMillis(screenData[0]));
		System.out.println("\tdim: \t\t" + millisToHours(screenData[1]) + "\t" + getDateStringFromBtdStringMillis(screenData[1]));
		System.out.println("\tmedium:\t\t" + millisToHours(screenData[2]) + "\t" + getDateStringFromBtdStringMillis(screenData[2]));
		System.out.println("\tlight: \t\t" + millisToHours(screenData[3]) + "\t" + getDateStringFromBtdStringMillis(screenData[3]));
		System.out.println("\tbright:\t\t" + millisToHours(screenData[4]) + "\t" + getDateStringFromBtdStringMillis(screenData[4]));
		
		// Specific detections
		System.out.println("Tethering time: " + tetheringTime + " " + getDateStringFromBtdStringMillis(tetheringTime) + "\n");
	}
	
	public void printResultToFile() throws IOException
	{
		File output = new File(path + "/BTD output.txt");
		BufferedWriter br = new BufferedWriter(new FileWriter(output));
		
		br.write("The longer discharge period is from " + finalState.getStart() + " (" + formatDate(finalState.getStartDate()) + ") to " + finalState.getEnd()
		         + " (" + formatDate(finalState.getEndDate()) + ")\nA total time of " + dateDiff(finalState.getStartDate(), finalState.getEndDate()) + " or "
		         + getMillisFromBtdStringDate(dateDiff(finalState.getStartDate(), finalState.getEndDate())) + "ms\n" + "\n");
		br.write("Cell Rx: " + cellRX + " KBytes  ||  Cell TX: " + cellTX + " KBytes" + "\n");
		br.write("Wifi Rx: " + wifiRX + " KBytes  ||  Wifi TX: " + wifiTX + " KBytes" + "\n");
		br.write("Btt: " + bttDischarged[0] + "% --> " + bttDischarged[1] + "%" + "\n");
		br.write("GPS Location: " + gpsLocation + "\n");
		br.write("Network Location: " + networkLocation + "\n");
		br.write("Screen ON: " + getDateStringFromBtdStringMillis(timeOn) + " or " + timeOn + "ms" + "\n");
		br.write("Screen OFF: " + getDateStringFromBtdStringMillis(timeOff) + " or " + timeOff + "ms" + "\n");
		br.write("Total time: " + getDateStringFromBtdStringMillis(finalState.getDuration()) + " or " + finalState.getDuration() + "ms" + "\n");
		br.write("Phonecalls time: " + getDateStringFromBtdStringMillis(phoneCall) + " - " + phoneCall + "ms" + "\n");
		br.write("Time on battery: " + getDateStringFromBtdStringMillis(realTimeOnBatt) + " - " + realTimeOnBatt + "ms" + "\n");
		br.write("Time awake: " + getDateStringFromBtdStringMillis(awakeTimeOnBatt) + " - " + awakeTimeOnBatt + "ms" + "\n");
		br.write("Wifi On time: " + getDateStringFromBtdStringMillis(wifiOnTime) + " - " + wifiOnTime + "ms" + "\n");
		br.write("Wifi Running time: " + getDateStringFromBtdStringMillis(wifiRunningTime) + " - " + wifiRunningTime + "ms" + "\n");
		
		br.write("Total discharge ms: " + (timeOn + timeOff) + "\n");
		br.write("Total discharge hours: " + (millisToHours(timeOn) + millisToHours(timeOff)) + "\n");
		br.write("Total discharge capacity: " + (consumeOn + consumeOff) + "\n");
		
		br.write("Total On mAh: " + consumeOn + " - Total ms: " + timeOn + "\n");
		br.write("Average On mAh: " + getAverageconsumeOn() + " - " + millisToHours(timeOn) + "\n");
		
		br.write("Total Off mAh: " + consumeOff + " - Total ms: " + timeOff + "\n");
		br.write("Average Off mAh: " + getAverageconsumeOff() + " - " + millisToHours(timeOff) + "\n");
		
		br.write("Signal data:" + "\n");
		br.write("\tnone: \t\t" + millisToHours(signalData[0]) + "\t" + getDateStringFromBtdStringMillis(signalData[0]) + "\n");
		br.write("\tpoor: \t\t" + millisToHours(signalData[1]) + "\t" + getDateStringFromBtdStringMillis(signalData[1]) + "\n");
		br.write("\tmoderate:\t" + millisToHours(signalData[2]) + "\t" + getDateStringFromBtdStringMillis(signalData[2]) + "\n");
		br.write("\tgood: \t\t" + millisToHours(signalData[3]) + "\t" + getDateStringFromBtdStringMillis(signalData[3]) + "\n");
		br.write("\tgreat: \t\t" + millisToHours(signalData[4]) + "\t" + getDateStringFromBtdStringMillis(signalData[4]) + "\n");
		
		br.write("Screen brightnesses:" + "\n");
		br.write("\tdark: \t\t" + millisToHours(screenData[0]) + "\t" + getDateStringFromBtdStringMillis(screenData[0]) + "\n");
		br.write("\tdim: \t\t" + millisToHours(screenData[1]) + "\t" + getDateStringFromBtdStringMillis(screenData[1]) + "\n");
		br.write("\tmedium:\t\t" + millisToHours(screenData[2]) + "\t" + getDateStringFromBtdStringMillis(screenData[2]) + "\n");
		br.write("\tlight: \t\t" + millisToHours(screenData[3]) + "\t" + getDateStringFromBtdStringMillis(screenData[3]) + "\n");
		br.write("\tbright:\t\t" + millisToHours(screenData[4]) + "\t" + getDateStringFromBtdStringMillis(screenData[4]) + "\n");
		
		// Specific detections
		br.write("Tethering time: " + tetheringTime + " " + getDateStringFromBtdStringMillis(tetheringTime) + "\n");
		
		br.close();
	}
	
	public String parseResult()
	{
		String data = "";
		
		data = data + "The longer discharge period is from " + finalState.getStart() + " (" + formatDate(finalState.getStartDate()) + ") to "
		       + finalState.getEnd() + " (" + formatDate(finalState.getEndDate()) + ")\nA total time of "
		       + dateDiff(finalState.getStartDate(), finalState.getEndDate()) + " or "
		       + getMillisFromBtdStringDate(dateDiff(finalState.getStartDate(), finalState.getEndDate())) + "ms\n" + "\n";
		
		data = data + "Time awake: " + getDateStringFromBtdStringMillis(awakeTimeOnBatt) + " - " + awakeTimeOnBatt + "ms" + "\n";
		data = data + "Total discharged capacity: " + (consumeOn + consumeOff) + "\n";
		data = data + "Battery from/to: " + bttDischarged[0] + "% --> " + bttDischarged[1] + "%" + "\n";
		
		// data = data + "Total On mAh: " + consumeOn + " - Total ms: " + timeOn + "\n";
		data = data + "Average Screen On mAh: " + getAverageconsumeOn() + " - " + getDateStringFromBtdStringMillis(timeOn) + "\n";
		
		// data = data + "Total Off mAh: " + consumeOff + " - Total ms: " + timeOff + "\n";
		data = data + "Average Screen Off mAh: " + getAverageconsumeOff() + " - " + getDateStringFromBtdStringMillis(timeOff) + "\n";
		
		data = data + "Cell Rx: " + cellRX + " KBytes  ||  Cell TX: " + cellTX + " KBytes" + "\n";
		data = data + "Wifi Rx: " + wifiRX + " KBytes  ||  Wifi TX: " + wifiTX + " KBytes" + "\n";
		data = data + "GPS Location: " + gpsLocation + "\n";
		data = data + "Network Location: " + networkLocation + "\n";
		
		data = data + "Phonecalls time: " + getDateStringFromBtdStringMillis(phoneCall) + " - " + phoneCall + "ms" + "\n";
		
		data = data + "Wifi Running time: " + getDateStringFromBtdStringMillis(wifiRunningTime) + " - " + wifiRunningTime + "ms" + "\n";
		
		data = data + "Tethering time: " + tetheringTime + " " + getDateStringFromBtdStringMillis(tetheringTime) + "\n";
		
		data = data + "Signal data:" + "\n";
		data = data + "\tnone: \t\t" + getDateStringFromBtdStringMillis(signalData[0]) + " - " + formatNumber(getPercentage(signalData[0], signalData[5]))
		       + "%\n";
		data = data + "\tpoor: \t\t" + getDateStringFromBtdStringMillis(signalData[1]) + " - " + formatNumber(getPercentage(signalData[1], signalData[5]))
		       + "%\n";
		data = data + "\tmoderate:\t" + getDateStringFromBtdStringMillis(signalData[2]) + " - " + formatNumber(getPercentage(signalData[2], signalData[5]))
		       + "%\n";
		data = data + "\tgood: \t\t" + getDateStringFromBtdStringMillis(signalData[3]) + " - " + formatNumber(getPercentage(signalData[3], signalData[5]))
		       + "%\n";
		data = data + "\tgreat: \t\t" + getDateStringFromBtdStringMillis(signalData[4]) + " - " + formatNumber(getPercentage(signalData[4], signalData[5]))
		       + "%\n";
		
		data = data + "Screen brightnesses:" + "\n";
		data = data + "\tdark: \t\t" + getDateStringFromBtdStringMillis(screenData[0]) + " - " + formatNumber(getPercentage(screenData[0], screenData[5]))
		       + "%\n";
		data = data + "\tdim: \t\t" + getDateStringFromBtdStringMillis(screenData[1]) + " - " + formatNumber(getPercentage(screenData[1], screenData[5]))
		       + "%\n";
		data = data + "\tmedium:\t\t" + getDateStringFromBtdStringMillis(screenData[2]) + " - " + formatNumber(getPercentage(screenData[2], screenData[5]))
		       + "%\n";
		data = data + "\tlight: \t\t" + getDateStringFromBtdStringMillis(screenData[3]) + " - " + formatNumber(getPercentage(screenData[3], screenData[5]))
		       + "%\n";
		data = data + "\tbright:\t\t" + getDateStringFromBtdStringMillis(screenData[4]) + " - " + formatNumber(getPercentage(screenData[4], screenData[5]))
		       + "%\n";
		
		return data;
	}
	
	public String toJiraComment()
	{
		String data = "{panel:title=*BTD Discharge Statistics*|titleBGColor=#E9F2FF}\\n{noformat}\\n";
		
		data += "The longer discharge period is from " + formatDate(finalState.getStartDate()) + " to " + formatDate(finalState.getEndDate())
		        + "\\nA total time of " + dateDiff(finalState.getStartDate(), finalState.getEndDate()) + "\\n";
		
		data += "Time awake: " + getDateStringFromBtdStringMillis(awakeTimeOnBatt) + "\\n";
		data += "Total discharged capacity: " + (consumeOn + consumeOff) + "mA\\n";
		data += "Battery from/to: " + bttDischarged[0] + "% --> " + bttDischarged[1] + "%" + "\\n";
		
		// data += "Total On mAh: " + consumeOn + " - Total ms: " + timeOn + "\\n";
		data += "Average Screen On mAh: " + getAverageconsumeOn() + "mAh for " + getDateStringFromBtdStringMillis(timeOn) + "\\n";
		
		if (getAverageconsumeOff() < 100)
		{
			data += "Average Screen Off mAh: *" + getAverageconsumeOff() + "mAh* for " + getDateStringFromBtdStringMillis(timeOff) + " --> _Low CD._\\n";
		}
		else
		{
			data += "Average Screen Off mAh: " + getAverageconsumeOff() + "mAh for " + getDateStringFromBtdStringMillis(timeOff) + "\\n";
		}
		
		data += "Cell Rx: " + cellRX + " KBytes  ||  Cell TX: " + cellTX + " KBytes" + "\\n";
		data += "Wifi Rx: " + wifiRX + " KBytes  ||  Wifi TX: " + wifiTX + " KBytes" + "\\n";
		data += "GPS Location: " + gpsLocation + "\\n";
		data += "Network Location: " + networkLocation + "\\n";
		
		data += "Phonecalls time: " + getDateStringFromBtdStringMillis(phoneCall) + "\\n";
		
		data += "Wifi Running time: " + getDateStringFromBtdStringMillis(wifiRunningTime) + "\\n";
		
		data += "Tethering time: " + getDateStringFromBtdStringMillis(tetheringTime) + "\\n";
		
		data += "Signal data:" + "\\n";
		data += "      none:             " + getDateStringFromBtdStringMillis(signalData[0]) + " - "
		        + formatNumber(getPercentage(signalData[0], signalData[5])) + "%\\n";
		data += "      poor:             " + getDateStringFromBtdStringMillis(signalData[1]) + " - "
		        + formatNumber(getPercentage(signalData[1], signalData[5])) + "%\\n";
		data += "      moderate:      " + getDateStringFromBtdStringMillis(signalData[2]) + " - " + formatNumber(getPercentage(signalData[2], signalData[5]))
		        + "%\\n";
		data += "      good:             " + getDateStringFromBtdStringMillis(signalData[3]) + " - "
		        + formatNumber(getPercentage(signalData[3], signalData[5])) + "%\\n";
		data += "      great:             " + getDateStringFromBtdStringMillis(signalData[4]) + " - "
		        + formatNumber(getPercentage(signalData[4], signalData[5])) + "%\\n";
		
		data += "Screen brightnesses:" + "\\n";
		data += "      dark:             " + getDateStringFromBtdStringMillis(screenData[0]) + " - "
		        + formatNumber(getPercentage(screenData[0], screenData[5])) + "%\\n";
		data += "      dim:             " + getDateStringFromBtdStringMillis(screenData[1]) + " - " + formatNumber(getPercentage(screenData[1], screenData[5]))
		        + "%\\n";
		data += "      medium:            " + getDateStringFromBtdStringMillis(screenData[2]) + " - "
		        + formatNumber(getPercentage(screenData[2], screenData[5])) + "%\\n";
		data += "      light:             " + getDateStringFromBtdStringMillis(screenData[3]) + " - "
		        + formatNumber(getPercentage(screenData[3], screenData[5])) + "%\\n";
		data += "      bright:            " + getDateStringFromBtdStringMillis(screenData[4]) + " - "
		        + formatNumber(getPercentage(screenData[4], screenData[5])) + "%\\n";
		
		data += "{noformat}\\n{panel}";
		
		return data;
	}
	
	public void showWakeLocks()
	{
		System.out.println("------------------------------------\n");
		for (BtdWL item : kernelWLs)
		{
			Logger.log(Logger.TAG_BTD_PARSER, item.toString());
			System.out.println("------------------------------------\n");
		}
	}
	
	public void showPeriods()
	{
		System.out.println("Periodos detectados:");
		for (BtdState btdState : statesData)
		{
			System.out.println("From: " + btdState.getStartDate() + " To: " + btdState.getEndDate() + " Status: " + btdState.getStatus());
		}
		System.out.println();
	}
	
	public void showUptimes()
	{
		int i = 1;
		System.out.println("----------------------------------------------");
		
		if (uptimes.size() == 0)
		{
			System.out.println("No uptimes detected");
			return;
		}
		
		long total = 0;
		for (BtdUptimePeriod ut : uptimes)
		{
			System.out.println("Uptime " + i);
			System.out.println(ut);
			total = total + ut.getDuration();
			System.out.println("----------------------------------------------");
			i++;
		}
		
		System.out.println("Total discharge duration: " + DateTimeOperator.getTimeStringFromMillis(realTimeOnBatt));
		System.out.println("Total long uptimes time detected: " + DateTimeOperator.getTimeStringFromMillis(total));
		System.out.println("Longer uptime: \n" + uptimes.getLongerPeriod());
		
		highUptime = uptime();
		
		if (highUptime)
			System.out.println("This log has periods where AP is ON for more than 20 minutes");
		
		System.out.println("Longer uptime percentage: " + formatNumber((float) (100.0 * uptimes.getLongerPeriod().getDuration() / realTimeOnBatt))
		                   + "% of total discharge period");
	}
	
	public void showUptimesScOff()
	{
		int i = 1;
		System.out.println("----------------------------------------------");
		
		if (uptimesScOff.size() == 0)
		{
			System.out.println("No uptimes detected");
			return;
		}
		
		long total = 0;
		for (BtdUptimePeriod ut : uptimesScOff)
		{
			System.out.println("Uptime " + i);
			System.out.println(ut);
			total = total + ut.getDuration();
			System.out.println("----------------------------------------------");
			i++;
		}
		
		System.out.println("Total discharge duration: " + DateTimeOperator.getTimeStringFromMillis(realTimeOnBatt));
		System.out.println("Total long uptimes time detected: " + DateTimeOperator.getTimeStringFromMillis(total));
		System.out.println("Longer uptime: \n" + uptimesScOff.getLongerPeriod());
		
		// highUptimeScOff = uptime();
		//
		// if (highUptime)
		// System.out.println("This log has periods where AP is ON for more than 20 minutes");
		
		System.out.println("Longer uptime percentage: " + formatNumber((float) (100.0 * uptimesScOff.getLongerPeriod().getDuration() / realTimeOnBatt))
		                   + "% of total discharge period");
	}
	
	// }}
	
	// Basic Getters and Setters -------------------------------------------------------------------------------
	// {{
	public int getStatus()
	{
		return status;
	}
	
	public BtdState getFinalState()
	{
		return finalState;
	}
	
	public BtdRowsList getBtdRows()
	{
		return btdRows;
	}
	
	public BtdStatesData getStatesData()
	{
		if (statesData.size() == 0)
			getPeriods();
		
		return statesData;
	}
	
	public long[] getScreenData()
	{
		return screenData;
	}
	
	public long[] getSignalData()
	{
		return signalData;
	}
	
	public float[] getCpuTempData()
	{
		return cpuTempData;
	}
	
	public float[] getDeviceTempData()
	{
		return deviceTempData;
	}
	
	public int[] getBttDischarged()
	{
		return bttDischarged;
	}
	
	public long getCellTX()
	{
		return cellTX;
	}
	
	public long getCellRX()
	{
		return cellRX;
	}
	
	public long getWifiTX()
	{
		return wifiTX;
	}
	
	public long getWifiRX()
	{
		return wifiRX;
	}
	
	public long getGpsLocation()
	{
		return gpsLocation;
	}
	
	public long getNetworkLocation()
	{
		return networkLocation;
	}
	
	public long getConsumeOn()
	{
		return consumeOn;
	}
	
	public long getConsumeOff()
	{
		return consumeOff;
	}
	
	public long getTimeOff()
	{
		return timeOff;
	}
	
	public long getTimeOn()
	{
		return timeOn;
	}
	
	public long getWifiOnTime()
	{
		return wifiOnTime;
	}
	
	public long getWifiRunningTime()
	{
		return wifiRunningTime;
	}
	
	public long getRealTimeOnBatt()
	{
		return realTimeOnBatt;
	}
	
	public long getAwakeTimeOnBatt()
	{
		return awakeTimeOnBatt;
	}
	
	public long getPhoneCall()
	{
		return phoneCall;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	public void setBtdRows(BtdRowsList btdRows)
	{
		this.btdRows = btdRows;
	}
	
	public void setFinalState(BtdState finalState)
	{
		this.finalState = finalState;
	}
	
	public void setStatesData(BtdStatesData statesData)
	{
		this.statesData = statesData;
	}
	
	public void setScreenData(long[] screenData)
	{
		this.screenData = screenData;
	}
	
	public void setSignalData(long[] signalData)
	{
		this.signalData = signalData;
	}
	
	public void setCpuTempData(float[] cpuTempData)
	{
		this.cpuTempData = cpuTempData;
	}
	
	public void setDeviceTempData(float[] deviceTempData)
	{
		this.deviceTempData = deviceTempData;
	}
	
	public void setBttDischarged(int[] bttDischarged)
	{
		this.bttDischarged = bttDischarged;
	}
	
	public void setCellTX(long cellTX)
	{
		this.cellTX = cellTX;
	}
	
	public void setCellRX(long cellRX)
	{
		this.cellRX = cellRX;
	}
	
	public void setWifiTX(long wifiTX)
	{
		this.wifiTX = wifiTX;
	}
	
	public void setWifiRX(long wifiRX)
	{
		this.wifiRX = wifiRX;
	}
	
	public void setGpsLocation(long gpsLocation)
	{
		this.gpsLocation = gpsLocation;
	}
	
	public void setNetworkLocation(long networkLocation)
	{
		this.networkLocation = networkLocation;
	}
	
	public void setConsumeOn(long consumeOn)
	{
		this.consumeOn = consumeOn;
	}
	
	public void setConsumeOff(long consumeOff)
	{
		this.consumeOff = consumeOff;
	}
	
	public void setTimeOff(long timeOff)
	{
		this.timeOff = timeOff;
	}
	
	public void setTimeOn(long timeOn)
	{
		this.timeOn = timeOn;
	}
	
	public void setWifiOnTime(long wifiOnTime)
	{
		this.wifiOnTime = wifiOnTime;
	}
	
	public void setWifiRunningTime(long wifiRunningTime)
	{
		this.wifiRunningTime = wifiRunningTime;
	}
	
	public void setRealTimeOnBatt(long realTimeOnBatt)
	{
		this.realTimeOnBatt = realTimeOnBatt;
	}
	
	public void setAwakeTimeOnBatt(long awakeTimeOnBatt)
	{
		this.awakeTimeOnBatt = awakeTimeOnBatt;
	}
	
	public void setPhoneCall(long phoneCall)
	{
		this.phoneCall = phoneCall;
	}
	
	public void setTetheringTime(long tetheringTime)
	{
		this.tetheringTime = tetheringTime;
	}
	
	public int getBatCap()
	{
		return batCap;
	}
	
	public void setBatCap(int batCap)
	{
		this.batCap = batCap;
	}
	
	// }}
	
	// Complementary Getters -----------------------------------------------------------------------------------
	// {{
	public int getAverageconsumeOn()
	{
		return (int) (consumeOn / (timeOn / 3600000.0));
	}
	
	public int getAverageconsumeOff()
	{
		return (int) (consumeOff / (timeOff / 3600000.0));
	}
	
	public float phoneCallPercentage()
	{
		return getPercentage(phoneCall, realTimeOnBatt);
	}
	
	public String tetherPercentage()
	{
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		return df.format(100.0 * tetheringTime / realTimeOnBatt);
	}
	
	public BtdWLList getWakeLocks()
	{
		return kernelWLs;
	}
	
	public BtdUptimesList getUptimes()
	{
		return uptimes;
	}
	
	public BtdUptimesList getUptimesScOff()
	{
		return uptimesScOff;
	}
	
	public String currentDrainStatistics()
	{
		String cdData = "{panel:title=*BTD Current drain data*|titleBGColor=#E9F2FF}\\n";
		
		cdData += "Total time on battery: " + DateTimeOperator.getTimeStringFromMillis(realTimeOnBatt) + "\\n";
		cdData += "Screen On  time: " + DateTimeOperator.getTimeStringFromMillis(timeOn) + " (" + formatNumber(getPercentage(timeOn, realTimeOnBatt)) + "%)\\n";
		if (getAverageconsumeOn() > 740 && getPercentage(timeOn, realTimeOnBatt) > 15)
		{
			cdData += "Screen On consume: *" + formatNumber(getAverageconsumeOn()) + " mAh*\\n";
		}
		else
		{
			cdData += "Screen On consume: " + formatNumber(getAverageconsumeOn()) + " mAh\\n";
		}
		cdData += "Screen Off time: " + DateTimeOperator.getTimeStringFromMillis(timeOff) + " (" + formatNumber(getPercentage(timeOff, realTimeOnBatt))
		          + "%)\\n";
		if (getAverageconsumeOff() < 100)
		{
			cdData += "Screen Off consume: *" + formatNumber(getAverageconsumeOff()) + " mAh* --> *Low* sc off consume\\n";
		}
		else
		{
			cdData += "Screen Off consume: *" + formatNumber(getAverageconsumeOff()) + " mAh* \\n";
		}
		
		cdData += "{panel}\\n";
		
		return cdData;
	}
	
	public int getThresholdInc()
	{
		return thresholdInc;
	}
	// }}
}
