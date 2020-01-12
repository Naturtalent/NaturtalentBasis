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
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import it.naturtalent.e4.project.expimp.ExpImportData;
import it.naturtalent.e4.project.expimp.ExpImportDataModel;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.expimp.ecp.ECPExportHandlerHelper;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;
import org.eclipse.swt.widgets.Label;


public abstract class AbstractExportDialog2 extends TitleAreaDialog //implements EventHandler
{
	private DataBindingContext m_bindingContext;	
	
	public static final String EXPORTPATH_SETTING = "exportpathsetting"; //$NON-NLS-N$ 
	protected String exportSettingKey = EXPORTPATH_SETTING;
	
	private IDialogSettings dialogSettings;
	
	protected Button okButton;
	
	protected CheckboxTableViewer checkBoxTableViewer;
	
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
	private String stgFilter;
	
	// sammelt die gecheckten Element 
	private Object [] checkedElements = null;
	private Text text;
	private Composite compositeFilter;
	private Button btnLoeschen;

	
	// Ein Filter fuer die Exportdaten
	private class NameFilter extends ViewerFilter
	{
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element)
		{					
			if (element instanceof ExpImportData)
			{	
				String name = ((ExpImportData)element).getLabel();
				if(StringUtils.isNotEmpty(stgFilter))					
					return StringUtils.containsIgnoreCase(name, stgFilter);					
			}
			
			return true;
		}
	}
		
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AbstractExportDialog2(Shell parentShell)
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
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		/*
		GridData gd_container = new GridData(GridData.FILL_BOTH);
		gd_container.verticalAlignment = SWT.TOP;
		container.setLayoutData(gd_container);
		*/
		container.setLayout(new GridLayout(2, false));
		
		// eigene Composite fuer die Zielresource
		exportDestinationComposite = new ExportDestinationComposite(container, SWT.NONE);
		exportDestinationComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		compositeFilter = new Composite(container, SWT.NONE);
		compositeFilter.setLayout(new GridLayout(2, false));
		compositeFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		text = new Text(compositeFilter, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 440;
		text.setLayoutData(gd_text);
		
		btnLoeschen = new Button(compositeFilter, SWT.NONE);
		GridData gd_btnLoeschen = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnLoeschen.widthHint = 81;
		btnLoeschen.setLayoutData(gd_btnLoeschen);
		btnLoeschen.setText("löschen");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		
		checkBoxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		checkBoxTableViewer.addCheckStateListener(new ICheckStateListener()
		{
			public void checkStateChanged(CheckStateChangedEvent event)
			{
				update();
			}
		});
		
		table = checkBoxTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(400);
		tblclmnName.setText("Name");
				
		compositeButton = new Composite(container, SWT.NONE);
		compositeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		compositeButton.setLayout(new FillLayout(SWT.HORIZONTAL));
		

		
		btnSelectAll = new Button(compositeButton, SWT.NONE);
		btnSelectAll.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				checkBoxTableViewer.setAllChecked(true);
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
				checkBoxTableViewer.setAllChecked(false);
				update();
			}
		});
		btnNoSelect.setText("keine auswählen");
		new Label(container, SWT.NONE);
		
	
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
		if(checkBoxTableViewer.getCheckedElements().length == 0)
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
		return new Point(554, 727);
	}

	@Override
	protected void okPressed()
	{
		if ((dialogSettings != null) && (StringUtils.isNotEmpty(exportPath)))
			dialogSettings.put(exportSettingKey, exportPath);
		
		Object [] result = checkBoxTableViewer.getCheckedElements();
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

	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), ExpImportData.class, "label");
		checkBoxTableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		checkBoxTableViewer.setContentProvider(listContentProvider);
		//
		IObservableList dataModelObserveList = BeanProperties.list("data").observe(model);
		checkBoxTableViewer.setInput(dataModelObserveList);
		//
		return bindingContext;
	}
}
