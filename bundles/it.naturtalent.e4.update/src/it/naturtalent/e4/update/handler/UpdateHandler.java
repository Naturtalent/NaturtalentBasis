package it.naturtalent.e4.update.handler;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.ui.ProvUIActivator;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.Policy;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.ui.NtPreferences;
import it.naturtalent.e4.update.UpdatePreferenceAdapter;
import it.naturtalent.e4.update.dialogs.SelectInstalledIUDialog;
import it.naturtalent.e4.update.dialogs.UpdatePreferenceLocationDialog;

public class UpdateHandler
{

	boolean cancelled = false;

	private URI repositoryURI = null;
	
	private Log log = LogFactory.getLog(this.getClass());

	@Execute
	public void execute(final IProvisioningAgent agent,
			final UISynchronize sync, final IWorkbench workbench , final Shell parent, 
			@Preference(nodePath = NtPreferences.ROOT_PREFERENCES_NODE,value = UpdatePreferenceAdapter.UPDATESITE_LOCATION_PREFERENCE) String prefLocation)
	{
		// den Path der UpdateSite ueber einen Dialog bereitstellen (mit Praefernzdaten vorbelegt)
		UpdatePreferenceLocationDialog locationDialog = new UpdatePreferenceLocationDialog(parent);
		
		if(StringUtils.isNotEmpty(prefLocation))
		{
			locationDialog.create();
			String [] prefs = StringUtils.split(prefLocation, ",");
			locationDialog.setInitialValues(prefs);
		}
		
		if(locationDialog.open() == UpdatePreferenceLocationDialog.CANCEL)
			return;
		
		// URI erzeugen, ggf. File-Protokoll ergaenzen
		String updateSite = locationDialog.getUpdateSiteLocation();
		try
		{
			repositoryURI = new URI(updateSite);
			if (!repositoryURI.isAbsolute())
			{
				updateSite = "file:/" + updateSite;
				repositoryURI = new URI(updateSite);
			}
		} catch (URISyntaxException e)
		{
			MessageDialog.openError(null, "Updates Speicherort", "fehlerhafte URI-Syntax");
			return;
		}

		// das Updating erfolgt mit Hilfe eines progress monitors
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
		//catch (InvocationTargetException | InterruptedException e) - verursacht Compilererror in tycho
		catch (Exception e)
		{
			log.error("Updateerror: "+e);
		}
	}

	private IStatus update(final IProvisioningAgent agent, IProgressMonitor monitor,
			final UISynchronize sync, final IWorkbench workbench, final Shell parent)
	{
		ProvisioningSession session = new ProvisioningSession(agent);
		UpdateOperation operation = new UpdateOperation(session);
		
		operation.getProvisioningContext().setArtifactRepositories(new URI[]{ repositoryURI });
		operation.getProvisioningContext().setMetadataRepositories(new URI[]{ repositoryURI });

		
		SubMonitor sub = SubMonitor.convert(monitor,
				"Prüfung auf Programmupdates...", 200);
		
		// check if updates are available
		IStatus status = operation.resolveModal(sub.newChild(100));
		if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE)
		{
			showMessage(sync, "keine Updates verfügbar");
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
								"Updates verfügbar",
								"Es sind Updates verfübar. Sollen diese jetzt installiert werden?");
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
																		"Updates installiert, restart?",
																		"Updates wurden erfolgreich installiert, soll die Anwendung neu gestartet werden?");
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
					showError(sync, "Keinen Provisioning Job erhalten: "
							+ operation.getResolutionResult());
				}
				else
				{
					showError(sync, "Keinen Provisioning Job erhalten: ");
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
	
}