package it.naturtalent.e4.project.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Mit diesem Interface werden die unterschiedlichsten Suchen definiert.
 * 
 * @author Markus Gebhard
 */
public interface ISearchInEclipsePage
{
	// EventBroker meldet mit diesem Key den Start einer neuen Suche
	public static final String START_SEARCH_EVENT = "startsearchevent"; 

	// EventBroker meldet mit diesem Key die PatternMatches
	public static final String MATCH_PATTERN_EVENT = "matchpatternevent"; 
	
	// Bezeichnung der SuchSeite (Label des TabFolder-UI im SearchDialog)
	public String getLabel();
	
	public void performSearch(IProgressMonitor progressMonitor);
	
	public boolean isStartSearchEnabled();
	
	public Control createControl(Composite parent);	
	
	public void requestFocus();
	
	public SearchResult getResult();
}