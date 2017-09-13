package it.naturtalent.e4.project.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;

public class ResourceSearchResult extends SearchResult
{
	public IResource [] getResourceResult()
	{
		SearchHit [] searchHits = getHits();		
		List<IResource>lResult = new ArrayList<IResource>();
		
		for(SearchHit searchHit : searchHits)
		{
			ResourceSearchHit resourceSearchHit = (ResourceSearchHit)searchHit;
			lResult.add((IResource) resourceSearchHit.iResource);
		}
		
		return lResult.toArray(new IResource[lResult.size()]);		
	}
}
