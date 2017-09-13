package it.naturtalent.e4.project.ui.actions.emf;

import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.StructuredViewer;

import it.naturtalent.e4.project.ui.emf.NtProjectProperty;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.parts.emf.ProjectView;
import it.naturtalent.emf.model.actions.DefaultModelAction;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

public class SaveProjectAction2 extends DefaultModelAction
{

	public SaveProjectAction2(StructuredViewer viewer)
	{
		super(viewer);
		
		setImageDescriptor(Icon.COMMAND_SAVE.getImageDescriptor(IconSize._16x16_DefaultIconSize));
		setEnabled(false);
	}

	//@Override
	public void run()
	{		
		
		NtProjectProperty projectProperty = new NtProjectProperty();
		//projectProperty.setNtPropertyData(eObject);
		projectProperty.commit();
		
		// Projekt im Navigator updaten
		MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
		EPartService partService  = currentApplication.getContext().get(EPartService.class);
		MPart mPart = partService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		ResourceNavigator resourceNavogator = (ResourceNavigator) mPart.getObject();
		resourceNavogator.getTreeViewer().refresh();
		
		/*
		ECPHandlerHelper.saveProject(Activator.getECPProject());

		EObject eObject = geteObject();
		if(eObject != null)
			eventBroker.send(ProjectModelEventKey.PROJECT_UNDO_MODELEVENT, eObject);
			*/
	}
}
