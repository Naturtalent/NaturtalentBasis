package it.naturtalent.e4.project.ui.actions;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;

import it.naturtalent.application.services.IOpenWithEditorAdapter;
import it.naturtalent.application.services.IOpenWithEditorAdapterRepository;
import it.naturtalent.e4.project.IResourceNavigator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;


/**
 * Die Aktion oeffnet die selektierte Datei mit dem Systemeditor. Vorher wird noch ueberprueft, ob ein Adapter 
 * registriert ist, der das Oeffnen uebernehmen kann
 * 
 * @author dieter
 *
 */
public class SystenOpenEditorAction extends Action
{
	
	@Inject @Optional private Shell shell;
	@Inject @Optional private IEclipseContext context;	
	@Inject @Optional private ESelectionService selectionService;
	@Inject @Optional private IOpenWithEditorAdapterRepository openwithAdapterRepository;
	
	private Log log = LogFactory.getLog(SystenOpenEditorAction.class);
	
	@Override
	public void run()
	{
		Object obj = selectionService.getSelection(ResourceNavigator.RESOURCE_NAVIGATOR_ID);
		if(obj instanceof IResource)
		{
			if(((IResource)obj).getType() ==  IResource.FILE)
			{
				String destPath = null;
				IFile ifile = (IFile) obj;
				String fileName = ifile.getName();
				String ext = FilenameUtils.getExtension(fileName);
				
				// Open mit Adapter nur noch im Kontextmenu 'OeffnenMit...'
				String filePath;
				try
				{
					filePath = FileUtils.toFile(ifile.getLocationURI().toURL()).getPath();
					List<IOpenWithEditorAdapter>openAdapters = openwithAdapterRepository.getOpenWithAdapters();
					for(IOpenWithEditorAdapter openAdapter : openAdapters)
					{					
						if(openAdapter.isExecutable(filePath))
						{
							// die Datei wird ueber einen Adapter geoeffnet
							log.info("mit Adapter öffnen");
							openAdapter.execute(filePath);
							return;
						}
					}
				} catch (MalformedURLException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
	
				// kein Adapter - oeffnen ueber Systemoeffner
				Program prog = Program.findProgram(ext);			
				if (prog != null)
				{
					try
					{						
						File file = FileUtils.toFile(ifile.getLocationURI().toURL());
						log.info("Open with: prog("+file.getPath()+")");
						prog.execute(file.getPath());
					} catch (MalformedURLException e)
					{
						// Message - keine Anwendung zugeordnet
						MessageDialog.openError(shell, Messages.OpenError,
								Messages.bind(Messages.OpenFilesError,
										ext));
					}
				}
				else
				{					
					try
					{
						IResource iResource = (IResource) obj;
						destPath = iResource.getLocation().toOSString();
						
						if (SystemUtils.IS_OS_LINUX)
							Runtime.getRuntime().exec(destPath);
						else
						{
							// in Windows nochmal ueber den Explorer versuchen
							String[] cmdArray = new String[2];
							cmdArray[0] = "explorer";
							cmdArray[1] = destPath;
							Process p = Runtime.getRuntime().exec(cmdArray);
						}

					} catch (Exception exp)
					{
						{
							// Message - keine Anwendung zugeordnet
							MessageDialog.openError(shell, Messages.OpenError,
									Messages.bind(Messages.OpenFilesError,ext));
						}
					}
				}
			}
			else
			{
				if(((IResource) obj).getType() == IResource.FOLDER)
				{
					IResourceNavigator resourceNavigator = it.naturtalent.e4.project.ui.Activator.findNavigator();
					TreeViewer treeViewer = resourceNavigator.getViewer();
					
					// Folder wird expandiert/kollabiert
					if(!treeViewer.getExpandedState(obj))
						treeViewer.expandToLevel(obj, 1);
					else
						treeViewer.collapseToLevel(obj, 1);					
				}
			}
		}
	}
}
