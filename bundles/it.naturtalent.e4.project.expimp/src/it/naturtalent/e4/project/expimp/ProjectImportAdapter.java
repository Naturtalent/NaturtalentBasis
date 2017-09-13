package it.naturtalent.e4.project.expimp;

import it.naturtalent.e4.project.IImportAdapter;
import it.naturtalent.e4.project.expimp.actions.ImportAction;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

public class ProjectImportAdapter implements IImportAdapter
{

	@Override
	public String getContext()
	{
		// TODO Auto-generated method stub
		return null;
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
		return new ImportAction();
	}

	@Override
	public String getMessage()
	{
		return "Projekte importieren";
	}

}
