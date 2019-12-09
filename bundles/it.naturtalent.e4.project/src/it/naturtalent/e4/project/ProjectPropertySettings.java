package it.naturtalent.e4.project;

import java.io.InputStream;
import java.util.HashSet;

//import javax.xml.bind.JAXB;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;



/**
 * @author A682055
 *
 * Diese Klasse speichert und laedt die ProjectPropertyData im jeweiligen Projektdatenordner.
 * ProjectPropertyData enthaelt alle PropertyFactories die dem Projekt zugeordnet sind.
 * Die ProjektFactories wiederum sind mit ihrem Klassennamen gespeichert. 
 * Speicherort im Filesystem './.projectData.propertyData.xml'
 */
public class ProjectPropertySettings
{

	// Zugriff auf EventBroker
	MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
	private IEventBroker eventBroker = currentApplication.getContext().get(IEventBroker.class);

	
	//private Log log = LogFactory.getLog(this.getClass());
	
	
	/**
	 * Propertydaten des Projekts 'iProject' zurueckgeben.
	 * 
	 * @param iProject
	 * @return
	 */
	public ProjectPropertyData get(IProject iProject)
	{
		System.out.println("it.naturtalent.e4.project.ProjectPropertySettings get() - entfernen wegen JAXB");
		
		/*
		InputStream in = getProjectDataInputStream(iProject);
		if (in != null)
			return (ProjectPropertyData) JAXB.unmarshal(in, ProjectPropertyData.class);
			*/
		
		return null;
	}

	
	/**
	 * Die ProjectPropertyData in der 'propertyData' - Datei im Datenbereich des NrProjekts speichern.
	 * 
	 * @param iProject
	 * @param propertyData
	 * @throws CoreException
	 */
	public void putProperty(final IProject iProject,
			final ProjectPropertyData propertyData) throws CoreException
	{
		
		System.out.println("it.naturtalent.e4.project.ProjectPropertySettings.putProperty() - entfernen wegen JAXB");
		
		/*
		// AdapterID bezeichnet die Datendatei
		String name = ProjectPropertyData.PROP_PROPERTYDATACLASS;
		if (StringUtils.isNotEmpty(name))
		{
			IFolder folder = iProject
					.getFolder(IProjectData.PROJECTDATA_FOLDER);
			if (!folder.exists())
				folder.create(IFolder.FORCE, true, null);

			// Extension der Datendatei
			name = name + ".xml";
			IFile iFile = folder.getFile(name);

			// Projektdaten in Puffer
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JAXB.marshal(propertyData, out);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

			// Puffer in die Datedatei ausgeben
			if (iFile.exists())
				iFile.setContents(in, IFile.FORCE, null);
			else
				iFile.create(in, IFile.FORCE, null);				
		}	
		*/	
	}
	
	
	/**
	 * Die ProjectPropertyData in der 'propertyData' - Datei im Datenbereich des NrProjekts soeichern.
	 * 
	 * @param iProject
	 * @return
	 */
	public void put(final IProject iProject, final ProjectPropertyData propertyData)
	{
		
		System.out.println("it.naturtalent.e4.project.ProjectPropertySettings.put() - entfernen wegen JAXB");
		
		/*
		// AdapterID bezeichnet die Datendatei
		String name = ProjectPropertyData.PROP_PROPERTYDATACLASS;
		if (StringUtils.isNotEmpty(name))
		{
			// Ordner, indem alle Projektdaten abgelegt werden
			try
			{
				IFolder folder = iProject
						.getFolder(IProjectData.PROJECTDATA_FOLDER);
				if (!folder.exists())
					folder.create(IFolder.FORCE, true, null);

				// Extension der Datendatei
				name = name + ".xml";
				IFile iFile = folder.getFile(name);

				// Projektdaten in Puffer
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JAXB.marshal(propertyData, out);
				ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

				// Puffer in die Datedatei ausgeben
				if (iFile.exists())
					iFile.setContents(in, IFile.FORCE, null);
				else
					iFile.create(in, IFile.FORCE, null);
			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		*/

	}
	
	/*
	public void put(final IProject iProject, final ProjectPropertyData propertyData)
	{
		Shell shell = Display.getDefault().getActiveShell();		
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
		{
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException
			{
				monitor.beginTask("put ProjectPropertydata", 100);

				// AdapterID bezeichnet die Datendatei
				String name = ProjectPropertyData.PROP_PROPERTYDATACLASS;
				if (StringUtils.isNotEmpty(name))
				{
					// Ordner, indem alle Projektdaten abgelegt werden
					IFolder folder = iProject
							.getFolder(IProjectData.PROJECTDATA_FOLDER);
					if (!folder.exists())
						folder.create(IFolder.FORCE, true, monitor);

					// Extension der Datendatei
					name = name + ".xml";
					IFile iFile = folder.getFile(name);

					// Projektdaten in Puffer
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					JAXB.marshal(propertyData, out);
					ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

					// Puffer in die Datedatei ausgeben
					if (iFile.exists())
						iFile.setContents(in, IFile.FORCE, monitor);
					else
						iFile.create(in, IFile.FORCE, monitor);
				}

				monitor.done();
			}
		};

		try
		{
			// im Progressmonitor ausfuehren
			new ProgressMonitorDialog(shell)
					.run(true, false, operation);

		} catch (InterruptedException e)
		{
			// Abbruch
			MessageDialog.openError(shell, "Abbruch", e.getMessage());
		} catch (InvocationTargetException e)
		{
			// Error
			Throwable realException = e.getTargetException();
			String errmsg = StringUtils.isNotEmpty(realException
					.getMessage()) ? realException.getMessage() : "";
			MessageDialog.openError(shell, "Error",
					"Put Propertydata Error" + errmsg);
		}
	}
	*/

	public ProjectPropertyData addPropertyFactory(IProject iProject, String propertyFactoryName)
	{
		String [] factoriesArray;
		ProjectPropertyData projectPropertyData = get(iProject);
		if(projectPropertyData != null)
		{			
			HashSet<String>factoriesSet = new HashSet<String>();
			factoriesArray = projectPropertyData.getPropertyFactories();	
			if (ArrayUtils.isNotEmpty(factoriesArray))
			{
				for (String factory : factoriesArray)
					factoriesSet.add(factory);
			}
			factoriesSet.add(propertyFactoryName);
			factoriesArray = factoriesSet.toArray(new String[factoriesSet.size()]);
			projectPropertyData.setPropertyFactories(factoriesArray);
			put(iProject, projectPropertyData);
			
			// Broker informiert
			String [] brokerInfo = new String []{iProject.getName(),propertyFactoryName}; 
			eventBroker.post(INtProjectProperty.PROJECT_PROPERTY_EVENT_SET_PROPERTY, brokerInfo);
		}
		
		return projectPropertyData;
	}
	
	public ProjectPropertyData removePropertyFactory(IProject iProject, String propertyFactoryName)
	{
		String [] factoriesArray;
		ProjectPropertyData projectPropertyData = get(iProject);
		if(projectPropertyData != null)
		{
			HashSet<String>factoriesSet = new HashSet<String>();
			factoriesArray = projectPropertyData.getPropertyFactories();	
			if (ArrayUtils.isNotEmpty(factoriesArray))
			{
				for (String factory : factoriesArray)
					factoriesSet.add(factory);
			}
			factoriesSet.remove(propertyFactoryName);
			factoriesArray = factoriesSet.toArray(new String[factoriesSet.size()]);
			projectPropertyData.setPropertyFactories(factoriesArray);
			put(iProject, projectPropertyData);
			
			// Broker informiert
			String [] brokerInfo = new String []{iProject.getName(),propertyFactoryName}; 
			eventBroker.post(INtProjectProperty.PROJECT_PROPERTY_EVENT_UNSET_PROPERTY, brokerInfo);
		}
		
		return projectPropertyData;
	}

	public static InputStream getProjectDataInputStream(IProject iProject)
	{		
		if ((iProject != null) && (iProject.isOpen()))
		{
			IFolder folder = iProject.getFolder(IProjectData.PROJECTDATA_FOLDER);
			if (folder.exists())
			{
				String name = ProjectPropertyData.PROP_PROPERTYDATACLASS + ".xml";
				IFile iFile = folder.getFile(name);
				if (iFile.exists())
					try
					{
						return iFile.getContents(true);
					} catch (CoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}

		return null;
	}
	
	public static String getProjectDataPath(IProject iProject)
	{		
		if ((iProject != null) && (iProject.isOpen()))
		{
			IFolder folder = iProject.getFolder(IProjectData.PROJECTDATA_FOLDER);
			if (folder.exists())
				return folder.getLocationURI().getPath();
		}
		
		return null;
	}
	
}
