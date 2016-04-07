package supportive.preanalyzers.btdparser;


import java.io.File;


public class TestsRun
{
	@SuppressWarnings({"resource", "unused"})
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		// BtdParser btdParser = new BtdParser("btdTestData/BT10_311480147990712_20150908_192510.btd");
		// BtdParser btdParser = new BtdParser("C:/CRs/Debuging/80760131_B2G_2015-09-15_20-45-14/BT10_724101809975920_20150915_012914.btd");
		// semi tether
		 BtdParser btdParser0 = new BtdParser("C:/Users/cesar.cardinali/Documents/Eld/Bateria/Logs/Errors/94881341/");
		// tether
		//BtdParser btdParser0 = new BtdParser("C:/Users/cesar.cardinali/Desktop/tethering/");
		boolean ok = btdParser0.parse();
		if (ok)
		{
			System.out.println("Tethering issue? " + btdParser0.tethering());
			btdParser0.close();
		}
		
		
		String root = "C:/CRs/Debuging/";
		
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
					
					BtdParser btdParser = new BtdParser(root + path);
					ok = btdParser.parse();
					if (ok)
					{
						System.out.println("Tethering issue? " + btdParser.tethering());
						btdParser.close();
					}
					
					System.out.println();System.out.println("-----------------");System.out.println();
					//break;
				}
			}
		}
		
		System.out.println("\nAll done");
		System.out.println("It takes " + (System.currentTimeMillis() - start) + "ms");
	}
	
}
