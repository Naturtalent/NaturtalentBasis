package it.naturtalent.e4.update.handler;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.ui.ProvUIActivator;
import org.eclipse.equinox.internal.p2.ui.model.IUElementListRoot;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.Policy;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.update.dialogs.SelectInstalledIUDialog;
import it.naturtalent.e4.update.dialogs.SelectableIUsPage;

public class UpdateFeatureHandler2
{

	
	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "file:/media/fritzbox/Dokumente/UpdateSites/Design/repository");
	
	private String message;
	
	private static final String PROFILE_ID = "DefaultProfile";
	
	boolean cancelled = false;

	private Log log = LogFactory.getLog(this.getClass());
	
	private Collection<IInstallableUnit> unInstallUnits = new LinkedList<IInstallableUnit>();

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
				IInstallableUnit newFeature = getNewFeature(monitor, agent, REPOSITORY_LOC);
				if(newFeature == null)				
				{		
					message = "kein gültigen Featuredaten im angegebenen Verzeichnis gefunden";					
					return;
				}
				
				IInstallableUnit existFeature = isFeatureInstalled(monitor, agent, newFeature);
				
				
				
				// bestehende Kategorien abfragen
				Collection<IInstallableUnit>iuSet = queryCategories(monitor, agent);	
				IInstallableUnit [] iuArray = iuSet.toArray(new IInstallableUnit[iuSet.size()]);
				final IUElementListRoot elementListRoot = new IUElementListRoot(iuArray);
				
				// bestehende Kategorien im Dialog zur Auswahl anbieten
				SelectableIUsPage dialog = new SelectableIUsPage(parent);
				dialog.create();
				dialog.updateCaches(elementListRoot);
				if(dialog.open() == SelectableIUsPage.OK)
				{ 
					Object[] selectObjects = dialog.getSelectedElements();
					if(!ArrayUtils.isEmpty(selectObjects))
					{						
						for(Object obj : selectObjects)
						{
							if(obj instanceof IInstallableUnit)
								unInstallUnits.add((IInstallableUnit) obj);
						}	
					}
					
					else 
					{											
					}
				}				
			}
		});
		
		if(StringUtils.isNotEmpty(message))
			return Status.CANCEL_STATUS;
		
		

		if(unInstallUnits.isEmpty())	
		{
			showError(sync,"kein Feature ausgewählt");
			return Status.CANCEL_STATUS;
		}
		
		
		
		
		
		
		
		ProvisioningSession session = new ProvisioningSession(agent);
		// update the whole running profile, otherwise specify IUs
		UpdateOperation operation = new UpdateOperation(session);
		//configureUpdate(operation);
		
	
			


		
		

		SubMonitor sub = SubMonitor.convert(monitor,
				"Checking for application updates...", 200);
	

		// check if updates are available
		IStatus status = operation.resolveModal(sub.newChild(100));
		if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE)
		{
			showMessage(sync, "Nothing to update");
			return Status.CANCEL_STATUS;
		}
		else
		{
			final ProvisioningJob provisioningJob = operation
					.getProvisioningJob(sub.newChild(100));
			if (provisioningJob != null)
			{
				sync.syncExec(new Runnable()
				{

					@Override
					public void run()
					{
						boolean performUpdate = MessageDialog.openQuestion(null,
								"Updates available",
								"There are updates available. Do you want to install them now?");
						if (performUpdate)
						{
							provisioningJob
									.addJobChangeListener(new JobChangeAdapter()
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
																		null,
																		"Updates installed, restart?",
																		"Updates have been installed successfully, do you want to restart?");
														if (restart)
														{
															workbench.restart();
														}
													}
												});
											}
											else
											{
												showError(sync,
														event.getResult()
																.getMessage());
												cancelled = true;
											}
										}
									});

							// since we switched to the UI thread for
							// interacting with the user
							// we need to schedule the provisioning thread,
							// otherwise it would
							// be executed also in the UI thread and not in a
							// background thread
							provisioningJob.schedule();
						}
						else
						{
							cancelled = true;
						}
					}
				});
			}
			else
			{
				if (operation.hasResolved())
				{
					showError(sync, "Couldn't get provisioning job: "
							+ operation.getResolutionResult());
				}
				else
				{
					showError(sync, "Couldn't resolve provisioning job");
				}
				cancelled = true;
			}
		}

		if (cancelled)
		{
			// reset cancelled flag
			cancelled = false;
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	/*
	private UpdateOperation configureUpdate(final UpdateOperation operation)
	{
		// create uri and check for validity
		URI uri = null;
		try
		{
			uri = new URI(REPOSITORY_LOC);
		} catch (final URISyntaxException e)
		{
			log.error(e);
			return null;
		}

		// set location of artifact and metadata repo
		operation.getProvisioningContext().setArtifactRepositories(new URI[]
			{ uri });
		operation.getProvisioningContext().setMetadataRepositories(new URI[]
			{ uri });
		return operation;
	}
	*/

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
	
	/*
	 * Ueberpruefen, ob das Feature bereits installiert ist.
	 *  
	 */
	private IInstallableUnit isFeatureInstalled(final IProgressMonitor monitor, IProvisioningAgent agent, IInstallableUnit newFeature)
	{	
		// Alle Repositores in einer Liste zusammenfassen
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
	    
	    // alle Features in den Repositores abfragen 
	    for (final IMetadataRepository repository : repositories)
	    {
			IQuery<IInstallableUnit> query = QueryUtil.createIUGroupQuery();
			IQueryResult<IInstallableUnit> result = repository.query(query, monitor);
			for (IInstallableUnit feature  : result)
			{		
				// 'newFeature selbst ignorieren
				if(feature.equals(newFeature))
					continue;

				// 
				if(StringUtils.equals(feature.getId(), newFeature.getId()))
				{
					System.out.println("gleiche Features");
				}
						
						
				
				/*
				Version checkVersion = iu.getVersion();				
				if(checkVersion.compareTo(existVersion) <= 0)
				{
					System.out.println();
				}
				*/
				
				
				
				//String UIid = iu.getId()+" "+iu.getVersion();
				//System.out.println(UIid);
			}
	    }
		
		
		IProfileRegistry installerRegistry = (IProfileRegistry) ((IProvisioningAgent) agent.getService(IProvisioningAgent.INSTALLER_AGENT)).getService(IProfileRegistry.SERVICE_NAME);
		//IProfile installerProfile = installerRegistry.getProfile((String) agent.getService(IProvisioningAgent.INSTALLER_PROFILEID));
		IProfile [] profiles = installerRegistry.getProfiles();
		IProfile installerProfile = installerRegistry.getProfile(PROFILE_ID);
		
		
		
		//ProvisioningSession session = new ProvisioningSession(agent);
		//IProfile profile = ((IProfileRegistry) agent.getService(IProfileRegistry.SERVICE_NAME)).getProfile(IProfileRegistry.SELF);
		//IProfile profile = ((IProfileRegistry) agent.getService(IProfileRegistry.SERVICE_NAME)).getProfile(IProfileRegistry.SELF);
		
		/*
		if(installerProfile != null)
		{
			IQueryResult<IInstallableUnit> alreadyInstalled = installerProfile.query(QueryUtil.createIUQuery(id), null);
		}
		*/
		

	    /*
		IQuery<IInstallableUnit> query = QueryUtil.createIUGroupQuery();
		IQueryResult<IInstallableUnit> result = repository.query(query, monitor.newChild(1));
		*/
		
		
		/*
		Collection<IInstallableUnit>iuSet = new LinkedList<IInstallableUnit>();
		IQueryable<IInstallableUnit> queryable = ((IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME))
				.getProfile(PROFILE_ID);
	
		if (queryable != null)
		{
			// Kategorien abfragen
			IQueryResult<IInstallableUnit> resultCategory = queryable.query(
					QueryUtil.createIUQuery(id), monitor);
			
			Iterator<IInstallableUnit> itMembers = resultCategory.iterator();
			while (itMembers.hasNext())
			{
				IInstallableUnit uiMember = itMembers.next();
				iuSet.add(uiMember);
			}
		}
		*/
	
		return null;
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