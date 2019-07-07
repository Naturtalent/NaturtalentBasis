package it.naturtalent.e4.search;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

public class SearchOptions
{
	private boolean isCaseSensitive = false;

	private String searchPattern;

	private boolean isWholeWordOnly;
	
	private boolean isRegularExpression;
	
	// Liste der in die Suche einbezogenen Objekte
	private List<IAdaptable>searchItems;

	public void setSearchPattern(String searchPattern)
	{
		this.searchPattern = searchPattern;
	}

	public String getSearchPattern()
	{
		return searchPattern;
	}

	public void setCaseSensitive(boolean isCaseSensitive)
	{
		this.isCaseSensitive = isCaseSensitive;
	}

	public boolean isCaseSensitive()
	{
		return isCaseSensitive;
	}

	public void setWholeWordOnly(boolean isWholeWordOnly)
	{
		this.isWholeWordOnly = isWholeWordOnly;
	}

	public boolean isWholeWordOnly()
	{
		return isWholeWordOnly;
	}
		

	public boolean isRegularExpression()
	{
		return isRegularExpression;
	}

	public void setRegularExpression(boolean isRegularExpression)
	{
		this.isRegularExpression = isRegularExpression;
	}

	public List<IAdaptable> getSearchItems()
	{
		return searchItems;
	}

	public void setSearchItems(List<IAdaptable> searchItems)
	{
		this.searchItems = searchItems;
	}
	
	
}