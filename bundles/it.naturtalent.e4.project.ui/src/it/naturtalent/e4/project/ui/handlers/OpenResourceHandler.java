package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.datatransfer.TarLeveledStructureProvider;
import it.naturtalent.e4.project.ui.model.IProjectActionFilter;
import it.naturtalent.e4.project.ui.wizards.ProjectWizard;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;

@Deprecated
public class OpenResourceHandler extends SelectedResourcesUtils
{

	@Inject @Optional public IEclipseContext context;
	
	private Log log = LogFactory.getLog(OpenResourceHandler.class);
	
	@Execute
	public void execute(Shell shell, MPart part)
	{
		IResource selectedResource = getSelectedResource(part);
		if(selectedResource != null)
		{
			switch (selectedResource.getType())
				{
					case IResource.PROJECT:

						// die Adapter des momentan selektierten Projekts ermitteln
						IProjectDataAdapter[] inUseAdapters = getProjectDataAdapters(part);
						
						
						//ProjectWizard wizard = new ProjectWizard((IResourceNavigator) part.getObject());
						
						ProjectWizard wizard = ContextInjectionFactory.make(ProjectWizard.class, context);
						//ContextInjectionFactory.invoke(wizard, Persist.class, context);
						
						IResourceNavigator navigator = (IResourceNavigator) part.getObject();
						wizard.setNavigator(navigator);
						
						
						wizard.setAdapters(inUseAdapters);
						WizardDialog dialog = new WizardDialog(shell, wizard);
						dialog.open();
						break;

					case IResource.FOLDER:

						// Verzeichnis oeffnen/schliessen
						IResourceNavigator resourceNavigator = (IResourceNavigator) part
								.getObject();
						TreePath tp = ((TreeSelection) getSelection(part))
								.getPaths()[0];
						TreeViewer treeViewer = resourceNavigator.getViewer();
						if (treeViewer.getExpandedState(tp))
							treeViewer.collapseToLevel(tp, 1);
						else
							treeViewer.expandToLevel(tp, 1);
						break;

					case IResource.FILE:

						IFile ifile = (IFile) selectedResource;
						String fileName = ifile.getName();
						String ext = FilenameUtils.getExtension(fileName);
						Program prog = Program.findProgram(ext);
						if (prog != null)
						{
							try
							{
								File file = FileUtils.toFile(ifile
										.getLocationURI().toURL());
								prog.execute(file.getPath());
							} catch (MalformedURLException e)
							{
								log.error(e);
							}
						}
						else
						{
							MessageDialog
									.openError(shell, Messages.OpenError,
											Messages.bind(
													Messages.OpenFilesError,
													ext));
						}

						break;

					default:
						break;
				}
		}		

	}
	
	@CanExecute
	public boolean canExecute(MPart part)
	{
		IProject iProject = getSelectedProject(part);
		if(iProject != null && !iProject.isOpen())
			return false;
		
		IResource selectedResource = getSelectedResource(part);
		if(selectedResource != null)
		{
			if((selectedResource.getType() & (IResource.PROJECT | IResource.FOLDER | IResource.FILE)) != 0)
				return true;
		}
		
		return false;
	}
}
