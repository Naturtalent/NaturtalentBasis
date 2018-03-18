package it.naturtalent.e4.project.expimp.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.ecp.spi.ui.util.ECPHandlerHelper;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.expimp.dialogs.ProjectImportDialog;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.datatransfer.CopyFilesAndFoldersOperation;
import it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;
import it.naturtalent.e4.project.ui.parts.emf.NtProjectView;

/**
 * @author dieter
 *
 * Importiert ausgewaehlte NtProjekte die in das 'ImportVerzeichnis' exportiert wurden.
 * 
 */
public class ImportAction extends Action
{
	private Shell shell;
	private IEventBroker eventBroker;
	
	private INtProjectPropertyFactoryRepository projektDataFactoryRepository;
	
	private static Log log = LogFactory.getLog(ImportAction.class);

	@PostConstruct
	public void postConstruct(UISynchronize sync,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
			@Optional IEventBroker eventBroker,
			@Optional INtProjectPropertyFactoryRepository projektDataFactoryRepository)
	{
		this.shell = shell;
		this.eventBroker = eventBroker;
		this.projektDataFactoryRepository = projektDataFactoryRepository;
	}
		
	@Override
	public void run()
	{
		// mit dem Dialog das Quellverzeichnis der Importdaten auswaehlen		
		ProjectImportDialog dialog = new ProjectImportDialog(shell);
		if(dialog.open() == ProjectImportDialog.OK)
		{
			// die im Dialog selektierten ImportProjekte abfragen 
			final EObject [] selectedImportObjects = dialog.getResultImportSource();
			
			// Verzeichnis in das die zu importierenden Projekte exportiert wurden
			final File importDir = new File(dialog.getImportSourceDirectory());
			
			// Operation Import vorbereiten (NtProjekte und diverse Hilfskonstruktionen (Maps) erzeugen)
			final ImportProjectPrepareOperation importProjectPrepareOperation = new ImportProjectPrepareOperation(
					shell, importDir, selectedImportObjects, dialog.getAssignedWorkingSets());
			try
			{
				// mit der Operation 'importProjectPrepareOperation' den ImportProzess vorbereiten 
				new ProgressMonitorDialog(shell).run(true, false, importProjectPrepareOperation);
			} catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Map mit IProjecten und den zugeorigen Resourcen
			final Map<IProject, String[]>importProjectMap = importProjectPrepareOperation.getImportProjectMap();
			BusyIndicator.showWhile(shell.getDisplay(), new Runnable()
			{
				@Override
				public void run()
				{
					// Die Resourcen in das Project kopieren
					CopyFilesAndFoldersOperation copyFileAndFolder = new CopyFilesAndFoldersOperation(shell);
					copyFileAndFolder.copyFileStores(shell, importProjectMap);

					// die ProjectProperties importieren
					List<NtProject>ntProjects = Activator.getNtProjects().getNtProject();
					for(EObject importObject : selectedImportObjects)
						ntProjects.add((NtProject) importObject);
					Activator.getECPProject().saveContents();
					
					// im Nachgang alle Adapter aufrufen
					List<INtProjectPropertyFactory> projectPropertyFactories = projektDataFactoryRepository
							.getAllProjektDataFactories();
					for(INtProjectPropertyFactory propertyFactory : projectPropertyFactories)
					{
						// koennen ueber den Adapter projectgekoppelte PropertyDaten geladen werden
						INtProjectProperty propertyAdapter = propertyFactory.createNtProjektData();
						
						// alle betroffenen Projekte (Projekte mit der Eigenschaft 'factory'  durchlaufen
						for(EObject importObject : selectedImportObjects)
						{
							// Importfunktion des Adapters aufrufen
							NtProject ntProject = (NtProject) importObject;
							propertyAdapter.setNtProjectID(ntProject.getId());
							propertyAdapter.importProperty(importDir);							
						}
					}
				}
			});
		}
	}

	/*
	 * Die exportierten Properties mit Hilfe der EMFProperty-Datei '.xmi' importieren.   
	 * Mit der ProjectPropertyFactory 'projectPropertyFactory' kann der jeweilige ProjectPropertyAdapter generiert werden.
	 * Mit dem ProjectPropertyAdapter koennen die fuer den Import selektierten NtProjekte('selectedImportProjectID')
	 * gefiltert und im jeweiligen Container gespeichert werden.
	 * 
	 */
	private void importProjectPropertyOLD(File importDir,
			String projectPropertyFactory,
			List<String> selectedImportProjectIDs)
	{
		// Factory ueber den Namen aus dem Repository laden
		INtProjectPropertyFactory factory = projektDataFactoryRepository.getFactoryByName(projectPropertyFactory);
		if(factory != null)
		{
			String factoryName = factory.getParentContainerName();
			File importEMFPropertyFile = new File(importDir,factoryName + ".xmi");
			if (importEMFPropertyFile.exists())
			{
				// alle exportierten Properties laden
				URI fileURI = URI.createFileURI(importEMFPropertyFile.getPath());
				ResourceSet resourceSet = new ResourceSetImpl();
				Resource resource = resourceSet.getResource(fileURI, true);
				EList<EObject> importObjects = resource.getContents();

				// mit dem zugeoerigen Adapter werden die selektierten Projekte gefiltert und gespeichet
				INtProjectProperty adapter =  factory.createNtProjektData();
				Object parentObject = adapter.getPropertyContainer();
								
				for(EObject importObject : importObjects)
				{					
					adapter.setNtPropertyData(importObject);
					String id = adapter.getNtProjectID();
					if(selectedImportProjectIDs.contains(id))
					{
						// nur die Objecte deren ProjektID zu den Selektierten gehoert werden importiert
						importEObject(parentObject, importObject);
						
						// pesistent speichern
						adapter.commit();		
					}
				}
			}
		}
	}
	
	/*
	 * Ein einzelnes ImportObject im Containerobjekt speichern.
	 * 
	 */
	private void importEObject(final Object parentObject, final EObject eObjectImport)
	{
		if (parentObject instanceof EObject)
		{	
			// Referenz auf diesen Object suchen
			for (final EReference ref : ((EObject) parentObject).eClass().getEAllContainments())
			{
				if (ref.getEReferenceType().isInstance(eObjectImport))
				{
					// im ParentObjekt zur Referenz hinzufuegen-/ bzw. diese ersetzen
					final EditingDomain editingDomain = AdapterFactoryEditingDomain
							.getEditingDomainFor(parentObject);
					if (ref.isMany())
					{
						editingDomain.getCommandStack().execute(
								new AddCommand(editingDomain,
										(EObject) parentObject, ref,
										EcoreUtil.copy(eObjectImport)));
					}
					else
					{
						editingDomain.getCommandStack().execute(
								new SetCommand(editingDomain,
										(EObject) parentObject, ref,
										EcoreUtil.copy(eObjectImport)));
					}
					break;
				}
			}
		}				
		else if (parentObject instanceof ECPProject) 
		{
			// Parent selbst ist ECPProject, importObjekt hinzufuegen
			final EditingDomain editingDomain = ((ECPProject) parentObject).getEditingDomain();
			editingDomain.getCommandStack().execute(new ChangeCommand(eObjectImport) 
			{
				@Override
				protected void doExecute() 
				{
					((ECPProject) parentObject).getContents().add(EcoreUtil.copy(eObjectImport));
				}
			});
		}
	}
	
	/*
	 * Alle PropertyFactoryNamen der zuimportierenden Objecte in einer Liste aufsammeln.
	 */
	/*
	private Map<String,List<EObject>> pickUpPropertyFactories(File importDir, EObject [] importObjects)
	{
		Map<String,List<EObject>>propertyFactories = new HashMap<String, List<EObject>>();
		
		for(EObject importObject : importObjects)
		{
			if (importObject instanceof NtProject)
			{
				String projectID = ((NtProject) importObject).getId();
				File importProjectFile = new File(importDir, projectID);
				if (importProjectFile.exists())
				{
					File projectDataDir = new File(importProjectFile,IProjectData.PROJECTDATA_FOLDER);
					File propertyFile = new File(projectDataDir, ProjectPropertyData.PROP_PROPERTYDATACLASS + ".xml");
					try
					{
						InputStream in = new FileInputStream(propertyFile);
						ProjectPropertyData projectPropertyData = JAXB.unmarshal(in, ProjectPropertyData.class);
						String [] factoryNames = projectPropertyData.getPropertyFactories();
						for(String factoryName : factoryNames)
						{
							if(!propertyFactories.containsKey(factoryName))
							{
								// Key bisher noch nicht vorhanden - Value Liste neue anlegen
								List<EObject> eObjectList = new ArrayList<EObject>();
								eObjectList.add(importObject);
								propertyFactories.put(factoryName, eObjectList);
							}
							else
							{
								List<EObject> eObjectList = propertyFactories.get(factoryName); 
								eObjectList.add(importObject);
							}
						}						
					} catch (FileNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
			}			
		}		
		
		return propertyFactories;
	}
	*/
	
}
