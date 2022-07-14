
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RESTfulCall 
{
	public static String makeServiceCall()
	{
		String output = "";
		String fnlTransID = "";
		
		try 
		{
			HttpURLConnection conn = null;
			
			if (Constants.restMethod.equalsIgnoreCase(Constants.REST_DO_GET))
			{
				URL url = new URL(Constants.restURL + "?" + "hcid" + "=" + "AJ0815429");
				conn = (HttpURLConnection) url.openConnection();
			  
				conn.setDoOutput(Constants.restDoOutput);
			
				conn.setRequestProperty(Constants.restApiKeyFld, Constants.restApiKeyVal);
				conn.setRequestProperty(Constants.restSenderAppFld, Constants.restSenderAppVal);
			}
			else if(Constants.restMethod.equalsIgnoreCase(Constants.REST_DO_POST))
			{
				String restInput = "hcid=AJ0815429";
				
				conn.setRequestMethod(Constants.restMethod);
				
				OutputStream os = conn.getOutputStream();
				os.write(restInput.getBytes());
				os.flush();
				os.close();
			}
				
			WriteLog.WriteToLog(conn.toString());
			
			int responseValue = conn.getResponseCode(); 
			WriteLog.WriteToLog("Response value: " + responseValue);
			
			String respCode = ""; 
			
			if (responseValue == HttpURLConnection.HTTP_OK) 
			{
				String getData = "";
				String passMessage = "";
				
				respCode = String.valueOf(conn.getResponseCode());
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				WriteLog.WriteToLog("Output from Server...");
				
				while ((output = br.readLine()) != null) 
				{
					WriteLog.WriteToLog(output);
					getData = output; 
				}
				
				int passStart = 0;
				
				if ((passStart = getData.indexOf(Constants.restRespPass)) > 0)
				{
					int passEnd = getData.indexOf(":", passStart) + 1;
					int messStart = getData.indexOf("\"", passEnd) + 1;
					int messEnd = getData.lastIndexOf("\"");
					
					fnlTransID = getData.substring(messStart, messEnd);
				}
				
				br.close();
			}
			else
			{
				int httpCode = 0;
				String errMessage = "";
				String fullError = "";
				String errData = "";
				
				httpCode = conn.getResponseCode();
				respCode = String.valueOf(httpCode);
				
				WriteLog.WriteToLog("Message request failed!");
				WriteLog.WriteToLog("HTTP Error Code: " + respCode);				

				BufferedReader brErr = new BufferedReader(new InputStreamReader((conn.getErrorStream())));

				WriteLog.WriteToLog("Output from Server...");
				
				while ((output = brErr.readLine()) != null) 
				{
					WriteLog.WriteToLog(output);
					errData += output; 
				}
				
				if (errData.length() > 0)
				{
					if (errData.indexOf(",") > 0)
					{
						String[] outMsgs = errData.split(",");
						
						for (int l = 0; l < outMsgs.length; l++)
						{
							if (outMsgs[l].contains(Constants.restRespError))
							{
								int msgSplit = outMsgs[l].indexOf(":");
								int msgStart = outMsgs[l].indexOf("\"", msgSplit) + 1;
								int msgEnd = outMsgs[l].lastIndexOf("\"");
								
								errMessage = outMsgs[l].substring(msgStart, msgEnd);
								
								break;
							}
						}
					}
				}
				
				fullError = respCode + ": "  + errMessage;
				//restCall.setRespError(fullError);
				//restCall.setRespStatus(Constants.restFailMsg);
				
				fnlTransID = fullError;
			}
			
			conn.disconnect();
		}
	  	catch (MalformedURLException e) 
	  	{
		  e.printStackTrace();
	  	}
	  	catch (IOException e) 
	  	{
		  e.printStackTrace();
	  	}

		return fnlTransID;
		
	}
}
