package it.naturtalent.e4.project.ui.utils;

import java.util.LinkedList;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.dialogs.IDialogSettings;

/**
 * In der Liste (Queue) werden die ProjectIDs der zulezt selektierten Projekte gespeichert. 
 * Der Queue ermoeglicht eine vor-rueckwaerts Selektion der gespeicherten Projekte.
 * 
 * @author dieter
 *
 */
public class ProjectQueue extends LinkedList<String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2939091301172087856L;
	private static final String PROJECTQUEUE_SETTINGKEY = "projectqueuesettings";
	private IDialogSettings dialogSettings = WorkbenchSWTActivator.getDefault().getDialogSettings();		
	
	public void start()
	{
		String [] queueStettings = dialogSettings.getArray(PROJECTQUEUE_SETTINGKEY);
		if(ArrayUtils.isNotEmpty(queueStettings))
		{
			for(String projectID : queueStettings)
				addLast(projectID);
		}
	}
	
	@Override
	public void addLast(String projectID)
	{
		if(size() > 20)
			removeFirst();
		remove(projectID);
		super.addLast(projectID);
	}



	public void stop()
	{
		String [] queueStettings = toArray(new String[size()]);
		dialogSettings.put(PROJECTQUEUE_SETTINGKEY, queueStettings);
	}
	
}
