
public class BPMField 
{
	String bpmFieldName;
	String bpmFieldValue;

	public BPMField(String fieldName, String fieldValue)
	{
		this.bpmFieldName = fieldName;
		this.bpmFieldValue = fieldValue;	
	}

	public void setFieldName(String fieldName)
	{
		this.bpmFieldName = fieldName;
	}
	
	public String getFieldName()
	{
		return this.bpmFieldName;
	}
	
	public void setFieldValue(String fieldValue)
	{
		this.bpmFieldName = fieldValue;
	}
	
	public String getFieldValue()
	{
		return this.bpmFieldValue;
	}
	
}
