package filters;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import supportive.FileFinder;

import com.google.common.base.Throwables;

import core.Logger;
import core.SharedObjs;


/**
 * Search for DIAG_WS wake lock
 **/
public class Diag
{
	private static String  result;
	private static boolean enabled = true;
	
	public static String makelog(String path)
	{
		long duration; // CR duration
		long diagDuration; // Diag wake lock duration
		String regex, diagAllKernel, diagMs, product, line;
		Pattern pattern;
		Matcher matcher;
		
		// Initialize variables
		diagAllKernel = "";
		diagMs = "";
		product = "";
		result = "";
		duration = 0;
		diagDuration = 0;
		BufferedReader reader = null;
		
		try
		{
			// File seek and load configuration
			String file_report = "";

			FileFinder ff = new FileFinder(path);
			file_report = ff.getFilePath(FileFinder.BUGREPORT);
			
			// Check if is directory exists
			if (!ff.getFound())
			{
				result = FileFinder.BUGREPORT + " " + file_report;
				return result;
			}
			
			try
			{
				Logger.log(Logger.TAG_DIAG, "Log file: " + file_report);
				reader = new BufferedReader(new FileReader(file_report));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				result = "FileNotFoundException\n" + Throwables.getStackTraceAsString(e);
				return result;
			}
			
			// Find DIAG_WS evidences
			while ((line = reader.readLine()) != null)
			{
				// Find product name
				if (line.contains("Build fingerprint:") && product.equals(""))
				{
					regex = ".*/([a-z]*)_.*";
					pattern = Pattern.compile(regex);
					matcher = pattern.matcher(line);
					
					if (!matcher.matches())
					{
						Logger.log(Logger.TAG_DIAG, "Nao encontrou produto");
					}
					else
					{
						Logger.log(Logger.TAG_DIAG, "Produto: " + matcher.group(1));
						product = matcher.group(1);
					}
				}
				
				// Find duration
				if (line.contains("[ID=BT_DISCHARGE_SUMMARY;") && line.contains("duration="))
				{
					regex = ".*;duration=([0-9]+);.*";
					pattern = Pattern.compile(regex);
					matcher = pattern.matcher(line);
					
					if (!matcher.matches())
					{
						Logger.log(Logger.TAG_DIAG, "Nao encontrou duracao da CR");
					}
					else
					{
						Logger.log(Logger.TAG_DIAG, "Duração: " + matcher.group(1) + "ms");
						duration = Long.parseLong(matcher.group(1));
					}
				}
				
				// Find "all kernel" data
				if (line.contains("All kernel wake locks"))
				{
					line = reader.readLine();
					
					if (line.contains("Kernel Wake lock DIAG_WS"))
					{
						diagAllKernel = diagAllKernel + "||" + line.replace(": ", "|").concat("|") + "\n";
					}
					else
					{
						line = reader.readLine();
						if (line.contains("Kernel Wake lock DIAG_WS"))
						{
							diagAllKernel = diagAllKernel + "||" + line.replace(": ", "|").concat("|") + "\n";
						}
					}
				}
				
				// Find kernel ms's data
				if (line.contains("DIAG_WS") && !line.contains(",") && !line.contains(";")
				    && !line.contains(".") && !line.contains("Kernel") && !line.contains("ms")
				    && !line.contains("\"") && !line.contains("(") && !line.contains(":"))
				{
					System.out.println(line);
					String parts[] = line.split("\t\t\t|\t\t|\t");
					diagDuration = Long.parseLong(parts[6]);
					diagMs = "DIAG_WS is held for more than "
					         + (diagDuration / 3600000)
					         + " hours following max_time:\n"
					         + "||name		|active_count	|event_count	|wakeup_count	|expire_count	|active_since	|total_time	|max_time	|last_change | prevent_suspend_time|"
					         + "\n||" + line.replaceAll("\t\t|\t", "|") + "|\n";
				}
				
				if (line.contains("DUMP OF SERVICE entropy:"))
				{
					break;
				}
			}
			
			if (diagDuration > duration * 0.5)
			{
				Logger.log(Logger.TAG_DIAG, "Diag!");
			}
			else
			{
				Logger.log(Logger.TAG_DIAG, "Not Diag! The DIAG period seems too short");
				diagMs = "";
			}
			
			reader.close();
			
			/*
			 * Logger.log(Logger.TAG_DIAG, "Product:\t\t" + product); Logger.log(Logger.TAG_DIAG, "CR Duration:\t\t" + duration);
			 * Logger.log(Logger.TAG_DIAG, "DIAG_WS duration:\t" + diagDuration); Logger.log(Logger.TAG_DIAG, "All Kernel:\t\t" +
			 * diagAllKernel); Logger.log(Logger.TAG_DIAG, "General mode:\t\t" + diagMs);
			 */
			
			// Prepare the final comment:
			if (!diagAllKernel.equals("") || !diagMs.equals(""))
			{
				result = SharedObjs.optionsPane.getTextDiag()
				                               .replace("#log#", diagMs + "\n" + diagAllKernel + "\n")
				                               .replace("\\n", "\n");
				
				File xmlFile = new File("Data/cfgs/user_cfg.xml"); // Create XML file
				
				SAXBuilder builder = new SAXBuilder(); // Instance of XML builder
				
				Document document = (Document) builder.build(xmlFile); // Create a document as a XML file
				
				Element satNode = document.getRootElement(); // Get root node
				
				Element diagNode = satNode.getChild("diag_dup"); // Get diag node
				
				result = result.replace("#dupcr#", diagNode.getChildText(product)); // Replace tag by diag dup cr
			}
			else
			{
				result = "No diag issue could be found in the logs";
			}
		}
		catch (FileNotFoundException e)
		{
			result = "FileNotFoundException\n" + Throwables.getStackTraceAsString(e);
			e.printStackTrace();
			
			return result;
		}
		catch (IOException e)
		{
			result = "IOException\n" + Throwables.getStackTraceAsString(e);
			e.printStackTrace();
			
			return result;
		}
		catch (JDOMException e)
		{
			result = "JDOMException\n" + Throwables.getStackTraceAsString(e);
			e.printStackTrace();
			
			return result;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			result = "ArrayIndexOutOfBoundsException\n" + Throwables.getStackTraceAsString(e);
			e.printStackTrace();
			
			return result;
		}
		return result;
	}
	
	// Getters and Setters
	public static String getResult()
	{
		return result;
	}
	
	public static void updateResult(String editedResult)
	{
		result = editedResult;
	}
	
	public static boolean isEnabled()
	{
		return enabled;
	}
	
	public static void setEnabled(boolean onoff)
	{
		enabled = onoff;
	}
}
