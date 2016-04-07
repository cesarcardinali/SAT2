package customobjects;


import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JiraQueryResult
{
	long                       resultCount;
	ArrayList<QueryResultItem> items;
	
	public JiraQueryResult(String jSON)
	{
		items = new ArrayList<QueryResultItem>();
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObj;
		
		try
		{
			jsonObj = (JSONObject) jsonParser.parse(jSON);
			resultCount = (long) jsonObj.get("total");
			
			JSONArray issues = (JSONArray) jsonObj.get("issues");
			
			if (resultCount > 0)
			{
				for (int i = 0; i < issues.size(); i++)
				{
					JSONObject obj = (JSONObject) issues.get(i);
					JSONObject fields = (JSONObject) obj.get("fields");
					JSONObject status = (JSONObject) fields.get("status");
					JSONObject comment = (JSONObject) fields.get("comment");
					JSONArray comments = (JSONArray)comment.get("comments");
					QueryResultItem qr = new QueryResultItem((String) obj.get("key"),
					                                         (String) fields.get("summary"),
					                                         (String) fields.get("updated"),
					                                         (String) status.get("description"));
					for (int j = 0; j < comments.size(); j++)
					{
						JSONObject c = (JSONObject) comments.get(j);
						qr.addcomment((String)c.get("body"));
					}
					
					items.add(qr);
					
//					System.out.println("Key: " + obj.get("key"));
//					System.out.println("Summary: " + fields.get("summary"));
//					System.out.println("Assignee: " + fields.get("assignee"));
//					System.out.println("Dup to: " + fields.get("customfield_10622"));
//					System.out.println("Updated: " + fields.get("updated"));
//					System.out.println("Status: " + status.get("description"));
//					System.out.println("Description: " + fields.get("description"));
//					System.out.println("Comments: ");
//					for(String c : qr.getComments())
//					{
//						System.out.println(c);
//					}
//					
//					System.out.println("\n---------------------------------\n");
				}
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
	}
	
	public long getResultCount()
	{
		return resultCount;
	}
	
	public ArrayList<QueryResultItem> getItems()
	{
		return items;
	}
	
	public class QueryResultItem
	{
		String            key;
		String            summary;
		String            updated;
		String            status;
		ArrayList<String> comments;
		
		public QueryResultItem(String key, String summary, String updated, String status)
		{
			this.key = key;
			this.summary = summary;
			this.updated = updated;
			this.status = status;
			comments = new ArrayList<String>();
		}
		
		public QueryResultItem(String key, String summary, String updated, String status, ArrayList<String> comments)
		{
			this.key = key;
			this.summary = summary;
			this.updated = updated;
			this.status = status;
			this.comments = comments;
		}
		
		public String getKey()
		{
			return key;
		}
		
		public String getSummary()
		{
			return summary;
		}
		
		public String getUpdated()
		{
			return updated;
		}
		
		public String getStatus()
		{
			return status;
		}
		
		public void addcomment(String comment)
		{
			comments.add(comment);
		}
		
		public ArrayList<String> getComments()
		{
			return comments;
		}
		
		public String toString()
		{
			return "Key: " + key + "\n " + "Summary: " + summary + "\n " + "Updated: " + updated + "\n " + "Status: " + status;
		}
	}
}