/**
 * ViewRegistration.java
 */
package it.naturtalent.e4.project.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.util.IPropertyChangeListener;

/**
 * Register the interface {@link IWorkspaceModifyListener}.
 * 
 * @author Dieter Apel
 * 
 */
public class WorkspaceModifyListenerFactory
{
	private static WorkspaceModifyListenerFactory instance;
	private Collection<IWorkspaceModifyListener> listeners;

		
	/**
	 * Rückgabe einer Instance
	 * 
	 * @return
	 */
	public static WorkspaceModifyListenerFactory getInstance()
	{
		if (instance == null)
		{
			instance = new WorkspaceModifyListenerFactory();
		}
		return instance;
	}

	/**
	 * Konstruktion (private - konkrete Instance über 'getInstance()')
	 */
	private WorkspaceModifyListenerFactory()
	{
		listeners = new ArrayList<IWorkspaceModifyListener>();
	}

	public void addListener(IWorkspaceModifyListener listener)
	{
		if (listener != null)
		{
			listeners.add(listener);
		}
	}

	public void removeListener(IWorkspaceModifyListener listener)
	{
		if (listener != null)
		{
			listeners.remove(listener);
		}
	}

	public Iterator<IWorkspaceModifyListener> getListeners()
	{
		return listeners.iterator();
	}
	

}
