package it.naturtalent.e4.project.expimp.actions;


import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.ECPProjectManager;
import org.eclipse.emf.ecp.core.util.ECPUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.DialogMessageArea;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
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
	
	
	@Override
	public void run()
	{
		final ProjectExportDialog projectExportDialog = new ProjectExportDialog(ExpImpProcessor.shell);
		
		// BusyIndicator - das Einlesen der Resourcen kann dauern
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
			File destDir = projectExportDialog.getResultDestDir();			
									
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
			
			// alle zuexportierenden Ressourcen werden exportiert
			Shell shell = Display.getDefault().getActiveShell();
			if (shell != null)
			{
				ExportResources exportResource = new ExportResources(shell);
				exportResource.export(shell, iResources,
						destDir.getPath(), projectExportDialog.isArchivState());
			}
			
			// PropertyDaten der NtProjekte auslesen und in 'mapProjectFactories' speichern
			List<String>noPropertyData = new ArrayList<String>(); 
			Map<String,List<String>>mapProjectFactories = new HashMap<String, List<String>>();			
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
				log.info("Projekte ohne Properties:");
				for(String projectID : noPropertyData)
					log.info(projectID);
				MessageDialog.openInformation(shell,"Projekteigenschaften","fehlerhafte Projekte (s. Logdatei");
			}
			
			Set<String>propertyFatoryNames = mapProjectFactories.keySet();
			for(String propertyFatoryName : propertyFatoryNames)
			{
				ECPProject ecpProject = ECPUtil.getECPProjectManager().getProject("ECPProject");
				System.out.println(ecpProject);
			}
				
			
			System.out.println(mapProjectFactories);
			
			
			
		}		
	}
	
	@PostConstruct
	private void postConstruct(@Optional INtProjectPropertyFactoryRepository projektDataFactoryRepository,
			@Named(IServiceConstants.ACTIVE_SHELL)@Optional Shell shell)
	{
		this.projektDataFactoryRepository = projektDataFactoryRepository;
		this.shell = shell;
	}
	
	/*
	 * Im Nachgang der Copyfunktion werden nochmals alle Projekte durchlaufen und die jeweiigenProjectPropertyDateien werden 
	 *  
	 * Die Daten der Projekteigenschaften werden in das Zielverzeichnis und dort in den jeweiligen Projektdatabereich
	 * des Projekts exportiert.
	 * Die Daten werden dort in der Datei 'IMPEXPORTFILE_NAME' gespeichert.
	 */
	private void exportProjectProperties(String destDir, List<IResource>lResources)
	{		
		for(IResource iResource : lResources)
		{			
			if (iResource.getType() == IResource.PROJECT)
			{				
				IProject iProject = iResource.getProject();
				
				// List sammelt alle dem Projekt zugeordneten PropertyDaten 
				final List<EObject> projectPropertyData = new LinkedList<EObject>();
				
				// die ProjectProperties auflisten
				List<INtProjectProperty> projectProperties = NtProjektPropertyUtils
						.getProjectProperties(projektDataFactoryRepository,
								iProject);

				// die konkreten Daten der jeweiligen Eigenschaft auflisten
				if (projectProperties != null)
				{
					for (INtProjectProperty projectProperty : projectProperties)
					{
						Object obj = projectProperty.getNtPropertyData();
						if (obj instanceof EObject)
							projectPropertyData.add(EcoreUtil.copy((EObject) obj));
					}

				}
				
				String dataPath = destDir + File.separator + iProject.getName()  + File.separator + IProjectData.PROJECTDATA_FOLDER;
				String filePath = dataPath + File.separator + IMPEXPORTFILE_NAME;
				ECPExportHandlerHelper.export(shell, projectPropertyData, filePath);

				// im Projektdatabereich des jeweiligen Projekts speichern
				//String filePath = ProjectPropertySettings.getProjectDataPath(
					//	iProject) + File.separator + IMPEXPORTFILE_NAME;
				
				//System.out.println("filePath");
				
				//ECPExportHandlerHelper.export(shell, projectPropertyData, filePath);

			}
		}
	}
}
