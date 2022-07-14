import org.apache.log4j.Logger;

import filenet.vw.api.VWException;
import filenet.vw.api.VWSession;

public class BPMConn
{
	private static Logger logger = Logger.getLogger(Constants.loggerClass);
	private static BPM oBPM = new BPM(); 
	
	public static VWSession createConnection()
	{	    	
		long stTime = System.currentTimeMillis();
		org.apache.log4j.PropertyConfigurator.configure(Constants.logConfig);
		
		WriteLog.WriteToLog("Begin process");
		WriteLog.WriteToLog("Connecting to PE");
		FileNetUtil.logger = logger;
		
		oBPM.oVWSession = FileNetUtil.createPESession();

		if(oBPM.oVWSession != null)
		{	 
			WriteLog.WriteToLog("Connected.");
		}

		long endTime = System.currentTimeMillis();

		WriteLog.WriteToLog("Process time = " + (endTime - stTime)/1000  + " seconds");
		WriteLog.WriteToLog("End process");
		
		return oBPM.oVWSession;
	}
	
	public static void close(VWSession bpmSession)
	{
		try 
		{
			bpmSession.logoff();
		}
		catch (VWException e) 
		{
			WriteLog.WriteToLog("Error disconnectiong from BPM!");
			WriteLog.WriteToLog("ERROR: " + e.getMessage()); 
		}
		
	}
	
}
