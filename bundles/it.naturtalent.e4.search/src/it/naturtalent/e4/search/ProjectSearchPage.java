package it.naturtalent.e4.search;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.search.ISearchInEclipsePage;
import it.naturtalent.e4.project.search.SearchResult;


/**
 * Mit dieser Seite wird die Suche nach Projekten gesteuert.
 * 
 * @author dieter
 *
 */
public class ProjectSearchPage implements ISearchInEclipsePage
{
	// mit dieser ID wird die Page im Registry eingetragen @see it.naturtalent.e4.search.ProcessSearch
	public static final String PROJECTSEARCHPAGE_ID = "01projectsearch";
	
	// wird benoetigt fuer ProgressDialog
	protected Shell shell;
	
	// UI-Composite der ProjektSearchPage
	private ProjectSearchComposite projectSeachComposite;
	
	// Default-Dialogsettings
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	/**
	 * UI fuer diese Seite erzeugen.
	 * 
	 */
	@Override
	public Control createControl(Composite parent)
	{		
		this.shell = parent.getShell();
		
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
			new ProgressMonitorDialog(shell).run(true, true, searchOperation);
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
	
	// Beschriftung Tab 
	@Override
	public String getLabel()
	{
		return Messages.SearchDialog_tbtmProject_text;
	}
		

	@Override
	public SearchResult getResult()
	{		
		return null;
	}

	@Override
	public String getSearchDialogMessage()
	{		
		return "Projekte suchen";
	}

}
