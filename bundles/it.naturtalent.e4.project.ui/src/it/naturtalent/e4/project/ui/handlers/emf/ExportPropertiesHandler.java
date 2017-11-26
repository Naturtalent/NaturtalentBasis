 
package it.naturtalent.e4.project.ui.handlers.emf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.ECPProjectManager;
import org.eclipse.emf.ecp.core.util.ECPUtil;
import org.eclipse.emf.ecp.internal.ui.util.ECPExportHandlerHelper;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.ui.Activator;

public class ExportPropertiesHandler
{
	@Execute
	public void execute(Shell ahell)
	{
		EObject project = Activator.getNtProjects();
		
		ECPProjectManager ecbProjectManager = ECPUtil.getECPProjectManager();
		Collection<ECPProject>allProjects = ecbProjectManager.getProjects();
		
		List<EObject>models = new ArrayList<EObject>();
		for(ECPProject ecpProject : allProjects)
		{
			EList<Object> childs = ecpProject.getContents();
			for (Object child : childs)
			{
				if (child instanceof EObject)			
					models.add((EObject) child);			
			}
		}
		
		ECPExportHandlerHelper.export(ahell, models);
		
	}

}