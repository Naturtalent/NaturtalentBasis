package it.naturtalent.e4.project;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.IWorkingSet;



public interface IResourceNavigator
{

	public static final String NAVIGATOR_EVENT = "navigatorevent/";
	public static final String NAVIGATOR_EVENT_IMPORTED = NAVIGATOR_EVENT+"imported";
	public static final String NAVIGATOR_EVENT_SELECTED = NAVIGATOR_EVENT+"selected";
	public static final String NAVIGATOR_EVENT_SELECT_PROJECT = NAVIGATOR_EVENT+"selectProject";
	
	/**
	 * In diesem WorkingSet sind alle im Navigator dargestellten Ressourcen
	 * zusammengefasst. 
	 * Der Name diese WorkingSets ist intern unter 'aggregatedResourceSetName'
	 * gespeichert
	 * 
	 * @return
	 */
	public IWorkingSet getAggregateWorkingSet ();
	
	
	/**
	 * Die WorkingSets der im Navigator angezeigten Ressourcen.
	 * @return
	 */
	public IWorkingSet [] getWindowWorkingSets ();
	
	
	/**
	 * Alle verfuegbaren WorkingSets zurueckgeben.
	 * @return
	 */
	public IWorkingSet [] getWorkingSets ();
	
	/**
	 * Den Navigator mit neuen WorkingSets initialisieren.
	 * 
	 * @param workingSets
	 */
	public void setWorkingSets (IWorkingSet [] workingSets);
	
	public TreeViewer getViewer();
	
	public Clipboard getClipboard();
	
	/**
	 * @return true, wenn Resource im WorkingSet Modus angezeigt werden
	 */
	public boolean getTopLevelStatus();
	
}
