package it.naturtalent.e4.project.expimp.dialogs;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXB;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.jface.viewers.ArrayContentProvider;
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
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.dialogs.ConfigureWorkingSetDialog;
import it.naturtalent.e4.project.ui.dialogs.SelectWorkingSetDialog;
import it.naturtalent.e4.project.ui.emf.ExpImpUtils;
import it.naturtalent.e4.project.ui.emf.NtProjectProperty;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

/**
 * Mit diesem Dialog wird das Importverzeichnis ausgewaehlt. Erwartet wird, dass die Unterverzeichnisse die NtProjekte
 * repraesentieren. Die Namen der Unterverzeichnisse entsprechen den NtProjekt-IDs. In jedem NtProjekt-Vezeichnis befinden 
 * sich nicht nur die Projektresourcen (Verzeichnisse, Dateien) sondern auch die Projekteigenschaften (Defaulteigenschaft,
 * Kontakt, Archiv ...) in den jeweiligen EMFStore - Dateien. Fuer die Praesentation der zuimportierenden NtProjekte im 
 * Dialogviewer werden die Defaulteigenschaften in EMF-Modell eingelesen und die Projektnamen werden mit dem Labelprovider
 * im CheckboxViewer angezeigt.
 *   
 * PropertyAdapter
 * @see it.naturtalent.e4.project.ui.emf.NtProjectProperty
 * 
 * @author dieter
 *
 */
public class ProjectImportDialog extends TitleAreaDialog
{

	private Log log = LogFactory.getLog(this.getClass());
	
	/*
	 * NameFilter fuer den Viewer.
	 * Filtert den Viewer nach einem String aus dem Textfeld 'txtSeek'
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
	

	// liefert den Namen des NtProjekts
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			if(element instanceof NtProject)	
				return Icon.ICON_PROJECT.getImage(IconSize._16x16_DefaultIconSize);
			
			return null;
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
	
	private Table table;
	private CheckboxTableViewer checkboxTableViewer;
	private Combo comboSourceDir;
	private Button okButton;
	private EObject [] resultImportEobjects;	
	private Button btnWorkingSets;
	private CCombo comboWorkingSets;
	private Button btnBrowseWorkingset;
	
	public static final String IMPORT_SOURCEDIRS_SETTINGS = "import_sourcedirs_settings"; //$NON-NLS-1$
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	// Liste der zugeordneten WorkingSets	
	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();
	
	// das ausgewaehlte Importverzeichnis
	private File selectedImportDirectory;
	
	private String stgFilter;

	// Textfeld zur Eingabe eines Suchstrings
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

		// Combo mit Importverzeichnissen
		comboSourceDir = new Combo(container, SWT.BORDER);		
		comboSourceDir.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{		
				// Viewer mit den Projekten aus der Combobox Selektion initialisieen
				initViewer(comboSourceDir.getText());
			}
		});
		comboSourceDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboSourceDir.setEnabled(false);
		
		// Combo initialisieren mit Settingwerten
		String [] sourcePaths = settings.getArray(IMPORT_SOURCEDIRS_SETTINGS);
		if(ArrayUtils.isNotEmpty(sourcePaths))
		{
			comboSourceDir.setItems(sourcePaths);
			comboSourceDir.setText(sourcePaths[0]);
		}
		
		// Button startet die Auswahl eines Importverzeichnisses
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dlg = new DirectoryDialog(getShell());

				// Dateibrowser auf Path im Combotext einstellen				
				dlg.setFilterPath(comboSourceDir.getText());

				// Label des Diealogs
				dlg.setText(Messages.SelectImportDialog_ImportDirTitle);

				// Customizable message displayed in the dialog
				dlg.setMessage(Messages.SelectImportDialog_Message);

				String dir = dlg.open();
				if (dir != null)
				{
					// das ausgewaehlte Verzeichnis in die Combobox uebernehmen
					comboSourceDir.setText(dir);
					
					// Viewer mit den Projekten aus der 'browse'-Button Selektion initialisieen
					initViewer(comboSourceDir.getText());					
					
					updateWidgets();
				}
			}
		});
		btnBrowse.setText(Messages.SelectImportDialog_btnBrowse_text);
		
		// Eingabefeld Filter
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
		checkboxTableViewer.setContentProvider(new ArrayContentProvider());
		//checkboxTableViewer.setContentProvider(new ContentProvider());
		
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

		// Viewer mit den ImportNtProjekte aus dem Setting ImportPath initialisieren
		initViewer(comboSourceDir.getText());
		updateWidgets();
		
		return area;
	}
	
	/**
	 * Den Viewer mit den Projekte des Importverzeichnisses initialisieren.
	 * 
	 * @param importDirPath
	 */
	private void initViewer(String importDirPath)
	{
		List <NtProject>ntProjects = readImportFiles(importDirPath);		
		checkboxTableViewer.setInput(ntProjects);	
		disableExistObjects(ntProjects);
	}
	
	/**
	 * Defaulteigenschaft der NtProjekte aus den NtProjekt-Unterverzeichnissen einlesen.
	 * Zurueckgegeben wird eine Liste mit den Eigenschaften der importierbaren NtProjekte.
	 * 
	 * @param importDirPath
	 * @return
	 */
	private List <NtProject>readImportFiles(String importDirPath)
	{
		List<NtProject>ntProjects = new ArrayList<NtProject>();
		File importDir = new File(importDirPath);
		File[] subdirs = importDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
				
		File propertyFile;
		if (ArrayUtils.isNotEmpty(subdirs))
		{
			for (File dir : subdirs)
			{
				// Defaulteigenschaft aus der Datei
				// 'NtProjectProperty.EXPIMP_NTPROJECTDATA_FILE' lesen
				propertyFile = new File(dir,NtProjectProperty.EXPIMP_NTPROJECTDATA_FILE);
				if (propertyFile.exists())
				{
					EList<EObject> eObjects = ExpImpUtils.loadEObjectFromResource(propertyFile);
					ntProjects.add((NtProject) eObjects.get(0));
				}
			}
		}
		
		return ntProjects;
	}
	
	/*
	 * Vorhandenen NtProjekte koennen nicht importiert werden und werden in der CheckBoxTableView 
	 * disabled (@see TableLableProvider).
	 * In diesem Fall wird eine Message angezeigt.
	 *      
	 */
	private void disableExistObjects(List<NtProject>importedNtProjects)
	{		
		boolean showExistsMessage = false;
		for(NtProject ntProject : importedNtProjects)
		{
			String id = ntProject.getId();
			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProject.getId());
			if(iProject.exists())
			{
				checkboxTableViewer.setGrayed(ntProject, true);
				showExistsMessage = true;
			}
		}
		
		if(showExistsMessage)
			setErrorMessage("nicht alle Projekte können importiert werden, da sie bereits existieren");
		
		checkboxTableViewer.refresh();
	}

	/*
	 * EMF-Daten aus der 'xmi' - Datei, in der die NtProject-Eigenschaften gespeichert sind, lesen
	 */
	private EList<EObject> importProjectPropertiesOLD(String sourceDir)
	{
		EList<EObject>projectProperties = null;
		
		// alle direkten Unterverzeichnisse auflisten
		File dir = new File(sourceDir);
		File projectPropertyFile = new File(dir, NtProjectProperty.EXPIMP_NTPROJECTDATA_FILE);
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
			log.error("keine Importdaten in "+/*ECP_NTPROJECT_PROPERTYFILE+*/" defniert");
			
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
		// die ausgewaehlten NtProjekt Defaulteigenschaften listen
		Object[] result = checkboxTableViewer.getCheckedElements();
		resultImportEobjects = new EObject[result.length];
		System.arraycopy(result, 0, resultImportEobjects, 0,result.length);
		
		// die selektierten WorkingSets
		assignedWorkingSets = (btnWorkingSets.getSelection() ? assignedWorkingSets : null);
		
		// das Importverzeichnis	
		String importDir = comboSourceDir.getText();
		selectedImportDirectory = (StringUtils.isNotEmpty(importDir)) ? new File(importDir) : null;
		
		// Dialogsettings aktualisieren
		storeSettings();
		
		super.okPressed();
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

	/**
	 * Die zum Import ausgewaehlten NtProjekt zurueckgeben.
	 * 
	 * @return
	 */
	public EObject[] getSelectedImportNtProjects()
	{
		return resultImportEobjects;
	}
	
	public List<IWorkingSet> getAssignedWorkingSets()
	{
		return assignedWorkingSets;
	}

	public File getSelectedImportDirectory()
	{
		return selectedImportDirectory;
	}



	

}
