package it.naturtalent.emf.model;

import java.util.Collection;
import java.util.List;

import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.util.observer.ECPProjectContentTouchedObserver;
import org.eclipse.emf.ecp.internal.core.ECPProjectImpl;
import org.eclipse.emf.ecp.ui.e4.editor.ECPE4Editor;
import org.eclipse.swt.widgets.Display;


/**
 * @author dieter
 *
 * Ueberwacht Aktionen im ECPProjekt und reagiert entsprechen.
 * 
 * - wird ein Element geloescht, dann wird auch das entsprechende Editorfenster geschlossen
 * 
 */

public class NtECPProjectContentTouchedObserver implements ECPProjectContentTouchedObserver
{

	// ID des Stacks indem alle ModellElement - Editorfenster angezeigt werden
	private final static String EDITOR_PARTSTACK_ID = "it.naturtalent.emf.ecp.model.partstack.editors";
	
	@Override
	public void contentTouched(ECPProject project, Collection<Object> objects,
			boolean structural)
	{
		if(project instanceof ECPProjectImpl)
		{						
			/*
			 * Reaktion auf das Loeschen eines Modellelements
			 */
			Object obj = objects.iterator().next();			
			if(obj instanceof EObject) 
			{
				// existiert ein EditorModelFenster
				final MPart existingPart = findEditorPart(obj);
				if (existingPart != null)
				{
					// existiert das Modellelement noch im Projekt 
					if (!((ECPProjectImpl) project).contains(obj))
					{
						// entsprechendes Editorfenster entfernen
						Display.getDefault().syncExec(new Runnable(){
							
							@Override
							public void run()
							{
								MElementContainer<MUIElement> parent = existingPart.getParent();
								List<MUIElement>elements = parent.getChildren();
								elements.remove(0);
							}							
						});
					}
				}
			}
		}
	}
	
	/*
	 * sucht in der Editorperspektive nach dem 'modelElement' - Editorfenster 
	 */
	private MPart findEditorPart(Object modelElement)
	{
		MApplication application = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();		
		EModelService modelService = (EModelService) application.getContext()
                .get(EModelService.class.getName());
		MPartStack editorPartStack = (MPartStack) modelService.find(EDITOR_PARTSTACK_ID, application);
		
		List<MStackElement>partElements = editorPartStack.getChildren();
		for(MStackElement partElement : partElements)
		{
			if(partElement instanceof MPart)
			{
				MPart part = (MPart) partElement;				
				if (((MPart) partElement).getContext().get(ECPE4Editor.INPUT) == modelElement)				
					return (MPart) partElement; 
			}
		}
		
		return null;
	}
	

}
