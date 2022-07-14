import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class FileUtilities
{
	
	public static void writeToFile(String fileName, InputStream iStream) throws IOException 
	{
		writeToFile(fileName, iStream, false);
	}

	public static void writeToFile(String fileName, InputStream iStream, boolean createDir) throws IOException 
	{
		String me = "FileUtils.WriteToFile";
	
		if (fileName == null) 
		{
			throw new IOException(me + ": filename is null");
		}
		
		if (iStream == null) 
		{
			throw new IOException(me + ": InputStream is null");
		}

		File theFile = new File(fileName);

		// Check if a file exists.
		if (theFile.exists()) 
		{
			String msg = theFile.isDirectory() ? "directory" : (!theFile.canWrite() ? "not writable" : null);
			
			if (msg != null) 
			{
				throw new IOException(me + ": file '" + fileName + "' is " + msg);
			}
		}

		// Create directory for the file, if requested.
		if (createDir && theFile.getParentFile() != null) 
		{
			theFile.getParentFile().mkdirs();
		}

		// Save InputStream to the file.
		BufferedOutputStream fOut = null;
		
		try 
		{
			fOut = new BufferedOutputStream(new FileOutputStream(theFile));

			byte[] buffer = new byte[512];
			int bytesRead = 0;
			
			while ((bytesRead = iStream.read(buffer)) != -1) 
			{
				fOut.write(buffer, 0, bytesRead);
			}
		} 
		catch (Exception e) 
		{
			throw new IOException(me + " failed, got: " + e.toString());
		} 
		finally 
		{
			close(iStream, fOut);
		}
	}

	protected static void close(InputStream iStream, OutputStream oStream) throws IOException 
	{
		try 
		{
			if (iStream != null) 
			{
				iStream.close();
			}
		} 
		finally 
		{
			if (oStream != null) 
			{
				oStream.close();
			}
		}
	}

	public static String getFileName(String fileName) 
	{
		String fileNamePart = fileName;
		int loc = fileName.indexOf(".");
		
		if (loc >= 0) 
		{
			fileNamePart = fileName.substring(0, loc);
		}
		
		return fileNamePart;
	}
	
	public static void deleteFile(String fileName) 
	{
		new File(fileName).delete();
	}
	
	public static String[] listFiles(String dirName, String fileType)
	{	
		String[] listOfFiles; 
		File folder = new File(dirName);
		
		if (fileType.equals(""))
		{
			listOfFiles = folder.list();
		}
		else
		{
			listOfFiles = folder.list(new FileFilter(fileType));
		}
		
		return listOfFiles;
	}
	
	public static void copyFile(String fileIn, String fileOut)
	{
		InputStream inStream = null;
		OutputStream outStream = null;

		try
		{

			File afile = new File(fileIn);
			File bfile = new File(fileOut);

			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);

			byte[] buffer = new byte[1024];

			int length;

			//copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0)
			{
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}