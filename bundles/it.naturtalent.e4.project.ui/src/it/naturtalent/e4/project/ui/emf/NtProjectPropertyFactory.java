package it.naturtalent.e4.project.ui.emf;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;

/**
 * Factory zur Erzeugung der ProjectProperty-Klasse. 
 * Die Klassennamen der dem jeweiligen NtProjekt zugeordneten Factories werden in einer Datei im Datenbereich
 * des Projekts gespeichert.
 * Die Besonderheit dieser Factory-Klasse ist, dass sie obligatorisch jedem NtProjekt zugeordnet ist.
 *     
 * @author dieter
 *
 */
public class NtProjectPropertyFactory implements INtProjectPropertyFactory
{
	public final static String NTPROJECTPROPERTYLABEL = "NtProjectProperties";
	
	/**
	 * Konstrukor
	 */
	public NtProjectPropertyFactory()
	{
		super();		
	}

	@Override
	public INtProjectProperty createNtProjektData()
	{	
		// die ProjectProperty-Klasse wird vie ContextInjectionFactory erzeugt 
		final MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
		IEclipseContext context = currentApplication.getContext();		
		return (context != null) ? ContextInjectionFactory.make(NtProjectProperty.class, context) : null;
	}

	@Override
	public String getLabel()
	{		
		return NTPROJECTPROPERTYLABEL;
	}


}
