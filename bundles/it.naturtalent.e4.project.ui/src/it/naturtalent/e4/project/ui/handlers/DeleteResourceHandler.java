package it.naturtalent.e4.project.ui.handlers;

import java.util.List;

import javax.inject.Inject;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.dialogs.NewFolderDialog;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.utils.DeleteResources;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.internal.decorators.DecoratorManager;



/**
 * Ressourcen loeschen
 * 
 * @author apel.dieter
 *
 */
public class DeleteResourceHandler extends SelectedResourcesUtils
{
	private IResourceNavigator resourceNavigator;
	
	
	@Optional @Inject private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	
	@Execute
	public void execute(Shell shell, MPart part)
	{
		IResource [] selectedResources = null;
		
		Object obj = part.getObject();
		if (obj instanceof IResourceNavigator)
		{
			resourceNavigator = (IResourceNavigator) obj;
			IStructuredSelection selection = (IStructuredSelection) resourceNavigator
					.getViewer().getSelection();
						
			List <IResource> resources = selection.toList();
			selectedResources = resources.toArray(new IResource [resources.size()]);

			MessageDialog dialog = new MessageDialog(
					shell,
					Messages.DeleteResourceHandler_delete,
					null,
					Messages.DeleteResourceHandler_resourcesdelete,
					MessageDialog.NONE,
					new String[]
						{ IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL },
					0);
			if(dialog.open() == MessageDialog.OK)
			{								
				DeleteResources.deleteResources(shell, selectedResources, ntProjektDataFactoryRepository);				
			}
		}
	}
	
	@CanExecute
	public boolean canExecute(MPart part)
	{		
		IResource[] selectedResources = getSelectedResources(part);		
		if (ArrayUtils.isNotEmpty(selectedResources))
		{
			boolean projSelected = selectionIsOfType(IResource.PROJECT);			
			if(projSelected)
			{
				for(IResource iResource : selectedResources)
				{
					if(iResource instanceof IProject)
					{
						if(!(((IProject)iResource).isOpen()))
							return false;
					}
				}
			}
			
			boolean fileFoldersSelected = selectionIsOfType(IResource.FILE
					| IResource.FOLDER);
			if (!projSelected && !fileFoldersSelected)
				return false;
			
			return true;
		}

		return false;
	}
	
	
}
