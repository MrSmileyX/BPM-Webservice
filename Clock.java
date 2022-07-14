import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Clock
{
	Calendar calTime;

	public static boolean readyToRun()
	{

		String sStart, sStop, sCurr;

		sStart = AppProperties.Read("time.Start", false);
		sStop = AppProperties.Read("time.Stop", false);

		Calendar calStart = setCalVal(sStart);
		Calendar calStop = setCalVal(sStop);

		return isInWindow(calStart, calStop);
	}

	public static Calendar setCalVal(String timeRep)
	{
		String sHour, sMinute, sSec, sAMPM;
		int colPos, colPrd, colSpc;
		int theHour, theMinute, theSecs, theAMPM;

		colPos = timeRep.indexOf(":");
		colPrd = timeRep.indexOf(".");
		colSpc = timeRep.indexOf(" ");

		sHour = timeRep.substring(0, colPos);
		sMinute = timeRep.substring(colPos + 1, colPrd);
		sSec = timeRep.substring(colPrd + 1, colSpc);
		sAMPM = timeRep.substring(colSpc + 1, timeRep.length());

		theHour = Integer.parseInt(sHour);
		theMinute = Integer.parseInt(sMinute);
		theSecs = Integer.parseInt(sSec);

		Calendar calCurr = Calendar.getInstance();

		calCurr.set(calCurr.HOUR, theHour);
		calCurr.set(calCurr.MINUTE, theMinute);
		calCurr.set(calCurr.SECOND, theSecs);

		if (sAMPM.equals("AM"))
		{
			calCurr.set(calCurr.AM_PM, calCurr.AM);
		}
		else
		{
			calCurr.set(calCurr.AM_PM, calCurr.PM);
		}

		return calCurr;
	}

	private static Boolean isInWindow(Calendar cStart, Calendar cStop)
	{
		Calendar cCurr = Calendar.getInstance();

		if ((cStart.get(cStart.AM_PM) == cStart.PM) && (cStop.get(cStop.AM_PM) == cStop.AM) && (cCurr.get(cCurr.AM_PM) == cCurr.PM))
		{
			cStop.add(cStop.DAY_OF_MONTH, +1);
		}
		else if ((cStart.get(cStart.AM_PM) == cStart.PM) && (cStop.get(cStop.AM_PM) == cStop.AM) && (cCurr.get(cCurr.AM_PM) == cCurr.AM))
		{
			cStart.add(cStart.DAY_OF_MONTH, -1);
		}

		if (cCurr.after(cStart) && cCurr.before(cStop))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private static Calendar getYesterday()
	{
		Calendar cBefore = Calendar.getInstance();
		cBefore.add(Calendar.DAY_OF_MONTH, -1);
		return cBefore;
	}

	private static Calendar getToday()
	{
		Calendar cNow = Calendar.getInstance();
		cNow.add(Calendar.DAY_OF_MONTH, +0);
		return cNow;
	}

	private static Calendar getTomorrow()
	{
		Calendar cTom = Calendar.getInstance();
		cTom.add(Calendar.DAY_OF_MONTH, +1);
		return cTom;
	}

	private static String getCurrTS()
	{
		DateFormat df = new SimpleDateFormat("hh:mm:ss a MM/dd/yyyy");
		Date dtNow = new Date();

		String sTimeStamp = df.format(dtNow);

		return sTimeStamp;
	}

	public static boolean performSleep(File isStop, long pauseMSecs, long fullMSecs)
	{
		long lTotMilSecs = 0;
		long lMins = 0;
		boolean bFileFnd = false;

		lMins = fullMSecs / 60000;

		WriteLog.WriteToLog("Process is returning to sleep for " + lMins + " minutes.");

		while (lTotMilSecs < fullMSecs)
		{
			try
			{
				Thread.sleep(pauseMSecs);

				WriteLog.WriteToLog("Checking for Stop file...");
				bFileFnd = isStop.exists();

				lTotMilSecs = lTotMilSecs + pauseMSecs;

				if (bFileFnd)
				{
					WriteLog.WriteToLog("Stop file was found. Preparing to terminate.");
					break;
				}
				else
				{
					WriteLog.WriteToLog("Stop file was not found. Continuing processing.");
				}
			}
			catch (InterruptedException ie)
			{
				lTotMilSecs = lTotMilSecs + pauseMSecs;
			}
		}

		return bFileFnd;
	}
}