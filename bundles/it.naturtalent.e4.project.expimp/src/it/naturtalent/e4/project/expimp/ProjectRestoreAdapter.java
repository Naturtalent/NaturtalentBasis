package it.naturtalent.e4.project.expimp;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

import it.naturtalent.e4.project.IImportAdapter;
import it.naturtalent.e4.project.expimp.actions.RestoreAction;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

public class ProjectRestoreAdapter implements IImportAdapter
{

	@Override
	public String getContext()
	{		
		return "Restore";
	}

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
	public Action getImportAction()
	{		
		return new RestoreAction();
	}

	@Override
	public String getMessage()
	{
		return "Projekte importieren";
	}

}
