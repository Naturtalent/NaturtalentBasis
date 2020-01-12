package it.naturtalent.e4.project.expimp.dialogs;

import java.io.File;
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
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import it.naturtalent.e4.project.expimp.ExpImportData;
import it.naturtalent.e4.project.expimp.ExpImportDataModel;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

public abstract class AbstractExportDialog extends TitleAreaDialog
{
	private DataBindingContext m_bindingContext;	
	
	public static final String EXPORTPATH_SETTING = "exportpathsetting"; //$NON-NLS-N$ 
	protected String exportSettingKey = EXPORTPATH_SETTING;

	private IDialogSettings dialogSettings;
	
	protected Button okButton;
	
	protected CheckboxTableViewer checkboxTableViewer;
	
	private ExpImportDataModel model = new ExpImportDataModel();
	
	// die gecheckten Elemente
	protected ExpImportData [] selectedData;
	
	// Pfad der Exportdatei
	protected File expFile;

	protected ExportDestinationComposite exportDestinationComposite;
	
	protected String exportPath;
	
	
	protected static Shell shell;
	
	private Table table;
	private Text textFilter;
	private Button btnSelectAll;
	private Button btnNoSelect;
	private TableColumn tblclmnName;
	private String stgFilter;
	
	// sammelt die gecheckten Element 
	private Object [] checkedElements = null;
	
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
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		exportDestinationComposite = new ExportDestinationComposite(container, SWT.NONE);
		exportDestinationComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		textFilter = new Text(container, SWT.BORDER);
		textFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textFilter.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{			
				stgFilter = textFilter.getText();	
				checkboxTableViewer.refresh();
				if(ArrayUtils.isNotEmpty(checkedElements))
					checkboxTableViewer.setCheckedElements(checkedElements);
			}
		});
		
		Button btnLoeschen = new Button(container, SWT.NONE);
		btnLoeschen.setToolTipText("Filter zurücksetzen");
		btnLoeschen.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				stgFilter = null;
				textFilter.setText("");		
			}
		});
		btnLoeschen.setText("löschen");
		
		checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		checkboxTableViewer.setFilters(new ViewerFilter []{new NameFilter()});
		checkboxTableViewer.setComparator(new ViewerComparator());
		checkboxTableViewer.addCheckStateListener(new ICheckStateListener()
		{
			public void checkStateChanged(CheckStateChangedEvent event)
			{
				update();
			}
		});
		
		checkboxTableViewer.addCheckStateListener(new ICheckStateListener()
		{			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event)
			{
				Object element= event.getElement();
				
				TableItem tableItem = (TableItem) checkboxTableViewer.testFindItem(element);
				boolean checkState = tableItem.getChecked();
				if(checkState)
					checkedElements = ArrayUtils.add(checkedElements, element);
				else
					checkedElements = ArrayUtils.removeElement(checkedElements, element);
				
				// eingegraute Element koennen nicht gecheckt werden
				if(checkboxTableViewer.getGrayed(element))
				{
					checkboxTableViewer.setChecked(element, false);
					checkedElements = ArrayUtils.removeElement(checkedElements, element);
				}
			}
		});
		
		table = checkboxTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(400);
		tblclmnName.setText("Name");

		
		Composite compositeButton = new Composite(container, SWT.NONE);
		compositeButton.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		
		btnSelectAll = new Button(compositeButton, SWT.NONE);
		btnSelectAll.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				checkboxTableViewer.setAllChecked(true);
				update();
			}
		});
		btnSelectAll.setText("alle auswählen");
		
		btnNoSelect = new Button(compositeButton, SWT.NONE);
		btnNoSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				checkboxTableViewer.setAllChecked(false);
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

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 765);
	}
	
	protected void update()
	{
		if(checkboxTableViewer.getCheckedElements().length == 0)
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
	
	public String getExportPath()
	{
		return exportPath;
	}

	@Override
	protected void okPressed()
	{
		if ((dialogSettings != null) && (StringUtils.isNotEmpty(exportPath)))
			dialogSettings.put(exportSettingKey, exportPath);
		
		if(ArrayUtils.isNotEmpty(checkedElements) && StringUtils.isNotEmpty(exportPath))
		{
			selectedData = new ExpImportData[checkedElements.length];
			System.arraycopy(checkedElements, 0, selectedData, 0,checkedElements.length);
		}
				
		super.okPressed();
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), ExpImportData.class, "label");
		checkboxTableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		checkboxTableViewer.setContentProvider(listContentProvider);
		//
		IObservableList dataModelObserveList = BeanProperties.list("data").observe(model);
		checkboxTableViewer.setInput(dataModelObserveList);
		//
		return bindingContext;
	}
}
