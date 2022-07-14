

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WriteLog
{
	public static void WriteToLog(String Message)
	{
		String sTS;
		String sLogNm;
		String sFilePath;
		String sLine;
		String fileDt;
		String showOutput = "N";
		String server = "";
		boolean bNew = false;

		server = getServerName(Constants.logServerLen);
		showOutput = "Y";
		
		DateFormat df = new SimpleDateFormat("MMddyy");
		Date dtNow = new Date();
		fileDt = df.format(dtNow);

		sLogNm = Constants.logMainPfx + "_" + server + "_" + fileDt + ".log";
		
		sFilePath = Constants.logPath + sLogNm;

		File fLog = new File(sFilePath);

		try
		{
			bNew = fLog.createNewFile();
		}
		catch (IOException e)
		{
			System.out.println("I/O Error");
			System.exit(0);
		}

		PrintWriter out = openWriter(sFilePath);

		sTS = makeTimeStmp();

		if (bNew)
		{
			writeLine(sTS + "  Log started.", out);
			bNew = false;
		}

		String preCursor = sTS + "  ";
		String extraLine = "\r\n" + preCursor;
		
		String editedMsg = Message.replaceAll("\r\n", extraLine);
		sLine = preCursor + editedMsg;
		
		writeLine(sLine, out);

		out.close();

		if (showOutput.equals("Y") || showOutput.equals("y"))
		{
			System.out.println(sLine);
		}
	}

	public static void WriteStatus(String Message)
	{
		String sTS;
		String sLogNm;
		String sFilePath;
		String sLine;
		String showOutput = "N";
		String server = "";
		
		boolean bNew = false;

		sLogNm = getLogName();

		server = getServerName(Constants.logServerLen);
		showOutput = "N";
		sFilePath = Constants.logPath + sLogNm;

		File fLog = new File(sFilePath);

		try
		{
			bNew = fLog.createNewFile();
		}
		catch (IOException e)
		{
			System.out.println("I/O Error");
			System.exit(0);
		}

		PrintWriter out = openWriter(sFilePath);

		if (bNew)
		{
			writeLine("Log started.", out);
			bNew = false;
		}

		sLine = Message;
		
		writeLine(sLine, out);

		out.close();

		if (showOutput.equals("Y") || showOutput.equals("y"))
		{
			System.out.println(sLine);
		}
	}

	private static String makeTimeStmp()
	{
		String sTimeStamp = "";

		DateFormat df = new SimpleDateFormat("[MM/dd/yyyy HH:mm:ss]");
		Date dtNow = new Date();
		sTimeStamp = df.format(dtNow);

		
		return sTimeStamp;
	}
	
	private static String getLogName()
	{
		String fileDt = "";
		String fileNm = "";
		String server = "";
		
		DateFormat df = new SimpleDateFormat("MMddyy");
		Date dtNow = new Date();
		fileDt = df.format(dtNow);

		fileNm = Constants.logStatusPfx +  "_" + server + "_" + fileDt + ".log";

		return fileNm;
	}

	private static PrintWriter openWriter(String name)
	{
		try
		{
			File file = new File(name);

			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw, true);

			return out;
		}
		catch (IOException e)
		{
			System.out.println("I/O Error");
			System.exit(0);
		}

		return null;
	}

	private static void writeLine(String FullMsg, PrintWriter fileOut)
	{
		fileOut.println(FullMsg);
	}
	
	public static String getServerName(int nmLen)
	{
		String srvrName = "";
		String srvrNbr = "";
		
		InetAddress ip;
		
		try
		{
			ip = InetAddress.getLocalHost();
			srvrName = ip.getHostName();
		}
		catch (UnknownHostException e) 
		{	 
            e.printStackTrace();
        }

		if (nmLen > 0)
		{
			if (srvrName.length() > nmLen)
			{
				srvrNbr = srvrName.substring(srvrName.length() - nmLen);
			}
			else
			{
				srvrNbr = srvrName;
			}
		}	
		else
		{
			srvrNbr = srvrName;
		}
		
		return srvrNbr;
	}
}