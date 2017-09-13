package it.naturtalent.e4.project.search;

import java.util.Map;

public interface IProjectSearchPageRegistry
{
	public void addSearchPage(String searchPageID, ISearchInEclipsePage page);
	public ISearchInEclipsePage [] getSearchPages();
	public ISearchInEclipsePage getSearchPage(String ID);	
}
