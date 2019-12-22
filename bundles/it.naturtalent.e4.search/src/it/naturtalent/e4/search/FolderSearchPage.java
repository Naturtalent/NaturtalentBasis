package it.naturtalent.e4.search;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import it.naturtalent.e4.project.IResourceNavigator;

/**
 * Erweitert die Suchseite fuer Projekte um die Moeglichkeit 'Verzeichnisse' innerhalb der Projekte zu suchen.
 * 
 * @author dieter
 *
 */
public class FolderSearchPage extends ProjectSearchPage
{
	// mit dieser ID wird die Suchseite im Registry eingetragen @see it.naturtalent.e4.search.ProcessSearch
	public static final String FOLDERSEARCHPAGE_ID = "02foldersearch";

	// UI-Composite der Verzeichnissuche
	private FolderSearchComposite folderSeachComposite;
	
	// Die Dialogsettings werden separater SettingSection gespeichert
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public Control createControl(Composite parent)
	{		
		this.shell = parent.getShell();
		
		// Foldercomposite erzeugen und mit den Settings fuellen
		folderSeachComposite = new FolderSearchComposite(parent, SWT.NONE); 
		folderSeachComposite.setDialogSettings(settings);
		return folderSeachComposite;
	}
	
	@Override
	public String getSearchDialogMessage()
	{		
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

		// SearchOptionen vervollstaendigen mit den Zielprojekten (alle NtProjekte werden einbezogen)
		IResourceNavigator resourceNavigator = it.naturtalent.e4.project.ui.Activator.findNavigator();
		IAdaptable [] allAdaptables = resourceNavigator.getAggregateWorkingSet().getElements();
		if(ArrayUtils.isNotEmpty(allAdaptables))
			searchFolderOptions.setSearchItems(Arrays.asList(allAdaptables));		
		
		// mit der Datumsfiltereinstellungen vom Composite abfragen
		DateFilterOptions filterOptions = folderSeachComposite.getFilterOptions();
		
		// DatumsFilterfunktion ausfuehren und Ergebisliste in 'searchOptions' austauschen
		List<IAdaptable>dateFiltered = filterOptions.filterResources(searchFolderOptions.getSearchItems());
		if(dateFiltered != null)
			searchFolderOptions.setSearchItems(dateFiltered);	
		
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
	/*
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
	*/
	
	
}
	
