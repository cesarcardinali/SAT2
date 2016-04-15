package customobjects;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Logger;


/**
 * Defines a CR item
 */
public class CrItem
{
	private String            b2gID;          // Represents the b2g ID
	private String            jiraID;         // Represents the Jira ID
	private String            summary;        // Represents CR summary
	private String            status;         // Represents CR status
	private String            resolution;     // Represents CR resolution
	private String            assignee;       // Represents CR assignee
	private String            product;        // Represents CR product
	private String            created;
	private String            updated;
	private String            affectedVersion;
	private String            component;
	private ArrayList<String> labels;         // Represents CR labels
	private String            dup;            // Represents CR dups
	private String            description;
	private String            comment;        // Represents a comment to be inserted at this CR
	private String            fixMerges;      // customfield_10631
	private String            closureDate;
	private String            build;
	
	/**
	 * Class constructor
	 * 
	 * @param b2gID CR b2g ID
	 */
	public CrItem(String b2gID)
	{
		jiraID = "";
		this.b2gID = b2gID;
		summary = "";
		status = "New";
		resolution = "Unresolved";
		assignee = "";
		product = "";
		created = "";
		updated = "";
		affectedVersion = "";
		build = "";
		component = "";
		labels = new ArrayList<String>();
		dup = "";
		description = "";
		comment = "";
		fixMerges = "";
		closureDate = "";
	}
	
	/**
	 * Class constructor
	 */
	public CrItem()
	{
		b2gID = "";
		jiraID = "";
		summary = "";
		status = "New";
		resolution = "Unresolved";
		assignee = "";
		product = "";
		created = "";
		updated = "";
		affectedVersion = "";
		build = "";
		component = "";
		labels = new ArrayList<String>();
		dup = "";
		description = "";
		comment = "";
		fixMerges = "";
		closureDate = "";
	}
	
	// Class to string
	public String toString()
	{
		return "Jira: " + jiraID + " || B2gID: " + b2gID + " || Summary: " + summary + " || Build: " + build + " || Status: " + status + " || Resolution: "
		       + resolution + " || Assignee: " + assignee + " || Product: " + product + " || Created: " + created + " || Updated: " + updated
		       + " || Resolved: " + closureDate + " || AffectedVersions: " + affectedVersion + " || Component/s: " + component + " || Fixed at: " + fixMerges
		       + " || Assignee: " + assignee + " || Assignee: " + assignee + " || Labels: " + labels;/* + " || Dup: " + dup + " || Description: " + description; */
	}
	
	/**
	 * Compare two build strings, like "MCK24.107-12.1" to "MCK24.107-13.2"</br> Usage:</br> Call this method with the build you want to compare as a String.</br> The method will compare the given build to the CR build.</br></br>
	 * @param buildComp Build string to compare to the CR build
	 * @return Integer as:</br> 0 - The given build is older than the CR build</br> 1 - The given build is newer than the CR build</br> 2 - The builds are equals</br> -1 - Could not compare
	 */
	public int compareBuild(String buildComp)
	{
		// TODO Create local build derivations like this to compare to the received parameter
		
		if (!build.equals("") && !buildComp.equals(""))
		{
			System.out.println("Comparing " + buildComp + " to " + build);
			// Parse CR build
			String bProp = "";
			int bBaseVersion = -1;
			int bDerivatedBaseVersion = -1;
			int bRevisionVersion = 0;
			int bDerivatedRevisionVersion = -1;
			
			bProp = build.substring(0, 3);
			
			bBaseVersion = Integer.parseInt(build.substring(3, build.indexOf(".")));
			
			if (build.indexOf("-", 5) > 0)
			{
				bDerivatedBaseVersion = Integer.parseInt(build.substring(build.indexOf(".") + 1, build.indexOf("-")));
				
				if (build.indexOf(".", 9) > 0)
				{
					bRevisionVersion = Integer.parseInt(build.substring(build.indexOf("-") + 1, build.indexOf(".", 9)));
					bDerivatedRevisionVersion = Integer.parseInt(build.substring(build.indexOf(".", 9) + 1, build.length()));
				}
				else
				{
					bRevisionVersion = Integer.parseInt(build.substring(build.indexOf("-") + 1, build.length()));
				}
			}
			else
			{
				bDerivatedBaseVersion = Integer.parseInt(build.substring(build.indexOf(".") + 1, build.length()));
				bRevisionVersion = -1;
			}
			
			// Parse build for comparsion
			String bcProp = "";
			int bcBaseVersion = -1;
			int bcDerivatedBaseVersion = -1;
			int bcRevisionVersion = 0;
			int bcDerivatedRevisionVersion = -1;
			
			bcProp = buildComp.substring(0, 3);
			
			bcBaseVersion = Integer.parseInt(buildComp.substring(3, buildComp.indexOf(".")));
			
			if (buildComp.indexOf("-", 5) > 0)
			{
				bcDerivatedBaseVersion = Integer.parseInt(buildComp.substring(buildComp.indexOf(".") + 1, buildComp.indexOf("-")));
				
				if (buildComp.indexOf(".", 9) > 0)
				{
					bcRevisionVersion = Integer.parseInt(buildComp.substring(buildComp.indexOf("-") + 1, buildComp.indexOf(".", 9)));
					bcDerivatedRevisionVersion = Integer.parseInt(buildComp.substring(buildComp.indexOf(".", 9) + 1, buildComp.length()));
				}
				else
				{
					bcRevisionVersion = Integer.parseInt(buildComp.substring(buildComp.indexOf("-") + 1, buildComp.length()));
				}
			}
			else
			{
				bcDerivatedBaseVersion = Integer.parseInt(buildComp.substring(buildComp.indexOf(".") + 1, buildComp.length()));
				bcRevisionVersion = -1;
			}
			
			// System.out.println("Builds: " + bcProp + " -> " + bProp);
			// System.out.println("Version: " + bcBaseVersion + " -> " + bBaseVersion);
			// System.out.println("Secondary Version: " + bcDerivatedBaseVersion + " -> " + bDerivatedBaseVersion);
			// System.out.println("Secondary Revision: " + bcDerivatedRevisionVersion + " -> " + bDerivatedRevisionVersion);
			// System.out.println("Revision: " + bcRevisionVersion + " -> " + bRevisionVersion);
			
			// Compare builds
			if (bcProp.equals(bProp))
			{
				if (bcBaseVersion > bBaseVersion)
				{
					return 1;
				}
				else if (bcBaseVersion < bBaseVersion)
				{
					return 0;
				}
				else
				{
					if (bcDerivatedBaseVersion > bDerivatedBaseVersion)
					{
						return 1;
					}
					else if (bcDerivatedBaseVersion < bDerivatedBaseVersion)
					{
						return 0;
					}
					else
					{
						if (bcRevisionVersion > bRevisionVersion)
						{
							return 1;
						}
						else if (bcRevisionVersion < bRevisionVersion)
						{
							return 0;
						}
						else
						{
							if (bcDerivatedRevisionVersion > bDerivatedRevisionVersion)
							{
								return 1;
							}
							else if (bcDerivatedRevisionVersion < bDerivatedRevisionVersion)
							{
								return 0;
							}
							else
							{
								return 2;
							}
						}
					}
				}
			}
		}
		
		return -1; // Comparsion error - Builds from different branch(prop)
	}
	
	// Getters and Setters
	public String getB2gID()
	{
		return b2gID;
	}
	
	public String getJiraID()
	{
		return jiraID;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public String getResolution()
	{
		return resolution;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	public void setB2gID(String b2gID)
	{
		this.b2gID = b2gID;
	}
	
	public void setJiraID(String jiraID)
	{
		this.jiraID = jiraID;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public void setResolution(String resolution)
	{
		this.resolution = resolution;
	}
	
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	
	public String getSummary()
	{
		return summary;
	}
	
	public void setSummary(String summary)
	{
		this.summary = summary;
		
		if (summary.contains("B2GID:") && b2gID.equals(""))
		{
			int index = summary.lastIndexOf("B2GID:");
			b2gID = summary.substring(index + 6);
		}
	}
	
	public String getAssignee()
	{
		return assignee;
	}
	
	public void setAssignee(String assignee)
	{
		this.assignee = assignee;
	}
	
	public String getDup()
	{
		return dup;
	}
	
	public void setDup(String dup)
	{
		this.dup = dup;
	}
	
	public ArrayList<String> getLabels()
	{
		return labels;
	}
	
	public void setLabels(ArrayList<String> labels)
	{
		this.labels = labels;
	}
	
	public String getClosureDate()
	{
		return closureDate;
	}
	
	public void setClosureDate(String closure_date)
	{
		this.closureDate = closure_date;
	}
	
	public String getProduct()
	{
		return product;
	}
	
	public void setProduct(String product)
	{
		this.product = product;
	}
	
	public String getCreated()
	{
		return created;
	}
	
	public void setCreated(String created)
	{
		this.created = created;
	}
	
	public String getUpdated()
	{
		return updated;
	}
	
	public void setUpdated(String updated)
	{
		this.updated = updated;
	}
	
	public String getAffectedVersion()
	{
		return affectedVersion;
	}
	
	public void setAffectedVersion(String affectedVersion)
	{
		this.affectedVersion = affectedVersion;
		
		if (affectedVersion.contains("userdebug"))
		{
			// lux_verizon-userdebug 6.0.1 MCD24.107-48 452 intcfg,test-keys
			String slices[] = affectedVersion.split(" ");
			
			if (slices.length > 1)
				build = slices[2];
			else
				build = slices[0];
		}
		else if (!affectedVersion.toLowerCase().contains("not yet"))
		{
			// MCD24.107-48
			build = affectedVersion;
		}
		else
		{
			// Try to extract build from description
			if (description.contains("This CR was automatically opened by the battery detectors julp tool"))
			{
				Pattern pat = Pattern.compile(".*AP Version:.*(\\w\\w\\w\\d\\d\\.\\d\\d\\d?-?\\d?\\d?\\d?\\.?\\d?\\d?).*");
				Matcher matcher = null;
				
				// Detect current status
				matcher = pat.matcher(description.replace("\r", "").replace("\n", ""));
				if (matcher.matches())
				{
					build = matcher.group(1);
					Logger.log("CR_ITEM", "Build extracted from description: " + build);
				}
				else
				{
					Logger.log("CR_ITEM", "Could not extracte build from description: " + build);
				}
			}
			else
			{
				build = "";
			}
		}
		
		Logger.log("CR_ITEM", "------------------------- Build detected: " + build);
	}
	
	public String getComponent()
	{
		return component;
	}
	
	public void setComponent(String component)
	{
		this.component = component;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getFixMerges()
	{
		return fixMerges;
	}
	
	public void setFixMerges(String fixMerges)
	{
		this.fixMerges = fixMerges;
	}
	
	public String getBuild()
	{
		return build;
	}
	
	public void setBuild(String build)
	{
		this.build = build;
	}
}
