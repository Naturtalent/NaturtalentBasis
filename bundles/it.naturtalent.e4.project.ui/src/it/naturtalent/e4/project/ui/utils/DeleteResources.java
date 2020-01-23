package it.naturtalent.e4.project.ui.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.NtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.actions.emf.DeleteProjectAction;
import it.naturtalent.e4.project.ui.actions.emf.UndoProjectAction;
import it.naturtalent.e4.project.ui.parts.emf.NtProjectView;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;

public class DeleteResources
{
	public static void deleteResources(Shell shell, final IResource[] resources,
			final INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository)
	{
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
		{
			public void execute(IProgressMonitor monitor) throws CoreException
			{
				try
				{
					monitor.beginTask(Messages.DeleteResources_delete, resources.length);

					for (int i = 0; i < resources.length; i++)
					{
						if (monitor.isCanceled())
						{
							throw new OperationCanceledException();
						}
						
						// physikalisch entfernen
						if (resources[i].exists())
						{							
							// aus den WorkingSets entfernen
							removeFromWorkingSets(resources[i]);

							switch (resources[i].getType())
								{
									case IResource.PROJECT:
									{
										IProject iProject;
										IProjectDescription desc;

										iProject = (IProject) resources[i];
										
										// das zuloeschende Projekt braucht keine
										// description mehr
										desc = ResourcesPlugin.getWorkspace()
												.newProjectDescription(iProject.getName());
										desc.setNatureIds(new String[0]);

										// provoziert den Funktionsaufruf 'deconfigure()' im
										// ProjectNature
										// Projekt hat keine 'nature' mehr und wird
										// selbst im Fehlerfall beim folgenden 'delete' nicht
										// mehr im Navigator angezeigt (Datenleiche)
										iProject.setDescription(desc, null);

										// EMF ProjectPropertyDaten loeschen
										it.naturtalent.e4.project.model.project.NtProject ntProject = Activator
												.findNtProject(
														iProject.getName());
										NtProjects ntProjects = Activator.getNtProjects();			
										ntProjects.getNtProject().remove(ntProject);			

										// im Nachgang nochmal 'delete' - Funktion in alllen
										// Adapter aufrufen wenn Zugriff auf das FactoryRepository moeglich ist
										if(ntProjektDataFactoryRepository != null)
										{
											List<INtProjectPropertyFactory> projectPropertyFactories = ntProjektDataFactoryRepository
													.getAllProjektDataFactories();
											for(INtProjectPropertyFactory propertyFactory : projectPropertyFactories)
											{
												// koennen ueber den Adapter projectgekoppelte PropertyDaten geladen werden
												INtProjectProperty propertyAdapter = propertyFactory.createNtProjektData();
												propertyAdapter.setNtProjectID(iProject.getName());
												propertyAdapter.delete();
											}
										}
										
										// iProject aus dem ProjektQueue entfernen										
										Activator.projectQueue.remove(iProject.getName());
											
										// project loeschen
										iProject.delete(true, true, new SubProgressMonitor(monitor, 1));
																				
									}
									break;

									case IResource.FOLDER:

										((IFolder) resources[i]).delete(true, true,
												new SubProgressMonitor(monitor, 1));

										break;

									case IResource.FILE:

										((IFile) resources[i]).delete(true, true,
												new SubProgressMonitor(monitor, 1));

										break;

									default:
										break;
								}
						}
						else
							monitor.worked(1);
					}
					
					
				} finally
				{
					monitor.done();
	
					// Modelldaten persistent speichern
					Activator.getECPProject().saveContents();

					MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
					IEventBroker eventBroker = currentApplication.getContext().get(IEventBroker.class);
					
					eventBroker.send(UndoProjectAction.PROJECTCHANGED_MODELEVENT, "delete");
					//eventBroker.send(DeleteProjectAction.DELETE_PROJECT_EVENT, ntProject.getName());				
					eventBroker.post(NtProjectView.UPDATE_PROJECTVIEW_REQUEST, null);
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
			MessageDialog.openError(shell, Messages.DeleteResources_cancel, e.getMessage());
		} catch (InvocationTargetException e)
		{
			// Error
			Throwable realException = e.getTargetException();
			MessageDialog.openError(shell, Messages.DeleteResources_error, realException.getMessage());
		}
	
	}
	
	public static void deleteResources(Shell shell, final IResource[] resources)
	{
		deleteResources(shell, resources,null);
	}
	
	/**
	 * 
	 */
	public static void deleteResourcesOLD(Shell shell, final IResource[] resources)
	{
		WorkspaceModifyOperation operation = new WorkspaceModifyOperation()
		{
			public void execute(IProgressMonitor monitor) throws CoreException
			{
				try
				{
					monitor.beginTask(Messages.DeleteResources_delete, resources.length);

					for (int i = 0; i < resources.length; i++)
					{
						if (monitor.isCanceled())
						{
							throw new OperationCanceledException();
						}
						
						// physikalisch entfernen
						if (resources[i].exists())
						{							
							// aus den WorkingSets entfernen
							removeFromWorkingSets(resources[i]);

							switch (resources[i].getType())
								{
									case IResource.PROJECT:
									{
										IProject iProject;
										IProjectDescription desc;

										iProject = (IProject) resources[i];
										
										// das zuloeschende Projekt braucht keine
										// description mehr
										desc = ResourcesPlugin.getWorkspace()
												.newProjectDescription(iProject.getName());
										desc.setNatureIds(new String[0]);

										// provoziert den Funktionsaufruf 'deconfigure()' im
										// ProjectNature
										// Projekt hat keine 'nature' mehr und wird
										// selbst im Fehlerfall beim folgenden 'delete' nicht
										// mehr im Navigator angezeigt (Datenleiche)
										iProject.setDescription(desc, null);

										// EMF ProjectPropertyDaten loeschen
										it.naturtalent.e4.project.model.project.NtProject ntProject = Activator
												.findNtProject(
														iProject.getName());
										NtProjects ntProjects = Activator.getNtProjects();			
										ntProjects.getNtProject().remove(ntProject);			

										// im Nachgang nochmal alle Adapter aufrufen
										//MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();										
										//NtProjectPropertyFactoryRepository projektDataFactoryRepository = currentApplication.getContext().get(NtProjectPropertyFactoryRepository.class);
										
										
										
										/*
										List<IProjectDataAdapter> alllAdapters = ProjectDataAdapterRegistry
												.getProjectDataAdapters();										
										List<IProjectDataAdapter> lProjectAdapters = new ArrayList<IProjectDataAdapter>();
										for (IProjectDataAdapter adapter : alllAdapters)
										{
											if(adapter.load(ntProject.getId()) != null)
											{
												adapter.setProject(ntProject);
												adapter.delete();
											}
										}
										*/

										
										
										final MApplication application = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
										IEclipseContext selectedWindowContext = application.getSelectedElement().getContext();										
										DeleteProjectAction deleteAction = ContextInjectionFactory
												.make(DeleteProjectAction.class,selectedWindowContext);
										deleteAction.run();
										
										
										// ProjectProperties loeschen
										/*
										MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
										IEclipseContext selectedWindowContext = currentApplication.getSelectedElement().getContext();										
										INtProjectPropertyFactoryRepository projektDataFactoryRepository = selectedWindowContext.get(INtProjectPropertyFactoryRepository.class);
										
										// alle dem iProject zugeordneten PropertyFactories auflisten 
										List<INtProjectProperty>projectProperties = NtProjektPropertyUtils.getProjectProperties(
												projektDataFactoryRepository, iProject);
										if(projectProperties != null)
										{
											for(INtProjectProperty projectProperty : projectProperties)		
											{
												//projectProperty.init();
												projectProperty.delete();
											}
										}
										*/
										
										

										// Projektdaten loeschen
										/*
										NtProject ntProject = new NtProject(iProject);
										IProjectDataAdapter[]inUseAdapters = NtProject.getAssignedProjectDataAdapters(ntProject);
										if(ArrayUtils.isNotEmpty(inUseAdapters))
										{
											for(IProjectDataAdapter adapter : inUseAdapters)
												adapter.delete();
										}
										Activator.projectDataFactory.deleteProjectData(iProject);
										*/

										
										// project loeschen
										iProject.delete(true, true, new SubProgressMonitor(
												monitor, 1));
																				
									}
									break;

									case IResource.FOLDER:

										((IFolder) resources[i]).delete(true, true,
												new SubProgressMonitor(monitor, 1));

										break;

									case IResource.FILE:

										((IFile) resources[i]).delete(true, true,
												new SubProgressMonitor(monitor, 1));

										break;

									default:
										break;
								}
						}
						else
							monitor.worked(1);
					}
					
					
				} finally
				{
					monitor.done();
	
					// Modelldaten persistent speichern
					Activator.getECPProject().saveContents();

					MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
					IEventBroker eventBroker = currentApplication.getContext().get(IEventBroker.class);
					
					eventBroker.send(UndoProjectAction.PROJECTCHANGED_MODELEVENT, "delete");
					//eventBroker.send(DeleteProjectAction.DELETE_PROJECT_EVENT, ntProject.getName());				
					eventBroker.post(NtProjectView.UPDATE_PROJECTVIEW_REQUEST, null);
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
			MessageDialog.openError(shell, Messages.DeleteResources_cancel, e.getMessage());
		} catch (InvocationTargetException e)
		{
			// Error
			Throwable realException = e.getTargetException();
			MessageDialog.openError(shell, Messages.DeleteResources_error, realException.getMessage());
		}
	}

	public static void removeFromWorkingSets(IResource removeElement)
	{
		WorkingSetManager workingSetManager = Activator.getWorkingSetManager();
		
		IWorkingSet [] workingSets = workingSetManager.getAllWorkingSets();
		for(IWorkingSet workingSet : workingSets)		
			workingSetManager.removeWorkingSetsElements(new IResource [] {removeElement});		
	}

	private static void removeFromWorkingSetsOLD(IResource removeElement)
	{		
		Set<IAdaptable>removeList = new HashSet<IAdaptable>();
		IPath elementPath = removeElement.getFullPath();
		
		IWorkingSet [] workingSets = Activator.getWorkingSetManager().getAllWorkingSets();
		for(IWorkingSet workingSet : workingSets)
		{
			IAdaptable[] workingSetElements  = workingSet.getElements();
			for (int i = 0; i < workingSetElements.length; i++)
			{
				IAdaptable workingSetElement = workingSetElements[i];
				
				IProject iProject = (IProject) workingSetElement.getAdapter(IProject.class);
				if((iProject != null) && (removeElement.getType() != IResource.PROJECT))
					continue;
				
				if(isEnclosedResource(removeElement, elementPath, workingSetElement))
					removeList.add(workingSetElement);
			}
		}
		
		IAdaptable [] removeAdaptables = removeList.toArray(new IAdaptable[removeList.size()]);
		Activator.getWorkingSetManager().removeWorkingSetsElements(removeAdaptables);
	}
	
	/**
	 * Returns if the given resource is enclosed by a working set element. A
	 * resource is enclosed if it is either a parent of a working set element, a
	 * child of a working set element or a working set element itself. Simple
	 * path comparison is used. This is only guaranteed to return correct
	 * results for resource working set elements.
	 * 
	 * @param element
	 *            resource to test for enclosure by a working set element
	 * @param elementPath
	 *            full, absolute path of the element to test
	 * @return true if element is enclosed by a working set element and false
	 *         otherwise.
	 */
	private static boolean isEnclosedResource(IResource element, IPath elementPath,
			IAdaptable workingSetElement)
	{
		IResource workingSetResource = null;

		if (workingSetElement.equals(element))
		{
			return true;
		}
		if (workingSetElement instanceof IResource)
		{
			workingSetResource = (IResource) workingSetElement;
		}
		else
		{
			workingSetResource = (IResource) workingSetElement
					.getAdapter(IResource.class);
		}
		if (workingSetResource != null)
		{
			IPath resourcePath = workingSetResource.getFullPath();
			if (resourcePath.isPrefixOf(elementPath))
			{
				return true;
			}
			if (elementPath.isPrefixOf(resourcePath))
			{
				return true;
			}
		}
		return false;
	}


}
