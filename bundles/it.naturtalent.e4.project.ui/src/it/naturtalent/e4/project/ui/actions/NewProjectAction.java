package it.naturtalent.e4.project.ui.actions;

import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.ui.dialogs.SelectProjectDataAdapter;
import it.naturtalent.e4.project.ui.handlers.SelectedResourcesUtils;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.wizards.NewProjectWizard;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

@Deprecated

// @see it.naturtalent.e4.project.ui.actions.emf.NewProjectAction

public class NewProjectAction extends Action
{

	@Inject @Optional private Shell shell;
	@Inject @Optional private IEclipseContext context;
	@Inject @Optional private MPart part;
	
	@Inject @Optional private ESelectionService selectionService;
	
	@Inject @Optional private EPartService partService;
	
	private ResourceNavigator resourceNavigator;
	
	
	@Override
	public void run()
	{
		IProjectDataAdapter[] inUseProjectDataAdapters = null;
		if (selectionService != null)
		{
			Object obj = selectionService.getSelection();
			if (obj instanceof IProject)
			{
				SelectedResourcesUtils selectionUtils = new SelectedResourcesUtils();
				// die Adapter des momentan selektierten Projekts ermitteln
				inUseProjectDataAdapters = selectionUtils
						.getProjectDataAdapters((IProject) obj);
			}
		}

		// Adapterauswahl fuer das neue Projekt anbieten
		SelectProjectDataAdapter projectDataDialog = new SelectProjectDataAdapter(
				shell);
		projectDataDialog.create();
		if(inUseProjectDataAdapters != null)
			projectDataDialog.setSelectAdapters(inUseProjectDataAdapters);
		if (projectDataDialog.open() == SelectProjectDataAdapter.OK)
		{
			IProjectDataAdapter[] adapters = projectDataDialog
					.getSelectResult();
			
			NewProjectWizard wizard = ContextInjectionFactory.make(
					NewProjectWizard.class, context);
			
			if(resourceNavigator == null)
			{			
				MPart part = partService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
				resourceNavigator = (ResourceNavigator) part.getObject();
			}			
			wizard.setNavigator(resourceNavigator);

			wizard.setAdapters(adapters);
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.open();
		}

	}

	@Override
	public boolean isHandled()
	{
		if(selectionService != null)
		{
			Object obj = selectionService.getSelection();
			if(obj instanceof IResource)
			{
				IResource iResource = (IResource) obj;				
				if(iResource.getType() == IProject.PROJECT)
					if(!((IProject)iResource).isOpen())
						return false;
			}				
		}
		
		return true;		
	}

	public void setResourceNavigator(ResourceNavigator resourceNavigator)
	{
		this.resourceNavigator = resourceNavigator;
	}
	
	
}
