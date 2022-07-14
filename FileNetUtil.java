import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.security.auth.Subject;
import com.filenet.api.core.Factory;

import org.apache.log4j.Logger;

import com.filenet.api.util.UserContext;
import com.filenet.wcm.api.ObjectFactory;
import com.filenet.wcm.api.ObjectStore;
import com.filenet.wcm.api.Session;

//import com.wellpoint.common.CommonException;

import filenet.vw.api.VWAttachment;
import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWModeType;
import filenet.vw.api.VWParameter;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWRoster;
import filenet.vw.api.VWRosterQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObject;

public class FileNetUtil 
{

	public static Logger logger;
	
	public static VWSession createPESessionManual() 
	{
		VWSession oVWSession = new VWSession();
	
    	Properties wcmProps = new Properties();	 
    	Properties fnProps = new Properties();	
		
		try{
			
			WriteLog.WriteToLog("Setting system properties...");
			
			WriteLog.WriteToLog("Setting initial context factory: " + Constants.InitialConextFactory);
			System.setProperty("java.naming.factory.initial", Constants.InitialConextFactory); 
			
			WriteLog.WriteToLog("Setting java security config: " + "C:\\Temp\\jaas.conf.WebSphere");
			System.setProperty("java.security.auth.login.config", "C:\\Temp\\jaas.conf.WebSphere");
			
			WriteLog.WriteToLog("Setting WASP location: " + "C:\\Personal\\RADWorkSpace\\WelcomeKits\\wsi");
			System.setProperty("wasp.location",	"C:\\Personal\\RADWorkSpace\\WelcomeKits\\wsi");
						
			WriteLog.WriteToLog("Getting FileNet Connection...");
			com.filenet.api.core.Connection ceConn = com.filenet.api.core.Factory.Connection.getConnection("corbaloc::va10duvwbs004:9810,:va10duvwbs004:9811/DEV_CellManager01/DEVce_cluster/FileNet/Engine");
	        
			WriteLog.WriteToLog("Getting user context...");
			UserContext uc = UserContext.get();
			
			WriteLog.WriteToLog("Creating user subject...");
			WriteLog.WriteToLog("PE User ID : " + "srcfnp8devb2image");
			WriteLog.WriteToLog("PE Password: " + "");
			WriteLog.WriteToLog("Jaas Config: " + "FileNetP8");
			
	        Subject subject = UserContext.createSubject(ceConn, "srcfnp8devb2image", "FNp8devb2image2015", "FileNetP8");
	        
	        WriteLog.WriteToLog("Pushing subject...");
	        uc.pushSubject(subject);

	        String peRouterVal = "REG1"; 
	        WriteLog.WriteToLog("PE Router: " + peRouterVal);
	        
	        WriteLog.WriteToLog("Logging onto " + peRouterVal);
	        oVWSession.logon(peRouterVal);
	    	
	        return oVWSession;
		}
		catch(Exception e)
		{
			System.out.println("Error: " + e.getMessage());
			
			System.out.println("Error while connecting to PE:  \n"
							 + "\n      JaasLocation = \t" + fnProps.getProperty("JAASConfWSILocation")
							 + "\n      WaspLocation = \t" + fnProps.getProperty("WASPLocation")
							 + "\n      RemoteServerUrl = " + wcmProps.getProperty("RemoteServerUrl")
							 + "\n      PEUserID = \t" + fnProps.getProperty("PEUserid")
							 + "\n      PEPassword = \t" + fnProps.getProperty("PEPassword")
							 + "\n      JAASConfigName = \t" + wcmProps.getProperty("jaasConfigurationName")      
							 + "\n      PERouter = \t" + fnProps.getProperty("PERouter") + "\n");
			
			return null;	
		}
	        
	}
	
	public static VWSession createPESession() 
	{
		VWSession oVWSession = new VWSession();
	
    	Properties wcmProps = new Properties();	 
    	Properties fnProps = new Properties();	
		
		try{
			WriteLog.WriteToLog("Getting WcmApiConfig " + Constants.WcmApiConfig);
	    	InputStream ins = (InputStream) (new java.io.FileInputStream(Constants.WcmApiConfig));   	
	    	
	    	WriteLog.WriteToLog("Getting WcmApiConfig properties...");
	    	wcmProps.load(ins);
			ins.close();
			
			WriteLog.WriteToLog("Getting FileNET Config " + Constants.FileNetConfig);
			ins = (InputStream) (new java.io.FileInputStream(Constants.FileNetConfig));
	    	
			WriteLog.WriteToLog("Getting FileNet Config properties...");
			fnProps.load(ins);
			ins.close();
			
			WriteLog.WriteToLog("Setting system properties...");
			
			WriteLog.WriteToLog("Setting initial context factory: " + Constants.InitialConextFactory);
			System.setProperty("java.naming.factory.initial", Constants.InitialConextFactory); 
			
			WriteLog.WriteToLog("Setting java security config: " + fnProps.getProperty("JAASConfWSILocation"));
			System.setProperty("java.security.auth.login.config", fnProps.getProperty("JAASConfWSILocation"));
			
			WriteLog.WriteToLog("Setting WASP location: " + fnProps.getProperty("WASPLocation"));
			System.setProperty("wasp.location",	fnProps.getProperty("WASPLocation"));
						
			WriteLog.WriteToLog("Getting FileNet Connection...");
			com.filenet.api.core.Connection ceConn = com.filenet.api.core.Factory.Connection.getConnection(wcmProps.getProperty("RemoteServerUrl"));
	        
			WriteLog.WriteToLog("Getting user context...");
			UserContext uc = UserContext.get();
			
			WriteLog.WriteToLog("Creating user subject...");
			WriteLog.WriteToLog("PE User ID : " + fnProps.getProperty("PEUserid"));
			WriteLog.WriteToLog("PE Password: " + Encryption.Encrypt(fnProps.getProperty("PEPassword")));
			WriteLog.WriteToLog("Jaas Config: " + wcmProps.getProperty("jaasConfigurationName"));
			
	        Subject subject = UserContext.createSubject(ceConn,
	        											fnProps.getProperty("PEUserid"),
	        											fnProps.getProperty("PEPassword"),
	        											wcmProps.getProperty("jaasConfigurationName"));
	        
	        WriteLog.WriteToLog("Pushing subject...");
	        uc.pushSubject(subject);

	        String peRouterVal = fnProps.getProperty("PERouter"); 
	        WriteLog.WriteToLog("PE Router: " + peRouterVal);
	        
	        WriteLog.WriteToLog("Logging onto " + peRouterVal);
	        oVWSession.logon(peRouterVal);
	    	
	        return oVWSession;
		}
		catch(Exception e)
		{
			WriteLog.WriteToLog("Error: " + e.getMessage());
			
			WriteLog.WriteToLog("Error while connecting to PE:" + "\r\n" +
								"JaasLocation: " + fnProps.getProperty("JAASConfWSILocation") + "\r\n" +
								"WaspLocation: " + fnProps.getProperty("WASPLocation") + "\r\n" +
								"RemoteServerUrl: " + wcmProps.getProperty("RemoteServerUrl") + "\r\n" +
								"PEUserID: " + fnProps.getProperty("PEUserid") + "\r\n" +
								"PEPassword: " + Encryption.Encrypt(fnProps.getProperty("PEPassword")) + "\r\n" +
								"JAASConfigName: " + wcmProps.getProperty("jaasConfigurationName") + "\r\n" +
								"PERouter: " + fnProps.getProperty("PERouter"));
			
			WriteLog.WriteToLog(e.getCause().toString());
			WriteLog.WriteToLog(e.getStackTrace().toString());
			
			return null;	
		}
	        
	}


	public static Session createCESession(String uid, String pwd) 
	{
		Session oCESession;
		String appId = "com.wellpoint.bpm.CE";
	
		try 
		{
			oCESession = ObjectFactory.getSession(appId, null, uid, pwd);
			oCESession.verify();
			return oCESession;
		}
		catch (Exception ve) 
		{
			WriteLog.WriteToLog("Error while connecting to CE using AppID: " + appId + ", User ID: " + uid + "!");
			WriteLog.WriteToLog("ERROR: " + ve.getMessage());
			return null;
		}
	}

	public static ObjectStore obtainCEObjectStore(Session oCESession, String sObjectStore) 
	{
		ObjectStore objStore = null;
		
		try 
		{
			objStore = ObjectFactory.getObjectStore(sObjectStore, oCESession);
			return objStore;
		} 
		catch (Exception ve) 
		{
			WriteLog.WriteToLog("Error while obtaining ObjectStore " + sObjectStore + "!");
			WriteLog.WriteToLog("ERROR: " + ve.getMessage());
			return null;
		}
	}

	public static VWWorkObject getWorkObject(VWSession oVWSession, String sWobNum, String sRosterName) 
	{
		VWRoster vwRoster;
		VWRosterQuery vwRosterQry;
		VWWorkObject vwWob = null;
		
		try 
		{

			vwRoster = oVWSession.getRoster(sRosterName);
			vwRoster.setBufferSize(10);
			vwRosterQry = vwRoster.createQuery("F_WobNum", new Object[] { sWobNum }, new Object[] { sWobNum },
												VWQueue.QUERY_READ_BOUND + 
												VWQueue.QUERY_READ_UNWRITABLE + 
												VWQueue.QUERY_READ_LOCKED + 
												VWQueue.QUERY_MIN_VALUES_INCLUSIVE + 
												VWQueue.QUERY_MAX_VALUES_INCLUSIVE, 
												null, null, VWFetchType.FETCH_TYPE_WORKOBJECT); // VWQueue.
			
			if (vwRosterQry.hasNext()) 
			{
				vwWob = (VWWorkObject) vwRosterQry.next();
			}
		} 
		catch (Exception e) 
		{
			WriteLog.WriteToLog("Error getting workobject for wobid=" + sWobNum	+ "!");
			WriteLog.WriteToLog("ERROR: " + e.getMessage());
		}
		return vwWob;

	}

	public static void closePESession(VWSession oVWSession) 
	{
		WriteLog.WriteToLog("Closing PE connection");
		
		try 
		{
			oVWSession.logoff();
		}
		catch (VWException ve) 
		{
			WriteLog.WriteToLog(ve.getMessage());
		}
	}

	public static boolean parameterExists(VWParameter[] vwFields, String sFldName) throws Exception 
	{
		boolean bExists = false;
	
		if (vwFields != null) 
		{
			for (int ii = 0; ii <= vwFields.length - 1; ii++) 
			{
				if (sFldName.equals(vwFields[ii].getName())	&& vwFields[ii].getMode() > VWModeType.MODE_TYPE_IN) 
				{
					bExists = true;
					break;
				}
			}
		}
		
		return bExists;
	}

	public static VWParameter getVWParameter(VWParameter[] vwFields, String sFldName) throws Exception 
	{
		VWParameter vwParam = null;
		
		if (vwFields != null) 
		{
			for (int ii = 0; ii <= vwFields.length - 1; ii++) 
			{
				if (sFldName.equals(vwFields[ii].getName()) && vwFields[ii].getMode() > VWModeType.MODE_TYPE_IN) 
				{
					vwParam = vwFields[ii];
					break;
				}
			}
		}
		
		return vwParam;
	}
	
	public static boolean ValidateSearchFldNames(VWParameter[] vwFields, String[] searchValueList) throws Exception 
	{
		for (int i = 0; i < searchValueList.length; i++) 
		{
			if (!parameterExists(vwFields, searchValueList[i])) 
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static String getAttachmentID(VWStepElement stepElement) throws Exception 
	{
		
		VWAttachment vwattach[] = new VWAttachment[10];
		vwattach = (VWAttachment[]) stepElement.getParameterValue("SupportDocs");
		
		String guid = vwattach[0].getVersion();
		
		if (guid != null && guid.length() > 0)
		{
			WriteLog.WriteToLog("Using version: " + vwattach[0].toString());
		}
		else
		{
			guid = vwattach[0].getId();
			WriteLog.WriteToLog("Using version: " + vwattach[0].getId());
		}
		
		WriteLog.WriteToLog("StringVersion: " + vwattach[0].toString());
				
		return guid;
	}
	

	public static String getSimpleDate(Date dt) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		formatter.setTimeZone(TimeZone.getDefault());
		return formatter.format(dt);
	}
	
	
	public static int getYear(String dt) throws Exception 
	{
        DateFormat formatter ; 
        Date date ; 
        
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        date = (Date)formatter.parse(dt); 
        
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
		
        return cal.get(Calendar.YEAR); 
	}
	
	public static int getMonth(String dt) throws Exception 
	{
        DateFormat formatter ; 
        Date date ; 
        
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        date = (Date)formatter.parse(dt); 
        
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
		
        return cal.get(Calendar.MONTH); 
	}
	
	public static int getDay(String dt) throws Exception 
	{
        DateFormat formatter ; 
        Date date ; 
    
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        date = (Date)formatter.parse(dt); 
        
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
		
        return cal.get(Calendar.DATE); 
	}

}
