 
package it.naturtalent.e4.update.handler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.ui.ProvUIActivator;
import org.eclipse.equinox.internal.p2.ui.model.IUElementListRoot;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.engine.query.UserVisibleRootQuery;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UninstallOperation;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.ui.LoadMetadataRepositoryJob;
import org.eclipse.equinox.p2.ui.Policy;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.update.dialogs.RepositoryLocationDialog;
import it.naturtalent.e4.update.dialogs.SelectableIUsPage;


public class DeinstallHandler
{
	
	private static final String PROFILE_ID = "DefaultProfile";
	
	@Inject
	@Optional
	static Shell shell;
	
	private Logger logger;
	
	private IQuery<IInstallableUnit> visibleInstalledIUQuery = new UserVisibleRootQuery();
	private IQuery<IInstallableUnit> visibleAvailableIUQuery = QueryUtil.createIUGroupQuery();
	
	/*
	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "file:/D:\\Naturtalent4\\updatesite");
			*/
	
	private static final String REPOSITORY_LOC = System.getProperty(
			"UpdateHandler.Repo", "file:/home/dieter/Naturtalent4/installtionsite");
	private URI repoLocationURI;


	
	
	public void executeTEST(final IProvisioningAgent agent, final Logger logger, final Shell parent,
			final UISynchronize sync, final IWorkbench workbench)
	{
		
		RepositoryLocationDialog dialog = new RepositoryLocationDialog(parent);
		dialog.open();
		
	}	
	
	
	@Execute
	public void executeOLD(final IProvisioningAgent agent, final Logger logger, final Shell parent,
			final UISynchronize sync, final IWorkbench workbench)
	{		
		
		this.logger = logger;
		
		Job j = new Job("Deinstall Job")
		{
			@Override
			protected IStatus run(final IProgressMonitor monitor)
			{
				sync.syncExec(new Runnable()
				{
					@Override
					public void run()
					{
																		
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
							Object[]selectObjects = dialog.getSelectedElements();
							if(!ArrayUtils.isEmpty(selectObjects))
							{
								Collection<IInstallableUnit> unInstallUnits = new LinkedList<IInstallableUnit>();
								for(Object obj : selectObjects)
								{
									if(obj instanceof IInstallableUnit)
										unInstallUnits.add((IInstallableUnit) obj);
								}
								
								/*
								IProfile profile = ((IProfileRegistry) agent
										.getService(IProfileRegistry.SERVICE_NAME))
										.getProfile(IProfileRegistry.SELF);
								
								IQuery<IInstallableUnit>query = QueryUtil.createIUQuery("it.naturtalent.e4.office.telekom.ui");
								IQueryResult<IInstallableUnit> resultCategory = profile.query(query, monitor);
								IInstallableUnit[]array =  resultCategory.toArray(IInstallableUnit.class);
								unInstallUnits = Arrays.asList(array);
								*/
								
								
								UninstallOperation operation = new UninstallOperation(new ProvisioningSession(
										agent), unInstallUnits);
								operation.setProvisioningContext(new ProvisioningContext(new ProvisioningSession(agent).getProvisioningAgent()));
								final IStatus status = operation.resolveModal(monitor);
								if (status.isOK())
								{
									final ProvisioningJob provisioningJob = operation
											.getProvisioningJob(monitor);

									// updates cannot run from within Eclipse IDE!!!
									if (provisioningJob == null)
									{
										System.err
												.println("Running update from within Eclipse IDE? This won't work!!!");
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
														// Erfolgsmeldung
														boolean restart = MessageDialog
																.openQuestion(
																		parent,
																		"Deinstallation abgeschlossen, restart?",
																		"Die Deinstallation wurde erfolgreich abgeschlossen, Neustart?");
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
							}
						}
					}
				});
				
				
				return Status.OK_STATUS;
			}

		};
		
		j.schedule();
	}



	private static IInstallableUnit[] getExistingFeatures(IProgressMonitor monitor, IProvisioningAgent agent, String featureID)			
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
			if (iu.getId().startsWith(featureID)
					&& QueryUtil.isGroup(iu))
			{
				out.add(iu);
			}
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


	private Collection<IInstallableUnit>queryVisible(final IProgressMonitor monitor, IProvisioningAgent agent)
	{		
		Collection<IInstallableUnit>iuSet = new LinkedList<IInstallableUnit>();
		IQueryable<IInstallableUnit> queryable = ((IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME))
				.getProfile(PROFILE_ID);

		if (queryable != null)
		{
			// Kategorien abfragen
			IQuery<IInstallableUnit> query = new UserVisibleRootQuery();
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

	
	private IStatus loadMetadataRepository(final IProgressMonitor monitor, IProvisioningAgent agent)
	{
		ProvisioningUI ui = getProvisioningUI(agent);
		LoadMetadataRepositoryJob loadJob = new LoadMetadataRepositoryJob(ui);
		IStatus status = loadJob.runModal(monitor);
		return status;
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