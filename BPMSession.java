import com.filenet.wcm.api.util.*;
import com.filenet.wcm.api.*;

import org.apache.log4j.Logger;
//import com.wellpoint.common.CommonException;


import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWRosterQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWWorkObject;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWRoster;
import filenet.vw.api.VWRosterDefinition;
import filenet.vw.api.VWExposedFieldDefinition;
import filenet.vw.api.VWParameter;
import filenet.vw.api.VWFieldType;

import java.util.ArrayList;
import java.util.Date;
import java.text.*;
import java.util.Properties;
import java.io.InputStream;

import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.util.UserContext;

import javax.security.auth.Subject;

public class BPMSession
{
	public static Logger logger;

	public static VWSession createPESession()
	{
		String WcmApiConfig = "WcmApiConfig.properties";
		String FileNetConfig = "FileNet.properties";
		String InitialConextFactory = "com.ibm.websphere.naming.WsnInitialContextFactory";

		VWSession oVWSession = new VWSession();

    	Properties wcmProps = new Properties();
    	Properties fnProps = new Properties();

		try
		{
	    	InputStream ins = (InputStream) (new java.io.FileInputStream(WcmApiConfig));
	    	wcmProps.load(ins);
			ins.close();

	    	ins = (InputStream) (new java.io.FileInputStream(FileNetConfig));
	    	fnProps.load(ins);
			ins.close();

			System.setProperty("java.naming.factory.initial", InitialConextFactory);
			System.setProperty("java.security.auth.login.config", fnProps.getProperty("JAASConfWSILocation"));
			System.setProperty("wasp.location",	fnProps.getProperty("WASPLocation"));

			com.filenet.api.core.Connection ceConn = com.filenet.api.core.Factory.Connection.getConnection(wcmProps.getProperty("RemoteServerUrl"));

	        UserContext uc = UserContext.get();

	        Subject subject = UserContext.createSubject(ceConn,
	        											fnProps.getProperty("PEUserid"),
	        											fnProps.getProperty("PEPassword"),
	        											wcmProps.getProperty("jaasConfigurationName"));

	        uc.pushSubject(subject);

	        oVWSession.logon(fnProps.getProperty("PERouter"));

	        return oVWSession;
		}
		catch(Exception e)
		{
			WriteLog.WriteToLog("Error while connecting to PE:" + "\r\n" +
								"JaasLocation: " + fnProps.getProperty("JAASConfWSILocation") + "\r\n" +
								"WaspLocation: " + fnProps.getProperty("WASPLocation") + "\r\n" +
								"RemoteServerUrl: " + wcmProps.getProperty("RemoteServerUrl") + "\r\n" +
								"PEUserID: " + fnProps.getProperty("PEUserid") + "\r\n" +
								"PEPassword: " + Encryption.Encrypt(fnProps.getProperty("PEPassword")) + "\r\n" +
								"JAASConfigName: " + wcmProps.getProperty("jaasConfigurationName") + "\r\n" +
								"PERouter: " + fnProps.getProperty("PERouter"));
			
			WriteLog.WriteToLog(e.getMessage());
			
			return null;
		}

	}

	
	public static boolean listRosters(VWSession bpmSession)
	{
		String[] rosterNames = null;
		VWRoster thisRoster = null;
		VWRosterDefinition rosterDef = null;
		
		int currRow = 0;

		try
		{
			rosterNames = bpmSession.fetchRosterNames(true);
		}
		catch(VWException vwe)
		{
			WriteLog.WriteToLog("Roster retrieval error: " + vwe.getMessage());
		}

		if(rosterNames != null)
		{
			for(currRow = 0; currRow < rosterNames.length - 1; currRow++)
			{
				String rosterName = rosterNames[currRow];
				
				try 
				{
					thisRoster = bpmSession.getRoster(rosterName);
					rosterDef = thisRoster.fetchRosterDefinition();
					
					String rosterDesc = rosterDef.getDescription();				
					
					WriteLog.WriteToLog("Roster Name: " + rosterName);
					WriteLog.WriteToLog("Roster Description: " + rosterDesc);
					WriteLog.WriteToLog("");
					
					try
					{
						VWRoster vwRoster = bpmSession.getRoster(rosterName);
						VWRosterDefinition vwRosterDef = vwRoster.fetchRosterDefinition();
						VWExposedFieldDefinition[] vwDef = vwRosterDef.getFields();
						
						for (int f = 0; f < vwDef.length; f++)
						{
							String fieldName = vwDef[f].getName();
							WriteLog.WriteToLog("\tField Name: " + fieldName);
						}
						
					}
					catch (VWException vwe)
					{
						WriteLog.WriteToLog("Roster Error: " + vwe.getMessage());
					}
					
					WriteLog.WriteToLog("");
					
				}
				catch (VWException e)
				{
					WriteLog.WriteToLog("Error retrieving roster: " + rosterName + "!");
					WriteLog.WriteToLog("ERROR: " + e.getMessage());
				}
				
				thisRoster = null;
				rosterDef = null;
			}
		}
		
		return true;
	}


	public static boolean workItemExists(VWSession bpmSession, String rosterName, String searchVal)
	{
		boolean foundWob = false;
		VWWorkObject searchWob = null;
		
		searchWob = getWorkObject(bpmSession, rosterName, searchVal);
		
		if(searchWob != null)
		{
			foundWob = true;
		}
		else
		{
			foundWob = false;
		}
		
		return foundWob;
	}

	public static VWWorkObject getHistWorkObject(VWSession oVWSession, String sRosterName, String vaDCN) 
	{
		VWRoster vwRoster;
		VWRosterQuery vwRosterQry;
		VWWorkObject vwWob = null;
		
		try 
		{
			vwRoster = oVWSession.getRoster(sRosterName);
			vwRoster.setBufferSize(1000);
			
			vwRosterQry = vwRoster.createQuery("VADCN", 
												new Object[] {vaDCN}, 
												new Object[] {vaDCN},
												  VWQueue.QUERY_READ_BOUND
												+ VWQueue.QUERY_READ_UNWRITABLE
												+ VWQueue.QUERY_READ_LOCKED
												+ VWQueue.QUERY_MIN_VALUES_INCLUSIVE
												+ VWQueue.QUERY_MAX_VALUES_INCLUSIVE, 
												null,
												null, 
												VWFetchType.FETCH_TYPE_WORKOBJECT);
			
			if (vwRosterQry.hasNext()) 
			{
				WriteLog.WriteToLog("Found WOB: " + vaDCN + ".");
				vwWob = (VWWorkObject) vwRosterQry.next();
			}
		}
		catch(Exception e) 
		{
			WriteLog.WriteToLog("Error getting workobject for wobid=" + vaDCN	+ ".");
			WriteLog.WriteToLog("Error: " + e.getMessage());
		}
		
		return vwWob;
	}
	
	public static boolean updateWorkItem(VWWorkObject updWOB, String updRec, boolean finalize)
	{
		String wobVal = "";
		
		int fldsUpdated = 0;
		
		boolean bUpdated = false;
		
		try
		{
			updWOB.doLock(true);
			wobVal = updWOB.getWorkObjectNumber();
			WriteLog.WriteToLog("Locked workobject number " + wobVal + " for update...");
		}
		catch(VWException vwe)
		{
			WriteLog.WriteToLog("WOB locking error: " + wobVal);
			WriteLog.WriteToLog(vwe.getMessage());
			wobVal = "";
			
		}
	
		if (!wobVal.isEmpty())
		{			
			WriteLog.WriteToLog("Updating data for WOB: " + wobVal + "...");
			
			/*
			for (int x = 0; x < updRec.recflds.size(); x++)
			{
				Field currFld = updRec.recflds.get(x);
				
				if (!currFld.sBPMMapping.equals("") && currFld.bUpdateable)
				{
					fldName = currFld.sBPMMapping;
					fldType = currFld.sFieldType;
				
					if (currFld.bFormattable)
					{
						if (currFld.sFormatType.toUpperCase().equals("DATE"))
						{
						
							String tempVal = currFld.getValue();
							
							if (tempVal.isEmpty() || tempVal == null || tempVal.equals("") || tempVal.length() < 8)
							{
								WriteLog.WriteToLog("Invalid Date. Field: " + currFld.sFieldDesc + " (" + currFld.sFieldName +  ") - Value: '" + tempVal + "'.");
								WriteLog.WriteToLog("Setting field value to NULL.");
								fldVal = null;
							}
							else
							{
								try
								{
									String fmtVal = tempVal.substring(4, 6) + "/" + tempVal.substring(6, 8) + "/" + tempVal.substring(0, 4);
									DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
									Date thisDate = df.parse(fmtVal);
									fldVal = thisDate;
								}
								catch(ParseException pe)
								{
									WriteLog.WriteToLog("Date Parsing exception!");
									fldVal = null;
								}
								catch(IllegalArgumentException iae)
								{
									WriteLog.WriteToLog("Date Parsing exception!");
									fldVal = null;
								}
								catch(Exception e)
								{
									WriteLog.WriteToLog("Date Parsing exception!");
									fldVal = null;
								}
							}
						}
						else if (currFld.sFormatType.toUpperCase().equals("TRUE"))
						{
							String sRecVal = currFld.getValue().toUpperCase();
							String sFmtVal = currFld.sFieldFmt.toUpperCase();
							
							String[] sFmtParts = sFmtVal.split("\\|");
							
							String sCompVal = sFmtParts[0];
							String sSetVal = sFmtParts[1];
							
							if (sCompVal.equals(sRecVal))
							{
								fldVal = sSetVal;
							}
							else
							{
								fldVal = "";
							}
						}
						else if (currFld.sFormatType.toUpperCase().equals("FALSE"))
						{
							String sRecVal = currFld.getValue().toUpperCase();
							String sFmtVal = currFld.sFieldFmt.toUpperCase();
							
							String[] sFmtParts = sFmtVal.split("\\|");
							
							String sCompVal = sFmtParts[0];
							String sSetVal = sFmtParts[1];
							
							if (sCompVal.equals(sRecVal))
							{
								fldVal = "";
							}
							else
							{
								fldVal = sSetVal;                                                                   
							}
						}
					}
					else
					{
						String thisVal = currFld.getValue();
						
						if (thisVal.isEmpty() || thisVal == null || thisVal.equals(""))
						{
							fldVal = "";
						}
						
						fldVal = thisVal;
					}
					
					if (fldVal != null)
					{
						try
						{
							updWOB.setFieldValue(fldName, fldVal, true);
							fldsUpdated++;
						}
						catch(VWException vwe)
						{
							WriteLog.WriteToLog("Field Update Error: " + wobVal);
							WriteLog.WriteToLog("Field: " + fldName + "\tValue: " + currFld.getValue()); 
						}
					}
				}
			}
			*/
			
			WriteLog.WriteToLog("Updated " + fldsUpdated + " fields for workobject " + wobVal + ".");
			
			try
			{
				if (finalize)
				{
					WriteLog.WriteToLog("Dispatching workobject " + wobVal + "...");
					updWOB.doDispatch();
					WriteLog.WriteToLog("Dispatched workobject " + wobVal + ".");
				}
				else
				{
					WriteLog.WriteToLog("Saving workobject " + wobVal + "...");
					updWOB.doSave(true);
					WriteLog.WriteToLog("Saved workobject " + wobVal + ".");	
				}
			}
			catch(VWException vwe)
			{
				Constants.peError = checkPESession(vwe.getMessage());
				
				WriteLog.WriteToLog("WOB Dispatch error: " + wobVal);
				WriteLog.WriteToLog(vwe.getMessage());
			}
				
			WriteLog.WriteToLog("Updated data for WOB: " + wobVal + ".");
			bUpdated = true;
		}
		else
		{
			WriteLog.WriteToLog("Invalid WOB, unable to update.");
			bUpdated = false;
		}
				
		return bUpdated;
	}
		
	
	public static void listParmNames(VWStepElement currStep)
	{			
		try
		{
			VWParameter[] Parms = currStep.getParameters(VWFieldType.ALL_FIELD_TYPES, 1);
			
			for(int x = 0; x < Parms.length; x++)
			{
				WriteLog.WriteToLog("Parameter " + x + ":\t" + Parms[x].getName());
			}
		}
		catch(VWException vwe)
		{
			WriteLog.WriteToLog("Error getting parameters!");
			WriteLog.WriteToLog(vwe.getMessage());
		}	
	}
	
	public static void setVWProperty(VWStepElement vwStepElement, String propName, Object propValue)
	{
		try
		{
			if (propValue != null)
			{
				vwStepElement.setParameterValue(propName, propValue, true);
			}
		}
		catch(Exception e)
		{
			WriteLog.WriteToLog("Failed to set the property value for " + propName);
			WriteLog.WriteToLog(e.getMessage());
		}
	}

	public static void setWobProperty(VWWorkObject vwStepElement, String propName, Object propValue)
	{
		if (propValue != null)
		{
			try
			{
				vwStepElement.setFieldValue(propName, propValue, true);
			}
			catch(Exception e)
			{
				WriteLog.WriteToLog("Failed to set the proeprty value for " + propName );
			}
		}
	}

	public static String getRecValue(String s)
	{
		if (s == null)
		{
			return "";
		}
		else
		{
			return s;
		}
	}

	public static void listWorkItems(VWSession peSession, String sRosterName)
	{
		
		VWRoster vwRoster;
		VWRosterQuery vwRosterQry;
		
		VWWorkObject vwWob;
		
		try
		{
			
			vwRoster = peSession.getRoster(sRosterName);
			vwRoster.setBufferSize(10000);
						
			vwRosterQry  = vwRoster.createQuery(null, 
												null, 
												null,
												  VWQueue.QUERY_READ_BOUND
												+ VWQueue.QUERY_READ_UNWRITABLE
												+ VWQueue.QUERY_READ_LOCKED
												+ VWQueue.QUERY_MIN_VALUES_INCLUSIVE
												+ VWQueue.QUERY_MAX_VALUES_INCLUSIVE, 
												null, 
												null, 
												VWFetchType.FETCH_TYPE_WORKOBJECT);
			
	        if (vwRosterQry.hasNext()) 
	        {
	        	while (vwRosterQry.hasNext())
	        	{	
	        		vwWob = (VWWorkObject) vwRosterQry.next();
	        		System.out.println(vwWob.getWorkObjectNumber());
	        	}
	        }
		}
		catch(Exception e)
		{
			WriteLog.WriteToLog("Error while retrieving the workitem.");
			WriteLog.WriteToLog(e.getMessage());
		}
		

	}
	
	public static boolean UpdateRosterWorkItem(VWSession peSession, String sRosterName, String wobNum, RESTTransaction updRec)
	{
		
		VWRoster vwRoster;
		VWRosterQuery vwRosterQry;
		VWWorkObject updWob = null;
		
		boolean wasUpdated = false;
	
		try
		{
			
			vwRoster = peSession.getRoster(sRosterName);
			vwRoster.setBufferSize(10);
					
			vwRosterQry  = vwRoster.createQuery(null, 
												null, 
												null,
												  VWQueue.QUERY_READ_BOUND
												+ VWQueue.QUERY_READ_UNWRITABLE
												+ VWQueue.QUERY_READ_LOCKED
												+ VWQueue.QUERY_MIN_VALUES_INCLUSIVE
												+ VWQueue.QUERY_MAX_VALUES_INCLUSIVE, 
												null, 
												null, 
												VWFetchType.FETCH_TYPE_WORKOBJECT);
			
	        if (vwRosterQry.hasNext()) 
	        {	
	        	updWob = (VWWorkObject) vwRosterQry.next();
	        	
	        	
	        }
		}
		catch(Exception e)
		{
			WriteLog.WriteToLog("Error while retrieving the workitem.");
			WriteLog.WriteToLog(e.getMessage());
		}
		
		return wasUpdated;
	}

	public static boolean checkPESession(String errMessage)
	{
		boolean bConnErr = false;
		
		if (errMessage.toUpperCase().contains("TIMEOUT") ||
			errMessage.toUpperCase().contains("CONN_ABORT") ||
			errMessage.toUpperCase().contains("COMM_FAILURE"))
		{
			bConnErr = true;
		}
		
		return bConnErr;
	}

	
	public static VWWorkObject findWorkObject(VWSession oVWSession, String sRosterName, String sClaimNum, String sSuffix) 
	{
		VWRoster vwRoster;
		VWRosterQuery vwRosterQry;
		VWWorkObject vwWob = null;
		int queryFlags;
		
		String srchFilter = "DCN = :a";
		
		Object[] minVals = {sClaimNum};
		Object[] maxVals = {sClaimNum};
		Object[] subsVars = {sClaimNum};
		
		queryFlags =   VWQueue.QUERY_READ_BOUND	+ 
					   VWQueue.QUERY_READ_UNWRITABLE + 
					   VWQueue.QUERY_READ_LOCKED + 
					   VWQueue.QUERY_MIN_VALUES_INCLUSIVE + 
					   VWQueue.QUERY_MAX_VALUES_INCLUSIVE; 
				
		try 
		{
			vwRoster = oVWSession.getRoster(sRosterName);
			vwRoster.setBufferSize(1000);
			
			vwRosterQry = vwRoster.createQuery(null, 
											   minVals, 
											   maxVals,
											   queryFlags, 
											   srchFilter,
											   subsVars, 
											   VWFetchType.FETCH_TYPE_WORKOBJECT);
			
			while (vwRosterQry.hasNext()) 
			{
				vwWob = (VWWorkObject) vwRosterQry.next();
				WriteLog.WriteToLog("Found WOB: " + vwWob.getWorkflowNumber() + ".");
			}
		}
		catch(Exception e) 
		{
			Constants.peError = checkPESession(e.getMessage());
						
			WriteLog.WriteToLog("Error getting workobject for claim number: " + sClaimNum + " " + sSuffix + ".");
			WriteLog.WriteToLog("Error: " + e.getMessage());
		}
		
		return vwWob;

	}

	public static VWWorkObject findAdjWorkObject(VWSession oVWSession, String sRosterName, String sClaimNum, String sSuffix) 
	{
		VWRoster vwRoster;
		VWRosterQuery vwRosterQry;
		VWWorkObject vwWob = null;
		int queryFlags;
		
		String srchFilter = "VADCN = :a and VA_DCN_SUFFIX = :b and CLMRECTYPE = :c";
		
		Object[] minVals = {sClaimNum, sSuffix, "ADJ"};
		Object[] maxVals = {sClaimNum, sSuffix, "ADJ"};
		Object[] subsVars = {sClaimNum, sSuffix, "ADJ"};
		
		queryFlags =   VWQueue.QUERY_READ_BOUND	+ 
					   VWQueue.QUERY_READ_UNWRITABLE + 
					   VWQueue.QUERY_READ_LOCKED + 
					   VWQueue.QUERY_MIN_VALUES_INCLUSIVE + 
					   VWQueue.QUERY_MAX_VALUES_INCLUSIVE; 
				
		try 
		{
			vwRoster = oVWSession.getRoster(sRosterName);
			vwRoster.setBufferSize(1000);

			vwRosterQry = vwRoster.createQuery(null, 
											   minVals, 
											   maxVals,
											   queryFlags, 
											   srchFilter,
											   subsVars, 
											   VWFetchType.FETCH_TYPE_WORKOBJECT);
			
			if (vwRosterQry.hasNext()) 
			{
				vwWob = (VWWorkObject) vwRosterQry.next();
				WriteLog.WriteToLog("Found WOB: " + vwWob.getWorkflowNumber() + ".");
			}
		}
		catch(Exception e) 
		{
			Constants.peError = checkPESession(e.getMessage());
						
			WriteLog.WriteToLog("Error getting workobject for claim number: " + sClaimNum + " " + sSuffix + ".");
			WriteLog.WriteToLog("Error: " + e.getMessage());
		}
		
		return vwWob;

	}
	
	public static ArrayList<VWWorkObject> findWorkObjects(VWSession oVWSession, String sRosterName) 
	{
		VWRoster vwRoster;
		VWRosterQuery vwRosterQry;
		ArrayList<VWWorkObject> vwWob = new ArrayList<VWWorkObject>();
		int queryFlags;
		
		String srchFilter = "QUEUENAME = :a";
		
		Object[] minVals = {"HIX_SALES_RESTFUL_HOLD"};
		Object[] maxVals = {"HIX_SALES_RESTFUL_HOLD"};
		Object[] subsVars = {"HIX_SALES_RESTFUL_HOLD"};
		
		queryFlags =   VWQueue.QUERY_READ_BOUND	+ 
					   VWQueue.QUERY_READ_UNWRITABLE + 
					   VWQueue.QUERY_READ_LOCKED + 
					   VWQueue.QUERY_MIN_VALUES_INCLUSIVE + 
					   VWQueue.QUERY_MAX_VALUES_INCLUSIVE; 
				
		try 
		{
			vwRoster = oVWSession.getRoster(sRosterName);
			vwRoster.setBufferSize(1000);

			vwRosterQry = vwRoster.createQuery(null, 
											   minVals, 
											   maxVals,
											   queryFlags, 
											   srchFilter,
											   subsVars, 
											   VWFetchType.FETCH_TYPE_WORKOBJECT);
			
			while (vwRosterQry.hasNext()) 
			{
				VWWorkObject nextWob = (VWWorkObject) vwRosterQry.next();
				vwWob.add(nextWob);
				WriteLog.WriteToLog("Found WOB: " + nextWob.getWorkflowNumber() + ".");
			}
		}
		catch(Exception e) 
		{
			Constants.peError = checkPESession(e.getMessage());
						
			WriteLog.WriteToLog("Error getting workobjects!");
			WriteLog.WriteToLog("Error: " + e.getMessage());
		}
		
		return vwWob;

	}
	
	public static VWWorkObject getWorkObject(VWSession oVWSession, String sRosterName, String sWobNum) 
	{
		VWRoster vwRoster;
		VWRosterQuery vwRosterQry;
		VWWorkObject vwWob = null;
		
		try 
		{
			vwRoster = oVWSession.getRoster(sRosterName);
			vwRoster.setBufferSize(1000);
			
			vwRosterQry = vwRoster.createQuery("F_WobNum", 
												new Object[] {sWobNum}, 
												new Object[] {sWobNum},
												  VWQueue.QUERY_READ_BOUND
												+ VWQueue.QUERY_READ_UNWRITABLE
												+ VWQueue.QUERY_READ_LOCKED
												+ VWQueue.QUERY_MIN_VALUES_INCLUSIVE
												+ VWQueue.QUERY_MAX_VALUES_INCLUSIVE, 
												null,
												null, 
												VWFetchType.FETCH_TYPE_WORKOBJECT);
			
			if (vwRosterQry.hasNext()) 
			{
				WriteLog.WriteToLog("Found WOB: " + sWobNum + ".");
				vwWob = (VWWorkObject) vwRosterQry.next();
			}
		}
		catch(Exception e) 
		{
			Constants.peError = checkPESession(e.getMessage());
						
			WriteLog.WriteToLog("Error getting workobject for wobid=" + sWobNum	+ ".");
			WriteLog.WriteToLog("Error: " + e.getMessage());
		}
		
		return vwWob;

	}

	public static boolean updateWorkItem(VWWorkObject updWOB, RESTTransaction restRec, boolean finalize)
	{
		String fldName = "";
		String fldVal = "";
		String wobVal = "";
		
		int fldsUpdated = 0;
		
		boolean bUpdated = false;
				
		try
		{
			updWOB.doLock(true);
			wobVal = updWOB.getWorkObjectNumber();
			WriteLog.WriteToLog("Locked workobject number " + wobVal + " for update...");
		}
		catch(VWException vwe)
		{
			WriteLog.WriteToLog("WOB locking error: " + wobVal);
			WriteLog.WriteToLog(vwe.getMessage());
			wobVal = "";
		}
	
		if (!wobVal.isEmpty())
		{			
			WriteLog.WriteToLog("Updating data for WOB: " + wobVal + "...");
	
			ArrayList<BPMField> fieldData = new ArrayList<BPMField>();
				
			fieldData.add(new BPMField(Constants.bpmStatusFld, restRec.restRespStatus));
			fieldData.add(new BPMField(Constants.bpmTmStmpFld, restRec.restRespTimeStamp));
			fieldData.add(new BPMField(Constants.bpmWorkStatFld, Constants.bpmWorkStatVal));
			fieldData.add(new BPMField(Constants.bpmUserNmFld, Constants.bpmUserNmVal));
			fieldData.add(new BPMField(Constants.bpmStatDtFld, restRec.restRespTimeStamp));
			fieldData.add(new BPMField(Constants.bpmFromQFld, Constants.bpmFromQVal));
			fieldData.add(new BPMField(Constants.bpmErrorFld, restRec.restRespErrMsg));
			
			if (restRec.restRespStatus.equals(Constants.restPassMsg))
			{
				fieldData.add(new BPMField(Constants.bpmTransIDFld, restRec.getRespTransID()));
				fieldData.add(new BPMField(Constants.bpmStageFld, restRec.restCaseStage));
			}
			
	
			for (int i = 0; i < fieldData.size(); i++)
			{
				try
				{		
					BPMField currFld = fieldData.get(i);
					
					fldName = currFld.getFieldName();
					fldVal = currFld.getFieldValue();
					
					updWOB.setFieldValue(fldName, fldVal, true);
					fldsUpdated++;
				}
				catch(VWException vwe)
				{
					WriteLog.WriteToLog("Field Update Error: " + wobVal);
					WriteLog.WriteToLog("Field: " + fldName + "\tValue: " + fldVal); 
				}
			}
					
			WriteLog.WriteToLog("Updated " + fldsUpdated + " fields for workobject " + wobVal + ".");
			
			try
			{
				if (finalize)
				{
					WriteLog.WriteToLog("Dispatching workobject " + wobVal + "...");
					updWOB.doDispatch();
					WriteLog.WriteToLog("Dispatched workobject " + wobVal + ".");
				}
				else
				{
					WriteLog.WriteToLog("Saving workobject " + wobVal + "...");
					updWOB.doSave(true);
					WriteLog.WriteToLog("Saved workobject " + wobVal + ".");	
				}
			}
			catch(VWException vwe)
			{
				Constants.peError = checkPESession(vwe.getMessage());
				
				WriteLog.WriteToLog("WOB Dispatch error: " + wobVal);
				WriteLog.WriteToLog(vwe.getMessage());
			}
				
			WriteLog.WriteToLog("Updated data for WOB: " + wobVal + ".");
			bUpdated = true;
		}
		else
		{
			WriteLog.WriteToLog("Invalid WOB, unable to update.");
			bUpdated = false;
		}
				
		return bUpdated;
	}
	
	public static boolean finalizeWorkItem(VWWorkObject updWOB)
	{
		String fldName = "";
		String wobVal = "";
		
		int fldsUpdated = 0;
		
		boolean bFinalized = false;
		
		Object fldVal = null;
		
		try
		{
			updWOB.doLock(true);
			wobVal = updWOB.getWorkObjectNumber();
			WriteLog.WriteToLog("Locked workobject number " + wobVal + " for update...");
		}
		catch(VWException vwe)
		{
			Constants.peError = checkPESession(vwe.getMessage());
			
			WriteLog.WriteToLog("WOB locking error: " + wobVal);
			WriteLog.WriteToLog(vwe.getMessage());
			wobVal = "";
			
		}
	
		if (!wobVal.isEmpty())
		{			
			WriteLog.WriteToLog("Finalizing WOB: " + wobVal + "...");
			
			fldName = "WORKSTATUS";
			fldVal = "FROUTE";
			
			if (fldVal != null)
			{
				try
				{
					updWOB.setFieldValue(fldName, fldVal, true);
					fldsUpdated++;
				}
				catch(VWException vwe)
				{
					WriteLog.WriteToLog("Field Update Error: " + wobVal);
					WriteLog.WriteToLog("Field: " + fldName + "\tValue: " + fldVal); 
				}
			}
			
			WriteLog.WriteToLog("Updated " + fldsUpdated + " fields for workobject " + wobVal + ".");
			
			/*
			try
			{
				WriteLog.WriteToLog("Dispatching workobject " + wobVal + "...");
				updWOB.doDispatch();
				WriteLog.WriteToLog("Dispatched workobject " + wobVal + ".");
			
			}
			catch(VWException vwe)
			{
				WriteLog.WriteToLog("WOB Dispatch error: " + wobVal);
				WriteLog.WriteToLog(vwe.getMessage());
			}
			*/
				
			WriteLog.WriteToLog("Finalized WOB: " + wobVal + ".");
			bFinalized = true;
		}
		else
		{
			WriteLog.WriteToLog("Invalid WOB, unable to update.");
			bFinalized = false;
		}
				
		return bFinalized;
	}
	
	
	public static void closePESession(VWSession bpmSession)
	{
		WriteLog.WriteToLog("Closing PE connection");

		try
		{
			WriteLog.WriteToLog("PE Connection closed.");
			bpmSession.logoff();
		}
		catch(VWException ve)
		{
			WriteLog.WriteToLog(ve.getMessage());
		}
	}

}