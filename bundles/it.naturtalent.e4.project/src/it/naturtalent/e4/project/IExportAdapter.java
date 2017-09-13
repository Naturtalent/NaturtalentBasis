package it.naturtalent.e4.project;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

public interface IExportAdapter
{
	public String getLabel();
	
	public Image getImage();
	
	public String getCategory ();
	
	public String getMessage ();
	
	public Action getExportAction ();
	
}
