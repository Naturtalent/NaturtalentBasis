package it.naturtalent.e4.project.ui.emf;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.ProjectPackage;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.DefaultNtProjectProperty;

/**
 * Obligatorische Eigenschaft des NtProjekts.
 * 
 * @author dieter
 *
 */

public class NtProjectProperty2 extends DefaultNtProjectProperty
{
	{
		//ecpProject = Activator.getECPProject();
		//undoEventKey = ProjectModelEventKey.PROJECT_UNDO_MODELEVENT;
	}
	
	private IProject selectedProject;
	
	@PostConstruct
	private void postConstruct(
			@Named(IServiceConstants.ACTIVE_SELECTION) @Optional IProject selectedProject)
	{
		this.selectedProject = selectedProject;	
	}
	
	@Override
	public Object init()
	{
		ntPropertyData = null;

		if(StringUtils.isEmpty(ntProjectID))
		{
			// keine ProjektID definiert - neues NtProjekt erzeugen 
			EClass newMEType = ProjectPackage.eINSTANCE.getNtProject();
			EPackage ePackage = newMEType.getEPackage();
			ntPropertyData = (it.naturtalent.e4.project.model.project.NtProject) ePackage.getEFactoryInstance().create(newMEType);
			
			// ggf. Name des selektierten Projekts vorbesetzen
			if(selectedProject != null)
			{
				//NtProject ntSelectedProject = findNtProject(selectedProject.getName());
				NtProject ntSelectedProject = Activator.findNtProject(selectedProject.getName());
				if(ntSelectedProject != null)
				{
					NtProject ntProject = (NtProject)ntPropertyData;
					ntProject.setName(ntSelectedProject.getName());
				}
			}
		}
		else
		{
			// vorhandenes NtProjekt ueber die ProjektID suchen 
			
			NtProject ntProject = Activator.findNtProject(ntProjectID);
			if(ntProject != null)
				ntPropertyData = ntProject;
			
			
			/*
			EList<Object>contents = ecpProject.getContents();
			for(Object content : contents)
			{
				if(content instanceof NtProject)
				{
					NtProject ntProject = (NtProject)content;
					if(StringUtils.equals(ntProjectID, ((NtProject)content).getId()))
					{
						ntPropertyData = (NtProject) content;
						break; 
					}					
				}
			}
			*/
			
			
			
		}

				
		return ntPropertyData;
	}
	
	/*
	private NtProject findNtProject(String projectID)
	{
		//EList<Object> contents = ecpProject.getContents();
		EList<Object> contents = Activator.getProjectContents();
		
		for (Object content : contents)
		{
			if (content instanceof NtProject)
			{
				NtProject ntProject = (NtProject) content;
				if (StringUtils.equals(projectID,
						((NtProject) content).getId()))
					return (NtProject) content;
			}
		}

		return null;
	}
	*/	

	@Override
	public void commit()
	{
		NtProject ntProject = (NtProject) ntPropertyData;
		if(StringUtils.isEmpty(ntProject.getId()))
		{
			//noch keine ID - ntProject wurde neu erzeugt			
			ntProject.setId(getNtProjectID());
			//ecpProject.getContents().add(ntPropertyData);
			Activator.getNtProjects().getNtProject().add((NtProject) ntPropertyData);
		}
				
		try
		{
			// die Projekteigenschaft 'name' im Workspaceprojekt 'IProject' persistent uebernehmen 
			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProject.getId());
			iProject.setPersistentProperty(INtProject.projectNameQualifiedName, ntProject.getName());
			
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.commit();
	}
	

	public String toString()
	{
		return (StringUtils.isEmpty(ntProjectID) ? "NtProjekt undefiniert" : "erstellt am: "+getCreatedDate()); 
	}

	/**
	 * aus der NtProjectID das Erstellungsdatum generieren
	 * 
	 * @return
	 */
	private String getCreatedDate()
	{		
		String stgDate = ntProjectID.substring(0, ntProjectID.indexOf('-'));
		Date date = new Date(NumberUtils.createLong(stgDate));
		return (DateFormatUtils.format(date, "dd.MM.yyyy")); 
	}

	
}
