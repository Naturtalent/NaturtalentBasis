package it.naturtalent.e4.project.ui;

import it.naturtalent.application.IPreferenceNode;
import it.naturtalent.e4.preferences.AbstractPreferenceAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.prefs.BackingStoreException;


public class ProjectPreferenceAdapter extends AbstractPreferenceAdapter
{

	// key Dateivorlagepreferenz
	private String fileTemplateKey = NtPreferences.FILE_TEMPLATE_PREFERENCE;
	
	private Log log = LogFactory.getLog(this.getClass());
			
	public ProjectPreferenceAdapter()
	{
		instancePreferenceNode = InstanceScope.INSTANCE.getNode(NtPreferences.ROOT_PREFERENCES_NODE);
		defaultPreferenceNode = DefaultScope.INSTANCE.getNode(NtPreferences.ROOT_PREFERENCES_NODE);
	}

	@Override
	public String getLabel()
	{		
		return Messages.ProjectPreferenceAdapter_Label;
	}

	@Override
	public void restoreDefaultPressed()
	{
		String value = defaultPreferenceNode.get(fileTemplateKey, null);
		if(StringUtils.isNotEmpty(value))
			((ProjectPreferenceComposite)referenceComposite).setPreferenceValue(value);
	}

	@Override
	public void appliedPressed()
	{
		String value = ((ProjectPreferenceComposite)referenceComposite).getPreferenceValue();
		if(StringUtils.isNotEmpty(value))
		{			
			try
			{
				instancePreferenceNode.put(fileTemplateKey, value);
				instancePreferenceNode.flush();
			} catch (BackingStoreException e)
			{
				log.error(e);	
			}
		}
	}

	@Override
	public Composite createNodeComposite(IPreferenceNode referenceNode)
	{
		referenceComposite = new ProjectPreferenceComposite(referenceNode.getParentNode(), SWT.None);
		
		String value = instancePreferenceNode.get(fileTemplateKey,null);
		if(StringUtils.isEmpty(value))			
			value = getDefaultPreference().get(fileTemplateKey,null);
		
		((ProjectPreferenceComposite)referenceComposite).setPreferenceValue(value);
		return super.createNodeComposite(referenceNode);
	}




}
