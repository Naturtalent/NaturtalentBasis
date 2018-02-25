 
package it.naturtalent.e4.project.ui.actions.emf;

import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.parts.emf.NtProjectView;

public class UndoAction
{
	@Execute
	public void execute(ESelectionService selectionService, EPartService partService, EModelService modelService, MPart part)
	{
		EditingDomain domain = AdapterFactoryEditingDomain
				.getEditingDomainFor(Activator.getECPProject());

		if (domain != null)
		{
			// undo
			domain.getCommandStack().undo();
			if(!domain.getCommandStack().canUndo())
			{
				// keine weiteren undos - Daten via SaveAction festschreiben
				List<MToolBarElement> items = modelService.findElements(part, NtProjectView.SAVE_TOOLBAR_ID, MToolBarElement.class,null, EModelService.IN_PART);
				MToolBarElement toolItem = items.get(0);
				if (toolItem instanceof MContribution)
				{
					MContribution directTool = (MContribution) toolItem;
					Object obj = directTool.getObject();
					if (obj instanceof SaveAction)
					{
						SaveAction saveAction = (SaveAction) obj;
						saveAction.execute(partService,modelService, part);
					}
				}
			}
		}
	}

	@CanExecute
	public boolean canExecute()
	{
		EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(Activator.getECPProject());
		return domain.getCommandStack().canUndo();	
	}
		
}