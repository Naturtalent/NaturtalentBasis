package it.naturtalent.application.services;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;


/**
 * Dieser Adapter definiert die Klasse die zur Erzeugung eines neuen Objects ausgefuehrt werden muss. 
 * 
 * @author dieter
 *
 */

public interface INewActionAdapter
{	
	// Name des adaptierten Objects
	public String getLabel();
	
	public Image getImage();

	// die 'NewAction' kann in eine Kategorie eingeordnet werden 
	public String getCategory ();
	
	public String getMessage ();
	
	// diese Klasse erzeugt ein neues Object
	public Class<? extends Action> getActionClass();
	
}
