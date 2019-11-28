package it.naturtalent.e4.search;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.internal.resources.Container;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.operation.IRunnableWithProgress;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;

/**
 * Die eigentliche Propertysuchfunktion zum Ablauf in einem RunnablePrrogress.
 * 
 * (momentan werden Unterverzeichnisse nicht beruecksichtigt)
 * 
 * @author dieter
 *
 */
public class PropertySearchOperation implements IRunnableWithProgress
{
	private SearchOptions searchOptions;
	
	private IEventBroker eventBroker;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public PropertySearchOperation(SearchOptions searchOptions)
	{
		super();
		this.searchOptions = searchOptions;
		
		// Broker liefert die Suchergebnisse
		MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();		
		eventBroker = currentApplication.getContext().get(IEventBroker.class);
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
	{
		int hitCount = 0;
		
		// Liste der in die Suche einzubeziehenden Projekte (innerhalb dieser Projekte wird nach Verzeichnissen Gesucht)
		List<IAdaptable>searchItems = searchOptions.getSearchItems();
		monitor.beginTask("Suche in  Projekten", searchItems.size());	//$NON-NLS-N$
		
		// null-Pattern durch "*" verhindern
		String patternString = searchOptions.getSearchPattern();
		patternString = StringUtils.isNotEmpty(patternString) ? patternString : "*"; 
		
		boolean isCaseSensitiv = searchOptions.isCaseSensitive();
		boolean isRegEx = searchOptions.isRegularExpression();
		boolean isWholeWord = searchOptions.isWholeWordOnly();
		boolean isStringMatcher = true;
		
		// Suchpattern kompilieren
		Pattern pattern = PatternConstructor.createPattern(patternString, isRegEx, isStringMatcher, isCaseSensitiv, isWholeWord);
		
		// Broker meldet der Start einer neuen Suche
		eventBroker.post(ISearchInEclipsePage.START_SEARCH_EVENT, "Start der Suche"); //$NON-NLS-N$
		
		// alle Suchitems (Projecte) durchlaufen
		for (Object item : searchItems)
		{				
			if (monitor.isCanceled())
			{
				throw new InterruptedException();
			}
			
			if (item instanceof IProject)
			{
				IProject iProject = (IProject) item;							
				if(!iProject.exists())
				{
					log.info("Fehler: nichtexistierendes Projekt "+iProject.getName());
					continue;
				}

				String projectID = iProject.getName();
				Matcher m = pattern.matcher(projectID);
				if (m.matches()) 
				{
					eventBroker.post(ISearchInEclipsePage.MATCH_PATTERN_EVENT, iProject);
					hitCount++;
				}
			}
			
			monitor.worked(1);
		}
		
		monitor.done();
		
		eventBroker.post(ISearchInEclipsePage.END_SEARCH_EVENT, "Anzahl der Treffer: "+hitCount);	//$NON-NLS-N$	
	}
	


}
