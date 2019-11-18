package it.naturtalent.e4.search;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.operation.IRunnableWithProgress;

import it.naturtalent.e4.project.search.ISearchInEclipsePage;

/**
 * Die eigentliche Foldersuchfunktion zum Ablauf in einem RunnablePrrogress.
 * 
 * (momentan werden Unterverzeichnisse nicht beruecksichtigt)
 * 
 * @author dieter
 *
 */
public class FolderSearchOperation implements IRunnableWithProgress
{
	private SearchOptions searchOptions;
	
	private IEventBroker eventBroker;
	
	private Log log = LogFactory.getLog(this.getClass());

	
	public FolderSearchOperation(SearchOptions searchOptions)
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
		
		// Liste der in die Suche einzubeziehenden Projekte (innerhalb dieser Projekte wird nach Verzeichnissen Gesucht)
		List<IAdaptable>searchItems = searchOptions.getSearchItems();
		monitor.beginTask("Suche in  Projekten", searchItems.size());	//$NON-NLS-N$
		
		String patternString = searchOptions.getSearchPattern();
		boolean isCaseSensitiv = searchOptions.isCaseSensitive();
		boolean isRegEx = searchOptions.isRegularExpression();
		boolean isWholeWord = searchOptions.isWholeWordOnly();
		boolean isStringMatcher = true;
		
		// Suchpattern kompilieren
		Pattern pattern = PatternConstructor.createPattern(patternString, isRegEx, isStringMatcher, isCaseSensitiv, isWholeWord);
		
		// Broker meldet der Start der Suche
		eventBroker.post(ISearchInEclipsePage.START_SEARCH_EVENT, "Start der Suche"); //$NON-NLS-N$
		
		// alle Suchitems (iProjects) durchlaufen
		for (Object item : searchItems)
		{				
			if (monitor.isCanceled())
			{
				throw new InterruptedException();
			}
			
			if (item instanceof IProject)
			{
				
				IProject iProject = (IProject) item;				
				//matchFolder(iProject, pattern);
				
				// realer Pfad zum IProjekt im Dateisystem
				File projectFile = iProject.getFullPath().toFile();
				projectFile = iProject.getLocation().toFile();
				
				// soll verzeichniss die mit '.' beginnen ausblenden
				IOFileFilter notHiddenFilter = FileFilterUtils.notFileFilter(FileFilterUtils.prefixFileFilter("."));	
				
				// Dateien ausfiltern 
				IOFileFilter notFileFilter = FileFilterUtils.notFileFilter(FileFilterUtils.fileFileFilter());
				
				// Verzeichnisfilter 
				IOFileFilter dirFilter = FileFilterUtils.and(notHiddenFilter, TrueFileFilter.INSTANCE);
				
				// die zu untersuchenden Verzeichnisse auflisten
				Collection<File>directories = FileUtils.listFilesAndDirs(projectFile, notFileFilter, dirFilter);
				
				if (directories.size() > 0)
				{
					// jedes Verzeichnis auf pattern checken
					for (File projectDir : directories)
					{
						log.info("Search: (vor Matcher) "+projectDir);
						
						// Name des Verzeichnisses (ohne Pfadangaben) matchen
						String dirName = projectDir.getName();
						Matcher m = pattern.matcher(dirName);
						if (m.matches()) 
						{
							// sichtbare Verzeichnisse werden akzeptiert
							// findMember() findet keine Unterverzeichnisse
							// fuer das Checken der Unterverzeichnisse ist moeglicherweise
							// eine separate Funktion erforderlich
							IResource folder = iProject.findMember(dirName);
							if(folder != null)
							{								
								eventBroker.post(ISearchInEclipsePage.MATCH_PATTERN_EVENT, folder);
								hitCount++;
							}
						}
						
						log.info("Search: (nach Matcher)");
					}
				}
			}
			
			log.info("alle Verzeichnisse durchlaufen");
			
			monitor.worked(1);
		}
		
		log.info("Ende Search: count: " + hitCount);
		monitor.done();
		
		eventBroker.post(ISearchInEclipsePage.END_SEARCH_EVENT, "Anzahl der Treffer: "+hitCount);	//$NON-NLS-N$	
	}
	


}
