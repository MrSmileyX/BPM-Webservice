import java.util.*;
import java.io.*;

public class AppProperties
{
	public static String Read(String prop, boolean Encrypted)
	{
		String tempVal = "";
		String propVal = "";

		try
		{
			Properties p = new Properties();

			p.load(new FileInputStream(Constants.PropFile));
			tempVal = p.getProperty(prop);

			if (Encrypted)
			{
		 		propVal = Encryption.Decrypt(tempVal);
			}
			else
			{
				propVal = tempVal;
			}

			return propVal;
		}
		catch (Exception e)
		{
		  	WriteLog.WriteToLog(e.getMessage());
		  	return "";
		}
	}

	
	public static String readISRAProp(String israProp, boolean Encrypted)
	{
		String tempVal = "";
		String propVal = "";

		try
		{
			Properties p = new Properties();

			//p.load(new FileInputStream(Constants.ISRAPropFile));
			tempVal = p.getProperty(israProp);

			if (Encrypted)
			{
		 		propVal = Encryption.Decrypt(tempVal);
			}
			else
			{
				propVal = tempVal;
			}

			return propVal;
		}
		catch (Exception e)
		{
		  	WriteLog.WriteToLog(e.getMessage());
		  	return "";
		}
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
	
	public static void Write(String prop, String value, boolean Encrypted)
	{
		String propVal = "";

		try
		{
			SortedProperties p = new SortedProperties();
			p.load(new FileInputStream(Constants.PropFile));

			if (Encrypted)
			{
				propVal = Encryption.Encrypt(value);
			}
			else
			{
				propVal = value;
			}

			p.put(prop, propVal);

			FileOutputStream out = new FileOutputStream(Constants.PropFile);
			p.store(out, "/*BPM Correspondence Extract Properties Updated */");

			out.close();

		}
		catch (Exception e)
		{
			WriteLog.WriteToLog("Unable to update property value for " + prop + " to " + value);
			WriteLog.WriteToLog(e.getMessage());
		}
	}
}

class SortedProperties extends Properties
{
	static final long serialVersionUID = 1L;
	
	public synchronized Enumeration<Object> keys()
	{
		final Object[] keys = keySet().toArray();

		Arrays.sort(keys);

		return new Enumeration<Object>()
		{
			int i = 0;

			public boolean hasMoreElements()
			{
				return i < keys.length;
			}

			public Object nextElement()
			{
				return keys[i++];
			}
		};
	}
}
