

public class RESTTransaction 
{
	
	//"\"statusUpdateTmstmp\": \"2017-08-03 12:45:10\"}";

	String restEIN = "";
	String restAppID = "";
	String restChannel = "";
	String restAppStat = "";
	String restCaseNum = "";
	String restACN = "";
	String restCaseStage = "";
	String restCaseTimeStamp = "";
	
	String restRespStatus = "";
	String restRespTransID = "";
	String restRespErrMsg = "";
	String restRespTimeStamp = "";
	
	boolean isReady = false;
	
	public RESTTransaction()
	{
		isReady = true;
	}
	
	public String getEIN()
	{
		return this.restEIN;
	}
	
	public void setEIN(String ein)
	{
		this.restEIN = ein;
	}
	
	public String getAppID()
	{
		return this.restAppID;
	}
	
	public void setAppID(String appID)
	{
		this.restAppID = appID;
	}
	
	public String getChannel()
	{
		return this.restChannel;
	}
	
	public void setChannel(String channel)
	{
		this.restChannel = channel;
	}
	
	public String getAppStatus()
	{
		return this.restAppStat;
	}
	
	public void setAppStatus(String appStatus)
	{
		this.restAppStat = appStatus;
	}
	
	public String getCaseNumber()
	{
		return this.restCaseNum;
	}
	
	public void setCaseNumber(String caseNumber)
	{
		this.restCaseNum = caseNumber;
	}
	
	public String getACN()
	{
		return this.restACN;
	}
	
	public void setACN(String acn)
	{
		this.restACN = acn;
	}
	
	public String getCaseStage()
	{
		return this.restCaseStage;
	}
	
	public void setCaseStage(String caseStage)
	{
		this.restCaseStage = caseStage;
	}
	
	public String getCaseTimeStamp()
	{
		return this.restCaseTimeStamp;
	}
	
	public void setCaseTimeStamp(String caseTimeStamp)
	{
		this.restCaseTimeStamp = caseTimeStamp;
	}

	public String getRespTimeStamp()
	{
		return this.restRespTimeStamp;
	}
	
	public void setRespTimeStamp(String respTimeStamp)
	{
		this.restRespTimeStamp = respTimeStamp;
	}

	public String getRespStatus()
	{
		return this.restRespStatus;
	}
	
	public void setRespStatus(String respStatus)
	{
		this.restRespStatus = respStatus;
	}

	public String getRespError()
	{
		return this.restRespErrMsg;
	}
	
	public void setRespError(String respError)
	{
		this.restRespErrMsg = respError;
	}

	public String getRespTransID()
	{
		return this.restRespTransID;
	}
	
	public void setRespTransID(String transID)
	{
		this.restRespTransID = transID;
	}

	public String toString()
	{
		String dataOut = "";
		
		dataOut = "EIN:                  " + this.restEIN + "\r\n" +  
				  "Application ID:       " + this.restAppID  + "\r\n" +
				  "Channel:              " + this.restChannel + "\r\n" +
				  "Application Status:   " + this.restAppStat + "\r\n" +
				  "Case Number:          " + this.restCaseNum + "\r\n" +
				  "ACN:                  " + this.restACN + "\r\n" +
				  "Case Stage:           " + this.restCaseStage + "\r\n" +
				  "Case TimeStamp:       " + this.restCaseTimeStamp + "\r\n" +
				  "Response Status:      " + this.restRespStatus + "\r\n" +
				  "Response Message:     " + this.restRespErrMsg + "\r\n" +
				  "Response Timestamp:   " + this.restRespTimeStamp + "\r\n" +
				  "Response Transaction: " + this.restRespTransID;
				  
		return dataOut;
		
	}
	
	public String requestToString()
	{
		String dataOut = "";
		
		dataOut = "EIN:                  " + this.restEIN + "\r\n" +  
				  "Application ID:       " + this.restAppID  + "\r\n" +
				  "Channel:              " + this.restChannel + "\r\n" +
				  "Application Status:   " + this.restAppStat + "\r\n" +
				  "Case Number:          " + this.restCaseNum + "\r\n" +
				  "ACN:                  " + this.restACN + "\r\n" +
				  "Case Stage:           " + this.restCaseStage + "\r\n" +
				  "Case TimeStamp:       " + this.restCaseTimeStamp + "\r\n";
				  
		return dataOut;
		
	}
	
	public String resultToString()
	{
		String dataOut = "";
		
		dataOut = "Response Status:      " + this.restRespStatus + "\r\n" +
				  "Response Message:     " + this.restRespErrMsg + "\r\n" +
				  "Response Timestamp:   " + this.restRespTimeStamp + "\r\n" +
				  "Response Transaction: " + this.restRespTransID;
		
		/*
		 * 
		 */
		
		return dataOut;
	}
	
}
