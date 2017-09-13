package it.naturtalent.e4.project;


import java.util.List;
import java.util.Map;

public interface IImportAdapterRepository
{		
	public Map <String, List<IImportAdapter>> getImportAdaptersMap();
	
	public List <IImportAdapter> getImportAdapters();
	
	public void addImportAdapter(IImportAdapter importAdapter);
	
}
