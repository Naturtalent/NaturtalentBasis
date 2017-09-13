package it.naturtalent.e4.project.search;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Markus Gebhard
 */
public class SearchResult
{

	private ArrayList<SearchHit>hits = new ArrayList<SearchHit>();

	private ArrayList<Throwable>executionErrors = new ArrayList<Throwable>();

	public void addHit(SearchHit hit)
	{
		hits.add(hit);
	}

	public int getHitCount()
	{
		return hits.size();
	}

	public SearchHit getHit(int index)
	{
		return (SearchHit) hits.get(index);
	}

	public void addExecutionError(Throwable t)
	{
		executionErrors.add(t);
	}

	public SearchHit[] getHits()
	{
		return (SearchHit[]) hits.toArray(new SearchHit[hits.size()]);
	}
}