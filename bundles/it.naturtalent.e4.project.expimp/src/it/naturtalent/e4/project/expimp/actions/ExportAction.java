package it.naturtalent.e4.project.expimp.actions;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.ProjectPropertyData;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.e4.project.expimp.ExpImpProcessor;
import it.naturtalent.e4.project.expimp.ExportResources;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.expimp.dialogs.ProjectExportDialog;
import it.naturtalent.e4.project.expimp.ecp.ECPExportHandlerHelper;
import it.naturtalent.e4.project.ui.datatransfer.RefreshResourcesOperation;

/**
 * Mit dieser Klasse wird der Export von Projekten in ein auszuwaehlendes Verzeichnis ausgefuehrt.
 * Kopiert werden alle Resourcen des ausgewaehleten Projekte.
 * Vor Ausfuehrung muessen (sollten) alle Dateien des Projekts geschlossen sein.
 * (Moeglicherweise sollte vor dem Kopieren noch ein Refresh erfolgen)
 * 
 *  Alle den Projekten zugeordnete PropertyAdapter werden angesprochen und die jeweiligen 'Export'-Funktionen aufgerufen.
 * 
 * @author dieter
 *
 */
public class ExportAction extends Action
{
	
	//public static final String PROJECT_OOEXPORT_TEMPLATE = "projekte0.ods"; //$NON-NLS-1$
	//public static final String PROJECT_MSEXPORT_TEMPLATE = "projekte0.xlsx"; //$NON-NLS-1$
		
	public static final String IMPEXPORTFILE_NAME = "impexpprojectproperty.xmi"; //$NON-NLS-1$	
		
	//@Inject @Optional IProjectDataFactory projectDataFactory;
	//@Inject @Optional UISynchronize sync;
	
	private INtProjectPropertyFactoryRepository projektDataFactoryRepository;
	private Log log = LogFactory.getLog(this.getClass());
	private Shell shell;
	
	private File exportDestDir;
	
	private Map<String,List<String>>mapProjectFactories = new HashMap<String, List<String>>();	
	
	@PostConstruct
	private void postConstruct(@Optional INtProjectPropertyFactoryRepository projektDataFactoryRepository,
			@Named(IServiceConstants.ACTIVE_SHELL)@Optional Shell shell)
	{
		this.projektDataFactoryRepository = projektDataFactoryRepository;
		this.shell = shell;
	}
	
	@Override
	public void run()
	{
		final ProjectExportDialog projectExportDialog = new ProjectExportDialog(ExpImpProcessor.shell);
		
		// BusyIndicator - das Einlesen der vorhandenen NtProjecte kann dauern	
		BusyIndicator.showWhile(shell.getDisplay(), new Runnable()
		{
			@Override
			public void run()
			{
				projectExportDialog.create();
			}
		});
		
		// Exportmodalitaeten im Dialog festlegen
		if(projectExportDialog.open() == ProjectExportDialog.OK)
		{
			// die zuexportierenden Resourcen in einer Liste zusammenfassen
			IResource [] resources = projectExportDialog.getResultExportSource();			
			if(ArrayUtils.isEmpty(resources))
				return;
			
			// das ausgewaelte Zielverzeichnis (hierhin werden die Projekte exportiert)
			exportDestDir = projectExportDialog.getResultDestDir();			
									
			// die Resourcen in eine Liste ueberfuehren
			List<IResource> iResources = Arrays.asList(resources);
			
		
			// Refresh fuer alle Resourcen
			RefreshResourcesOperation refreshOperation = new RefreshResourcesOperation(iResources);
			try
			{
				new ProgressMonitorDialog(shell).run(true, false, refreshOperation);
			} catch (InvocationTargetException e)
			{
				// Error
				Throwable realException = e.getTargetException();
				MessageDialog.openError(shell, Messages.ExportResources_Error, realException.getMessage());
			} catch (InterruptedException e)
			{
				// Abbruch
				MessageDialog.openError(shell, Messages.ExportResources_Cancel, e.getMessage());
			}
		
			
			// alle zuexportierenden Ressourcen (NtProjekte) werden exportiert		
			if (shell != null)
			{
				ExportResources exportResource = new ExportResources(shell);
				exportResource.export(shell, iResources,
						exportDestDir.getPath(), projectExportDialog.isArchivState());
			}
			
			// PropertyDaten der NtProjekte auslesen und in 'mapProjectFactories' speichern
			// im Map sind fuer jede PropertyFactory die Projekte aufgelistet die diese Eigenschaft besitzen
			List<String>noPropertyData = new ArrayList<String>(); 					
			ProjectPropertySettings projectPropertySettings = new ProjectPropertySettings();
			for(IResource iResource : iResources)
			{
				if (iResource instanceof IProject)
				{
					IProject iProject = (IProject) iResource;
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
			}
			
			if(!noPropertyData.isEmpty())
			{
				// es gibt Projekte ohne PropertyData-File
				log.info("Projekte ohne Properties:");
				for(String projectID : noPropertyData)
					log.info(projectID);
				MessageDialog.openInformation(shell,"Projekteigenschaften","fehlerhafte Projekte (s. Logdatei");
			}
			
			BusyIndicator.showWhile(shell.getDisplay(), new Runnable()
			{
				@Override
				public void run()
				{
					// alle PropjectProperties in separaten Dateien speichern		
					Set<String>propertyFatoryNames = mapProjectFactories.keySet();
					for(String propertyFatoryName : propertyFatoryNames)
						exportProjectProperty(propertyFatoryName);
				}
			});

			MessageDialog.openInformation(null, "Export", "exportiert in das Verzeichnis: " + exportDestDir); //$NON-NLS-1$ //$NON-NLS-2$			
		}
			
	}
	
	/*
	 * Die jeweiligen PropjectProperties sind im ECPProject unter der NtProjektID gespeichert.
	 */
	public void exportProjectProperty(String factoryName)
	{
		// Name des ECPProjects
		String ecpProjectName = null;
		
		INtProjectProperty ntProjectProperty = null;
		
		// mit Hilfe des PropertyFactory wird der NtProjectProperty-Adapter instanziiert
		List<INtProjectPropertyFactory>projectFactories = projektDataFactoryRepository.getAllProjektDataFactories();
		for(INtProjectPropertyFactory projectFactory : projectFactories)
		{
			if(StringUtils.equals(projectFactory.getClass().getName(),factoryName))
			{
				ecpProjectName = projectFactory.getParentContainerName();
				ntProjectProperty = projectFactory.createNtProjektData();
				break;				
			}
		}
		
		List<EObject>exportListe = new ArrayList<EObject>();
		if(ecpProjectName != null)
		{
			// alle Propertydaten laden und in einer Liste sammeln 
			List<String>projectIDs = mapProjectFactories.get(factoryName);
			for(String projectID : projectIDs)
			{
				// ueber den Adapter laden 
				ntProjectProperty.setNtProjectID(projectID);
				Object obj = ntProjectProperty.getNtPropertyData();
				if (obj instanceof EObject)
				{
					EObject eObject = (EObject) obj;
					exportListe.add(eObject);
				}				
			}
			
			// Dateiname des Properties generieren und daten exportieren
			File exportFile = new File(exportDestDir,ecpProjectName+".xmi");
			ECPExportHandlerHelper.export(shell, exportListe, exportFile.getPath());			
		}
	}
	
	
	
}
