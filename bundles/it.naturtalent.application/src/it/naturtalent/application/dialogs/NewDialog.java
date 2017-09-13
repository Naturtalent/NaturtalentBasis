package it.naturtalent.application.dialogs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import it.naturtalent.application.Messages;
import it.naturtalent.application.services.INewActionAdapter;
import it.naturtalent.application.services.INewActionAdapterRepository;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;


/**
 * Mit diesem Dialog kann ausgewählt werden, welches Objekt neu erzeugt werden soll. Die zur Auswahl angebotenen
 * Aktionen werden ueber entsprechende Adapter, die wiederum im zentralen Repository gespeichert sind, gesteuert werden.
 * Der globale Zugriff auf dieses Repository erfolgt ueber einen OSGI-Service.
 * 
 * @author dieter
 *
 */
public class NewDialog extends TitleAreaDialog
{
	
	private static final String GENERAL_CATEGORY_LABEL = "General";
	
	private INewActionAdapter selectedAdapter;
	
	private TreeViewer treeViewer;
	
	private Button button;
	
	/**
	 * Hilfskonstruktion zur Erstellung einer Hierachie
	 * 
	 * @author dieter
	 *
	 */
	private static class NewWizardAdapterTreeNode
	{
		String name;
		List<INewActionAdapter>adapters;
		List<NewWizardAdapterTreeNode> childs;
	}

	/**
	 * Labelprovider
	 * 
	 * @author dieter
	 *
	 */
	private static class ViewerLabelProvider extends LabelProvider
	{
		public Image getImage(Object element)
		{	
			if(element instanceof INewActionAdapter)
				return ((INewActionAdapter)element).getImage();

			
			
			/*
			if(element instanceof NewWizardAdapterTreeNode)
				return Icon.MENU_NEW_FOLDER.getImage(IconSize._16x16_DefaultIconSize);
			
			if(element instanceof INewActionAdapter)
				return Icon.MENU_NEW_PROJECT.getImage(IconSize._16x16_DefaultIconSize);
				*/
			
			return null;
		}

		public String getText(Object element)
		{
			if(element instanceof NewWizardAdapterTreeNode)
			{
				NewWizardAdapterTreeNode node = (NewWizardAdapterTreeNode) element;
				return node.name;
			}

			if(element instanceof INewActionAdapter)
			{
				INewActionAdapter adapter = (INewActionAdapter) element;
				return adapter.getLabel();
			}

			return "";
		}
	}
	
	/**
	 * Contentprovider
	 * 
	 * @author dieter
	 *
	 */
	private static class TreeContentProvider implements ITreeContentProvider
	{
		private NewWizardAdapterTreeNode rootNode;		
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			rootNode = null;
			if (newInput instanceof List)
			{
				List<INewActionAdapter>adapters = ewActionAdaptersRepository.getNewWizardAdapters();
				//List<INewWizardAdapter>adapters = getMock();
				createHierachy(adapters);
			}
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object inputElement)
		{
			if (rootNode != null)
			{
				if (inputElement instanceof List)
				{					
					return rootNode.childs
							.toArray(new NewWizardAdapterTreeNode[rootNode.childs.size()]);
				}
			}
			
			return new Object[0];
		}

		public Object[] getChildren(Object parentElement)
		{
			if (parentElement instanceof NewWizardAdapterTreeNode)
			{
				NewWizardAdapterTreeNode node = (NewWizardAdapterTreeNode) parentElement;
				
				List<Object>objects = new ArrayList<Object>();
				if ((node.childs != null) && (!node.childs.isEmpty()))
				{
					for(NewWizardAdapterTreeNode objNode : node.childs)
						objects.add(objNode);
				}
				
				if ((node.adapters != null) && (!node.adapters.isEmpty()))
				{
					for(INewActionAdapter objAdapter : node.adapters)
						objects.add(objAdapter);
				}
				
				return objects
						.toArray(new Object[objects.size()]);
			}

			return new Object[0];
		}

		public Object getParent(Object element)
		{
			return null;
		}

		public boolean hasChildren(Object element)
		{
			return getChildren(element).length > 0;			
		}
		
		/*
		 * die Hierachie erstellen
		 */
		private void createHierachy(List<INewActionAdapter>adapters)
		{
			// Root anlegen
			rootNode = new NewWizardAdapterTreeNode();
			rootNode.name = "root"; //$NON-NLS-N$
			rootNode.adapters = new ArrayList<INewActionAdapter>();
			rootNode.childs = new ArrayList<NewWizardAdapterTreeNode>();
					
		
			for(INewActionAdapter adapter : adapters)
			{
				//Action action = adapter.getAction();
				//adapter.setAction(ContextInjectionFactory.make(action.getClass(), context));				
				createAdapterTreeLevel(rootNode,adapter, adapter.getCategory());
			}
		
		}
		
		/*
		 * Rekursives Erstellen eine Hierachiestufe
		 */
		private void createAdapterTreeLevel(NewWizardAdapterTreeNode parentNode,
				INewActionAdapter adapter, String categoryPath)
		{
			
			categoryPath = (categoryPath == null) ? GENERAL_CATEGORY_LABEL : categoryPath;
			
			String [] splitKey = StringUtils.split(categoryPath, '/');
			if(ArrayUtils.isNotEmpty(splitKey))
			{
				String name = splitKey[0];
				splitKey[0] = "";
				categoryPath = StringUtils.join(splitKey,'/');
				
				NewWizardAdapterTreeNode child = getChild(parentNode, name);
				if(child == null)
				{
					child = new NewWizardAdapterTreeNode();
					child.name = name;
					child.adapters = new ArrayList<INewActionAdapter>();
					child.childs = new ArrayList<NewWizardAdapterTreeNode>();
					parentNode.childs.add(child);				
				}
					
				// mit dem Kind geht es weiter im Hierachielevel
				createAdapterTreeLevel(child, adapter, categoryPath);		
			}
			else
			{
				// Adapter im letzten Knoten der Hierachielevel eintragen
				parentNode.adapters.add(adapter);
			}
		}
		
		/*
		 * Hilfsfunktion bei der Hierachieerstellung
		 * 
		 */
		private NewWizardAdapterTreeNode getChild(NewWizardAdapterTreeNode parent, String name)
		{
			List<NewWizardAdapterTreeNode> childs = parent.childs;
			if ((childs != null) && (!childs.isEmpty()))
			{
				for (NewWizardAdapterTreeNode child : childs)
				{
					if (StringUtils.equals(name, child.name))
						return child;
				}
			}
			return null;
		}
	}

	@Inject
	@Optional
	static INewActionAdapterRepository ewActionAdaptersRepository;

	@Inject
	@Optional
	private static IEclipseContext context;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	@Inject
	public NewDialog(Shell parentShell)
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
		setTitle(Messages.NewWizardDialog_this_title);
		setTitleImage(Icon.WIZBAN_NEW.getImage(IconSize._75x66_TitleDialogIconSize)); //$NON-NLS-N$
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		treeViewer = new TreeViewer(container, SWT.BORDER);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				setMessage("");
				button.setEnabled(false);
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selObj = selection.getFirstElement();
				if (selObj instanceof INewActionAdapter)
				{
					INewActionAdapter adapter = (INewActionAdapter) selObj;
					String message = adapter.getMessage();
					setMessage(StringUtils.isEmpty(message) ? "" : message);
					button.setEnabled(true);
				}
			}
		});
		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{		
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object selObj = selection.getFirstElement();
				if (selObj instanceof NewWizardAdapterTreeNode)
				{
					TreePath tp = ((TreeSelection) treeViewer.getSelection())
							.getPaths()[0];
					if (treeViewer.getExpandedState(tp))
						treeViewer.collapseToLevel(tp, 1);
					else
						treeViewer.expandToLevel(tp, 1);
				}
				else
				{
					if (selObj instanceof INewActionAdapter)
					{
						
						okPressed();
					}
				}				
			}
		});
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeViewer.setLabelProvider(new ViewerLabelProvider());
		treeViewer.setContentProvider(new TreeContentProvider());
		
		if (ewActionAdaptersRepository != null)
		{
			List<INewActionAdapter> wizardAdapters = ewActionAdaptersRepository
					.getNewWizardAdapters();

			treeViewer.setInput(wizardAdapters);
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
		button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		button.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize()
	{
		return new Point(450, 458);
	}


	@Override
	protected void okPressed()
	{
		selectedAdapter = null;
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		Object selObj = selection.getFirstElement();
		if (selObj instanceof INewActionAdapter)
			selectedAdapter = (INewActionAdapter) selObj;
		
		super.okPressed();
	}


	public INewActionAdapter getSelectedAdapter()
	{
		return selectedAdapter;
	}
	
	

}
