package it.naturtalent.e4.search;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.search.HitRange;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;
import it.naturtalent.e4.project.search.ResourceSearchHit;
import it.naturtalent.e4.project.search.ResourceSearchResult;
import it.naturtalent.e4.project.search.SearchHit;
import it.naturtalent.e4.project.search.SearchOptions;
import it.naturtalent.e4.project.search.SearchResult;
import it.naturtalent.e4.project.search.TextSearcher;
import it.naturtalent.e4.project.search.textcomponents.ComponentPath;
import it.naturtalent.e4.project.search.textcomponents.TextComponent;
import it.naturtalent.e4.project.search.textcomponents.TextComponentType;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;


/**
 * Mit dieser Seite wird nach Projekten gesucht
 * 
 * @author dieter
 *
 */
public class ProjectSearchPage implements ISearchInEclipsePage
{

	protected ResourceNavigator resourceNavigator;
	
	protected SearchResultView resultView;
	
	public static final String PROJECTSEARCHPAGE_ID = "01projectsearch";
	
	private IProject [] projects = null;

	protected Shell shell;
	
	private ProjectSearchComposite projectSeachComposite;
	
	private boolean isCaseSensitive = false;
	
	//private String searchPattern = "";
	
	protected boolean isWholeWordOnly = false;
	
	// Hilfskonstrukte der Suchfunktionen
	protected ResourceSearchResult searchResult = new ResourceSearchResult();
	protected TextComponent textComponent;

	
	@Override
	public Control createControl(Composite parent)
	{		
		this.shell = parent.getShell();
		
		MPart mPart = Activator.ePartService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		if(mPart != null)
			resourceNavigator = (ResourceNavigator) mPart.getObject();
		
		// Ergebnisview anzeigen
		mPart = Activator.ePartService.findPart(SearchResultView.SEARCHRESULT_VIEW_ID);
		resultView = (SearchResultView) mPart.getObject();
		
		
		projectSeachComposite = new ProjectSearchComposite(parent, SWT.NONE); 
		projectSeachComposite.setResourceNavigator(resourceNavigator);
		return projectSeachComposite;
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
	
	
	@Override
	public void performSearch(IProgressMonitor progressMonitor)
	{		
		final SearchOptions searchOptions = new SearchOptions();
		
		searchOptions.setSearchPattern(projectSeachComposite.getSearchPattern());		
		searchOptions.setCaseSensitive(projectSeachComposite.getCaseSensitve());
		searchOptions.setWholeWordOnly(isWholeWordOnly);
					
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException
			{				
				SearchResult result = performSearch(searchOptions, monitor);
				monitor.done();
				
				if (result.getHitCount() > 0)
				{
					IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
					String projectSearchPattern = projectSeachComposite.getSearchPattern();
					settings.put(ProjectSearchComposite.SEARCH_PROJECT_SETTING,projectSearchPattern);

					// 'SEARCH_PROJECT_SETTINGS' - Array im Dialogsetting
					// speichern
					String[] projectPattern = settings.getArray(ProjectSearchComposite.SEARCH_PROJECT_SETTINGS);
					if (!ArrayUtils.contains(projectPattern,projectSearchPattern))
					{
						// max. 10 Eintraege werden gespeichert
						projectPattern = ArrayUtils.add(projectPattern, 0,projectSearchPattern);
						if (projectPattern.length > 9)
							projectPattern = ArrayUtils.remove(projectPattern,9);						
						settings.put(ProjectSearchComposite.SEARCH_PROJECT_SETTINGS,projectPattern);
					}
				}				
			}
		};

		try
		{
			ModalContext.run(runnable, false, progressMonitor, shell.getDisplay());
			
		} catch (InvocationTargetException e)
		{
			String msg = (e.getMessage() != null) ? e.getMessage() : "Fehler beim Suchen von: '"+searchOptions.getSearchPattern()+"'"; 
			msg = (msg == null) ? "unbekannter Fehler" : msg;
			MessageDialog.openError(shell,"Fehler beim Suchen", msg);
			
		} catch (InterruptedException e)
		{
			
			//clearResultCountLabel();
			//setSearchResult(null);
			
			/*
			MessageDialog.openError(getShell(),
					Messages.TelekomProjektSearchPage_TitleSearch,
					Messages.TelekomProjektSearchPage_CancelSearching);
					*/
		}
	}

	protected SearchResult performSearch(final SearchOptions searchOptions,
			IProgressMonitor monitor) throws InterruptedException
	{
		
		IAdaptable[] adapIAdaptables = projectSeachComposite
				.getResultAdaptables();
	
		if (ArrayUtils.isNotEmpty(adapIAdaptables) && (resourceNavigator != null))
		{		
			String resourceName;
			IResource resource;
			
			searchResult = new ResourceSearchResult();						
			monitor.beginTask("Suche in  Projekten", adapIAdaptables.length);			
			for (IAdaptable adaptable : adapIAdaptables)
			{				
				if (monitor.isCanceled())
				{
					throw new InterruptedException();
				}
 
				resource = null;
				if(adaptable instanceof IResource)
					resource = (IResource) adaptable;
				
				if(resource == null)
					continue;
				
				resourceName = resource.getName();				
				if(adaptable instanceof IProject)
				{
					IProject iProject = (IProject)adaptable;
					resourceName = new NtProject(iProject).getName();
				}
				
				// Name des Zielprojekts im Monitor anzeigen				
				monitor.subTask(Messages.bind("Projekt: ", resourceName));
				
				// Such nach Projektnamen
				if (StringUtils.isNotEmpty(searchOptions.getSearchPattern()))
				{
					textComponent = new TextComponent(
							TextComponentType.TEXT,
							"Text component", //$NON-NLS-1$
							resourceName, new ComponentPath());

					HitRange hitRange = TextSearcher.getFirstHit(textComponent,
							searchOptions);
					if (hitRange != null)
					{
						if (!isSearchResultContains(resource))
						{
							ResourceSearchHit hit = new ResourceSearchHit(
									resource, textComponent, hitRange);
							searchResult.addHit(hit);
						}
					}
				}				

				monitor.worked(1);
			}
		}
		
		return searchResult;
	}
	
	/*
	 * Suchergebnis in das Erbenisfenster uebertragen
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

	protected boolean isSearchResultContains(IResource resource)
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
		return Messages.SearchDialog_tbtmProject_text;
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

}
