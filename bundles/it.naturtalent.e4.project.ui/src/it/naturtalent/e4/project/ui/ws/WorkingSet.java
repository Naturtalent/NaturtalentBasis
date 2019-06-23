package it.naturtalent.e4.project.ui.ws;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.WorkbenchImages;
import it.naturtalent.e4.project.ui.model.ResourceFactory;
import it.naturtalent.e4.project.ui.utils.Util;

/**
 * A working set holds a number of IAdaptable elements. A working set is
 * intended to group elements for presentation to the user or for operations on
 * a set of elements.
 * 
 * @see org.eclipse.ui.IWorkingSet
 * @since 2.0
 */
public class WorkingSet extends AbstractWorkingSet
{
	private static final String DEFAULT_ID = "org.eclipse.ui.resourceWorkingSetPage"; //$NON-NLS-1$
		
	public static boolean DEFAULT = false;
	public static boolean DEBUG_WORKING_SETS = DEFAULT;

	private String editPageId;
	
	private Log log = LogFactory.getLog(WorkingSet.class);

	/**
	 * Creates a new working set.
	 * 
	 * @param name
	 *            the name of the new working set. Should not have leading or
	 *            trailing whitespace.
	 * @param label
	 *            the label of the new working set
	 * @param elements
	 *            the content of the new working set. May be empty but not
	 *            <code>null</code>.
	 */
	public WorkingSet(String name, String label, IAdaptable[] elements)
	{
		super(name, label);
		internalSetElements(elements);
	}

	/**
	 * Creates a new working set from a memento.
	 * 
	 * @param name
	 *            the name of the new working set. Should not have leading or
	 *            trailing whitespace.
	 * @param memento
	 *            persistence memento containing the elements of the working
	 *            set.
	 */
	protected WorkingSet(String name, String label, IMemento memento)
	{
		super(name, label);
		workingSetMemento = memento;
		if (workingSetMemento != null)
		{
			String uniqueId = workingSetMemento
					.getString(IWorkbenchConstants.TAG_ID);
			if (uniqueId != null)
			{
				setUniqueId(uniqueId);
			}
		}
	}

	/**
	 * Tests the receiver and the object for equality
	 * 
	 * @param object
	 *            object to compare the receiver to
	 * @return true=the object equals the receiver, the name is the same. false
	 *         otherwise
	 */
	public boolean equals(Object object)
	{
		if (this == object)
		{
			return true;
		}
		if (object instanceof WorkingSet)
		{
			WorkingSet workingSet = (WorkingSet) object;
			return Util.equals(workingSet.getName(), getName())
					&& Util.equals(workingSet.getElementsArray(),
							getElementsArray())
					&& Util.equals(workingSet.getId(), getId());
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */	
	public boolean isEditable()
	{
		WorkingSetDescriptor descriptor = getDescriptor(null);
		return descriptor != null && descriptor.isEditable();
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkingSet
	 */
	public String getId()
	{
		return editPageId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkingSet#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor()
	{
		
		try
		{				 
			//URL url = new URL("platform:/plugin/it.naturtalent.e4.login/icons/eye.png");
			URL url = new URL(Activator.PLATFORM_PREFIX + "/"
					+ Activator.PLUGIN_ID 
					+ Activator.ICONS_RESOURCE_FOLDER + "/"
					+ WorkbenchImages.IMG_WORKINGSETS);
			return ImageDescriptor.createFromURL(url);			
			
		} catch (Exception e)
		{			
			e.printStackTrace();
		}
		
		/*
		return ResourceManager.getPluginImageDescriptor(Activator.PLUGIN_ID,
				Activator.ICONS_RESOURCE_FOLDER + File.separator
						+ WorkbenchImages.IMG_WORKINGSETS);
						*/
		
		return null;
	}

	/**
	 * Returns the hash code.
	 * 
	 * @return the hash code.
	 */
	public int hashCode()
	{
		int hashCode = getName().hashCode();

		if (editPageId != null)
		{
			hashCode &= editPageId.hashCode();
		}
		return hashCode;
	}

	/**
	 * Recreates the working set elements from the persistence memento.
	 */
	void restoreWorkingSet()
	{
		IMemento[] itemMementos = workingSetMemento
				.getChildren(IWorkbenchConstants.TAG_ITEM);
		final Set items = new HashSet();
		for (int i = 0; i < itemMementos.length; i++)
		{
			final IMemento itemMemento = itemMementos[i];
			final String factoryID = itemMemento
					.getString(IWorkbenchConstants.TAG_FACTORY_ID);

			if (factoryID == null)
			{
				log.error("Unable to restore working set item - no factory ID."); //$NON-NLS-1$
				continue;
			}
			
			/*
			final IElementFactory factory = PlatformUI.getWorkbench()
					.getElementFactory(factoryID);
					*/

			// auf ResourceFactory festlegen
			final IElementFactory factory = new ResourceFactory();
			
			if (factory == null)
			{
				log.error("Unable to restore working set item - cannot instantiate factory: " + factoryID); //$NON-NLS-1$
				continue;
			}
			SafeRunner
					.run(new SafeRunnable(
							"Unable to restore working set item - exception while invoking factory: " + factoryID) { //$NON-NLS-1$

						public void run() throws Exception
						{
							IAdaptable item = factory
									.createElement(itemMemento);
							
							if (item == null)
							{
								if (DEBUG_WORKING_SETS)
									log.error("Unable to restore working set item - cannot instantiate item: " + factoryID); //$NON-NLS-1$

							}
							else
								items.add(item);
						}
					});
		}
		internalSetElements((IAdaptable[]) items.toArray(new IAdaptable[items
				.size()]));
	}

	/**
	 * Implements IPersistableElement. Persist the working set name and working
	 * set contents. The contents has to be either IPersistableElements or
	 * provide adapters for it to be persistent.
	 * 
	 * @see org.eclipse.ui.IPersistableElement#saveState(IMemento)
	 */
	public void saveState(IMemento memento)
	{
		if (workingSetMemento != null)
		{
			// just re-save the previous memento if the working set has
			// not been restored
			memento.putMemento(workingSetMemento);
		}
		else
		{
			memento.putString(IWorkbenchConstants.TAG_NAME, getName());
			memento.putString(IWorkbenchConstants.TAG_LABEL, getLabel());
			memento.putString(IWorkbenchConstants.TAG_ID, getUniqueId());
			memento.putString(IWorkbenchConstants.TAG_EDIT_PAGE_ID, editPageId);
			Iterator iterator = elements.iterator();
			while (iterator.hasNext())
			{
				IAdaptable adaptable = (IAdaptable) iterator.next();
				
				final IPersistableElement persistable = (IPersistableElement) Util
						.getAdapter(adaptable, IPersistableElement.class);
				
				
				if (persistable != null)
				{
					final IMemento itemMemento = memento
							.createChild(IWorkbenchConstants.TAG_ITEM);

					itemMemento.putString(IWorkbenchConstants.TAG_FACTORY_ID,
							persistable.getFactoryId());
					SafeRunner
							.run(new SafeRunnable(
									"Problems occurred while saving persistable item state") { //$NON-NLS-1$

								public void run() throws Exception
								{
									persistable.saveState(itemMemento);
								}
							});
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkingSet
	 */
	public void setElements(IAdaptable[] newElements)
	{
		internalSetElements(newElements);
		fireWorkingSetChanged(
				IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkingSet
	 */
	public void setId(String pageId)
	{
		editPageId = pageId;
	}

	public boolean isVisible()
	{
		return true;
	}

	public boolean isSelfUpdating()
	{
		WorkingSetDescriptor descriptor = getDescriptor(null);
		return descriptor != null && descriptor.getUpdaterClassName() != null;
	}

	public boolean isAggregateWorkingSet()
	{
		return false;
	}

	/**
	 * Return the working set descriptor for this working set.
	 * 
	 * @param defaultId
	 *            the default working set type ID to use if this set has no
	 *            defined type
	 * @return the descriptor for this working set or <code>null</code> if it
	 *         cannot be determined
	 * @since 3.3
	 */
	private WorkingSetDescriptor getDescriptor(String defaultId)
	{
		/*
		WorkingSetRegistry registry = WorkbenchPlugin.getDefault()
				.getWorkingSetRegistry();
		String id = getId();
		if (id == null)
			id = defaultId;
		if (id == null)
			return null;

		return registry.getWorkingSetDescriptor(id);
		*/
		return null;
	}
 

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkingSet#adaptElements(org.eclipse.core.runtime.IAdaptable
	 * [])
	 */
	public IAdaptable[] adaptElements(IAdaptable[] objects)
	{
		IWorkingSetManager manager = getManager();
		if (manager instanceof WorkingSetManager)
		{
			WorkingSetDescriptor descriptor = getDescriptor(null);
			if (descriptor == null || !descriptor.isElementAdapterClassLoaded())
				return objects;
			return ((WorkingSetManager) manager).getElementAdapter(descriptor)
					.adaptElements(this, objects);
		}
		return objects;
	}
}
