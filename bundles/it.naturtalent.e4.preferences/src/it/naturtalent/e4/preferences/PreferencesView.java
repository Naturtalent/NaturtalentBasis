package it.naturtalent.e4.preferences;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import it.naturtalent.application.IPreferenceAdapter;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.DoubleClickEvent;

public class PreferencesView
{
	private static final String GENERAL_WIZARD_LABEL = "Allgemein";
	private IPreferenceRegistry preferenceRegistry;
	private TreeViewer treeViewer;
	private PreferenceNodeComposite preferenceNodeComposite;
	private IPreferenceAdapter preferenceAdapter;
		
	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent)
	{
		parent.setLayout(new GridLayout(1, false));
		SashForm sashForm = new SashForm(parent, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		treeViewer = new TreeViewer(sashForm, SWT.BORDER);
		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection(); 
				Object selectedNode = selection.getFirstElement(); 
								
				boolean exp = treeViewer.getExpandedState(selectedNode);
				if(exp)
					treeViewer.collapseToLevel(selectedNode, TreeViewer.ALL_LEVELS);				
				else
					treeViewer.expandToLevel(selectedNode, TreeViewer.ALL_LEVELS);
			}
		});
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				Object selObj = selection.getFirstElement();
				if(selObj instanceof IPreferenceAdapter)
				{
					// das individuelle Detail wird aktiviert
					preferenceAdapter = (IPreferenceAdapter) selObj;					
					preferenceNodeComposite.getNodeComposite().dispose(); // die aktuelle Seite wird ungueltig
					preferenceNodeComposite.setNodeComposite(preferenceAdapter.createNodeComposite(preferenceNodeComposite));
					preferenceNodeComposite.refresh();
					preferenceNodeComposite.setPreferenceAdapter(preferenceAdapter);
				}
				else
				{
					PreferenceAdapterTreeNode tree = (PreferenceAdapterTreeNode)selObj;
					IPreferenceAdapter check = tree.adapters.get(0);
					
					preferenceNodeComposite.setTitle("");
					preferenceNodeComposite.getNodeComposite().dispose();
					preferenceNodeComposite.setPreferenceAdapter(null);
				}
				
			}
		});
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new ViewerLabelProvider());
		Tree tree = treeViewer.getTree();
		
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		preferenceNodeComposite = new PreferenceNodeComposite(composite, SWT.NONE);
		preferenceNodeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sashForm.setWeights(new int[] {230, 514});		
	}
	

	
	public IPreferenceAdapter getPreferenceAdapter()
	{
		return preferenceAdapter;
	}



	public void setPreferenceRegistry(IPreferenceRegistry preferenceRegistry)
	{
		this.preferenceRegistry = preferenceRegistry;
		if(preferenceRegistry != null)
		{						
			List<IPreferenceAdapter>adapters = preferenceRegistry.getPreferenceAdapters();
			treeViewer.setInput(adapters);
		}
	}

	@PreDestroy
	public void dispose()
	{
	}

	@Focus
	public void setFocus()
	{
		// TODO	Set the focus to control
	}
	
	/**
	 * Hilfskonstruktion zur Erstellung einer Hierachie
	 * 
	 * @author dieter
	 *
	 */
	private static class PreferenceAdapterTreeNode
	{
		String name;
		List<IPreferenceAdapter>adapters;
		List<PreferenceAdapterTreeNode> childs;
	}
	
	/**
	 * Contentprovider
	 * 
	 * @author dieter
	 *
	 */
	private class TreeContentProvider implements ITreeContentProvider
	{
		private PreferenceAdapterTreeNode rootNode;		
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			rootNode = null;
			if (newInput instanceof List)
			{
				List<IPreferenceAdapter>adapters = preferenceRegistry.getPreferenceAdapters();				
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
							.toArray(new PreferenceAdapterTreeNode[rootNode.childs.size()]);
				}
			}
			
			return new Object[0];
		}

		public Object[] getChildren(Object parentElement)
		{
			if (parentElement instanceof PreferenceAdapterTreeNode)
			{
				PreferenceAdapterTreeNode node = (PreferenceAdapterTreeNode) parentElement;
				
				List<Object>objects = new ArrayList<Object>();
				if ((node.childs != null) && (!node.childs.isEmpty()))
				{
					for(PreferenceAdapterTreeNode objNode : node.childs)
						objects.add(objNode);
				}
				
				if ((node.adapters != null) && (!node.adapters.isEmpty()))
				{
					for(IPreferenceAdapter objAdapter : node.adapters)
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
		private void createHierachy(List<IPreferenceAdapter>adapters)
		{
			// Root anlegen
			rootNode = new PreferenceAdapterTreeNode();
			rootNode.name = "root"; //$NON-NLS-N$
			rootNode.adapters = new ArrayList<IPreferenceAdapter>();
			rootNode.childs = new ArrayList<PreferenceAdapterTreeNode>();
			
					
			for(IPreferenceAdapter adapter : adapters)
				createAdapterTreeLevel(rootNode,adapter, adapter.getNodePath());							
		}
		
		/*
		 * Rekursives Erstellen eine Hierachiestufe
		 */
		private void createAdapterTreeLevel(PreferenceAdapterTreeNode parentNode,
				IPreferenceAdapter adapter, String categoryPath)
		{
			
			categoryPath = (categoryPath == null) ? GENERAL_WIZARD_LABEL : categoryPath;
			
			String [] splitKey = StringUtils.split(categoryPath, '/');
			if(ArrayUtils.isNotEmpty(splitKey))
			{
				String name = splitKey[0];
				splitKey[0] = "";
				categoryPath = StringUtils.join(splitKey,'/');
				
				PreferenceAdapterTreeNode child = getChild(parentNode, name);
				if(child == null)
				{
					child = new PreferenceAdapterTreeNode();
					child.name = name;
					child.adapters = new ArrayList<IPreferenceAdapter>();
					child.childs = new ArrayList<PreferenceAdapterTreeNode>();
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
		private PreferenceAdapterTreeNode getChild(PreferenceAdapterTreeNode parent, String name)
		{
			List<PreferenceAdapterTreeNode> childs = parent.childs;
			if ((childs != null) && (!childs.isEmpty()))
			{
				for (PreferenceAdapterTreeNode child : childs)
				{
					if (StringUtils.equals(name, child.name))
						return child;
				}
			}
			return null;
		}
	}
	
	
	
	public PreferenceNodeComposite getPreferenceNodeComposite()
	{
		return preferenceNodeComposite;
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
			return null;
		}

		public String getText(Object element)
		{
			if(element instanceof PreferenceAdapterTreeNode)
			{
				PreferenceAdapterTreeNode node = (PreferenceAdapterTreeNode) element;
				return node.name;
			}
			
			if(element instanceof IPreferenceAdapter)
			{
				IPreferenceAdapter adapter = (IPreferenceAdapter) element;
				return adapter.getLabel();
			}

			return "";
		}
	}

}
