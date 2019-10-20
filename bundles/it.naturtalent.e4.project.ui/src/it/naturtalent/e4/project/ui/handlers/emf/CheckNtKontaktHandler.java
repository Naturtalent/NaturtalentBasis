 
package it.naturtalent.e4.project.ui.handlers.emf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Named;
import javax.xml.bind.JAXB;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecp.spi.ui.util.ECPHandlerHelper;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.IProjectData;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ProjectPropertyData;
import it.naturtalent.e4.project.ProjectPropertySettings;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.model.project.ProjectFactory;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.dialogs.emf.CheckNtKontakteDialog;
import it.naturtalent.e4.project.ui.emf.NtProjectPropertyFactory;

/**
 * Hilfsklasse dient der einmaligen Umstellung von ProjectData auf PropertyData
 * 
 * @author dieter
 *
 */
public class CheckNtKontaktHandler
{	
	
	private final static String PROJECTPROPERTYFILE = "propertyData.xml";
	private final static String PROJECTDATAFILE = "projectData.xml";
	
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL)@Optional Shell shell )
	{
		CheckNtKontakteDialog checkDialog = new CheckNtKontakteDialog(shell);
		checkDialog.open();
	}
	
	@CanExecute
	public boolean canExecute()
	{
		NtProjects ntProjects = Activator.getNtProjects();
		return (ntProjects != null);
	}
	
	
	private void doMigrate()
	{		
	}
}