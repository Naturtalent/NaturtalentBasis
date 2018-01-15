package it.naturtalent.e4.project.expimp;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

import it.naturtalent.e4.project.IExportAdapter;
import it.naturtalent.e4.project.expimp.actions.ExportAction;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

/**
 * Ueber diesen Adapter wird der ExportTask (NtProjekte exportieren) ausgefuehrt.
 * 
 * @author dieter
 *
 */
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

	/* 
	 * NtProjekte exportieren
	 * @see it.naturtalent.e4.project.IExportAdapter#getExportAction()
	 */
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
