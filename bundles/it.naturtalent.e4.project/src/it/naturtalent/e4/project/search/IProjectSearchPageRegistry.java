package it.naturtalent.e4.project.search;

/**
 * Interface des Registry in dem die SeachPages-Interfaces gespeichert werden.
 * 
 * @author dieter
 *
 */
public interface IProjectSearchPageRegistry
{
	public void addSearchPage(String searchPageID, ISearchInEclipsePage page);
	public ISearchInEclipsePage [] getSearchPages();
	public ISearchInEclipsePage getSearchPage(String ID);	
}
