package it.naturtalent.application.handlers;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import it.naturtalent.application.dialogs.ShowViewDialog;

/**
 * Der Handler oeffnet einen allgemeinenen Dialog mit dem Views ueber ihre Id in den Status 'sichtbar' geschaltet werden.
 *  
 * @author dieter
 *
 */
public class ShowViewsHandler
{
	@Execute
	public void execute(IEclipseContext context, EPartService partService, EModelService modelService,MApplication application)
	{		
		ShowViewDialog dialog = ContextInjectionFactory.make(ShowViewDialog.class, context);
		if(dialog.open() == ShowViewDialog.OK)
		{	
			String viewId = dialog.getSelectectViewID();
			if(StringUtils.isNotEmpty(viewId))
			{
				// die ausgewaehlte View sichtbar machen
				MPart part = (MPart) modelService.find(viewId, application);
				part.setVisible(true);
				partService.showPart(part, PartState.ACTIVATE);
			}			
		}		
	}

}