package supportive;


import java.util.ArrayList;


public class AppsChecker
{
	static ArrayList<String> audioAppsList;
	static ArrayList<String> gpsAppsList;
	static ArrayList<String> gamesList;
	static String            audioService;
	static String            cameraService;
	
	public static void initClass()
	{
		audioAppsList = new ArrayList<String>();
		gpsAppsList = new ArrayList<String>();
		gamesList = new ArrayList<String>();
		
		audioService = "mediaserver";
		cameraService = "qcamera";
		
		audioAppsList.add("tunein");
		audioAppsList.add("slacker");
		audioAppsList.add("pandora");
		audioAppsList.add("sirius");
		audioAppsList.add("android.music");
		audioAppsList.add("saavn");
		audioAppsList.add("com.audible.application");
		audioAppsList.add("spotify");
		audioAppsList.add("fmradio");
		audioAppsList.add("deezer");
		audioAppsList.add("fm.player");
		
		gpsAppsList.add("runtastic");
		gpsAppsList.add("runkeeper");
		gpsAppsList.add("pedometer");
		gpsAppsList.add("plusgps");
		gpsAppsList.add("cc.pacer.androidapp");
		gpsAppsList.add("com.stt.android");
		gpsAppsList.add("apps.maps");
		gpsAppsList.add("waze");
		
		gamesList.add("com.supercell.boombeach");
		gamesList.add("com.supercell.clashofclans");
		gamesList.add("com.blizzard.wtcg.hearthstone");
	}
	
	public static boolean isAudioApp(String process)
	{
		for (String app : audioAppsList)
		{
			if (process.contains(app))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isGpsApp(String process)
	{
		for (String app : gpsAppsList)
		{
			if (process.contains(app))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isGameApp(String process)
	{
		for (String app : gamesList)
		{
			if (process.contains(app))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isAudioService(String process)
	{
		if (process.contains(audioService))
		{
			return true;
		}
		
		return false;
	}
	
	public static boolean isCameraService(String process)
	{
		if (process.contains(cameraService))
		{
			return true;
		}
		
		return false;
	}
	
	public static boolean isBTService(String process)
	{
		if (process.contains("bluetooth"))
		{
			return true;
		}
		
		return false;
	}
}
