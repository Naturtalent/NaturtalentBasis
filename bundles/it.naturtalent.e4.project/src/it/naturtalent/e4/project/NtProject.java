package it.naturtalent.e4.project;


import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;



import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;


public class NtProject extends BaseBean implements INtProject 
{
	//it.naturtalent.projekt.ProjectNature
	
	private IProject iProject;
	
	// ProjektNature
	public static final String NATURE_ID = "it.naturtalent.projekt.ProjectNature";


	
	private String name = null;

	public NtProject(IProject iProject)
	{
		super();
		this.iProject = iProject;
	}
	
	@Override
	public String getName()
	{
		if(name != null)
			return name;
					
		if (iProject != null)
		{
			try
			{
				// Name aus dem Projekt ProjectProperty laden
				name = iProject.getPersistentProperty(projectNameQualifiedName);
				if (StringUtils.isNotEmpty(name))
					return name;

			} catch (CoreException e)
			{
			}
		}

		// kein Name gespeichert - ProjectID mit Markierung generieren
		return name = (iProject != null ? "*" + iProject.getName() + "*" : null);
	}

	@Override
	public void setName(String name)
	{
		if (iProject != null)
		{
			try
			{
				iProject.setPersistentProperty(projectNameQualifiedName,name);
				firePropertyChange(new PropertyChangeEvent(this, "name", this.name,
						this.name = name));		
			} catch (CoreException e)
			{
				
			}					
		}		
	}

	@Override
	public IResource[] getResources()
	{
		if (iProject != null)
		{
			try
			{
				if(iProject.isOpen())				
					return iProject.members();				
			} catch (Exception e)				
			{				
			}	
		}
		
		return null;
	}

	@Override
	public boolean isOpen()
	{
		if (iProject != null)
			return iProject.isOpen();

		return false;
	}

	public IProject getIProject()
	{
		return iProject;
	}

	public void setiProject(IProject iProject)
	{
		this.iProject = iProject;
	}


	
	public String getNatureID()
	{
		try
		{
			String[] natureIds = iProject.getDescription().getNatureIds();
			if (ArrayUtils.isNotEmpty(natureIds))
				return iProject.getDescription().getNatureIds()[0];
		} catch (Exception e1)
		{
		}

		return null;
	}

	@Override
	public String getId()
	{		
		return iProject.getName();
	}
	
}
