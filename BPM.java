import org.apache.log4j.Logger;

import filenet.vw.api.*;

public class BPM 
{

	public VWSession oVWSession;
	public  Logger logger = Logger.getLogger(Constants.loggerClass);
	public static Logger applogger = Logger.getLogger(Constants.apploggerClass);
	
	public VWWorkObject getWorkObject(String sWobNum, String sRosterName)
	{
		VWRoster vwRoster;
		VWRosterQuery vwRosterQry;
		VWWorkObject vwWob = null;
		
		try
		{
			
			vwRoster=oVWSession.getRoster(sRosterName);
			vwRoster.setBufferSize(10);
			
			vwRosterQry  = vwRoster.createQuery("F_WobNum", new Object[]{sWobNum}, new Object[]{sWobNum},
												VWQueue.QUERY_READ_BOUND + VWQueue.QUERY_READ_UNWRITABLE + VWQueue.QUERY_READ_LOCKED +
												VWQueue.QUERY_MIN_VALUES_INCLUSIVE + VWQueue.QUERY_MAX_VALUES_INCLUSIVE, 
												null, null, VWFetchType.FETCH_TYPE_WORKOBJECT);
		    
			if  (vwRosterQry.hasNext()) 
			{
		    	vwWob = (VWWorkObject) vwRosterQry.next();
		    }
		}
		catch( Exception e)
		{
			logger.error("Error getting workobject for wobid=" + sWobNum +". " + e.getMessage());
		}
		
		return vwWob;
	}
	
	public VWRosterQuery getWorkFlow(String sWobNum, String sRosterName)
	{
		VWRoster vwRoster;
		VWRosterQuery vwRosterQry=null;
		
		try
		{
			
			vwRoster=oVWSession.getRoster(sRosterName);
			vwRoster.setBufferSize(10);
			
			vwRosterQry  = vwRoster.createQuery("F_WobNum", new Object[]{sWobNum}, new Object[]{sWobNum},
												VWQueue.QUERY_READ_BOUND + VWQueue.QUERY_READ_UNWRITABLE + VWQueue.QUERY_READ_LOCKED +
												VWQueue.QUERY_MIN_VALUES_INCLUSIVE + VWQueue.QUERY_MAX_VALUES_INCLUSIVE,
												null, null, VWFetchType.FETCH_TYPE_WORKOBJECT);
	
		}
		catch( Exception e)
		{
			logger.error("Error getting workobject for wobid=" + sWobNum +". " + e.getMessage());
		}
		
		return vwRosterQry;
		
	}
	
	public VWStepElement getStepElement(String queueName, String wobNumber) 
	{
	
	    VWQueue queue;
	    VWStepElement vwStepElement = null;
	
	    try 
	    {
	        logger.debug("Loading Step Element with wobNumber " + wobNumber + " from queue " + queueName);
	
	        queue = oVWSession.getQueue(queueName);
	
	        VWQueueQuery queueQuery = queue.createQuery("F_WobNum", new Object[]{wobNumber}, new Object[]{wobNumber},
	        											VWQueue.QUERY_READ_BOUND + VWQueue.QUERY_READ_UNWRITABLE +
	        											VWQueue.QUERY_MIN_VALUES_INCLUSIVE + VWQueue.QUERY_MAX_VALUES_INCLUSIVE, 
	        											null, null, VWFetchType.FETCH_TYPE_STEP_ELEMENT);
	
	        if (queueQuery.hasNext()) 
	        {
	            vwStepElement = (VWStepElement) queueQuery.next();
	            vwStepElement.doLock(true);
	            //vwStepElement.doReassign(oVWSession.fetchCurrentUserInfo().getName(), false, FileNetField.INBOX);
	        }  
	    } 
	    catch (VWException e) 
	    {
	        logger.error("Couldn't load the element from the queue.", e);
	        throw new RuntimeException(e);
	    }
	    
	    return vwStepElement;
	}
	
	public void closePESession()
	{
		logger.debug("closing PE connection");
		
		try
		{
			oVWSession.logoff();
		}
		catch(VWException ve)
		{
			logger.error(ve.getMessage());
		}
	}
	
	public void UnlockRosterWorkItems(String sRosterName, String sFilter)
	{
			VWRoster vwRoster;
			VWRosterQuery vwRosterQry;
			VWWorkObject vwWob;
		
			int nCount=0;
			int i=0;
			
		try
		{
			vwRoster=oVWSession.getRoster(sRosterName);
			vwRoster.setBufferSize(100);
			nCount = vwRoster.fetchCount();
			
			System.out.println(sRosterName + " has " + nCount +"  items");
			
			vwRosterQry  = vwRoster.createQuery(null, null,null,
												VWQueue.QUERY_READ_BOUND + VWQueue.QUERY_READ_UNWRITABLE + VWQueue.QUERY_READ_LOCKED +
												VWQueue.QUERY_MIN_VALUES_INCLUSIVE + VWQueue.QUERY_MAX_VALUES_INCLUSIVE, 
												sFilter, null, VWFetchType.FETCH_TYPE_WORKOBJECT); 
	        
			while (vwRosterQry.hasNext()) 
			{	
				vwWob = (VWWorkObject) vwRosterQry.next();
	        	
				if (vwWob.fetchLockedStatus() == 1)
				{
					try
					{
						vwWob.doLock(true);
		        		vwWob.doAbort();
		        		
		        		i=i+1;
	        		}
					catch(Exception e)
					{
						logger.error("error while unlocking workitem. " +   e.getMessage());
	        		}
	        	}
			}
			
	        System.out.println("");
	        System.out.println(i + " workitems were unlocked.");
	        
	        applogger.info(i + " workitems were unlocked.");
	        
		}
		catch(Exception e)
		{
			logger.error("Error while unlocking workitem! " + e.getMessage());
		}
	}


	public void UnlockQueueWorkItems(String sQueueName, String sFilter)
	{
		VWQueue vwQueue;
		VWQueueQuery vwQueueQry;
		VWStepElement vwStepElmnt;
		
		int i=0;

		try
		{
			vwQueue=oVWSession.getQueue(sQueueName);
			vwQueue.setBufferSize(100);
			
			vwQueueQry  = vwQueue.createQuery(null, null,null,
											  VWQueue.QUERY_READ_BOUND + VWQueue.QUERY_READ_UNWRITABLE + VWQueue.QUERY_READ_LOCKED +
											  VWQueue.QUERY_MIN_VALUES_INCLUSIVE + VWQueue.QUERY_MAX_VALUES_INCLUSIVE, 
											  sFilter, null, VWFetchType.FETCH_TYPE_STEP_ELEMENT);

	        while (vwQueueQry.hasNext()) 
	        {	
	        	vwStepElmnt = (VWStepElement) vwQueueQry.next();

	        	try
	        	{
	        		vwStepElmnt.doLock(true);
	        		vwStepElmnt.doAbort();
	    	        
	        		i=i+1;
	        	}
	        	catch(Exception e)
	        	{
	        		logger.error("error while unlocking workitem. " +   e.getMessage());
	        	}
        	}

	        System.out.println("");
	        System.out.println(i + " workitems were unlocked.");
	        applogger.info(i + " workitems were unlocked.");

		}
		catch(Exception e)
		{
			logger.error("Error while unlocking workitem! " + e.getMessage());
		}
	}

	public void ReleaseRosterWorkItems(String sRosterName, String sFilter)
	{
		VWRoster vwRoster;
		VWRosterQuery vwRosterQry;
		VWWorkObject vwWob;
	
		int nCount=0;
		int i=0;
	
		try
		{
			vwRoster=oVWSession.getRoster(sRosterName);
			vwRoster.setBufferSize(100);
			nCount = vwRoster.fetchCount();
			
			System.out.println(sRosterName + " has " + nCount +"  items");
			
			vwRosterQry  = vwRoster.createQuery(null, null,null,
												VWQueue.QUERY_READ_BOUND + VWQueue.QUERY_READ_UNWRITABLE + VWQueue.QUERY_READ_LOCKED +
												VWQueue.QUERY_MIN_VALUES_INCLUSIVE + VWQueue.QUERY_MAX_VALUES_INCLUSIVE, 
												sFilter, null, VWFetchType.FETCH_TYPE_WORKOBJECT);
			
	        while (vwRosterQry.hasNext()) 
	        {	
	        	vwWob = (VWWorkObject) vwRosterQry.next();
	        	
	        	if (vwWob.fetchLockedStatus() == 1)
	        	{
	        		try
	        		{
	        			vwWob.doLock(true);
	        			vwWob.setFieldValue("WORKSTATUS", "XHOLD", true);
	        			vwWob.setFieldValue("HOLDRELEASEDATE", new java.util.Date(-2000000000000L), true);
		        		vwWob.doSave(true);
		        		
		        		i=i+1;	
	        		}
	        		catch(Exception e)
	        		{
	        			logger.error("error while updating the workitem. " +   e.getMessage());
	        		}
	        	}
	        }
	        
	        System.out.println("");
	        System.out.println(i + " workitems were unlocked.");
	        
	        applogger.info(i + " workitems were unlocked.");
		}
		catch(Exception e)
		{
			logger.error("Error while unlocking workitem! " + e.getMessage());
		}
	}

	public void ReleaseQueueWorkItems(String sQueueName, String sFilter)
	{
		VWQueue vwQueue;
		VWQueueQuery vwQueueQry;
		VWStepElement vwStepElmnt;
		
		int i=0;

		try
		{
			vwQueue=oVWSession.getQueue(sQueueName);
			vwQueue.setBufferSize(100);
			
			vwQueueQry  = vwQueue.createQuery(null, null,null,
											  VWQueue.QUERY_READ_BOUND + VWQueue.QUERY_READ_UNWRITABLE + VWQueue.QUERY_READ_LOCKED +
											  VWQueue.QUERY_MIN_VALUES_INCLUSIVE + VWQueue.QUERY_MAX_VALUES_INCLUSIVE, 
											  sFilter, null, VWFetchType.FETCH_TYPE_STEP_ELEMENT);

	        while (vwQueueQry.hasNext()) 
	        {	
	        	vwStepElmnt = (VWStepElement) vwQueueQry.next();

	        	try
	        	{
	        		vwStepElmnt.doLock(true);
	        		vwStepElmnt.setParameterValue("WORKSTATUS", "XHOLD", true);
	        		vwStepElmnt.setParameterValue("HOLDRELEASEDATE", new java.util.Date(-2000000000000L), true);
	        		vwStepElmnt.doSave(true);
	    	        
	        		i=i+1;
	        	}
	        	catch(Exception e)
	        	{
	        		logger.error("error while updating the workitem. " +   e.getMessage());
	        	}
        	}

	        System.out.println("");
	        System.out.println(i + " workitems were unlocked.");
	        
	        applogger.info(i + " workitems were unlocked.");

		}
		catch(Exception e)
		{
			logger.error("Error while unlocking workitem! " + e.getMessage());
		}
	}
}