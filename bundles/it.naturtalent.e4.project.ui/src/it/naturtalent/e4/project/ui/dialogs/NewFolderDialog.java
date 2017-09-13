package it.naturtalent.e4.project.ui.dialogs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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

import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.filters.ClosedProjectFilter;
import it.naturtalent.e4.project.ui.filters.HiddenResourceFilter;
import it.naturtalent.e4.project.ui.filters.ResourceFilterProvider;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.navigator.WorkbenchLabelProvider;
import it.naturtalent.e4.project.ui.utils.CreateNewFolder;

public class NewFolderDialog extends TitleAreaDialog implements PropertyChangeListener
{
	private Text textContainer;
	private Text textFolderName;
	private Button okButton;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Parent composite of the advanced widget group for creating 
	 * linked resources.
	 */
	private Composite linkedResourceParent;
	
	private ControlDecoration folderControlDecoration;
	
	/**
	 * Linked resources widget group. Null if advanced section is not visible.
	 */
	private LinkedResourceComposite linkedResourceComposite;
		
	private Object selectionObject;	
	private IResource selectedResource = null;
	
	//private IContainer iContainer;
	
	private IResourceNavigator navigator;
	
	
	/**
	 * Height of the dialog without the "advanced" linked resource group. 
	 * Set when the advanced group is first made visible. 
	 */
	private int basicShellHeight = -1;
	private Button advancedButton;

	private String linkTarget = ""; //$NON-NLS-N$
	private Tree tree;
	private TreeViewer treeViewer;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	@Inject
	public NewFolderDialog(Shell parentShell, ESelectionService selectionService, MPart part)
	{
		super(parentShell);
		if(selectionService != null)
			selectionObject = selectionService.getSelection();
		
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
		setMessage(Messages.NewFolderDialog_this_message);
		setTitle(Messages.NewFolderDialog_this_title);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblContainer = new Label(container, SWT.NONE);
		lblContainer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblContainer.setText(Messages.NewFolderDialog_lblContainer);
		
		textContainer = new Text(container, SWT.BORDER);
		textContainer.setEnabled(false);
		textContainer.setEditable(false);
		textContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		treeViewer = new TreeViewer(container, SWT.BORDER);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{				
				selectedResource = (IResource) ((IStructuredSelection)event.getSelection()).getFirstElement();				
				if (selectedResource != null)
				{
					IProject iProject = selectedResource.getProject();
					String parent = new NtProject(iProject).getName();
					if (selectedResource.getType() != IResource.PROJECT)
						parent = parent + File.separator
								+ selectedResource.getRawLocation().toString();
					textContainer.setText(parent);
				}
			}
		});
		tree = treeViewer.getTree();
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
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
		lblFolderName.setText(Messages.NewFolderDialog_lblFolder);
		
		textFolderName = new Text(container, SWT.BORDER);
		textFolderName.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				updateState();
			}
		});
		textFolderName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		folderControlDecoration = new ControlDecoration(textFolderName, SWT.LEFT | SWT.TOP);
		folderControlDecoration.setImage(SWTResourceManager.getImage(NewFolderDialog.class, "/org/eclipse/jface/fieldassist/images/error_ovr.gif"));		
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

		if(selectionObject != null)
			treeViewer.setSelection(new StructuredSelection(selectionObject));
		
		updateState();
		
		return area;
	}
		
	private void updateState()
	{
		if(okButton != null)
			okButton.setEnabled(false);
		
		folderControlDecoration.hide();
			
		String folderName = textFolderName.getText();
		if(StringUtils.isEmpty(folderName))
		{
			folderControlDecoration.show();
			folderControlDecoration.setDescriptionText(Messages.NewFolderDialog_folderControlDecoration_noNameDescriptionText);
			return;
		}
		
		IContainer container = (IContainer) selectedResource;
		IPath path = new Path(container.getProjectRelativePath()+File.separator+folderName);
		IFolder folder = container.getFolder(path);
		if((folder != null) && (folder.exists()))
		{
			folderControlDecoration.show();
			folderControlDecoration.setDescriptionText(Messages.NewFolderDialog_folderControlDecoration_folderexistdescription);
			return;			
		}
		
		if(okButton != null)
			okButton.setEnabled(true);		
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
				textFolderName.setText(FilenameUtils.getBaseName(linkTarget));
		}
	}

	@Override
	protected void okPressed()
	{		
		if (selectedResource instanceof IContainer)
		{
			try
			{
				URI uriLink = null;
				IPath path = Path.fromOSString(linkTarget);
				if (path != null && path.toFile().exists())
					uriLink = URIUtil.toURI(path);

				CreateNewFolder.createFolder(getShell(),
						(IContainer) selectedResource,
						textFolderName.getText(), uriLink);

				// neuen Folder im Navigator selektieren
				IContainer container = (IContainer) selectedResource;
				Path newPath = new Path(textFolderName.getText());
				IResource newResource = container.findMember(newPath);
				if(newResource == null)
					return;
				
				navigator.getViewer().setSelection(new StructuredSelection(newResource));

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


}
