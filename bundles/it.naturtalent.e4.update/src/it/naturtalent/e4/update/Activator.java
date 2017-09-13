package it.naturtalent.e4.update;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

	private static BundleContext context;
	
	public static final String ROOT_UPDATE_PREFERENCES_NODE = "it.naturtalent.e4.update"; //$NON-NLS-1$

	public static final String REPOSITORY_PREF = "repository_pref"; //$NON-NLS-1$
	public static final String REPOSITORY_LOC = "updatesite"; //$NON-NLS-1$
	
	private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$
	private static final String CMD_LAUNCHER = "-launcher"; //$NON-NLS-1$
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$
	
	
	static BundleContext getContext()
	{
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception
	{
		Activator.context = bundleContext;
		initPreferences();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception
	{
		Activator.context = null;
	}
	
	
	private void initPreferences()
	{
		// Local Repository
		File launcherDir = new File(getProperty(
				System.getProperty(PROP_COMMANDS), CMD_LAUNCHER))
				.getParentFile();
		File repoLoc = new File(launcherDir,REPOSITORY_LOC).getParentFile().getParentFile();
		String locPath = repoLoc.getParent()+File.separator+REPOSITORY_LOC;
				
		IEclipsePreferences defaultNode = InstanceScope.INSTANCE
				.getNode(ROOT_UPDATE_PREFERENCES_NODE);
		
		defaultNode.put(REPOSITORY_PREF, "file:"+locPath);		
	}
	
	public String getProperty(String commandLine, String command)
	{
		String prop = null;

		int pos = StringUtils.indexOf(commandLine, command);
		if (pos != (-1))
		{
			prop = StringUtils.substring(commandLine, pos + command.length());
			if (prop.startsWith(NEW_LINE))
				prop = StringUtils.substring(prop, 1);
			pos = StringUtils.indexOf(prop, NEW_LINE);
			if (pos != (-1))
				prop = StringUtils.substring(prop, 0, pos);	
		}
		return prop;
	}	

}
