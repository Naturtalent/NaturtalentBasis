package it.naturtalent.e4.search;

import it.naturtalent.e4.project.search.IProjectSearchPageRegistry;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

	private static BundleContext context;
	
	public static IProjectSearchPageRegistry searchPageregistry;
	
	public static EPartService ePartService;
	
	public static MApplication application;

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
