package it.naturtalent.application.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.Action;

import it.naturtalent.application.dialogs.NewDialog;
import it.naturtalent.application.services.INewActionAdapter;
import it.naturtalent.application.services.INewActionAdapterRepository;


/**
 * Ein neues Object anlegen. 
 * 
 * Alle im zentralen Repository abgelegten Adapter (Projekt, Folder, File ...) in einem Dialog auflisten. 
 * Das neue Object wird ueber die im selektierte Adapter definierte Action erzeugt.  
 * 
 * Die adaptierten Aktionen sollten die 'isHandled()' - Methode ueberschreiben
 * 
 * @author dieter
 *
 */
public class NewHandler
{
	
	@Optional @Inject INewActionAdapterRepository newActionAdapterRepository;
	
	@Execute
	public void execute(IEclipseContext context)
	{			
		List<INewActionAdapter>newAdapters = newActionAdapterRepository.getNewWizardAdapters();
		NewDialog dialog = ContextInjectionFactory.make(NewDialog.class, context);
		if(dialog.open() == NewDialog.OK)
		{			
			INewActionAdapter adapter = dialog.getSelectedAdapter();
			if(adapter !=  null)
			{
				Action action = (Action) ContextInjectionFactory.make(adapter.getActionClass(), context);
				if(action.isHandled())
					action.run();
			}		
		}
	}

}