package it.naturtalent.e4.search.handlers;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import it.naturtalent.e4.search.dialogs.SearchDialog;
import it.naturtalent.e4.search.parts.SearchView;

/**
 * Ueber diesen Handler wird ein allgemeiner Suchdialog aufgerufen. Die Suchoptionen koennen in diesem Dialog
 * ueber Foldertabs ausgewaehlt werden. Jeder Tab repraesentiert eine Suchseite die die Einzelheiten der Suche festlegt.
 *   
 * @author dieter
 *
 */
public class SearchHandler 
{
	
	@Execute
	public void execute(EPartService partService, EModelService modelService, IEclipseContext context ,MApplication application)
	{
		// SearchView aktivieren (sichtbar machen) - zeigt die Ergebisse der Suche an	
		MPart part = (MPart) modelService.find(SearchView.SEARCHVIEW_ID, application);
		part.setVisible(true);
		partService.showPart(part, PartState.ACTIVATE);
		
		// allgemeiner Suchdialog erzeugen und oeffnen
		SearchDialog searchDialog = ContextInjectionFactory.make(SearchDialog.class, context);
		searchDialog.open();
	}

	@CanExecute
	public boolean canExecute()
	{
		return true;
	}

}