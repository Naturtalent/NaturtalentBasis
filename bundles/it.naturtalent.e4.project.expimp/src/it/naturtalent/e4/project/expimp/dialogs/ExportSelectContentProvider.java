package it.naturtalent.e4.project.expimp.dialogs;

import org.eclipse.ui.databinding.WorkbenchProperties;
import org.eclipse.ui.model.IWorkbenchAdapter;
import it.naturtalent.e4.project.ui.model.WorkbenchProject;

import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;

/**
 * Bereitet die Projekte fuer die Anzeige im ExportNtProjectDialog auf.
 * 
 * @see it.naturtalent.e4.project.expimp.dialogs.ExportNtProjectDialog
 *  
 * @author dieter
 *
 */
public class ExportSelectContentProvider extends WorkbenchContentProvider
{
	@Override
	public Object[] getChildren(Object element)
	{
		// nur bis zur Projektebene
		IWorkbenchAdapter adapter = getAdapter(element);
		if(adapter instanceof WorkbenchProject)
			return new Object[0];
		
		return super.getChildren(element);
	}

}
