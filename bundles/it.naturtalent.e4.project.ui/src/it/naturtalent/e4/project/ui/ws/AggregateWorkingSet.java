package it.naturtalent.e4.project.ui.ws;

import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.WorkbenchImages;
import it.naturtalent.e4.project.ui.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IAggregateWorkingSet;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkingSet;


/**
 * 
 * @since 3.2
 */
public class AggregateWorkingSet extends AbstractWorkingSet implements
		IAggregateWorkingSet, IPropertyChangeListener
{
	
	public static final String TAG_ID = "id"; //$NON-NLS-1$
	public static final String IMG_OBJ_WORKING_SETS = "IMG_OBJ_WORKING_SETS"; //$NON-NLS-1$
	public static final String TAG_NAME = "name"; //$NON-NLS-1$
    public static final String TAG_LABEL = "label"; //$NON-NLS-1$
    public static final String TAG_WORKING_SET = "workingSet"; //$NON-NLS-1$		
    
	private IWorkingSet[] components;
	
	private Log log = LogFactory.getLog(AggregateWorkingSet.class);

	/**
	 * Prevents stack overflow on cyclic element inclusions.
	 */
	private boolean inElementConstruction = false;

	/**
	 * 
	 * @param name
	 * @param label
	 * @param components
	 */
	public AggregateWorkingSet(String name, String label,
			IWorkingSet[] components)
	{
		super(name, label);

		if (components != null)
		{
			IWorkingSet[] componentCopy = new IWorkingSet[components.length];
			System.arraycopy(components, 0, componentCopy, 0, components.length);
			internalSetComponents(componentCopy);
			constructElements(false);
		}
	}

	/**
	 * 
	 * @param name
	 * @param label
	 * @param memento
	 */
	public AggregateWorkingSet(String name, String label, IMemento memento)
	{
		super(name, label);
		workingSetMemento = memento;
		if (workingSetMemento != null)
		{
			String uniqueId = workingSetMemento
					.getString(TAG_ID);
			if (uniqueId != null)
			{
				setUniqueId(uniqueId);
			}
		}
	}

	void setComponents(IWorkingSet[] components)
	{
		internalSetComponents(components);
		constructElements(true);
	}

	private void internalSetComponents(IWorkingSet[] components)
	{
		this.components = components;
	}

	/**
	 * Takes the elements from all component working sets and sets them to be
	 * the elements of this working set. Any duplicates are trimmed.
	 * 
	 * @param fireEvent
	 *            whether a working set change event should be fired
	 */
	private void constructElements(boolean fireEvent)
	{
		if (inElementConstruction)
		{
			String msg = NLS.bind(Messages.ProblemCyclicDependency,
					getName());
			log.error(msg);
			throw new IllegalStateException(msg);
		}
		inElementConstruction = true;
		try
		{
			Set elements = new HashSet();
			IWorkingSet[] localComponents = getComponentsInternal();
			for (int i = 0; i < localComponents.length; i++)
			{
				IWorkingSet workingSet = localComponents[i];
				try
				{
					IAdaptable[] componentElements = workingSet.getElements();
					elements.addAll(Arrays.asList(componentElements));
				} catch (IllegalStateException e)
				{ // an invalid component; remove it
					IWorkingSet[] tmp = new IWorkingSet[components.length - 1];
					if (i > 0)
						System.arraycopy(components, 0, tmp, 0, i);
					if (components.length - i - 1 > 0)
						System.arraycopy(components, i + 1, tmp, i,
								components.length - i - 1);
					components = tmp;
					workingSetMemento = null; // toss cached info
					fireWorkingSetChanged(
							IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE,
							null);
					continue;
				}
			}
			internalSetElements((IAdaptable[]) elements
					.toArray(new IAdaptable[elements.size()]));
			if (fireEvent)
			{
				fireWorkingSetChanged(
						IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE,
						null);
			}
		} finally
		{
			inElementConstruction = false;
		}
	}

	public String getId()
	{
		return null;
	}

	public ImageDescriptor getImageDescriptor()
	{
		return WorkbenchImages
				.getImageDescriptor(IMG_OBJ_WORKING_SETS);
	}

	/**
	 * A no-op for aggregates - their contents should be derived.
	 */
	public void setElements(IAdaptable[] elements)
	{
	}

	public void setId(String id)
	{

	}

	/**
	 * Aggregates are not editable.
	 */
	public boolean isEditable()
	{
		return false;
	}

	/**
	 * Aggregates should not generally be visible in the UI.
	 */
	public boolean isVisible()
	{
		return false;
	}

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
			memento.putString(TAG_NAME, getName());
			memento.putString(TAG_LABEL, getLabel());
			memento.putString(TAG_ID, getUniqueId());
			memento.putString(AbstractWorkingSet.TAG_AGGREGATE,
					Boolean.TRUE.toString());

			IWorkingSet[] localComponents = getComponentsInternal();
			for (int i = 0; i < localComponents.length; i++)
			{
				IWorkingSet componentSet = localComponents[i];
				memento.createChild(TAG_WORKING_SET,
						componentSet.getName());
			}
		}
	}

	public void connect(IWorkingSetManager manager)
	{
		manager.addPropertyChangeListener(this);
		super.connect(manager);
	}

	public void disconnect()
	{
		IWorkingSetManager connectedManager = getManager();
		if (connectedManager != null)
			connectedManager.removePropertyChangeListener(this);
		super.disconnect();
	}

	/**
	 * Return the component working sets.
	 * 
	 * @return the component working sets
	 */
	public IWorkingSet[] getComponents()
	{
		IWorkingSet[] localComponents = getComponentsInternal();
		IWorkingSet[] copiedArray = new IWorkingSet[localComponents.length];
		System.arraycopy(localComponents, 0, copiedArray, 0,
				localComponents.length);
		return copiedArray;
	}

	private IWorkingSet[] getComponentsInternal()
	{
		if (components == null)
		{
			restoreWorkingSet();
			workingSetMemento = null;
		}
		return components;
	}

	public void propertyChange(PropertyChangeEvent event)
	{
		String property = event.getProperty();
		if (property.equals(IWorkingSetManager.CHANGE_WORKING_SET_REMOVE))
		{
			IWorkingSet[] localComponents = getComponentsInternal();
			for (int i = 0; i < localComponents.length; i++)
			{
				IWorkingSet set = localComponents[i];
				if (set.equals(event.getOldValue()))
				{
					IWorkingSet[] newComponents = new IWorkingSet[localComponents.length - 1];
					Util.arrayCopyWithRemoval(localComponents, newComponents, i);
					setComponents(newComponents);
				}
			}
		}
		else if (property
				.equals(IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE))
		{
			IWorkingSet[] localComponents = getComponentsInternal();
			for (int i = 0; i < localComponents.length; i++)
			{
				IWorkingSet set = localComponents[i];
				if (set.equals(event.getNewValue()))
				{
					constructElements(true);
					break;
				}
			}
		}
	}

	void restoreWorkingSet()
	{
		IWorkingSetManager manager = getManager();
		if (manager == null)
		{
			throw new IllegalStateException();
		}
		IMemento[] workingSetReferences = workingSetMemento
				.getChildren(TAG_WORKING_SET);
		ArrayList list = new ArrayList(workingSetReferences.length);

		for (int i = 0; i < workingSetReferences.length; i++)
		{
			IMemento setReference = workingSetReferences[i];
			String setId = setReference.getID();
			IWorkingSet set = manager.getWorkingSet(setId);
			if (set != null)
			{
				list.add(set);
			}
		}
		internalSetComponents((IWorkingSet[]) list.toArray(new IWorkingSet[list
				.size()]));
		constructElements(false);
	}

	public boolean equals(Object object)
	{
		if (this == object)
		{
			return true;
		}
		if (object instanceof AggregateWorkingSet)
		{
			AggregateWorkingSet workingSet = (AggregateWorkingSet) object;

			return Util.equals(workingSet.getName(), getName())
					&& Util.equals(workingSet.getComponentsInternal(),
							getComponentsInternal());
		}
		return false;
	}

	public int hashCode()
	{
		int hashCode = getName().hashCode()
				& getComponentsInternal().hashCode();
		return hashCode;
	}

	public boolean isSelfUpdating()
	{
		IWorkingSet[] localComponents = getComponentsInternal();
		if (localComponents == null || localComponents.length == 0)
		{
			return false;
		}
		for (int i = 0; i < localComponents.length; i++)
		{
			if (!localComponents[i].isSelfUpdating())
			{
				return false;
			}
		}
		return true;
	}

	public boolean isAggregateWorkingSet()
	{
		return true;
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
		return new IAdaptable[0];
	}
}
