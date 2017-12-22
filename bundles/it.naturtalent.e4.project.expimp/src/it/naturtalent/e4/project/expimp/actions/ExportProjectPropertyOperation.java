package it.naturtalent.e4.project.expimp.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.ProjectPropertyData;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.e4.project.expimp.ecp.ECPExportHandlerHelper;

public class ExportProjectPropertyOperation implements IRunnableWithProgress
{
	private final static String SAVEPROPERTY_OPERATION_TITLE = "Speichern der Projekteigenschaft";
	private int totalWork = IProgressMonitor.UNKNOWN;

	private Shell shell;
	private File exportDestDir;
	private IResource [] exportProjects;
	
	// Zuordnung der NtProjekte zu den PropertyFactories
	private Map<String,List<String>>mapProjectFactories = new HashMap<String, List<String>>();	
	private INtProjectPropertyFactoryRepository projektDataFactoryRepository;
	
	private List<String>noPropertyData = new ArrayList<String>(); 	
	
	/**
	 * @param exportProjects
	 */
	public ExportProjectPropertyOperation(Shell shell, File exportDestDir, IResource[] exportProjects,
			INtProjectPropertyFactoryRepository projektDataFactoryRepository)
	{
		super();
		this.shell = shell;
		this.exportDestDir = exportDestDir;
		this.exportProjects = exportProjects;
		this.projektDataFactoryRepository = projektDataFactoryRepository;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
	{
		if(ArrayUtils.isNotEmpty(exportProjects))
		{			
			totalWork = exportProjects.length; 
			totalWork = totalWork + projektDataFactoryRepository.getAllProjektDataFactories().size();
			monitor.beginTask(SAVEPROPERTY_OPERATION_TITLE,totalWork);
			
			// Im ersten Schritt wird eine Hilsmap erzeugt.
			// Die PropertyDataFiles der NtProjekte werden gelesen und die Zuordnung NtProjekte zu PropertyFactory aufgelistet.
			// 'mapProjectFactories' besitzt fuer jede PropertyFactory (key) eine Liste (value) mit den  NtProjekten 
			// die die jeweilige Eigenschaft besitzen. 						
			ProjectPropertySettings projectPropertySettings = new ProjectPropertySettings();
			for(IResource exportProject : exportProjects)
			{
				if (exportProject instanceof IProject)
				{
					IProject iProject = (IProject) exportProject;
					ProjectPropertyData projectPropertyData = projectPropertySettings.get(iProject);
					
					if (projectPropertyData != null)
					{
						String[] factoryNames = projectPropertyData.getPropertyFactories();
						for (String factoryName : factoryNames)
						{
							List<String> projectIds = mapProjectFactories.get(factoryName);
							if (projectIds == null)
							{
								projectIds = new ArrayList<String>();
								mapProjectFactories.put(factoryName,projectIds);
							}
							projectIds.add(iProject.getName());
						}
					}
					else
					{
						// Project mit fehlenden PropertyDaten registrieren
						noPropertyData.add(iProject.getName());
					}
				}
				monitor.worked(1);
			}
			
			// im zweiten Schritt werden die Properties exportiert)				
			Set<String>propertyFatoryNames = mapProjectFactories.keySet();
			for(String propertyFatoryName : propertyFatoryNames)
			{
				exportProjectProperty(propertyFatoryName);
				monitor.worked(1);
			}
			
			monitor.done();
		}
	}
	
	/*
	 * Die jeweiligen PropjectProperties werden in ECPProjectFiles (.xmi) gespeichert.
	 * Der Name dieses Files wird vom jeweiligen ContainerObject (ECPProject) abgeleitet.
	 */
	public void exportProjectProperty(String propertyFactoryName)
	{
		// Containername (ECPProject) des PropertyObjects
		String ecpProjectName = null;
		
		// der Propertyadapter
		INtProjectProperty ntProjectProperty = null;
		
		// Name des Containers und den Adapter mit ProjectPropertyFactory ermitteln
		List<INtProjectPropertyFactory>projectFactories = projektDataFactoryRepository.getAllProjektDataFactories();
		for(INtProjectPropertyFactory projectFactory : projectFactories)
		{
			if(StringUtils.equals(projectFactory.getClass().getName(),propertyFactoryName))
			{
				ecpProjectName = projectFactory.getParentContainerName();
				ntProjectProperty = projectFactory.createNtProjektData();
				break;				
			}
		}
		
		final List<EObject>exportListe = new ArrayList<EObject>();
		if(ecpProjectName != null)
		{
			// alle Propertydaten laden und in einer Liste 'exportListe' sammeln 
			List<String>projectIDs = mapProjectFactories.get(propertyFactoryName);
			for(String projectID : projectIDs)
			{
				// ueber den Adapter laden 
				ntProjectProperty.setNtProjectID(projectID);
				Object obj = ntProjectProperty.getNtPropertyData();
				if (obj instanceof EObject)
				{
					EObject eObject = (EObject) obj;
					exportListe.add(EcoreUtil.copy(eObject));
				}				
			}
			
			// Dateiname des Properties generieren und daten exportieren
			final File exportFile = new File(exportDestDir,ecpProjectName+".xmi");

			shell.getDisplay().syncExec(new Runnable()
			{
				public void run()
				{
					ECPExportHandlerHelper.export(shell, exportListe, exportFile.getPath());
				}
			});
		}
	}

	
	// Ruekgabe der Fehlerliste
	public List<String> getNoPropertyData()
	{
		return noPropertyData;
	}

	/*
	public Map<String, List<String>> getMapProjectFactories()
	{
		return mapProjectFactories;
	}
	*/
	

}
