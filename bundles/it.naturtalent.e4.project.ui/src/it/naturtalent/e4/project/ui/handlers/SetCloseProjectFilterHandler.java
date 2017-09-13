 
package it.naturtalent.e4.project.ui.handlers;

import it.naturtalent.e4.project.IProjectDataFactory;
import it.naturtalent.e4.project.NaturtalentConstants;
import it.naturtalent.e4.project.NtProject;
import it.naturtalent.e4.project.ProjectData;
import it.naturtalent.e4.project.ui.Activator;
import it.naturtalent.e4.project.ui.Messages;
import it.naturtalent.e4.project.ui.NtPreferences;
import it.naturtalent.e4.project.ui.filters.ResourceFilterProvider;
import it.naturtalent.e4.project.ui.navigator.ResourceNavigator;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.wb.swt.ResourceManager;

public class SetCloseProjectFilterHandler
{
	public class ShowClosedProjectFilter extends ViewerFilter
	{

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element)
		{
			
			if (element instanceof IProject)
				return (!((IProject) element).isOpen());
						
			return true;
		}

	}

	private static boolean originalTopLevelState = false;
	
	private ResourceNavigator navigator;
	
	private IBaseLabelProvider labelProvider;
	
	
	@Execute
	public void execute(
			MDirectMenuItem directHandleMenu,
			@Preference(nodePath = NtPreferences.ROOT_PREFERENCES_NODE)
			IEclipsePreferences preferences, @Optional final
			IProjectDataFactory projectDataFactory)
	{
		if (navigator != null)
		{
			boolean closeState = switchMenu(directHandleMenu);
			if (!closeState)
			{
				// original TopLevel-Status sichern und ProjektSicht erzwingen
				originalTopLevelState = preferences.getBoolean(
						NaturtalentConstants.WORKINGSET_AS_TOPLEVEL,
						originalTopLevelState);
				preferences.putBoolean(
						NaturtalentConstants.WORKINGSET_AS_TOPLEVEL, false);
				
				TreeViewer viewer = navigator.getViewer();
				viewer.setFilters(new ViewerFilter []{new ShowClosedProjectFilter()});
				labelProvider = viewer.getLabelProvider();
				viewer.setLabelProvider(new LabelProvider(){

					@Override
					public Image getImage(Object element)
					{
						if(element instanceof IProject)
						{
							ImageDescriptor imgDesc = ResourceManager.getPluginImageDescriptor(Activator.PLUGIN_ID, "/icons"+File.separator+"project_close.gif");
							return imgDesc.createImage();
						}

						return super.getImage(element);
					}

					@Override
					public String getText(Object element)
					{
						if(element instanceof IProject)
						{
							IProject iProject = (IProject) element;
							NtProject ntProject = new NtProject(iProject);							
							ProjectData projectData = projectDataFactory.getProjectData(ntProject);								
							return projectData.getName();
						}
						return super.getText(element);
					}					
				});
				
			}
			else
			{		
				// wieder die Originalfilter 
				ResourceFilterProvider filterProvider = navigator.getFilterProvider();				
				TreeViewer viewer = navigator.getViewer();				
				viewer.setFilters(filterProvider.getFilters());
				viewer.setLabelProvider(labelProvider);

				// original TopLevel-Status wiederherstellen
				preferences.putBoolean(
						NaturtalentConstants.WORKINGSET_AS_TOPLEVEL,
						originalTopLevelState);
				
				//navigator.initDefaultResources();
				//navigator.updateResourceSet();
				navigator.refreshViewer();
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean switchMenu(MDirectMenuItem directHandleMenu)
	{
		boolean closeState = !directHandleMenu.isSelected();
				
		MElementContainer container = directHandleMenu.getParent();
		List<MMenuElement> menus = container.getChildren();
		for(MMenuElement menu : menus)
		{
			if (StringUtils.equals(menu.getLabel(), "Top Level Elements"))				
				menu.setVisible(closeState);

			if (StringUtils.equals(menu.getLabel(),
					Messages.ResourceNavigator_ConfigureWorkingSet))
				menu.setVisible(closeState);
		}
		return closeState;
	}

	@CanExecute
	public boolean canExecute()
	{
		// TODO Your code goes here
		return true;
	}
	
	@Inject
	public void	setSelection(@Named(IServiceConstants.ACTIVE_PART)@Optional MPart part)
	{
		if(part != null)
		{
			Object obj = part.getObject();
			if(obj instanceof ResourceNavigator)
				navigator = (ResourceNavigator) obj;
		}		
	}

		
}