package it.naturtalent.e4.project.expimp;

import java.beans.PropertyChangeEvent;

/**
 * Allgemeine Datenstruktur zum Im- und Export.
 * 
 * @author dieter
 *
 */
public class ExpImportData extends BaseBean
{
	public static final String PROP_EXPIMPORTITEM = "expimportitem";
	public static final String PROP_EXPIMPORTDATA = "expimportdata";
	
	private String label;
	
	private Object data;

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		firePropertyChange(new PropertyChangeEvent(this, PROP_EXPIMPORTITEM, this.label,
				this.label = label));		
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		firePropertyChange(new PropertyChangeEvent(this, PROP_EXPIMPORTDATA, this.data,
				this.data = data));		
	}
	
	

}
