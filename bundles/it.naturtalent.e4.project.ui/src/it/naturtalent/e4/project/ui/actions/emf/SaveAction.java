 
package it.naturtalent.e4.project.ui.actions.emf;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.parts.emf.NtProjectView;

public class SaveAction
{
	//private IProject selectedIProject; 
	
	@Execute
	public void execute(EPartService partService, EModelService modelService, MPart part)
	{
		Activator.getECPProject().saveContents();
		
		// ToolbarStatus triggern  
		List<MToolItem> items = modelService.findElements(part, NtProjectView.SAVE_TOOLBAR_ID, MToolItem.class,null, EModelService.IN_PART);
		MToolItem item = items.get(0);
		item.setEnabled(false);
		
		items = modelService.findElements(part, NtProjectView.UNDO_TOOLBAR_ID, MToolItem.class,null, EModelService.IN_PART);
		item = items.get(0);
		item.setEnabled(false);					
		
		// Name des IProjects und Name des NtProjects synchronisieren
		Object obj = part.getObject();
		if (obj instanceof NtProjectView)
		{
			NtProjectView ntProjectView = (NtProjectView) obj;
			NtProject editedProject = ntProjectView.getEditedNtProject();
			if (editedProject != null)
			{
				try
				{
					// Name des IProjects ermitteln
					IProject iProject = ResourcesPlugin.getWorkspace().getRoot()
								.getProject(editedProject.getId());					
					String name = iProject.getPersistentProperty(INtProject.projectNameQualifiedName);
						
					// Name IProject mit dem Name des NtProjects vergleichen
					if(!StringUtils.equals(name, editedProject.getName()))
					{
						// IProjectname aktualisieren und im ResourceNavigator aktualisieren
						iProject.setPersistentProperty(INtProject.projectNameQualifiedName,
								editedProject.getName());
						MApplication currentApplication = E4Workbench
								.getServiceContext().get(IWorkbench.class).getApplication();
						IEventBroker eventBroker = currentApplication.getContext().get(IEventBroker.class);
						eventBroker.post(IResourceNavigator.NAVIGATOR_EVENT_UPDATE_REQUEST,iProject);
					}
				} catch (CoreException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@CanExecute
	public boolean canExecute()
	{				
		return Activator.getECPProject().hasDirtyContents();
	}
	
}