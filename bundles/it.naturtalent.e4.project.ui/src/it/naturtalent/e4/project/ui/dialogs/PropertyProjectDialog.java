package it.naturtalent.e4.project.ui.dialogs;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.IProjectDataAdapter;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ProjectDataAdapterRegistry;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory;
import it.naturtalent.e4.project.ui.wizards.ProjectWizard;

public class PropertyProjectDialog extends TitleAreaDialog
{
	private IProject iProject;
	private CheckboxTableViewer checkboxTableViewer;
	
	private CheckboxTableViewer propertiesCheckboxTableViewer;
	
	private Table tableAdapter;
	private Text textPath;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private MPart part;
	private Shell parentShell;
	private IProjectDataAdapter[] oldProjectAdapters;
	
	// der benutzte Zwischenspeicher
	private Clipboard clipboard;

	@Inject @Optional protected IEclipseContext context;
	
	// Liste aller bekannten ProjectPropertyFactories
	private List<INtProjectPropertyFactory> ntProjectPropertyFactories;
	
	// Array mit den setting-Factories
	//private String [] settingFactories = null;
	
	//private INtProjectPropertyFactory [] checkedPropertyFactories = null;
	private List<INtProjectPropertyFactory>checkedPropertyFactories = new ArrayList<INtProjectPropertyFactory>();
	
	private Text txtCreated;
	private Table table;

	private class AdapterFilter extends ViewerFilter
	{

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element)
		{
			if (element instanceof IProjectDataAdapter)
			{
				IProjectDataAdapter adapter = (IProjectDataAdapter) element;
				if(adapter.getName().equals(IProjectDataAdapter.DEFAULTPROJECTADAPTERNAME))
					return false;
			}
			return true;
		}

	}

	
	private class TableLabelProvider extends LabelProvider implements
			ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			IProjectDataAdapter adapter = (IProjectDataAdapter) element;
			return adapter.getName();
		}
	}
	
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
	public PropertyProjectDialog(Shell parentShell, IResource iResource, MPart part)
	{
		super(parentShell);
		this.parentShell = parentShell;
		this.iProject = (IProject) iResource;
		this.part = part;
		
		
		// Clipboard aktivieren
		clipboard = new Clipboard(Display.getDefault());
	}
	
	@Persist
	public void setParams(IEclipseContext context)
	{
		this.context = context;
	}
	
	@Override
	public boolean close()
	{
		if (clipboard != null)
		{
			clipboard.dispose();
			clipboard = null;
		}

		return super.close();
	}


	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	@PostConstruct
	protected Control createDialogArea(Composite parent)
	{
		setTitle(Messages.PropertyProjectDialog_this_title); //$NON-NLS-1$
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblAdapter = new Label(container, SWT.NONE);
		lblAdapter.setText(Messages.PropertyProjectDialog_lblAdapter_text);		
		
		// (alt) Adapter Viewer
		checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		tableAdapter = checkboxTableViewer.getTable();
		tableAdapter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));		
		checkboxTableViewer.setLabelProvider(new TableLabelProvider());
		checkboxTableViewer.setContentProvider(new ArrayContentProvider());
		ViewerFilter [] filters = new ViewerFilter[]{new AdapterFilter()};  
		checkboxTableViewer.setFilters(filters);
		
		// (neu) Properties Viewer
		propertiesCheckboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = propertiesCheckboxTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(table);
		propertiesCheckboxTableViewer.setContentProvider(new ArrayContentProvider());
		propertiesCheckboxTableViewer.setLabelProvider(new PropertiesLabelProvider());
		ViewerFilter [] propertyFilters = new ViewerFilter[]{new ViewerFilter()
			{
				@Override
				public boolean select(Viewer viewer, Object parentElement,
						Object element)
				{
					// Default 'NtProjectProperty' ausblenden 
					return (!(element instanceof NtProjectPropertyFactory));
				}
			} };		
		propertiesCheckboxTableViewer.setFilters(propertyFilters);
		
		initPropertiesViewer();
		
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Hyperlink hprlnkPath = formToolkit.createHyperlink(composite, Messages.PropertyProjectDialog_hprlnkPath_text, SWT.NONE); 
		hprlnkPath.addHyperlinkListener(new HyperlinkAdapter()
		{
			public void linkActivated(HyperlinkEvent e)
			{
				try
				{
					//os = System.getProperty("os.name");
					if (SystemUtils.IS_OS_LINUX)
						Runtime.getRuntime().exec("nautilus " + textPath.getText());
					else
						Runtime.getRuntime().exec("explorer " + textPath.getText());

				} catch (Exception exp)
				{
					exp.printStackTrace();
				}
			}
		});
		hprlnkPath.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		formToolkit.paintBordersFor(hprlnkPath);
		
		textPath = new Text(composite, SWT.BORDER);
		GridData gd_textPath = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textPath.widthHint = 429;
		textPath.setLayoutData(gd_textPath);
		textPath.setSize(306, 19);
		textPath.setEditable(false);		
		
		ImageHyperlink mghprlnkClipboard = formToolkit.createImageHyperlink(composite, SWT.NONE);
		mghprlnkClipboard.setToolTipText(Messages.PropertyProjectDialog_mghprlnkClipboard_toolTipText);
		mghprlnkClipboard.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		mghprlnkClipboard.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		formToolkit.paintBordersFor(mghprlnkClipboard);
		mghprlnkClipboard.setText("");
		mghprlnkClipboard.setImage(ResourceManager.getPluginImage("it.naturtalent.e4.project.ui", "icons/copy_edit.gif"));
		mghprlnkClipboard.addHyperlinkListener(new HyperlinkAdapter()
		{
			public void linkActivated(HyperlinkEvent e)
			{
				clipboard.setContents(new Object[]
					{ iProject.getLocation().toOSString() }, new Transfer[]
					{ TextTransfer.getInstance() });
			}
		});
		
		Label lblCreated = formToolkit.createLabel(composite, Messages.PropertyProjectDialog_lblCreated_text, SWT.NONE);
		lblCreated.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		lblCreated.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtCreated = formToolkit.createText(composite, "", SWT.NONE);		
		txtCreated.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		txtCreated.setEditable(false);		
		txtCreated.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		
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
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}
	
	private void init()
	{		
		// alle verfuegbaren Adapter anzeigen
		List<IProjectDataAdapter> alllAdapters = ProjectDataAdapterRegistry
				.getProjectDataAdapters();
		checkboxTableViewer.setInput(alllAdapters);
		

		// die dem Projekt zugeordnetern Adapter 'checken'
		NtProject ntProject = new NtProject(iProject);
		List<IProjectDataAdapter> lProjectAdapters = new ArrayList<IProjectDataAdapter>();
		
		// alle verfuegbaren Adapter probieren
		for (IProjectDataAdapter adapter : alllAdapters)
		{ 
			// real dem Projekt zugeordnete Adapter auflisten
			Object projectData = adapter.load(ntProject.getId());
			if((projectData != null) && (!(projectData instanceof ProjectData)))
				lProjectAdapters.add(adapter);
		}		
		oldProjectAdapters = lProjectAdapters
				.toArray(new IProjectDataAdapter[lProjectAdapters.size()]);	
		
		checkboxTableViewer.setCheckedElements(oldProjectAdapters);

		// Pfad im Dateiensystem
		textPath.setText(iProject.getLocation().toOSString());	
		
		// Erstellungsdatum
		txtCreated.setText(getCreatedDate());
	}
	
	private void initPropertiesViewer()
	{
		// alle verfuegbaren Factories anzeigen
		if (ntProjectPropertyFactories != null)
			propertiesCheckboxTableViewer.setInput(ntProjectPropertyFactories);
		
		/*
		if(checkedPropertyFactories != null)
			propertiesCheckboxTableViewer.setCheckedElements(checkedPropertyFactories);
			*/
		if (checkedPropertyFactories != null)
			propertiesCheckboxTableViewer.setCheckedElements(
					checkedPropertyFactories.toArray(new INtProjectPropertyFactory[checkedPropertyFactories.size()]));

		/*
		String [] settingFactories = null;
		ProjectPropertySettings propertyDataSettings = new ProjectPropertySettings();
		
		// die gesetzten Factories checken		
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
		*/
		
		
	}
	
	private String getCreatedDate()
	{
		String stgDate = iProject.getName().substring(0, iProject.getName().indexOf('-'));
		Date date = new Date(NumberUtils.createLong(stgDate));
		return (DateFormatUtils.format(date, "dd.MM.yyyy")); 
	}

	@Override
	protected void okPressed()
	{
		NtProject ntProject = new NtProject(iProject);
		Object[] result = checkboxTableViewer.getCheckedElements();			
		IProjectDataAdapter [] checkedAdapters = new IProjectDataAdapter[result.length];
		System.arraycopy(result, 0, checkedAdapters, 0,
				result.length);
		
		if(!ArrayUtils.isEquals(checkedAdapters, oldProjectAdapters))
		{
			// alle loeschen, die nicht in 'checkedAdapters' vorhanden sind
			for(IProjectDataAdapter oldAdapter : oldProjectAdapters)
			{
				if(!ArrayUtils.contains(checkedAdapters, oldAdapter))
				{
					if (MessageDialog.openConfirm(parentShell, Messages.PropertyProjectDialog_this_title,
							Messages.bind(Messages.PropertyProjectDialog_deleteProperty, oldAdapter.getName())))
					{
						// Projektdaten via Adapter loeschen
						IProjectData data = Activator.projectDataFactory
								.readProjectData(oldAdapter, ntProject);
						oldAdapter.setProjectData(data);
						oldAdapter.delete();
						
						// den Adapter loeschen
						Activator.projectDataFactory.deleteProjectDataAdapter(iProject, oldAdapter);						
					}
					else
					{
						// den 'alten' Adapter wieder aufnehmen
						checkedAdapters = ArrayUtils.add(checkedAdapters, oldAdapter);
					}
				}
			}
		}
			
			if(checkedAdapters.length > 0)
			{		
				// ProjektWizard erzeugen, die ausgewaehlten Adapter und ResourceNavigator uebergeben 
				ProjectWizard wizard = new ProjectWizard();	
				wizard.setAdapters(checkedAdapters);			
				ContextInjectionFactory.invoke(wizard, PostConstruct.class, context);				 
				Object obj = part.getObject();
				if(obj instanceof IResourceNavigator)				
					wizard.setNavigator((IResourceNavigator) obj);

				// Wizard oeffnen
				WizardDialog dialog = new WizardDialog(parentShell, wizard);
				dialog.open();
			}			
		
		
		//
		// neu
		//
		// CheckedData updaten
		INtProjectPropertyFactory [] checkedFactories = null;
		if((ntProjectPropertyFactories != null) && (!ntProjectPropertyFactories.isEmpty()))
		{
			result = propertiesCheckboxTableViewer.getCheckedElements();
			checkedFactories = new INtProjectPropertyFactory[result.length];
			System.arraycopy(result, 0, checkedFactories, 0, result.length);
			checkedPropertyFactories.clear();
			for(INtProjectPropertyFactory factory : checkedFactories)
				checkedPropertyFactories.add(factory);
		}
		
		/*
		settingFactories = null;
		if(ArrayUtils.isNotEmpty(checkedFactories))
		{			
			for(INtProjectPropertyFactory factory : checkedFactories)
				settingFactories = ArrayUtils.add(settingFactories, factory.getClass().getName());
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
		return new Point(497, 579);
	}
	
	public void setNtProjectPropertyFactories(
			List<INtProjectPropertyFactory> ntProjectPropertyFactories)
	{
		this.ntProjectPropertyFactories = ntProjectPropertyFactories;
	}
	
	public List<INtProjectPropertyFactory> getNtProjectPropertyFactories()
	{
		return ntProjectPropertyFactories;
	}

	public void setContext(IEclipseContext context)
	{
		this.context = context;
	}
	

	public List<INtProjectPropertyFactory> getCheckedPropertyFactories() {
		return checkedPropertyFactories;
	}

	public void setCheckedPropertyFactories(List<INtProjectPropertyFactory> checkedPropertyFactories) {
		this.checkedPropertyFactories = checkedPropertyFactories;
	}

	/*
	public void setCheckedPropertyFactories(
			INtProjectPropertyFactory[] checkedPropertyFactories)
	{
		this.checkedPropertyFactories = checkedPropertyFactories;
	}

	public INtProjectPropertyFactory[] getCheckedPropertyFactories()
	{
		return checkedPropertyFactories;
	}
	*/
	
	
	
	
	
}
