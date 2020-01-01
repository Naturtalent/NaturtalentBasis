package it.naturtalent.e4.project.expimp.dialogs;


import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import it.naturtalent.e4.project.expimp.ExpImportData;
import it.naturtalent.e4.project.expimp.ExpImportDataModel;
import it.naturtalent.e4.project.expimp.ecp.ECPExportHandlerHelper;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;


public abstract class AbstractExportDialog extends TitleAreaDialog //implements EventHandler
{
	private DataBindingContext m_bindingContext;	
	
	public static final String EXPORTPATH_SETTING = "exportpathsetting"; //$NON-NLS-N$ 
	protected String exportSettingKey = EXPORTPATH_SETTING;
	
	private IDialogSettings dialogSettings;
	
	protected Button okButton;
	
	protected CheckboxTableViewer tableViewer;
	
	private ExpImportDataModel model = new ExpImportDataModel();
	
	protected ExpImportData [] selectedData;
	
	protected File expFile;
	
	protected ExportDestinationComposite exportDestinationComposite;
	
	protected String exportPath;
	
	
	protected static Shell shell;
	
		
	private Table table;
	private Composite compositeButton;
	private Button btnSelectAll;
	private Button btnNoSelect;
	private TableColumn tblclmnName;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AbstractExportDialog(Shell parentShell)
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
		setTitle("Export");
		setMessage("die ausgewählten Einträge in einer Datei speichern");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		GridData gd_container = new GridData(GridData.FILL_BOTH);
		gd_container.verticalAlignment = SWT.TOP;
		container.setLayoutData(gd_container);
		
		exportDestinationComposite = new ExportDestinationComposite(container, SWT.NONE);
		exportDestinationComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));	
		
		tableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addCheckStateListener(new ICheckStateListener()
		{
			public void checkStateChanged(CheckStateChangedEvent event)
			{
				update();
			}
		});
		
		table = tableViewer.getTable();
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = 432;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(400);
		tblclmnName.setText("Name");
		
		
		compositeButton = new Composite(container, SWT.NONE);
		compositeButton.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		btnSelectAll = new Button(compositeButton, SWT.NONE);
		btnSelectAll.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				tableViewer.setAllChecked(true);
				update();
			}
		});
		btnSelectAll.setText("alle auswählen"); //$NON-NLS-N$
		
		btnNoSelect = new Button(compositeButton, SWT.NONE);
		btnNoSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				tableViewer.setAllChecked(false);
				update();
			}
		});
		btnNoSelect.setText("keine auswählen");
		
	
		// ab jetzt ueberwacht der Broker Eingaben in 'ExportDestinationComposite'
		MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
		IEventBroker eventBroker = currentApplication.getContext().get(IEventBroker.class);
		if (eventBroker != null)
			exportDestinationComposite.setEventBroker(eventBroker);
			
		m_bindingContext = initDataBindings();
		init();
		
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
		createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, false);				
	}
	
	protected void update()
	{
		if(tableViewer.getCheckedElements().length == 0)
		{
			okButton.setEnabled(false);
			return;
		}
		okButton.setEnabled(StringUtils.isNotEmpty(exportPath));
	}
	
	protected void init()
	{
		dialogSettings= WorkbenchSWTActivator.getDefault().getDialogSettings();		
		if (dialogSettings != null)
		{
			exportPath = dialogSettings.get(this.exportSettingKey);
			if (StringUtils.isNotEmpty(exportPath))			
				exportDestinationComposite.setExportPath(exportPath);
		}		
	}
	
	public void setModelData(List<ExpImportData>expimpdata)
	{
		if(m_bindingContext != null)
			m_bindingContext.dispose();		
		model.setData(expimpdata);		
		m_bindingContext = initDataBindings();
	}
	
	public ExpImportData[] getSelectedData()
	{
		return selectedData;
	}

	public abstract void doExport();
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 727);
	}

	@Override
	protected void okPressed()
	{
		if ((dialogSettings != null) && (StringUtils.isNotEmpty(exportPath)))
			dialogSettings.put(exportSettingKey, exportPath);
		
		Object [] result = tableViewer.getCheckedElements();
		if(ArrayUtils.isNotEmpty(result) && StringUtils.isNotEmpty(exportPath))
		{
			selectedData = new ExpImportData[result.length];
			System.arraycopy(result, 0, selectedData, 0,result.length);
			
			expFile = new File(exportPath);
			if(expFile.exists())
			{
				if(!MessageDialog.openQuestion(getShell(), "Export", "vorhandene Datei überschreiben ?")) //$NON-NLS-N$
						return;				
			}
			doExport();
		}
				
		super.okPressed();
	}
	
	/**
	 * Export EMF Modeldata 'eObjects' in die ausgewaehlte Datei. 
	 * 	  
	 * @param exportPath
	 * @param eObjects
	 */
	public void exportEMFModeData()
	{
		// die zum Export selektioerten EObjects auflisten
		final List<EObject>eObjects = new LinkedList<EObject>();				
		for(ExpImportData expImpData : selectedData)
		{
			Object obj = expImpData.getData();
			if (obj instanceof EObject)				
				eObjects.add(EcoreUtil.copy((EObject) obj));				
		}	
				
		// Dateiname des Properties generieren und Daten exportieren
		shell.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				ECPExportHandlerHelper.export(shell, eObjects, exportPath);
			}
		});
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
