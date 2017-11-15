package jira.tool.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class JiraHelper 
{
private static String worklogJqlTemplate = "issuetype IN (%s) and assignee IN (%s) AND worklogDate = \"%s\"";
	
	public static String buildWorklogRequestBody(String assignees, String worklogDate)
	{
		String jql = String.format(worklogJqlTemplate, "Sub-task", assignees, worklogDate);
		
		JSONObject json = new JSONObject();
		json.put(Constants.JQL, jql);
		json.put(Constants.START_AT, 0);
		json.put(Constants.MAX_RESULTS, 50);		
		JSONArray array = new JSONArray();
		array.put(Constants.SUMMARY);
		array.put(Constants.STATUS);
		array.put(Constants.ASSIGNEE);
		array.put(Constants.WORKLOG);		
		json.put(Constants.FIELDS, array);
		
		return json.toString();
	}
	
	public static String getDateFormatted(Date date) throws ParseException
	{				
		DateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);	
		String dateFormatted =  sdf.format(date);
		
		return dateFormatted;
	}
	
	public static String checkWorklogWarnings(String jsonString) 
	{
		try
		{
			JSONObject jsonObject = new JSONObject(jsonString);	
			JSONArray warningMessages = jsonObject.getJSONArray(Constants.WARNING_MESSAGES);
			if(warningMessages == null || warningMessages.length() == 0) {
				return null;
			}

			String warnMessages = "Warnings!: ";
			for(int i = 0; i < warningMessages.length(); ++i) 
			{
				warnMessages += warningMessages.getString(i);
			}
			return warnMessages;
		}
		catch (Exception e)
		{
			return null;
		}
		
	}
}
