import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class StringUtils 
{
	public static String formatString(String input)
	{
		String newDate = "";
		String holdDate = "";
		String dtFormat = "";
		String fullFmt = "";
		String mainStr = "";
		String partA = "";
		String fullRetStr = "";
		String formatType = ""; 
		String days = "";
		
		int calc = 0;
		int pipeA = 0;
		int pipeB = 0;
		int pipeC = 0;
		int pipeD = 0;
		int minusChar = 0;
		int plusChar = 0;
		int bracketA = 0;
		int bracketB = 0;
		
		Date convDate = null;
		
		if (!input.isEmpty())
		{
			pipeA = input.indexOf("|", 0);
			
			if (pipeA > -1)
			{
				pipeB = input.indexOf("|", pipeA + 1);
				
				if(pipeB > pipeA)
				{
					if(pipeA > 0)
					{
						partA = input.substring(0, pipeA); 
					}
					
					fullFmt = input.substring(pipeA, pipeB + 1);
					
					if (pipeB < input.length())
					{
						mainStr = input.substring(pipeB + 1, input.length());
					}
				}
				else
				{
					return input;
				}
			}
			else
			{
				return input;
			}
			
			
			pipeC = fullFmt.indexOf("|", 0);
			
			if (pipeC > -1)
			{
				pipeD = fullFmt.indexOf("|", pipeC + 1);
			}
			
			bracketA = fullFmt.indexOf("{");
			
			if (bracketA > -1)
			{
				bracketB = fullFmt.indexOf("}", bracketA + 1);
				
				if(bracketB > bracketA)
				{
					dtFormat = fullFmt.substring(bracketA + 1, bracketB);
				}
				else
				{
					return input;
				}
			}
			else
			{
				return input;
			}
			
			String fmtType = fullFmt.substring(pipeC + 1, bracketA);
			
			if (fmtType.toUpperCase().equals("DATE"))
			{
				DateFormat df = new SimpleDateFormat(dtFormat);
				Date dtNow;
				
				dtNow = new Date();
				
				minusChar = fullFmt.indexOf("-", bracketB); 
				plusChar = fullFmt.indexOf("+", bracketB);
				
				if (minusChar > 0)
				{
					days = "-" + fullFmt.substring(minusChar + 1, pipeD); 
					calc = Integer.parseInt(days);
					convDate = getCalculatedDate(dtNow, calc);
					newDate = df.format(convDate);
				}
				else if (plusChar > 0) 
				{
					days = "+" + fullFmt.substring(plusChar + 1, pipeD); 
					calc = Integer.parseInt(days);
					convDate = getCalculatedDate(dtNow, calc);
					newDate = df.format(convDate);
				}
				else
				{
					newDate = df.format(dtNow);
				}
			}
			
			
			if (fmtType.toUpperCase().equals("LASTDATE"))
			{
				int currRpt = 0;
				
				String lastProp = "ts.last." + currRpt;
				String lastDate = AppProperties.Read(lastProp, false);
				
				newDate = lastDate;
			}
			
			fullRetStr = partA + newDate + mainStr; 
		}
		else
		{
			fullRetStr = "";
		}
		return fullRetStr;
	}
	
	public static Date getCalculatedDate(Date myDate, int days)
	{
		Date result;
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(myDate);
		gc.add(Calendar.DAY_OF_YEAR, days);
		result = gc.getTime();
		
		return result;
	}
	
	public static String getCurrentDate()
	{
		String dtFormat = "MM/dd/yyyy";
		
		DateFormat lastDF = new SimpleDateFormat(dtFormat);
		Date dtNow;
		
		dtNow = new Date();
		String lastDate = lastDF.format(dtNow);
		
		return lastDate;
	}
	
}
