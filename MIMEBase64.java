import java.io.*;

public class MIMEBase64
{
	static String BaseTable[] =
	{
	"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
	"Q","R","S","T","U","V","W","X","Y","Z","a","b","c","d","e","f",
	"g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v",
	"w","x","y","z","0","1","2","3","4","5","6","7","8","9","+","/"
	};
 
	public static void encode(String filename, BufferedWriter out)
	{
		try
		{
			File f              = new File(filename);
			FileInputStream fin = new FileInputStream(filename);

			// read the entire file into the byte array

			byte bytes[]        = new byte[(int)(f.length())];
			int n               = fin.read(bytes);

			if (n < 1) return;          							// no bytes to encode!?!

			byte buf[] = new byte[4];   							// array of base64 characters

			int n3byt      = n / 3;     							// how 3 bytes groups?
			int nrest      = n % 3;     							// the remaining bytes from the grouping
			int k          = n3byt * 3; 							// we are doing 3 bytes at a time
			int linelength = 0;         							// current linelength
			int i          = 0;         							// index

			// do the 3-bytes groups ...
			while ( i < k )
			{
				buf[0] = (byte)(( bytes[i]   & 0xFC) >> 2);

				buf[1] = (byte)(((bytes[i]   & 0x03) << 4) |
						  ((bytes[i+1] & 0xF0) >> 4));

				buf[2] = (byte)(((bytes[i+1] & 0x0F) << 2) |
						  ((bytes[i+2] & 0xC0) >> 6));

				buf[3] = (byte)(  bytes[i+2] & 0x3F);

				send(out, BaseTable[buf[0]]);
				send(out, BaseTable[buf[1]]);
				send(out, BaseTable[buf[2]]);
				send(out, BaseTable[buf[3]]);

				if ((linelength += 4) >= 76)
				{
					send(out, "\r\n");
					linelength = 0;
				}

				i += 3;

			}
			// deals with the padding ...

			if (nrest==2)
			{
				buf[0] = (byte)(( bytes[k] & 0xFC)   >> 2);
				buf[1] = (byte)(((bytes[k] & 0x03)   << 4) |
						 ((bytes[k+1] & 0xF0) >> 4));
				buf[2] = (byte)(( bytes[k+1] & 0x0F) << 2);
			}
			else if (nrest==1)
			{
				// 1 byte left
				buf[0] = (byte)((bytes[k] & 0xFC) >> 2);
				buf[1] = (byte)((bytes[k] & 0x03) << 4);
			}

			if (nrest > 0)
			{
				// send the padding
				if ((linelength += 4) >= 76)
				{
					send(out, "\r\n");
				}

				send(out, BaseTable[buf[0]]);
				send(out, BaseTable[buf[1]]);
			if (nrest==2)
			{
			   	send(out, BaseTable[buf[2]]);
			}
			else
			{
			  	send(out, "=");
			}
				send(out, "=");
			}

			out.flush();
  		}
 		catch (Exception e)
 		{
   			e.printStackTrace();
   		}
	}

	public static void send(BufferedWriter out, String s)
	{
		try
		{
			out.write(s);
		}
		catch (Exception e)
		{
  			e.printStackTrace();
  		}
	}


	public static String InitMIMETypes()
	{
		String ecMIME_VERSION = "0001.503.2";
		String MIMETypes;

	    MIMETypes = "\r\n";

	    //TEXT TYPES
	    MIMETypes = MIMETypes + "text/html  <html> <htm> <shtml> <cgi> <asp> <inc>\r\n";
	    MIMETypes = MIMETypes + "text/plain  <txt>\r\n";
	    MIMETypes = MIMETypes + "text/richtext  <rtx>\r\n";
	    MIMETypes = MIMETypes + "text/tab-separated-values  <tsv>\r\n";
	    MIMETypes = MIMETypes + "text/x-setext  <etx>\r\n";
	    MIMETypes = MIMETypes + "text/x-sgml  <sgml> <sgm>\r\n";

	    //IMAGE TYPES
	    MIMETypes = MIMETypes + "image/gif  <gif>\r\n";
	    MIMETypes = MIMETypes + "image/ief  <ief>\r\n";
	    MIMETypes = MIMETypes + "image/jpeg  <jpeg> <jpg> <jpe>\r\n";
	    MIMETypes = MIMETypes + "image/png  <png>\r\n";
	    MIMETypes = MIMETypes + "image/tiff  <tiff> <tif>\r\n";

	    //APPLICATION TYPES
	    MIMETypes = MIMETypes + "application/mac-binhex40  <hqx>\r\n";
	    MIMETypes = MIMETypes + "application/msword  <doc>\r\n";
	    MIMETypes = MIMETypes + "application/octet-stream  <bin> <dms> <lha> <lzh> <exe> <class>\r\n";
	    MIMETypes = MIMETypes + "application/oda  <oda>\r\n";
	    MIMETypes = MIMETypes + "application/pdf  <pdf>\r\n";
	    MIMETypes = MIMETypes + "application/postscript  <ai> <eps> <ps>\r\n";
	    MIMETypes = MIMETypes + "application/powerpoint  <ppt>\r\n";
	    MIMETypes = MIMETypes + "application/rtf  <rtf>\r\n";
	    MIMETypes = MIMETypes + "application/x-stuffit  <sit>\r\n";
	    MIMETypes = MIMETypes + "application/x-tar  <tar>\r\n";
	    MIMETypes = MIMETypes + "application/x-wais-source  <src>\r\n";
	    MIMETypes = MIMETypes + "application/zip  <zip>\r\n";

	    //AUDIO TYPES
	    MIMETypes = MIMETypes + "audio/basic  <au> <snd>\r\n";
	    MIMETypes = MIMETypes + "audio/mpeg  <mpga> <mp2> <mp3>\r\n";
	    MIMETypes = MIMETypes + "audio/x-aiff  <aif> <aiff> <aifc>\r\n";
	    MIMETypes = MIMETypes + "audio/x-pn-realaudio  <ram>\r\n";
	    MIMETypes = MIMETypes + "audio/x-pn-realaudio-plugin  <rpm>\r\n";
	    MIMETypes = MIMETypes + "audio/x-realaudio  <ra>\r\n";
	    MIMETypes = MIMETypes + "audio/x-wav  <wav>\r\n";

	    //VIDEO TYPES
	    MIMETypes = MIMETypes + "video/mpeg  <mpeg> <mpg> <mpe>\r\n";
	    MIMETypes = MIMETypes + "video/quicktime  <qt> <mov>\r\n";
	    MIMETypes = MIMETypes + "video/x-msvideo  <avi>\r\n";

	    return MIMETypes;
	}

	public static String MIMETypeLookup(String FileName)
	{
		int pos, mpos, lstart, lend;			;
		String sTemp, sSeg, sExt, MIMEType, sHold;

		sSeg = FileName.substring(FileName.indexOf("."), FileName.length());
	    sTemp = sSeg.toLowerCase();

	    String MIMETypes = InitMIMETypes();

		MIMEType = "";
		sHold = sTemp.substring(0, 1);

	    //make sure we have the extension in the right format
	    if (sHold.equals("."))
	    {
			sExt = sTemp.substring(1, sTemp.length());
		}
		else
		{
			sExt = sSeg;
		}

	    if (!sExt.substring(0, 1).equals("<"))
	    {
			sExt = "<" + sExt;
		}

	    if (!sExt.substring(sExt.length()).equals(">"))
	    {
			sExt = sExt + ">";
		}

		//begin lookup
	    pos = MIMETypes.indexOf(sExt);
		mpos = 0;

	    if (pos > 0)
	    {
			while ((mpos < pos))
			{
	        	int temppos = 0;

	        	temppos = MIMETypes.indexOf("\r\n", mpos);

	        	if (temppos < pos)
	        	{
					mpos = temppos + 2;
				}
				else
				{
					break;
				}
			}

			if (pos > 0 && mpos < pos)
			{
				lstart = mpos;
				lend = MIMETypes.indexOf("  ", lstart);

				MIMEType = MIMETypes.substring(lstart, lend);
			}
			else
			{
				MIMEType = "mime/x-type-unknown";
			}
		}
	    else
	    {
	        MIMEType = "mime/x-type-unknown";
		}

	    return MIMEType;

	}
}
