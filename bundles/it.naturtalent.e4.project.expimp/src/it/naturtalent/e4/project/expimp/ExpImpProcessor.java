package it.naturtalent.e4.project.expimp;

import it.naturtalent.e4.project.IExportAdapterRepository;
import it.naturtalent.e4.project.IImportAdapterRepository;
import it.naturtalent.e4.project.IProjectDataFactory;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

@Creatable
public class ExpImpProcessor
{
	
	@Inject @Optional IProjectDataFactory projectDataFactory;
	@Inject @Optional public static Shell shell;
	
	public static @Inject @Optional IImportAdapterRepository importAdapterRepository;
	public static @Inject @Optional IExportAdapterRepository exportAdapterRepository;
	public static @Inject @Optional EPartService partService;
	
	public static @Inject @Optional MPart part;
	
	@Execute
	//void init (Logger logger)
	void init ()
	{
		//Activator.logger = logger;
		//Activator.projectDataFactory = projectDataFactory;	
		//Activator.projectDataAdapterRegister = projectDataAdapterRegister;
		
		// Projekt Import/Export Adapter registrieren 
		importAdapterRepository.addImportAdapter(new ProjectImportAdapter());
		importAdapterRepository.addImportAdapter(new ProjectRestoreAdapter());
		exportAdapterRepository.addExportAdapter(new ProjectExportAdapter());
		exportAdapterRepository.addExportAdapter(new ProjectBackupAdapter());
	}
}
