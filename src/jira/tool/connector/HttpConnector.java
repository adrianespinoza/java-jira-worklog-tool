package jira.tool.connector;

public interface HttpConnector 
{
	String doRequest(String method, String body);
}
