package it.wp.common.logger;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

	// SystemProperty ist Variable des LoggerFile - Directory
	public static final String NT_PROGRAM_HOME = "nt.programm.home"; //$NON-NLS-1$
	
	private static BundleContext context;

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
		
		String check = System.getProperty(NT_PROGRAM_HOME);
		if(check == null)
			System.err.println("Systemproperty 'nt.program.home' ist nicht definiert, Logger 'FileAppender' ohne Zielverzeichnis");		
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
