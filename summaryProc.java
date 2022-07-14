import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class summaryProc
{
	String reportSystem;
	String reportEnv;
	
	int processedRecs;
	int writtenRecs;
	
	Date procStart;
	Date procStop;
	
	String startTime = "";
	String stopTime = "";

	String outputFile = "";
	String outputPath = ""; 
	
	ArrayList<String> documents = new ArrayList<String>();
	
	long procTime = 0;
	
	String elapsedTime = "";
	
	public summaryProc()
	{
		//this.reportSystem = inReport.getName();
		//this.reportEnv = inReport.getTitle(); 
				
		this.processedRecs = 0;
		this.writtenRecs = 0;	
	}
	
	public void addDocFile(String docFile)
	{
		this.documents.add(docFile);
	}
	
	public void addProcessed()
	{
		this.processedRecs++;
	}
	
	public void addWritten()
	{
		this.writtenRecs++;
	}
	
	public void addProcessed(int recsProc)
	{
		this.processedRecs += recsProc;
	}
	
	public void addWritten(int recsWritten)
	{
		this.writtenRecs += recsWritten;
	}
		
	public void fmtProcTime(long msStart, long msEnd)
	{
		long totTime = msEnd - msStart;
		String outTime = TimeUtilities.millisToLongDHMS(totTime);
		this.elapsedTime = outTime;
	}
}