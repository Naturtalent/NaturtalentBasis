package it.naturtalent.e4.project.expimp.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.application.IPreferenceAdapter;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.expimp.dialogs.ExportNtProjektDialog.NameFilter;
import it.naturtalent.e4.project.ui.navigator.WorkbenchLabelProvider;
import it.naturtalent.e4.project.ui.ws.AggregateWorkingSet;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSetRoot;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Dialog zur Auswahl des Exportverzeichnisses und der zuexortierenden Projekte.
 * 
 * @author dieter
 *
 */
public class ExportNtProjectDialog extends TitleAreaDialog
{

	public static final String EXPORT_DIRECTORY = "exportDirectory"; //$NON-NLS-1$

	private IProject[] resultExportProjects;

	private File resultExportDir;

	private CCombo exportComboDir;

	private Button btnWorkingSet;

	private Button btnProject;

	private CheckboxTreeViewer checkboxTreeViewer;

	private Button okButton;

	// DialogSettings
	private static final String EXPORT_DESTDIRS_SETTINGS = "export_sourcedirs_settings"; //$NON-NLS-1$

	private static final String EXPORT_DEST_SETTINGS = "export_source_settings"; //$NON-NLS-1$
	// private static final String EXPORT_OPTION_SETTINGS =
	// "export_option_settings"; //$NON-NLS-1$

	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();

	WorkingSetManager wsManager = it.naturtalent.e4.project.ui.Activator.getWorkingSetManager();

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ExportNtProjectDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		setTitleImage(Icon.WIZBAN_EXPORT
				.getImage(IconSize._75x66_TitleDialogIconSize));
		setMessage(Messages.SelectExportDialog_this_message);
		setTitle(Messages.SelectExportDialog_this_title);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		GridData gd_container = new GridData(GridData.FILL_BOTH);
		gd_container.grabExcessHorizontalSpace = false;
		container.setLayoutData(gd_container);

		// Exportverzeichnis auswaehlen
		Composite compositeExportdir = new Composite(container, SWT.NONE);
		compositeExportdir.setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeExportdir.setLayout(new GridLayout(3, false));

		Label lblExportDir = new Label(compositeExportdir, SWT.NONE);
		lblExportDir.setBounds(0, 0, 55, 15);
		lblExportDir.setText("Exportverzeichnis");

		// Combo Zielverzeichnisses - nur Selektion keine aktive Eingabe
		exportComboDir = new CCombo(compositeExportdir, SWT.BORDER);
		exportComboDir.setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		exportComboDir.setEnabled(false);

		// Zielverzeichnis aendern
		Button btnBrowse = new Button(compositeExportdir, SWT.NONE);
		btnBrowse.setText("Browse");
		btnBrowse.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dlg = new DirectoryDialog(getShell());

				// Set the initial filter path according
				// to anything they've selected or typed in
				dlg.setFilterPath(exportComboDir.getText());

				// Change the title bar text
				dlg.setText("Exportverzeichnis"); //$NON-NLS-1$

				// Customizable message displayed in the dialog
				dlg.setMessage("Zielordner der Exportdaten auswählen"); //$NON-NLS-1$

				// Calling open() will open and run the dialog.
				// It will return the selected directory, or
				// null if user cancels
				String dir = dlg.open();
				if (dir != null)
				{
					// Set the text box to the new selection
					exportComboDir.setText(dir);
				}
			}
		});

		// Gruppe Quelle
		Group group = new Group(container, SWT.NONE);
		group.setLayout(new RowLayout(SWT.HORIZONTAL));
		group.setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		group.setText(Messages.SelectExportDialog_group_text);

		btnWorkingSet = new Button(group, SWT.RADIO);
		btnWorkingSet.setText(Messages.SelectExportDialog_btnWorkingsets_text);
		btnWorkingSet.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (((Button) e.getSource()).getSelection())
					checkboxTreeViewer.setInput(
							new WorkingSetRoot(wsManager.getWorkingSets()));
			}
		});

		btnProject = new Button(group, SWT.RADIO);
		btnProject.setText(Messages.SelectExportDialog_btnProjects_text);
		btnProject.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (((Button) e.getSource()).getSelection())
					checkboxTreeViewer.setInput(getAggregateSet());
			}
		});

		checkboxTreeViewer = new CheckboxTreeViewer(container, SWT.BORDER);
		checkboxTreeViewer.setLabelProvider(new WorkbenchLabelProvider());
		checkboxTreeViewer.setContentProvider(new ExportSelectContentProvider());
		checkboxTreeViewer.setComparator(new ViewerComparator());
		
		
		
		// checkboxTreeViewer.addFilter(new NameFilter());
		checkboxTreeViewer.addCheckStateListener(new ICheckStateListener()
		{
			@Override
			public void checkStateChanged(CheckStateChangedEvent event)
			{
				Object node = event.getElement();
				if (event.getChecked())
				{
					checkboxTreeViewer.setSubtreeChecked(node, true);
				}
				else
				{
					checkboxTreeViewer.setSubtreeChecked(node, false);
				}

				updateWidgets();
			}
		});

		Tree tree = checkboxTreeViewer.getTree();
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_tree.widthHint = 100;
		tree.setLayoutData(gd_tree);

		Composite compositeSelectButtons = new Composite(container, SWT.NONE);
		compositeSelectButtons.setLayout(new GridLayout(2, false));

		Button btnAllSelect = new Button(compositeSelectButtons, SWT.NONE);
		btnAllSelect.setSize(75, 25);
		btnAllSelect.setText(Messages.SelectExportDialog_btnAllSelect_text);
		btnAllSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setAllChecked(checkboxTreeViewer.getInput(), true);
				updateWidgets();
			}
		});

		Button btnNoSelect = new Button(compositeSelectButtons, SWT.NONE);
		btnNoSelect.setText(Messages.SelectExportDialog_btnNoSelect_text);
		btnNoSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setAllChecked(checkboxTreeViewer.getInput(), false);
				updateWidgets();
			}
		});

		init();
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		okButton = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, false);
		okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(700, 700);
	}

	/*
	 * SetAllChecked auf Viewerebene als Alternative zum deprecated
	 * 'CheckViewer.setAllChecked(boolean state)'
	 */
	private void setAllChecked(Object input, boolean state)
	{
		if (input != null)
		{
			if (input instanceof AggregateWorkingSet)
			{
				AggregateWorkingSet workingSet = (AggregateWorkingSet) input;
				IAdaptable[] adaptables = workingSet.getElements();
				for (IAdaptable adaptable : adaptables)
					checkboxTreeViewer.setSubtreeChecked(adaptable, state);
			}
			else
			{
				WorkingSetRoot workingSetRoot = (WorkingSetRoot) input;
				IWorkingSet[] workingSets = workingSetRoot.getWorkingSets();
				for (IWorkingSet iWorkingSet : workingSets)
					checkboxTreeViewer.setSubtreeChecked(iWorkingSet, state);
			}
		}
	}

	/*
	 * UI-Elemente mit Settingwerten initialisieren
	 */
	private void init()
	{
		// Setting: Zielverzeichnisse
		String[] sourcePaths = settings.getArray(EXPORT_DESTDIRS_SETTINGS);
		exportComboDir.setItems(sourcePaths);
		checkExportDirectory(sourcePaths[0]);

		// Setting: Quelle (Projekte oder Workingsets)
		final boolean btnState = settings.getBoolean(EXPORT_DEST_SETTINGS);

		// DialogButtons entsprechend dis-/enablen
		btnProject.setSelection(btnState);
		btnWorkingSet.setSelection(!btnState);

		// CheckboxViewer mit Projekten / WorkingSets fuellen
		if (!btnState)
			checkboxTreeViewer
					.setInput(new WorkingSetRoot(wsManager.getWorkingSets()));
		else
			checkboxTreeViewer.setInput(getAggregateSet());
	}

	/*
	 * uebeprueft das uebergebenen Exportverzeichnis auf Existenz und schaltet
	 * im Fehlerfall auf temporaeres Verzeichnis
	 */
	private void checkExportDirectory(String dirPath)
	{
		if (StringUtils.isNotEmpty(dirPath))
		{
			File checkDir = new File(dirPath);
			if (checkDir.exists() && checkDir.isDirectory())
			{
				exportComboDir.setText(dirPath);
				return;
			}
		}

		// temporaeres Verzeichnis ist Defaultverzeichnis
		IEclipsePreferences instancePreferenceNode = InstanceScope.INSTANCE
				.getNode(IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE);
		String tmpPath = instancePreferenceNode.get(
				IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY, null);
		File checkDir = new File(tmpPath);
		if (checkDir.exists() && checkDir.isDirectory())
			exportComboDir.setText(dirPath);
	}

	private void updateWidgets()
	{
		okButton.setEnabled((ArrayUtils.isNotEmpty(getCheckedProjects())));
	}

	/*
	 * die vorhandenen WorkingSets ermitteln
	 */
	static IWorkingSet aggregateResourceSet = null;

	private IWorkingSet getAggregateSet()
	{
		if (aggregateResourceSet == null)
		{
			IWorkingSet[] workingSets = wsManager.getWorkingSets();
			aggregateResourceSet = wsManager.createAggregateWorkingSet(
					"tempAggregate", "", workingSets);
		}

		return aggregateResourceSet;
	}

	@Override
	protected void okPressed()
	{
		// das ausgewaehlte Zielverzeichnis fuer spaetere Verwendung sichern
		resultExportDir = new File(exportComboDir.getText());

		// der ausgewaehlte Verzeichnispfad wird um einen unique-Namen ergaenzt
		String exportDirName = getAutoFileName(resultExportDir,EXPORT_DIRECTORY);
		resultExportDir = new File(resultExportDir, exportDirName);
		
		// Exportverzeichnis physikalisch anlegen
		if(!resultExportDir.mkdir())
		{
			MessageDialog
					.openError(
							getShell(),
							Messages.SelectExportDialog_DestDirectoryError,
							"Das Exportverzeichnis konnte nicht erzeugt werden"); //$NON-NLS-N$
			return;
		}

		// die gecheckten Projekte sichern (damit Abfrage noch moeglich ist auch wenn
		// CheckboxViewer bereits disposed)
		resultExportProjects = getCheckedProjects();

		storeSettings();
		super.okPressed();
	}

	/*
	 * Rueckgabe eines Arrays mit den gecheckten Projekten. Gecheckte
	 * WorkingSets werden ignoriert.
	 */
	private IProject[] getCheckedProjects()
	{
		Object[] result = checkboxTreeViewer.getCheckedElements();

		// die ausgewaehlten Projekte fuer spaetere Verwendung sichern
		if (ArrayUtils.isNotEmpty(result))
		{
			List<IProject> projects = new ArrayList<IProject>();
			for (Object obj : result)
			{
				if (obj instanceof IProject)
					projects.add((IProject) obj);
			}
			return projects.toArray(new IProject[projects.size()]);
		}

		return null;
	}

	/*
	 * einige Einstellungen und Selektionen im DialogSetting speichern - Status
	 * Projekt/Workingset - Zielverzeichnis (es wird nur eine maximale Anzahl
	 * (n=5) gespeichert - ZielOptionen (Filesystem, Zip-Datei)
	 */
	private void storeSettings()
	{
		String[] sourcePaths = null;

		sourcePaths = ArrayUtils.addAll(sourcePaths, exportComboDir.getItems());
		sourcePaths = ArrayUtils.removeElement(sourcePaths,
				exportComboDir.getText());
		sourcePaths = ArrayUtils.insert(0, sourcePaths,
				exportComboDir.getText());
		if (sourcePaths.length >= 6)
			sourcePaths = ArrayUtils.removeAll(sourcePaths, 5);

		settings.put(EXPORT_DESTDIRS_SETTINGS, sourcePaths);

		// Status btnProject (Quelle Projekt / WorkingSet)
		settings.put(EXPORT_DEST_SETTINGS, btnProject.getSelection());
	}

	public IProject[] getResultExportSource()
	{
		return resultExportProjects;
	}

	/*
	 * Rueckgabe des Verzeichnisses in welches die Projekte exportiert werden
	 * sollen.
	 * 
	 */
	public File getResultDestDir()
	{
		return resultExportDir;
	}

	public boolean isArchivState()
	{
		return false;
	}

	/*
	 * Auf Basis des 'originalExportName' wird ein neuer Verzeichnisame
	 * generiert, der in dem Parentverzeichnis 'dir' einmalig ist.
	 * 
	 */
	private static String getAutoFileName(File dir, String originalExportName)
	{
		String autoFileName;

		if (dir == null)
			return ""; //$NON-NLS-1$

		int counter = 1;
		while (true)
		{
			if (counter > 1)
			{
				autoFileName = FilenameUtils.getBaseName(originalExportName)
						+ new Integer(counter);
			}
			else
			{
				autoFileName = originalExportName;
			}
			File res = new File(dir, autoFileName);
			if (!res.exists())
				return autoFileName;

			counter++;
		}
	}

}
