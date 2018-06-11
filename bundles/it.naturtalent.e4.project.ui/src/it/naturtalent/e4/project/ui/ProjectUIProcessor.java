package it.naturtalent.e4.project.ui;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import it.naturtalent.application.services.IOpenWithEditorAdapter;
import it.naturtalent.application.services.IOpenWithEditorAdapterRepository;
import it.naturtalent.e4.preferences.IPreferenceRegistry;
import it.naturtalent.e4.project.INewActionAdapter;
import it.naturtalent.e4.project.INewActionAdapterRepository;
import it.naturtalent.e4.project.IProjectDataFactory;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;


@Creatable
public class ProjectUIProcessor
{
	
	@Optional @Inject it.naturtalent.application.services.INewActionAdapterRepository newActionAdapterRepository;
	
	@Inject @Optional IProjectDataFactory projectDataFactory;
	@Inject @Optional INewActionAdapterRepository newActionRepository;
	@Inject @Optional IPreferenceRegistry preferenceRegistry;
	@Inject @Optional IOpenWithEditorAdapterRepository openwithAdapterRepository;
		
	//private static EPartService partService;

	public static final String NEW_PROJECT_MENUE_ID = "it.naturtalent.e4.project.ui.handledmenuitem.newProject"; //$NON-NLS-1$
	public static final String NEW_PROJECT_MENUE_LABEL = "Explorer.newProjectLabel"; //$NON-NLS-1$
	public static final String NEW_PROJECT_COMMAND_ID = "it.naturtalent.application.newProject"; //$NON-NLS-1$

	public static final String NEW_FOLDER_MENUE_ID = "it.naturtalent.e4.project.ui.handledmenuitem.newFolder"; //$NON-NLS-1$
	public static final String NEW_FOLDER_MENUE_LABEL = "Explorer.newFolderLabel"; //$NON-NLS-1$
	public static final String NEW_FOLDER_COMMAND_ID = "it.naturtalent.application.newFolder"; //$NON-NLS-1$

	public static final String NEW_FILE_MENUE_ID = "it.naturtalent.e4.project.ui.handledmenuitem.newFile"; //$NON-NLS-1$
	public static final String NEW_FILE_MENUE_LABEL = "Explorer.newFileLabel"; //$NON-NLS-1$
	public static final String NEW_FILE_COMMAND_ID = "it.naturtalent.application.newFile"; //$NON-NLS-1$

	// Dynamic OpenWith
	public static final String OPEN_PROJECT_MENUE_ID = "it.naturtalent.e4.project.ui.handledmenuitem.openProject"; //$NON-NLS-1$
	public static final String OPEN_PROJECT_MENUE_LABEL = "Explorer.openProjectLabel"; //$NON-NLS-1$
	public static final String OPEN_PROJECT_COMMAND_ID = "it.naturtalent.e4.project.ui.command.openProject"; //$NON-NLS-1$
	
	public static final String PROJECT_OPENWITHMENUE_ID = "it.naturtalent.e4.project.menu.open"; //$NON-NLS-1$
	public static final String PROJECT_OPENWITHMENUE_LABEL = "Explorer.explorterLabel"; //$NON-NLS-1$
	public static final String PROJECT_OPENWITHCOMMAND_ID = "it.naturtalent.e4.project.openCommand"; //$NON-NLS-1$

	
	@Execute
	void init (IEclipseContext context, Logger logger, MApplication application)
	{				
		String label;
		//this.partService = partService; 
				
		// New Project Action dem zentralen Repository hinzufuegen
		newActionAdapterRepository.getNewWizardAdapters().add((it.naturtalent.application.services.INewActionAdapter) new NewProjectAdapter());
		
		// New Folder Action dem zentralen Repository hinzufuegen
		newActionAdapterRepository.getNewWizardAdapters().add((it.naturtalent.application.services.INewActionAdapter) new NewFolderAdapter());

		// New File Action dem zentralen Repository hinzufuegen
		newActionAdapterRepository.getNewWizardAdapters().add((it.naturtalent.application.services.INewActionAdapter) new NewFileAdapter());
		
		
		//openwithAdapterRepository.getOpenWithAdapters().add(new SystemOpenWithEditor());
		openwithAdapterRepository.getOpenWithAdapters().add(new TestOpenWithAdapter());
		
		

		DynamicNewMenu newDynamicMenu = new DynamicNewMenu();
		//DynamicOpenWithMenu openWithMenu = new DynamicOpenWithMenu();
		//DynamicNewMenu openWithMenu = new DynamicNewMenu();
		
		// alle definierten Commands auflisten
		List<MCommand>commands = application.getCommands();
		for(MCommand command : commands)
		{
			
			// neues Projekt
			if(StringUtils.equals(command.getElementId(),NEW_PROJECT_COMMAND_ID))
			{						
				label = Activator.properties.getProperty(NEW_PROJECT_MENUE_LABEL);											
				newDynamicMenu.addHandledDynamicItem(NEW_PROJECT_MENUE_ID,label,command, 0);				
				continue;
			}			

			// neues Verzeichnis
			if(StringUtils.equals(command.getElementId(),NEW_FOLDER_COMMAND_ID))
			{						
				label = Activator.properties.getProperty(NEW_FOLDER_MENUE_LABEL);											
				newDynamicMenu.addHandledDynamicItem(NEW_FOLDER_MENUE_ID,label,command, 1);
				continue;
			}

			// neue Datei
			if(StringUtils.equals(command.getElementId(),NEW_FILE_COMMAND_ID))
			{						
				label = Activator.properties.getProperty(NEW_FILE_MENUE_LABEL);											
				newDynamicMenu.addHandledDynamicItem(NEW_FILE_MENUE_ID,label,command, 2);				
				continue;
			}

		/*
			// Dynamic OpenWith
			if(StringUtils.equals(command.getElementId(),PROJECT_OPENWITHCOMMAND_ID))
			{						
				label = Activator.properties.getProperty(PROJECT_OPENWITHMENUE_LABEL);									
				openWithMenu.addHandledDynamicItem(PROJECT_OPENWITHMENUE_ID,label,command,0);		
				continue;
			}
			
			if(StringUtils.equals(command.getElementId(),OPEN_PROJECT_COMMAND_ID))
			{						
				label = Activator.properties.getProperty(OPEN_PROJECT_MENUE_LABEL);								
				openWithMenu.addHandledDynamicItem(OPEN_PROJECT_MENUE_ID,label,command,1);		
				continue;
			}
		*/
			
		}
		
		
		
		Activator.workbenchContext = context;
		//Activator.setLogger(logger);
		
		if(newActionRepository != null)
		{
			newActionRepository.addNewActionAdapter(new NewProjectActionAdapter());
			newActionRepository.addNewActionAdapter(new NewFileActionAdapter());
			newActionRepository.addNewActionAdapter(new NewFolderActionAdapter());
		}
		
		// Zugriff auf die Projektdaten (AdapterRegister)
		Activator.projectDataAdapterRegister = ContextInjectionFactory.make(ProjectDataAdapterRegistry.class, context);		
		Activator.projectDataFactory = projectDataFactory;	
		
		if(preferenceRegistry != null)
			preferenceRegistry.getPreferenceAdapters().add(new ProjectPreferenceAdapter());
	}
	
	/*
	public static MMenu getProjectOpenWithMenu()
	{
		MPart projectNavigatorPart = partService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		
		if(projectNavigatorPart != null)
		{
			List<MMenu>menus = projectNavigatorPart.getMenus();
			for(MMenu menu : menus)
			{					
				if(StringUtils.equals(menu.getElementId(), ResourceNavigator.RESOURCE_NAVIGATOR_POPUPMENUE_ID))
				{
					List<MMenuElement>popupMenus = menu.getChildren();
					for(MMenuElement popupMenu : popupMenus)
					{
						if(StringUtils.equals(popupMenu.getElementId(), ResourceNavigator.RESOURCE_NAVIGATOR_OPENWITHMENUE_ID))
							return(MMenu) popupMenu; 
					}
				}						
			}
		}

		return null;
	}
	*/
}
