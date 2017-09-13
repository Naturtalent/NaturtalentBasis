package it.naturtalent.e4.project;

import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;

public interface IProjectDataFactory
{
	public ProjectData getProjectData(INtProject ntProject);
	
	public IProjectData getProjectData (Class<?> projectDataClass, InputStream in);
	
	public IProjectData readProjectData (IProjectDataAdapter adapter, String projectId);
	
	public IProjectData readProjectData (IProjectDataAdapter adapter, INtProject ntProject);
	
	// speichert alle ProjektDaten (die Klassen sind im jeweiligen Adapter definiert)
	public void saveOrUpdateProjectData (Shell shell, List<IProjectDataAdapter>lAdapters, INtProject ntProject);
	
	/**
	 * Alle Projektdaten loeschen
	 * @param iProject
	 */
	public void deleteProjectData (IProject iProject);
	
	public void deleteProjectDataAdapter(IProject project, IProjectDataAdapter adapter);
}
