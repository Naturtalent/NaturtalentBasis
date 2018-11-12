package it.naturtalent.e4.project.expimp.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.expimp.ExpImpProcessor;
import it.naturtalent.e4.project.expimp.ExportResources;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.expimp.dialogs.ProjectExportDialog;
import it.naturtalent.e4.project.ui.datatransfer.RefreshResourcesOperation;
import it.naturtalent.e4.project.ui.emf.ExportProjectPropertiesOperation;

/**
 * Mit dieser Klasse wird der Export von Projekten in ein auszuwaehlendes
 * Verzeichnis ausgefuehrt. Kopiert werden alle Resourcen der ausgewaehleten
 * Projekte. 
 * 
 * Alle verfuegbaren Eigenschftsadapter werden angesprochen und die von dem Adapter gelieferten
 * projektspezifischen Daten werden ueber die Adapter-Export-Funktion exportiert (in einer Datei im
 * Projektbereich gespeichert)
 * 
 * exportieren der Eigenschaften im ProgressMonitor 
 * @see it.naturtalent.e4.project.ui.emf.ExportProjectPropertiesOperation
 * 
 * Dialog zur Auswahl der zuexportierenden Projekte und dem Zielverzeichnis
 * @see it.naturtalent.e4.project.expimp.dialogs.ProjectExportDialog
 * 
 * Adapter der Projecteigenschaften
 * @see it.naturtalent.e4.project.ui.emf.NtProjectProperty
 * 
 * @author dieter
 *
 */
public class ExportAction extends Action
{

	private INtProjectPropertyFactoryRepository projektDataFactoryRepository;

	private Log log = LogFactory.getLog(this.getClass());

	private Shell shell;

	private File exportDestDir;

	private Map<String, List<String>> mapProjectFactories = new HashMap<String, List<String>>();

	@PostConstruct
	private void postConstruct(
			@Optional INtProjectPropertyFactoryRepository projektDataFactoryRepository,
			@Named(IServiceConstants.ACTIVE_SHELL) @Optional Shell shell)
	{
		this.projektDataFactoryRepository = projektDataFactoryRepository;
		this.shell = shell;
	}

	@Override
	public void run()
	{
		final ProjectExportDialog projectExportDialog = new ProjectExportDialog(
				ExpImpProcessor.shell);

		// BusyIndicator - das Einlesen der vorhandenen NtProjecte kann dauern
		BusyIndicator.showWhile(shell.getDisplay(), new Runnable()
		{
			@Override
			public void run()
			{
				// ueber ProjectExportDialog.init() werden die Projekte oder
				// WorkingSets eingelesen
				projectExportDialog.create();
			}
		});

		// Exportmodalitaeten im Dialog festlegen
		if (projectExportDialog.open() == ProjectExportDialog.OK)
		{
			// die zum Export ausgewaehlten Resourcen in einer Liste zusammenfassen
			IResource[] resources = projectExportDialog.getResultExportSource();
			if (ArrayUtils.isEmpty(resources))
				return;

			// das ausgewaelte Zielverzeichnis (hierhin werden die Projekte exportiert)
			exportDestDir = projectExportDialog.getResultDestDir();

			// die Resourcen in eine Liste ueberfuehren
			List<IResource> iResources = Arrays.asList(resources);

			// die Eigenschaften des Projekts werden ueber die
			// Eigenschaftsadapter ermittelt und
			// in einer fuer jeder Eigenschaft spezifische Date im
			// Projektbereich gespeichet
			// zuerst alle definierten AdapterFactories aus dem Repository laden
			List<INtProjectPropertyFactory> projectPropertyFactories = projektDataFactoryRepository
					.getAllProjektDataFactories();

			// dann die Adapter selbst erzeugen und auflisten
			List<INtProjectProperty> projectPropertyAdapters = new ArrayList<INtProjectProperty>();
			for (INtProjectPropertyFactory propertyFactory : projectPropertyFactories)
				projectPropertyAdapters
						.add(propertyFactory.createNtProjektData());
			
			ExportProjectPropertiesOperation exportPropertiesOperation = new ExportProjectPropertiesOperation(
					iResources, projectPropertyAdapters);
			try
			{
				new ProgressMonitorDialog(shell).run(true, false,exportPropertiesOperation);
			} catch (InvocationTargetException e)
			{
				// Error
				Throwable realException = e.getTargetException();
				MessageDialog.openError(shell, Messages.ExportResources_Error,realException.getMessage());
			} catch (InterruptedException e)
			{
				// Abbruch
				MessageDialog.openError(shell, Messages.ExportResources_Cancel,e.getMessage());
				return;
			}

			// da die Eigenschaften in separaten Dateien gespeichert wurden ist
			// ein refresh erforderlich
			RefreshResourcesOperation refreshOperation = new RefreshResourcesOperation(iResources);
			try
			{
				new ProgressMonitorDialog(shell).run(true, false,
						refreshOperation);
			} catch (InvocationTargetException e)
			{
				// Error
				Throwable realException = e.getTargetException();
				MessageDialog.openError(shell, Messages.ExportResources_Error,
						realException.getMessage());
			} catch (InterruptedException e)
			{
				// Abbruch
				MessageDialog.openError(shell, Messages.ExportResources_Cancel,e.getMessage());
				return;
			}

			// abschliessend alle zuexportierenden Ressourcen exportiert
			// (kopiert)
			if (shell != null)
			{
				ExportResources exportResource = new ExportResources(shell);
				exportResource.export(shell, iResources,
						exportDestDir.getPath(),
						projectExportDialog.isArchivState());
			}

			MessageDialog.openInformation(null, "Export", //$NON-NLS-1$
					"Projekte wurden exportiert in das Verzeichnis: " //$NON-NLS-1$
							+ exportDestDir);
		}
	}

}
