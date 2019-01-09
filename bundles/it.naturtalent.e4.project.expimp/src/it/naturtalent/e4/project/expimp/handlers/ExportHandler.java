package it.naturtalent.e4.project.expimp.handlers;

import it.naturtalent.e4.project.expimp.dialogs.ExportDialog;
import it.naturtalent.e4.project.expimp.dialogs.SelectExportDialog;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;


public class ExportHandler 
{
	
	@Inject @Optional IEclipseContext context;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Execute
	public void execute(Shell shell, MPart part)
	{
		ExportDialog exportDialog = ContextInjectionFactory.make(ExportDialog.class, context);
		exportDialog.open();
	}
	
	@CanExecute
	public boolean canExecute()
	{
		// TODO Your code goes here
		return true;
	}

}