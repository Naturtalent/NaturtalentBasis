package it.naturtalent.e4.update.handler;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.ui.ProvUIActivator;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
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

public class InstallHandler
{

	// repository location needs to be adjusted for your
	// location

	private static final String PROFILE_ID = "DefaultProfile";
	

	/*
	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "file://home/dieter/InstallationSites/Design/repository");
			*/
		

	/*
	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "file:/media/dieter/TOSHIBA1/Updates/Design/repository/");
			*/
	
	
/*
	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "file:/media/fritzbox/Dokumente/UpdateSites/Design/repository");
			*/


	/*
	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "http://download.eclipse.org/eclipse/updates/4.7");
			*/


	private String REPOSITORY_LOC;
	
	private IArtifactRepositoryManager artifactManager;
	private IMetadataRepositoryManager metaManager;
	private URI repoLocationURI;
	private InstallOperation operation;
	

	private Log log = LogFactory.getLog(this.getClass());
	
	private boolean cancelled = false;
	
	private IStatus status = null;
	
	@Execute
	public void execute(final IProvisioningAgent agent, final Shell parent,
			final UISynchronize sync, final IWorkbench workbench)
	{
		
		InstallLocationDialog locationDialog = new InstallLocationDialog(parent);
		if(locationDialog.open() == InstallLocationDialog.CANCEL)
			return;
		
		REPOSITORY_LOC = locationDialog.getInstallLocation();

		// update using a progress monitor
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException
			{

				install(agent, monitor, sync, workbench);
			}

		};

		try
		{
			new ProgressMonitorDialog(null).run(true, true, runnable);
		} 
			//catch (InvocationTargetException | InterruptedException e)
			catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	private IStatus install(IProvisioningAgent agent,IProgressMonitor monitor, final UISynchronize sync,final IWorkbench workbench)
	{
		/* 1. Prepare update plumbing */
		//final ProvisioningSession session = new ProvisioningSession(agent);

		// create uri
		repoLocationURI = null;
		try
		{			
			repoLocationURI = new URI(REPOSITORY_LOC);	
			
			if(!repoLocationURI.isAbsolute())
			{
				REPOSITORY_LOC = "file:/"+REPOSITORY_LOC;
				repoLocationURI = new URI(REPOSITORY_LOC);
			}

			// Repos laden
			metaManager = (IMetadataRepositoryManager) agent
					.getService(IMetadataRepositoryManager.SERVICE_NAME);	
						
			Collection<IMetadataRepository> metadataReposList = new LinkedList<IMetadataRepository>(); 
			metadataReposList.add(metaManager.loadRepository(repoLocationURI, monitor));
			
			// Querying
			IQueryable<IInstallableUnit>queryable = QueryUtil.compoundQueryable(metadataReposList);
			Set<IInstallableUnit> toInstallOrUpdate = queryable.query(
			        QueryUtil.createIUCategoryQuery(), monitor).toUnmodifiableSet();
						
			operation = new InstallOperation(new ProvisioningSession(agent), toInstallOrUpdate);
			operation.getProvisioningContext().setArtifactRepositories(new URI[]{ repoLocationURI });
			operation.getProvisioningContext().setMetadataRepositories(new URI[]{ repoLocationURI });

		} catch (ProvisionException e)
		{			
			log.error(e);
			showError(sync, "ProvisionException");
			return Status.CANCEL_STATUS;			
		} catch (OperationCanceledException e)
		{			
			log.error(e);
			showError(sync, "Abbruch");
			return Status.CANCEL_STATUS;
		} catch (URISyntaxException e)
		{
			showMessage(sync, "fehlerhafte Location");
			return Status.CANCEL_STATUS;
		}
		
		SubMonitor sub = SubMonitor.convert(monitor, "Installation...", 200);
		
		
		try
		{
			status = operation.resolveModal(sub.newChild(100));
		} catch (Exception e)
		{
			showError(sync, "Running  from within Eclipse IDE? This won't work!!!");
			return Status.CANCEL_STATUS;
		}
		
		if(!status.isOK())
		{
			showMessage(sync, "Nothing to install");
            return Status.CANCEL_STATUS;
		}
			
    	final ProvisioningJob provisioningJob = operation.getProvisioningJob(sub.newChild(100));
    	
		// install cannot run from within Eclipse IDE!!!
		if (provisioningJob == null)
		{
			showError(sync, "Running from within Eclipse IDE? This won't work!!!");
			return Status.CANCEL_STATUS;	
		}

		else
		{
			sync.syncExec(new Runnable()
			{
				@Override
				public void run()
				{
					boolean performUpdate = MessageDialog.openQuestion(null,
							"Installation verfügbar",
							"jetzt installieren?");
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
											
											// die Installation wurde erfolgreich beendet - Settings aktualisieren
											IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
											String[]locations = settings.getArray(InstallLocationDialog.INSTALL_LOCATION_SETTINGS);
											if (!ArrayUtils.contains(locations, REPOSITORY_LOC))
											{
												// max. 10 Setting-Eintraege werden gespeichert
												locations = ArrayUtils.add(locations, 0, REPOSITORY_LOC);
												if (locations.length > 9)
													locations = ArrayUtils.remove(locations,9);						
												settings.put(InstallLocationDialog.INSTALL_LOCATION_SETTINGS,locations);
											}
	
											sync.syncExec(new Runnable()
											{

												@Override
												public void run()
												{
													boolean restart = MessageDialog
															.openQuestion(null,
																	"Installation",
																	"Die Installation wurde erfolgreich abgeschlossen, Neustart?");
													if (restart)
													{
														workbench.restart();
													}
												}
											});
										}
										else
										{
											showError(sync, event.getResult().getMessage());
											cancelled = true;

											// versuchen im Log zusätzliche Fehlerinfos zu hinterlegen 
											IStatus planStatus = operation.getProvisioningPlan().getStatus();
											if(planStatus.isMultiStatus())
											{
												IStatus [] planMultiStatus = planStatus.getChildren();
												for(IStatus multi : planMultiStatus)
													log.error("Multi - "+multi.getMessage());
											}
											log.error("Install resolving error - "+status);

										}
									}
								});

						// since we switched to the UI thread for interacting
						// with the user
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
		
		return Status.OK_STATUS;
	}
	
    private void showMessage(UISynchronize sync, final String message) {
        // as the provision needs to be executed in a background thread
        // we need to ensure that the message dialog is executed in 
        // the UI thread
        sync.syncExec(new Runnable() {
            
            @Override
            public void run() {
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
	
	
	/*
	 * 
	 * 
	 */

	public void executeOLD(final IProvisioningAgent agent, final Shell parent,
			final UISynchronize sync, final IWorkbench workbench)
	{
		
		InstallLocationDialog locationDialog = new InstallLocationDialog(parent);
		if(locationDialog.open() == InstallLocationDialog.CANCEL)
			return;
		
		REPOSITORY_LOC = locationDialog.getInstallLocation();
		
		final Job j = new Job("Install Job")
		{
			@Override
			protected IStatus run(final IProgressMonitor monitor)
			{				
				/* 1. Prepare update plumbing */
				final ProvisioningSession session = new ProvisioningSession(agent);

				// create uri
				repoLocationURI = null;
				try
				{
					repoLocationURI = new URI(REPOSITORY_LOC);	
					
					if(!repoLocationURI.isAbsolute())
					{
						REPOSITORY_LOC = "file:/"+REPOSITORY_LOC;
						repoLocationURI = new URI(REPOSITORY_LOC);
					}
					
				} catch (final URISyntaxException e)
				{
					sync.syncExec(new Runnable()
					{
						@Override
						public void run()
						{
							MessageDialog.openError(parent, "URI invalid",e.getMessage());
						}
					});
					return Status.CANCEL_STATUS;
				}

				try
				{										
					// Repos laden
					metaManager = (IMetadataRepositoryManager) agent
							.getService(IMetadataRepositoryManager.SERVICE_NAME);	
					
					
					Collection<IMetadataRepository> metadataReposList = new LinkedList<IMetadataRepository>(); 
					metadataReposList.add(metaManager.loadRepository(repoLocationURI, monitor));
					
					// Querying
					IQueryable<IInstallableUnit>queryable = QueryUtil.compoundQueryable(metadataReposList);
					Set<IInstallableUnit> toInstallOrUpdate = queryable.query(
					        QueryUtil.createIUCategoryQuery(), monitor).toUnmodifiableSet();
					
					
					operation = new InstallOperation(new ProvisioningSession(agent), toInstallOrUpdate);
					operation.getProvisioningContext().setArtifactRepositories(new URI[]{ repoLocationURI });
					operation.getProvisioningContext().setMetadataRepositories(new URI[]{ repoLocationURI });

				} catch (ProvisionException e)
				{
					// TODO Auto-generated catch block
					log.error(e);
					e.printStackTrace();
				} catch (OperationCanceledException e)
				{
					// TODO Auto-generated catch block
					log.error(e);
					e.printStackTrace();
				}
								
				/* 2. check for updates */
				// run update checks causing I/O
				final IStatus status = operation.resolveModal(monitor);
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
								// die Installation wurde erfolgreich beendet - Settings aktualisieren
								IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
								String[]locations = settings.getArray(InstallLocationDialog.INSTALL_LOCATION_SETTINGS);
								if (!ArrayUtils.contains(locations, REPOSITORY_LOC))
								{
									// max. 10 Setting-Eintraege werden gespeichert
									locations = ArrayUtils.add(locations, 0, REPOSITORY_LOC);
									if (locations.length > 9)
										locations = ArrayUtils.remove(locations,9);						
									settings.put(InstallLocationDialog.INSTALL_LOCATION_SETTINGS,locations);
								}
								
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
							else
							{
								sync.syncExec(new Runnable()
								{
									@Override
									public void run()
									{
										MessageDialog
												.openError(
														parent,
														"Installation neuer Software",
														"Fehler bei der Installation");										
									}
								});
								
								IStatus planStatus = operation.getProvisioningPlan().getStatus();
								if(planStatus.isMultiStatus())
								{
									IStatus [] planMultiStatus = planStatus.getChildren();
									for(IStatus multi : planMultiStatus)
										log.error("Multi - "+multi.getMessage());
								}
								log.error("Install resolving error - "+status);
							}
							
							super.done(event);
						}
					});

					provisioningJob.schedule();
				}
				
				else
				{
					log.error("Install resolving error - "+status);
					IStatus planStatus = operation.getProvisioningPlan().getStatus();
					if(planStatus.isMultiStatus())
					{
						IStatus [] planMultiStatus = planStatus.getChildren();
						for(IStatus multi : planMultiStatus)
							log.error("Multi - "+multi.getMessage());
					}
					
					sync.syncExec(new Runnable()
					{
						@Override
						public void run()
						{
							MessageDialog.openInformation(parent,
											"Installation neuer Software",
											"Installationserror - siehe Logfile");
						}
					});

				}
				
				return Status.OK_STATUS;
			}
		};
		j.schedule();
	}
	
	private Collection<IInstallableUnit>queryAvailable(final IProgressMonitor monitor, IProvisioningAgent agent)
	{		
		Collection<IInstallableUnit>iuSet = new LinkedList<IInstallableUnit>();
		IQueryable<IInstallableUnit> queryable = ((IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME))
				.getProfile(PROFILE_ID);

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
	
	private ProvisioningUI getProvisioningUI(IProvisioningAgent agent)
	{
		Policy policy = (Policy) ServiceHelper.getService(ProvUIActivator.getContext(), Policy.class.getName());
		if (policy == null)
			policy = new Policy();
		
		final ProvisioningSession session = new ProvisioningSession(agent);
		return new ProvisioningUI(session, IProfileRegistry.SELF, policy);		
	}

}