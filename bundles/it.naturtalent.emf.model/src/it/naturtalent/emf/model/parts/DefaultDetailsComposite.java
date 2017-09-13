package it.naturtalent.emf.model.parts;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.DefaultReferenceService;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContextFactory;
import org.eclipse.emf.ecp.view.spi.provider.ViewProviderHelper;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import it.naturtalent.emf.model.Activator;
import it.naturtalent.emf.model.ModelEventKey;
import it.naturtalent.emf.model.actions.DefaultModelAction;
import it.naturtalent.emf.model.actions.DefaultModelActionDistributor;

public class DefaultDetailsComposite extends Composite
{
	protected final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	
	protected Composite container;
	protected Section sctnDetails;
	
	protected ScrolledForm scrldfrmDetails;
	
	private DefaultModelActionDistributor actionDistributor;
	
	private EObject eObject;
		
	protected EventHandler modelEventHandler = new EventHandler()
	{		
		@Override
		public void handleEvent(Event event)
		{			
			if (StringUtils.equals(event.getTopic(),ModelEventKey.DEFAULT_UNDO_MODELEVENT))
				updateDetailsToolbarsState();
		}
	};

	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DefaultDetailsComposite(Composite parent, int style)
	{
		super(parent, style);
				
		addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				Activator.getEventBroker().unsubscribe(modelEventHandler);
				toolkit.dispose();
			}
		});
		
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(1, false));
		
		sctnDetails = toolkit.createSection(this, Section.TWISTIE | Section.TITLE_BAR);
		sctnDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		toolkit.paintBordersFor(sctnDetails);
		sctnDetails.setText("Details");
		sctnDetails.setExpanded(true);
		
		scrldfrmDetails = toolkit.createScrolledForm(sctnDetails);
		sctnDetails.setClient(scrldfrmDetails);		
		toolkit.paintBordersFor(scrldfrmDetails);
		scrldfrmDetails.setText("New ScrolledForm");
		scrldfrmDetails.getBody().setLayout(new GridLayout(1, false));		
				
		Activator.getEventBroker().subscribe(ModelEventKey.PREFIX_MODELEVENT+"*", modelEventHandler);
	}
	
	public void setTitle(String title)
	{
		sctnDetails.setText(title);
	}
	
	public void setMessage(String message)
	{
		scrldfrmDetails.setText(message);
	}
	
	/**
	 * Die uebergebenen Aktionen in die Toolbar einbauen.
	 * 
	 * @param modelActions
	 */
	protected void createActionToolbar(Map<String, DefaultModelAction> modelActions)
	{
		actionDistributor = new DefaultModelActionDistributor(sctnDetails);		
		Set<String>actionIDs = modelActions.keySet();
		for(String actionID : actionIDs)
			actionDistributor.addAction(actionID, modelActions.get(actionID));
		sctnDetails.setTextClient(actionDistributor.createActionToolbar());		
	}

	
	/**
	 * Den eigentlichen Inhalt von 'EObject' im Detailrahmen 'scrldfrmDetails'  anzeigen.
	 * 
	 * @param eObject
	 * @param detailsView
	 */
	protected void showDetails(EObject eObject)
	{
		this.eObject = eObject;
		
		// alten Inhalt entfernen
		if(container != null)
			container.dispose();
		
		if(eObject != null)
		{			
			container = new Composite(scrldfrmDetails.getBody(), SWT.NONE);			
			container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			toolkit.adapt(container);
			toolkit.paintBordersFor(container);
			container.setLayout(GridLayoutFactory.fillDefaults().margins(10, 10).create());		
			
			createDetailsObject(eObject);
			
			scrldfrmDetails.setSize(scrldfrmDetails.computeSize(SWT.DEFAULT,SWT.DEFAULT));
			sctnDetails.layout();
		}		
	}
	
	protected void createDetailsObject(EObject eObject) 
	{	
		try
		{
			ViewModelContext vmc = ViewModelContextFactory.INSTANCE.createViewModelContext(
							ViewProviderHelper.getView(eObject, null),
							eObject, new DefaultReferenceService());

			ECPSWTViewRenderer.INSTANCE.render(container,vmc);

		} catch (ECPRendererException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DefaultModelActionDistributor getActionDistributor()
	{
		return actionDistributor;
	}


	/**
	 * Status der Toolbaraktionen aktualisieren.
	 * Enable wenn keine weiteren undo-Commands vorhanden.  
	 *  
	 * @param eObject
	 */
	protected void	updateDetailsToolbarsState()
	{
		DefaultModelActionDistributor actionDistributor = getActionDistributor();
		
		if((actionDistributor != null) && (eObject != null))
		{
			EditingDomain domain  = AdapterFactoryEditingDomain.getEditingDomainFor(eObject);
			
			if (domain != null)
			{
				boolean undo = false;
				if (domain != null)
					undo = domain.getCommandStack().canUndo();

				// Status der Aktionen Save- and Undo aktualisieren				
				actionDistributor.setEnable(DefaultModelActionDistributor.UNDO_ACTION_ID, undo);
				actionDistributor.setEnable(DefaultModelActionDistributor.SAVE_ACTION_ID, undo);
			}
		}
	}
	

	
}
