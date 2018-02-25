package it.naturtalent.e4.project;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.IWizardPage;


/**
 * Adapter Interface 
 * 
 * Mit diesem Adapter werden die ProjektProperties angepasst. Die konkreten Daten sind ohnehin 
 * alle vom Typ EObject und werden in einem ECPProject persistent gespeichert. 
 * 
 * @author dieter
 *
 */
public interface INtProjectProperty
{
	public static final String PROJECT_PROPERTY_EVENT = "projectPropertyEvent/"; //$NON-NLS-N$
	public static final String PROJECT_PROPERTY_EVENT_SET_PROPERTY = PROJECT_PROPERTY_EVENT+"setProperty"; //$NON-NLS-N$
	public static final String PROJECT_PROPERTY_EVENT_UNSET_PROPERTY = PROJECT_PROPERTY_EVENT+"unsetProperty"; //$NON-NLS-N$
	
	// Wizardmodus (steuert das Verhalten z.B. beim Speichern)
	public static final int ADD_WIZARD_MODUS = 0;
	public static final int UPDATE_WIZARD_MODUS = 1;
	public static final int DELETE_WIZARD_MODUS = 2;
	
	/**
	 * Setter - Id des Projekts fuer das die Eigenschaft definiert wird.  
	 * 
	 * @param ntProject
	 */
	public void setNtProjectID(String ntProjectID);
	
	
	/**
	 * Getter - Id des Projekts fuer das die Eigenschaft definiert wird.
	 * 
	 * @return
	 */
	public String getNtProjectID();

	/**
	 * Die konkreten Daten (EObject) zurueckgeben
	 *  
	 * @return
	 */
	public Object getNtPropertyData();
	
	/**
	 * Die konkreten Daten setzen
	 *  
	 * @return
	 */
	public void setNtPropertyData(Object eObject);

	/**
	 * Rueckgabe des Containers, indem alle Objecte gespeichert werden
	 *  
	 * @return
	 */
	public Object getPropertyContainer();

	
	/**
	 * Die konkreten Daten der jeweiligen Eigenschaft vom persistenten Speicher laden.
	 * 
	 * @return
	 */
	public Object init();
	
	/**
	 * Die konkreten Daten der jeweiligen Eigenschaft persistent speichern.
	 * 
	 */
	public void commit();
			
	/**
	 * Eine Wizardseite zur Bearbeitung der Eigenschaft erzeugen.
	 * 
	 * @return
	 */
	public IWizardPage createWizardPage();
	
	/**
	 * Aenderungen an den Eigenschaftsdaten rueckgaengig machen.
	 */
	public void undo();
	
	/**
	 * Loeschen der Eigenschaftsdaten.
	 */
	public void delete();
	
	/**
	 * Eigenschaft exportieren
	 */
	//public void exportProperty();

	/**
	 * Eigenschaft importieren
	 * @return 
	 */
	//public boolean importProperty(Object importData);

	
	/**
	 * Eine auf die Projekteigenschaft bezogene Aktion.
	 *  
	 * @return
	 */
	public Action createAction();
	
	//public void setWizardModus(int modus);
	
	/**
	 * Beschriftung
	 * @return
	 */
	public String getLabel();

	
	/**
	 * mit diesem Key informiert der EventBroker ueber Undo-Events
	 * @param undoEventKey
	 */
	//void setUndoEventKey(String undoEventKey);
	
	/**
	 * mit diesem Key informiert der EventBroker ueber Delete-Events
	 * @param deleteEventKey
	 */
	//void setDeleteEventKey(String deleteEventKey);
}
