package jira.tool.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import jira.tool.main.Log;

public class JiraConnector implements HttpConnector
{
	private String url;
	private int timeout;
	private String basicAuth;
	
	private final String AUTHORIZATION = "Authorization";
	private final String CONTENT_TYPE = "Content-Type";
	private final String ACCEPT = "Accept";
	private final String CONTENT_LENGTH = "Content-length";
	private final String APPLICATION_JSON = "application/json";
	private final String UTF_8 = "UTF-8";
	
	public JiraConnector(String url, String username, String password)
	{
		this.url = url;
		this.timeout = 20000;
		this.basicAuth = generateBasicAuth(username, password);
	}
	
	public String doRequest(String method, String requestBody) 
	{
	    HttpURLConnection connection = null;
	    try 
	    {	 
	        URL u = new URL(url);
	        connection = (HttpURLConnection) u.openConnection();
	        connection.setRequestMethod(method);	        
	        connection.setRequestProperty (AUTHORIZATION, this.basicAuth);
	        
	        //set the sending type and receiving type to json
	        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
	        connection.setRequestProperty(ACCEPT, APPLICATION_JSON);
	 
	        connection.setAllowUserInteraction(false);
	        connection.setConnectTimeout(timeout);
	        connection.setReadTimeout(timeout);
	 
	        if (requestBody != null) 
	        {
	            //set the content length of the body
	            connection.setRequestProperty(CONTENT_LENGTH, requestBody.getBytes().length + "");
	            connection.setDoInput(true);
	            connection.setDoOutput(true);
	            connection.setUseCaches(false);
	 
	            //send the json as body of the request
	            OutputStream outputStream = connection.getOutputStream();
	            outputStream.write(requestBody.getBytes(UTF_8));
	            outputStream.close();
	        }
	 
	        //Connect to the server
	        connection.connect();
	 
	        int status = connection.getResponseCode();
	        Log.info("HTTP Client", "HTTP status code : " + status);
	        switch (status) 
	        {
	            case 200:
	            case 201:	                
	                return getResponseMessage(connection.getInputStream());
	            case 401:	            	
	                return getResponseMessage(connection.getErrorStream(), ". Please review your credentials.");
	            case 404:	            	
	                return getResponseMessage(connection.getErrorStream(), ". Please review the jira url.");
	        }
	 
	    } 
	    catch (MalformedURLException ex) 
	    {
	        Log.error("HTTP Client", "Error in http connection " + ex.toString());
	    } 
	    catch (IOException ex) 
	    {
	        Log.error("HTTP Client", "Error in http connection " + ex.toString());
	    } 
	    catch (Exception ex) 
	    {
	        Log.error("HTTP Client", "Error in http connection " + ex.toString());
	    } 
	    finally 
	    {
	        if (connection != null) 
	        {
	            try 
	            {
	                connection.disconnect();
	            } 
	            catch (Exception ex) 
	            {
	                Log.error("HTTP Client", "Error in http connection " + ex.toString());
	            }
	        }
	    }
	    
	    return null;
	}
	
	private String generateBasicAuth(String username, String password)
	{
		if(username == null || password == null)
		{
			return null;
		}
		
		String userCredentials = username + ":" + password;
		String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
		
		return basicAuth;
	}
	
	private String getResponseMessage(InputStream inputStream) throws IOException
	{
		return getResponseMessage(inputStream, false, null);
	}
	
	private String getResponseMessage(InputStream inputStream, String errorMessage) throws IOException
	{
		return getResponseMessage(inputStream, true, errorMessage);
	}
	
	private String getResponseMessage(InputStream inputStream, boolean isError, String subMessage) throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) 
        {
            sb.append(line + "\n");
        }
        bufferedReader.close();        
        String response = sb.toString();        
        if(!isError)
        {
        	Log.info("HTTP Client", "Received String : " + response);
        	return response;
        }        
        String reason = "Error: " + response.substring(response.indexOf("<title>") + 7, response.indexOf("</title>"));
        reason += subMessage;        
        Log.info("HTTP Client", "Received String : " + reason);
        
        return reason;
	}
}
