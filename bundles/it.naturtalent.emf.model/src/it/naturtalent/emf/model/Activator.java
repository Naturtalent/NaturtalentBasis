package it.naturtalent.emf.model;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.emf.ecp.core.ECPProjectManager;
import org.eclipse.emf.ecp.core.util.ECPUtil;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

	private static BundleContext context;
	
	private static ECPProjectManager ecbProjectManager;

	
	static BundleContext getContext()
	{
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception
	{
		Activator.context = bundleContext;
		
		// Initialisierung eines ECPProjectManager 	
		ECPProjectManager ecbProjectManager = ECPUtil.getECPProjectManager();
		
		// ECPProject aller NtProject - Elemente
		//Collection<ECPProject>projects = ecbProjectManager.getProjects();
		
		// Listener beobachtet Aendereungen am Modell
		ECPUtil.getECPObserverBus().register(new NtECPProjectContentTouchedObserver());
	

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
	
	public static IEventBroker getEventBroker()
	{
		final MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
		return currentApplication.getContext().get(EventBroker.class);		
	}
	
	public static IEclipseContext getApplicationContext()
	{
		final MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
		return currentApplication.getContext();		
	}


}
