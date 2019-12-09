package it.naturtalent.e4.project;

//import javax.xml.bind.JAXB;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

@Deprecated
public abstract class AbstractProjectDataAdapter implements IProjectDataAdapter
{
	
	//public static final String RESOURCENAVIGATOR_ID = "it.naturtalent.e4.project.ui.part.explorer";
	
	protected ESelectionService selectionService;	
	
	// standardisierter Zugriff auf die persistenten Daten
	private IProjectDataFactory projectDataFactory;
	
	protected Object data;
	
	protected NtProject ntProject;
	
	@Override
	public String getId()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getName()
	{
		return "keine Bezeichnung";
	}

	@Override
	public Class<?> getProjectDataClass()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Action getAction(IEclipseContext context)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Composite createComposite(Composite parent)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public WizardPage getWizardPage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProjectData(Object data)
	{
		this.data = data;		
	}

	@Override
	public Object getProjectData()
	{
		return data;
	}
	
	@Override
	public Object getProjectData(String projectID)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete()
	{
		// standardmaeßig wird durch das Loeschen eines Projekts auch die Projektdaten geloescht,
		// da die Projektdaten in Dateien des Projektverzeichnisses gespeichert sind.
	}
	
	@Override
	public Object load(String projectId)
	{
		Object dataObj = null;
		if((projectDataFactory != null) && (StringUtils.isNotEmpty(projectId)))			
			dataObj = (projectDataFactory.readProjectData(this, projectId));
		return dataObj;
	}

	@Override
	public void save()
	{
		
		System.out.println("it.naturtalent.e4.project.AbstractProjectDataAdapter.save() - entfernen wegen JAXB");
		
		/*
		if ((data != null) && (data instanceof IProjectData))
		{
			final IProjectData projectData = (IProjectData) data;
			Shell shell = Display.getDefault().getActiveShell();
			final IProject project = ntProject.getIProject();

			if ((projectData != null) && (project != null) && (shell != null))
			{
				WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
				{
					@Override
					protected void execute(IProgressMonitor monitor)
							throws CoreException, InvocationTargetException,
							InterruptedException
					{
						monitor.beginTask("Update Projectdata", 1);

						// AdapterID bezeichnet die Datendatei
						String name = getId();
						if (StringUtils.isNotEmpty(name))
						{
							// Ordner, indem alle Projektdaten abgelegt werden
							IFolder folder = project
									.getFolder(IProjectData.PROJECTDATA_FOLDER);
							if (!folder.exists())
								folder.create(IFolder.FORCE, true, monitor);

							// Extension der Datendatei
							name = name + ".xml";
							IFile iFile = folder.getFile(name);

							// Projektdaten in Puffer
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							JAXB.marshal(projectData, out);
							ByteArrayInputStream in = new ByteArrayInputStream(
									out.toByteArray());

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
							"Save Projectdata Error" + errmsg);
				}
			}
		}
		*/
	}
	
	
	
	@Override
	public String [] toText(Object projectData)
	{	
		String [] txtArray = {"Projekt"};
		String token;		
		if (projectData instanceof ProjectData)
		{
			IProjectData iProjectData = (IProjectData) projectData;
			
			token = iProjectData.getId();
			if(StringUtils.isNotEmpty(token))
				txtArray = ArrayUtils.add(txtArray, token);

			token = iProjectData.getName();
			if(StringUtils.isNotEmpty(token))
				txtArray = ArrayUtils.add(txtArray, token);

			/*
			token = iProjectData.getDescription();
			if(StringUtils.isNotEmpty(token))
				txtArray = ArrayUtils.add(txtArray, token);
				*/
			
			return txtArray;
		}
		
		return null;
	}

	public void setProjectDataFactory(IProjectDataFactory projectDataFactory)
	{
		this.projectDataFactory = projectDataFactory;
	}

	public void setSelectionService(ESelectionService selectionService)
	{
		this.selectionService = selectionService;
	}
	
	public Object getSelectedProjectData()
	{		
		return (ntProject != null) ? getProjectData(ntProject.getId()) : null;
			
		/*
		ntProject = null;
		Object obj = null;
		
		if(selectionService != null)
		{
			obj = selectionService.getSelection(RESOURCENAVIGATOR_ID);
			if(obj instanceof IProject)
			{
				IProject iProject = (IProject) obj;
				obj = getProjectData(iProject.getName());
				ntProject = new NtProject(iProject);
			}
		}
				
		return obj;
		*/
		
	}

	@Override
	public NtProject getProject()
	{
		return ntProject;
	}

	@Override
	public void setProject(NtProject project)
	{
		this.ntProject = project;
	}


	

}
