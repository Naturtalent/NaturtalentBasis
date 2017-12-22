package it.naturtalent.e4.project.expimp.dialogs;

import java.io.File;
import java.io.FilenameFilter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.wb.swt.SWTResourceManager;

import it.naturtalent.application.IPreferenceAdapter;
import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.dialogs.ConfigureWorkingSetDialog;
import it.naturtalent.e4.project.ui.dialogs.SelectWorkingSetDialog;
import it.naturtalent.e4.project.ui.emf.NtProjectProperty;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

/**
 * Mit diesem Dialog wird das Quellverzeichnis der Importdateien ausgewaehlt.
 * Im ausgewaehlten Verzeichnis werden NtProjekte (Verzeichnissnamen = ProjektID's) und die EMFStore 
 * Exportdateinen (*.xmi) erwartet. 
 * Die EMFStore-Dateien beinhalten die dem jeweiligen NtProjekt zugeordneten Property Informationen.
 * Unverzichtbar ist die "ECPProject.xmi" - Datei. Diese Datei beinhaltet u.a. den eigentlichen Namen des NtProjekts.
 * 
 * @author dieter
 *
 */
public class ProjectImportDialog extends TitleAreaDialog
{
	
	private final static String ECP_NTPROJECT_PROPERTYFILE = "ECPProject.xmi";
	
	private DataBindingContext m_bindingContext;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/*
	 * Filter nach einem String in 'stgFilter'
	 */
	public class NameFilter extends ViewerFilter
	{
		@Override
		public boolean select(Viewer viewer, Object parentElement,Object element)
		{			
			if (element instanceof NtProject)
			{				
				if(StringUtils.isNotEmpty(stgFilter))
				{
					String projektName = ((NtProject)element).getName();
					return StringUtils.containsIgnoreCase(projektName, stgFilter);					
				}				
			}			
			return true;
		}
	}
	
	/*
	 * Sortiert die Anzeige im TreeViewer
	 */
	private class Sorter extends AbstractTableSorter
	{

		public Sorter(CheckboxTableViewer viewer, TableViewerColumn column)
		{
			super(viewer, column);			
		}

		Collator collator = Collator.getInstance(Locale.GERMAN);
		
		@Override
		protected int doCompare(Viewer viewer, Object e1, Object e2)
		{
			String stgValue1, stgValue2;
			
			stgValue1 = ((NtProject)e1).getName();
			stgValue2 = ((NtProject)e2).getName();			
			if (StringUtils.isNotEmpty(stgValue1)
					&& StringUtils.isNotEmpty(stgValue2))
				return collator.compare(stgValue1, stgValue2);
			
			return 0;
		}
	}


	/*
	 * Realisiert ein Modell fuer Databindings
	 */
	public class ImportSourceDirectory
	{
		String srcDir;

		public String getSrcDir()
		{
			return srcDir;
		}

		public void setSrcDir(String srcDir)
		{
			this.srcDir = srcDir;
		}
	}
	
	/**
	 * Interne Klasse zum ueberpruefen des Textfeldes 'applicationText'
	 * 
	 * @author dieter
	 * 
	 */
	public class EmptyStringValidator implements IValidator
	{
		public EmptyStringValidator()
		{
			super();
		}

		@Override
		public IStatus validate(Object value)
		{
			if (StringUtils.isNotEmpty((String) value))
			{				
				controlDecoration.hide();	
				updateWidgets();
				return Status.OK_STATUS;
			}
			else
			{			
				controlDecoration.show();
				updateWidgets();
				return ValidationStatus.error(Messages.SelectExportDialog_controlDecoration_descriptionText);
			}
		}
	}

	// liefert den Namen des NtProjekts
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return Icon.ICON_PROJECT.getImage(IconSize._16x16_DefaultIconSize);
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if(element instanceof NtProject)
			{		
				if(checkboxTableViewer.getGrayed(element))
				{
					Display display = getShell().getDisplay();					
					Color gray = display.getSystemColor(SWT.COLOR_GRAY);					
					TableItem tableItem = (TableItem) checkboxTableViewer.testFindItem(element);
					tableItem.setForeground(gray);
				}
				
				return ((NtProject)element).getName();
			}
			
			return element.toString();
		}
	}
	
	// beinhaltet die zu importierenden NtProjectProperties
	private static class ContentProvider implements IStructuredContentProvider
	{
		private EObject[] ntProjects;
		
		public Object[] getElements(Object inputElement)
		{
			return ntProjects;
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			ntProjects = new EObject[0];
			if(newInput instanceof EList)
			{
				EList inputProperties = (EList) newInput; 
				ntProjects = (EObject[]) ((EList) newInput).toArray(new NtProject[inputProperties.size()]);
			}
		}
	}
	
	private ImportSourceDirectory importSourceDirectory = new ImportSourceDirectory();
	
	private Table table;
	private CheckboxTableViewer checkboxTableViewer;
	private Combo comboSourceDir;
	private Button okButton;
	private EObject [] resultImportEobjects;
	private ControlDecoration controlDecoration;
	private Button btnWorkingSets;
	private CCombo comboWorkingSets;
	private Button btnBrowseWorkingset;
	
	public static final String IMPORT_SOURCEDIRS_SETTINGS = "import_sourcedirs_settings"; //$NON-NLS-1$
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	// Liste der zugeordneten WorkingSets	
	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();
	
	private String stgFilter;
	
	/*
	 * Der FileFilter akzeptiert alle Verzeichnisse, die selbst ein Unterverzeichnis IProjectData.PROJECTDATA_FOLDER und
	 * hier eine ProjectProperty-Datei IProjectData.PROJECTDATAFILE gespeichert ist.
	 * (In dieser Datei sind die dem NtProject zugeordnete ProjectPropertyFactory-Klassennamen gespeichert)    
	 */
	private FilenameFilter projectFileFilter = new FilenameFilter()
	{		
		@Override
		public boolean accept(File dir, String name)
		{
			File checkDir = new File(dir,name);
			checkDir = new File(checkDir, IProjectData.PROJECTDATA_FOLDER);
			return (new File(checkDir,IProjectData.PROJECTDATAFILE).exists());
		}
	};
	private Text txtSeek;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ProjectImportDialog(Shell parentShell)
	{
		super(parentShell);		
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		setTitleImage(SWTResourceManager.getImage(ProjectImportDialog.class, "/icons/full/wizban/import_wiz.png")); //$NON-NLS-1$
		setMessage(Messages.SelectImportDialog_this_message);
		setTitle(Messages.SelectImportDialog_this_title);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblSource = new Label(container, SWT.NONE);
		lblSource.setText(Messages.SelectImportDialog_lblSource_text);
		
		comboSourceDir = new Combo(container, SWT.BORDER);		
		comboSourceDir.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				initSourceTable(comboSourceDir.getText());
			}
		});
		comboSourceDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboSourceDir.setEnabled(false);
		
		controlDecoration = new ControlDecoration(comboSourceDir, SWT.LEFT | SWT.TOP);
		controlDecoration.setImage(SWTResourceManager.getImage(ProjectImportDialog.class, "/icons/full/ovr16/error_ovr.gif")); //$NON-NLS-1$
		controlDecoration.setDescriptionText("Some description"); //$NON-NLS-1$
		
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dlg = new DirectoryDialog(getShell());

				// Set the initial filter path according
				// to anything they've selected or typed in
				dlg.setFilterPath(comboSourceDir.getText());

				// Change the title bar text
				dlg.setText(Messages.SelectImportDialog_ImportDirTitle);

				// Customizable message displayed in the dialog
				dlg.setMessage(Messages.SelectImportDialog_Message);

				// Calling open() will open and run the dialog.
				// It will return the selected directory, or
				// null if user cancels
				String dir = dlg.open();
				if (dir != null)
				{
					// Set the text box to the new selection
					comboSourceDir.setText(dir);
					initSourceTable(dir);
					updateWidgets();
				}
			}
		});
		btnBrowse.setText(Messages.SelectImportDialog_btnBrowse_text);
		
		txtSeek = new Text(container, SWT.BORDER);		
		txtSeek.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				stgFilter = txtSeek.getText();
				checkboxTableViewer.refresh();
			}
		});
		txtSeek.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
		checkboxTableViewer.addSelectionChangedListener(new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{						
						updateWidgets();						
					}
				});	
		
		checkboxTableViewer.addCheckStateListener(new ICheckStateListener()
		{			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event)
			{
				Object element= event.getElement();
				
				if(checkboxTableViewer.getGrayed(element))
					checkboxTableViewer.setChecked(element, false);
			}
		});
		
		checkboxTableViewer.addFilter(new NameFilter());
		table = checkboxTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(checkboxTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
		tblclmnNewColumn.setWidth(500);
		tblclmnNewColumn.setText(Messages.SelectImportDialog_ColumnProjekte);
		AbstractTableSorter sorter = new Sorter(checkboxTableViewer,tableViewerColumn);
		
		checkboxTableViewer.setLabelProvider(new TableLabelProvider());
		checkboxTableViewer.setContentProvider(new ContentProvider());
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		composite.setLayout(new GridLayout(2, false));
		
		Button btnAllSelect = new Button(composite, SWT.NONE);
		btnAllSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// grayed Items werden nicht gecheckt
				Table table = checkboxTableViewer.getTable();
				int n = table.getItemCount();
				for(int i = 0;i < n;i++)
				{
					Object item = checkboxTableViewer.getElementAt(i);
					checkboxTableViewer.setChecked(item, !checkboxTableViewer.getGrayed(item));
				}
				updateWidgets();
			}
		});
		btnAllSelect.setBounds(0, 0, 68, 23);
		btnAllSelect.setText(Messages.SelectImportDialog_btnAllSelect_text);
		
		Button btnNoSelect = new Button(composite, SWT.NONE);
		btnNoSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				checkboxTableViewer.setAllChecked(false);
				updateWidgets();
			}
		});
		btnNoSelect.setBounds(0, 0, 68, 23);
		btnNoSelect.setText(Messages.SelectImportDialog_btnNoSelect_text);
		new Label(container, SWT.NONE);
		
		Group group = new Group(container, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		group.setText(Messages.SelectImportDialog_group_text);
		
		btnWorkingSets = new Button(group, SWT.CHECK);
		btnWorkingSets.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				comboWorkingSets.setEnabled(btnWorkingSets.getSelection());
				btnBrowseWorkingset.setEnabled(btnWorkingSets.getSelection());
			}
		});
		btnWorkingSets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		btnWorkingSets.setText(Messages.SelectImportDialog_btnWorkingSets_text);
		
		Label lblWorkingset = new Label(group, SWT.NONE);
		lblWorkingset.setText(Messages.SelectImportDialog_lblWorkingset_text);
		
		comboWorkingSets = new CCombo(group, SWT.BORDER);
		comboWorkingSets.setEditable(false);
		comboWorkingSets.setEnabled(false);
		comboWorkingSets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnBrowseWorkingset = new Button(group, SWT.NONE);
		btnBrowseWorkingset.setEnabled(false);
		btnBrowseWorkingset.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				SelectWorkingSetDialog dialog = new SelectWorkingSetDialog(
						getShell(), assignedWorkingSets
								.toArray(new IWorkingSet[assignedWorkingSets
										.size()]));				
				if(dialog.open() == ConfigureWorkingSetDialog.OK)
				{
					// die ausgewaehlten WorkingSets in Combo uebernehmen 
					IWorkingSet [] configResults = dialog.getConfigResult();
					assignedWorkingSets.clear();				
					StringBuilder buildName = new StringBuilder(5);
					for(IWorkingSet workingSet : configResults)
					{
						String wsName = workingSet.getName();
						if (!StringUtils.equals(wsName,
								IWorkingSetManager.OTHER_WORKINGSET_NAME))
						{
							if (assignedWorkingSets.size() > 0)
								buildName.append("," + wsName); //$NON-NLS-N$ //$NON-NLS-1$
							else
								buildName.append(wsName);								
							assignedWorkingSets.add(workingSet);
						}	
					}
					String name = buildName.toString();
					comboWorkingSets.add(name);
					comboWorkingSets.setText(buildName.toString());
					comboWorkingSets.setData(name, assignedWorkingSets.clone());					

					
					
				}
			}
		});
		btnBrowseWorkingset.setText(Messages.SelectImportDialog_btnBrowseWorkingset_text);

		initSourceDirCombo();
		updateWidgets();
		
		return area;
	}
	
	/*
	 * Sourceverzeichnis vorbelegen (aus DialogSettings od. Default-TempDir)
	 */
	private void initSourceDirCombo()
	{
		String [] sourcePaths = settings.getArray(IMPORT_SOURCEDIRS_SETTINGS);
		if(sourcePaths != null)
		{
			for(String path : sourcePaths)
				comboSourceDir.add(path);
			setImportDir(sourcePaths[0]);	
			
			// falls Zielverzeichnis nicht existiert, temporaeres Verzeichnis benutzen
			if(!new File(sourcePaths[0]).exists())
			{
				IEclipsePreferences instancePreferenceNode = InstanceScope.INSTANCE.getNode(IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE);
				setImportDir(instancePreferenceNode.get(IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY,null));
			}
		}
		else
		{
			// temporaeres Verzeichnis ist Defaultverzeichnis
			IEclipsePreferences instancePreferenceNode = InstanceScope.INSTANCE
					.getNode(
							IPreferenceAdapter.ROOT_APPLICATION_PREFERENCES_NODE);			
			setImportDir(instancePreferenceNode.get(IPreferenceAdapter.PREFERENCE_APPLICATION_TEMPDIR_KEY,null));
		}
		
		initSourceTable(importSourceDirectory.getSrcDir());		
	}
	
	/*
	 * Initialisiert den TableViewer des Dialogs
	 * Die Daten aus EMF-Datei lesen und in dem TableViewer (ContentProvider) speichern
	 */
	private void initSourceTable(String sourceDir)
	{		
		// die NtProject-Eigenschaften aus der 'xmi'-Datei auslesen
		EList<EObject>importedNtProperties = importProjectProperties(sourceDir);
		if(importedNtProperties != null)
		{
			checkboxTableViewer.setInput(importedNtProperties);
			disableExistObjects(importedNtProperties);
		}
	}
	
	/*
	 * Die vorhandenen Objekte werden in der CheckBoxTableView disabled (@see TableLableProvider).    
	 */
	private void disableExistObjects(EList<EObject>importedNtProperties)
	{		
		boolean exists = false;
		for(EObject eObject : importedNtProperties)
		{
			String id = ((NtProject)eObject).getId();
			if(it.naturtalent.e4.project.ui.Activator.findNtProject(id) != null)
			{
				checkboxTableViewer.setGrayed(eObject, true);
				exists = true;
			}
		}
		
		if(exists)
			setErrorMessage("nicht alle Projekte können importiert werden, da sie bereits existieren");
		
		checkboxTableViewer.refresh();
	}
	
	/*
	 * EMF-Daten aus der 'xmi' - Datei, in der die NtProject-Eigenschaften gespeichert sind, lesen
	 */
	private EList<EObject> importProjectProperties(String sourceDir)
	{
		EList<EObject>projectProperties = null;
		
		// alle direkten Unterverzeichnisse auflisten
		File dir = new File(sourceDir);
		File projectPropertyFile = new File(dir,ECP_NTPROJECT_PROPERTYFILE);
		if(projectPropertyFile.exists())
		{
			// Resource laden
			URI fileURI = URI.createFileURI(projectPropertyFile.getPath());
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.getResource(fileURI, true);
			projectProperties = resource.getContents();
		}

		// fehlerhafte NtProjekte (fehlende ID) aussortieren
		List<EObject>errorProjectProperties = new ArrayList<EObject>();		
		if (projectProperties != null)
		{
			for (EObject projectProperty : projectProperties)
			{
				if (projectProperty instanceof NtProject)
				{
					NtProject ntProject = (NtProject) projectProperty;
					String id = ntProject.getName();
					if (StringUtils.isEmpty(id))
						errorProjectProperties.add(projectProperty);
				}
			}			
			for(EObject eObject : errorProjectProperties)
				projectProperties.remove(eObject);
		}
		
		if((projectProperties == null) || (projectProperties.isEmpty()))
			log.error("keine Importdaten in "+ECP_NTPROJECT_PROPERTYFILE+" defniert");
			
		return projectProperties;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,true);
		createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, false);
		updateWidgets();
		m_bindingContext = initDataBindings();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(683, 697);
	}
	
	private void updateWidgets()
	{
		if(okButton != null)
			okButton.setEnabled(checkboxTableViewer.getCheckedElements().length > 0);
	}

	@Override
	protected void okPressed()
	{
		Object[] result = checkboxTableViewer.getCheckedElements();
		
		resultImportEobjects = new EObject[result.length];
		System.arraycopy(result, 0, resultImportEobjects, 0,result.length);
		
		assignedWorkingSets = (btnWorkingSets.getSelection() ? assignedWorkingSets : null);
		
		storeSettings();
		super.okPressed();
	}
	
	

	public String getImportSourceDirectory()
	{
		return importSourceDirectory.getSrcDir();
	}

	private void storeSettings()
	{
		List<String>srcPaths = new ArrayList<String>();

		String srcDir = comboSourceDir.getText();
		if (StringUtils.isNotEmpty(srcDir))
		{
			String[] paths = comboSourceDir.getItems();
			for (int i = 0; i < paths.length; i++)
			{
				if (i > 5)
					break;
				srcPaths.add(paths[i]);
			}
			
			srcPaths.remove(srcDir);
			srcPaths.add(0,srcDir);
			
			settings.put(IMPORT_SOURCEDIRS_SETTINGS, srcPaths.toArray(new String[srcPaths.size()]));
		}
	}

	public EObject[] getResultImportSource()
	{
		return resultImportEobjects;
	}
	
	public List<IWorkingSet> getAssignedWorkingSets()
	{
		return assignedWorkingSets;
	}

	private void setImportDir(String importDir)
	{
		if(m_bindingContext != null)
			m_bindingContext.dispose();

		importSourceDirectory.setSrcDir(StringUtils.isNotEmpty(importDir) ? importDir : ""); //$NON-NLS-1$
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextComboSourceDirObserveWidget = WidgetProperties.text().observe(comboSourceDir);
		IObservableValue srcDirImportSourceDirectoryObserveValue = PojoProperties.value("srcDir").observe(importSourceDirectory); //$NON-NLS-1$
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setAfterGetValidator(new EmptyStringValidator());
		bindingContext.bindValue(observeTextComboSourceDirObserveWidget, srcDirImportSourceDirectoryObserveValue, strategy, null);
		//
		return bindingContext;
	}
}
