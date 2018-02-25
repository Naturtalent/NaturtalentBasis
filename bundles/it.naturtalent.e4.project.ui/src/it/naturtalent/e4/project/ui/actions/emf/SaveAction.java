 
package it.naturtalent.e4.project.ui.actions.emf;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.parts.emf.NtProjectView;

import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class SaveAction
{
	@Execute
	public void execute(EPartService partService, EModelService modelService, MPart part)
	{
		Activator.getECPProject().saveContents();
		
		// ToolbarStatus triggern  
		List<MToolItem> items = modelService.findElements(part, NtProjectView.SAVE_TOOLBAR_ID, MToolItem.class,null, EModelService.IN_PART);
		MToolItem item = items.get(0);
		item.setEnabled(false);
		
		items = modelService.findElements(part, NtProjectView.UNDO_TOOLBAR_ID, MToolItem.class,null, EModelService.IN_PART);
		item = items.get(0);
		item.setEnabled(false);					
				
	}

	@CanExecute
	public boolean canExecute()
	{
		return Activator.getECPProject().hasDirtyContents();
	}
		
}