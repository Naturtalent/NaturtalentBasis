package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.datatransfer.RenameResources;

import java.text.MessageFormat;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;



public class RenameResourceHandler extends SelectedResourcesUtils
{
	
	private IResourceNavigator navigator;
	
	private String extension = null;
	
	@Execute
	public void execute(Shell shell, MPart part)
	{		
		IResource resource = getSelectedResource(part);
		if (resource != null)
		{
			if (checkReadOnlyAndNull(shell, resource))
			{
				String newName = queryNewResourceName(shell, resource);
				if (StringUtils.isNotEmpty(newName))
				{
					RenameResources renameResources = new RenameResources();
					renameResources.rename(shell, resource, newName);
				}
			}
		}
	}

	@CanExecute
	public boolean canExecute(MPart part)
	{	
		IProject iProject = getSelectedProject(part);
		if(iProject != null && !iProject.isOpen())
			return false;

		IResource iResource = getSelectedResource(part);		
		return (iResource != null) && (iResource.getType() != IResource.PROJECT);
	}
	
	/**
	 * Check if the supplied resource is read only or null. If it is then ask
	 * the user if they want to continue. Return true if the resource is not
	 * read only or if the user has given permission.
	 * 
	 * @return boolean
	 */
	private boolean checkReadOnlyAndNull(Shell shell, IResource currentResource)
	{
		// Do a quick read only and null check
		if (currentResource == null)
		{
			return false;
		}

		// Do a quick read only check
		final ResourceAttributes attributes = currentResource
				.getResourceAttributes();
		if (attributes != null && attributes.isReadOnly())
		{
			return MessageDialog.openQuestion(shell, Messages.RenameResourceAction_checkTitle,
					MessageFormat.format(Messages.RenameResourceAction_readOnlyCheck, new Object[]
						{ currentResource.getName() }));
		}

		return true;
	}
	
	/**
	 * Return the new name to be given to the target resource.
	 * 
	 * @return java.lang.String
	 * @param resource
	 *            the resource to query status on
	 */
	protected String queryNewResourceName(Shell shell, final IResource resource)
	{
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IPath prefix = resource.getFullPath().removeLastSegments(1);
		IInputValidator validator = new IInputValidator()
		{
			public String isValid(String string)
			{
				if (resource.getName().equals(string))
				{
					return Messages.RenameResourceAction_nameMustBeDifferent;
				}
				IStatus status = workspace.validateName(string,
						resource.getType());
				if (!status.isOK())
				{
					return status.getMessage();
				}
				if (workspace.getRoot().exists(prefix.append(string)))
				{
					return Messages.RenameResourceAction_nameExists;
				}
				return null;
			}
		};

		// original Dateisuffix sichern 
		if(resource.getType() == IResource.FILE)
		{
			IPath iPath = resource.getFullPath();
			this.extension = iPath.getFileExtension(); 
		}
		
		InputDialog dialog = new InputDialog(shell,
				Messages.RenameResourceAction_inputDialogTitle,
				Messages.RenameResourceAction_inputDialogMessage,
				resource.getName(), validator);
		dialog.setBlockOnOpen(true);
		int result = dialog.open();
		if (result == Window.OK)
		{
			String newName = dialog.getValue();
			if(resource.getType() == IResource.FILE)
			{
				// original Dateisuffix rekonstruieren
				newName = FilenameUtils.removeExtension(newName);
				newName = newName + "." + extension;
			}
			return newName;
		}
		return null;
	}


}
