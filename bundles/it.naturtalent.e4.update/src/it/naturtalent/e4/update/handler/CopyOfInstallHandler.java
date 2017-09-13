package it.naturtalent.e4.update.handler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IInstallableUnitFragment;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.Update;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.IRepositoryReference;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.e4.core.services.log.Logger;

public class CopyOfInstallHandler
{

	// repository location needs to be adjusted for your
	// location


	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "file:/home/dieter/Naturtalent4/updatesite");


	private IArtifactRepositoryManager artifactManager;
	private IMetadataRepositoryManager metaManager;
	private URI repoLocationURI;
	private InstallOperation operation;
	

	private Logger logger;

	@Execute
	public void execute(final IProvisioningAgent agent, final Shell parent,
			final UISynchronize sync, final IWorkbench workbench, final Logger logger)
	{
		
		this.logger = logger;
		
		Job j = new Job("Update Job")
		{
			private boolean doInstall = false;

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
					IQueryable<IInstallableUnit> allMetadataRepos = QueryUtil.compoundQueryable(metadataReposList);
					Set<IInstallableUnit> toInstallOrUpdate = allMetadataRepos.query(
					        QueryUtil.createIUAnyQuery(), monitor).toUnmodifiableSet();
					
					operation = new InstallOperation(new ProvisioningSession(agent), toInstallOrUpdate);					
					operation.getProvisioningContext().setArtifactRepositories(new URI[]{ repoLocationURI });
					operation.getProvisioningContext().setMetadataRepositories(new URI[]{ repoLocationURI });
					
					
				} catch (ProvisionException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OperationCanceledException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				/* 2. check for updates */
				// run update checks causing I/O
				
				//logger.info("check monitor:  " + monitor);
				//logger.info("check operation:  " + operation);
				
				final IStatus status = operation.resolveModal(monitor);
				
				logger.info("Install Status:  " + status.getMessage());
				logger.info("Install StatusMessage:  " + status.getMessage());

				if (status.isOK())
				{
					final ProvisioningJob provisioningJob = operation.getProvisioningJob(monitor);
					
					// updates cannot run from within Eclipse IDE!!!
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
														"Installation abgeschlossen, restart?",
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
				return Status.OK_STATUS;
			}
		};
		j.schedule();
	}

}