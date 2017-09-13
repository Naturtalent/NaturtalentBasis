package it.naturtalent.e4.project.expimp.actions;


import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.e4.project.expimp.ExpImpProcessor;
import it.naturtalent.e4.project.expimp.ExportResources;
import it.naturtalent.e4.project.expimp.dialogs.ProjectExportDialog;
import it.naturtalent.e4.project.expimp.ecp.ECPExportHandlerHelper;

public class ExportAction extends Action
{
	
	//public static final String PROJECT_OOEXPORT_TEMPLATE = "projekte0.ods"; //$NON-NLS-1$
	//public static final String PROJECT_MSEXPORT_TEMPLATE = "projekte0.xlsx"; //$NON-NLS-1$
	
	public static final String IMPEXPORTFILE_NAME = "impexpprojectproperty.xmi"; //$NON-NLS-1$	
		
	//@Inject @Optional IProjectDataFactory projectDataFactory;
	//@Inject @Optional UISynchronize sync;
	
	private INtProjectPropertyFactoryRepository projektDataFactoryRepository;
	private Shell shell;
	
	@Override
	public void run()
	{
		ProjectExportDialog dialog = new ProjectExportDialog(ExpImpProcessor.shell);
		if(dialog.open() == ProjectExportDialog.OK)
		{
			// das Zielverzeichnis abgreifen (hierhin werden die Projekte exportiert)
			File destDir = dialog.getResultDestDir();
			
			// die zuexportierenden Resourcen in einer Liste zusammenfassen
			IResource [] resources = dialog.getResultExportSource();			
			if(ArrayUtils.isEmpty(resources))
				return;
									
			List<IResource>lResources = new ArrayList<IResource>();			
			for(IResource iResource : resources)
				lResources.add(iResource);
			
			// alle zuexportierenden Ressourcen werden exportiert
			Shell shell = Display.getDefault().getActiveShell();
			if (shell != null)
			{
				ExportResources exportResource = new ExportResources(shell);
				exportResource.export(shell, lResources,
						destDir.getPath(), dialog.isArchivState());
			}
			
			// export der ProjektPropertyDaten
			exportProjectProperties(destDir.getPath(), lResources);

			
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
