package it.naturtalent.e4.project;

public interface IProjectData
{
	
	public static final String PROJECT_EVENT = "projectEvent/"; //$NON-NLS-N$
	public static final String PROJECT_EVENT_MODIFY_PROJECTNAME = PROJECT_EVENT+"modifyProjectname"; //$NON-NLS-N$
	public static final String PROJECT_EVENT_MODIFY_PROJECTDATA = PROJECT_EVENT+"modifyProjectdata"; //$NON-NLS-N$
	public static final String PROJECT_EVENT_SAVE_EXTERMODEL = PROJECT_EVENT+"saveexternProjectdata"; //$NON-NLS-N$
		
	
	public static final String PROJECTDATA_FOLDER = ".projectdata";
	
	public static final String PROJECTDATAFILE = "projectData.xml";
	
	public String getId();
	
	public void setId(String id);
	
	public String getName();
	
	public void setName(String name);
	
	public String getDescription();
	
	public void setDescription(String description);
}
