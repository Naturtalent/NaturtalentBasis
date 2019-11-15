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
public class DiagnoseSearchPage implements ISearchInEclipsePage
{
	// mit dieser ID wird die Page im Registry eingetragen @see it.naturtalent.e4.search.ProcessSearch
	public static final String DIAGNOSESEARCHPAGE_ID = "04diagnosesearch";
	
	// wird benoetigt fuer ProgressDialog
	protected Shell shell;
	
	// UI-Composite der ProjektSearchPage
	private DiagnoseSearchComposite diagnoseSeachComposite;
	
	// Default-Dialogsettings
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	public enum DiagnoseCheckEnum
	{
		NOREALPROJECT,              // sucht nach ModelProjektIDs ohne reale Enstprechung
		NOPROJECTNAME, 				// sucht nach fehlenden ProjektID in der Modeldatenbank
		NOQUALIFIEDPROJECTNAME,		// sucht Projekt ohne quelifizierten Namen
		NOPROJECTDIR				// sucht Projekte ohne Entsprechung im Filesystem
	}
	
	
	/**
	 * UI fuer diese Seite erzeugen.
	 * 
	 */
	@Override
	public Control createControl(Composite parent)
	{		
		this.shell = parent.getShell();
		
		// Composite der ProjektSuchSeite
		diagnoseSeachComposite = new DiagnoseSearchComposite(parent, SWT.NONE); 
		
		return diagnoseSeachComposite;
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
		DiagnoseCheckEnum diagCheck = diagnoseSeachComposite.getSelectedCheck();		
		DiagnoseSearchOperation searchOperation = new DiagnoseSearchOperation(diagCheck);
		
		try
		{
			// Suchfunktion ausfuehren
			new ProgressMonitorDialog(shell).run(true, true, searchOperation);			
			
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
		return "Diagnose";
	}
		

	@Override
	public SearchResult getResult()
	{		
		return null;
	}

	@Override
	public String getSearchDialogMessage()
	{		
		return "Diagnose";
	}

}
