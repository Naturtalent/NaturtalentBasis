package it.naturtalent.e4.search.registry;

import java.util.Map;
import java.util.TreeMap;

import it.naturtalent.e4.project.search.IProjectSearchPageRegistry;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;

public class ProjectSearchPageRegistry implements IProjectSearchPageRegistry
{
	static Map<String, ISearchInEclipsePage>mapSearchPages = new TreeMap<String, ISearchInEclipsePage>();
	
	@Override
	public ISearchInEclipsePage [] getSearchPages()
	{		
		return mapSearchPages.values().toArray(new ISearchInEclipsePage[mapSearchPages.values().size()]);		
	}

	@Override
	public void addSearchPage(String searchPageID,
			ISearchInEclipsePage searchPage)
	{
		mapSearchPages.put(searchPageID, searchPage);
	}

	@Override
	public ISearchInEclipsePage getSearchPage(String searchPageID)
	{		
		return mapSearchPages.get(searchPageID);				
	}

}
