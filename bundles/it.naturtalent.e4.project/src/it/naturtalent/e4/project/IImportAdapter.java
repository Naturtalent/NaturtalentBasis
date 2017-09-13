package it.naturtalent.e4.project;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

public interface IImportAdapter
{
	public String getLabel();
	
	public Image getImage();
	
	public String getContext ();
	
	public String getMessage ();
	
	public Action getImportAction ();
	
}
