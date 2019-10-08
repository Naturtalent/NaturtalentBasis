package it.naturtalent.e4.update;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.prefs.BackingStoreException;

import it.naturtalent.application.IPreferenceNode;
import it.naturtalent.e4.preferences.AbstractPreferenceAdapter;
import it.naturtalent.e4.preferences.ListEditorComposite;
import it.naturtalent.e4.project.ui.NtPreferences;

public class UpdatePreferenceAdapter extends AbstractPreferenceAdapter
{
	
	// key Install-/Update Site - Locatins
	public static final String UPDATESITE_LOCATION_PREFERENCE = "updatesitelocationpreference"; //$NON-NLS-1$
	
	private Log log = LogFactory.getLog(this.getClass());
		
	public UpdatePreferenceAdapter()
	{
		instancePreferenceNode = InstanceScope.INSTANCE.getNode(NtPreferences.ROOT_PREFERENCES_NODE);
		defaultPreferenceNode = DefaultScope.INSTANCE.getNode(NtPreferences.ROOT_PREFERENCES_NODE);
	}

	@Override
	public String getLabel()
	{		
		return "Install/Update"; 
	}

	@Override
	public void restoreDefaultPressed()
	{
		String value = defaultPreferenceNode.get(UPDATESITE_LOCATION_PREFERENCE, null);
		((ListEditorComposite)preferenceComposite).setValues(value);
	}

	@Override
	public void appliedPressed()
	{
		String value = ((ListEditorComposite) preferenceComposite).getValues();
		try
		{
			instancePreferenceNode.put(UPDATESITE_LOCATION_PREFERENCE, value);
			instancePreferenceNode.flush();
		} catch (BackingStoreException e)
		{
			log.error(e);
		}
	}

	@Override
	public Composite createNodeComposite(IPreferenceNode referenceNode)
	{
		preferenceComposite = new UpdatePreferenceComposite(referenceNode.getParentNode(), SWT.None);
		
		String value = instancePreferenceNode.get(UPDATESITE_LOCATION_PREFERENCE,null);
		if(StringUtils.isEmpty(value))			
			value = getDefaultPreference().get(UPDATESITE_LOCATION_PREFERENCE,null);		
		((ListEditorComposite)preferenceComposite).setValues(value);
				
		return super.createNodeComposite(referenceNode);
	}
	
	

}
