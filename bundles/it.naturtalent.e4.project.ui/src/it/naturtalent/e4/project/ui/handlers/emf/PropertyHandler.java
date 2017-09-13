 
package it.naturtalent.e4.project.ui.handlers.emf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProjectProperty;
import it.naturtalent.e4.project.INtProjectPropertyFactory;
import it.naturtalent.e4.project.INtProjectPropertyFactoryRepository;
import it.naturtalent.e4.project.NtProjektPropertyUtils;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.e4.project.ui.dialogs.CommitProjectPropertiesDialog;
import it.naturtalent.e4.project.ui.dialogs.PropertyFolderDialog;
import it.naturtalent.e4.project.ui.dialogs.PropertyProjectDialog;
import it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory;
import it.naturtalent.e4.project.ui.handlers.SelectedResourcesUtils;
import it.naturtalent.e4.project.ui.wizards.emf.ProjectPropertyWizard;

public class PropertyHandler extends SelectedResourcesUtils
{
	
	@Optional @Inject private INtProjectPropertyFactoryRepository ntProjektDataFactoryRepository;
	
	@Execute
	public void execute(MPart part, Shell shell, IEclipseContext context)
	{	
		IResource resource = getSelectedResource(part);
		switch (resource.getType())
			{
				case IResource.PROJECT:
					
					
					// neu
					it.naturtalent.e4.project.ui.dialogs.emf.PropertyProjectDialog propertyProjectDialog = new it.naturtalent.e4.project.ui.dialogs.emf.PropertyProjectDialog(
							shell, resource, part);
					
					// classic - PropertyProjectDialog dialog = new PropertyProjectDialog(shell, resource, part);					
					ContextInjectionFactory.invoke(propertyProjectDialog, Persist.class, context);		
										
					if (ntProjektDataFactoryRepository != null)
						propertyProjectDialog.setNtProjectPropertyFactories( ntProjektDataFactoryRepository
								.getAllProjektDataFactories());
					
					// die dem Projekt bereits zugeordneten Properties im Dialog gecheckt darstellen					
					List<INtProjectPropertyFactory> propertyFactories = NtProjektPropertyUtils
							.getProjectPropertyFactories(ntProjektDataFactoryRepository,(IProject) resource);
					
					if(propertyFactories == null)
					{
						// mindestens die obligatorische Eigenschaft muss zugeordnet sein
						propertyFactories = new ArrayList<INtProjectPropertyFactory>();
						propertyFactories.add(ntProjektDataFactoryRepository.getFactory(NtProjectPropertyFactory.class));						
					}
											
					// die obligatorische Projekteigenschaft voruebergehend entfernen (PropertyDialog)
					INtProjectPropertyFactory obligatePropertyFactory = null;
					for(INtProjectPropertyFactory propertyFactory : propertyFactories)
					{
						if(propertyFactory instanceof NtProjectPropertyFactory)
						{
							obligatePropertyFactory = propertyFactory; 
							propertyFactories.remove(propertyFactory);
							break;
						}
					}
													
					// alle aktuell zugeordneten Properties in 'settingFactories' kopieren 
					List<INtProjectPropertyFactory>settingFactories = new ArrayList<INtProjectPropertyFactory>();
					if(propertyFactories != null)
					{
						for(INtProjectPropertyFactory propertyFactory : propertyFactories)
							settingFactories.add(propertyFactory);							
					}	
					
					// 'settingFactories' im Dialog checken
					propertyProjectDialog.setCheckedPropertyFactories(settingFactories);
					
					// PropertyDialog oeffnen
					if(propertyProjectDialog.open() == PropertyProjectDialog.OK)
					{						
						// die im Dialog gecheckten Properties abfragen
						List<INtProjectPropertyFactory> dialogCheckedPropertyFactories = propertyProjectDialog.getCheckedPropertyFactories();

						// mit 'ProjectPropertySettings' erfolgen die persistenten Aktualisierungen
						ProjectPropertySettings projectPropertySettings = new ProjectPropertySettings();
						IProject iProject = (IProject) resource;

						// die abgewaehlten Properties auflisten
						List<INtProjectPropertyFactory>removeableFactories = new ArrayList<INtProjectPropertyFactory>();
						if (propertyFactories != null)
						{		 
							for (INtProjectPropertyFactory property : propertyFactories)
							{
								if (!dialogCheckedPropertyFactories.contains(property))
									removeableFactories.add(property);
							}
						}

						// sollen die abgewahlten PropertyFactories wirklich gelöscht werden						
						if(!removeableFactories.isEmpty())
						{
							CommitProjectPropertiesDialog removableDialog = new CommitProjectPropertiesDialog(shell);
							removableDialog.create();
							removableDialog.setPropertyFactories(((IProject) resource).getName(), removeableFactories);
							if(removableDialog.open() == CommitProjectPropertiesDialog.CANCEL)
							{
								// die abgewaehlten Factories werden wieder hinzugefuegt
								for (INtProjectPropertyFactory removeable : removeableFactories)									
									dialogCheckedPropertyFactories.add(removeable);
							}
							else
							{
								// die abgewaehlten Properties werden endgueltig geloescht
								for (INtProjectPropertyFactory removeable : removeableFactories)
								{
									// abgewaehlte PropertyData loeschen
									INtProjectProperty removeProperty = removeable.createNtProjektData(); 
									removeProperty.setNtProjectID(iProject.getName());
									removeProperty.delete();
								}
							}
						}

						// die obligatorische Projekteigenschaft wieder hinzufuegen
						dialogCheckedPropertyFactories.add(obligatePropertyFactory);

						// die im Dialog gecheckten PropertyFactories persistent speichern
						NtProjektPropertyUtils.saveProjectPropertyFactories(
								iProject.getName(),
								dialogCheckedPropertyFactories);
						
						// Wizard mit den gecheckten PropertyFactories starten
						ProjectPropertyWizard projectPropertyWizard = ContextInjectionFactory.make(ProjectPropertyWizard.class, context);						
						projectPropertyWizard.setPropertyFactories(dialogCheckedPropertyFactories);
						WizardDialog wizardDialog = new WizardDialog(shell, projectPropertyWizard);
						wizardDialog.open();
					
					}
					
					break;
					
					
				case IResource.FOLDER:
					PropertyFolderDialog propFolderdialog = new PropertyFolderDialog(shell);
					ContextInjectionFactory.invoke(propFolderdialog, PostConstruct.class, context);
					propFolderdialog.open();
					break;

				case IResource.FILE:
					
					System.out.println("File");
					break;

				default: break;
			}
	}

	@CanExecute
	public boolean canExecute(MPart part)
	{
		return resourceIsType(getSelectedResource(part), IResource.PROJECT
				| IResource.FOLDER | IResource.FILE);
	}
	
		
}