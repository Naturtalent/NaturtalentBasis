package it.naturtalent.e4.project.ui.handlers;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.ui.actions.SystenOpenEditorAction;

/**
 * Der Handler wird ueber das Menue aufgerufen
 * 
 * @author dieter
 *
 */
public class SystemOpenWithHandler extends SelectedResourcesUtils
{
	@Execute
	public void execute(Shell shell, MPart part, IEclipseContext context)
	{		
		// die Aktion 'SystenOpenEditorAction' oeffnet die selektierte Datei  
		SystenOpenEditorAction systemEditor = ContextInjectionFactory.make(SystenOpenEditorAction.class, context);
		systemEditor.run();
	}

}
