package it.naturtalent.e4.project.ui.emf;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.ECPProjectManager;
import org.eclipse.emf.ecp.core.util.ECPUtil;
import org.eclipse.emf.ecp.spi.ui.util.ECPHandlerHelper;
import org.eclipse.jface.operation.IRunnableWithProgress;

import it.naturtalent.e4.project.INtProjectProperty;

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
	
	// Liste aller PropertyAdapter
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
				// alle Adapter aller zuimportierenden Projekte bearbeiten  
				for (INtProjectProperty propertyAdapter : projectPropertyAdapters)
				{
					// Eigenschaftsdaten ueber den Adapter importieren
					propertyAdapter.setNtProjectID(projectID);
					propertyAdapter.importProperty();
				}

				monitor.worked(1);
			}
			monitor.done();	
		}
		
		// abschliessend alle ECP-Projecte saven
		ECPProjectManager projectManager = ECPUtil.getECPProjectManager();
		Collection<ECPProject>projects = projectManager.getProjects();
		for(ECPProject project : projects)
			ECPHandlerHelper.saveProject(project);
		
	}

}
