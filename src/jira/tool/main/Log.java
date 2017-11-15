package jira.tool.main;

public class Log 
{
	public static void info(String header, String message) 
	{
		System.out.println("INFO - "  + header + " : " + message);
	}
	
	public static void error(String header, String message) 
	{
		System.out.println("ERROR - " + header + " : " + message);
	}
}
