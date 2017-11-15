package jira.tool.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import jira.tool.connector.JiraConnector;
import jira.tool.util.JiraHelper;
import jira.tool.util.UiHelper;

public class Main 
{
	public static void main(String[] args) throws ParseException {
		String url = "https://agile.pros.com/rest/api/2/search";
		String method = "POST";
		
		String DATE_FORMAT_NOW = "yyyy-MM-dd";		
		DateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);		
		String dateString = "2017-11-13";// in the future the date will come from date picked component
		Date dateObject = sdf.parse(dateString); // Handle the ParseException here		
		String worklogDate =  sdf.format(dateObject);
		
		String assignees = "mpardo,ahuanca,lfernand,kpaniagu";		
		String json = JiraHelper.buildWorklogRequestBody(assignees, worklogDate);		
		JiraConnector connector = new JiraConnector(url, "ahuanca", "Adri@n1!");
		
		String jsonString = connector.doRequest(method, json);		
		JSONObject jsonObject = new JSONObject(jsonString);		
		JSONArray issues = jsonObject.getJSONArray("issues");
		
		List<String> headersList = new ArrayList<String>();
		headersList.add("Key");
		headersList.add("Sumary");
		headersList.add("Author");
		headersList.add("Created");
		headersList.add("Time Spent");
		
		List<List<String>> rowsList = new ArrayList<List<String>>();
		
		for(int i = 0; i < issues.length(); ++i) 
		{
			JSONObject issue = issues.getJSONObject(i);
			JSONObject fields = issue.getJSONObject("fields");
			
			String key = issue.getString("key");
			String summary = fields.getString("summary");
			
			JSONObject worklog = fields.getJSONObject("worklog");
			JSONArray worklogs = worklog.getJSONArray("worklogs");
			
			for(int j = 0; j < worklogs.length(); ++j)
			{
				JSONObject worklog2 = worklogs.getJSONObject(j);
				if(worklog2.get("created").toString().contains(worklogDate))
				{
					JSONObject author = worklog2.getJSONObject("author");
					
					List<String> row = new ArrayList<String>();
					row.add(key);
					row.add(summary);
					row.add(author.getString("name"));
					row.add(worklog2.getString("created"));
					row.add(worklog2.getString("timeSpent"));
					
					rowsList.add(row);
				}
			}
		}
		
		if(rowsList.size() > 0) 
		{
			TableGenerator tableGenerator = new TableGenerator();
			String table = tableGenerator.generateTable(headersList, rowsList);
			
			System.out.println(table);
		}
	}
}
