package it.naturtalent.e4.project.expimp.dialogs;

import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.expimp.ExpImpProcessor;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.navigator.WorkbenchLabelProvider;
import it.naturtalent.e4.project.ui.ws.AggregateWorkingSet;
import it.naturtalent.e4.project.ui.ws.WorkingSet;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSetRoot;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.ErrorDialog;
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
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class SelectExportDialog extends TitleAreaDialog
{
	private DataBindingContext m_bindingContext;
	
	
	public class ExportDestDirectory
	{
		String destDir;

		public String getDestDir()
		{
			return destDir;
		}

		public void setDestDir(String destDir)
		{
			this.destDir = destDir;
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


	private ExportDestDirectory exportDestDirectory = new ExportDestDirectory();
	//private IResourceNavigator navigator;
	
	private Table table;
	private CheckboxTableViewer checkboxTableViewer;
	private Combo comboDestDir;
	private Button okButton;
	private Button btnWorkingsets;
	private Button btnProjects;
	private static ControlDecoration controlDecoration;
	private IProject [] resultExportProjects;
	private File resultDestDir;
	
	// DialogSettings 
	private static final String EXPORT_DESTDIRS_SETTINGS = "export_sourcedirs_settings"; //$NON-NLS-1$
	private static final String EXPORT_DEST_SETTINGS = "export_source_settings"; //$NON-NLS-1$
	
	private IDialogSettings settings = WorkbenchSWTActivator.getDefault().getDialogSettings();
	
	WorkingSetManager wsManager = it.naturtalent.e4.project.ui.Activator
			.getWorkingSetManager();

	private FilenameFilter projectFileFilter = new FilenameFilter()
	{		
		@Override
		public boolean accept(File dir, String name)
		{
			File checkDir = new File(dir,name);
			return (new File(checkDir,IProjectData.PROJECTDATAFILE).exists());
		}
	};
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SelectExportDialog(Shell parentShell, MPart part)
	{
		super(parentShell);	
		
		//this.navigator = (IResourceNavigator) ExpImpProcessor.partService.findPart("it.naturtalent.e4.project.ui.part.explorer");
		//this.navigator = (IResourceNavigator) ExpImpProcessor.part.getObject();
		
		System.out.println("Ok");
		
		/*
		Object obj = part.getObject();
		if (obj instanceof IResourceNavigator)	
			this.navigator = (IResourceNavigator) obj;
			*/

	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		setTitleImage(SWTResourceManager.getImage(SelectExportDialog.class, "/icons/full/wizban/export_wiz.png")); //$NON-NLS-1$
		setMessage(Messages.SelectExportDialog_this_message);
		setTitle(Messages.SelectExportDialog_this_title);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblSource = new Label(container, SWT.NONE);
		lblSource.setText(Messages.SelectExportDialog_lblSource_text);
		
		comboDestDir = new Combo(container, SWT.BORDER);
		comboDestDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		controlDecoration = new ControlDecoration(comboDestDir, SWT.LEFT | SWT.TOP);
		controlDecoration.setImage(SWTResourceManager.getImage(SelectExportDialog.class, "/icons/full/ovr16/error_ovr.gif")); //$NON-NLS-1$
		controlDecoration.setDescriptionText(Messages.SelectExportDialog_controlDecoration_descriptionText);
		
		// Browse
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dlg = new DirectoryDialog(getShell());

				// Set the initial filter path according
				// to anything they've selected or typed in
				dlg.setFilterPath(comboDestDir.getText());

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
					comboDestDir.setText(dir);					
					updateWidgets();
				}
			}
		});
		btnBrowse.setText(Messages.SelectExportDialog_btnBrowse_text);
		
		Group group = new Group(container, SWT.NONE);
		group.setLayout(new FillLayout(SWT.HORIZONTAL));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		group.setText(Messages.SelectExportDialog_group_text);
		
		btnWorkingsets = new Button(group, SWT.RADIO);
		btnWorkingsets.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(((Button)e.getSource()).getSelection())
					checkboxTableViewer.setInput(new WorkingSetRoot(wsManager.getWorkingSets()));
			}
		});
		btnWorkingsets.setText(Messages.SelectExportDialog_btnWorkingsets_text);
		
		btnProjects = new Button(group, SWT.RADIO);
		btnProjects.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(((Button)e.getSource()).getSelection())				
					checkboxTableViewer.setInput(getAggregateSet());
			}
		});
		btnProjects.setText(Messages.SelectExportDialog_btnProjects_text);
		new Label(container, SWT.NONE);
		
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
		checkboxTableViewer.setLabelProvider(new WorkbenchLabelProvider());
		checkboxTableViewer.setContentProvider(new WorkbenchContentProvider());
		checkboxTableViewer.setComparator(new ViewerComparator());
		
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
		btnAllSelect.setText(Messages.SelectExportDialog_btnAllSelect_text);
		
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
		btnNoSelect.setText(Messages.SelectExportDialog_btnNoSelect_text);
		new Label(container, SWT.NONE);

		init();
		updateWidgets();
		
		return area;
	}
	
	private void init()
	{
		// Zielverzeichnis
		String [] sourcePaths = settings.getArray(EXPORT_DESTDIRS_SETTINGS);
		if(sourcePaths != null)
		{
			for(String path : sourcePaths)
				comboDestDir.add(path);
			setExportDir(sourcePaths[0]);			
		}
		else setExportDir(SystemUtils.getUserDir().getPath());
		
		// Projeke/Workingsets einlesen
		boolean btnState = settings.getBoolean(EXPORT_DEST_SETTINGS);
		btnProjects.setSelection(btnState);
		btnWorkingsets.setSelection(!btnState);
		
		if(!btnState)
			checkboxTableViewer.setInput(new WorkingSetRoot(wsManager.getWorkingSets()));
		else checkboxTableViewer.setInput(getAggregateSet());
	}
	
	static IWorkingSet aggregateResourceSet = null;
	private IWorkingSet getAggregateSet()
	{
		if(aggregateResourceSet == null)
		{
			IWorkingSet [] workingSets = wsManager.getWorkingSets();
			aggregateResourceSet = wsManager
					.createAggregateWorkingSet("tempAggregate", "",
							workingSets);
		}
		
		return aggregateResourceSet;
	}
	
	
	private void initSourceTable(String sourceDir)
	{
		// alle direkten Unterverzeichnisse auflisten
		
		/*
		File dir = new File(sourceDir);
		File[] childDirs = dir
				.listFiles(projectFileFilter);
		checkboxTableViewer.setInput(childDirs);
		*/
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
		boolean directorydefined = !controlDecoration.isVisible();
		boolean itemsSelected = checkboxTableViewer.getCheckedElements().length > 0;
		
		if(okButton != null)
			okButton.setEnabled(directorydefined && itemsSelected);
		
	}

	@Override
	protected void okPressed()
	{	
		resultExportProjects = null;
		Object[] result = checkboxTableViewer.getCheckedElements();
		
		resultDestDir = new File(exportDestDirectory.destDir);
		if(!resultDestDir.exists() || !resultDestDir.isDirectory())
		{
			MessageDialog
					.openError(
							getShell(),
							Messages.SelectExportDialog_DestDirectoryError,
							Messages.bind(
									Messages.SelectExportDialog_DestDirectoryErrorMessage,
									exportDestDirectory.destDir));
			return;
		}

		if (ArrayUtils.isNotEmpty(result))
		{
			if (result[0] instanceof IWorkingSet)
			{
				for (Object obj : result)
				{
					IWorkingSet workingSet = (IWorkingSet) obj;
					IAdaptable[] adaptables = workingSet.getElements();
					for (IAdaptable iAdaptable : adaptables)
						resultExportProjects = (IProject[]) ArrayUtils.add(
								resultExportProjects,
								iAdaptable.getAdapter(IProject.class));
				}
			}
			else
			{
				resultExportProjects = new IProject[result.length];
				System.arraycopy(result, 0, resultExportProjects, 0,
						result.length);
			}
		}
		
		storeSettings();
		super.okPressed();
	}

	private void storeSettings()
	{		
		List<String>destPaths = new ArrayList<String>();
		
		String destDir = comboDestDir.getText();
		if (StringUtils.isNotEmpty(destDir))
		{
			String[] paths = comboDestDir.getItems();
			for (int i = 0; i < paths.length; i++)
			{
				if (i > 5)
					break;
				destPaths.add(paths[i]);
			}
			
			destPaths.remove(destDir);
			destPaths.add(0,destDir);			
			
			settings.put(EXPORT_DESTDIRS_SETTINGS, destPaths.toArray(new String[destPaths.size()]));
		}
		
		settings.put(EXPORT_DEST_SETTINGS, btnProjects.getSelection());
	}

	public IProject [] getResultExportSource()
	{
		return resultExportProjects;
	}
	
	public File getResultDestDir()
	{
		return resultDestDir;
	}

	private void setExportDir(String exportDir)
	{
		if(m_bindingContext != null)
			m_bindingContext.dispose();
		
		exportDestDirectory
				.setDestDir(StringUtils.isNotEmpty(exportDir) ? exportDir : ""); //$NON-NLS-1$
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextComboDestDirObserveWidget = WidgetProperties.text().observe(comboDestDir);
		IObservableValue destDirExportDestDirectoryObserveValue = PojoProperties.value("destDir").observe(exportDestDirectory); //$NON-NLS-1$
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setAfterGetValidator(new EmptyStringValidator());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setAfterGetValidator(new EmptyStringValidator());
		bindingContext.bindValue(observeTextComboDestDirObserveWidget, destDirExportDestDirectoryObserveValue, strategy_1, strategy);
		//
		return bindingContext;
	}
}
