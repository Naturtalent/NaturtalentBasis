package it.naturtalent.e4.project.search;

import org.eclipse.swt.widgets.Shell;

/**
 * @author Markus Gebhard
 */
public abstract class AbstractSearchInEclipsePage implements
		ISearchInEclipsePage
{
	private ISearchEclipseContainer searchContainer;

	public final void init(ISearchEclipseContainer searchContainer)
	{
		this.searchContainer = searchContainer;
	}

	protected final ISearchEclipseContainer getSearchContainer()
	{
		return searchContainer;
	}

	protected final Shell getShell()
	{
		return getSearchContainer().getShell();
	}
}