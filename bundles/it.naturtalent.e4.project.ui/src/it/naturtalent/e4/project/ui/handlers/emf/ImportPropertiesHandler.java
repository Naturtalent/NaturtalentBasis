 
package it.naturtalent.e4.project.ui.handlers.emf;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.internal.ui.util.ECPExportHandlerHelper;
import org.eclipse.emf.ecp.internal.ui.util.ECPImportHandlerHelper;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.ui.Activator;

public class ImportPropertiesHandler
{
	@Execute
	public void execute(Shell ahell)
	{
		/*
		ECPProject ecpProject = Activator.getECPProject();
		
		NtProjects ntProjects = Activator.getNtProjects();
		if(ntProjects != null)
		{
			List<Object>lNtObjects = new ArrayList<Object>();
			lNtObjects.add(ntProjects);
			ecpProject.deleteElements(lNtObjects);
			Activator.ntProjects = null;
		}
		*/
		
		//Activator.deleteNtProjects();
		
		ECPProject ecpProject = Activator.getECPProject();
		ECPImportHandlerHelper.importElement(ahell, ecpProject);
		ecpProject.saveContents();
		
	}
	
	@CanExecute
	public boolean canExecute()
	{
		return true;
		/*
		NtProjects ntProjects = Activator.getNtProjects();
		if(ntProjects == null)
			return false;
		return (ntProjects.getNtProject().size() == 0);
		*/
	}
}