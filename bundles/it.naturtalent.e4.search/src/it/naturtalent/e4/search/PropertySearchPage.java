package it.naturtalent.e4.search;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;
import it.naturtalent.e4.project.search.ResourceSearchResult;
import it.naturtalent.e4.project.search.SearchResult;



//public class ProjectSearchPage extends AbstractSearchInEclipsePage

public class PropertySearchPage implements ISearchInEclipsePage
{		
	public static final String PROPERTYSEARCHPAGE_ID = "03propertysearch";
	
	private Shell shell;
	
	private PropertySearchComposite propertySeachComposite;
		
	// Hilfskonstrukte der Suchfunktionen
	private ResourceSearchResult searchResult = new ResourceSearchResult();
	
	@Override
	public Control createControl(Composite parent)
	{		
		this.shell = parent.getShell();
		
		propertySeachComposite = new PropertySearchComposite(parent, SWT.NONE); 
		//propertySeachComposite.setResourceNavigator(resourceNavigator);
		return propertySeachComposite;
	}

	@Override
	public boolean isStartSearchEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void requestFocus()
	{
		// TODO Auto-generated method stub

	}
	
	/*
	 * die seitenspezifische Suche
	 */	
	@Override
	public void performSearch(IProgressMonitor progressMonitor)
	{	
		// im UI definierten SearchOptions abfragen
		SearchOptions searchOptions = propertySeachComposite.getSearchOptions();
		
		// SearchOptionen vervollstaendigen mit den Zielpbjekten (alle NtProjekte werden einbezogen)
		IResourceNavigator resourceNavigator = it.naturtalent.e4.project.ui.Activator.findNavigator();
		IAdaptable [] allAdaptables = resourceNavigator.getAggregateWorkingSet().getElements();
		if(ArrayUtils.isNotEmpty(allAdaptables))
			searchOptions.setSearchItems(Arrays.asList(allAdaptables));	
		
		// mit der Datumsfiltereinstellungen vom Composite abfragen
		DateFilterOptions filterOptions = propertySeachComposite.getFilterOptions();
		
		// DatumsFilterfunktion ausfuehren und Ergebisliste in 'searchOptions' austauschen
		List<IAdaptable>dateFiltered = filterOptions.filterResources(searchOptions.getSearchItems());
		if(dateFiltered != null)
			searchOptions.setSearchItems(dateFiltered);		
		
		// die Property - Suchoperation instanziieren
		PropertySearchOperation searchOperation = new PropertySearchOperation(searchOptions);
		try
		{
			// Suchfunktion ausfuehren
			new ProgressMonitorDialog(shell).run(true, true, searchOperation);			
			
		} catch (InvocationTargetException e)
		{
			// Error
			Throwable realException = e.getTargetException();
			MessageDialog.openError(shell, "SearchError", realException.getMessage()); //$NON-NLS-N$
			
		} catch (InterruptedException e)
		{
			// Abbruch
			MessageDialog.openError(shell, "SearchCancel", e.getMessage()); //$NON-NLS-N$
			return;
		}		
	}

	@Override
	public String getLabel()
	{
		return "ProjektID";
	}
	
	@Override
	public SearchResult getResult()
	{		
		return searchResult;
	}

	@Override
	public String getSearchDialogMessage()
	{
		return "sucht Projekte durch Eingabe ihre ID"; //$NON-NLS-N$
	}

}
