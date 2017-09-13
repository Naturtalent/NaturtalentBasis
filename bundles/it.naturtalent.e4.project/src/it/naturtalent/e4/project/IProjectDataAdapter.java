package it.naturtalent.e4.project;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Diese Adapterklasse definiert die Schnittstelle zu Daten die einem Projekt zugeodnet
 * werden sollen und dient der Unterstuetzung im ProjectWizards 
 * 
 * @author apel.dieter
 *
 */
public interface IProjectDataAdapter
{
	public static final String DEFAULTPROJECTADAPTERNAME = "default"; //$NON-NLS-N$
	
	// eindeutige Id des Adapters
	public String getId();
	
	// Bezeichnung
	public String getName();
	
	// die 'ProjectDataClass' muss das Interface 'IProjectData' implementieren
	public Class<?> getProjectDataClass();
	
	public Action getAction(IEclipseContext context);
	
	// erzeugt ein Composite in 'parent' (direkte Bearbeitung in der ProjectWizardPage)
	public Composite createComposite(Composite parent);
	
	// gibt eine eigene WizardPage zur Bearbeitung der Daten zurueck
	public WizardPage getWizardPage();
	
	
	public void delete();

	/**
	 * Ueber diese Funktion koennen die persistenten Daten eines Projekts geladen werden.
	 * 
	 */
	
	
	/**
	 * Daten des Projekts 'Id' laden und zurueckgeben.
	 * 
	 * @param projectIS
	 * @return
	 */
	public Object load(String projectId);

	/**
	 * Die im Adapter gespeicherten Datan (@see setProjectData()) werden persistent gespeichert.
	 * 
	 */
	public void save();
	
	
	public String [] toText(Object projectData);

	/**
	 * In diesem temporaeren Datenspeicher kann der ProjectWizard den momentan
	 * zubearbeitenden Datensatz ablegen.
	 *  
	 * @param data
	 */
	//public void setProjectData (IProjectData data);
	//public IProjectData getProjectData ();

	public void setProjectData (Object data);
	public Object getProjectData ();

	
	
	/**
	 * Rueckgabe der Projektdaten des Projekts mit der ID 'projectID"
	 * 
	 * @param projectID
	 * @return
	 */
	public Object getProjectData (String projectID);
	
	/**
	 * Rueckgabe der Projektdaten des momentan im ResourceNavigator selektierten Projekts
	 * 
	 * @return
	 */
	public Object getSelectedProjectData();
	
	
	public NtProject getProject();
	
	public void setProject(NtProject project);
	
}

