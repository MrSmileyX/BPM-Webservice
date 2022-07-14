
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import filenet.vw.api.VWDataField;
import filenet.vw.api.VWException;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWWorkObject;

public class RESTConsumer 
{
	public static void main(String[] args) 
	{
		VWSession peSession = null;
		
		boolean inWindow = false;
		boolean stopRcvd = false;
		boolean wobUpdated = false;

		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("-U"))
			{
				Constants.osType = "U";
			}
			else
			{
				Constants.osType = "W";
			}
		}
		else
		{
			Constants.osType = "W";
		}

		RESTProperties.setPropsPath(Constants.osType);
		RESTProperties.initProps();
		
		Security.setProperty("ssl.SocketFactory.provider", "com.ibm.jsse2.SSLSocketFactoryImpl");
		Security.setProperty("ssl.ServerSocketFactory.provider", "com.ibm.jsse2.SSLServerSocketFactoryImpl");
		
		//Initialize Properties
		WriteLog.WriteToLog("Initializing process...");

		ServerSocket socket = null;
		
		WriteLog.WriteToLog("Attempting bind on port " + Constants.bindPort + "...");
		
		try
		{
		    socket = new ServerSocket(Constants.bindPort, 10, InetAddress.getLocalHost());
		    WriteLog.WriteToLog("Bind successful on port " + Constants.bindPort + ", continuing.");
		}
		catch(java.net.BindException b)
		{
			WriteLog.WriteToLog("Process is already running... exiting.");
			System.exit(9999);
		}
		catch(Exception e)
		{
			WriteLog.WriteToLog("Binding error: " + e.toString());
			WriteLog.WriteToLog("Exiting.");
			System.exit(100);
		}
		
		WriteLog.WriteToLog("Retrieving process info...");
		
		String pidName = (ManagementFactory.getRuntimeMXBean().getName());
		String pidVal = pidName.split("@")[0];
		
		WriteLog.WriteToLog("Process Name: " + pidName);
		WriteLog.WriteToLog("Process ID  : " + pidVal);		
		
		WriteLog.WriteToLog(Constants.appName + " Service started.");

		String restResult = RESTfulCall.makeServiceCall(); 

		//Connect to PE
		File fileStop = new File(Constants.stopFile);
		stopRcvd = fileStop.exists();
	
		if (!stopRcvd)
		{
			while (!stopRcvd)
			{
				inWindow = Clock.readyToRun();

				if (inWindow)
				{
					WriteLog.WriteToLog("Service is currently in the processing window. Processing.");
		
					ArrayList<VWWorkObject> myWob = new ArrayList<VWWorkObject>();
					WriteLog.WriteToLog("Searching BPM for specified workitems.");

					//Connect to PE
					peSession = BPMConn.createConnection();
					
					//Get Workitems from Roster
					myWob = BPMSession.findWorkObjects(peSession, Constants.bpmShortRoster);
	
					if (myWob != null && myWob.size() > 0)
					{
						for (int w = 0; w < myWob.size(); w++)
						{
							//For each WobNum... create rest transaction
							RESTTransaction nextTrans = new RESTTransaction();
							
							VWWorkObject nextWob = null;
							VWDataField vwdEIN = null;
							VWDataField vwdAppID = null;
							VWDataField vwdChannel = null;
							VWDataField vwdACN = null;
							VWDataField vwdCaseNum = null;
							
							//WorkType
							String thisEIN = "";
							String thisAppID = "";
							String thisChannel = "";
							String thisACN = "";
							String thisCaseNum = "";
							
							nextWob = myWob.get(w);
							
							if (nextWob != null)
							{
								try 
								{
									vwdEIN = nextWob.getDataField("EIN");
									vwdAppID = nextWob.getDataField("APPID");
									vwdChannel = nextWob.getDataField("WorkType");
									vwdACN = nextWob.getDataField("ACN");
									vwdCaseNum = nextWob.getDataField("GROUP_NUM");
								}
								catch (VWException e)
								{
									WriteLog.WriteToLog("Error retrieving data field!");
									WriteLog.WriteToLog("ERROR: " + e.getMessage());
								}
									
								thisEIN = vwdEIN.getStringValue();
								thisAppID = vwdAppID.getStringValue();
								thisChannel = vwdChannel.getStringValue();
								thisACN = vwdACN.getStringValue();
								thisCaseNum = vwdCaseNum.getStringValue();
								
								nextTrans.setAppID(thisAppID);
								nextTrans.setEIN(thisEIN);
								nextTrans.setACN(thisACN);
								nextTrans.setCaseNumber(thisCaseNum);
								nextTrans.setCaseStage(Constants.restCaseStage);
								
								if (thisChannel.equalsIgnoreCase("PAPER"))
								{
									nextTrans.setChannel(thisChannel);
								}
								else
								{
									nextTrans.setChannel("ELECTRONIC");
								}
								
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date dtNow = new Date();
								nextTrans.setCaseTimeStamp(sdf.format(dtNow));
								
								WriteLog.WriteToLog("________________________________________");
								WriteLog.WriteToLog(nextTrans.requestToString());
								WriteLog.WriteToLog("________________________________________");
								
								//Perform restful webservice call.
								WriteLog.WriteToLog("Making call to Small Group container...");
								String restResultX = RESTfulCall.makeServiceCall(); 
								
								WriteLog.WriteToLog("Call result: " + restResult);
								
								//Update workitem and Dispatch
								try 
								{
									WriteLog.WriteToLog("Updating WOB " + nextWob.getWorkflowNumber());
									wobUpdated = BPMSession.updateWorkItem(nextWob, nextTrans, true);
								} 
								catch (VWException e) 
								{
									WriteLog.WriteToLog("Error Dispatching workitem!");
									WriteLog.WriteToLog("ERROR: " + e.getMessage());
								}	
							}
						}
						
						WriteLog.WriteToLog("Returning to sleep...");
						stopRcvd = Clock.performSleep(fileStop, Constants.breakMilSeconds, Constants.winMilSeconds);
					}
					else
					{
						WriteLog.WriteToLog("No Workitems found to process...");
						stopRcvd = Clock.performSleep(fileStop, Constants.breakMilSeconds, Constants.winMilSeconds);
					}
				}
			}
		}
		
		try 
		{
			socket.close();
		} 
		catch (IOException e) 
		{
			WriteLog.WriteToLog("Error closing socket on port " + Constants.bindPort + "!");
			WriteLog.WriteToLog("ERROR: " + e.getMessage());
		}
	}
}