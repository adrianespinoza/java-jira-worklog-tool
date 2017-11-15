package jira.tool.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class UiHelper 
{			
	public static void setWorklogTableModelRows(DefaultTableModel dtm, String jsonString, String worklogDate)
	{
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONArray issues = jsonObject.getJSONArray(Constants.ISSUES);
		
		for(int i = 0; i < issues.length(); ++i) 
		{
			JSONObject issue = issues.getJSONObject(i);
			JSONObject fields = issue.getJSONObject(Constants.FIELDS);
			
			String key = issue.getString(Constants.KEY);
			String summary = fields.getString(Constants.SUMMARY);
			
			JSONObject worklog = fields.getJSONObject(Constants.WORKLOG);
			JSONArray worklogs = worklog.getJSONArray(Constants.WORKLOGS);
			
			for(int j = 0; j < worklogs.length(); ++j)
			{
				JSONObject worklog2 = worklogs.getJSONObject(j);
				if(worklog2.getString(Constants.CREATED).contains(worklogDate))
				{
					JSONObject author = worklog2.getJSONObject(Constants.AUTHOR);					
					dtm.addRow(new Object[] { 
						key, 
						author.getString(Constants.NAME), 
						worklog2.getString(Constants.TIME_SPENT), 
						worklog2.getString(Constants.CREATED), 
						summary 
					});
				}
			}
		}
	}
}
