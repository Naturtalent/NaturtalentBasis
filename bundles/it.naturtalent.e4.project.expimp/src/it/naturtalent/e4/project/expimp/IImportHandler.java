package it.naturtalent.e4.project.expimp;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

public interface IImportHandler
{
	public void execute(Shell shell);
	
	public boolean canExecute();
}
