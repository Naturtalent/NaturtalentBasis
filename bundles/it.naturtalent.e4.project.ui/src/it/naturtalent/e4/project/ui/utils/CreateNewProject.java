package it.naturtalent.e4.project.ui.utils;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.handlers.OpenResourceHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

public class CreateNewProject
{
	
	
	/**
	 * @param shell
	 * @param mapProjectNames (key, aliasName)
	 */
	public static void createProject(Shell shell, final Map<String,String>mapProjectNames)
	{			
		Log log = LogFactory.getLog(CreateNewProject.class);
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
		{
			public void execute(IProgressMonitor monitor) throws CoreException
			{
				try
				{
					monitor.beginTask("Projekte erzeugen", 2 * mapProjectNames.size());
					
					// Hilfskonstruktionen zuruecksetzen
					Activator.creatProjectAuxiliaryFlag = false;
					Activator.newlyCreatedProjectMap.clear();
					
					for (String key : mapProjectNames.keySet())
					{
						if (monitor.isCanceled())
						{
							throw new OperationCanceledException();
						}
						else
						{
							Activator.newlyCreatedProjectMap.put(key,mapProjectNames.get(key));
							IProject iProject = ResourcesPlugin.getWorkspace()
									.getRoot().getProject(key);
							
							Activator.creatProjectAuxiliaryFlag = true;
							//iProject.create(new SubProgressMonitor(monitor, 1));
							//iProject.open(new SubProgressMonitor(monitor, 1));
							iProject.create(monitor);
							iProject.open(monitor);
						}
					}
					
				} finally
				{
					monitor.done();
				}
			}
		};
	try
		{
			// im Progressmonitor ausfuehren
			new ProgressMonitorDialog(shell).run(true, false, operation);

		} catch (InterruptedException e)
		{
			// Abbruch
			MessageDialog.openError(shell, "Abbruch", e.getMessage());
		} catch (InvocationTargetException e)
		{
			if (e.getTargetException() instanceof CoreException)
			{				
				ErrorDialog.openError(shell,
						"New Project Error", null, 													
						((CoreException) e.getTargetException()).getStatus());
			}
			else
			{
				// CoreExceptions are handled above, but unexpected runtime exceptions and errors may still occur.
				String msg = "interner Fehler beim Erzeugen eines Projektes";				
				if(StringUtils.isNotEmpty(e.getTargetException().getMessage()))
					msg = msg + "\n" + e.getTargetException().getMessage();					
				log.error(msg);
				MessageDialog.openError(shell, "New Project Error", msg);
			}
		}
	}

	
	
	/**
	 * Ein neues Project erzeugen
	 * 
	 * @param shell
	 * @param container
	 * @param folderName
	 * @param linkTarget
	 */
	public static void createProject(Shell shell, final String [] projectAliasNames)
	{			
		if(ArrayUtils.isEmpty(projectAliasNames))
			return;
		
		Log log = LogFactory.getLog(CreateNewProject.class);
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
		{
			public void execute(IProgressMonitor monitor) throws CoreException
			{
				try
				{
					monitor.beginTask("neues Projekt", 2 * projectAliasNames.length);
					
					// Hilfskonstruktionen zuruecksetzen
					Activator.creatProjectAuxiliaryFlag = false;
					Activator.newlyCreatedProjectMap.clear();
					
					for (String projectAliasName : projectAliasNames)
					{

						if (monitor.isCanceled())
						{
							throw new OperationCanceledException();
						}
						else
						{
							// ID erzeugen und zusammen mit Aliasnamen zwischenspeichern
							String key = makeIdentifier();
							Activator.newlyCreatedProjectMap.put(key,projectAliasName);
							IProject iProject = ResourcesPlugin.getWorkspace()
									.getRoot().getProject(key);
							
							Activator.creatProjectAuxiliaryFlag = true;
							//iProject.create(new SubMonitor(monitor, 1));
							//iProject.open(new SubMonitor(monitor, 1));
							
							iProject.create(new SubProgressMonitor(monitor, 1));
							iProject.open(new SubProgressMonitor(monitor, 1));
						}
					}
					
				} finally
				{
					monitor.done();
				}
			}
		};
	try
		{
			// im Progressmonitor ausfuehren
			new ProgressMonitorDialog(shell).run(true, false, operation);

		} catch (InterruptedException e)
		{
			// Abbruch
			MessageDialog.openError(shell, "Abbruch", e.getMessage());
		} catch (InvocationTargetException e)
		{
			if (e.getTargetException() instanceof CoreException)
			{				
				ErrorDialog.openError(shell,
						"New Project Error", null, 													
						((CoreException) e.getTargetException()).getStatus());
			}
			else
			{
				// CoreExceptions are handled above, but unexpected runtime exceptions and errors may still occur.
				String msg = "interner Fehler beim Erzeugen eines Projektes";				
				if(StringUtils.isNotEmpty(e.getTargetException().getMessage()))
					msg = msg + "\n" + e.getTargetException().getMessage();					
				log.error(msg);
				MessageDialog.openError(shell, "New Project Error", msg);
			}
		}
	}
	
	/**
	 * einen datumsbasierenden Key erzeugen
	 */

	private static String date;

	private static long identifierCounter;

	/**
	 * Einen eindeutigen, datumsbasierenden Schl�ssel erzeugen
	 * 
	 * @return
	 */
	public static String makeIdentifier()
	{
		if (date == null)
			date = Long.toString((new Date().getTime())) + "-";
		return date + Long.toString(++identifierCounter);
	}
}
