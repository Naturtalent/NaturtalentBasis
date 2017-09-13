package it.naturtalent.e4.perspectiveswitcher;

import it.naturtalent.e4.perspectiveswitcher.tools.E4PerspectiveSwitcherPreferences;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.e4.ui.internal.workbench.URIHelper;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;

/**
 * The activator class controls the plug-in life cycle
 */
public class PerspectiveActivator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "it.naturtalent.e4.PerspectiveSwitcher"; //$NON-NLS-1$

	public static final String RESOURCE_SCHEMA = "bundleclass://"; //$NON-NLS-1$

	public static final String RESOURCE_SEPARATOR = "/"; //$NON-NLS-1$

	// The shared instance
	private static PerspectiveActivator plugin;

	/**
	 * The constructor
	 */
	public PerspectiveActivator()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		E4PerspectiveSwitcherPreferences.initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PerspectiveActivator getDefault()
	{
		return plugin;
	}

	/**
	 * Returns the platform URI of the bundle
	 * 
	 * @return the platform URI
	 */
	public String getPlatformURI()
	{
		return URIHelper.constructPlatformURI(this.getBundle());
	}

	/**
	 * Returns the platform resource URI for the provided class
	 * 
	 * @param clazz
	 *            the class to get the resource URI for
	 * @return the platform resource URI
	 */
	public String getResourceURI(Class<?> clazz)
	{
		return RESOURCE_SCHEMA + PLUGIN_ID + RESOURCE_SEPARATOR
				+ clazz.getCanonicalName();
	}
	
}
