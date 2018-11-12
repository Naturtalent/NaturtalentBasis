package it.naturtalent.e4.project.ui.emf;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.ui.datatransfer.RefreshResourcesOperation;

/**
 * Export der Projecteigenschaften im Rahmen eines Progressmonitors 
 * 
 * @see it.naturtalent.e4.project.expimp.actions.ExportAction
 * 
 * @author dieter
 *
 */
public class ExportProjectPropertiesOperation implements IRunnableWithProgress
{

	private List<IResource>toExportResources;
	private List<INtProjectProperty> projectPropertyAdapters;
	
	private int totalWork = IProgressMonitor.UNKNOWN;
	
	private static final String EXPORTPROPERTIES_LABEL = "Export Eigenschaften"; 
	
	
	public ExportProjectPropertiesOperation(List<IResource> toExportResources, List<INtProjectProperty> projectPropertyAdapters)
	{
		super();
		this.toExportResources = toExportResources;
		this.projectPropertyAdapters = projectPropertyAdapters;
	}



	@Override
	public void run(IProgressMonitor monitor)
			throws InvocationTargetException, InterruptedException
	{
		if((toExportResources != null) && (!toExportResources.isEmpty()) && 
				(projectPropertyAdapters != null) && (!projectPropertyAdapters.isEmpty()))
		{
			totalWork = toExportResources.size(); 
			monitor.beginTask(EXPORTPROPERTIES_LABEL,totalWork);
			for(IResource iResource : toExportResources)
			{
				if (iResource.getType() == IResource.PROJECT)
				{
					String projectID = ((IProject) iResource).getName();

					// projektspezifische Properties ueber die Adapter laden
					for (INtProjectProperty propertyAdapter : projectPropertyAdapters)
					{
						// die ProjektID wird an den Adapter uebergeben, kann
						// dieser dann mit der ProjektID
						// Daten laden, werden diese in einer spezifischen
						// Datei im Projekt abgelegt
						// die dann mit allen anderen Resourcen exportiert wird
						propertyAdapter.setNtProjectID(projectID);
						propertyAdapter.exportProperty();
					}											
				}
				
				monitor.worked(1);				
			}
			monitor.done();	
		}
	}

}
