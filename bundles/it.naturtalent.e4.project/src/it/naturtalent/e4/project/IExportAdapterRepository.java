package it.naturtalent.e4.project;

import java.util.List;
import java.util.Map;

public interface IExportAdapterRepository
{		
	public Map <String, List<IExportAdapter>> getExportAdaptersMap();
	
	public List <IExportAdapter> getExportAdapters();
	
	public void addExportAdapter(IExportAdapter exportAdapter);
	
}
