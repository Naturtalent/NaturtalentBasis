package it.naturtalent.e4.project.ui.handlers;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.datatransfer.MailTransfer;
import it.naturtalent.e4.project.ui.datatransfer.PasteResourceAction;
import it.naturtalent.e4.project.ui.datatransfer.RenameResources;
import it.naturtalent.e4.project.ui.dialogs.ContainerSelectionDialog;
import it.naturtalent.e4.project.ui.dialogs.NewFolderDialog;
import it.naturtalent.e4.project.ui.utils.CopyResourcesAction;
import it.naturtalent.e4.project.ui.utils.RefreshResource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.internal.decorators.DecoratorManager;



public class RefreshHandler extends SelectedResourcesUtils
{
	
	private IResourceNavigator navigator;
	
	@Execute
	public void execute(Shell shell, MPart part)
	{		
		IProject project = getSelectedProject(part);		
		if(project != null)
		{
			RefreshResource refreshResource = new RefreshResource();
			refreshResource.refresh(shell, project);
		}
	}

	@CanExecute
	public boolean canExecute(Shell shell, MPart part)
	{	
		IProject iProject = getSelectedProject(part);
		if(iProject != null && !iProject.isOpen())
			return false;

		return true;
		
		//return (getSelectedProject(part) != null);		
	}
	
	
	
}
