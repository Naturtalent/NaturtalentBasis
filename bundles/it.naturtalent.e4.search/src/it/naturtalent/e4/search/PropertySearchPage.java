package it.naturtalent.e4.search;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.search.ISearchInEclipsePage;
import it.naturtalent.e4.project.search.ResourceSearchHit;
import it.naturtalent.e4.project.search.ResourceSearchResult;
import it.naturtalent.e4.project.search.SearchHit;
import it.naturtalent.e4.project.search.SearchResult;
import it.naturtalent.e4.project.search.textcomponents.TextComponent;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;



//public class ProjectSearchPage extends AbstractSearchInEclipsePage

public class PropertySearchPage implements ISearchInEclipsePage
{

	private ResourceNavigator resourceNavigator;
	
	private SearchResultView resultView;
	
	public static final String PROPERTYSEARCHPAGE_ID = "03propertysearch";
	
	private IProject [] projects = null;

	private Shell shell;
	
	private PropertySearchComposite propertySeachComposite;
	
	private boolean isCaseSensitive = false;
	
	//private String searchPattern = "";
	
	private boolean isWholeWordOnly = false;
	
	// Hilfskonstrukte der Suchfunktionen
	private ResourceSearchResult searchResult = new ResourceSearchResult();
	private TextComponent textComponent;

	
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
		SearchOptions searchOptions = propertySeachComposite.getPropertySearchOptions(null);
		
		// Suchoperation instanziieren
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

	/*
	 * Suchergebnis in das Ergebnisfenster uebertragen
	 */
	private void setSearchResult(SearchResult searchResult)
	{
		resultView.clear();
		SearchHit [] searchHits = searchResult.getHits();
		for(SearchHit searchHit : searchHits)
		{
			ResourceSearchHit resourceSearchHit = (ResourceSearchHit)searchHit;
			resultView.addResource(resourceSearchHit.iResource);
		}
	}

	private boolean isSearchResultContains(IResource resource)
	{
		SearchHit [] hits = searchResult.getHits();
				
		for(SearchHit hit : hits)
		{
			IResource containProject = ((ResourceSearchHit)hit).iResource;			
			if(containProject.equals(resource))
				return true;
		}
		
		return false;
	}

	@Override
	public String getLabel()
	{
		return "Projekteigenschaften";
	}
	
	private static final String SEARCH_VIEW_LABEL = "searchviewlabel";
	private void showSearchResultView()
	{
		MWindow resultWindow = null;
		
		if(Activator.application != null)
		{
			List<MWindow>existWindows = Activator.application.getChildren();
			for(MWindow mWindow : existWindows)
			{
				if(mWindow.getLabel().equals(SEARCH_VIEW_LABEL))
				{
					resultWindow = mWindow;
					break;
				}
			}
			
			if(resultWindow == null)
			{
				MTrimmedWindow newWindow = MBasicFactory.INSTANCE.createTrimmedWindow();
				newWindow.setLabel(SEARCH_VIEW_LABEL);
				newWindow.setWidth(200);
				Activator.application.getChildren().add(newWindow);
				
				MWindow existingWindow = Activator.application.getChildren().get(0);
				existingWindow.setX(200);
			}
		}
	}

	@Override
	public SearchResult getResult()
	{		
		return searchResult;
	}

	@Override
	public String getSearchDialogMessage()
	{
		return "sucht Projekte durch ihre ID (Zeitfilter können ausgewählt werden)";
	}

}
