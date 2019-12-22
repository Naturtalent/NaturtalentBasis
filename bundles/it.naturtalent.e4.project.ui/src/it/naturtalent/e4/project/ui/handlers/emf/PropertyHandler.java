 
package it.naturtalent.e4.project.ui.handlers.emf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.e4.project.ui.dialogs.CommitProjectPropertiesDialog;
import it.naturtalent.e4.project.ui.dialogs.PropertyFolderDialog;
import it.naturtalent.e4.project.ui.dialogs.PropertyProjectDialog;
import it.naturtalent.e4.project.ui.dialogs.ResourcePropertyDialog;
import it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory;
import it.naturtalent.e4.project.ui.handlers.SelectedResourcesUtils;
import it.naturtalent.e4.project.ui.wizards.emf.ProjectPropertyWizard;

public class PropertyHandler extends SelectedResourcesUtils
{
	
	@Optional @Inject private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	@Execute
	public void execute(MPart part, Shell shell, IEclipseContext context)
	{	
		IResource resource = getSelectedResource(part);
		
		ResourcePropertyDialog proertyDialog = new ResourcePropertyDialog(shell);
		proertyDialog.create();
		proertyDialog.setResource(resource);
		proertyDialog.open();
	}

	@CanExecute
	public boolean canExecute(MPart part)
	{
		return resourceIsType(getSelectedResource(part), IResource.PROJECT
				| IResource.FOLDER | IResource.FILE);
	}
	
		
}