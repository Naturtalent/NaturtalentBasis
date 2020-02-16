package it.naturtalent.e4.project.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import it.naturtalent.e4.project.model.project.Proxies;
import it.naturtalent.e4.project.model.project.Proxy;
import it.naturtalent.e4.project.ui.dialogs.emf.EditNetzwerkConnectPraeferenzDialog;

/**
 * @author dieter
 *
 */
public class NetzwerkPreferenceComposite extends Composite
{
	/*
	 * Provider zur Darstellung im Viewer
	 */
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof Proxy)
			{
				Proxy proxy = (Proxy) element;
				switch (columnIndex)
					{
						case 0:	return "";							
						case 1: return proxy.getSchemata();
						case 2: return proxy.getHost();
						case 3: return proxy.getPort();
						default: return null;							
					}				
			}
			return element.toString();
		}
	}
	
	private Map<String,String>templateMap;
	
	private Map<String,String>defaultMap;
	
	private Table table;
	//private Button btnDelete;
	private Button btnEdit;
	private CheckboxTableViewer tableViewer;
	private TableColumn tblclmnNewColumn1;
	private TableViewerColumn tableViewerColumnCheck;
	private TableColumn tblclmnNewColumn2;
	private TableViewerColumn tableViewerColumnSchema;
	private TableColumn tblclmnNewColumn3;
	private TableViewerColumn tableViewerColumnHost;
	private TableColumn tblclmnNewColumn4;
	private TableViewerColumn tableViewerColumnPort;
	private TableColumn tblclmnNewColumn5;
	private TableViewerColumn tableViewerColumn;
	private Table table_1;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public NetzwerkPreferenceComposite(Composite parent, int style)
	{
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_lblNewLabel.widthHint = 200;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("Proxies");
		new Label(this, SWT.NONE);
		
		Composite composite = new Composite(this, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.widthHint = 450;
		gd_composite.heightHint = 200;
		composite.setLayoutData(gd_composite);
		TableColumnLayout tcl_composite = new TableColumnLayout();
		composite.setLayout(tcl_composite);
		
		tableViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateWidgets();
			}
		});
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tableViewerColumnCheck = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnNewColumn1 = tableViewerColumnCheck.getColumn();
		tcl_composite.setColumnData(tblclmnNewColumn1, new ColumnPixelData(30, true, true));
		//tblclmnNewColumn1.setText();
		
		tableViewerColumnSchema = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnNewColumn2 = tableViewerColumnSchema.getColumn();
		tblclmnNewColumn2.setAlignment(SWT.CENTER);
		tcl_composite.setColumnData(tblclmnNewColumn2, new ColumnPixelData(56, true, true));
		tblclmnNewColumn2.setText(Messages.NetzwerkPreferenceComposite_tblclmnNewColumn_text1);
		
		tableViewerColumnHost = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnNewColumn3 = tableViewerColumnHost.getColumn();
		tblclmnNewColumn3.setAlignment(SWT.CENTER);
		tcl_composite.setColumnData(tblclmnNewColumn3, new ColumnPixelData(50, true, true));
		tblclmnNewColumn3.setText(Messages.NetzwerkPreferenceComposite_tblclmnNewColumn_text2);
		
		tableViewerColumnPort = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnNewColumn4 = tableViewerColumnPort.getColumn();
		tblclmnNewColumn4.setAlignment(SWT.CENTER);
		tcl_composite.setColumnData(tblclmnNewColumn4, new ColumnPixelData(50, true, true));
		tblclmnNewColumn4.setText(Messages.NetzwerkPreferenceComposite_tblclmnNewColumn_text3);
		
		tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnNewColumn5 = tableViewerColumn.getColumn();
		tcl_composite.setColumnData(tblclmnNewColumn5, new ColumnPixelData(150, true, true));
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		Composite compositeButton = new Composite(this, SWT.NONE);
		compositeButton.setLayout(new FillLayout(SWT.VERTICAL));
		
		/*
		btnAdd = new Button(compositeButton, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileTemplateDialog dialog = new FileTemplateDialog(getShell());
				dialog.create();
				PreferenceData preferenceData = dialog.createPreferenceData(); 
				dialog.setPreferenceData(preferenceData);				
				if(dialog.open() == FileTemplateDialog.OK)
				{
					preferenceData = dialog.getPreferenceData();
					templateMap.put(preferenceData.name, preferenceData.url);					
					tableViewer.refresh();					
				}
			}
		});
		btnAdd.setText(Messages.ProjectPreferenceComposite_btnAdd_text);
		*/
		
		btnEdit = new Button(compositeButton, SWT.NONE);
		btnEdit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doEdit();
			}
		});
		btnEdit.setText(Messages.ProjectPreferenceComposite_btnEdit_text);
		
		
		
		
		/*
		 * 'delete' momentan nicht aktiviert
		 */
		/*
		btnDelete = new Button(compositeButton, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
			}
		});
		btnDelete.setText(Messages.ProjectPreferenceComposite_btnDelete_text);
		*/
		updateWidgets();		
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void doEdit()
	{	
		StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
		Object selObj = selection.getFirstElement();
		if (selObj instanceof Proxy)
		{
			Proxy proxy = (Proxy) selObj;
			EditNetzwerkConnectPraeferenzDialog dialog = new EditNetzwerkConnectPraeferenzDialog(
					Display.getDefault().getActiveShell(), proxy);	
			dialog.open();
		}
	}
	
	/*
	 * Die praeferenziereten Proxies in der Tabelle anzeigen und checken der 'inUse' - Proxies
	 */
	public void setPreferenceValue(Proxies proxies)
	{		
		tableViewer.setInput(proxies.getProxies());
		
		// 'inUse' - Proxies checken
		Proxy[] checkedElemensts = null;		
		for(Proxy proxy: proxies.getProxies())
		{
			if(proxy.isInUse())
				checkedElemensts = (Proxy[]) ArrayUtils.add(checkedElemensts, proxy);
		}			
		if(checkedElemensts != null)
			tableViewer.setCheckedElements(checkedElemensts);
		
		/*
		templateMap = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(preferenceValue))
		{
			String[] templateArray = StringUtils.split(preferenceValue, ",");
			for (int i = 0; (i + 1) < templateArray.length; i++)
				templateMap.put(templateArray[i], templateArray[++i]);
			tableViewer.setInput(templateMap.keySet());
		}
		*/	
	}
	
	public Proxy [] getCheckedProxies()
	{		
		Object[] result = tableViewer.getCheckedElements();			
		Proxy[] proxies = new Proxy [result.length];
		System.arraycopy(result, 0, proxies, 0,result.length);
		return proxies;
	}
	
	/*
	public String getPreferenceValue()
	{
		StringBuilder prefValue = new StringBuilder();
		for(String key : templateMap.keySet())
		{
			String value = templateMap.get(key);			
			if(StringUtils.isEmpty(prefValue))			
				prefValue.append(key+","+value);
			else
				prefValue.append(","+key+","+value);
		}
		
		return prefValue.toString();
	}
	*/
	
	/*
	 * Enable 'edit' wenn Proxy selektiert ist
	 * 'add' und 'delete' sind momentan nicht aktiviert 
	 */
	private void updateWidgets()
	{
		btnEdit.setEnabled(false);
		//btnDelete.setEnabled(false);
		
		StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
		Object selObject  = selection.getFirstElement();
		if (selObject instanceof Proxy)
		{
			Proxy proxy = (Proxy) selObject;
			btnEdit.setEnabled(true);
			//btnDelete.setEnabled(true);
		}
	}
	
	/*
	 * Listener fuer Einzelchecking (nur ein Eintrag kann gecheckt werden) 
	 */
	protected void addCheckStateListener()
	{
		tableViewer.addCheckStateListener(new ICheckStateListener()
		{			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event)
			{							
				tableViewer.setAllChecked(false);
				tableViewer.setChecked(event.getElement(), event.getChecked());
			}
		});
	}

}
