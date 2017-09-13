package it.naturtalent.e4.update.handler;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.ui.ProvUIActivator;
import org.eclipse.equinox.internal.p2.ui.model.IUElementListRoot;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UninstallOperation;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.Policy;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.update.dialogs.InstallLocationDialog;
import it.naturtalent.e4.update.dialogs.SelectInstalledIUDialog;
import it.naturtalent.e4.update.dialogs.SelectableIUsPage;

public class UpdateFeatureHandler
{

	
	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "file:/media/fritzbox/Dokumente/UpdateSites/Design/repository");
	
	private String message;
	
	private static final String PROFILE_ID = "DefaultProfile";
	
	boolean cancelled = false;

	private Log log = LogFactory.getLog(this.getClass());
	
	
	@Execute
	public void execute(final IProvisioningAgent agent,
			final UISynchronize sync, final IWorkbench workbench , final Shell parent)
	{

		// update using a progress monitor
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException
			{

				update(agent, monitor, sync, workbench, parent);
			}
		};

		try
		{
			new ProgressMonitorDialog(null).run(true, true, runnable);
		}
		// catch (InvocationTargetException | InterruptedException e)
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private IStatus update(final IProvisioningAgent agent, final IProgressMonitor monitor,
			final UISynchronize sync, final IWorkbench workbench, final Shell parent)
	{
		sync.syncExec(new Runnable()
		{
			@Override
			public void run()
			{
				// Feature im angegebenen Verzeichnis ermitteln
				final IInstallableUnit newFeature = getNewFeature(monitor, agent, REPOSITORY_LOC);
				if(newFeature == null)				
				{		
					message = "kein gültigen Featuredaten im angegebenen Verzeichnis gefunden";					
					return;
				}
				
				List<IInstallableUnit>olderFeatures = getOlderFeatures(monitor, agent, newFeature);
				if(olderFeatures.isEmpty())
				{
					message = "keine neuere Featureversion gefunden";					
					return;
				}
				
				List<IInstallableUnit>olderCategories = getOlderCategory(monitor, agent, newFeature);
				if(olderCategories.isEmpty())
				{
					message = "keine zueordnete Kategorie gefunden";	
					olderCategories = olderFeatures;
					return;
				}

				
				// die vorhandenen Feature deinstallieren
				UninstallOperation uninstallOperation = new UninstallOperation(new ProvisioningSession(agent), olderCategories);
				uninstallOperation.setProvisioningContext(new ProvisioningContext(new ProvisioningSession(agent).getProvisioningAgent()));
				final IStatus status = uninstallOperation.resolveModal(monitor);
				if (status.getSeverity() != IStatus.ERROR)
				{
					final ProvisioningJob provisioningJob = uninstallOperation.getProvisioningJob(monitor);

					// updates cannot run from within Eclipse IDE!!!
					if (provisioningJob == null)
					{
						message = "Running update from within Eclipse IDE? This won't work!!!";
						return;
					}

					// register a job change listener to track
					// installation progress and notify user upon success
					provisioningJob.addJobChangeListener(new JobChangeAdapter()
					{
						@Override
						public void done(IJobChangeEvent event)
						{
							if (event.getResult().isOK())
							{
								sync.syncExec(new Runnable()
								{
									@Override
									public void run()
									{
										// NewFeature installieren
										List<IInstallableUnit>toInstallFeatures = new ArrayList<IInstallableUnit>();
										toInstallFeatures.add(newFeature);
										InstallOperation operation = new InstallOperation(new ProvisioningSession(agent), toInstallFeatures);
										
										IStatus status = operation.resolveModal(monitor);
										if (status.isOK())
										{
											final ProvisioningJob provisioningJob = operation.getProvisioningJob(monitor);
											
											// install cannot run from within Eclipse IDE!!!
											if (provisioningJob == null)
											{
												System.err.println("Running update from within Eclipse IDE? This won't work!!!");
												throw new NullPointerException();
											}

											// register a job change listener to track
											// installation progress and notify user upon success					
											provisioningJob.addJobChangeListener(new JobChangeAdapter()
											{
												@Override
												public void done(IJobChangeEvent event)
												{
													if (event.getResult().isOK())
													{
														sync.syncExec(new Runnable()
														{
															@Override
															public void run()
															{
																boolean restart = MessageDialog
																		.openQuestion(
																				parent,
																				"Installation neuer Software",
																				"Die Installation wurde erfolgreich abgeschlossen, Neustart?");
																if (restart)
																{
																	workbench.restart();
																}
															}
														});
													}
													super.done(event);
												}
											});
											
											provisioningJob.schedule();
										}
										
										else
										{
											showError(sync, "Update neuer Features misslungen");
										}										
									}
								});
							}
							
							// Deinstallation erledigt
							super.done(event);
						}
					});

					// Deinstallation starten
					provisioningJob.schedule();
				}				
			}			
		});
			
		if(StringUtils.isNotEmpty(message))
			showError(sync, message);
		
		return Status.OK_STATUS;
	}	
	
	private void showMessage(UISynchronize sync, final String message)
	{
		// as the provision needs to be executed in a background thread
		// we need to ensure that the message dialog is executed in
		// the UI thread
		sync.syncExec(new Runnable()
		{

			@Override
			public void run()
			{
				MessageDialog.openInformation(null, "Information", message);
			}
		});
	}

	private void showError(UISynchronize sync, final String message)
	{
		// as the provision needs to be executed in a background thread
		// we need to ensure that the message dialog is executed in
		// the UI thread
		sync.syncExec(new Runnable()
		{

			@Override
			public void run()
			{
				MessageDialog.openError(null, "Error", message);
			}
		});
	}
	
	private ProvisioningUI getProvisioningUI(IProvisioningAgent agent)
	{
		Policy policy = (Policy) ServiceHelper.getService(ProvUIActivator.getContext(), Policy.class.getName());
		if (policy == null)
			policy = new Policy();
		
		final ProvisioningSession session = new ProvisioningSession(agent);
		return new ProvisioningUI(session, IProfileRegistry.SELF, policy);		
	}
	
	/*
	 * 
	 */
	private IInstallableUnit getNewFeature(IProgressMonitor monitor, IProvisioningAgent agent, String repositoryPath)
	{		
		URI repositoryURI = null;
		Collection<IMetadataRepository> metadataReposList = null;
		
		try
		{
			repositoryURI = new URI(repositoryPath);
			if (!repositoryURI.isAbsolute())
			{
				repositoryPath = "file:/" + repositoryPath;
				repositoryURI = new URI(repositoryPath);
			}
		} catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		IMetadataRepositoryManager metaManager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		try
		{
			metadataReposList = new LinkedList<IMetadataRepository>();
			metadataReposList.add(metaManager.loadRepository(repositoryURI, monitor));
			
			IMetadataRepository metadataRepos = metadataReposList.iterator().next();
			
			IQuery<IInstallableUnit> query = QueryUtil.createIUGroupQuery();
			IQueryResult<IInstallableUnit> collector =metadataRepos.query(query, monitor);
			Iterator<IInstallableUnit> iter = collector.iterator();
			return iter.next();
			
		} catch (ProvisionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationCanceledException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private List<IInstallableUnit> getOlderCategory(final IProgressMonitor monitor, IProvisioningAgent agent, IInstallableUnit newFeature)
	{	
		List<IInstallableUnit>existCategories = new ArrayList<IInstallableUnit>();
		
		IQueryable<IInstallableUnit> queryable = ((IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME))
				.getProfile(PROFILE_ID);


		if (queryable != null)
		{
			String newFestureName = newFeature.getId();
			
			IQuery<IInstallableUnit> query = QueryUtil.createIUCategoryQuery();
			IQueryResult<IInstallableUnit> result = queryable.query(query, monitor);			
			Iterator<IInstallableUnit> categories = result.iterator();
						
			while (categories.hasNext())
			{
				IInstallableUnit feature = categories.next(); 
			//	if(StringUtils.equals(newFestureName, feature.getId()))
					existCategories.add(feature);
			}
		}
		
		return existCategories;
	}

	private List<IInstallableUnit> getOlderFeatures(final IProgressMonitor monitor, IProvisioningAgent agent, IInstallableUnit newFeature)
	{	
		List<IInstallableUnit>existFeatures = new ArrayList<IInstallableUnit>();
		
		IQueryable<IInstallableUnit> queryable = ((IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME))
				.getProfile(PROFILE_ID);


		if (queryable != null)
		{
			String newFestureName = newFeature.getId();
			
			IQuery<IInstallableUnit> query = QueryUtil.createIUGroupQuery();
			IQueryResult<IInstallableUnit> result = queryable.query(query, monitor);			
			Iterator<IInstallableUnit> features = result.iterator();
			while (features.hasNext())
			{
				IInstallableUnit feature = features.next(); 
				if(StringUtils.equals(newFestureName, feature.getId()))
					existFeatures.add(feature);
			}
		}
		
		return existFeatures;
	}
	
	/*
	 * Aeltere Features in einer Liste zusammenfassen. Diese Festures werden im weiteren Verlauf deinstalliert.
	 *  
	 */
	private List<IInstallableUnit> getOlderFeaturesOLD(final IProgressMonitor monitor, IProvisioningAgent agent, IInstallableUnit newFeature)
	{	
		List<IInstallableUnit>existFeatures = new ArrayList<IInstallableUnit>();
		
		// Alle verfuegbaren Repositores in einer Liste zusammenfassen
	    IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent.getService(IMetadataRepositoryManager.SERVICE_NAME);
	    URI[] repositoryURIs = manager.getKnownRepositories(IMetadataRepositoryManager.REPOSITORIES_ALL);	    
	    List<IMetadataRepository>repositories = new LinkedList<IMetadataRepository>();
	    int n = repositoryURIs.length;
	    for(int i = 0; i < n; i++)
			try
			{				
				repositories.add(manager.loadRepository(repositoryURIs[i], monitor));
			} catch (ProvisionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OperationCanceledException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    
	    // die zu deinstallierende Feature in den Repositores ermitteln  
	    for (final IMetadataRepository repository : repositories)
	    {
			IQuery<IInstallableUnit> query = QueryUtil.createIUGroupQuery();
			IQueryResult<IInstallableUnit> result = repository.query(query, monitor);
			for (IInstallableUnit feature  : result)
			{		
				// 'newFeature selbst ignorieren
				if(feature.equals(newFeature))
					continue;

				// Versionen ueberprufen, wenn des zu aktualisierende Feature gefungen wurden
				if(StringUtils.equals(feature.getId(), newFeature.getId()))
				{
					Version featureVersion = feature.getVersion();				
					if(featureVersion.compareTo(newFeature.getVersion()) <= 0)
						existFeatures.add(feature);
				}						
			}
	    }
		
		return existFeatures;
	}

	/*
	 * bestehende Kategorien abfagen und in einem Set zurueckgeben
	 */
	private Collection<IInstallableUnit>queryCategories(final IProgressMonitor monitor, IProvisioningAgent agent)
	{		
		Collection<IInstallableUnit>iuSet = new LinkedList<IInstallableUnit>();
		IQueryable<IInstallableUnit> queryable = ((IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME))
				.getProfile(PROFILE_ID);
	
		if (queryable != null)
		{
			// Kategorien abfragen
			IQueryResult<IInstallableUnit> resultCategory = queryable.query(
					QueryUtil.createIUCategoryQuery(), monitor);
			
			Iterator<IInstallableUnit> itMembers = resultCategory.iterator();
			while (itMembers.hasNext())
			{
				IInstallableUnit uiMember = itMembers.next();
				iuSet.add(uiMember);
			}
		}
	
		return iuSet;
	}

	private Collection<IInstallableUnit>queryAvailable(final IProgressMonitor monitor, IProvisioningAgent agent)
	{		
		Collection<IInstallableUnit>iuSet = new LinkedList<IInstallableUnit>();
		IQueryable<IInstallableUnit> queryable = ((IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME))
				.getProfile("DefaultProfile");
	
		if (queryable != null)
		{			
			IQuery<IInstallableUnit> query = QueryUtil.createIUGroupQuery();
			IQueryResult<IInstallableUnit> resultCategory = queryable.query(
					query, monitor);
	
			
			Iterator<IInstallableUnit> itMembers = resultCategory.iterator();
			while (itMembers.hasNext())
			{
				IInstallableUnit uiMember = itMembers.next();
				iuSet.add(uiMember);
			}
		}
	
		return iuSet;
	}

	/*
	 * 
	 */
	private Version getFeatureVersion(IProgressMonitor monitor, IProvisioningAgent agent, String repositoryPath)
	{		
	
		URI repositoryURI = null;
		Collection<IMetadataRepository> metadataReposList = null;
		
		try
		{
			repositoryURI = new URI(repositoryPath);
			if (!repositoryURI.isAbsolute())
			{
				repositoryPath = "file:/" + repositoryPath;
				repositoryURI = new URI(repositoryPath);
			}
		} catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		IMetadataRepositoryManager metaManager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		try
		{
			metadataReposList = new LinkedList<IMetadataRepository>();
			metadataReposList.add(metaManager.loadRepository(repositoryURI, monitor));
			
			IMetadataRepository metadataRepos = metadataReposList.iterator().next();
			
			IQuery<IInstallableUnit> query = QueryUtil.createIUGroupQuery();
			IQueryResult<IInstallableUnit> collector =metadataRepos.query(query, monitor);
			Iterator<IInstallableUnit> iter = collector.iterator();
			return iter.next().getVersion();
			
			/*
			while (iter.hasNext())
			{
				IInstallableUnit iu = iter.next();
				System.out.println(iu.getId()+" | "+iu.getVersion());
			} 
			
			String name = metadataRepos.getName();
			System.out.println(name+" "+metadataRepos.getVersion());
			*/	
			
		} catch (ProvisionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationCanceledException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

				
		
		return null;
	}	
	

}