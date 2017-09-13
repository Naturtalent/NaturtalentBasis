package it.naturtalent.e4.project.search;

import it.naturtalent.e4.project.IResourceNavigator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Markus Gebhard
 */
public interface ISearchInEclipsePage
{
	// Bezeichnung der SuchSeite (z.B. Projekte fuer die Seite mit der nach Projekten gesucht wird)
	public String getLabel();
	
	public void performSearch(IProgressMonitor progressMonitor);
	
	public boolean isStartSearchEnabled();
	
	public Control createControl(Composite parent);	
	
	public void requestFocus();
	
	public SearchResult getResult();
}