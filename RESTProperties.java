
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class RESTProperties
{
	public static void setPropsPath(String arg)
	{
		if (arg.equalsIgnoreCase("U") || arg.equalsIgnoreCase("UNIX"))
		{
			Constants.appPath = "/opt/IBM/FileNet/AE/Router/SG/BPMRestful";
			Constants.PropFile = Constants.appPath + "/Properties/BPMRest.props";
		}
		else
		{
			//System.getProperty("user.dir");
			Constants.appPath = "/Personal/Anthem Applications/BPM Webservice Calls"; 
			Constants.PropFile = Constants.appPath + "/Properties/BPMRest.props";
		}
	}
	
	public static void initProps()
	{
		loadMailProperties();
		
		Constants.bpmDomain = AppProperties.Read("bpm.Domain", false);
		Constants.bpmUser = AppProperties.Read("bpm.User", false);
		Constants.bpmPass = AppProperties.Read("bpm.Pass", true);
		Constants.bpmServer = AppProperties.Read("bpm.Server", false);
		Constants.bpmPort = AppProperties.Read("bpm.Port", false);
		Constants.bpmShortRoster = AppProperties.Read("bpm.Roster.Short", false);
		Constants.bpmLongRoster = AppProperties.Read("bpm.Roster.Long", false);
		Constants.bpmRouter = AppProperties.Read("bpm.Router", false);

		Constants.logPath = getPathVal("path.Log", Constants.appPath);
		Constants.logMainPfx = AppProperties.Read("log.Main.Prefix", false);
		Constants.logStatusPfx = AppProperties.Read("log.Status.Prefix", false);
		Constants.logServerLen = getNumericVal(AppProperties.Read("log.Server.Length", false));
				
		Constants.procEnv = AppProperties.Read("proc.Env", false);
		Constants.showOutput = getBooleanVal(AppProperties.Read("output.Show", false));
		Constants.appName = AppProperties.Read("application.Name", false);
		Constants.appEnv = AppProperties.Read("application.Env", false);
		Constants.fileToProc = AppProperties.Read("file.To.Process", false);
		Constants.stopFile = AppProperties.Read("file.Stop", false);
		
		Constants.restart = getBooleanVal(AppProperties.Read("proc.Restart", false));
		Constants.restFile = AppProperties.Read("proc.Last.File", false);
				
		Constants.WcmApiConfig = getPathVal("path.WcmApiConfig", Constants.appPath);
		Constants.FileNetConfig = getPathVal("path.FileNetConfig", Constants.appPath);
		Constants.logConfig = getPathVal("path.LogConfig", Constants.appPath);
		
		Constants.isServer = AppProperties.Read("fnet.Server", false);
		Constants.isUser = AppProperties.Read("fnet.User", false);
		Constants.isPass = AppProperties.Read("fnet.Pass", true);
		
		Constants.restApiKeyFld = AppProperties.Read("rest.Api.Key.Fld", false);
		Constants.restApiKeyVal = AppProperties.Read("rest.Api.Key.Val", false);
		Constants.restAuthKeyFld = AppProperties.Read("rest.Auth.Key.Fld", false);
		Constants.restAuthKeyVal = AppProperties.Read("rest.Auth.Key.Val", false);
		Constants.restContentTypeFld = AppProperties.Read("rest.Content.Type.Fld", false);
		Constants.restContentTypeVal = AppProperties.Read("rest.Content.Type.Val", false);
		Constants.restAcceptTypeFld = AppProperties.Read("rest.Accept.Type.Fld", false);
		Constants.restAcceptTypeVal = AppProperties.Read("rest.Accept.Type.Val", false);
		Constants.restSenderAppFld = AppProperties.Read("rest.Sender.App.Fld", false);
		Constants.restSenderAppVal = AppProperties.Read("rest.Sender.App.Val", false);
		Constants.restTransIDFld = AppProperties.Read("rest.Trans.ID.Fld", false);
		Constants.restTransIDVal = AppProperties.Read("rest.Trans.ID.Val", false);
		Constants.restMethod = AppProperties.Read("rest.Method", false);
		Constants.restURL = AppProperties.Read("rest.URL", false);
		Constants.restCaseStage = AppProperties.Read("rest.CaseStage", false);
		Constants.restPassMsg = AppProperties.Read("rest.Msg.Pass", false);
		Constants.restFailMsg = AppProperties.Read("rest.Msg.Fail", false);
		Constants.restRespError = AppProperties.Read("rest.out.Error", false);
		Constants.restRespPass = AppProperties.Read("rest.out.Pass", false);
		Constants.restDoOutput = getBooleanVal(AppProperties.Read("rest.Do.Output", false));
		
		Constants.bpmStatusFld = AppProperties.Read("bpm.Status.Field", false);
		Constants.bpmTransIDFld = AppProperties.Read("bpm.TransID.Field", false);
		Constants.bpmTmStmpFld = AppProperties.Read("bpm.TimeStamp.Field", false);
		Constants.bpmStageFld = AppProperties.Read("bpm.Stage.Field", false);
		Constants.bpmErrorFld = AppProperties.Read("bpm.Error.Field", false);
		Constants.bpmWorkStatFld = AppProperties.Read("bpm.WorkStatus.Field", false);
		Constants.bpmUserNmFld = AppProperties.Read("bpm.UserName.Field", false);
		Constants.bpmStatDtFld = AppProperties.Read("bpm.StatusDate.Field", false);
		Constants.bpmFromQFld = AppProperties.Read("bpm.FromQueue.Field", false);
		Constants.bpmWorkStatVal = AppProperties.Read("bpm.WorkStatus.Value", false);
		Constants.bpmUserNmVal = AppProperties.Read("bpm.UserName.Value", false);
		Constants.bpmFromQVal = AppProperties.Read("bpm.FromQueue.Value", false);
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		
		String stopTime = AppProperties.Read("time.Stop", false);
		
		try
		{
			Constants.stopTime = sdf.parse(stopTime);
		}
		catch (ParseException pe)
		{
			WriteLog.WriteToLog("Error parsing stop time..." + stopTime);
			WriteLog.WriteToLog("ERROR: " + pe.getMessage()); 
	    }
		
		String startTime = AppProperties.Read("time.Start", false);
		
		try
		{
			Constants.startTime = sdf.parse(startTime);
		}
		catch (ParseException pe)
		{
			WriteLog.WriteToLog("Error parsing start time..." + startTime);
			WriteLog.WriteToLog("ERROR: " + pe.getMessage()); 
	    }
						
		Constants.milliSecs = getLongVal(AppProperties.Read("time.Actual.MilSecs", false));
		Constants.fileMilSeconds = getLongVal(AppProperties.Read("time.File.MilSecs", false));
		Constants.winMilSeconds = getLongVal(AppProperties.Read("time.Window.MilSecs", false));
		Constants.breakMilSeconds = getLongVal(AppProperties.Read("time.Break.MilSecs", false));
		Constants.retryMilSeconds = getLongVal(AppProperties.Read("time.Retry.MilSecs", false));
		Constants.waitInterval = getNumericVal(AppProperties.Read("time.Wait.MilSecs", false));
		
		Constants.fileMinutes = Constants.fileMilSeconds / Constants.milliSecs;
		Constants.windowMinutes = Constants.winMilSeconds / Constants.milliSecs;
		Constants.breakMinutes = Constants.breakMilSeconds / Constants.milliSecs;
		Constants.retryMinutes = Constants.retryMilSeconds / Constants.milliSecs;
		Constants.waitIntMinutes = Constants.waitInterval / Constants.milliSecs;

		Constants.fileDescriptor = getMinDescriptor(Constants.fileMinutes);
		Constants.windowDescriptor = getMinDescriptor(Constants.windowMinutes);
		Constants.breakDescriptor = getMinDescriptor(Constants.breakMinutes);
		Constants.retryDescriptor = getMinDescriptor(Constants.retryMinutes);
		Constants.waitIntDescriptor = getMinDescriptor(Constants.waitIntMinutes);

		Constants.rowColorA = AppProperties.Read("mail.color.A", false);
		Constants.rowColorB = AppProperties.Read("mail.color.B", false);
		Constants.stopProc = getBooleanVal(AppProperties.Read("proc.Stop", false));		
		Constants.bindPort = getNumericVal(AppProperties.Read("bind.Port", false));
	}

	public static void loadMailProperties()
	{
		Constants.mailServer = AppProperties.Read("mail.Server", false);
		Constants.mailSubject = AppProperties.Read("mail.Subject", false);
		Constants.mailFrom = AppProperties.Read("mail.From", false);
		Constants.mailFromMask = AppProperties.Read("mail.FromMask", false);
		Constants.mailToMask = AppProperties.Read("mail.ToMask", false);
		Constants.mailPort = getNumericVal(AppProperties.Read("mail.Port", false));
		Constants.mailFiles = AppProperties.Read("mail.Files", false);
	}
	
	public static String getMinDescriptor(long minuteVal)
	{
		String descriptor = "";
		
		if (minuteVal == 1)
		{
			descriptor = "minute";
		}
		else
		{
			descriptor = "minutes";
		}
		
		return descriptor;
		
	}
	
	private static boolean getBooleanVal(String fieldVal)
	{
		boolean boolVal = false;
		
		if (fieldVal.toUpperCase().equals("Y") || 
			fieldVal.toUpperCase().equals("YES") ||
			fieldVal.toUpperCase().equals("TRUE"))
		{
			boolVal = true;
		}
		
		return boolVal;
	}
	
	private static int getNumericVal(String fieldVal)
	{
		int numVal = 0;
		
		try
		{
			numVal = Integer.parseInt(fieldVal);
		}
		catch (NumberFormatException nfe)
		{
			WriteLog.WriteToLog("Field Definition, Number format exception.");
			numVal = 0;
		}
		
		return numVal;
	}

	private static long getLongVal(String fieldVal)
	{
		long numVal = 0;
		
		try
		{
			numVal = Long.parseLong(fieldVal.trim());
		}
		catch (NumberFormatException nfe)
		{
			WriteLog.WriteToLog("Property Field Definition, Number format exception.");
			numVal = 0;
		}
		
		return numVal;
	}
	
	private static Date getDateVal(String fieldVal)
	{
		Date theDateVal = null;
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		
		try 
		{
			theDateVal = format.parse(fieldVal);
		}
		catch (ParseException e) 
		{
			WriteLog.WriteToLog("Property Field Definition, Date format exception.");
		}
		
		return theDateVal;
	}
	
	private static Date getTimeVal(String fieldVal)
	{
		Date theDateVal = null;
		DateFormat format = new SimpleDateFormat("hh:mm.ss a", Locale.ENGLISH);
		
		try 
		{
			theDateVal = format.parse(fieldVal);
		}
		catch (ParseException e) 
		{
			WriteLog.WriteToLog("Property Field Definition, Date format exception.");
		}
		
		return theDateVal;
	}
	
	
	private static String getPathVal(String pathNode, String appPath)
	{
		String finalPath = "";
		String tempPath = "";
		
		tempPath = AppProperties.Read(pathNode, false);
		
		if (tempPath.contains("{APPPATH}"))
		{
			int maskEnd = tempPath.indexOf("}");
			String partPath = tempPath.substring(maskEnd + 1);
			
			finalPath = appPath + partPath;
		}
		else
		{
			finalPath = tempPath;
		}
		
		return finalPath;
	}
	
	public static Date summaryWasSent()
	{
		Date summarySent = getDateVal(AppProperties.Read("mail.Summary.Sent", false));
		return summarySent;
	}
}