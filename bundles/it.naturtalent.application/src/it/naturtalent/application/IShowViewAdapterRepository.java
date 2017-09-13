package it.naturtalent.application;

import java.util.List;
import java.util.Map;

public interface IShowViewAdapterRepository
{		
	public Map <String, List<IShowViewAdapter>> getShowViewAdaptersMap();
	
	public List <IShowViewAdapter> getShowViewAdapters();
	
	public void addShowViewAdapter(IShowViewAdapter showViewAdapter);
	
}
