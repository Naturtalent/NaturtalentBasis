package it.naturtalent.e4.search;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;

/**
 * Erweitert die Suchseite fuer Projekte um die Moeglichkeit 'Verzeichnisse' innerhalb der Projekte zu suchen.
 * 
 * @author dieter
 *
 */
public class FolderSearchPage extends ProjectSearchPage
{
	// mit dieser ID wird die Page im Registry eingetragen @see it.naturtalent.e4.search.ProcessSearch
	public static final String FOLDERSEARCHPAGE_ID = "02foldersearch";

	// UI-Composite der Verzeichnissuche
	private FolderSearchComposite folderSeachComposite;
	
	// Die Dialogsettings werden als separate Sektion gespeichert
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	
	@Override
	public Control createControl(Composite parent)
	{		
		this.shell = parent.getShell();
		
		folderSeachComposite = new FolderSearchComposite(parent, SWT.NONE); 
		folderSeachComposite.setDialogSettings(settings);
		return folderSeachComposite;
	}
	
	@Override
	public String getSearchDialogMessage()
	{
		// TODO Auto-generated method stub
		return "sucht nach Verzeichnissen innerhalb der Projekte (Subdirektories werden nicht berücksichtigt)";
	}
	
	/*
	 * Diese Funktion wird vom zentralen SearchDialog aufgerufen und fuehrt die Suchfunktion aus.
	 * 
	 */
	@Override
	public void performSearch(IProgressMonitor progressMonitor)
	{
		// SearchOptionen vom Composite abfragen
		SearchOptions searchFolderOptions = folderSeachComposite.getFolderSearchOptions();
		SearchOptions searchProjectOptions = folderSeachComposite.getProjectSearchOptions();
				
		// sollte auf keine Projekte fokussiert sein, werden alle NtProjekte einbezogen
		List<IAdaptable>searchItems = searchProjectOptions.getSearchItems();
		if((searchItems == null) || (searchItems.isEmpty()))
		{
			IResourceNavigator resourceNavigator = it.naturtalent.e4.project.ui.Activator.findNavigator();
			IAdaptable [] allAdaptables = resourceNavigator.getAggregateWorkingSet().getElements();
			if(ArrayUtils.isNotEmpty(allAdaptables))
				searchProjectOptions.setSearchItems(Arrays.asList(allAdaptables));				
		}
		
		// die einzubeziehenden Projekte suchen (wenn Eingaben im ProjektGroup erfolgten)
		preProjectSearch(searchProjectOptions);
		
		// die Liste der einzubeziehenden Suchobjekte wird an die Foldersuchoptionen uebergeben		
		searchFolderOptions.setSearchItems(searchProjectOptions.getSearchItems());
		
		// Suchoperation instanziieren
		FolderSearchOperation searchOperation = new FolderSearchOperation(searchFolderOptions);
		
		try
		{
			// Suchfunktion ausfuehren
			new ProgressMonitorDialog(shell).run(true, true, searchOperation);
			folderSeachComposite.saveDialogSettings(settings);
			
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
		return "Verzeichnis";
	}
	
	/*
	 * Ausgehend von den Projekt-SearchOptions werden die fuer die eigentliche Verzeichnissuche einbezogenen
	 * Projekte vorausgewaehlt und die Liste der verfuegbaren Projekte entsprechen aktualisiert.
	 * 
	 */
	private void preProjectSearch(SearchOptions searchProjectOptions)
	{
		List<IAdaptable>foundedItems = new ArrayList<IAdaptable>();
		
		String searchPattern = searchProjectOptions.getSearchPattern();
		if(StringUtils.isNotEmpty(searchPattern))
		{
			List<IAdaptable>searchItems = searchProjectOptions.getSearchItems();
			if((searchItems != null) && (!searchItems.isEmpty()))
			{
				String patternString = searchProjectOptions.getSearchPattern();
				boolean isCaseSensitiv = searchProjectOptions.isCaseSensitive();
				boolean isRegEx = searchProjectOptions.isRegularExpression();
				boolean isWholeWord = searchProjectOptions.isWholeWordOnly();
				boolean isStringMatcher = true;
				Pattern pattern = PatternConstructor.createPattern(
						patternString, isRegEx, isStringMatcher, isCaseSensitiv,
						isWholeWord);
				
				// alle Suchitems durchlaufen
				for (Object item : searchItems)
				{				
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
								foundedItems.add(iProject);
							}
						} catch (CoreException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			
				// Liste der einzubeziehenden Projekte aktualisieren
				searchProjectOptions.setSearchItems(foundedItems);
			}
		}
	}
	
	
}
	
