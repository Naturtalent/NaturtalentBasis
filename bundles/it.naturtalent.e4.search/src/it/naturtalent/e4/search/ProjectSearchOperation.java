package it.naturtalent.e4.search;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;

/**
 * Die eigentliche Projektsuchfunktion zum Ablauf in einem RunnablePrrogress.
 * 
 
 * @author dieter
 *
 */
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
		int hitCount = 0;
		List<IAdaptable>searchItems = searchOptions.getSearchItems();
		monitor.beginTask("Suche in  Projekten", searchItems.size());	
		
		String patternString = searchOptions.getSearchPattern();
		boolean isCaseSensitiv = searchOptions.isCaseSensitive();
		boolean isRegEx = searchOptions.isRegularExpression();
		boolean isWholeWord = searchOptions.isWholeWordOnly();
		boolean isStringMatcher = true;
		Pattern pattern = PatternConstructor.createPattern(patternString, isRegEx, isStringMatcher, isCaseSensitiv, isWholeWord);
		
		// Broker meldet der Start einer neuen Suche
		eventBroker.post(ISearchInEclipsePage.START_SEARCH_EVENT, "Start der Suche");
		
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
					if(StringUtils.isEmpty(projectName))
					{	
						// Versuch einer Fehlerkorrektur, wenn iProject alias-Name 'null' ist,
						// dann wird versucht den Namen ueber Nt-Property zu ermitteln
						NtProject ntProject = it.naturtalent.e4.project.ui.Activator.findNtProject(iProject.getName());						
						projectName = ntProject.getName();
						System.out.println(iProject.getName()+" : ERROR Name : "+projectName);
						if(StringUtils.isEmpty(projectName))
						 continue;
					}
					
					Matcher m = pattern.matcher(projectName);
					if (m.matches()) 
					{
						eventBroker.post(ISearchInEclipsePage.MATCH_PATTERN_EVENT, iProject);
						hitCount++;
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

		eventBroker.post(ISearchInEclipsePage.END_SEARCH_EVENT, "Anzahl der Treffer: "+hitCount);		
	}

}
