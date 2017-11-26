package it.naturtalent.e4.project.expimp.dialogs;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.expimp.Activator;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.ui.dialogs.ConfigureWorkingSetDialog;
import it.naturtalent.e4.project.ui.dialogs.SelectWorkingSetDialog;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.browser.Browser;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class SelectImportDialog extends TitleAreaDialog
{
	private DataBindingContext m_bindingContext;
	
	
	
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
	 * Interne Klasse zum Überprüfen des Textfeldes 'applicationText'
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
			return SWTResourceManager.getImage(ImportExistProjects.class,
					"/icons/projekt.png");
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if(element instanceof File)
			{
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
			}
			
			return element.toString();
		}
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
	
	public static final String IMPORT_SOURCEDIRS_SETTINGS = "import_sourcedirs_settings"; //$NON-NLS-1$
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	// Liste der zugeordneten WorkingSets	
	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();


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
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SelectImportDialog(Shell parentShell)
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
		setTitleImage(SWTResourceManager.getImage(SelectImportDialog.class, "/icons/full/wizban/import_wiz.png"));
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
		controlDecoration.setImage(SWTResourceManager.getImage(SelectImportDialog.class, "/icons/full/ovr16/error_ovr.gif"));
		controlDecoration.setDescriptionText("Some description");
		
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
				dlg.setText("Importverzeichnis");

				// Customizable message displayed in the dialog
				dlg.setMessage("Ordner mit den Quellprojekten auswählen");

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
		
		checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		checkboxTableViewer
				.addSelectionChangedListener(new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{
						updateWidgets();
					}
				});
		table = checkboxTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
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
			}
		});
		btnWorkingSets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		btnWorkingSets.setText(Messages.SelectImportDialog_btnWorkingSets_text);
		
		Label lblWorkingset = new Label(group, SWT.NONE);
		lblWorkingset.setText(Messages.SelectImportDialog_lblWorkingset_text);
		
		comboWorkingSets = new CCombo(group, SWT.BORDER);
		comboWorkingSets.setEnabled(false);
		comboWorkingSets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnBrowseWorkingset = new Button(group, SWT.NONE);
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
								buildName.append("," + wsName); //$NON-NLS-N$
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
			initSourceTable(sourcePaths[0]);
		}
		else setImportDir(SystemUtils.getUserDir().getPath());
	}
	
	private void initSourceTable(String sourceDir)
	{
		// alle direkten Unterverzeichnisse auflisten
		File dir = new File(sourceDir);
		File[] childDirs = dir
				.listFiles(projectFileFilter);
		checkboxTableViewer.setInput(childDirs);
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

		importSourceDirectory.setSrcDir(StringUtils.isNotEmpty(importDir) ? importDir : "");
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextComboSourceDirObserveWidget = WidgetProperties.text().observe(comboSourceDir);
		IObservableValue srcDirImportSourceDirectoryObserveValue = PojoProperties.value("srcDir").observe(importSourceDirectory);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setAfterGetValidator(new EmptyStringValidator());
		bindingContext.bindValue(observeTextComboSourceDirObserveWidget, srcDirImportSourceDirectoryObserveValue, strategy, null);
		//
		return bindingContext;
	}
}
