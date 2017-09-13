package it.naturtalent.e4.project.ui.parts.emf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.core.util.observer.ECPProjectContentChangedObserver;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.dialogs.SelectWorkingSetDialog;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.ws.IWorkingSetManager;
import it.naturtalent.e4.project.ui.ws.WorkingSetManager;
import it.naturtalent.emf.model.parts.DefaultDetailsComposite;

public class ProjectViewDetailsComposite extends DefaultDetailsComposite
{

	private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	private ImageHyperlink hyperlink;
	
	private ArrayList<IWorkingSet> assignedWorkingSets = new ArrayList<IWorkingSet>();
	
	@Inject
	@Optional
	private ESelectionService selectionService;
	
	public ProjectViewDetailsComposite(Composite parent, int style)
	{
		super(parent, style);
		setTitle("Projektdetails");
		scrldfrmDetails.setText("Eigenschaften");
	}

	@Override
	public void showDetails(final EObject eObject)
	{				
		Composite scrllfrmComposite = scrldfrmDetails.getBody();
		
		// frueher erstellte Hyperlinks entfernen
		Control [] controls = scrllfrmComposite.getChildren();
		for(Control control : controls)
				control.dispose();
		
		/*
		for(Control control : controls)
		{
			if(control instanceof ImageHyperlink)
				control.dispose();
		}
		*/
		
		// Projekteigenschaften zeigen
		super.showDetails(eObject);
		
		// Hyperlinks der zugeordneten Eigenschaften
		if (eObject instanceof NtProject)
		{
			NtProject ntProject = (NtProject) eObject;
			
			
			
			
			IProject project = null;
			if(StringUtils.isNotEmpty(ntProject.getId()))			
				project = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProject.getId());
			
			final IProject iProject = project;
			if(iProject != null)
			{
				// alle dem Projekt zugeordente PropertyFatories laden
				List<INtProjectProperty>projectProperties  = NtProjektPropertyUtils.getProjectProperties(
						ntProjektDataFactoryRepository, iProject);
				
				// die PropertyAdapter als Hyperlink anzeigen
				for(final INtProjectProperty projectProperty : projectProperties)
				{
					// Hyperlink des jeweiligen Property erzeugen mit 'toString()' als Label
					hyperlink = toolkit.createImageHyperlink(scrllfrmComposite,SWT.BOTTOM);
					hyperlink.setText(projectProperty.toString());
					
					// eine Aktion, falls im PropertyAdapter definiert, ueber den Hyperlink aufrufbar machen 
					final Action action = projectProperty.createAction();
					if (action != null)
					{
						hyperlink.addHyperlinkListener(new HyperlinkAdapter()
						{
							@Override
							public void linkActivated(HyperlinkEvent e)
							{
								action.run();
							}
						});
					}
					
					// die projekteigene Eigenschaft wird ergaenzt durch die WorkingSet-Zuordnung, wenn eine vohanden
					Object obj = projectProperty.getNtPropertyData();
					if(obj instanceof NtProject)
					{						
						String workingSetLabel = getWorkingSetLabel(iProject);
						
						//System.out.println("Label: "+workingSetLabel);
						
						if (workingSetLabel != null)
						{
							hyperlink = toolkit.createImageHyperlink(scrllfrmComposite, SWT.BOTTOM);
							hyperlink.setText(workingSetLabel.toString());
							
							// Hyperlink Projekt - WorkingsSet - Zuordnung Dialog
							hyperlink.addHyperlinkListener(new HyperlinkAdapter()
							{
								@Override
								public void linkActivated(HyperlinkEvent e)
								{
									IWorkingSet [] activeWorkingSets = assignedWorkingSets.toArray(new IWorkingSet[assignedWorkingSets.size()]);
									SelectWorkingSetDialog dialog = new SelectWorkingSetDialog(getShell(), activeWorkingSets);
									if(dialog.open() == SelectWorkingSetDialog.OK)
									{
										// zunaechst Projekt aus allen WorkingSets entfernen
										WorkingSetManager workingSetManager = Activator.getWorkingSetManager();
										IAdaptable [] adaptables = {iProject};
										workingSetManager.removeWorkingSetsElements(adaptables);
												
										// Projekt in alle selektieren WS eintragen
										IWorkingSet [] configResults = dialog.getConfigResult();
										for(IWorkingSet workingSet : configResults)
										{
											IAdaptable[] wsAdaptables = workingSet.getElements();
											wsAdaptables = ArrayUtils.add(wsAdaptables, iProject);
											workingSet.setElements(wsAdaptables);
											
										}
										
										// ResourceNavigator aktualisieren
										IResourceNavigator navigator = Activator.findNavigator();
										navigator.getViewer().refresh();
										
										for(IWorkingSet workingSet : configResults)
											navigator.getViewer().expandToLevel(workingSet, 1);
										
										// diese Seite neu zeigen
										showDetails(eObject);
									}
								}
							});
						}
					}
					
				}
				
				scrldfrmDetails.setSize(scrldfrmDetails.computeSize(500,SWT.DEFAULT));
			}			
		}
	}

	public void setNtProjektDataFactoryRepository(
			INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository)
	{
		this.ntProjektDataFactoryRepository = ntProjektDataFactoryRepository;
	}

	public Section getSectionComposite()
	{
		return sctnDetails;
	}
	
	/**
	 * Die dem Projekt zugeordneten WorkingSets in einem Label zusammenfassen.
	 * 
	 * @param selectedAdaptable - das selektierte Projekt
	 * @return
	 */
	private String getWorkingSetLabel(IAdaptable selectedAdaptable)
	{
		StringBuilder wsLabel = null;
		assignedWorkingSets.clear();	
		IResourceNavigator navigator = Activator.findNavigator();
		IWorkingSet[] workingSets = navigator.getWindowWorkingSets();
		for (IWorkingSet workingSet : workingSets)
		{
			IAdaptable[] adaptables = workingSet.getElements();
			if (ArrayUtils.contains(adaptables, selectedAdaptable))
			{
				String wsName = workingSet.getName();
				if (!StringUtils.equals(wsName,IWorkingSetManager.OTHER_WORKINGSET_NAME))
				{
					assignedWorkingSets.add(workingSet);
					if (wsLabel == null)
						wsLabel = new StringBuilder("WorkingSet: "+wsName);
					else
						wsLabel.append(","+wsName);					
				}
			}
		}		
			
		return (wsLabel != null) ? wsLabel.toString() : null;
	}

}
