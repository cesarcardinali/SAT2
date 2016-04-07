package supportive.preanalyzers.logsparser;


import java.util.Date;

import supportive.DateTimeOperator;
import supportive.preanalyzers.btdparser.BtdParser;
import supportive.preanalyzers.btdparser.BtdRow;
import supportive.preanalyzers.btdparser.BtdUptimePeriod;


public class TestIt
{
	public static void main(String[] args)
	{
		String root = "C:/CRs/Demo/";
		String crPath = root + "84623411";
		long now = System.currentTimeMillis();
		
//		BugrepParser brParser = new BugrepParser(crPath);
//		brParser.parse();
//		System.out.println();
//		brParser.showData();
		
		
		
		
		//System.out.println(DateTimeOperator.getDateStringFromBtdStringMillis(148857));
		
		BtdParser btdParser = new BtdParser(crPath);
		System.out.println("Parsing BTD data ...");
		btdParser.parse();
		int audio, count;
		BtdRow lastRow = null;
		for (BtdUptimePeriod up : btdParser.getUptimes())
		{
			audio = 0;
			count = 0;
			System.out.println(BtdParser.formatDate(BtdParser.generateDate(up.getStart())));
			System.out.println(BtdParser.formatDate(BtdParser.generateDate(up.getEnd())));
			System.out.println(DateTimeOperator.getTimeStringFromMillis(up.getDuration()));
			for (BtdRow btdRow : btdParser.getBtdRows())
			{
				if (btdRow.getTimestamp() >= up.getStart() && btdRow.getTimestamp() <= up.getEnd())
				{
					if (btdRow.getTopProcesses().contains("null"))
					{
						if (lastRow == null)
						{
							continue;
						}
						if (lastRow.getTopProcesses().contains("mediaserver")
						    || lastRow.getTopProcesses().contains("tunein")
						    || lastRow.getTopProcesses().contains("slacker")
						    || lastRow.getTopProcesses().contains("pandora")
						    || lastRow.getTopProcesses().contains("sirius")
						    || lastRow.getTopProcesses().contains("android.music")
						    || lastRow.getTopProcesses().contains("saavn")
						    || lastRow.getTopProcesses().contains("com.audible.application")
						    || lastRow.getTopProcesses().contains("spotify")
						    || lastRow.getTopProcesses().contains("fmradio"))
						{
							audio++;
						}
					}
					else
					{
						if (btdRow.getTopProcesses().contains("mediaserver")
						    || btdRow.getTopProcesses().contains("tunein")
						    || btdRow.getTopProcesses().contains("slacker")
						    || btdRow.getTopProcesses().contains("pandora")
						    || btdRow.getTopProcesses().contains("sirius")
						    || btdRow.getTopProcesses().contains("android.music")
						    || btdRow.getTopProcesses().contains("saavn")
						    || btdRow.getTopProcesses().contains("com.audible.application")
						    || btdRow.getTopProcesses().contains("spotify")
						    || btdRow.getTopProcesses().contains("fmradio"))
						{
							audio++;
						}
						
						lastRow = btdRow;
					}
					
					count++;
				}
			}
			System.out.println(audio);
			System.out.println(count);
			System.out.println("audio%" + (float)audio*100/count + "\n");
		}
//		System.out.println("-- BTD Aquired data");
//		btdParser.showParseResults();
//		btdParser.showPeriods();
//		System.out.println("-- BTD Tether data");
//		btdParser.tethering();
//		btdParser.showUptimes();
//		System.out.println("\n<><><><><><><><><><><><><><><><><><><><>\n");
//		btdParser.showUptimesScOff();
//		System.out.println("\n<><><><><><><><><><><><><><><><><><><><>\n");
//		System.out.println(btdParser.uptime());
//		System.out.println(btdParser.uptimeScOff());
//		System.out.println(btdParser.wakeLocks());
		
		//System.out.println("Longer uptime\n" + btdParser.getLongerUptime());
		//System.out.println("Longer wakelock\n" + btdParser.getLongerWakeLock());
//		btdParser.showWakeLocks();
		System.out.println("Done");
		
//		MainParser mainParser = new MainParser(crPath);
//		System.out.println("Parsing Main log data ...");
//		mainParser.parse();
//		System.out.println("-- Main Aquired data");
//		mainParser.showAcquiredData();
//		mainParser.checkForTethering();
//		System.out.println("-- Main Tether data");
//		System.out.println(mainParser.checkForTethering());
//		mainParser.showTetheringData();
//		System.out.println("Done");
		
//		BugrepParser bugrepParser = new BugrepParser(crPath);
//		System.out.println("Parsing Bugreport log data ...");
//		bugrepParser.parse();
//		System.out.println(bugrepParser.currentDrainStatistics() + "\n" + bugrepParser.eblDecreasedReasons());
//		System.out.println("Done");
		
		
		System.out.println("\n\nIt took " + DateTimeOperator.getTimeStringFromMillis((System.currentTimeMillis() - now)));
		
		
		//System.out.println("Started at " + new Date(now) + " stopped at "
		//                   + new Date(System.currentTimeMillis()));
		/*
		try
		{
			
			System.out.println();System.out.println("-----------------");System.out.println();
			
			// File seek and load configuration
			File folder = new File(root);
			File[] listOfFiles = folder.listFiles();
			
			if (folder.isDirectory())
			{
				// Look for the file
				for (int i = 0; i < listOfFiles.length; i++)
				{
					// Logger.log(Logger.TAG_DIAG, folder.listFiles()[i]);
					if (listOfFiles[i].isDirectory())
					{
						String path = listOfFiles[i].getName();
						
						mainParser = new MainParser(root + path);
						mainParser.getMainData();
						mainParser.checkForTethering();
						mainParser.showTetheringData();
						
						System.out.println();System.out.println("-----------------");System.out.println();
						//break;
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}*/
		
		System.out.println("\n\nIt took " + DateTimeOperator.getTimeStringFromMillis((System.currentTimeMillis() - now)));
		System.out.println("Started at " + new Date(now) + " stopped at "
		                   + new Date(System.currentTimeMillis()));
	}
}
