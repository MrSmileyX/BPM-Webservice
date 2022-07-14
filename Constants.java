import java.util.ArrayList;
import java.util.Date;

public class Constants
{
	public static String claimSystem;
	
	public static String outFilePfx;
	public static String inFilePfx;
	
	public static String bridgePass;
	public static String bridgeServer;
	public static String bridgeUser;
	public static String bridgePath;
	public static String bridgeLastLine;
	
	public static long bridgeLines;
	public static int bridgePort;
	
	public static String dbaseRegion;
	public static String dbaseUrl;
	public static String dbaseClass;
	public static String dbaseThin;
	public static String dbaseOCI;
	public static String dbaseUser;
	public static String dbasePass;
	
	public static boolean stopProc; 
	public static boolean restart;
	public static boolean hdrProcessed = false;
	
	public static String isServer;
	public static String isUser;
	public static String isPass;
		
	public static String appPath;
	public static String logPath;
	public static String logMainPfx;
	public static String logStatusPfx;
	public static String fileToProc;
	public static String restFile;
	public static long restRecord;
	public static int logServerLen;
	
	//Miscellaneous Application Constants
	public static String appName; 
	public static String appEnv;
	public static boolean showOutput;
	public static boolean batchHasRun;
		
	public static String procEnv; 
	public static String dcnPrefix;
	public static String docExt; 
	public static String dataExt;
	public static String transExt;
	public static String eobExt;
	public static String imageExt;
	
	//Time Constants
	public static long milliSecs;
	public static long fileMilSeconds;
	public static long winMilSeconds;
	public static long breakMilSeconds;
	public static long retryMilSeconds;
	public static int waitInterval; 
	public static long fileMinutes;
	public static long windowMinutes;
	public static long breakMinutes;
	public static long retryMinutes;
	public static long waitIntMinutes;
	public static String fileDescriptor;
	public static String windowDescriptor;
	public static String breakDescriptor;
	public static String retryDescriptor;
	public static String waitIntDescriptor;
			
	public static String stopFile;
	public static boolean peError = false; 
	
	public static String tdSysID;
	public static String tdRouteCode;
	public static String tdOpenIndex;
	public static String tdDocType;
	public static String tdSrcID;
	public static String tdDocFormat;
	
	public static String mailServer;
	public static String mailSubject;
	public static String mailFrom;
	public static String mailFromMask;
	public static String mailToMask;
	public static String mailFiles;
	public static int mailPort;
	
	public static String rowColorA;
	public static String rowColorB;
	
	public static String WcmApiConfig;
	public static String FileNetConfig;
	public static String logConfig;
	public static String PropFile;
	public static String osType;
			
	public static Date stopTime = null;
	public static Date startTime = null;
	public static int bindPort;
	
	public static ArrayList<String> ceDocClass = new ArrayList<String>(); 
	public static ArrayList<String> ceObjStores = new ArrayList<String>(); 
	
	public static String bpmDomain;
	public static String bpmUser;
	public static String bpmPass;
	public static String bpmServer;
	public static String bpmPort;
	public static String bpmShortRoster;
	public static String bpmLongRoster;
	public static String bpmRouter;

	public static String restApiKeyFld;
	public static String restApiKeyVal;
	public static String restAuthKeyFld;
	public static String restAuthKeyVal;
	public static String restAcceptTypeFld;
	public static String restAcceptTypeVal;
	public static String restContentTypeFld;
	public static String restContentTypeVal;
	public static String restSenderAppFld;
	public static String restSenderAppVal;
	public static String restMethod;
	public static String restURL;
	public static String restTransIDFld;
	public static String restTransIDVal;
	public static String restCaseStage;
	public static String restPassMsg;
	public static String restFailMsg;
	public static String restRespError;
	public static String restRespPass;
	public static boolean restDoOutput;
	
	public static final String REST_DO_GET = "GET";
	public static final String REST_DO_POST = "POST";
		
	public static String bpmStatusFld;
	public static String bpmTransIDFld;
	public static String bpmTmStmpFld;
	public static String bpmStageFld;
	public static String bpmErrorFld;
	public static String bpmWorkStatFld;
	public static String bpmUserNmFld;
	public static String bpmStatDtFld;
	public static String bpmFromQFld;
	public static String bpmWorkStatVal;
	public static String bpmUserNmVal;
	public static String bpmFromQVal;
		
	public static String ceMode;
	public static String ceObjectStore;
	public static String ceQueueName;
	public static String ceProcStatus;
	public static String ceProcMessage;
	public static String ceIdxKey;
	public static String ceSrchFldValLst;
	public static String ceExtPEPropList;
	public static String cePropertyList;
	
	public static String ceFileRecDelimiter;
	public static String ceFileExtension;
	public static String ceFileNamePrefix;
	public static String ceFileNamePrefixType;
	public static String ceFileNameUnique;
	public static String ceFileNameUniqueSeperator;
	
	public static String ceExtractMetadata;
	public static String ceExtractContent;
	public static String ceSearchKeyList;
	
	public static String ceFtpHost;
	public static String ceFtpUserid;
	public static String ceFtpPassword;
	public static String ceFtpRootfolder;
	public static String ceFtpFolder;
	
	public static String ceTempFolder;
	public static String ceCompleteErrorWorkitem;
	
	public static String ServiceNameField = "ServiceNameField";
	public static String loggerClass = "com.wellpoint.error";
	public static String apploggerClass = "com.wellpoint.log";
	public static String InitialConextFactory = "com.ibm.websphere.naming.WsnInitialContextFactory";

}
