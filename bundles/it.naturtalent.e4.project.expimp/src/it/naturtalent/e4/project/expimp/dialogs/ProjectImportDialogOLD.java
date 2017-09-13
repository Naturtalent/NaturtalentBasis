package it.naturtalent.e4.project.expimp.dialogs;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.expimp.Activator;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.expimp.actions.ExportAction;
import it.naturtalent.e4.project.ui.dialogs.ConfigureWorkingSetDialog;
import it.naturtalent.e4.project.ui.dialogs.SelectWorkingSetDialog;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class ProjectImportDialogOLD extends TitleAreaDialog
{
	private DataBindingContext m_bindingContext;
	
	/*
	 * 
	 */
	public class NameFilter extends ViewerFilter
	{
		@Override
		public boolean select(Viewer viewer, Object parentElement,Object element)
		{			
			if (element instanceof File)
			{				
				if(StringUtils.isNotEmpty(stgFilter))
				{
					File file = (File) element;					
					String projektName = getProjektName(file);
					return StringUtils.containsIgnoreCase(projektName, stgFilter);					
				}				
			}			
			return true;
		}
	}
	
	/*
	 * 
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
			
			stgValue1 = getProjektName((File) e1);
			stgValue2 = getProjektName((File) e2);			
			if (StringUtils.isNotEmpty(stgValue1)
					&& StringUtils.isNotEmpty(stgValue2))
				return collator.compare(stgValue1, stgValue2);
			
			return 0;
		}
	}

	/*
	 * 
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

	private class TableLabelProvider extends LabelProvider implements
			ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return Icon.ICON_PROJECT.getImage(IconSize._16x16_DefaultIconSize);

			/*
			return SWTResourceManager.getImage(ImportExistProjects.class,
					"/icons/projekt.png"); //$NON-NLS-1$
					*/
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if(element instanceof File)
			{
				return getProjektName((File) element);
				
				/*
				try
				{
					File projectFile = new File((File)element,IProjectData.PROJECTDATA_FOLDER);
					projectFile = new File(projectFile,IProjectData.PROJECTDATAFILE);
					InputStream in = FileUtils.openInputStream(projectFile);
				
					IProjectData projectData = Activator.projectDataFactory
							.getProjectData(ProjectData.class, in);
					
					if(projectData != null)
						return (projectData.getName());
					
					
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
			
			return element.toString();
		}
	}
	
	private String getProjektName(File file)
	{
		String name = ""; //$NON-NLS-1$

		try
		{
			File projectFile = new File(file, IProjectData.PROJECTDATA_FOLDER);
			projectFile = new File(projectFile, IProjectData.PROJECTDATAFILE);
			InputStream in = FileUtils.openInputStream(projectFile);

			IProjectData projectData = Activator.projectDataFactory
					.getProjectData(ProjectData.class, in);

			if (projectData != null)
				name = projectData.getName();

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	private static class ContentProvider implements IStructuredContentProvider
	{
		private File [] projectDirs;
		
		public Object[] getElements(Object inputElement)
		{
			return projectDirs;
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			projectDirs = new File[0];
			if(newInput instanceof File[])
				projectDirs = (File[]) newInput;
		}
	}
	
	private ImportSourceDirectory importSourceDirectory = new ImportSourceDirectory();
	
	private Table table;
	private CheckboxTableViewer checkboxTableViewer;
	private Combo comboSourceDir;
	private Button okButton;
	private File [] resultImportSource;
	private ControlDecoration controlDecoration;
	private Button btnWorkingSets;
	private CCombo comboWorkingSets;
	private Button btnBrowseWorkingset;
	
	public static final String IMPORT_SOURCEDIRS_SETTINGS = "import_sourcedirs_settings"; //$NON-NLS-1$
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	// Liste der zugeordneten WorkingSets	
	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();
	
	private String stgFilter;


	private FilenameFilter projectFileFilter = new FilenameFilter()
	{		
		@Override
		public boolean accept(File dir, String name)
		{
			File checkDir = new File(dir,name);
			checkDir = new File(checkDir, IProjectData.PROJECTDATA_FOLDER);
			//return (new File(checkDir,IProjectData.PROJECTDATAFILE).exists());
			return (new File(checkDir,ExportAction.IMPEXPORTFILE_NAME).exists());
		}
	};
	private Text txtSeek;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ProjectImportDialogOLD(Shell parentShell)
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
		setTitleImage(SWTResourceManager.getImage(ProjectImportDialogOLD.class, "/icons/full/wizban/import_wiz.png")); //$NON-NLS-1$
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
		
		controlDecoration = new ControlDecoration(comboSourceDir, SWT.LEFT | SWT.TOP);
		controlDecoration.setImage(SWTResourceManager.getImage(ProjectImportDialogOLD.class, "/icons/full/ovr16/error_ovr.gif")); //$NON-NLS-1$
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
		
		checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		checkboxTableViewer
				.addSelectionChangedListener(new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{
						updateWidgets();
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
				checkboxTableViewer.setAllChecked(true);
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
	
	private void initSourceDirCombo()
	{
		String [] sourcePaths = settings.getArray(IMPORT_SOURCEDIRS_SETTINGS);
		if(sourcePaths != null)
		{
			for(String path : sourcePaths)
				comboSourceDir.add(path);
			setImportDir(sourcePaths[0]);			
		}
		else setImportDir(SystemUtils.getUserDir().getPath());
		initSourceTable(importSourceDirectory.getSrcDir());
	}
	
	private void initSourceTable(String sourceDir)
	{
		// alle direkten Unterverzeichnisse auflisten
		File dir = new File(sourceDir);
		final File[] childDirs = dir
				.listFiles(projectFileFilter);
		
		
		Display display = Display.getDefault();
		if (display != null)
		{
			BusyIndicator.showWhile(display, new Runnable()
			{
				@Override
				public void run()
				{
					checkboxTableViewer.setInput(childDirs);
				}

			});
		}
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
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
		
		resultImportSource = new File[result.length];
		System.arraycopy(result, 0, resultImportSource, 0,
				result.length);
		
		assignedWorkingSets = (btnWorkingSets.getSelection() ? assignedWorkingSets : null);
		
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

	public File[] getResultImportSource()
	{
		return resultImportSource;
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
