package it.naturtalent.application;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


public class ShowViewAdapterRepository implements IShowViewAdapterRepository
{

	private static final String GENERAL_CONTEXT = "General";
	
	private static List<IShowViewAdapter>showViewAdapters = new ArrayList<IShowViewAdapter>();
	
	@Override
	public Map<String, List<IShowViewAdapter>> getShowViewAdaptersMap()
	{
		Map<String, List<IShowViewAdapter>>adaptersMap = new HashMap<String, List<IShowViewAdapter>>();
		
		for(IShowViewAdapter adapter : showViewAdapters)
		{
			String context = StringUtils.isNotEmpty(adapter.getContext()) ? adapter.getContext() : GENERAL_CONTEXT;
			
			List<IShowViewAdapter>adapterList = adaptersMap.get(context);
			if(adapterList == null)
			{
				adapterList = new ArrayList<IShowViewAdapter>();
				adaptersMap.put(context, adapterList);
			}
			adapterList.add(adapter);
		}
		
		return adaptersMap;

	}

	@Override
	public List<IShowViewAdapter> getShowViewAdapters()
	{
		return showViewAdapters;
	}

	@Override
	public void addShowViewAdapter(IShowViewAdapter showViewAdapter)
	{
		if(!showViewAdapters.contains(showViewAdapter))
			showViewAdapters.add(showViewAdapter);				
	}

}
