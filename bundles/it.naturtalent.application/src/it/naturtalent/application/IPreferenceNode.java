package it.naturtalent.application;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * Das Interface definiert die Detailseite der PreferenzUI.
 * 
 * Ueber den Adapter 'IPreferenceAdapter' erfolgt die individuelle Anpassung.
 * 
 * @author dieter
 *
 */
public interface IPreferenceNode
{
	// Zugriff auf die individuelle Anpassung
	public IPreferenceAdapter getPreferenceAdapter();	
	public void setPreferenceAdapter(IPreferenceAdapter preferenceAdapter);
	
	public void refresh();
	
	// Zugriff auf die individuellen Composites
	public Composite getNodeComposite();
	public void setNodeComposite(Composite nodeComposite);
	
	public String getTitle();
	public void setTitle(String title);
	public void setGroupNode(Group groupNode);
	
	// fuer die Erzeugung des Details notwendige Parentcomposite
	public Composite getParentNode();	
}
