package it.naturtalent.application.services;

/**
 * Mit diesem Adapter kann ein spezieller Editor mit dem eine Datei geoffnet werden soll verfuegbar gemacht werden.
 * (z.B. kann zum Oeffnen einer .odt Datei ein spezieller TextWizard angeboten werden)
 * Die Auswahlt erfolgt ueber das dynamische Menue 'OpenWith'.
 * Der Apdapter steuert die Konfigurierung des dyn. Menu. Der eigentliche Handler zum Oeffnen des Editors
 * erfolgt ueber die CommanID.
 * 
 * oder
 * 
 * statisches Oeffen einer Datei durch Auswertung der File-Extension
 * Soll dyn. Menue definitiv ausgeschaltet werden, muss 'getIndex()' einen Wert <0 zurueckgeben 
 *  
 * @author dieter
 *
 */
public interface IOpenWithEditorAdapter
{
	public String getCommandID();
	public String getMenuID ();
	public String getMenuLabel ();
	public String getContribURI ();
	public boolean getType ();
	public int getIndex();
	
	public boolean isExecutable(String filePath);
	public void execute(String filePath);
	
}
