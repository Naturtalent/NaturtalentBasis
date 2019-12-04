package it.naturtalent.e4.project.expimp;

import java.beans.PropertyChangeEvent;
import java.util.List;

public class ExpImportDataModel extends BaseBean
{
	public static final String PROP_EXPIMPORTMODELDATA = "expimportmodeldata";
	
	private List<ExpImportData>data;

	public List<ExpImportData> getData()
	{
		return data;
	}

	public void setData(List<ExpImportData> data)
	{
		firePropertyChange(new PropertyChangeEvent(this, PROP_EXPIMPORTMODELDATA, this.data,
				this.data = data));

		this.data = data;
	}

}
