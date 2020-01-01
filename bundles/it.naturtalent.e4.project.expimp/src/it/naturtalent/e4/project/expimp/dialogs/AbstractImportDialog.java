package it.naturtalent.e4.project.expimp.dialogs;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.swt.WorkbenchSWTActivator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import it.naturtalent.e4.project.expimp.ExpImportData;
import it.naturtalent.e4.project.expimp.ExpImportDataModel;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;



/**
 * Grundgeruest eines Importdialogs.
 * 
 * @author dieter
 *
 */
public abstract class AbstractImportDialog extends TitleAreaDialog
{
	private DataBindingContext m_bindingContext;
	
	@Inject @Optional private static Shell shell;	
	@Inject @Optional protected IEventBroker eventBroker;

	public static final String IMPORTPATH_SETTING = "importpathsetting"; //$NON-NLS-N$ 
	protected String importSettingKey = IMPORTPATH_SETTING;

	private IDialogSettings dialogSettings;
		
	protected Text txtSource;
	private Table table;
	protected CheckboxTableViewer checkBoxTableViewer;

	protected String importPath;
	
	// die eingelesenen Importdaten
	protected List<ExpImportData>lexpimpdata;
	
	private ExpImportDataModel model = new ExpImportDataModel();	
	protected ExpImportData [] selectedData;
	
	private Button okButton;
	protected Button cancelButton;
	protected Button btnCheckOverwrite;
	
	private Button btnResetFilter;
	private Text textFilter;
	private String stgFilter;	
	
	// sammelt die gecheckten Element 
	private Object [] checkedElements = null;
	
	// Ein Filter fuer die Importdaten
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

	// Labelprovider mit der Moeglichkeit zum 'eingrauen' von ImportElementen
	protected class GrayedTableLabelProvider extends LabelProvider 
	{
		public GrayedTableLabelProvider()
		{			
		}

		public Image getColumnImage(Object element, int columnIndex)
		{
			return Icon.ICON_PROJECT.getImage(IconSize._16x16_DefaultIconSize);
		}

		public String getText(Object element)
		{
			if(element instanceof ExpImportData)
			{		
				if(checkBoxTableViewer.getGrayed(element))
				{
					Display display = getShell().getDisplay();					
					Color gray = display.getSystemColor(SWT.COLOR_GRAY);					
					TableItem tableItem = (TableItem) checkBoxTableViewer.testFindItem(element);
					tableItem.setForeground(gray);
				}
				
				return ((ExpImportData)element).getLabel();
			}
			
			return element.toString();
		}
	}

	
	/**
	 * Create the dialog.
	 * @wbp.parser.constructor
	 */
	public AbstractImportDialog()
	{
		super(shell);
	}
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AbstractImportDialog(Shell parentShell)
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
				handleSelectButton();
				update();
			}
		});
		btnSelect.setText(Messages.AbstractImportDialog_btnSelect_text);
		
		textFilter = new Text(container, SWT.BORDER);		
		textFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		textFilter.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{			
				stgFilter = textFilter.getText();	
				checkBoxTableViewer.refresh();
				if(ArrayUtils.isNotEmpty(checkedElements))
					checkBoxTableViewer.setCheckedElements(checkedElements);
			}
		});
		
		btnResetFilter = new Button(container, SWT.NONE);
		btnResetFilter.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				stgFilter = null;
				textFilter.setText("");				
			}
		});
		btnResetFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnResetFilter.setToolTipText(Messages.AbstractImportDialog_btnResetFilter_toolTipText);
		btnResetFilter.setText(Messages.AbstractImportDialog_btnNewButton_text);
		
		checkBoxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		checkBoxTableViewer.setFilters(new ViewerFilter []{new NameFilter()});
		checkBoxTableViewer.addCheckStateListener(new ICheckStateListener()
		{
			public void checkStateChanged(CheckStateChangedEvent event)
			{
				update();
			}
		});
		
		checkBoxTableViewer.addCheckStateListener(new ICheckStateListener()
		{			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event)
			{
				Object element= event.getElement();
				
				TableItem tableItem = (TableItem) checkBoxTableViewer.testFindItem(element);
				boolean checkState = tableItem.getChecked();
				if(checkState)
					checkedElements = ArrayUtils.add(checkedElements, element);
				else
					checkedElements = ArrayUtils.removeElement(checkedElements, element);
				
				// eingegraute Element koennen nicht gecheckt werden
				if(checkBoxTableViewer.getGrayed(element))
				{
					checkBoxTableViewer.setChecked(element, false);
					checkedElements = ArrayUtils.removeElement(checkedElements, element);
				}
			}
		});


		table = checkBoxTableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gd_table.widthHint = 429;
		table.setLayoutData(gd_table);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(checkBoxTableViewer, SWT.NONE);
		TableColumn tblclmnName = tableViewerColumn.getColumn();
		tblclmnName.setWidth(422);
		tblclmnName.setText(Messages.AbstractImportDialog_tblclmnName_text);
		
		Composite compositeButton = new Composite(container, SWT.NONE);
		compositeButton.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));
		
		// alle Elemente auf checked
		Button btnSelectAll = new Button(compositeButton, SWT.NONE);
		btnSelectAll.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				//checkBoxTableViewer.setAllChecked(true);
				// grayed Items werden nicht gecheckt
				Table table = checkBoxTableViewer.getTable();
				int n = table.getItemCount();
				for(int i = 0;i < n;i++)
				{
					Object item = checkBoxTableViewer.getElementAt(i);
					checkBoxTableViewer.setChecked(item, !checkBoxTableViewer.getGrayed(item));
					checkedElements = ArrayUtils.add(checkedElements, item);
				}

				update();
			}
		});
		btnSelectAll.setText(Messages.AbstractImportDialog_btnSelectAll_text);
		
		// alle Elemente auf unchecked
		Button btnNoSelection = new Button(compositeButton, SWT.NONE);
		btnNoSelection.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				checkBoxTableViewer.setAllChecked(false);
				checkedElements = null;
				update();
			}
		});
		btnNoSelection.setText(Messages.AbstractImportDialog_btnNoSelection_text);
		
		Composite compositeCheck = new Composite(container, SWT.NONE);
		compositeCheck.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeCheck.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 4, 1));
		
		btnCheckOverwrite = new Button(compositeCheck, SWT.CHECK);
		btnCheckOverwrite.setSelection(true);
		btnCheckOverwrite.setText(Messages.AbstractImportDialog_btnCheckOverwrite_text);
		
		try
		{
			init();
		} catch (Exception e1)
		{
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		
		return area;
	}
	
	// Auswahle einer Importdatei mit dem FileDialog
	protected void handleSelectButton()
	{
		importPath = null;
		FileDialog dlg = new FileDialog(getShell());

		// Change the title bar text
		dlg.setText("Importverzeichnis");
		dlg.setFilterExtensions(new String[]{"*.xmi","*.xml"}); //$NON-NLS-1$
		dlg.setFilterPath(importPath);
		
		String importFile = dlg.open();
		if (importFile != null)
		{			
			importPath = importFile;
			txtSource.setText(importFile);
			
			readImportSource();
			
		}
	

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
		cancelButton = createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, false);
		m_bindingContext = initDataBindings();
		
	}
	
	// wird aufgerufen bei der Initialisierung und ImportFilePath-Selektion
	public abstract void readImportSource();
	
	// Daten werden importiert
	public abstract void doImport(ExpImportData [] selectedData);
	
	public abstract void removeExistedObjects(List<EObject>importObjects);
	
	// Die ExpImportDaten in Modell einlesen	
	public void setModelData(List<ExpImportData>expimpdata)
	{
		if(m_bindingContext != null)
			m_bindingContext.dispose();		
		model.setData(expimpdata);		
		m_bindingContext = initDataBindings();
	}

	public List<ExpImportData> getModelData()
	{
		return model.getData();
	}
	
	
	protected void init()
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
	
	private void update()
	{
		if(okButton != null)
			okButton.setEnabled(checkBoxTableViewer.getCheckedElements().length > 0);
	}
	
	@Override
	protected void okPressed()
	{
		if ((dialogSettings != null) && (StringUtils.isNotEmpty(importPath)))
			dialogSettings.put(importSettingKey, importPath);

		if(ArrayUtils.isNotEmpty(checkedElements) && StringUtils.isNotEmpty(importPath))
		{
			selectedData = new ExpImportData[checkedElements.length];
			System.arraycopy(checkedElements, 0, selectedData, 0,checkedElements.length);
			
			doImport(selectedData);
		}

		/*
		Object [] result = checkBoxTableViewer.getCheckedElements();
		if(ArrayUtils.isNotEmpty(result) && StringUtils.isNotEmpty(importPath))
		{
			selectedData = new ExpImportData[result.length];
			System.arraycopy(result, 0, selectedData, 0,result.length);
			
			doImport();
		}
		*/
		
		
		super.okPressed();
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
		checkBoxTableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		checkBoxTableViewer.setContentProvider(listContentProvider);
		//
		IObservableList dataModelObserveList = BeanProperties.list("data").observe(model);
		checkBoxTableViewer.setInput(dataModelObserveList);
		//
		return bindingContext;
	}

}
