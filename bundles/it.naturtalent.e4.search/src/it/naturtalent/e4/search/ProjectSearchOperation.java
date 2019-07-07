package it.naturtalent.e4.search;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.operation.IRunnableWithProgress;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;

public class ProjectSearchOperation implements IRunnableWithProgress
{
	private SearchOptions searchOptions;
	
	private IEventBroker eventBroker;
	
	
	public ProjectSearchOperation(SearchOptions searchOptions)
	{
		super();
		this.searchOptions = searchOptions;
		
		MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();		
		eventBroker = currentApplication.getContext().get(IEventBroker.class);
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
	{
		List<IAdaptable>searchItems = searchOptions.getSearchItems();
		monitor.beginTask("Suche in  Projekten", searchItems.size());	
		
		String patternString = searchOptions.getSearchPattern();
		boolean isCaseSensitiv = searchOptions.isCaseSensitive();
		boolean isRegEx = searchOptions.isRegularExpression();
		boolean isWholeWord = searchOptions.isWholeWordOnly();
		boolean isStringMatcher = true;
		Pattern pattern = PatternConstructor.createPattern(patternString, isRegEx, isStringMatcher, isCaseSensitiv, isWholeWord);
		
		// Broker meldet der Start einer neuen Suche
		eventBroker.post(ISearchInEclipsePage.START_SEARCH_EVENT, "keine Ergebnisse");
		
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
						eventBroker.post(ISearchInEclipsePage.MATCH_PATTERN_EVENT, iProject);
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

		
	}

}
