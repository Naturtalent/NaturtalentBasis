package it.naturtalent.e4.project.ui.actions.emf;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.spi.ui.util.ECPHandlerHelper;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.emf.ProjectModelEventKey;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

public class UndoProjectAction extends Action
{

	private @Inject @Optional  EventBroker eventBroker;
	
	private ESelectionService selectionService;
	
	public static final String PROJECTCHANGED_MODELEVENT = "projectmodelevent"; //$NON-NLS-N$
	
	//private static EditingDomain domain;
	
	public UndoProjectAction()
	{
		setImageDescriptor(Icon.COMMAND_UNDO.getImageDescriptor(IconSize._16x16_DefaultIconSize));
		setEnabled(false);
	}
	
	@PostConstruct
	private void postConstruct(ESelectionService selectionService)
	{
		this.selectionService = selectionService;
	}
	
	@Override
	public void run()
	{
		EditingDomain domain = AdapterFactoryEditingDomain
				.getEditingDomainFor(Activator.getNtProjects());

		if (domain != null)
		{
			// undo
			domain.getCommandStack().undo();
			setEnabled(domain.getCommandStack().canUndo());

			if(!domain.getCommandStack().canUndo())
			{
				ECPHandlerHelper.saveProject(Activator.getECPProject());	
				eventBroker.send(PROJECTCHANGED_MODELEVENT, "Model last undo");
			}

		}
	}
	
	@Inject @Optional
	public void  getModelChangeEvent(@UIEventTopic(PROJECTCHANGED_MODELEVENT) String message) 
	{
		//System.out.println("it.naturtalent.e4.project.ui.actions.emf.UndoProjectAction.Modelchanged");
		setEnabled(Activator.getECPProject().hasDirtyContents());		
	}
	

}
