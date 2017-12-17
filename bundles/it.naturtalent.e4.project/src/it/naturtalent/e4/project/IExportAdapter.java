package it.naturtalent.e4.project;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

/**
 * Alle ExportTasks implementieren dieses Interface und koennen somit in einem Repository dynamisch eingesetzt werden.
 * 
 * @author dieter
 *
 */
public interface IExportAdapter
{
	public String getLabel();
	
	public Image getImage();
	
	public String getCategory ();
	
	public String getMessage ();
	
	public Action getExportAction ();
	
}
