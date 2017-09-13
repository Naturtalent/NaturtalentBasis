 
package it.naturtalent.e4.project.ui.handlers.emf;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.internal.ui.util.ECPExportHandlerHelper;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.ui.Activator;

public class ExportPropertiesHandler
{
	@Execute
	public void execute(Shell ahell)
	{
		EObject project = Activator.getNtProjects();
		
		List<EObject>projects = new ArrayList<EObject>();
		projects.add(project);
		
		ECPExportHandlerHelper.export(ahell, (List<EObject>) projects);
		
	}

}