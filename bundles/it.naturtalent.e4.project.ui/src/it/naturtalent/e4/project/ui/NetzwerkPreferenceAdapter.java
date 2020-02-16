package it.naturtalent.e4.project.ui;

import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.spi.ui.util.ECPHandlerHelper;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import it.naturtalent.application.IPreferenceNode;
import it.naturtalent.e4.preferences.AbstractPreferenceAdapter;
import it.naturtalent.e4.project.model.project.ProjectPackage;
import it.naturtalent.e4.project.model.project.Proxies;
import it.naturtalent.e4.project.model.project.Proxy;


/**
 * Netzwerk - Connections
 * 
 * @author dieter
 *
 */
public class NetzwerkPreferenceAdapter extends AbstractPreferenceAdapter
{
	
	// Networking Properties
	public final static String HTTP_PROXY_HOST = "http.proxyHost";
	public final static String HTTP_PROXY_PORT = "http.proxyPort";
	public final static String HTTP_NONPROXY_HOSTS = "http.nonProxyHosts";
	// auf den Host sollte nicht durch Proxy zugegriffen werden (z.B. localhost)	
	public final static String HTTP_PROXY_USER = "http.proxyUser";
	public final static String HTTP_PROXY_PASSWORD = "http.proxyPassword"; 

	public final static String HTTPS_PROXY_HOST = "https.proxyHost";
	public final static String HTTPS_PROXY_PORT = "https.proxyPort";
	// Protocol nonProxyHost ist das Gleiche wie http
	public final static String HTTPS_PROXY_USER = "https.proxyUser";
	public final static String HTTPS_PROXY_PASSWORD = "https.proxyPassword"; 

	public final static String FTP_PROXY_HOST = "ftp.proxyHost"; 
	public final static String FTP_PROXY_PORT = "ftp.proxyPort";
	public final static String FTP_NONPROXY_HOSTS = "ftp.nonProxyHosts";
	public final static String FTP_PROXY_USER = "ftp.proxyUser";
	public final static String FTP_PROXY_PASSWORD = "ftp.proxyPassword"; 

	public final static String SOCKS_PROXY_HOST = "socks.proxyHost"; 
	public final static String SOCKS_PROXY_PORT = "socks.proxyPort";
	public final static String SOCKS_NONPROXY_HOSTS = "socks.nonProxyHosts";
	public final static String SOCKS_PROXY_USER = "socks.proxyUser";
	public final static String SOCKS_PROXY_PASSWORD = "socks.proxyPassword"; 

	// EMFModel - Namen
	public final static String HTTP_NAME = "Http";
	public final static String HTTPS_NAME = "Https";
	public final static String FTP_NAME = "Ftp";
	public final static String SOCKS_NAME = "Socks";

	// HTTP Proxy ein/aus
	public static final String HTTP_PROXY_KEY = "httpproxykey"; //$NON-NLS-1$

	private String fileTemplateKey = NtPreferences.FILE_TEMPLATE_PREFERENCE;

	private Log log = LogFactory.getLog(this.getClass());
	
	private EList<Proxy> proxies;

	/**
	 * Konstruktion
	 */
	public NetzwerkPreferenceAdapter()
	{
		instancePreferenceNode = InstanceScope.INSTANCE
				.getNode(NtPreferences.ROOT_PREFERENCES_NODE);
		defaultPreferenceNode = DefaultScope.INSTANCE
				.getNode(NtPreferences.ROOT_PREFERENCES_NODE);
	}

	@Override
	public String getLabel()
	{
		return "Netzwerk Connection";
	}

	@Override
	public void restoreDefaultPressed()
	{
		Proxies proxieContainer = getProxyContainer();
		initDefautProxies(proxieContainer);		
	}

	@Override
	public void appliedPressed()
	{
		// Status 'inUse' updaten an den aktuelle CheckStatus der Proxys im Viewer
		for(Proxy proxy : proxies)
			proxy.setInUse(false);
		
		Proxy[]checkedProxies = ((NetzwerkPreferenceComposite)preferenceComposite).getCheckedProxies();
		if(ArrayUtils.isNotEmpty(checkedProxies))
		{
			for(Proxy proxy : checkedProxies)
				proxy.setInUse(true);
		}
		
		// Proxies speichern
		ECPHandlerHelper.saveProject(Activator.getECPProject());
		
		// Systemproperties updaten
		setProxySystemProperties();
	}
	
	@Override
	public void cancelPressed()
	{
		Proxies proxieContainer = getProxyContainer();
		EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(proxieContainer);
		if(domain != null)
		{
			while(domain.getCommandStack().canUndo())
				domain.getCommandStack().undo();
		}		
		super.cancelPressed();
	}

	@Override
	public Composite createNodeComposite(IPreferenceNode referenceNode)
	{
		preferenceComposite = new NetzwerkPreferenceComposite(
				referenceNode.getParentNode(), SWT.None);

		Proxies proxieContainer = getProxyContainer();
		proxies = proxieContainer.getProxies();
		if (proxies.isEmpty())
		{
			// Defaulteintraege initialisieren
			initDefautProxies(proxieContainer);
			ECPHandlerHelper.saveProject(Activator.getECPProject());
		}

		// Proxies in Tabelle anzeigen
		((NetzwerkPreferenceComposite) preferenceComposite).setPreferenceValue(proxieContainer);
		
		return super.createNodeComposite(referenceNode);
	}

	/*
	 * Den Proxycontainer mit Derfaultproxies initialisieren.
	 * Evtl.gespeicherte Proxies werden geloescht.
	 */
	private void initDefautProxies(Proxies proxyContainer)
	{
		addProtocolEntry(HTTP_NAME);
		addProtocolEntry(HTTPS_NAME);
		addProtocolEntry(FTP_NAME);
		addProtocolEntry(SOCKS_NAME);	
	}
	
	// einzelnen Protokolleintrag generieren
	private void addProtocolEntry(String protocolName)
	{
		EObject container = getProxyContainer();
		EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(container);	
		EReference eReference = ProjectPackage.eINSTANCE.getProxies_Proxies();
		
		EClass proxieClass = ProjectPackage.eINSTANCE.getProxy();
		Proxy proxy = (Proxy) EcoreUtil.create(proxieClass);
		proxy.setSchemata(protocolName);		
		Command addCommand = AddCommand.create(domain, container, eReference, proxy);
		if(addCommand.canExecute())	
			domain.getCommandStack().execute(addCommand);
	}

	/*
	 * Containerelement aller Proxies zurueckgeben
	 */
	private static Proxies getProxyContainer()
	{
		Proxies proxies = null;
		ECPProject ecpProject = Activator.getECPProject();

		// im ECPProject einen Proxies-Datensatz suchen
		EList<Object> projectContents = ecpProject.getContents();
		if (!projectContents.isEmpty())
		{
			for (Object projectContent : projectContents)
			{
				if (projectContent instanceof Proxies)
				{
					proxies = (Proxies) projectContent;
					break;
				}
			}

			if (proxies == null)
			{
				// einen neuer Proxies-Containerelement erzeugen und speichern
				EClass proxiesClass = ProjectPackage.eINSTANCE.getProxies();
				proxies = (Proxies) EcoreUtil.create(proxiesClass);
				projectContents.add(proxies);
				ECPHandlerHelper.saveProject(ecpProject);
			}
		}

		return proxies;
	}
	
	// abhäegig von den 'inUse' - Status werden die Connection Proxys - Eigenscahften in den SystemProperties eingetragen
	public static void setProxySystemProperties()
	{
		Proxies proxies = getProxyContainer();
		EList<Proxy> projectContents = proxies.getProxies();
		if (!projectContents.isEmpty())
		{
			for(Proxy proxy : projectContents)
			{
				setSingleSystemProperty(proxy);
			}
		}
	}
	
	/*
	 * Die gespeicherten Daten eines Proxies werden in die Systemproperties uebernommen
	 */
	private static void setSingleSystemProperty(Proxy proxy)
	{
		String value;
		
		Properties systemProperties = System.getProperties();
		
		if (proxy.isInUse())
		{
			switch (proxy.getSchemata())
				{
					case HTTP_NAME:
						
						// Http host
						value = proxy.getHost();
						value = StringUtils.isNotEmpty(value) ? value : "";						
						systemProperties.setProperty(HTTP_PROXY_HOST, value);

						// Http port
						value = proxy.getPort();
						value = StringUtils.isNotEmpty(value) ? value : "";						
						systemProperties.setProperty(HTTP_PROXY_PORT, value);
						
						if(proxy.isAuthentification())
						{
							// Http user
							value = proxy.getUser();
							value = StringUtils.isNotEmpty(value) ? value : "";						
							systemProperties.setProperty(HTTP_PROXY_USER, value);

							// Http password
							value = proxy.getPassword();
							value = StringUtils.isNotEmpty(value) ? value : "";						
							systemProperties.setProperty(HTTP_PROXY_PASSWORD, value);
						}
						break;
						
					case HTTPS_NAME:
						
						// Https host
						value = proxy.getHost();
						value = StringUtils.isNotEmpty(value) ? value : "";						
						systemProperties.setProperty(HTTPS_PROXY_HOST, value);

						// Https port
						value = proxy.getPort();
						value = StringUtils.isNotEmpty(value) ? value : "";						
						systemProperties.setProperty(HTTPS_PROXY_PORT, value);
						
						if(proxy.isAuthentification())
						{
							// Https user
							value = proxy.getUser();
							value = StringUtils.isNotEmpty(value) ? value : "";						
							systemProperties.setProperty(HTTPS_PROXY_USER, value);

							// Https password
							value = proxy.getPassword();
							value = StringUtils.isNotEmpty(value) ? value : "";						
							systemProperties.setProperty(HTTPS_PROXY_PASSWORD, value);
						}
						break;

					case FTP_NAME:
						
						// Ftp host
						value = proxy.getHost();
						value = StringUtils.isNotEmpty(value) ? value : "";						
						systemProperties.setProperty(FTP_PROXY_HOST, value);

						// Ftp port
						value = proxy.getPort();
						value = StringUtils.isNotEmpty(value) ? value : "";						
						systemProperties.setProperty(FTP_PROXY_PORT, value);
						
						if(proxy.isAuthentification())
						{
							// Ftp user
							value = proxy.getUser();
							value = StringUtils.isNotEmpty(value) ? value : "";						
							systemProperties.setProperty(FTP_PROXY_USER, value);

							// Ftp password
							value = proxy.getPassword();
							value = StringUtils.isNotEmpty(value) ? value : "";						
							systemProperties.setProperty(FTP_PROXY_PASSWORD, value);
						}
						break;

					case SOCKS_NAME:
						
						// Socks host
						value = proxy.getHost();
						value = StringUtils.isNotEmpty(value) ? value : "";						
						systemProperties.setProperty(SOCKS_PROXY_HOST, value);

						// Socks port
						value = proxy.getPort();
						value = StringUtils.isNotEmpty(value) ? value : "";						
						systemProperties.setProperty(SOCKS_PROXY_PORT, value);
						
						if(proxy.isAuthentification())
						{
							// Socks user
							value = proxy.getUser();
							value = StringUtils.isNotEmpty(value) ? value : "";						
							systemProperties.setProperty(SOCKS_PROXY_USER, value);

							// Socks password
							value = proxy.getPassword();
							value = StringUtils.isNotEmpty(value) ? value : "";						
							systemProperties.setProperty(SOCKS_PROXY_PASSWORD, value);
						}
						break;

					default:
						break;
				}
		}
	}
	


}
