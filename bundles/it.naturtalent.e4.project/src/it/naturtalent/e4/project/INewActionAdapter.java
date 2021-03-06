package it.naturtalent.e4.project;



import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;


/**
 * 
 * @author dieter
 *
 */

public interface INewActionAdapter
{
	public String getLabel();
	
	public Image getImage();
	
	public String getCategory ();
	
	public String getMessage ();
	
	public Class<? extends Action> getActionClass();
	
	public Action getAction();
	
	public void setAction(Action newAction);

}
