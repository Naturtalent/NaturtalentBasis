package it.naturtalent.e4.project.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory;

/**
 * @author Dieter.Apel
 *
 * Dialog zum Definieren der ProjektProperties
 * 
 */
public class ProjectPropertyDialog extends TitleAreaDialog
{
	
	private CheckboxTableViewer propertiesCheckboxTableViewer;
	private Table table;

	// Liste aller bekannten ProjectPropertyFactories
	private List<INtProjectPropertyFactory> ntProjectPropertyFactories;	
	
	// Liste der im Dialog gecheckten ProjectPropertyFactories
	private List<INtProjectPropertyFactory>checkedPropertyFactories = new ArrayList<INtProjectPropertyFactory>();
	
	// obligatorische PropertyFactory
	private INtProjectPropertyFactory obligatePropertyFactory;
	
	private INtProjectPropertyFactory [] checkedFactories;
	private String[] settingFactories;
	
	private class PropertiesLabelProvider extends LabelProvider
			implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			INtProjectPropertyFactory adapter = (INtProjectPropertyFactory) element;
			return adapter.getLabel();
		}
	}
	
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ProjectPropertyDialog(Shell parentShell)
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
		setMessage("Projekteigenschaften definieren");
		setTitle("Projekteigenschaften");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		propertiesCheckboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = propertiesCheckboxTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		propertiesCheckboxTableViewer.setContentProvider(new ArrayContentProvider());
		propertiesCheckboxTableViewer.setLabelProvider(new PropertiesLabelProvider());
		initPropertiesViewer();

		return area;
	}

	private void initPropertiesViewer()
	{
		// tableViewer zeigt alle verfuegbaren PropertyFactories
		if (ntProjectPropertyFactories != null)
			propertiesCheckboxTableViewer.setInput(ntProjectPropertyFactories);
		
		// die momentan zugeordneten Factories voreingestellt checken		
		if(settingFactories != null)
		{
			for(String settingFactory : settingFactories)
			{
				for(INtProjectPropertyFactory ntProjectPropertyFactory : ntProjectPropertyFactories)
				{
					if(StringUtils.equals(ntProjectPropertyFactory.getClass().getName(), settingFactory))
					{
						propertiesCheckboxTableViewer.setChecked(ntProjectPropertyFactory, true);
						break;
					}
				}				
			}			
		}

		// obligatorisches Property 'NtProjectPropertyFactory' der Auswahl entziehen
		for(INtProjectPropertyFactory ntProjectPropertyFactory : ntProjectPropertyFactories)
		{			
			if(ntProjectPropertyFactory instanceof NtProjectPropertyFactory)
			{
				propertiesCheckboxTableViewer.testFindItem(ntProjectPropertyFactories.get(0)).dispose();
				obligatePropertyFactory = ntProjectPropertyFactory; 
				break;
			}
		}
		
	}
	
	@Override
	protected void okPressed()
	{
		// die gecheckte Factories aktualisieren
		Object[] result = propertiesCheckboxTableViewer.getCheckedElements();		
		checkedFactories = new INtProjectPropertyFactory[result.length];
		System.arraycopy(result, 0, checkedFactories, 0, result.length);
		
		// die Namen der gecheckten Factories im Array 'settingFactories' sammeln
		settingFactories = null;
		if(ArrayUtils.isNotEmpty(checkedFactories))
		{			
			for(INtProjectPropertyFactory factory : checkedFactories)
				checkedPropertyFactories.add(factory);
		}
		
		checkedPropertyFactories.add(obligatePropertyFactory);
		super.okPressed();
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 454);
	}

	/**
	 * Alle im Viewer anzuzeigenten Factories definieren.
	 * Muss vor create() definiert werden.
	 * @param ntProjectPropertyFactories
	 */
	public void setNtProjectPropertyFactories(
			List<INtProjectPropertyFactory> ntProjectPropertyFactories)
	{
		this.ntProjectPropertyFactories = ntProjectPropertyFactories;
	}
	
	/**
	 * Rueckgabe der mit dem Dialog gecheckten Factories.
	 * Wird von okPressed() aktualisiert.
	 * @return 
	 */
	/*
	public INtProjectPropertyFactory[] getCheckedFactories()
	{
		return checkedFactories;
	}
	*/
	
	public List<INtProjectPropertyFactory> getCheckedPropertyFactories()
	{
		return checkedPropertyFactories;
	}


	/**
	 * Die im Array uebergebenen Factories(namen) werden im Viewer als gecheckt 
	 * dargestellt. Muss vor create() definiert werden.
	 * @param settingFactories
	 */
	public void setSettingFactories(String[] settingFactories)
	{
		this.settingFactories = settingFactories;
	}
		
	/**
	 * Rueckgabe der Namen der gecheckten Factories.
	 * Wird von okPressed() aktualisiert.
	 * @return 
	 */
	public String[] getSettingFactories()
	{
		return settingFactories;
	}
}
