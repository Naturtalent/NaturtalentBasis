package it.naturtalent.e4.project.ui.dialogs;

import java.util.List;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkingSet;

/**
 * erweitert 'ConfigureWorkingSetDialog' mit dem Ziel festzulegen, in welchen WorkingSets ein Projekt Mitglied ist. 
 * Die Button 'new'; 'edit' und 'delete' sind gesperrt.
 * 
 * @author dieter
 *
 */
public class SelectWorkingSetDialog extends ConfigureWorkingSetDialog
{

	private String dialogMessage = Messages.SelectWorkingSetDialog_titel;
	
	public SelectWorkingSetDialog(Shell parentShell,IWorkingSet[] activeWorkingSets)
	{
		super(parentShell, activeWorkingSets);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void updateWidgets()
	{		
		super.updateWidgets();
		btnNew.setEnabled(false);
		btnDelete.setEnabled(false);
		btnEdit.setEnabled(false);		
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{		
		Control control = super.createDialogArea(parent);
		
		// Titel anpassen
		//setMessage(Messages.SelectWorkingSetDialog_titel);
		setMessage(dialogMessage);
		setTitle(Messages.SelectWorkingSetDialog_message);
				
		return control;		
	}

	@Override
	protected void init()
	{		
		super.init();
		
		// WorkingSet 'Others' kann nicht selektiert werden - enfernen
		List<IWorkingSet>viewersWorkingSets = (List<IWorkingSet>)tableViewer.getInput();
		for(IWorkingSet workingSet : viewersWorkingSets)
		{
			if(workingSet.getName().equals(IWorkingSetManager.OTHER_WORKINGSET_NAME))
			{
				tableViewer.remove(workingSet);
				break;
			}
		}
	}

	public void setDialogMessage(String dialogMessage)
	{
		this.dialogMessage = dialogMessage;
	}



	
	
	

}
