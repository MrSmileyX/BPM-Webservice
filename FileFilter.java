import java.io.File;
import java.io.FilenameFilter;

public class FileFilter implements FilenameFilter 
{  
	private String fileExtension;  
	
	public FileFilter(String fileExtension) 
	{ 
		this.fileExtension = fileExtension;
	}
	
	@Override
    public boolean accept(File dir, String name) 
    {
       if(name.lastIndexOf('.') > 0)
       {
          int lastIndex = name.lastIndexOf('.');
          String ext = name.substring(lastIndex);
          
          if(ext.equals(fileExtension))
          {
             return true;
          }
       }
       return false;
    }	
}  