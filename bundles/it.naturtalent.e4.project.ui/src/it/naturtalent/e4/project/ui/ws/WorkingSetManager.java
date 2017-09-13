package it.naturtalent.e4.project.ui.ws;

import it.naturtalent.e4.project.ui.Activator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.dialogs.IWorkingSetEditWizard;
import org.eclipse.ui.dialogs.IWorkingSetNewWizard;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;
import org.eclipse.ui.internal.IWorkbenchConstants;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * A working set manager stores working sets and provides property change
 * notification when a working set is added or removed. Working sets are
 * persisted whenever one is added or removed.
 * 
 * @see IWorkingSetManager
 * @since 2.0
 */
public class WorkingSetManager extends AbstractWorkingSetManager implements
		IWorkingSetManager, BundleListener
{

	// Working set persistence
	public static final String WORKING_SET_STATE_FILENAME = "workingsets.xml"; //$NON-NLS-1$

	public WorkingSetManager(BundleContext context)
	{
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkingSetManager
	 */
	public void addRecentWorkingSet(IWorkingSet workingSet)
	{
		internalAddRecentWorkingSet(workingSet);
		saveState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkingSetManager
	 */
	public void addWorkingSet(IWorkingSet workingSet)
	{
		super.addWorkingSet(workingSet);
		saveState();
	}

	/**
	 * Returns the file used as the persistence store, or <code>null</code> if
	 * there is no available file.
	 * 
	 * @return the file used as the persistence store, or <code>null</code>
	 */
	private File getWorkingSetStateFile()
	{
		
		IPath path = WorkbenchPlugin.getDefault().getDataLocation();
		if (path == null)
		{
			return null;
		}
		path = path.append(WORKING_SET_STATE_FILENAME);
		return path.toFile();
		
		
		//return bundleContext.getDataFile(WORKING_SET_STATE_FILENAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkingSetManager
	 */
	public void removeWorkingSet(IWorkingSet workingSet)
	{
		if (internalRemoveWorkingSet(workingSet))
		{
			saveState();
		}
	}

	/**
	 * Reads the persistence store and creates the working sets stored in it.
	 */
	public void restoreState()
	{
		File stateFile = getWorkingSetStateFile();

		if (stateFile != null && stateFile.exists())
		{
			try
			{
				FileInputStream input = new FileInputStream(stateFile);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input, "utf-8")); //$NON-NLS-1$

				IMemento memento = XMLMemento.createReadRoot(reader);
				
				restoreWorkingSetState(memento);
				restoreMruList(memento);
				reader.close();
			} catch (IOException e)
			{
				handleInternalError(
						e,
						WorkbenchMessages.ProblemRestoringWorkingSetState_title,
						WorkbenchMessages.ProblemRestoringWorkingSetState_message);
			} catch (WorkbenchException e)
			{
				handleInternalError(
						e,
						WorkbenchMessages.ProblemRestoringWorkingSetState_title,
						WorkbenchMessages.ProblemRestoringWorkingSetState_message);
			}
		}
	}	

	/**
	 * Saves the working sets in the persistence store
	 */
	private void saveState()
	{
		File stateFile = getWorkingSetStateFile();
		if (stateFile == null)
		{
			return;
		}
		try
		{
			saveState(stateFile);
		} catch (IOException e)
		{
			stateFile.delete();
			handleInternalError(e,
					WorkbenchMessages.ProblemSavingWorkingSetState_title,
					WorkbenchMessages.ProblemSavingWorkingSetState_message);
		}		
	}

	/**
	 * Persists all working sets and fires a property change event for the
	 * changed working set. Should only be called by
	 * org.eclipse.ui.internal.WorkingSet.
	 * 
	 * @param changedWorkingSet
	 *            the working set that has changed
	 * @param propertyChangeId
	 *            the changed property. one of CHANGE_WORKING_SET_CONTENT_CHANGE
	 *            and CHANGE_WORKING_SET_NAME_CHANGE
	 */
	public void workingSetChanged(IWorkingSet changedWorkingSet,
			String propertyChangeId, Object oldValue)
	{
		saveState();
		super.workingSetChanged(changedWorkingSet, propertyChangeId, oldValue);
	}

	/**
	 * Show and Log the exception using StatusManager.
	 */
	private void handleInternalError(Exception exp, String title, String message)
	{
		Status status = new Status(IStatus.ERROR, WorkbenchPlugin.PI_WORKBENCH,
				message, exp);
		StatusAdapter sa = new StatusAdapter(status);
		sa.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, title);
		StatusManager.getManager().handle(sa,
				StatusManager.SHOW | StatusManager.LOG);
	}

	@Override
	public IWorkingSet availableResourceWorkingSet()
	{
		IWorkingSet availResourcesWorkingSet = null;

		// WorkingSet mit den 'available' Resourcen erzeugen
		IWorkingSet[] allWorkingSets = Activator.getWorkingSetManager()
				.getAllWorkingSets();
				
		// die Elemente aller WS in einer Liste zusammenfassen
		List<IAdaptable> all = new ArrayList<IAdaptable>();
		for (IWorkingSet set : allWorkingSets)
		{			
			if (!StringUtils.startsWith(set.getName(), IWorkingSetManager.TAG_WINDOW_AGGREGATE))
			{
				IAdaptable[] elements = set.getElements();
				Collections.addAll(all, elements);
			}
		}
		
		// sicherstellen, dass keine Elemente doppelt vorkommen
		List<IAdaptable> allAdaptable = new ArrayList<IAdaptable>(
				new HashSet<IAdaptable>(all));
		
		// WS mit den aggregierten Elementen erzeugen
		IAdaptable[] iAdaptables = allAdaptable.toArray(new IAdaptable[allAdaptable.size()]);		
		availResourcesWorkingSet = Activator.getWorkingSetManager()
				.createWorkingSet("allAvailResources", iAdaptables); //$NON-NLS-1$

		return availResourcesWorkingSet;
	}
	

	public void removeWorkingSetElements(IWorkingSet workingSet,
			IAdaptable [] adaptables)
	{		
		IAdaptable[] adapts = (IAdaptable[]) workingSet.getElements();
		
		for(IAdaptable iAdaptable : adaptables)
			adapts = ArrayUtils.removeElement(adapts, iAdaptable);

		workingSet.setElements(adapts);
	}


	public void removeWorkingSetsElements(IAdaptable [] adaptables)
	{
		for(IWorkingSet workingSet : getWorkingSets())
		{
			IAdaptable [] elements = workingSet.getElements();
			removeWorkingSetElements(workingSet, adaptables);
		}
	}
	


	/*
	 * Es wird ueberprueft, ob die uebergbenen Elemente in einem WorkingSet enthalten ist. Wenn
	 * nicht, wird dies dem WorkingSet 'Others' zugeordnet.
	 * 
	 */
	public void updateOthers() 
	{	
		// alle vorhandenen Projecte 
		IAdaptable[] allAdaptables = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		if(allAdaptables == null)
			return;
		
		// alle WorkingSet-Elemente zusammenfassen (doppelte werden ausgeblendet)
		HashSet<IAdaptable>allWorkingSetElements = new HashSet<IAdaptable>();
		IWorkingSet [] workingSets = getWorkingSets();				
		for(IWorkingSet workingSet : workingSets)
		{
			if(!StringUtils.equals(workingSet.getName(), IWorkingSetManager.OTHER_WORKINGSET_NAME))
			{
				IAdaptable [] wsElements = workingSet.getElements();
				for(IAdaptable element : wsElements)
					allWorkingSetElements.add(element);
			}
		}
		
		// die nicht in WorkingSets gebundenen Elemente separieren (Others)
		List<IAdaptable>lOthers = new ArrayList<IAdaptable>();
		for(IAdaptable element : allAdaptables)
		{
			if(!allWorkingSetElements.contains(element))
				lOthers.add(element);
		}
		IAdaptable[] adaptOthers = lOthers.toArray(new IAdaptable[lOthers
				.size()]);

		// 'Others' in separaten WorkingSet zusammenfassen
		IWorkingSet otherSet = getWorkingSet(IWorkingSetManager.OTHER_WORKINGSET_NAME);
		if(otherSet == null)
		{
			otherSet = new WorkingSet(IWorkingSetManager.OTHER_WORKINGSET_NAME,
					IWorkingSetManager.OTHER_WORKINGSET_NAME, adaptOthers);
			addWorkingSet(otherSet);
		}
		else		
			otherSet.setElements(adaptOthers);				
	}
	
    /**
     * Gibt die Namen aller WorkingSets zurueck, die unter 'aggregateWorkingSet' gespeichert
     * sind.
     * 
     * @param aggregateWorkingSet
     * @return
     */
	@Override
    public String [] getAggregateWorkingSetNames(String aggregateWorkingSet)
	{
		String[] aggregateWorkingSetNames = null;
	
		if (StringUtils.isNotEmpty(aggregateWorkingSet))
		{
			File stateFile = getWorkingSetStateFile();
	
			if (stateFile != null && stateFile.exists())
			{
				try
				{
					FileInputStream input = new FileInputStream(stateFile);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(input, "utf-8")); //$NON-NLS-1$
	
					IMemento[] workingSetReferences = XMLMemento
							.createReadRoot(reader).getChildren(
									IWorkbenchConstants.TAG_WORKING_SET);
	
					if (workingSetReferences != null)
					{
						for (IMemento memento : workingSetReferences)
						{
							String workingSetEditPageId = memento
									.getString(IWorkbenchConstants.TAG_EDIT_PAGE_ID);
							String aggregateString = memento
									.getString(AbstractWorkingSet.TAG_AGGREGATE);
							boolean isAggregate = aggregateString != null
									&& Boolean.valueOf(aggregateString)
											.booleanValue();
	
							if (isAggregate)
							{
								String name = memento
										.getString(IWorkbenchConstants.TAG_NAME);
								
								if (StringUtils.equals(name,
										aggregateWorkingSet))
								{
									IMemento[] aggregatMmementos = memento
											.getChildren();
									if (!ArrayUtils.isEmpty(aggregatMmementos))
									{
										for (IMemento aggregateMemento : aggregatMmementos)
										{
											String id = aggregateMemento
													.getID();
											if (id != null)
												aggregateWorkingSetNames = ArrayUtils
														.add(aggregateWorkingSetNames,
																id);
										}
										break;
									}
								}
							}
						}
					}
					reader.close();
	
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return aggregateWorkingSetNames;
	}
	
	public List<IWorkingSet> getAssignedWorkingSets(IAdaptable adaptable)
	{
		List<IWorkingSet>assignedWorkingSets = new ArrayList<IWorkingSet>();
		
		IWorkingSet[] workingSets = getWorkingSets(); 			
		for (IWorkingSet workingSet : workingSets)
		{
			// 'Other' - WorkingSet ausblenden
			String wsName = workingSet.getName();
			if (StringUtils.equals(wsName,IWorkingSetManager.OTHER_WORKINGSET_NAME))
				continue;

			// die Elemente des WorkingSets ueberpruefen
			IAdaptable[] adaptables = workingSet.getElements();
			if (ArrayUtils.contains(adaptables, adaptable))
				assignedWorkingSets.add(workingSet);
		}						

		return assignedWorkingSets;
	}

	@Override
	public void bundleChanged(BundleEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public IWorkingSetEditWizard createWorkingSetEditWizard(
			IWorkingSet workingSet)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkingSetNewWizard createWorkingSetNewWizard(String[] workingSetIds)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkingSetSelectionDialog createWorkingSetSelectionDialog(
			Shell parentShell, boolean multi, String[] workingsSetIds)
	{
		// TODO Auto-generated method stub
		return null;
	}


}
