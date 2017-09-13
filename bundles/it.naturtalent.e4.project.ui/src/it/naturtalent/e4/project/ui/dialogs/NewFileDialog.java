package it.naturtalent.e4.project.ui.dialogs;

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.NtPreferences;
import it.naturtalent.e4.project.ui.filters.ClosedProjectFilter;
import it.naturtalent.e4.project.ui.filters.HiddenResourceFilter;
import it.naturtalent.e4.project.ui.filters.ResourceFilterProvider;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.navigator.WorkbenchLabelProvider;
import it.naturtalent.e4.project.ui.utils.CreateNewFile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wb.swt.SWTResourceManager;

public class NewFileDialog extends TitleAreaDialog implements PropertyChangeListener
{
	private Text textContainer;
	private Text textFileName;
	private Button okButton;
	
	private Map<String,String>templateMap;
	private String templateKey;
	
	/**
	 * Parent composite of the advanced widget group for creating 
	 * linked resources.
	 */
	private Composite linkedResourceParent;
	
	private ControlDecoration fileControlDecoration;
	
	/**
	 * Linked resources widget group. Null if advanced section is not visible.
	 */
	private LinkedResourceComposite linkedResourceComposite;
	
	// ResourceNavigator und die dort selektierte Resource
	private IResourceNavigator navigator;
	private Object selectedResource;	
	
	// das ausgewaehlte Zielverzeichnis
	private IResource selectedDestDir = null;
	
	/**
	 * Height of the dialog without the "advanced" linked resource group. 
	 * Set when the advanced group is first made visible. 
	 */
	private int basicShellHeight = -1;
	private Button advancedButton;

	private String linkTarget = ""; //$NON-NLS-N$
	private Tree tree;
	private TreeViewer treeViewer;
	private Button btnTemplate;
	
	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	@Inject
	public NewFileDialog(Shell parentShell, ESelectionService selectionService, MPart part)
	{
		super(parentShell);
		if(selectionService != null)
			selectedResource = selectionService.getSelection();
		
		Object obj = part.getObject();
		if (obj instanceof IResourceNavigator)	
			this.navigator = (IResourceNavigator) obj;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		setMessage(Messages.NewFileDialog_this_message);
		setTitle(Messages.NewFileDialog_this_title);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblContainer = new Label(container, SWT.NONE);
		lblContainer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblContainer.setText(Messages.NewFolderDialog_lblContainer);
		
		textContainer = new Text(container, SWT.BORDER);
		textContainer.setEnabled(false);
		textContainer.setEditable(false);
		textContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		treeViewer = new TreeViewer(container, SWT.BORDER);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{				
				selectedDestDir = (IResource) ((IStructuredSelection)event.getSelection()).getFirstElement();
				
				IProject iProject = selectedDestDir.getProject();
				String parent = new NtProject(iProject).getName();
				if(selectedDestDir.getType() != IResource.PROJECT)
					parent = parent+File.separator+selectedDestDir.getRawLocation().toString();				
				textContainer.setText(parent);
			}
		});
		tree = treeViewer.getTree();
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd_tree.heightHint = 500;
		tree.setLayoutData(gd_tree);
		
		treeViewer.setContentProvider(new WorkbenchContentProvider());
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());		
		ResourceFilterProvider filterProvider = new ResourceFilterProvider();
		filterProvider.addFilter(new ClosedProjectFilter());
		filterProvider.addFilter(new HiddenResourceFilter());
		filterProvider.addFilter(new ViewerFilter()
		{			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element)
			{
				if (element instanceof IResource)
				{
					IResource resource = (IResource) element;
					if((resource.getType() & (IResource.PROJECT | IResource.FOLDER)) != 0)
						return true;			
				}
				return false;
			}
		});
		treeViewer.setFilters(filterProvider.getFilters());
		
		if(navigator != null)
			treeViewer.setInput(navigator.getAggregateWorkingSet());
		
		
		Label lblFolderName = new Label(container, SWT.NONE);
		lblFolderName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFolderName.setText(Messages.NewFileDialog_lblFile);
		
		textFileName = new Text(container, SWT.BORDER);
		textFileName.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				updateState();
			}
		});
		textFileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		fileControlDecoration = new ControlDecoration(textFileName, SWT.LEFT | SWT.TOP);
		fileControlDecoration.setImage(SWTResourceManager.getImage(NewFileDialog.class, "/org/eclipse/jface/fieldassist/images/error_ovr.gif"));		
		
		btnTemplate = new Button(container, SWT.NONE);
		btnTemplate.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				NewFileTemplateDialog dialog = new NewFileTemplateDialog(getShell());
				dialog.create();
				dialog.setTemplates(templateMap.keySet());
				if(dialog.open() == NewFileTemplateDialog.OK)
				{
					templateKey = dialog.getSelectedTemplate();
					textFileName.setText(templateKey);
					textFileName.setSelection(0, templateKey.length());
				}
			}
		});
		btnTemplate.setText(Messages.NewFileDialog_btnTemplate_text);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
				
		linkedResourceParent = new Composite(container, SWT.NONE);
		linkedResourceParent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		linkedResourceParent.setLayout(new GridLayout(1, false));
		
		advancedButton = new Button(linkedResourceParent, SWT.NONE);
		advancedButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				handleAdvancedButtonSelect();
			}
		});
		advancedButton.setText(Messages.showAdvanced);
		new Label(container, SWT.NONE);

		if(selectedResource != null)
			treeViewer.setSelection(new StructuredSelection(selectedResource));
		
		updateState();
		
		return area;
	}
	
	@PostConstruct
	public void postConstruct(@Preference(value = NtPreferences.FILE_TEMPLATE_PREFERENCE, nodePath = NtPreferences.ROOT_PREFERENCES_NODE)
	String templates)
	{		
		templateMap = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(templates))
		{
			String[] templateArray = StringUtils.split(templates, ",");
			for (int i = 0; (i + 1) < templateArray.length; i++)
				templateMap.put(templateArray[i], templateArray[++i]);

			if (!templateMap.isEmpty())
				templateKey = templateMap.keySet().iterator().next();
		}
	}

		
	private void updateState()
	{
		if(okButton != null)
			okButton.setEnabled(true);		
		fileControlDecoration.hide();
		
		IPath path = Path.fromOSString(linkTarget);
		if (path != null && path.toFile().exists())
			return;

		if(StringUtils.isNotEmpty(textFileName.getText()))
			return;
		
		fileControlDecoration.show();
		if(okButton != null)
			okButton.setEnabled(false);		
	}

	/**
	 * Shows/hides the advanced option widgets. 
	 */
	protected void handleAdvancedButtonSelect()
	{
		Shell shell = getShell();
		Point shellSize = shell.getSize();
		Composite composite = (Composite) getDialogArea();

		if (linkedResourceComposite != null)
		{			
			linkedResourceComposite.removePropertyChangeListener(this);
			linkedResourceComposite.dispose();
			linkedResourceComposite = null;			
			composite.layout();
			shell.setSize(shellSize.x, basicShellHeight);
			advancedButton.setText(Messages.showAdvanced);
		}
		else
		{		
			
			if (basicShellHeight == -1)
			{
				basicShellHeight = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT,
						true).y;
			}
						
			linkedResourceComposite = new LinkedResourceComposite(linkedResourceParent, SWT.NONE);
			linkedResourceComposite.addPropertyChangeListener(this);
			linkedResourceComposite.setLinkTarget(linkTarget);
			linkedResourceComposite.setType(IResource.FILE);
			
			shellSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			shell.setSize(shellSize);
			composite.layout();
			advancedButton.setText(Messages.hideAdvanced);
		}
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
		
		updateState();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(672, 651);
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0)
	{
		if(arg0.getPropertyName().equals(LinkedResourceComposite.LINKTARGETPROPERTY))
		{
			linkTarget = (String) arg0.getNewValue();		
			
			if(linkTarget != null)
				textFileName.setText(FilenameUtils.getBaseName(linkTarget));
		}
	}

	@Override
	protected void okPressed()
	{		
		if (selectedDestDir instanceof IContainer)
		{
			String filename;
			
			try
			{
				// wurde ein 'link' ausgewaehlt 
				URI uriLink = null;
				IPath path = Path.fromOSString(linkTarget);
				if (path != null && path.toFile().exists())
				{
					// Link wird erzeugt
					filename = FilenameUtils.removeExtension(textFileName.getText());					
					uriLink = URIUtil.toURI(path);
					CreateNewFile.createFile(getShell(),
							(IContainer) selectedDestDir,
							filename,null, uriLink);
				}
				else
				{
					filename = textFileName.getText();
					if(StringUtils.isEmpty(templateKey))
					{
						filename = FilenameUtils.removeExtension(filename)+ ".txt";
						filename = getAutoFileName((IContainer) selectedDestDir, filename);
						CreateNewFile.createFile(getShell(),
								(IContainer) selectedDestDir, filename, null, null);						
					}
					
					else
					{
						String templateValue = templateMap.get(templateKey);
						if(templateValue.startsWith("."))
						{
							filename = FilenameUtils.removeExtension(filename)+ templateValue;
							CreateNewFile.createFile(getShell(),
									(IContainer) selectedDestDir, filename, null, null);													
						}
						else
						{
							URL url = new URL(templateValue);
							URLConnection con = url.openConnection();
							InputStream in = con.getInputStream();

							String ext = FilenameUtils.getExtension(url.getFile());
							filename = FilenameUtils.removeExtension(filename) + "." + ext;
							filename = getAutoFileName((IContainer) selectedDestDir, filename);
							CreateNewFile.createFile(getShell(),
									(IContainer) selectedDestDir, filename, in,
									null);
						}
					}
							
				}
			} catch (Exception e)
			{
				log.error(e);
			}
		}
		super.okPressed();
	}
	
	/**
	 * Returns the link target location entered by the user.
	 * 
	 * @return the link target location entered by the user. null if the user
	 *         chose not to create a link.
	 */
	public URI getLinkTargetURI(String linkTarget) 
	{
		// linkTarget can contain either:
		//  1) a URI, ex: 								foo://bar/file.txt
		//  2) A path, ex: 								c:\foo\bar\file.txt
		//  3) A path variable relative path, ex:		VAR\foo\bar\file.txt
		URI uri = null;
		try {
			IPath path = Path.fromOSString(linkTarget);
			if (path != null && path.toFile().exists())
				return URIUtil.toURI(path);
			
			uri = new URI(linkTarget);
		}catch(URISyntaxException e) {
			//uri = convertToURI(linkTarget);
		}
		return uri;
	}

	private String getAutoFileName(IContainer dir, String originalFileName)
	{
		String autoFileName;

		if (dir == null)
			return ""; //$NON-NLS-1$

		int counter = 1;
		while (true)
		{
			if (counter > 1)
			{
				autoFileName = FilenameUtils.getBaseName(originalFileName)
						+ new Integer(counter) + "." //$NON-NLS-1$
						+ FilenameUtils.getExtension(originalFileName);
			}
			else
			{
				autoFileName = originalFileName;
			}

			IResource res = dir.findMember(autoFileName);
			if (res == null)
				return autoFileName;

			counter++;
		}
	}


}
