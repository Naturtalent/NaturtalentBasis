package it.naturtalent.emf.model.parts;

import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import it.naturtalent.emf.model.Activator;
import it.naturtalent.emf.model.ModelEventKey;
import it.naturtalent.emf.model.actions.DefaultModelAction;

/**
 * View mit 2 Teilfenstern (MasterComposite u. DetailsComposite).
 * Implementiert eine Ueberwachung der Projektaenderungen.
 * @author dieter
 *
 */
public class DefaultMasterDetailsModelView
{
	protected String viewID; 
	
	protected EPartService service;
	
	protected ECPProject ecpProject; 
	
	protected SashForm sashForm;
	protected DefaultMasterComposite masterComposite;
	protected DefaultDetailsComposite detailsComposite;

	protected @Inject MDirtyable dirtyable;	
	
	private EventHandler modelEventHandler = new EventHandler()
	{		
		@Override
		public void handleEvent(Event event)
		{		
			EditingDomain domain;
			
			String check = event.getTopic();
			if(StringUtils.equals(check, ModelEventKey.DEFAULT_UNDO_MODELEVENT) ||
			StringUtils.equals(check, ModelEventKey.DEFAULT_SHOWDETAILS_MODELEVENT))
			{
				// EditingDomain festlegen
				Object obj = event.getProperty(IEventBroker.DATA);
				if (obj instanceof ECPProject)
				{
					ECPProject ecpProject = (ECPProject) obj;
					domain = ecpProject.getEditingDomain();
				}
				else
				{
					domain = AdapterFactoryEditingDomain
							.getEditingDomainFor((EObject) obj);
					if (domain == null)
					{
						if (ecpProject != null)
							domain = ecpProject.getEditingDomain();
					}
				}

				switch (event.getTopic())
					{
						case ModelEventKey.DEFAULT_UNDO_MODELEVENT:
							dirtyable.setDirty(domain.getCommandStack().canUndo());
							break;

						case ModelEventKey.DEFAULT_SHOWDETAILS_MODELEVENT:
							EObject eObject = (EObject) event.getProperty(IEventBroker.DATA);
							showDetails(eObject);

						default:
							break;
					}
			}

		}
	};
	
	private IPartListener partListener = new IPartListener()
	{		
		@Override
		public void partVisible(MPart part)
		{
			// TODO Auto-generated method stub			
		}
		
		@Override
		public void partHidden(MPart part)
		{
			// TODO Auto-generated method stub			
		}
		
		@Override
		public void partDeactivated(MPart part)
		{
			if(StringUtils.equals(part.getElementId(),viewID))
			{
				dirtyable.setDirty(false);
				service.savePart(part, true);								
			}
		}
		
		@Override
		public void partBroughtToTop(MPart part)
		{
			// TODO Auto-generated method stub			
		}
		
		@Override
		public void partActivated(MPart part)
		{
			// TODO Auto-generated method stub			
		}
	};
		
	/**
	 * Create contents of the view part.
	 */	
	protected void postConstruct(Composite parent)
	{
		parent.setLayout(new GridLayout(1, false));
		
		// zweigeteiltes Fenster
		sashForm = new SashForm(parent, SWT.NONE);		
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		// MasterComposite (linkes Teilfenster) in MasterDetailsBlock einbinden
		masterComposite = new DefaultMasterComposite(sashForm, SWT.NONE);
		masterComposite.setMasterDetailsView(this);
		
		// DetailsComposite (rechtes Teilfenster) in MasterDetailsBlock einbinden
		createDetailsComposite(sashForm);
		
		Activator.getEventBroker().subscribe(ModelEventKey.PREFIX_MODELEVENT+"*", modelEventHandler);
		
		sashForm.setWeights(new int[] {1, 1});		
	}
	
	
	
	public void setService(EPartService service)
	{
		this.service = service;
		service.addPartListener(partListener);
	}



	@Focus
	public void onFocus()
	{
		getMasterViewer().getTree().setFocus();
	}
	
	@PreDestroy
	public void preDestroy()
	{
		 Activator.getEventBroker().unsubscribe(modelEventHandler);
	}
		
	public void showDetails(EObject details)
	{
		if(detailsComposite != null)
			detailsComposite.showDetails(details);
	}
	
	protected Section getMasterSection()
	{
		return (masterComposite.getSctnMaster());
	}
	
	protected TreeViewer getMasterViewer()
	{
		return (masterComposite.getMasterViewer());
	}
	
	protected void createActionToolbar(Map<String, DefaultModelAction> modelActions)
	{
		masterComposite.createActionToolbar(modelActions);
	}
	
	
	
	/**
	 * Eine Standard DetailsComposite einfuegen
	 * @param parent
	 */
	public void createDetailsComposite(Composite parent)
	{
		detailsComposite = new DefaultDetailsComposite(parent, SWT.NONE);
	}



}
