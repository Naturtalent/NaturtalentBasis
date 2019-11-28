package it.naturtalent.e4.search;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.operation.IRunnableWithProgress;

import it.naturtalent.e4.project.INtProject;
import it.naturtalent.e4.project.model.project.NtProject;
import it.naturtalent.e4.project.model.project.NtProjects;
import it.naturtalent.e4.project.search.ISearchInEclipsePage;
import it.naturtalent.e4.search.DiagnoseSearchPage.DiagnoseCheckEnum;

/**
 * Die eigentliche Diagnosesuchfunktion zum Ablauf in einem RunnablePrrogress.
 * 
 * (momentan werden Unterverzeichnisse nicht beruecksichtigt)
 * 
 * @author dieter
 *
 */
public class DiagnoseSearchOperation implements IRunnableWithProgress
{
	private IEventBroker eventBroker;
	
	private DiagnoseCheckEnum diagnoseCheck;
	
	private Log log = LogFactory.getLog(this.getClass());

	
	public DiagnoseSearchOperation(DiagnoseCheckEnum diagnoseCheck)
	{
		super();
		
		this.diagnoseCheck = diagnoseCheck;
		
		MApplication currentApplication = E4Workbench.getServiceContext().get(IWorkbench.class).getApplication();		
		eventBroker = currentApplication.getContext().get(IEventBroker.class);
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
	{
		int hitCount = 0;
		
		// Diagnose durchsucht ime alle IProjekte
		IProject [] iProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		monitor.beginTask("Suche im Workspace", iProjects.length);	//$NON-NLS-N$
		
		// Broker meldet der Start einer neuen Suche
		eventBroker.post(ISearchInEclipsePage.START_SEARCH_EVENT, "Start der Suche"); //$NON-NLS-N$
		
		// gibt es zu eimem ntProjekt ein entsprechendes iProject
		if(diagnoseCheck == DiagnoseCheckEnum.NOREALPROJECT)
		{
			NtProjects ntProjects = it.naturtalent.e4.project.ui.Activator.getNtProjects();
			if (ntProjects != null)
			{
				// die Summe der NtProjekte ist die Ausgangsmende der Suche
				EList<NtProject> allNtProjects = ntProjects.getNtProject();
				for (NtProject ntProject : allNtProjects)
				{
					IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(ntProject.getId());
					if(!iProject.exists())
					{
						// Broker meldet das Fehlen eines iProjects unter dieser Id
						eventBroker.post(ISearchInEclipsePage.MATCH_PATTERN_EVENT, ntProject.getName()+"  |  "+ntProject.getId());
						hitCount++;	
					}
					
					monitor.worked(1);
				}
			}
		}
		else
		{		
			// alle Suchitems (Projecte) durchlaufen
			for (IProject iProject : iProjects)
			{
				if (monitor.isCanceled())
				{
					throw new InterruptedException();
				}

				switch (diagnoseCheck)
					{
						case NOPROJECTDIR:

							// hat das Projekt eine Entsprechung im Filesystem
							File projectFile = iProject.getFullPath().toFile();
							projectFile = iProject.getLocation().toFile();
							if (!projectFile.exists())
							{
								eventBroker.post(ISearchInEclipsePage.MATCH_PATTERN_EVENT,iProject.getName());
								hitCount++;
							}

							break;

						case NOPROJECTNAME:

							// ist ProjektID in der Modeldatenbank gespeichert
							String id = iProject.getName();
							NtProject ntProject = it.naturtalent.e4.project.ui.Activator
									.findNtProject(id);
							if (ntProject == null)
							{
								eventBroker.post(
										ISearchInEclipsePage.MATCH_PATTERN_EVENT,
										id);
								hitCount++;
							}
							break;

						case NOQUALIFIEDPROJECTNAME:

							try
							{
								String name = iProject.getPersistentProperty(
										INtProject.projectNameQualifiedName);
								if (StringUtils.isEmpty(name))
								{
									eventBroker.post(
											ISearchInEclipsePage.MATCH_PATTERN_EVENT,
											iProject.getName());
									hitCount++;
								}

							} catch (CoreException e)
							{
								log.info("Fehler: kein Projektname oder Projekt geschlossen ID:"+iProject.getName());
								eventBroker.post(
										ISearchInEclipsePage.MATCH_PATTERN_EVENT,
										iProject.getName());
								hitCount++;
								e.printStackTrace();
							}

							break;

						default:
							break;
					}

				monitor.worked(1);
			}
		}

		monitor.done();
		
		eventBroker.post(ISearchInEclipsePage.END_SEARCH_EVENT, "Anzahl der Treffer: "+hitCount);	//$NON-NLS-N$	
	}
	


}
