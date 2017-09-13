package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.ui.Activator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Nur zum Propieren (Kontextmenu ProjectExplorer)
 * 
 * s. it.naturtalent.e4.project.navigator.ResourceNavigator.initPopUp()
 * 
 * @author A682055
 *
 */
public class TestProjectHandler  
{
	
	private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$
	private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$
	private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$
	private static final String PROP_EXIT_CODE = "eclipse.exitcode"; //$NON-NLS-1$
	private static final String PROP_EXIT_DATA = "eclipse.exitdata"; //$NON-NLS-1$
	private static final String CMD_NL = "-nl"; //$NON-NLS-1$
	private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	@Inject 
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;
	
	@Inject
	@Optional
	static IStatusLineManager status;
	
	private IResourceNavigator navigator;
	
	@Execute
	public void execute(IEclipseContext context)
	{		
		
		scan();
		
		//status.setMessage("STATUS");
		System.out.println("test "+status);
	}
	
	
	/*
	public void execute(IWorkbench workbench, IStatusLineManager status)
	{		
		
		status.setMessage("STATUS");
		System.out.println("test");
	}
	*/
	
	@CanExecute
	public boolean canExecute()
	{		
		return true;
	}

	private void storeProjectDataInXML()
	{
		IProject [] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		List<IProjectDataAdapter>adapters = ProjectDataAdapterRegistry.getProjectDataAdapters();
				
		for(IProject iProject : allProjects)
		{
			if(iProject.isOpen())
			{
				NtProject ntProject = new NtProject(iProject);	
				
				for(IProjectDataAdapter adapter : adapters)
				{
					IProjectData data = Activator.projectDataFactory
							.readProjectData(adapter, ntProject);
					adapter.setProjectData(data);
				}

				Activator.projectDataFactory.saveOrUpdateProjectData(shell,
						ProjectDataAdapterRegistry.getProjectDataAdapters(),
						ntProject);		
				
				break;
			}			
		}
	}

	
	/**
	 * Konvertiert alte Projektstruktur (Projektdaten in 'exist') in FileStruktur (".projektData")
	 */
	private void convertOldStructure()
	{
		IProject [] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		List<IProjectDataAdapter>adapters = ProjectDataAdapterRegistry.getProjectDataAdapters();
				
		for(IProject iProject : allProjects)
		{
			if(iProject.isOpen())
			{
				NtProject ntProject = new NtProject(iProject);	
				
				for(IProjectDataAdapter adapter : adapters)
				{
					IProjectData data = Activator.projectDataFactory
							.readProjectData(adapter, ntProject);
					adapter.setProjectData(data);
				}
				
				Activator.projectDataFactory.saveOrUpdateProjectData(shell,
						ProjectDataAdapterRegistry.getProjectDataAdapters(),
						ntProject);		
			}			
		}
				
		System.out.println("TEST handler");
				
	}
	
	/*
	 * fuer Testzwecke
	 */
	private ServiceTracker<?, ?> proxyTracker;

	@SuppressWarnings("unchecked")
	public void readWebPage(String stgUrl)
	{
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();

		proxyTracker = new ServiceTracker(FrameworkUtil.getBundle(
				this.getClass()).getBundleContext(),
				IProxyService.class.getName(), null);
		proxyTracker.open();

		try
		{
			// URI uri = new URI("http://www.vogella.de");
			URI uri = new URI(stgUrl);
			IProxyService proxyService = getProxyService();
			IProxyData[] proxyDataForHost = proxyService.select(uri);

			for (IProxyData data : proxyDataForHost)
			{
				if (data.getHost() != null)
				{
					System.setProperty("http.proxySet", "true");
					System.setProperty("http.proxyHost", data.getHost());
				}
				if (data.getHost() != null)
				{
					System.setProperty("http.proxyPort",
							String.valueOf(data.getPort()));
				}
			}
			// Close the service and close the service tracker
			proxyService = null;

			URL url;

			url = uri.toURL();

			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null)
			{
				// Process each line.
				sb.append(inputLine + "\n");
			}

			proxyTracker.close();

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IProxyService getProxyService()
	{
		return (IProxyService) proxyTracker.getService();
	}

	private static String buildCommandLine(String nl) {
	    String property = System.getProperty(PROP_VM);  

	    StringBuffer result = new StringBuffer();
	    if (property != null) {
	        result.append(property);
	    }
	    result.append(NEW_LINE);

	    // append the vmargs and commands. Assume that these already end in \n
	    String vmargs = System.getProperty(PROP_VMARGS);
	    if (vmargs != null) {
	        result.append(vmargs);
	    }

	    // append the rest of the args, replacing or adding -data as required
	    property = System.getProperty(PROP_COMMANDS);
	    if (property != null) {// find the index of the arg to replace its value
	        int cmd_nl_pos = property.lastIndexOf(CMD_NL);
	        if (cmd_nl_pos != -1) {
	            cmd_nl_pos += CMD_NL.length() + 1;
	            result.append(property.substring(0, cmd_nl_pos));
	            result.append(nl);
	            result.append(property.substring(property.indexOf('\n',
	                    cmd_nl_pos)));
	        } else {
	            result.append(NEW_LINE);
	            result.append(property);
	            result.append(NEW_LINE);
	            result.append(CMD_NL);
	            result.append(NEW_LINE);
	            result.append(nl);
	        }
	    }

	    // put the vmargs back at the very end (the eclipse.commands property
	    // already contains the -vm arg)
	    if (vmargs != null) {
	        result.append(CMD_VMARGS);
	        result.append(NEW_LINE);
	        result.append(vmargs);
	    }


	    return result.toString();
	}
	
	/*
	 *  SCAN WIFI
	 * 
	 * 
	 */
	
	private void scan()
	{
		InetAddress localhost = null;
		
		try
		{
			localhost = InetAddress.getLocalHost();

			// this code assumes IPv4 is used
			byte[] ip = localhost.getAddress();
			for (int i = 0; i < 255; i++)
			{
				ip[3] = (byte) i;
				InetAddress address = InetAddress.getByAddress(ip);
				if (address.isReachable(1000))
				{
					System.out.println("can b pinged");
					// machine is turned on and can be pinged
				}
				else if (!address.getHostAddress()
						.equals(address.getHostName()))
				{
					System.out.println("Name is......" + address.getHostName()
							+ "\tIP is......." + address.getHostAddress());
					// machine is known in a DNS lookup
				}
				else
				{
					System.out.println("nothing");
					// the host address and host name are equal, meaning the
					// host name could not be resolved
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

		

	
	
}