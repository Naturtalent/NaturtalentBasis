package it.naturtalent.e4.search;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Separate Klasse zum Speichern der fuer die Suchoperation notwendigen Daten.
 * 
 * @author dieter
 *
 */
public class SearchOptions
{
	// Groß-und Kleinschreibung beachten
	private boolean isCaseSensitive = false;

	// die Suchmaske
	private String searchPattern;

	// momemtan nicht verwendet
	private boolean isWholeWordOnly;
	
	// eine RegularExpression anwenden 
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