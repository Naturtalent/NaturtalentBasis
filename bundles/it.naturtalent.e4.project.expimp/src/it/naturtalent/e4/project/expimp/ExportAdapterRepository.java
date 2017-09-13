package it.naturtalent.e4.project.expimp;

import it.naturtalent.e4.project.IExportAdapter;
import it.naturtalent.e4.project.IExportAdapterRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class ExportAdapterRepository implements IExportAdapterRepository
{
	
	private static final String GENERAL_CONTEXT = "General";
	
	private static List<IExportAdapter>exportAdapters = new ArrayList<IExportAdapter>();
	
	@Override
	public List<IExportAdapter> getExportAdapters()
	{
		return exportAdapters;
	}

	@Override
	public void addExportAdapter(IExportAdapter exportAdapter)
	{
		if(!exportAdapters.contains(exportAdapter))
			exportAdapters.add(exportAdapter);		
	}

	@Override
	public Map<String, List<IExportAdapter>> getExportAdaptersMap()
	{
		Map<String, List<IExportAdapter>>adaptersMap = new HashMap<String, List<IExportAdapter>>();
				
		for(IExportAdapter adapter: exportAdapters)
		{
			String context = StringUtils.isNotEmpty(adapter.getCategory()) ? adapter.getCategory() : GENERAL_CONTEXT;
			
			List<IExportAdapter>adapterList = adaptersMap.get(context);
			if(adapterList == null)
			{
				adapterList = new ArrayList<IExportAdapter>();
				adaptersMap.put(context, adapterList);
			}
			adapterList.add(adapter);
		}
		
		return adaptersMap;
	}

}
