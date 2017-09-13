package it.naturtalent.application;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.ui.internal.workbench.URIHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator
{
	
	public static final String ICONS_RESOURCE_FOLDER = "/icons"; //$NON-NLS-1$
	
	// The plug-in ID
	public static final String PLUGIN_ID = "it.naturtalent.application"; //$NON-NLS-1$

	public static final String RESOURCE_SCHEMA = "bundleclass://"; //$NON-NLS-1$

	public static final String RESOURCE_SEPARATOR = "/"; //$NON-NLS-1$

	// !!! gleiche Definition in 'it.wp.common.logger' (Zirkelbezuege verhindern)
	public static final String NT_PROGRAM_HOME = "nt.programm.home"; //$NON-NLS-1$
	
	private static BundleContext context;
	
	public static Properties properties = new Properties();
	public static final String PROPERTY_SHOWVIEW = "Application.showViewLabel"; //$NON-NLS-1$
	
	// Preferenceknoten
	//public static final String ROOT_PREFERENCES_NODE = "it.naturtalent.application"; //$NON-NLS-1$
	
	// externes Tempverzeichnis
	//public static final String EXTERN_TEMPFOLDER_KEY = "office.profile"; //$NON-NLS-1$		

	
	
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
		setExternProgramsPathProperty();
		initProperties();		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception
	{
		// soll Message: 
		//'The workspace exited with unsaved changes in the previous session'
		// verhindern
		ResourcesPlugin.getWorkspace().save(true, null);  
		Activator.context = null;
	}
	
	/**
	 * Returns the platform resource URI for the provided class
	 * 
	 * @param clazz
	 *            the class to get the resource URI for
	 * @return the platform resource URI
	 */
	public static String getResourceURI(Class<?> clazz)
	{
		return RESOURCE_SCHEMA + PLUGIN_ID + RESOURCE_SEPARATOR
				+ clazz.getCanonicalName();
	}
	
	/**
	 * Returns the platform URI of the bundle
	 * 
	 * @return the platform URI
	 */
	public static String getPlatformURI()
	{
		return URIHelper.constructPlatformURI(context.getBundle());
	}
	
	/*
	 * Relativ zum aktuellen Workspace wird ein Verzeichnis erwartet, 
	 * in dem Applicationen gespeichert sind.
	 * (z.B. auch der Launchpath dieser Application selbst)
	 * Dieses Verzeichnis 'programme' wird als
	 * SystemProperty 'NT_PROGRAM_HOME' gespeichert.
	 * 
	 * !!! Bei Aenderungen des Workspaceverzeichnisses ist diese
	 * Hierachie ggf. nicht mehr gewaehrleistet. 
	 * -> Workspaceaenderungen
	 * nur innerhalb von 'workspaces' zulassen.
	 * 
	 * !!! Der Logger speichert in dieses Verzeichnis
	 */
	private void setExternProgramsPathProperty()
	{		
		if (StringUtils.isEmpty(System.getProperty(NT_PROGRAM_HOME)))
		{
			File workspaceDir = ResourcesPlugin.getWorkspace().getRoot()
					.getLocation().toFile();
			Path path = new Path(workspaceDir.getPath());
			path = (Path) path.removeLastSegments(2);
			
			// existiert ein Verzeichnis 'programme'
			path = (Path) path.append("programme"); //$NON-NLS-1$
			
			File programFile = path.toFile();
			if(!programFile.exists())
			{
				path = new Path(workspaceDir.getPath());
				path = (Path) path.removeLastSegments(1);
				path = (Path) path.append("programme"); //$NON-NLS-1$
				programFile = path.toFile();
				programFile.mkdir();
			}
				
			if(!programFile.exists())
			{				
				System.err.println(Messages.Activator_IncorrectWorkspaceStructure);
				return;
			}
			
			
			
			System.setProperty(NT_PROGRAM_HOME, path.toOSString());
		}
	}
	
	private void initProperties()
	{
		try
		{
			String path = "platform:/plugin/"+PLUGIN_ID+"/plugin.properties"; //$NON-NLS-1$ //$NON-NLS-2$
			URL url = new URL(path);	
			InputStream inputStream = url.openConnection().getInputStream();			
			properties.load(inputStream);			
		} catch (Exception e)
		{
		}
	}
	
	/*
	private void initPreferences ()
	{
		String progDir = System.getProperty(EXTERN_PROGRAM_PROPERTY);
		File file = new File(progDir,"temp"); //$NON-NLS-N$
				
		if(!file.exists())
			file.mkdir();
		
		if(file.exists())
		{			
			IEclipsePreferences defaultNode = DefaultScope.INSTANCE
					.getNode(ApplicationPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE);
			defaultNode.put(ApplicationPreferenceAdapter.PREFERNCE_APPLICATION_TEMPDIR_KEY, file.getPath());
		}
	}
	*/


}
