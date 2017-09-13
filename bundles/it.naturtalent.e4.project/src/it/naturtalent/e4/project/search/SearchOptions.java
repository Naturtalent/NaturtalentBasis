package it.naturtalent.e4.project.search;

public class SearchOptions
{
	private boolean isCaseSensitive = false;

	private String searchPattern;

	private boolean isWholeWordOnly;

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
}