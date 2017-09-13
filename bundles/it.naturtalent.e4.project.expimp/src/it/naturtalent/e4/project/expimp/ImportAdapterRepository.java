package it.naturtalent.e4.project.expimp;

import it.naturtalent.e4.project.IImportAdapter;
import it.naturtalent.e4.project.IImportAdapterRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class ImportAdapterRepository implements IImportAdapterRepository
{
	
	private static final String GENERAL_CONTEXT = "General";
	
	private static List<IImportAdapter>importAdapters = new ArrayList<IImportAdapter>();
	
	@Override
	public List<IImportAdapter> getImportAdapters()
	{
		return importAdapters;
	}

	@Override
	public void addImportAdapter(IImportAdapter importAdapter)
	{
		if(!importAdapters.contains(importAdapter))
			importAdapters.add(importAdapter);		
	}

	@Override
	public Map<String, List<IImportAdapter>> getImportAdaptersMap()
	{
		Map<String, List<IImportAdapter>>adaptersMap = new HashMap<String, List<IImportAdapter>>();
				
		for(IImportAdapter adapter: importAdapters)
		{
			String context = StringUtils.isNotEmpty(adapter.getContext()) ? adapter.getContext() : GENERAL_CONTEXT;
			
			List<IImportAdapter>adapterList = adaptersMap.get(context);
			if(adapterList == null)
			{
				adapterList = new ArrayList<IImportAdapter>();
				adaptersMap.put(context, adapterList);
			}
			adapterList.add(adapter);
		}
		
		return adaptersMap;
	}

}
