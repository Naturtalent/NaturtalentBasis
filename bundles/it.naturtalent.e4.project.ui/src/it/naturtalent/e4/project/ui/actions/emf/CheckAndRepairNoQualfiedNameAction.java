package it.naturtalent.e4.project.ui.actions.emf;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.Activator;

/**
 * prueft ob fuer das uebergebene IProjectID ein IProject existiert und eine Qualified-Nama existiert. Fehlt der Qualified-Name
 * wird versucht ueber das EMF NtProject den Namen zu rekonstruieren.
 *  
 * @author dieter
 *
 */
public class CheckAndRepairNoQualfiedNameAction extends Action
{
	private String iProjectID;
	
	public CheckAndRepairNoQualfiedNameAction(String iProjectID)
	{
		super();
		this.iProjectID = iProjectID;
	}

	@Override
	public void run()
	{
		// existiert ein IProject mit dem ID
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(iProjectID);
		if(iProject.exists())
		{
			// exist ein qualifizierter Name
			try
			{
				String qualiName = iProject.getPersistentProperty(INtProject.projectNameQualifiedName);
				if (StringUtils.isEmpty(qualiName))
				{
					// ein NtProjekt mit dem ID laden
					NtProject ntProject = Activator.findNtProject(iProjectID);
					if (ntProject != null)
					{
						String name = ntProject.getName();

						String message = "'" + name + "'" + "rekonstruieren?";
						if(MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),"CheckAndRepair", message))
						{
							try
							{
								iProject.setPersistentProperty(INtProject.projectNameQualifiedName,name);
							} catch (CoreException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

			} catch (CoreException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
