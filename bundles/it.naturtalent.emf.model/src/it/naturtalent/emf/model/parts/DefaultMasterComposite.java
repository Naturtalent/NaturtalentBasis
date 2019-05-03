package it.naturtalent.emf.model.parts;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.util.ECPUtil;
import org.eclipse.emf.ecp.core.util.observer.ECPProjectContentTouchedObserver;
import org.eclipse.emf.ecp.internal.core.ECPProjectImpl;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import it.naturtalent.emf.model.Activator;
import it.naturtalent.emf.model.ModelEventKey;
import it.naturtalent.emf.model.actions.DefaultModelAction;
import it.naturtalent.emf.model.actions.DefaultModelActionDistributor;

/**
 * Composite zur Darstellung der ECP-Projektelemente.
 * 
 * Implementiert eine Ueberwachung (Observer) der Projektaenderungen.
 * @author A682055
 *
 */
public class DefaultMasterComposite extends Composite  implements ECPProjectContentTouchedObserver
{
	private DefaultModelActionDistributor actionDistributor;
	
	private EObject selectedEObject;
	
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	
	private DefaultMasterDetailsModelView masterDetailsView;
	
	private IDoubleClickListener defaultDoubleClickListener;
	
	// EventKeys
	//public static final String MASTER_VIEWEVENT = "masterEvent/"; //$NON-NLS-N$	
	//private Map<String,String> masterEventKeys;
	
	private Section sctnMaster;
	private TreeViewer treeViewer;
	
	//private IEventBroker eventBroker;
	protected EventHandler modelEventHandler = new EventHandler()
	{		
		@Override
		public void handleEvent(Event event)
		{						
			if (StringUtils.equals(event.getTopic(),ModelEventKey.DEFAULT_NEW_MODELEVENT))
			{
				// das neue Object in den TreeView einfuegen
				EObject eObject = (EObject) event.getProperty(IEventBroker.DATA);		
				EObject container = eObject.eContainer();
				if (container instanceof Project)									
					treeViewer.add(treeViewer.getInput(), eObject);									
				else			
					treeViewer.add(container, eObject);					
			
				treeViewer.setSelection(new StructuredSelection(eObject));
				return;
			}
			
			if (StringUtils.equals(event.getTopic(),ModelEventKey.DEFAULT_DELETE_MODELEVENT))
			{				
				// Object aus dem TreeViewer entfernen
				EObject eObject = (EObject) event.getProperty(IEventBroker.DATA);
				treeViewer.remove(eObject);
			}
		}
	};


	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DefaultMasterComposite(Composite parent, int style) 
	{
		super(parent, style);
		
		//eventBroker.subscribe(MASTER_VIEWEVENT+"*",archivViewHandler);
		addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				// ModelObserver abmelden
				ECPUtil.getECPObserverBus().unregister(DefaultMasterComposite.this);
				
				// Default Eventhandler beim Broker abmelden
				Activator.getEventBroker().unsubscribe(modelEventHandler);
				
				toolkit.dispose();
			}
		});
		
		
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(1, false));
		
		sctnMaster = toolkit.createSection(this, Section.TWISTIE | Section.TITLE_BAR);
		sctnMaster.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		toolkit.paintBordersFor(sctnMaster);
		sctnMaster.setText("Master");
		sctnMaster.setExpanded(true);
		
		Composite composite = toolkit.createComposite(sctnMaster, SWT.NONE);
		toolkit.paintBordersFor(composite);
		sctnMaster.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		
		treeViewer = new TreeViewer(composite, SWT.BORDER);
		
		defaultDoubleClickListener = new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				actionDistributor.run(DefaultModelActionDistributor.EDIT_ACTION_ID);
			}
		};
		treeViewer.addDoubleClickListener(defaultDoubleClickListener);

		
		/*
		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				actionDistributor.run(DefaultModelActionDistributor.EDIT_ACTION_ID);
			}
		});
		*/
		
		
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		toolkit.paintBordersFor(tree);
				
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
				Object selObject =  selection.getFirstElement();

				// ersten selektierten Eintrag an ActionDistribution uebergeben
				selectedEObject = null;
				if (selObject instanceof EObject)
					selectedEObject = (EObject) selObject;				
				actionDistributor.setSelectedObject(selectedEObject);
				
				// ist diese Composite in ein ViewDetailsBlock eingebunden, dann auch Details anzeigen
				if(masterDetailsView != null)				
					masterDetailsView.showDetails(selectedEObject);
				
				
				// EventBroker informiert
				//Activator.getEventBroker().post(MASTER_VIEWEVENT_MASTERSELECTION, selection.toArray());
			}
		});
		
		// Observer anmelden (meldet Aenderungen am Modell)
		ECPUtil.getECPObserverBus().register(this);
		
		// Default Eventhandler beim Broker anmelden
		Activator.getEventBroker().subscribe(ModelEventKey.PREFIX_MODELEVENT+"*", modelEventHandler);
	}
	
	/**
	 * Die uebergebenen Aktionen in die Toolbar einbauen.
	 * 
	 * @param modelActions
	 */
	public void createActionToolbar(Map<String, DefaultModelAction> modelActions)
	{
		actionDistributor = new DefaultModelActionDistributor(sctnMaster);		
		Set<String>actionIDs = modelActions.keySet();
		for(String actionID : actionIDs)
			actionDistributor.addAction(actionID, modelActions.get(actionID));
		sctnMaster.setTextClient(actionDistributor.createActionToolbar());		
	}
	


	public Section getSctnMaster()
	{
		return sctnMaster;
	}

	public TreeViewer getMasterViewer()
	{
		return treeViewer;
	}

	/* 
	 * die Meldung einer Modellaenderung wird verarbeitet 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecp.core.util.observer.ECPProjectContentTouchedObserver#contentTouched(org.eclipse.emf.ecp.core.ECPProject, java.util.Collection, boolean)
	 */
	
	@Override
	public void contentTouched(ECPProject project, Collection<Object> objects, boolean structural)
	{
		Object[] objectsArray = objects.toArray(new Object[objects.size()]);
		for (final Object obj : objectsArray)
		{
			if (obj instanceof EObject)
			{
				// Aenderungen im Modell auch im Masterviewer aktualisieren
				final EObject eObject = (EObject) obj;
				
				// existiert das Modellelement noch im Projekt
				if (!((ECPProjectImpl) project).contains(obj))
				{
					// das Objekt wurde geloescht, auch im viewer loeschen
					Display.getDefault().syncExec(new Runnable()
					{
						@Override
						public void run()
						{
							
							//eventBroker.send(ModelEventKeys.VIEWEVENT_UNDOMASTER, eObject);
							//treeViewer.refresh(treeViewer.getInput());
							
							//if(eObject.eContainer() != null)
								//masterDetailsView.detailsComposite.updateWidgets(eObject);
							
							/*
							
							// wegen externer Thread, zuloeschenden Eintrag
							// markieren dann loescchen
							treeViewer.setSelection(new StructuredSelection(obj));
							treeViewer.remove(obj);
							
							if (masterDetailsView != null)
								masterDetailsView.showDetails(null);
							
							if(parentObjj instanceof EObject)
								treeViewer.setSelection(new StructuredSelection(parentObjj));
							
							*/
						}
					});

					return;
				}
				else
				{
					treeViewer.refresh(eObject);
					//treeViewer.add(treeViewer.getInput(),eObject);

					// durch Selection wird auch der DetailComposite aktualisiert
					//if (selectedEObject.equals(eObject))
						//treeViewer.setSelection(new StructuredSelection(eObject));	
					
					//masterDetailsView.detailsComposite.updateWidgets(eObject);
				}
			}
		}
	}

	public void setDefaultDoubleClickListener(IDoubleClickListener doubleClickListener)
	{
		// den bestehenden Listener entfernen
		if(defaultDoubleClickListener != null)
		{
			treeViewer.removeDoubleClickListener(defaultDoubleClickListener);
			defaultDoubleClickListener = null;
		}
		
		if(doubleClickListener != null)
		{			
			defaultDoubleClickListener = doubleClickListener;
			treeViewer.addDoubleClickListener(defaultDoubleClickListener);
		}
	}

	public void setMasterDetailsView(
			DefaultMasterDetailsModelView masterDetailsView)
	{
		this.masterDetailsView = masterDetailsView;
	}

	public DefaultModelActionDistributor getActionDistributor()
	{
		return actionDistributor;
	}
	
	

}
