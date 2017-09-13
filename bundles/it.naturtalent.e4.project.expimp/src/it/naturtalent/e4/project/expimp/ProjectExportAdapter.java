package it.naturtalent.e4.project.expimp;

import java.io.File;

import it.naturtalent.e4.project.IExportAdapter;
import it.naturtalent.e4.project.expimp.actions.ExportAction;
import it.naturtalent.e4.project.expimp.actions.ImportAction;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

public class ProjectExportAdapter implements IExportAdapter
{
	@Override
	public Image getImage()
	{
		return Icon.ICON_PROJECT_OPEN.getImage(IconSize._16x16_DefaultIconSize);
	}

	@Override
	public String getLabel()
	{		
		return "Projekte";
	}

	@Override
	public String getCategory()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action getExportAction()
	{		
		return new ExportAction();
	}

	@Override
	public String getMessage()
	{
		return "Projekte exportieren";
	}

}
