package it.naturtalent.e4.search;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;
import it.naturtalent.e4.project.search.ResourceSearchHit;
import it.naturtalent.e4.project.search.ResourceSearchResult;
import it.naturtalent.e4.project.search.SearchHit;
import it.naturtalent.e4.project.search.SearchResult;
import it.naturtalent.e4.project.search.textcomponents.TextComponent;


/**
 * Mit dieser Seite wird die Suche nach Projekten gesteuert.
 * 
 * @author dieter
 *
 */
public class ProjectSearchPage2 implements ISearchInEclipsePage
{

	//protected ResourceNavigator resourceNavigator;
	
	//protected SearchView resultView;
	//private TableViewer tableViewer;
	
	//public static final String PROJECTSEARCHPAGE_ID = "01projectsearch";
	
	//private IProject [] projects = null;

	protected Shell shell;
	
	// UI-Composite der ProjektSearchPage
	private ProjectSearchComposite projectSeachComposite;
	
	//private boolean isCaseSensitive = false;
	
	//private String searchPattern = "";
	
	//protected boolean isWholeWordOnly = false;
	
	// Hilfskonstrukte der Suchfunktionen
	protected ResourceSearchResult searchResult = new ResourceSearchResult();
	protected TextComponent textComponent;
	
	private Pattern pattern; 
	
	// Default-Dialogsettings
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	private IEventBroker eventBroker;

	
	@Override
	public Control createControl(Composite parent)
	{		
		this.shell = parent.getShell();
		
		// Ergebnisview anzeigen
		/*
		MPart mPart = Activator.ePartService.findPart(SearchResultView.SEARCHRESULT_VIEW_ID);
		resultView = (SearchView) mPart.getObject();
		tableViewer = resultView.getTableViewer();
		*/
		
		// Composite der ProjektSuchSeite
		projectSeachComposite = new ProjectSearchComposite(parent, SWT.NONE); 
		
		// Default-DialogSettings eintragen
		projectSeachComposite.setDialogSettings(settings);
		
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
	
	/*
	 * Diese Funktion wird vom zentralen SearchDialog aufgerufen und fuehrt die Suchfunktion aus.
	 * 
	 */
	@Override
	public void performSearch(IProgressMonitor progressMonitor)
	{
		// SearchOptionen vom Composite abfragen
		final SearchOptions searchOptions = projectSeachComposite.getSearchOptions();
				
		// Suchoperation instanziieren
		ProjectSearchOperation searchOperation = new ProjectSearchOperation(searchOptions);
		
		try
		{
			// Suchfunktion ausfuehren
			new ProgressMonitorDialog(shell).run(true, false, searchOperation);
			projectSeachComposite.saveDialogSettings(settings);
			
		} catch (InvocationTargetException e)
		{
			// Error
			Throwable realException = e.getTargetException();
			MessageDialog.openError(shell, "SearchError", realException.getMessage());
			
		} catch (InterruptedException e)
		{
			// Abbruch
			MessageDialog.openError(shell, "SearchCancel", e.getMessage());
			return;
		}
		
	}
	
	
	
	public void performSearch_OLD(IProgressMonitor progressMonitor)
	{		
		// SearchOptionen vom Composite abfragen
		final SearchOptions searchOptions = projectSeachComposite.getSearchOptions();
		
		MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();		
		eventBroker = currentApplication.getContext().get(IEventBroker.class);
		//eventBroker.post(MATCH_PATTERN_EVENT, "nichts gefunden");			
		
		
		
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor)throws InvocationTargetException, InterruptedException
			{				
				List<IAdaptable>searchItems = searchOptions.getSearchItems();
				monitor.beginTask("Suche in  Projekten", searchItems.size());	
				
				String patternString = searchOptions.getSearchPattern();
				boolean isCaseSensitiv = searchOptions.isCaseSensitive();
				boolean isRegEx = searchOptions.isRegularExpression();
				boolean isWholeWord = searchOptions.isWholeWordOnly();
				boolean isStringMatcher = true;
				pattern = PatternConstructor.createPattern(patternString, isRegEx, isStringMatcher, isCaseSensitiv, isWholeWord);
								
				// alle Suchitems durchlaufen
				for (Object item : searchItems)
				{				
					if (monitor.isCanceled())
					{
						throw new InterruptedException();
					}
					
					if (item instanceof IProject)
					{
						IProject iProject = (IProject) item;
						String projectName;
						try
						{
							projectName = iProject.getPersistentProperty(INtProject.projectNameQualifiedName);
							Matcher m = pattern.matcher(projectName);
							if (m.matches()) 
							{
								eventBroker.post(MATCH_PATTERN_EVENT, iProject);
								//System.out.println("posted: "+projectName);
							}
						} catch (CoreException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					monitor.worked(1);
				}
				
				monitor.done();
				
				// Dialogsettings speicheern
				projectSeachComposite.saveDialogSettings(settings);
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
	
	/*
	protected SearchResult performSearch(final SearchOptions searchOptions,
			IProgressMonitor monitor) throws InterruptedException
	{
		
//		IAdaptable[] adapIAdaptables = projectSeachComposite.getResultAdaptables();
		
		// von
		
		String patternString = searchOptions.getSearchPattern();
		patternString = StringUtils.trim(patternString);
		
		
		pattern = PatternConstructor.createPattern(patternString, false, true, true, true);
		System.out.println(pattern.toString());
		
		
		
		
		// bis
	
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
					
					// von
					//				
					Matcher m = pattern.matcher(resourceName);
					if (m.matches()) 
					{
						
						System.out.println(resourceName);
					}
					
					while (m.find()) {
			            System.out.print("Start index: " + m.start());
			            System.out.print(" End index: " + m.end() + " ");
			            System.out.println(m.group());
					}
					

					// bis
					
					

					HitRange hitRange = TextSearcher.getFirstHit(textComponent,searchOptions);
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
	*/
	
	/*
	 * Suchergebnis in das Erbenisfenster uebertragen
	 */
	/*
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
	*/

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
