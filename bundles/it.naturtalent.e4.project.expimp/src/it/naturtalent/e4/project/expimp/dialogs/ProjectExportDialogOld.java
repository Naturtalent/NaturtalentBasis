package it.naturtalent.e4.project.expimp.dialogs;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.ui.navigator.WorkbenchLabelProvider;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSetRoot;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.wb.swt.SWTResourceManager;

public class ProjectExportDialogOld extends TitleAreaDialog
{
	
	// Exportoptionen
	public static final int EXPORTOPTION_XMLFORMAT = 0;
	public static final int EXPORTOPTION_OOFORMAT = 1;
	public static final int EXPORTOPTION_MSFORMAT = 2;
	private int exportOption = EXPORTOPTION_XMLFORMAT;
	
	private DataBindingContext m_bindingContext;
	
	
	/*
	 * 
	 */
	public class NameFilter extends ViewerFilter
	{
		@Override
		public boolean select(Viewer viewer, Object parentElement,Object element)
		{
			if (StringUtils.isNotEmpty(stgFilter))
			{
				if (element instanceof IWorkingSet)
				{
					IWorkingSet ws = (IWorkingSet) element;
					return StringUtils.containsIgnoreCase(ws.getName(),
							stgFilter);
				}

				if (element instanceof IProject)
				{					
					try
					{
						String name = ((IProject)element).getPersistentProperty(INtProject.projectNameQualifiedName);
						return StringUtils.containsIgnoreCase(name,stgFilter);
					} catch (CoreException e)
					{
					}
				}
			}
			return true;
		}
	}


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


	private ExportDestDirectory exportDestDirectory = new ExportDestDirectory();
		
	private Tree tree;
	private ContainerCheckedTreeViewer checkboxTreeViewer;
	private Combo comboDestDir;
	private Button okButton;
	private Button btnWorkingsets;
	private Button btnProjects;
	private Text textSeek;
	private String stgFilter;
	private Button btnCheckButton;
	
	//private Button btnRadioXML;
	//private Button btnRadioOO;
	//private Button btnRadioExcel;
	
	private List<Button>optionradios = new ArrayList<Button>();
	
	private static ControlDecoration controlDecoration;
	private IProject [] resultExportProjects;
	private boolean archivState = false;
	private File resultDestDir;
	
	// DialogSettings 
	private static final String EXPORT_DESTDIRS_SETTINGS = "export_sourcedirs_settings"; //$NON-NLS-1$
	private static final String EXPORT_DEST_SETTINGS = "export_source_settings"; //$NON-NLS-1$
	private static final String EXPORT_OPTION_SETTINGS = "export_option_settings"; //$NON-NLS-1$
	
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
	public ProjectExportDialogOld(Shell parentShell)
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
		setTitleImage(SWTResourceManager.getImage(ProjectExportDialogOld.class, "/icons/full/wizban/export_wiz.png")); //$NON-NLS-1$
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
		controlDecoration.setImage(SWTResourceManager.getImage(ProjectExportDialogOld.class, "/icons/full/ovr16/error_ovr.gif")); //$NON-NLS-1$
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
				dlg.setMessage("Zielordner der Exportdaten ausw�hlen"); //$NON-NLS-1$

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
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		group.setText(Messages.SelectExportDialog_group_text);
		
		btnWorkingsets = new Button(group, SWT.RADIO);
		btnWorkingsets.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(((Button)e.getSource()).getSelection())
					checkboxTreeViewer.setInput(new WorkingSetRoot(wsManager.getWorkingSets()));
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
					checkboxTreeViewer.setInput(getAggregateSet());
			}
		});
		btnProjects.setText(Messages.SelectExportDialog_btnProjects_text);
		
		textSeek = new Text(container, SWT.BORDER);		
		textSeek.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				stgFilter = textSeek.getText();
				checkboxTreeViewer.refresh();
			}
		});
		textSeek.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		//checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		checkboxTreeViewer = new ContainerCheckedTreeViewer(container, SWT.BORDER);
		checkboxTreeViewer
				.addSelectionChangedListener(new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{						
						updateWidgets();
					}
				});
		tree = checkboxTreeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
		checkboxTreeViewer.setLabelProvider(new WorkbenchLabelProvider());
		checkboxTreeViewer.setContentProvider(new ExportSelectContentProvider());
		checkboxTreeViewer.setComparator(new ViewerComparator());
		checkboxTreeViewer.addFilter(new NameFilter());
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		composite.setLayout(new GridLayout(2, false));
		
		Button btnAllSelect = new Button(composite, SWT.NONE);
		btnAllSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				checkboxTreeViewer.setAllChecked(true);
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
				checkboxTreeViewer.setAllChecked(false);
				updateWidgets();
			}
		});
		btnNoSelect.setBounds(0, 0, 68, 23);
		btnNoSelect.setText(Messages.SelectExportDialog_btnNoSelect_text);
		new Label(container, SWT.NONE);
		
		Group grpOptionen = new Group(container, SWT.NONE);
		grpOptionen.setLayout(new GridLayout(2, false));
		grpOptionen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		grpOptionen.setText(Messages.SelectExportDialog_grpOptionen_text);
		
		Button btnRadioXML = new Button(grpOptionen, SWT.RADIO);
		btnRadioXML.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				exportOption = EXPORTOPTION_XMLFORMAT;
				comboDestDir.setEnabled(true);
			}
		});
		btnRadioXML.setSelection(true);
		btnRadioXML.setText(Messages.SelectExportDialog_btnRadioButton_text);
		optionradios.add(EXPORTOPTION_XMLFORMAT, btnRadioXML);
		
		btnCheckButton = new Button(grpOptionen, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				archivState = btnCheckButton.getSelection();
			}
		});
		btnCheckButton.setToolTipText(Messages.SelectExportDialog_btnCheckButton_toolTipText);
		btnCheckButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnCheckButton.setText(Messages.SelectExportDialog_btnCheckButton_text);
		
		Button btnRadioOO = new Button(grpOptionen, SWT.RADIO);
		btnRadioOO.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				exportOption = EXPORTOPTION_OOFORMAT;
				comboDestDir.setEnabled(false);
			}
		});
		btnRadioOO.setText(Messages.SelectExportDialog_btnRadioOO_text);
		optionradios.add(EXPORTOPTION_OOFORMAT, btnRadioOO);
		new Label(grpOptionen, SWT.NONE);
		
		Button btnRadioExcel = new Button(grpOptionen, SWT.RADIO);
		btnRadioExcel.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				exportOption = EXPORTOPTION_MSFORMAT;
				comboDestDir.setEnabled(false);
			}
		});
		btnRadioExcel.setText(Messages.SelectExportDialog_btnRadioExcel_text);
		optionradios.add(EXPORTOPTION_MSFORMAT, btnRadioExcel);
		new Label(grpOptionen, SWT.NONE);
		
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
			checkboxTreeViewer.setInput(new WorkingSetRoot(wsManager.getWorkingSets()));
		else checkboxTreeViewer.setInput(getAggregateSet());
		
		// Optionsettings
		int option;
		try
		{
			option = settings.getInt(EXPORT_OPTION_SETTINGS);
			exportOption = option;
			for(Button button : optionradios)
				button.setSelection(false);
			optionradios.get(exportOption).setSelection(true);	
			comboDestDir.setEnabled(exportOption == EXPORTOPTION_XMLFORMAT);
		} catch (NumberFormatException e)
		{
		}
			
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
		boolean itemsSelected = checkboxTreeViewer.getCheckedElements().length > 0;
		
		if(okButton != null)
			okButton.setEnabled(directorydefined && itemsSelected);
		
	}

	@Override
	protected void okPressed()
	{	
		resultExportProjects = null;
		Object[] result = checkboxTreeViewer.getCheckedElements();
		
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
			List<IProject>projects = new ArrayList<IProject>();
			for (Object obj : result)
			{
				if (obj instanceof IProject)
					projects.add((IProject) obj);
			}
			resultExportProjects = projects.toArray(new IProject[projects.size()]);
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
		
		// Exportoptionen
		settings.put(EXPORT_OPTION_SETTINGS, exportOption);
	}

	public IProject [] getResultExportSource()
	{
		return resultExportProjects;
	}
	
	public boolean isArchivState()
	{
		return archivState;
	}

	public File getResultDestDir()
	{
		return resultDestDir;
	}
	
	public int getExportOption()
	{
		return exportOption;
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
