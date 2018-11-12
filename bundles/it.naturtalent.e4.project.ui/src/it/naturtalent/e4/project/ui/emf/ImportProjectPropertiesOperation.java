package it.naturtalent.e4.project.ui.emf;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.ui.datatransfer.RefreshResourcesOperation;

/**
 * Import der Projecteigenschaften im Rahmen eines Progressmonitors 
 * 
 * @see it.naturtalent.e4.project.expimp.actions.ImportAction
 * 
 * DefaultNtProjektEigenschaftadapter
 * @see it.naturtalent.e4.project.ui.emf.NtProjectProperty
 * 
 * @author dieter
 *
 */
public class ImportProjectPropertiesOperation implements IRunnableWithProgress
{

	// IDs der importierten Projekte
	private List<String>importedProjectIDs = new ArrayList<String>();
	private List<INtProjectProperty> projectPropertyAdapters;
	
	private static final String IMPORTPROPERTIES_LABEL = "Import Projekteigenschaften"; 	
	
	public ImportProjectPropertiesOperation(Set<String>importedProjectIDs, List<INtProjectProperty> projectPropertyAdapters)
	{
		super();
		this.importedProjectIDs.addAll(importedProjectIDs);
		this.projectPropertyAdapters = projectPropertyAdapters;
	}
	
	@Override
	public void run(IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException
	{		
		if(!importedProjectIDs.isEmpty() && (projectPropertyAdapters != null))
		{	
			monitor.beginTask(IMPORTPROPERTIES_LABEL, importedProjectIDs.size());
			for(String projectID : importedProjectIDs)
			{
				// projektspezifische Eigenschaften ueber die Adapter laden
				for (INtProjectProperty propertyAdapter : projectPropertyAdapters)
				{
					// die ProjektID wird an den Adapter uebergeben
					propertyAdapter.setNtProjectID(projectID);
					propertyAdapter.importProperty();
				}

				monitor.worked(1);
			}
			monitor.done();	
		}
	}

}
