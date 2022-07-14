import java.util.*;
import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EMail
{
	public static int allDups, allSkip, allRead, allComm, allKept;
	public static ArrayList<String> rptList = new ArrayList<String>();


	public static void sendError(String Message)
	{
		String propTemp;
		String HTMLhdr, HTMLend, HTMLbody, HTMLMessage;
 
		int noErr = 0;
		int lvl = 0;
		boolean bFiles = false;

		ArrayList<String> sendTo = new ArrayList<String>();
		ArrayList<String> attFiles = new ArrayList<String>();

		//Retrieve E-Mail properties
		String mailServer = AppProperties.Read("mail.Server", false);
		String mailSubject = AppProperties.Read("mail.Subject", false);
		String mailFrom = AppProperties.Read("mail.From", false);
		String mailFromMask = AppProperties.Read("mail.FromMask", false);
		String mailToMask = AppProperties.Read("mail.ToMask", false);
		String mailPort = AppProperties.Read("mail.Port", false);
		String mailFiles = AppProperties.Read("mail.Files", false);

		int SmtpPort = Integer.parseInt(mailPort);

		while (noErr == 0)
		{
			propTemp = AppProperties.Read("mail.To." + lvl, false);

			if (propTemp == null || propTemp.equals(""))
			{
				noErr = -1;
			}
			else
			{
				sendTo.add(propTemp);
				lvl++;
			}
		}

		if (sendTo.size() == 0)
		{
			sendTo.add(mailFrom);
		}

		HTMLhdr = "<HTML>\r\n<BODY bgcolor=\"#000000\">\r\n<FONT color=\"#FF0000\"face=\"Courier\"><B>\r\n";
		HTMLend = "\r\n</B></FONT>\r\n</BODY>\r\n</HTML>";

		HTMLMessage = HTMLhdr + Message + HTMLend;
		WriteLog.WriteToLog("Sending error e-mail message to support.");

		sendAttMail(mailServer, SmtpPort, mailSubject, mailFrom, mailFromMask,
					sendTo, mailToMask, HTMLMessage, bFiles, attFiles);

		WriteLog.WriteToLog("Error e-mail message sent.");
	}


	public static void sendDoneMail(summaryProc wasProc)
	{
		String propTemp;
		String HTMLhdr, HTMLend, HTMLbody, HTMLMessage;

		int noErr = 0;
		int lvl = 0;
		boolean bFiles = false;

		ArrayList<String> sendTo = new ArrayList<String>();
		ArrayList<String> attFiles = new ArrayList<String>();

		//Retrieve E-Mail properties
		String mailServer = AppProperties.Read("mail.Server", false);
		String mailSubject = AppProperties.Read("mail.Subject", false);
		String mailFrom = AppProperties.Read("mail.From", false);
		String mailFromMask = AppProperties.Read("mail.FromMask", false);
		String mailToMask = AppProperties.Read("mail.ToMask", false);
		String mailPort = AppProperties.Read("mail.Port", false);
		String mailFiles = AppProperties.Read("mail.Files", false);

		int SmtpPort = Integer.parseInt(mailPort);

		while (noErr == 0)
		{
			propTemp = AppProperties.Read("mail.To." + lvl, false);

			if (propTemp == null || propTemp.equals(""))
			{
				noErr = -1;
			}
			else
			{
				sendTo.add(propTemp);
				lvl++;
			}
		}

		HTMLMessage = createSummaryMsg(wasProc);
		
		WriteLog.WriteToLog("Sending detailed e-mail message to customers.");

		sendAttMail(mailServer, SmtpPort, mailSubject, mailFrom, mailFromMask,
					sendTo, mailToMask, HTMLMessage, bFiles, attFiles);

		WriteLog.WriteToLog("Detailed e-mail message sent.");
	}
	
	private static String createSummaryMsg(summaryProc procData)
	{
		String messageFull = "";
		String messageHdr = "";
		String headerEnd = "";
		String messageTitle = "";
		String messageSubTitle = "";
		String messageColTitle = "";
		String messageTblDef = "";
		String messageBody = "";
		String messageRows = "";
		String messageTail = "";
		String messageTblEnd = "";
		String messageDivider = "";
		
		String timeStamp = "";
		String currentColor = "";

		int recCurr = 0;
		int itemCount = 0;

		DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy - h:mm a");

	    Date currDate = new Date();
	    timeStamp = dateFormat.format(currDate);

	    messageHdr  = "<HTML>\n";
	    messageHdr += "<HEAD>\n";
	    messageHdr += "<META http-equiv=\"Content-Type\" CONTENT=\"text/html; CHARSET=us-ascii\">\n";
	              
	    messageTitle = constScript();
	              
	    headerEnd = "</HEAD>\n\n";
	                
	    messageBody  = "<BODY bgcolor=\"#D6D6C6\" lang=EN-US link=blue vlink=purple>\n";
	    messageBody += "<font face=\"Courier\" size=\"3\" color=\"#000000\">\n";

	    messageSubTitle  = "<p class=MsoNormal align=center style='text-align:center'><o:p><span style='font-size:12.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>BPM Report Extract Summary Report" + "</o:p></p>\n\n";
	    messageSubTitle += "<p class=MsoNormal align=center style='text-align:center'><b><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" +  timeStamp + "<br><br>" + "</span></b>\n";
	    messageSubTitle += "</p>\n";
	    
	    messageDivider = "<div class=MsoNormal align=center style='text-align:center'><hr size=2 width=\"100%\" align=center></div>\n";
	 
	    //Here is where the magic for the table happens
	    messageTblDef = "<table class=MsoNormalTable border=\"0\" cellpadding=\"0\" width=\"700\" style='width:700'>\n";
	    
	    //For the header row
	    messageColTitle  = "<tr>\n";
	    messageColTitle += "<th colspan=\"2\" style='background:#B6B68C;padding:.75pt .75pt .75pt .75pt'>\n";
	    messageColTitle += "<span style='font-size:8.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>Current Report Processing Information:</span><o:p></o:p></b></p>\n";
	    messageColTitle += "</th>\n";
	    messageColTitle += "</tr>";

		currentColor = Constants.rowColorA;
		    	
		messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "BPM Environment:" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + procData.reportSystem + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";

        currentColor = Constants.rowColorB;
		
		messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "Report Title:" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + procData.reportEnv + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";

        currentColor = Constants.rowColorA;
    	
		messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "Records Found:" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + procData.processedRecs + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";

        currentColor = Constants.rowColorB;
		
		messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "Records Written:" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + procData.writtenRecs + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";

		currentColor = Constants.rowColorA;
    	
        messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";

        currentColor = Constants.rowColorB;
    	
        messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";
        
        currentColor = Constants.rowColorA;

        messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "Process Start Time:" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + procData.startTime + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";

        currentColor = Constants.rowColorB;
    	
        messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "Process Stop Time:" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + procData.stopTime + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";

        currentColor = Constants.rowColorB;

        messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";
        
        currentColor = Constants.rowColorA;
    	
        messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "Total Processing Time:" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + procData.elapsedTime + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";
        
        currentColor = Constants.rowColorB;
        
        messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";
        
        currentColor = Constants.rowColorB;

        messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "Output File:" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + procData.outputFile + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";
        
        currentColor = Constants.rowColorA;
    	
        messageRows += "<tr>\n";
        messageRows += " <td width=\"300\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + "Output Path:" + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += " <td width=\"400\" style='background:" + currentColor + ";padding:.75pt .75pt .75pt .75pt'>\n";
        messageRows += "  <p class=MsoNormal><span style='font-size:10.0pt;font-family:\"Trebuchet MS\",\"sans-serif\"'>" + procData.outputPath + "</span><o:p></o:p></p>\n";
        messageRows += " </td>\n";
        messageRows += "</tr>\n";
        

	    messageTblEnd = "</table>\n";

	    messageTail  = "</font>\n";
	    messageTail += "</body>\n";
	    messageTail += "</html>\n";

	    messageFull = messageHdr + messageTitle + headerEnd + messageBody + messageSubTitle + 
	    			  messageDivider + messageTblDef + messageColTitle + messageRows + messageTblEnd + 
	    			  messageDivider + messageTail;
		
	    return messageFull;
	}

	private static String constScript()
	{
		String timeStamp = "";
		String sFinal = "";

		DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy - h:mm a");

	    Date currDate = new Date();
	    timeStamp = dateFormat.format(currDate);

	    sFinal = "<!--[if !mso]>" + "\n" + "\n";
	    sFinal += "  <style>" + "\n";
	    sFinal += "   v\\:* {behavior:url(#default#VML);}" + "\n";
	    sFinal += "   o\\:* {behavior:url(#default#VML);}" + "\n";
	    sFinal += "   w\\:* {behavior:url(#default#VML);}" + "\n";
	    sFinal += "   .shape {behavior:url(#default#VML);}" + "\n";
	    sFinal += "  </style>" + "\n";
	    sFinal += "<![endif]-->" + "\n";
	    sFinal += "<title>eCerts - Processed booklet Report - " + timeStamp + "</title>";
	    sFinal += "<style>" + "\n";
	    sFinal += "<!--" + "\n";
	    sFinal += "/* Font Definitions */" + "\n";
	    sFinal += " @font-face" + "\n";
	    sFinal += "  {font-family:Calibri;" + "\n";
	    sFinal += "    panose-1:2 15 5 2 2 2 4 3 2 4;}" + "\n";
	    sFinal += " @font-face" + "\n";
	    sFinal += "  {font-family:Tahoma;" + "\n";
	    sFinal += "    panose-1:2 11 6 4 3 5 4 4 2 4;}" + "\n";
	    sFinal += " @font-face" + "\n";
	    sFinal += "  {font-family:\"Trebuchet MS\";" + "\n";
	    sFinal += "    panose-1:2 11 6 3 2 2 2 2 2 4;}" + "\n" + "\n";
	    sFinal += "/* Style Definitions */" + "\n";
	    sFinal += " p.MsoNormal , li.MsoNormal, div.MsoNormal" + "\n";
	    sFinal += "  {margin:0in;" + "\n";
	    sFinal += "   margin-bottom:.0001pt;" + "\n";
	    sFinal += "   font-size:12.0pt;" + "\n";
	    sFinal += "   font-family:\"Times New Roman\",\"serif\";}" + "\n";
	    sFinal += "   a: link , Span.MsoHyperlink" + "\n";
	    sFinal += "      {mso-style-priority:99;" + "\n";
	    sFinal += "       color:blue;" + "\n";
	    sFinal += "       text-decoration:underline;}" + "\n";
	    sFinal += "   a: visited , Span.MsoHyperlinkFollowed" + "\n";
	    sFinal += "      {mso-style-priority:99;" + "\n";
	    sFinal += "       color:purple;" + "\n";
	    sFinal += "       text-decoration:underline;}" + "\n";
	    sFinal += "      Span.EmailStyle17" + "\n";
	    sFinal += "      {mso-style-type:personal;" + "\n";
	    sFinal += "   font-family:\"Tahoma\",\"sans-serif\";" + "\n";
	    sFinal += "   color:windowtext;}" + "\n";
	    sFinal += "   Span.EmailStyle18" + "\n";
	    sFinal += "      {mso-style-type:personal-reply;" + "\n";
	    sFinal += "   font-family:\"Calibri\",\"sans-serif\";" + "\n";
	    sFinal += "   color:#1F497D;}" + "\n";
	    sFinal += "    .MsoChpDefault" + "\n";
	    sFinal += "     {mso-style-type:export-only;" + "\n";
	    sFinal += "      font-size:10.0pt;}" + "\n";
	    sFinal += "      @page Section1" + "\n";
	    sFinal += "     {size:8.5in 11.0in;" + "\n";
	    sFinal += "      margin:1.0in 1.0in 1.0in 1.0in;}" + "\n";
	    sFinal += "      div.Section1" + "\n";
	    sFinal += "     {page:Section1;}" + "\n";
	    sFinal += "  -->" + "\n";
	    sFinal += " </style>" + "\n" + "\n";
	    sFinal += "<!--[if gte mso 9]>" + "\n";
	    sFinal += " <xml>" + "\n";
	    sFinal += "  <o:shapedefaults v:ext=\"edit\" spidmax=\"1026\"/>" + "\n";
	    sFinal += " </xml>" + "\n";
	    sFinal += "<![endif]-->" + "\n" + "\n";
	    sFinal += "<!--[if gte mso 9]>" + "\n";
	    sFinal += " <xml>" + "\n";
	    sFinal += "   <o:shapelayout v:ext=\"edit\">" + "\n";
	    sFinal += "   <o:idmap v:ext=\"edit\" data=\"1\"/>" + "\n";
	    sFinal += "   </o:shapelayout>" + "\n";
	    sFinal += " </xml>" + "\n";
	    sFinal += "<![endif]-->" + "\n";

	    return sFinal;
	}

	public static void sendAttMail(String mailServer, int mailPort, String subject, String mailFrom, String maskFrom,
						    ArrayList<String> recipient, String maskTo, String message, boolean mailFiles, ArrayList<String> fileNames)
	{

		String currRcpt = "";
		String currFile = "";

		try
		{
			Socket s = new Socket(mailServer, mailPort);

			BufferedReader in = new BufferedReader
			(new InputStreamReader(s.getInputStream(), "8859_1"));

			BufferedWriter out = new BufferedWriter
			(new OutputStreamWriter(s.getOutputStream(), "8859_1"));

			String boundary = "bound1";

			Thread.sleep(500);
			sendln(in, out, "HELO " + mailServer, true);
			sendln(in, out, "MAIL FROM: <" + mailFrom + ">", false);

			for (int i = 0; i < recipient.size(); i++)
			{
				currRcpt = recipient.get(i);
				sendln(in, out, "RCPT TO: <" + currRcpt + ">", false);
			}

			Thread.sleep(500);
			sendln(in, out, "DATA", false);

			sendln(out, "From: " + maskFrom, false);
			sendln(out, "To: " + maskTo, false);
			sendln(out, "Subject: " + subject, false);
			sendln(out, "MIME-Version: 1.0", false);
			sendln(out,"Content-Type: multipart/mixed; boundary=\"" + boundary +"\"", false);
			sendln(out, "\r\n" + "--" +  boundary, false);

			// Send the body
			sendln(out, "Content-Type: text/html; charset=\"us-ascii\"", false);
			sendln(out, "\r\n\r\n", false);
			sendln(out, message + "\r\n", false);

			if (mailFiles)
			{
				for (int i = 0; i < fileNames.size(); i++)
				{
					currFile = fileNames.get(i);

					sendln(out, "\r\n" + "--" +  boundary, false);

					sendln(out, "Content-Type: " + MIMEBase64.MIMETypeLookup(currFile) + "; name=" + currFile, false);
					sendln(out, "Content-Disposition: attachment;filename=\"" + currFile + "\"", false);
					sendln(out, "Content-Transfer-Encoding: base64\r\n", true);

					MIMEBase64.encode(currFile, out);
				}
			}

			// done
			sendln(out, "\r\n\r\n--" + boundary + "--", false);
			sendln(in, out,".", true);
			sendln(in, out, "QUIT", true);

			s.close();
		}
	   	catch (Exception e)
	   	{
	     	e.printStackTrace();
	    }
	}

	public static void sendSummaryMail()
	{
		ArrayList<String> sendTo = new ArrayList<String>();
		ArrayList<String> attFiles = new ArrayList<String>();

		String propTemp;
		String HTMLhdr, HTMLend, HTMLbody, HTMLMessage;

		int noErr = 0;
		int lvl = 0;
		boolean bFiles = false;

		//Retrieve E-Mail properties
		String mailServer = AppProperties.Read("mail.Server", false);
		String mailSubject = AppProperties.Read("mail.Subject", false);
		String mailFrom = AppProperties.Read("mail.From", false);
		String mailFromMask = AppProperties.Read("mail.FromMask", false);
		String mailToMask = AppProperties.Read("mail.ToMask", false);
		String mailPort = AppProperties.Read("mail.Port", false);
		String mailFiles = AppProperties.Read("mail.Files", false);

		int SmtpPort = Integer.parseInt(mailPort);

		while (noErr == 0)
		{
			propTemp = AppProperties.Read("mail.done." + lvl, false);

			if (propTemp == null || propTemp.equals(""))
			{
				noErr = -1;
			}
			else
			{
				sendTo.add(propTemp);
				lvl++;
			}
		}

		String sumMessage = "Don't forget to add code...";

		WriteLog.WriteToLog("Sending summary e-mail message to customers.");

		sendAttMail(mailServer, SmtpPort, mailSubject, mailFrom, mailFromMask,
					sendTo, mailToMask, sumMessage, bFiles, attFiles);

		WriteLog.WriteToLog("Summary e-mail message sent.");
	}

	public static void sendln(BufferedReader in, BufferedWriter out, String s, boolean logIt)
	{
		try
		{
			logIt = true;
			out.write(s + "\r\n");
			out.flush();
			s = in.readLine();

			if (logIt)
			{
				WriteLog.WriteToLog(s);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void sendln(BufferedWriter out, String s, boolean logIt)
	{
		try
		{
			if (logIt)
			{
				WriteLog.WriteToLog(s);
			}

			out.write(s + "\r\n");
			out.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}