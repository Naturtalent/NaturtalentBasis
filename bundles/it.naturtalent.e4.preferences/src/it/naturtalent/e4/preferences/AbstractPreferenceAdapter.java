package it.naturtalent.e4.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.widgets.Composite;

import it.naturtalent.application.IPreferenceAdapter;
import it.naturtalent.application.IPreferenceNode;


public abstract class AbstractPreferenceAdapter implements IPreferenceAdapter
{	
	protected IEclipsePreferences defaultPreferenceNode;
	protected IEclipsePreferences instancePreferenceNode;	
	protected Composite composite = null;
	
	@Override
	public abstract String getLabel();

	//@Override
	//public abstract String getKey();

	@Override
	public String getNodePath()
	{		
		return null;
	}

	@Override
	public abstract void restoreDefaultPressed();

	@Override
	public abstract void appliedPressed();

	@Override
	public void okPressed()
	{
		appliedPressed();
	}

	@Override
	public Composite createNodeComposite(IPreferenceNode referenceNode)
	{
		referenceNode.setTitle(getLabel());		
		return composite;
	}


	@Override
	public IEclipsePreferences getDefaultPreference()
	{		
		return defaultPreferenceNode;
	}

	@Override
	public void setDefaultPreference(IEclipsePreferences defaultPreference)
	{
		this.defaultPreferenceNode = defaultPreference;
		
	}

	@Override
	public IEclipsePreferences getInstancePreference()
	{		
		return instancePreferenceNode;
	}

	@Override
	public void setInstancePreference(IEclipsePreferences instancePreference)
	{
		this.instancePreferenceNode = instancePreference;		
	}
}
