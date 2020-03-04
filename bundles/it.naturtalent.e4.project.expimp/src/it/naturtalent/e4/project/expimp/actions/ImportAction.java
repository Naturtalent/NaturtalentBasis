package it.naturtalent.e4.project.expimp.actions;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.core.ECPProject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.expimp.Messages;
import it.naturtalent.e4.project.expimp.dialogs.ProjectImportDialog;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.ui.datatransfer.CopyFilesAndFoldersOperation;
import it.naturtalent.e4.project.ui.emf.ImportProjectPropertiesOperation;
import it.naturtalent.e4.project.ui.navigator.WorkbenchContentProvider;
import it.naturtalent.e4.project.ui.utils.CreateNewProject;

/**
 * @author dieter
 *
 * Importiert ausgewaehlte NtProjekte aus dem 'ImportVerzeichnis' 
 * 
 * @see it.naturtalent.e4.project.expimp.dialogs.ProjectImportDialog
 */
public class ImportAction extends Action
{
	protected Shell shell;
	private IEventBroker eventBroker;
	
	protected INtProjectPropertyFactoryRepository projektDataFactoryRepository;
	
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
			doRun(dialog);
		}
	}
	
	/*
	 * die eigentliche Importfunktion
	 */
	protected void doRun(ProjectImportDialog dialog)
	{
		// die im Dialog selektierten ImportProjekte abfragen 
		final EObject [] selectedImportObjects = dialog.getSelectedImportNtProjects();
		if(ArrayUtils.isEmpty(selectedImportObjects))
			return;
		
		// Verzeichnis in das die zu importierenden Projekte exportiert wurden
		final File importDir = dialog.getSelectedImportDirectory();
		if(importDir == null)
			return;
		
		//Mapped ProjektId u. Name der selektierten ImportProjekte
		final Map<String,String>createProjectMap = new HashMap<String, String>();
		for(EObject eObject : selectedImportObjects)
		{
			if (eObject instanceof NtProject)
			{
				NtProject ntProject = (NtProject) eObject;
				String projectID = ntProject.getId();
				String projectName = ntProject.getName();
				createProjectMap.put(projectID, projectName);					
			}
		}
		
		// die selektierten NtProjekte im Workspace erzeugen 
		final List<IWorkingSet> selectedWorkingSets = dialog.getAssignedWorkingSets();
		shell.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				if((selectedWorkingSets != null) && (!selectedWorkingSets.isEmpty()))
					WorkbenchContentProvider.newAssignedWorkingSets = selectedWorkingSets.toArray(new IWorkingSet[selectedWorkingSets.size()]);					
				CreateNewProject.createProject(shell, createProjectMap);
				WorkbenchContentProvider.newAssignedWorkingSets = null;
			}
		});

		// NtProjectID und zugehoerige Resourcen in einer Map zusammenfassen
		Map<String, String[]> mapImportFiles = prepareProjectResourceMap(importDir, selectedImportObjects);

		// zum Kopiern mit 'CopyFilesAndFoldersOperation' in das IProject muss der key=NtProjectID in der			
		// ResourceMap ausgetauscht werden durch IProject
		Map<IProject,String[]>importProjectMap = new HashMap<IProject, String[]>();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();			
		for(EObject eObject : selectedImportObjects)
		{
			NtProject ntProject = (NtProject) eObject;
			IProject iProject = workspaceRoot.getProject(ntProject.getId());
			String [] importResources = mapImportFiles.get(ntProject.getId());
			importProjectMap.put(iProject, importResources);
		}
					
		// Die Resourcen in das Project kopieren
		CopyFilesAndFoldersOperation copyFileAndFolder = new CopyFilesAndFoldersOperation(shell);
		copyFileAndFolder.copyFileStores(shell, importProjectMap);

		// die Eigenschaften des Projekts werden ueber die
		// Eigenschaftsadapter ermittelt und
		// in einer fuer jeder Eigenschaft spezifische Date im
		// Projektbereich gespeichet
		// zuerst alle definierten AdapterFactories aus dem Repository laden
		List<INtProjectPropertyFactory> projectPropertyFactories = projektDataFactoryRepository
				.getAllProjektDataFactories();

		// dann die Adapter selbst erzeugen und auflisten
		List<INtProjectProperty> projectPropertyAdapters = new ArrayList<INtProjectProperty>();
		for (INtProjectPropertyFactory propertyFactory : projectPropertyFactories)
			projectPropertyAdapters.add(propertyFactory.createNtProjektData());
		
		// Runnable zum Importieren der Eigenschaften vorbereiten
		Set<String> importedProjectID = mapImportFiles.keySet();
		ImportProjectPropertiesOperation importPropertiesOperation = new ImportProjectPropertiesOperation(
				importedProjectID, projectPropertyAdapters);
		
		
		try
		{
			// Eigenschaften importieren
			new ProgressMonitorDialog(shell).run(true, false, importPropertiesOperation);
		} catch (InvocationTargetException e)
		{
			// Error
			Throwable realException = e.getTargetException();
			MessageDialog.openError(shell, Messages.ExportResources_Error,
					realException.getMessage());
		} catch (InterruptedException e)
		{
			// Abbruch
			MessageDialog.openError(shell, Messages.ExportResources_Cancel,e.getMessage());
			return;
		}	
		
		// ToDo - ist sichergestellt, dass die importierten Eigenschaften korrekt im NtProjektView gezeigt werden 
		
	}
	
	/*
	 * In einer Map werden alle zu einem NtProjekt (key = ProjectID) gehoerenden Resourcen (value = Dateien und Verzeichnisse)
	 * zusammengefasst.
	 */
	protected Map<String, String[]> prepareProjectResourceMap(File sourceImportDir, EObject [] importObjects)
	{
		// zu importierende und vorhandene Projekte separieren
		Map<String, String[]>mapImportFiles = new HashMap<String, String[]>();
		
		for(EObject eObject : importObjects)
		{
			if (eObject instanceof NtProject)
			{
				String projectID = ((NtProject) eObject).getId();
				File importProjectFile = new File(sourceImportDir, projectID);
				if (importProjectFile.exists())
				{
					String [] srcFiles = importProjectFile.list(new FilenameFilter()
					{						
						@Override
						public boolean accept(File dir, String name)
						{							
							return !name.equals(".project");
						}
					});
					
					// das Projektverzeichnis wird vorangestellt
					for(int i = 0;i < srcFiles.length;i++)
						srcFiles[i] = importProjectFile.getPath()+File.separator+srcFiles[i];
					
					mapImportFiles.put(projectID, srcFiles);
				}				
			}			
		}
		return mapImportFiles;
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
