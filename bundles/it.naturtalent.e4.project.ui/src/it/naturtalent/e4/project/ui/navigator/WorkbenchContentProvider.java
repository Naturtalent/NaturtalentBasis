package it.naturtalent.e4.project.ui.navigator;


import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.BaseWorkbenchContentProvider;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSetRoot;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.internal.resources.WorkspaceRoot;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkingSet;

public class WorkbenchContentProvider extends BaseWorkbenchContentProvider implements IResourceChangeListener
{

	private Viewer viewer;
	
	public static IWorkingSet [] newAssignedWorkingSets;
	
	//public static IWorkingSet [] oldAssignedWorkingSets;
	
	private WorkingSetManager workingSetManager = Activator.getWorkingSetManager();
		
	
	/*
	 * (non-Javadoc) Method declared on IContentProvider.
	 */
	public void dispose()
	{
		if (viewer != null)
		{
			IWorkspace workspace = null;
			Object obj = viewer.getInput();
			if (obj instanceof IWorkspace)
			{
				workspace = (IWorkspace) obj;
			}
			else if (obj instanceof IContainer)
			{
				workspace = ((IContainer) obj).getWorkspace();
			}
			if (workspace != null)
			{
				workspace.removeResourceChangeListener(this);
			}
		}

		super.dispose();
	}
	
	/*
	 * (non-Javadoc) Method declared on IContentProvider.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		super.inputChanged(viewer, oldInput, newInput);

		this.viewer = viewer;
		IWorkspace oldWorkspace = null;
		IWorkspace newWorkspace = null;
		

		if (oldInput instanceof IWorkspace)
		{
			oldWorkspace = (IWorkspace) oldInput;
		}
		else
		{
			if (oldInput instanceof IContainer)
			{
				oldWorkspace = ((IContainer) oldInput).getWorkspace();
			}
			else
			{
				if(oldInput instanceof WorkingSetRoot)
				{
					oldWorkspace = ResourcesPlugin.getWorkspace();
				}
			}
		}

		if (newInput instanceof IWorkspace)
		{
			newWorkspace = (IWorkspace) newInput;
		}
		else
		{
			if (newInput instanceof IContainer)
			{
				newWorkspace = ((IContainer) newInput).getWorkspace();
			}
			else
			{
				if(newInput instanceof WorkingSetRoot)
				{					
					newWorkspace = ResourcesPlugin.getWorkspace();
				}
			}
		}

		if (oldWorkspace != newWorkspace)
		{
			if (oldWorkspace != null)
			{
				oldWorkspace.removeResourceChangeListener(this);
			}
			if (newWorkspace != null)
			{
				newWorkspace.addResourceChangeListener(this,
						IResourceChangeEvent.POST_CHANGE);
			}
		}
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event)
	{
		processDelta(event.getDelta());		
	}

	/**
	 * Process the resource delta.
	 * 
	 * @param delta
	 */
	protected void processDelta(IResourceDelta delta)
	{
		Control ctrl = viewer.getControl();
		if (ctrl == null || ctrl.isDisposed())
		{
			return;
		}

		final Collection runnables = new ArrayList();
		processDelta(delta, runnables);

		if (runnables.isEmpty())
		{
			return;
		}

		// Are we in the UIThread? If so spin it until we are done
		if (ctrl.getDisplay().getThread() == Thread.currentThread())
		{
			runUpdates(runnables);
		}
		else
		{
			ctrl.getDisplay().asyncExec(new Runnable()
			{
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
				public void run()
				{
					// Abort if this happens after disposes
					Control ctrl = viewer.getControl();
					if (ctrl == null || ctrl.isDisposed())
					{
						return;
					}

					runUpdates(runnables);
				}
			});
		}
	}
	
	/**
	 * Run all of the runnables that are the widget updates
	 * @param runnables
	 */
	private void runUpdates(Collection runnables)
	{
		Iterator runnableIterator = runnables.iterator();
		while (runnableIterator.hasNext())
		{
			((Runnable) runnableIterator.next()).run();
		}

	}

	
	/**
	 * Process a resource delta. Add any runnables
	 */
	private void processDelta(IResourceDelta delta, Collection runnables)
	{
		// he widget may have been destroyed
		// by the time this is run. Check for this and do nothing if so.
		Control ctrl = viewer.getControl();
		if (ctrl == null || ctrl.isDisposed())
		{
			return;
		}

		// Get the affected resource
		final IResource resource = delta.getResource();
		
		// If any children have changed type, just do a full refresh of this
		// parent,
		// since a simple update on such children won't work,
		// and trying to map the change to a remove and add is too dicey.
		// The case is: folder A renamed to existing file B, answering yes to
		// overwrite B.
		IResourceDelta[] affectedChildren = delta
				.getAffectedChildren(IResourceDelta.CHANGED);
		for (int i = 0; i < affectedChildren.length; i++)
		{
			if ((affectedChildren[i].getFlags() & IResourceDelta.TYPE) != 0)
			{
				runnables.add(getRefreshRunnable(resource));
				return;
			}
		}
		
		// Opening a project just affects icon, but we need to refresh when
		// a project is closed because if child items have not yet been created
		// in the tree we still need to update the item's children
		int changeFlags = delta.getFlags();
		if ((changeFlags & IResourceDelta.OPEN) != 0)
		{
			if (resource.isAccessible())
			{
				runnables.add(getUpdateRunnable(resource));				
			}
			else
			{
				runnables.add(getRefreshRunnable(resource));
				return;
			}
		}
		// Check the flags for changes the Navigator cares about.
		// See ResourceLabelProvider for the aspects it cares about.
		// Notice we don't care about F_CONTENT or F_MARKERS currently.
		if ((changeFlags & (IResourceDelta.SYNC | IResourceDelta.TYPE | IResourceDelta.DESCRIPTION)) != 0)
		{
			runnables.add(getUpdateRunnable(resource));
		}
		// Replacing a resource may affect its label and its children
		if ((changeFlags & IResourceDelta.REPLACED) != 0)
		{
			runnables.add(getRefreshRunnable(resource));
			return;
		}
		
		// Aenderung an den Projektdaten wird angezeigt durch Ueberpruefung
		// auf Inhaltsaenderung im ProjektDatafile
		if((resource.getType() == IResource.FILE) && ((changeFlags & IResourceDelta.CONTENT) != 0))
		{
			IFile contentFile = (IFile) resource;
			if(contentFile.getName().equals(IProjectData.PROJECTDATAFILE))
			{
				IProject iProject = resource.getProject();				
				runnables.add(getUpdateRunnable(iProject));
				
				// Aenderungen an der WorkingSetzuordnung erforderlich
				if(ArrayUtils.isNotEmpty(newAssignedWorkingSets))
				{
					// alle bisherigen WorkingSetzuordnungen loesen
					IAdaptable[] adaptables = new IAdaptable[]{iProject};
					workingSetManager.removeWorkingSetsElements(adaptables);

					// neu zuordnen
					workingSetManager.addToWorkingSets(iProject,
							newAssignedWorkingSets);
					
					for(IWorkingSet workingSet : newAssignedWorkingSets)
						runnables.add(getRefreshWorkingSetRunnable(workingSet));
				}
				
				return;
			}
		}
		
		
		// Handle changed children .
		for (int i = 0; i < affectedChildren.length; i++)
		{
			processDelta(affectedChildren[i], runnables);
		}

		// @issue several problems here:
		// - should process removals before additions, to avoid multiple equal
		// elements in viewer
		// - Kim: processing removals before additions was the indirect cause of
		// 44081 and its varients
		// - Nick: no delta should have an add and a remove on the same element,
		// so processing adds first is probably OK
		// - using setRedraw will cause extra flashiness
		// - setRedraw is used even for simple changes
		// - to avoid seeing a rename in two stages, should turn redraw on/off
		// around combined removal and addition
		// - Kim: done, and only in the case of a rename (both remove and add
		// changes in one delta).

		IResourceDelta[] addedChildren = delta
				.getAffectedChildren(IResourceDelta.ADDED);
		IResourceDelta[] removedChildren = delta
				.getAffectedChildren(IResourceDelta.REMOVED);

		if (addedChildren.length == 0 && removedChildren.length == 0)
		{
			return;
		}
		
		// sind noch Nacharbeiten an einem neuerzeugten Projekt erforderlich	
		if (Activator.creatProjectAuxiliaryFlag)
		{
			for (int i = 0; i < addedChildren.length; i++)
			{
				IResource iResource = addedChildren[i].getResource();
				if (iResource.getType() == IResource.PROJECT)
				{
					IProject iAddedProject = (IProject) iResource;

					// das neue Projekt in allen zugeordneten WorkingSets
					// eintragen
					if (!ArrayUtils.isEmpty(newAssignedWorkingSets))
						workingSetManager.addToWorkingSets(iAddedProject,
								newAssignedWorkingSets);
					else
					{
						// im WorkingSet 'Andere' eintragen
						IWorkingSet andere = workingSetManager.getWorkingSet(IWorkingSetManager.OTHER_WORKINGSET_NAME);
						if(andere != null)
							workingSetManager.addToWorkingSets(iAddedProject,
									new IWorkingSet[]{andere});
					}

					// Aliasname (der eigentliche Projektname) als Projekteigenschaft speichern
					String projectAliasName = Activator.newlyCreatedProjectMap
							.get(iAddedProject.getName());					
					if (StringUtils.isNotEmpty(projectAliasName))
					{
						// den Aliasnamen zuordnen - (provoziert einen erneuten
						// 'processDelta')
						new NtProject(iAddedProject).setName(projectAliasName);
					}
				}
			}			
		}	
		Activator.creatProjectAuxiliaryFlag = false;
		
		
		final Object[] addedObjects;
		final Object[] removedObjects;

		// Process additions before removals as to not cause selection
		// preservation prior to new objects being added
		// Handle added children. Issue one update for all insertions.
		int numMovedFrom = 0;
		int numMovedTo = 0;
		if (addedChildren.length > 0)
		{
			addedObjects = new Object[addedChildren.length];
			for (int i = 0; i < addedChildren.length; i++)
			{
				addedObjects[i] = addedChildren[i].getResource();
				if ((addedChildren[i].getFlags() & IResourceDelta.MOVED_FROM) != 0)
				{
					++numMovedFrom;
				}				
			}
		}
		else
		{
			addedObjects = new Object[0];
		}

		// Handle removed children. Issue one update for all removals.
		if (removedChildren.length > 0)
		{
			removedObjects = new Object[removedChildren.length];
			for (int i = 0; i < removedChildren.length; i++)
			{
				removedObjects[i] = removedChildren[i].getResource();
				if ((removedChildren[i].getFlags() & IResourceDelta.MOVED_TO) != 0)
				{
					++numMovedTo;
				}
			}
		}
		else
		{
			removedObjects = new Object[0];
		}
		// heuristic test for items moving within same folder (i.e. renames)
		final boolean hasRename = numMovedFrom > 0 && numMovedTo > 0;

		Runnable addAndRemove = new Runnable()
		{
			public void run()
			{
				if (viewer instanceof AbstractTreeViewer)
				{
					AbstractTreeViewer treeViewer = (AbstractTreeViewer) viewer;
					// Disable redraw until the operation is finished so we
					// don't
					// get a flash of both the new and old item (in the case of
					// rename)
					// Only do this if we're both adding and removing files (the
					// rename case)
					if (hasRename)
					{
						treeViewer.getControl().setRedraw(false);
					}
					try
					{
						// add
						if (addedObjects.length > 0)
						{		
							treeViewer.add(resource, addedObjects);
							
							// im Anzeigemodus 'WorkingSet' refresh fuer alle WorkingSets 
							if (treeViewer.getInput() instanceof WorkingSetRoot)
							{
								IWorkingSet[] workingSets = workingSetManager
										.getWorkingSets();
								for (IWorkingSet workingSet : workingSets)
									((TreeViewer) viewer).refresh(workingSet);
							}		
						}
						
						// delete
						if (removedObjects.length > 0)
						{										
							treeViewer.remove(removedObjects);							
							
							// im Anzeigemodus 'WorkingSet' refresh fuer alle WorkingSets 
							if (treeViewer.getInput() instanceof WorkingSetRoot)
							{
								IWorkingSet[] workingSets = workingSetManager
										.getWorkingSets();
								for (IWorkingSet workingSet : workingSets)
									((TreeViewer) viewer).refresh(workingSet);
							}							
						}
					} finally
					{
						if (hasRename)
						{
							treeViewer.getControl().setRedraw(true);
						}
					}
				}
				else
				{
					((StructuredViewer) viewer).refresh(resource);
				}
			}
		};
		runnables.add(addAndRemove);
	}

	/**
	 * Return a runnable for refreshing a resource.
	 * @param resource
	 * @return Runnable
	 */
	private Runnable getRefreshWorkingSetRunnable(final IWorkingSet workingSet)
	{
		return new Runnable()
		{
			public void run()
			{
				IWorkingSet [] workingSets = workingSetManager.getWorkingSets();
				for(IWorkingSet workingSet : workingSets)
					((StructuredViewer) viewer).refresh(workingSet);
			}
		};
	}


	
	/**
	 * Return a runnable for refreshing a resource.
	 * @param resource
	 * @return Runnable
	 */
	private Runnable getRefreshRunnable(final IResource resource)
	{
		return new Runnable()
		{
			public void run()
			{
				((StructuredViewer) viewer).refresh(resource);
			}
		};
	}

		/**
		 * Return a runnable for refreshing a resource.
		 * @param resource
		 * @return Runnable
		 */
	private Runnable getUpdateRunnable(final IResource resource)
	{
		return new Runnable()
		{
			public void run()
			{
				((StructuredViewer) viewer).update(resource, null);
			}
		};
	}

}
