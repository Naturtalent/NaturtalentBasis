package it.naturtalent.e4.project.ui;


import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.internal.workbench.URIHelper;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.ECPProjectManager;
import org.eclipse.emf.ecp.core.ECPProvider;
import org.eclipse.emf.ecp.core.ECPProviderRegistry;
import org.eclipse.emf.ecp.core.exceptions.ECPProjectWithNameExistsException;
import org.eclipse.emf.ecp.core.util.ECPUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkingSet;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IProjectDataFactory;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.model.project.ProjectPackage;
import it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory;
import it.naturtalent.e4.project.ui.model.WorkbenchAdapterFactory;
import it.naturtalent.e4.project.ui.model.WorkingSetAdapterFactory;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.registry.ProjectImageRegistry;
import it.naturtalent.e4.project.ui.utils.ProjectQueue;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSetRoot;
import it.naturtalent.emf.model.EMFModelUtils;




public class Activator implements BundleActivator
{
	
	// Name des ECP Projekts indem alle NtProjekte gespeichert werden
	//public final static String ECPNTPROJECTNAME_OLD = "Project";
	
	// Projekt, in dem alle Archive abgelegt sind
	//private static ECPProject ntProject = null;

	
	
	// Name des ECP Projekts indem alle NtProjekte gespeichert werden
	public final static String ECPNTPROJECTNAME = "ECPProject";
	
	//private static ECPProject ecpProject = null;
	private static NtProjects ntProjects;
	
	public static final String ICONS_RESOURCE_FOLDER = "/icons"; //$NON-NLS-1$
	
	// The plug-in ID
	public static final String PLUGIN_ID = "it.naturtalent.e4.project.ui"; //$NON-NLS-1$

	public static final String RESOURCE_SCHEMA = "bundleclass://"; //$NON-NLS-1$

	public static final String RESOURCE_SEPARATOR = "/"; //$NON-NLS-1$
	
	public static final String PLATFORM_PREFIX = "platform:/plugin"; //$NON-NLS-1$
	
	private static BundleContext context;
	
	private static WorkingSetManager iWorkingSetManager = null;
	
	//private static Logger logger = null;
	
	private static Log log = LogFactory.getLog(Activator.class);
	
	public static IEclipseContext workbenchContext = null;
	
	public static IProjectDataFactory projectDataFactory;
	
	public static ProjectDataAdapterRegistry projectDataAdapterRegister;
	
	// steuert erweiterte Funktionalitaet nach der phys. Erzeugung von Projekten ('WorkbenchContentProvider')
	public static boolean creatProjectAuxiliaryFlag = false;
	
	// Zuordnung (ID,AliasName) neuerzeugter Projekte
	public static HashMap<String, String> newlyCreatedProjectMap = new HashMap<String, String>();
	
    /**
     * Project image registry; lazily initialized.
     */
    private static ProjectImageRegistry projectImageRegistry = null;

    
    // Zugriff auf 'plugin.properties'
    public static Properties properties = new Properties();

    // Queue indem die ProjectID der zuletzt selektierten Projekte gespeichert sind 
    public static ProjectQueue projectQueue = new ProjectQueue();

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
			
		registerAdapters();
		initProjectImageRegistry();
		NtPreferences.initialize();
		initPluginProperties();
		initService();	
		projectQueue.start();
	}
	
	
	/*
	private static ECPProject createProject(String projectName)
	{
		ECPProject project = null;
		
		final List<ECPProvider> providers = new ArrayList<ECPProvider>();		
		for (final ECPProvider provider : ECPUtil.getECPProviderRegistry().getProviders())
		{
			if (provider.hasCreateProjectWithoutRepositorySupport())			
				providers.add(provider);			
		}
		
		if (providers.size() == 0)
		{
			log.error("kein Provider installiert"); 
			return null;
		}
	
		try
		{
			project = ECPUtil.getECPProjectManager()
					.createProject(providers.get(0), projectName, ECPUtil.createProperties());
		} catch (ECPProjectWithNameExistsException e)
		{
			log.error("es wurde kein ECPProject istalliert"); 
		}
		
		return project;
	}
	*/
	
	
	/**
	 * Sucht im ECP-Project 'ECPNTPROJECTNAME' nach dem EMFModell 'NtProjects' und gibt dieses zurueck.
	 * NtProjects ist das Containerelement indem alle NtProjekte gesoeichert sind.
	 * 
	 * Bei Erstaufruf sicherstellen, dass alle erforderlichen Resourcen angelegt werden. 
	 * 
	 * @return
	 */
	public static NtProjects getNtProjects()
	{
		if (ntProjects != null)
			return ntProjects;
		
		ECPProject ecpProject = getECPProject();
		if(ecpProject != null)
		{
			// im ECPProject das Modell 'NtProjects" suchen 		
			EList<Object>projectContents = getECPProject().getContents();
			if(!projectContents.isEmpty())
			{
				for(Object projectContent : projectContents)
				{
					if (projectContent instanceof NtProjects)
					{
						ntProjects = (NtProjects) projectContent; 
						break;
					}
				}			
			}
			else
			{
				// das Modell Designs erzeugen und im ECPProject speichern
				EClass designsClass = ProjectPackage.eINSTANCE.getNtProjects();
				ntProjects = (NtProjects)EcoreUtil.create(designsClass);
				projectContents.add(ntProjects);
				ecpProject.saveContents();			
			}
			
		}
		
		
		
	
		
		/*
		
	
		EList<Object> childs = null;
		try
		{
			childs = getECPProject().getContents();
		} catch (Exception e)
		{
			log.error("keine Projekte im EMF Modell vorhanden");
	
			boolean b = getECPProject().hasDirtyContents();
	
			// return ntProjects;
		}
	
		// Model 'NtProjects' im ECPProject suchen
		if (childs != null)
		{
			for (Object child : childs)
			{
				if (child instanceof NtProjects)
				{
					// Modell 'NtProjects' gefunden, statisch
					// zwischenspeichern
					ntProjects = (NtProjects) child;
					break;
				}
			}
		}
	
		if (ntProjects == null)
		{
			// Modell noch nicht vorhanden, neu anlegen, zum ECPProject
			// hinzufuegen und speichern
			EClass ntProjectsClass = ProjectPackage.eINSTANCE.getNtProjects();
			ntProjects = (NtProjects) EcoreUtil.create(ntProjectsClass);
			try
			{
				List<Object> test = ecpProject.getContents();
	
				ecpProject.getContents().add(ntProjects);
				ecpProject.saveContents();
			} catch (Exception e)
			{
				log.error("Fehler beim Laden des Projektinhalts");
	
				//
				// final List<ECPContainer> toBeDeleted = new
				// ArrayList<ECPContainer>(); toBeDeleted.add(ecpProject);
				// ECPHandlerHelper.deleteHandlerHelper(toBeDeleted,
				// Display.getCurrent().getActiveShell()); ecpProject = null;
				// log.info("korruptes ECPProjekt wurde gesloescht");
				//
			}
	
		}
		*/
	
		return ntProjects;
	}

	/**
	 * ECP-Projekt fuer die NtProject-Properties abfragen (ggf. neu erzugen)
	 *   
	 * @return
	 */
	private static ECPProject ecpNtProject;
	public static ECPProject getECPProject()
	{
		
/*		
		ECPProviderRegistry registry = ECPUtil.getECPProviderRegistry();
		for (final ECPProvider provider : registry.getProviders())
		{
			String s = provider.getName();
			System.out.println(s);
		}
		*/
		
		
		if(ecpNtProject == null)
		{
			ECPProjectManager manager = ECPUtil.getECPProjectManager();
			if(manager != null)			
				ecpNtProject = ECPUtil.getECPProjectManager().getProject(ECPNTPROJECTNAME);
			else return null;
		}
		
		if(ecpNtProject == null)
		{
			ecpNtProject = EMFModelUtils.createProject(ECPNTPROJECTNAME);
			//ecpNtProject = createProject(ECPNTPROJECTNAME);
			log.error("es konnte kein ECPProject erzeugt werden");
		}
		
		return ecpNtProject;
	}
	

	private static ECPProject createProject(String projectName)
	{
		ECPProject project = null;
		
		final List<ECPProvider> providers = new ArrayList<ECPProvider>();		
		for (final ECPProvider provider : ECPUtil.getECPProviderRegistry().getProviders())
		{
			if (provider.hasCreateProjectWithoutRepositorySupport())			
				providers.add(provider);			
		}
		
		if (providers.size() == 0)
		{
			log.error("kein Provider installiert"); 
			return null;
		}

		try
		{
			project = ECPUtil.getECPProjectManager()
					.createProject(providers.get(0), projectName, ECPUtil.createProperties());
		} catch (ECPProjectWithNameExistsException e)
		{
			log.error("es wurde kein ECPProject istalliert"); 
		}
		
		return project;
	}



	/**
	 * loescht das Conteinerelement NtProjects
	 */
	public static void deleteNtProjects()
	{		
		if(ntProjects != null)
		{
			List<Object>lNtObjects = new ArrayList<Object>();
			lNtObjects.add(ntProjects);
			getECPProject().deleteElements(lNtObjects);
			ntProjects = null;
		}		
	}
	
	/**
	 * Sucht ein NtProject 
	 * 
	 * @ToDo durch EMF Search Funktion ersetzen ???
	 * 
	 * @param ntProject
	 * @return
	 */

	public static NtProject findNtProject(String ntProjectID)
	{
		NtProjects ntProjects = getNtProjects();		
		if(ntProjects != null)
		{			
			EList<NtProject>projects = ntProjects.getNtProject();
			for(NtProject project : projects)
			{
				if(StringUtils.equals(project.getId(), ntProjectID))
					return project;					
			}
		}
					
		log.error("NtProperty fuer Project: '"+ntProjectID+"' nicht gefunden");
		return null;
	}
	
	/*
	private static EList<Object>childs;
	public static project.NtProject findNtProjectOLD(String ntProjectID)
	{
		project.NtProject ntProject;
		
		if(childs == null)
			childs = getECPProject().getContents();
		
		for(Object child : childs)
		{
			if (child instanceof project.NtProject)
			{
				ntProject = (project.NtProject) child;
				if(StringUtils.equals(ntProject.getId(), ntProjectID))
					return ntProject;
			}
		}
		
		return null;
	}
	
	public static EList<Object>getProjectContents()
	{
		if(childs == null)
			childs = getECPProject().getContents();
		
		return childs;
	}
	*/
	


	private void initService()
	{
		IEclipseContext context = E4Workbench.getServiceContext();
		INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository = context.get(INtProjectPropertyFactoryRepository.class);
		List<INtProjectPropertyFactory>ntPropertyFactories = ntProjektDataFactoryRepository.getAllProjektDataFactories();
		ntPropertyFactories.add(new NtProjectPropertyFactory());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception
	{
		projectQueue.stop();
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

	/**
	 * Register workbench adapters programmatically. This is necessary to enable
	 * certain types of content in the explorers.
	 * <p>
	 * <b>Note:</b> this method should only be called once, in your
	 * application's WorkbenchAdvisor#initialize(IWorkbenchConfigurer) method.
	 * </p>
	 * 
	 * @since 3.5
	 */
	public static void registerAdapters()
	{
		IAdapterManager manager = Platform.getAdapterManager();
		IAdapterFactory factory = new WorkbenchAdapterFactory();
		manager.registerAdapters(factory, IWorkspace.class);
		manager.registerAdapters(factory, IWorkspaceRoot.class);
		manager.registerAdapters(factory, IProject.class);
		manager.registerAdapters(factory, IFolder.class);
		manager.registerAdapters(factory, IFile.class);
		manager.registerAdapters(factory, IMarker.class);	
		
		factory = new WorkingSetAdapterFactory();
		manager.registerAdapters(factory, IWorkingSet.class);
		manager.registerAdapters(factory, WorkingSetRoot.class);
	}
	

	public static WorkingSetManager getWorkingSetManager()
	{
		if(iWorkingSetManager == null)
		{
			iWorkingSetManager = new WorkingSetManager(context);
			iWorkingSetManager.restoreState();
		}
		
		return iWorkingSetManager;
	}


	/**
	 * Ein ProjectImageRegistry im Context anlegen und ein OverlayImage fuer das Nt-Project hinterlegen
	 * 
	 */
	private void initProjectImageRegistry()
	{
		URI uri = null;
		IEclipseContext eclipseCtx = EclipseContextFactory
				.getServiceContext(context);
		
		projectImageRegistry = ContextInjectionFactory.make(
				ProjectImageRegistry.class, eclipseCtx);
		
		String iconURI = Activator.getPlatformURI()+Activator.ICONS_RESOURCE_FOLDER+"/"+WorkbenchImages.IMG_PROJECT_OVR;
		try
		{
			uri = URI.createURI(iconURI);
			ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(new URL(uri.toString()));
			projectImageRegistry.setNatureImage(it.naturtalent.e4.project.NtProject.NATURE_ID, imageDescriptor);
			
		} catch (MalformedURLException e)
		{
			System.err.println("iconURI \"" + uri.toString()
					+ "\" is invalid, no image will be shown");
		}
	}
	
	public static void setProjectImage(String natureID, String iconURI)
	{
		URI uri = null;
		
		try
		{
			uri = URI.createURI(iconURI);
			ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(new URL(uri.toString()));
			projectImageRegistry.setNatureImage(natureID, imageDescriptor);
			
		} catch (MalformedURLException e)
		{
			System.err.println("iconURI \"" + uri.toString()
					+ "\" is invalid, no image will be shown");
		}
		
	}
	

    /**
     * Return the manager that maps project nature ids to images.
     */
	public static ProjectImageRegistry getProjectImageRegistry()
	{
		return projectImageRegistry;
	}

	/*
	public static Logger getLogger()
	{
		return logger;
	}

	public static void setLogger(Logger logger)
	{
		Activator.logger = logger;
		if(logger != null)
			logInfo("logger installiert");
		else
			logError("kein logger installiert");
	}
	
	public static void logError(String message)
	{
		if(logger != null)
			logger.error(message);
	}
	
	public static void logError(Throwable t)
	{
		if(logger != null)
			logger.error(t);
	}

	public static void logInfo(String message)
	{
		if(logger != null)
			logger.info(message);
	}
	*/
	
	private void initPluginProperties()
	{
		try
		{
			String path = "platform:/plugin/"+PLUGIN_ID+"/plugin.properties";
			URL url = new URL(path);	
			InputStream inputStream = url.openConnection().getInputStream();			
			properties.load(inputStream);			
		} catch (Exception e)
		{
		}
	}
	
	public static IResourceNavigator findNavigator()
	{
		final MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
		final IEclipseContext selectedWindowContext = currentApplication.getSelectedElement().getContext();
		EPartService partService = selectedWindowContext.get(EPartService.class);		
		MPart part = partService.findPart(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		Object obj = part.getObject();
		return (IResourceNavigator) part.getObject();
	}

}