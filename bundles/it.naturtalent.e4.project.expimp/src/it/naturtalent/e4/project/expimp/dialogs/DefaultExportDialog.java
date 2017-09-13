package it.naturtalent.e4.project.expimp.dialogs;

import it.naturtalent.e4.project.expimp.ExpImportData;
import it.naturtalent.e4.project.expimp.ExpImportDataModel;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import java.io.File;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DefaultExportDialog extends TitleAreaDialog implements EventHandler
{
	
	private static class Sorter extends ViewerSorter
	{
		Collator collator = Collator.getInstance(Locale.GERMAN);
		
		public int compare(Viewer viewer, Object e1, Object e2)
		{
			String stg1 = null;
			String stg2 = null;
						
			if(e1 instanceof ExpImportData)
			{				
				stg1 = ((ExpImportData)e1).getLabel();
				stg2 = ((ExpImportData)e2).getLabel();
			}
			
			return collator.compare(stg1, stg2);
		}
	}

	
	//protected IEventBroker eventBroker;
	
	// Exportoptionen
	public static final int EXPORTOPTION_XMLFORMAT = 0;
	public static final int EXPORTOPTION_OOFORMAT = 1;
	public static final int EXPORTOPTION_MSFORMAT = 2;
	private int exportOption = EXPORTOPTION_XMLFORMAT;
	

	private IDialogSettings dialogSettings;
	private Button okButton;
	
	public static final String EXPORTTODOCUMENTPATH_SETTING = "exporttodocumentpathsetting"; //$NON-NLS-N$ 
	private String exportSettingKey = EXPORTTODOCUMENTPATH_SETTING;

	private DataBindingContext m_bindingContext;
	private Table table;
	private Button btnSelectAll;
	private Button btnNoSelect;
	private ExportDestinationComposite exportDestinationComposite;
	private Button btnRadioODF;
	private Button btnRadioExcel;
	private Button btnRadioXML;

	private ExpImportDataModel model = new ExpImportDataModel();
	protected ExpImportData [] selectedData;
	protected File expFile;
	
	private CheckboxTableViewer tableViewer;
	
	private boolean archivState = false;
	protected String exportPath;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public DefaultExportDialog(Shell parentShell)
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
		setTitle(Messages.DefaultExportDialog_this_title);
		setMessage(Messages.DefaultExportDialog_this_message);

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
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
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer.setSorter(new Sorter ());
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		btnSelectAll = new Button(composite, SWT.NONE);
		btnSelectAll.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				tableViewer.setAllChecked(true);
				update();
			}
		});
		btnSelectAll.setText("alle auswaehlen");
		
		btnNoSelect = new Button(composite, SWT.NONE);
		btnNoSelect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				tableViewer.setAllChecked(false);
				update();
			}
		});
		btnNoSelect.setText("keine auswaehlen");
		
		Group group = new Group(container, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setText("Optionen");
		group.setLayout(new GridLayout(1, false));
		
		// XML Export
		btnRadioXML = new Button(group, SWT.RADIO);
		btnRadioXML.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				exportOption = EXPORTOPTION_XMLFORMAT;	
				exportDestinationComposite.setEnabled(true);
			}
		});
		btnRadioXML.setText("XML Format ");
		btnRadioXML.setSelection(true);
		
		// ODF Dokumentexport
		btnRadioODF = new Button(group, SWT.RADIO);
		btnRadioODF.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				exportOption = EXPORTOPTION_OOFORMAT;	
				//exportDestinationComposite.setEnabled(false);
			}
		});
		btnRadioODF.setText("OpenDocument Tabellenkalkulation (LibreOffice)");
		
		// MSExcel Dokumentexport
		btnRadioExcel = new Button(group, SWT.RADIO);
		btnRadioExcel.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				exportOption = EXPORTOPTION_MSFORMAT;	
				//exportDestinationComposite.setEnabled(false);
			}
		});
		btnRadioExcel.setText("MS Excel");

		m_bindingContext = initDataBindings();
		
		dialogSettings= WorkbenchSWTActivator.getDefault().getDialogSettings();		
		if (dialogSettings != null)
		{
			exportPath = dialogSettings.get(this.exportSettingKey);
			if (StringUtils.isNotEmpty(exportPath))			
				exportDestinationComposite.setExportPath(exportPath);
		}		
		
		return area;
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
		//okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);				
	}
	
	private void update()
	{		
		boolean checkedElements = tableViewer.getCheckedElements().length > 0;
		if(exportOption != EXPORTOPTION_XMLFORMAT)
		{
			// in Dokument exportieren
			okButton.setEnabled(checkedElements);
			return;
		}
		
		// XML Export
		okButton.setEnabled(checkedElements & StringUtils.isNotEmpty(exportPath));
	}
	
	public void setEventBroker(IEventBroker eventBroker)
	{
		eventBroker.subscribe(ExportDestinationComposite.EXPORTDESTINATION_EVENT, this);
		exportDestinationComposite.setEventBroker(eventBroker);
	}
	
	@Override
	public void handleEvent(Event event)
	{
		exportPath = (String) event.getProperty(IEventBroker.DATA);
		update();
	}

	public void init(List<ExpImportData>lexpimpdata)
	{
		setModelData(lexpimpdata);
		update();
	}
	
	private void setModelData(List<ExpImportData>expimpdata)
	{
		if(m_bindingContext != null)
			m_bindingContext.dispose();		
		model.setData(expimpdata);		
		m_bindingContext = initDataBindings();
	}
	
	public void setSorter(ViewerSorter viewSorter)
	{
		tableViewer.setSorter(viewSorter);
	}
	
	@Override
	public boolean close()
	{
		exportDestinationComposite.getEventBroker().unsubscribe(this);
		return super.close();
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
		}
				
		super.okPressed();
	}
	
	public String getExportPath()
	{
		return exportPath;
	}
	
	public void setExportPath(String exportPath)
	{
		this.exportPath = exportPath;
		exportDestinationComposite.setExportPath(exportPath);
	}
	
	public int getExportOption()
	{
		return exportOption;
	}
	
	public void setExportOption(int exportOption)
	{
		this.exportOption = exportOption;
		
		btnRadioXML.setSelection(false);
		btnRadioODF.setSelection(false);
		btnRadioExcel.setSelection(false);
				
		switch (exportOption)
			{
				case EXPORTOPTION_XMLFORMAT:
					btnRadioXML.setSelection(true);
					break;

				case EXPORTOPTION_OOFORMAT:
					btnRadioODF.setSelection(true);
					break;

				case EXPORTOPTION_MSFORMAT:
					btnRadioExcel.setSelection(true);
					break;

				default:
					break;
			}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(523, 795);
	}

	public ExpImportData[] getSelectedData()
	{
		return selectedData;
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
