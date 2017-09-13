
package it.naturtalent.e4.preferences;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import it.naturtalent.application.ChooseWorkspaceData;
import it.naturtalent.application.IPreferenceAdapter;

public class PreferenceAddOn
{

	private @Inject @Optional IPreferenceRegistry preferenceRegistry;

	@Inject
	@Optional
	public void applicationStarted(
			@EventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) Event event)
	{		
		
		ApplicationPreferenceAdapter appl =	new ApplicationPreferenceAdapter();
		
		if (preferenceRegistry != null)
			preferenceRegistry.getPreferenceAdapters()
					.add(new ApplicationPreferenceAdapter());	
		
		// default Workspace = current Workspace
		ChooseWorkspaceData chooseWorkspaceData = new ChooseWorkspaceData();
		String value = chooseWorkspaceData.getCurrentWorkspaceLocation();
		IEclipsePreferences defaultPreferenceNode = DefaultScope.INSTANCE.getNode(IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE);
		defaultPreferenceNode.put(IPreferenceAdapter.PREFERENCE_APPLICATION_WORKSPACE_KEY, value);
	}

}
