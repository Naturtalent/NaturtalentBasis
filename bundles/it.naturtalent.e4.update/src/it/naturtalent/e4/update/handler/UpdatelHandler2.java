package it.naturtalent.e4.update.handler;

import it.naturtalent.e4.perspectiveswitcher.tools.E4PerspectiveSwitcherPreferences;
import it.naturtalent.e4.update.Activator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.internal.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.p2.operations.PlanAnalyzer;
import org.eclipse.equinox.internal.p2.ui.model.IUElementListRoot;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.engine.query.UserVisibleRootQuery;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.Update;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.e4.core.services.log.Logger;

public class UpdatelHandler2
{

	// repository location needs to be adjusted for your
	// location
	
	private static final String PROFILE_ID = "DefaultProfile";

	/*
	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "file:/home/dieter/Naturtalent4/updatesite");
			*/

	/*
	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "/media/dieter/TOSHIBA1/Updates/Design/repository");
			*/

	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "file:/media/fritzbox/Dokumente/UpdateSites/Design/repository");
	
	
	private IMetadataRepositoryManager metaManager;
	private URI repoLocationURI;
	private UpdateOperation operation;

	

	@Execute
	public void execute(
			final IProvisioningAgent agent,
			final Shell parent,
			final UISynchronize sync,
			final IWorkbench workbench,
			@Preference(nodePath = "/instance/"+Activator.ROOT_UPDATE_PREFERENCES_NODE) final
			IEclipsePreferences prefs)
	{
		
		Job j = new Job("Update Job")
		{
			private boolean doInstall = false;

			@Override
			protected IStatus run(final IProgressMonitor monitor)
			{
				
				/* 1. Prepare update plumbing */
		        final ProvisioningSession session = new ProvisioningSession(agent);
		        final UpdateOperation operation = new UpdateOperation(session);
		        
				// create uri
				repoLocationURI = null;
				try
				{
					repoLocationURI = new URI(prefs.get(Activator.REPOSITORY_PREF, REPOSITORY_LOC));
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
				
				operation.getProvisioningContext().setArtifactRepositories(new URI[]{ repoLocationURI });
				operation.getProvisioningContext().setMetadataRepositories(new URI[]{ repoLocationURI });
				
				/* 2. check for updates */
				
				// run update checks causing I/O
				final IStatus status = operation.resolveModal(monitor);
				
				// failed to find updates (inform user and exit)
				if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE)
				{
					sync.syncExec(new Runnable()
					{
						@Override
						public void run()
						{
							MessageDialog
									.openWarning(parent, "No update",
											"No updates for the current installation have been found");
						}
					});
					return Status.CANCEL_STATUS;
				}

				/* 3. Ask if updates should be installed and run installation */

				// found updates, ask user if to install?
				if (status.isOK() && status.getSeverity() != IStatus.ERROR)
				{
					sync.syncExec(new Runnable()
					{
						@Override
						public void run()
						{
							String updates = "";
							Update[] possibleUpdates = operation
									.getPossibleUpdates();
							for (Update update : possibleUpdates)
							{
								updates += update + "\n";
							}
							doInstall = MessageDialog.openQuestion(parent,
									"Really install updates?", updates);
						}
					});
				}

				// start installation
				if (doInstall)
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
														"Updates installed, restart?",
														"Updates have been installed successfully, do you want to restart?");
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
	
	private static final String JETBRAINS_TEAMCITY_FEATURE_SELECTOR = "it.naturtalent.e4.telekom.feature";

	private static IInstallableUnit[] getExistingFeatures(IProgressMonitor monitor, IProvisioningAgent agent)			
	{
		monitor.beginTask("Looking for installed plugin", 100);
		final ArrayList<IInstallableUnit> out = new ArrayList<IInstallableUnit>();
		final IProfileRegistry profileRegistry = ((IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME));
		final IProfile profile = profileRegistry
				.getProfile(IProfileRegistry.SELF);

		IQuery<IInstallableUnit> query = QueryUtil.createIUAnyQuery();
		IQueryResult<IInstallableUnit> collector = profile.query(query, monitor);
		Iterator<IInstallableUnit> iter = collector.iterator();
		while (iter.hasNext())
		{
			IInstallableUnit iu = iter.next();
			if (iu.getId().startsWith(JETBRAINS_TEAMCITY_FEATURE_SELECTOR)
					&& QueryUtil.isGroup(iu))
			{
				out.add(iu);
			}
		}
		return out.toArray(new IInstallableUnit[out.size()]);
	}
	
	private static IInstallableUnit[] getExistingCategories(IProgressMonitor monitor, IProvisioningAgent agent)			
	{
		final ArrayList<IInstallableUnit> out = new ArrayList<IInstallableUnit>();
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
				out.add(itMembers.next());
		}	
		return out.toArray(new IInstallableUnit[out.size()]);
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

}