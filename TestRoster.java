import org.apache.log4j.Logger;

public class TestRoster
{
	private static Logger logger = Logger.getLogger(Constants.loggerClass);
	private static Logger applogger = Logger.getLogger(Constants.apploggerClass);
	private static BPM oBPM = new BPM(); 
	
	public static void main(String[] args)
	{	    	
		long stTime = System.currentTimeMillis();
		org.apache.log4j.PropertyConfigurator.configure(Constants.logConfig);
		
		applogger.info("******************************************************************");
		applogger.info("Begin process");
		
		applogger.debug("connecting to PE");
		FileNetUtil.logger = logger;
		oBPM.oVWSession = FileNetUtil.createPESession();

		if(oBPM.oVWSession != null)
		{	 
			 System.out.println("Connected.");
		}

		long endTime = System.currentTimeMillis();

		System.out.println("Process Time = " + (endTime - stTime)/1000  + " seconds");
		applogger.info("Process time = " + (endTime - stTime)/1000  + " seconds");
		applogger.info("End process");
	}	
}

