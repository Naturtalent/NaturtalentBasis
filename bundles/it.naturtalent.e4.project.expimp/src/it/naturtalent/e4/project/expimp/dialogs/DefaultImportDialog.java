package it.naturtalent.e4.project.expimp.dialogs;

import java.io.File;
import java.util.List;

import it.naturtalent.e4.project.expimp.ExpImportData;
import it.naturtalent.e4.project.expimp.ExpImportDataModel;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import it.naturtalent.e4.project.expimp.Messages;

@Deprecated
public class DefaultImportDialog extends TitleAreaDialog
{
	
	public final static String IMPORTDESTINATION_EVENT = "importdestination"; //$NON-NLS-1$

	
	private DataBindingContext m_bindingContext;
	
	@Inject @Optional private static Shell shell;	
	@Inject @Optional protected IEventBroker eventBroker;

	public static final String IMPORTPATH_SETTING = "importpathsetting"; //$NON-NLS-N$ 
	private String importSettingKey = IMPORTPATH_SETTING;

	private IDialogSettings dialogSettings;
		
	private Text txtSource;
	private Table table;
	private CheckboxTableViewer tableViewer;

	protected String importPath;
	
	private ExpImportDataModel model = new ExpImportDataModel();	
	protected ExpImportData [] selectedData;
	
	private Button okButton;
	private Button btnCheckOverwrite;
	private boolean overwritePermission = false;


	/**
	 * Create the dialog.
	 * @wbp.parser.constructor
	 */
	public DefaultImportDialog()
	{
		super(shell);
	}
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public DefaultImportDialog(Shell parentShell)
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
		setTitleImage(Icon.WIZBAN_EXPORT.getImage(IconSize._75x66_TitleDialogIconSize));
		setMessage(Messages.AbstractImportDialog_this_message);
		setTitle(Messages.AbstractImportDialog_this_title);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(4, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblSourcFile = new Label(container, SWT.NONE);
		lblSourcFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSourcFile.setText(Messages.AbstractImportDialog_lblSourcFile_text);
		new Label(container, SWT.NONE);
		
		txtSource = new Text(container, SWT.BORDER);		
		txtSource.setEditable(false);
		txtSource.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnSelect = new Button(container, SWT.NONE);
		btnSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				importPath = null;
				FileDialog dlg = new FileDialog(getShell());

				// 'xml' - Files filtern
				dlg.setText("Importverzeichnis");
				dlg.setFilterExtensions(new String[]{"*.xml"}); //$NON-NLS-1$
				dlg.setFilterPath(importPath);
				
				importPath = dlg.open();
				if (importPath != null)
				{								
					txtSource.setText(importPath);
					eventBroker.post(IMPORTDESTINATION_EVENT, importPath);
				}
				update();
			}
		});
		btnSelect.setText(Messages.AbstractImportDialog_btnSelect_text);
		
		tableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addCheckStateListener(new ICheckStateListener()
		{
			public void checkStateChanged(CheckStateChangedEvent event)
			{
				update();
			}
		});
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gd_table.widthHint = 429;
		table.setLayoutData(gd_table);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnName = tableViewerColumn.getColumn();
		tblclmnName.setWidth(422);
		tblclmnName.setText(Messages.AbstractImportDialog_tblclmnName_text);
		
		Composite compositeButton = new Composite(container, SWT.NONE);
		compositeButton.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));
		
		Button btnSelectAll = new Button(compositeButton, SWT.NONE);
		btnSelectAll.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				tableViewer.setAllChecked(true);
				update();
			}
		});
		btnSelectAll.setText(Messages.AbstractImportDialog_btnSelectAll_text);
		
		Button btnNoSelection = new Button(compositeButton, SWT.NONE);
		btnNoSelection.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				tableViewer.setAllChecked(false);
				update();
			}
		});
		btnNoSelection.setText(Messages.AbstractImportDialog_btnNoSelection_text);
		
		Composite compositeCheck = new Composite(container, SWT.NONE);
		compositeCheck.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeCheck.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 4, 1));
		
		btnCheckOverwrite = new Button(compositeCheck, SWT.CHECK);
		btnCheckOverwrite.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				overwritePermission = btnCheckOverwrite.getSelection(); 
			}
		});		
		setOverwritePermission(true);
		btnCheckOverwrite.setText(Messages.AbstractImportDialog_btnCheckOverwrite_text);
		
		dialogSettings= WorkbenchSWTActivator.getDefault().getDialogSettings();		
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,true);
		okButton.setEnabled(false);
		Button button = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		m_bindingContext = initDataBindings();
		
	}
	
	public void setModelData(List<ExpImportData>expimpdata)
	{
		if(m_bindingContext != null)
			m_bindingContext.dispose();		
		model.setData(expimpdata);		
		m_bindingContext = initDataBindings();
	}

	protected void readImportSource ()
	{
		
	}
	
	/*
	public void init(String kontakteCollectionName, String importSettingKey)
	{				
		dialogSettings= WorkbenchSWTActivator.getDefault().getDialogSettings();		
		if (dialogSettings != null)
		{
			importPath = dialogSettings.get(this.importSettingKey);
			if (StringUtils.isNotEmpty(importPath))	
			{
				txtSource.setText(importPath);
				readImportSource();
			}
		}			
	}
	*/
	
	private void update()
	{
		if(okButton != null)
			okButton.setEnabled(tableViewer.getCheckedElements().length > 0);
	}
	
	@Override
	protected void okPressed()
	{
		if ((dialogSettings != null) && (StringUtils.isNotEmpty(importPath)))
			dialogSettings.put(importSettingKey, importPath);
		
		Object [] result = tableViewer.getCheckedElements();
		if(ArrayUtils.isNotEmpty(result) && StringUtils.isNotEmpty(importPath))
		{
			selectedData = new ExpImportData[result.length];
			System.arraycopy(result, 0, selectedData, 0,result.length);
		}
		
		super.okPressed();
	}
	
	
	
	public void setImportPath(String importPath)
	{
		this.importPath = importPath;
		
		if(StringUtils.isNotEmpty(importPath))
			txtSource.setText(importPath);
	}

	public ExpImportData[] getSelectedData()
	{
		return selectedData;
	}
	
	public IDialogSettings getDialogSettings()
	{
		return dialogSettings;
	}
	
	public boolean isOverwritePermission()
	{
		return overwritePermission;
	}

	public void setOverwritePermission(boolean overwritePermission)
	{
		this.overwritePermission = overwritePermission;
		btnCheckOverwrite.setSelection(overwritePermission);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 673);
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), ExpImportData.class, "label");
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		tableViewer.setContentProvider(listContentProvider);
		//
		IObservableList dataModelObserveList = BeanProperties.list("data").observe(model);
		tableViewer.setInput(dataModelObserveList);
		//
		return bindingContext;
	}
}
