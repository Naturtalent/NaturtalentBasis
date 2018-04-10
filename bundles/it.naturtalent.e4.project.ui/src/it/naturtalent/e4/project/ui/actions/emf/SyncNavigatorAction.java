package it.naturtalent.e4.project.ui.actions.emf;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.parts.emf.NtProjectView;

public class SyncNavigatorAction
{

	@Inject @Optional private IEventBroker eventBroker;	
	@Inject @Optional public EPartService partService;
	@Inject @Optional public EModelService modelService;

	// die im Navigator selektierte Resource
	private IProject iProject;
	
	@Execute
	public void execute()
	{
		if(iProject != null)
		{
			ResourceNavigator resourceNavigator = (ResourceNavigator) Activator.findNavigator();
			
			TreeViewer treeViewer = Activator.findNavigator().getViewer();
			if (resourceNavigator.getTopLevelStatus())
			{
				IWorkingSet[] workingSets = resourceNavigator.getWindowWorkingSets();
				for (IWorkingSet workingSet : workingSets)
				{
					IAdaptable[] elements = workingSet.getElements();
					for (IAdaptable element : elements)
					{
						if (element.equals(iProject))
						{
							treeViewer.expandToLevel(workingSet, 1);
							break;
						}
					}
				}
			}

			treeViewer.expandToLevel(iProject, 1);
			treeViewer.setSelection(new StructuredSelection(iProject), true);	
		}
	}
	
	/*
	 * 'canExecute' - kann nicht genutzt werden, da diese Funktion zeitlich vor 'handleSelection'
	 * aufgerufen wird 
	 */
	/*
	@CanExecute
	public boolean canExecute()
	{		
		return iProject != null;	
	}
	*/
		
	
	@Inject
	public void handleSelection(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IResource iResource)
	{		
		if(iResource instanceof IResource)
		{
			// Toolbarstatus Sync updaten		
			MPart mPart = partService.findPart(NtProjectView.NTPROJECT_VIEW_ID);

			// sync - Toolbar
			List<MToolItem> items = modelService.findElements(mPart,
					NtProjectView.SYNC_TOOLBAR_ID, MToolItem.class, null,
					EModelService.IN_PART);
			MToolItem item = items.get(0);
			item.setEnabled(false);
			
			
			iProject = ((IResource) iResource).getProject();
			item.setEnabled(true);
		}		
	}
	
}
