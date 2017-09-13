package it.naturtalent.e4.project.ui.actions.emf;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

public class SyncNavigatorAction extends Action
{

	// die im Navigator selektierte Resource
	private IProject iProject;
	
	public SyncNavigatorAction()
	{
		setImageDescriptor(Icon.ICON_SYNC.getImageDescriptor(IconSize._16x16_DefaultIconSize));
		setEnabled(false);
	}

	@Override
	public void run()
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

	/**
	 * Die im Navigator selektierte Resource wird gemeldet.
	 *  
	 * @param selectedResource
	 */
	@Inject
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION)@Optional IResource selectedResource)
	{
		if(selectedResource != null)
		{
			iProject = selectedResource.getProject();		
			setEnabled(true);
		}
	}
}
