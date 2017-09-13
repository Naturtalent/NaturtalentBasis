package it.naturtalent.e4.project.expimp;

import it.naturtalent.e4.project.IProjectDataFactory;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;

import org.eclipse.e4.core.services.log.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.PreferencesService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator
{
	private static BundleContext context;
	
	public static Logger logger = null;
	
	public static IProjectDataFactory projectDataFactory;
	
	public static ProjectDataAdapterRegistry projectDataAdapterRegister;
	
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


	
	

}
