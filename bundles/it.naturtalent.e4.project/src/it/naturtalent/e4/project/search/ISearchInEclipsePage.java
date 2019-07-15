package it.naturtalent.e4.project.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Mit diesem Interface werden die unterschiedlichsten Suchen (Suchseiten) definiert.
 * 
 * @author Markus Gebhard
 */
public interface ISearchInEclipsePage
{
	// EventBroker meldet mit diesem Key den Start einer neuen Suche
	public static final String START_SEARCH_EVENT = "startsearchevent"; 

	// EventBroker meldet mit diesem Key die PatternMatches
	public static final String MATCH_PATTERN_EVENT = "matchpatternevent";
	
	// EventBroker meldet mit diesem Key den Start einer neuen Suche
	public static final String END_SEARCH_EVENT = "endsearchevent"; 
	
	// Messageanzeige im SearchDialog
	public String getSearchDialogMessage();
	
	// Bezeichnung der SuchSeite (Label des TabFolder-UI im SearchDialog)
	public String getLabel();
	
	// Ausfuehrung der konkreten Suchfunktion
	public void performSearch(IProgressMonitor progressMonitor);
	
	public boolean isStartSearchEnabled();
	
	// UI der spezifischen Suchseite
	public Control createControl(Composite parent);	
	
	public void requestFocus();
	
	public SearchResult getResult();
}