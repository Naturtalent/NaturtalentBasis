package it.naturtalent.e4.project;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

@Deprecated
public class ProjectDataAdapter extends AbstractProjectDataAdapter
{

	@Override
	public String getName()
	{
		return IProjectDataAdapter.DEFAULTPROJECTADAPTERNAME;
	}

	@Override
	public Class<?> getProjectDataClass()
	{
		return ProjectData.class;
	}

	@Override
	public String getId()
	{
		return ProjectData.PROP_PROJECTDATACLASS;
	}

	@Override
	public void setProjectData(Object data)
	{
		if(data == null)
		{
			data = new ProjectData();
			((ProjectData)data).setName("Template");
		}
		
		super.setProjectData(data);
	}

	@Override
	public void save()
	{
		if ((data != null) && (data instanceof IProjectData))
		{
			IProjectData projectData = (IProjectData) data;
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectData.getId());
			if ((project != null) && (project.isOpen()))
			{
				NtProject ntProjekt = new NtProject(project);
				if (!StringUtils.equals(projectData.getName(),
						ntProjekt.getName()))
					ntProjekt.setName(projectData.getName());
				super.save();
			}
		}
	}


}
