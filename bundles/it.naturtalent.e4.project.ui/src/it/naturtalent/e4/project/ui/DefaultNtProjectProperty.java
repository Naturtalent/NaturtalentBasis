package it.naturtalent.e4.project.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.spi.ui.util.ECPHandlerHelper;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.DialogMessageArea;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.emf.model.ModelEventKey;
import it.naturtalent.emf.model.ModelEventKeys;



/**
 * Realisiert die EMF Variante des Interface INtProjectProperty. Die Properties sind vom Typ 'EObject'. 
 * 
 * @author dieter
 *
 */
public class DefaultNtProjectProperty implements INtProjectProperty
{
	// mit diesem EventKey informiert der Broker Undo - Events
	protected String undoEventKey;
	
	// mit diesem EventKey informiert der Broker Delete - Events
	protected String deleteEventKey;
	
	// ID des Projekts, auf das sich die Eigenschaft bezieht
	protected String ntProjectID;
		
	// die eigentlichen Propertydaten
	protected EObject ntPropertyData;
	
	// in diesem ECP-Projekt befinden sich die Daten
	protected ECPProject ecpProject;
	
	
	// Zugriff auf EventBroker
	MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();
	protected IEventBroker eventBroker = currentApplication.getContext().get(IEventBroker.class);

	// steuert das Verhalten des Wizards (z.B. beim Speichern)
	protected int wizardmodus = ADD_WIZARD_MODUS;
	
	@Override
	public String getNtProjectID()
	{
		return ntProjectID;
	}

	@Override
	public void setNtProjectID(String ntProjectID)
	{
		this.ntProjectID = ntProjectID;
	}
	
	public EObject getNtPropertyData()
	{
		return ntPropertyData;
	}

	public void setNtPropertyData(EObject ntProjectProperty)
	{
		this.ntPropertyData = ntProjectProperty;
	}

	@Override
	public String getLabel()
	{
		// TODO Auto-generated method stub
		return "DefaultProjectProperty";
	}

	@Override
	public Object init()
	{
		return ntPropertyData;
	}

	/* 
	 * ProjektProperty speichern
	 * 
	 * (non-Javadoc)
	 * @see it.naturtalent.e4.project.INtProjectProperty#saveNtProjectProperty()
	 */
	@Override
	public void commit()
	{	
		ECPHandlerHelper.saveProject(ecpProject);
		eventBroker.send(undoEventKey, ntPropertyData);
	}	
	
	@Override
	public void undo()
	{
		if(ntPropertyData != null)
		{
			EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(ntPropertyData);			
			if (domain != null)
			{
				while (domain.getCommandStack().canUndo())
					domain.getCommandStack().undo();
				
				eventBroker.send(undoEventKey, ntPropertyData);
			}
		}		
	}

	@Override
	public void delete()
	{		
		if (ecpProject != null)
		{
			List<Object> delObjects = new ArrayList<Object>();
			delObjects.add(ntPropertyData);
			
			System.out.println("ntProperty: "+ntPropertyData);
			
			ecpProject.deleteElements(delObjects);		
			eventBroker.send(ModelEventKey.DEFAULT_DELETE_MODELEVENT, ntPropertyData);
			
			ECPHandlerHelper.saveProject(ecpProject);
			eventBroker.send(deleteEventKey, ntPropertyData);
		}				
	}

	@Override
	public IWizardPage createWizardPage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override	
	public Action createAction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	public void setUndoEventKey(String undoEventKey)
	{
		this.undoEventKey = undoEventKey;
	}
	*/
	
	/*
	public void setDeleteEventKey(String deleteEventKey)
	{
		this.deleteEventKey = deleteEventKey;
	}
	*/

	@Override
	public void exportProperty()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean importProperty(Object importData)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	
	/*

	@Override
	public void setWizardModus(int modus)
	{
		wizardmodus = modus;		
	}
*/


}
