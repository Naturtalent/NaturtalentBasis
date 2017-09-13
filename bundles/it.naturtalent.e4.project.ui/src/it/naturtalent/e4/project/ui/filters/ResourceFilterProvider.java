package it.naturtalent.e4.project.ui.filters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ViewerFilter;

public class ResourceFilterProvider
{

	private List<ViewerFilter> filters = new ArrayList<ViewerFilter>();
	
	
	public ViewerFilter [] getFilters()
	{		
		return filters.toArray(new ViewerFilter[filters.size()]);
	}
	
	public void addFilter(ViewerFilter filter)
	{
		filters.add(filter);		
	}
	
}
