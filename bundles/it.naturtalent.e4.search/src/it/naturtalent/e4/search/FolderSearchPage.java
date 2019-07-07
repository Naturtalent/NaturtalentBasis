package it.naturtalent.e4.search;

import java.io.File;
import java.io.FileFilter;
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

import it.naturtalent.e4.project.IResourceNavigator;

/**
 * Erweitert die Suchseite fuer Projekte um die Moeglichkeit Verzeichnisse innerhalb der Projekte zu suchen.
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
	
	@Override
	public Control createControl(Composite parent)
	{		
		this.shell = parent.getShell();
		
		folderSeachComposite = new FolderSearchComposite(parent, SWT.NONE); 
		return folderSeachComposite;
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
				searchItems = (Arrays.asList(allAdaptables));
		}
		
		// die Liste der einzubeziehenden Suchobjekte wird an die Foldersuchoptionen uebergeben
		searchFolderOptions.setSearchItems(searchItems);
		
		// Suchoperation instanziieren
		FolderSearchOperation searchOperation = new FolderSearchOperation(searchFolderOptions);
		
		try
		{
			// Suchfunktion ausfuehren
			new ProgressMonitorDialog(shell).run(true, false, searchOperation);
			//projectSeachComposite.saveDialogSettings(settings);
			
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




	@Override
	public String getLabel()
	{
		return "Verzeichnis";
	}
	
	
}
	
