package it.naturtalent.e4.project.ui;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

import it.naturtalent.application.services.INewActionAdapter;
import it.naturtalent.e4.project.ui.actions.NewFileAction;
import it.naturtalent.e4.project.ui.actions.NewFolderAction;
import it.naturtalent.e4.project.ui.actions.emf.NewProjectAction;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;


public class NewFileAdapter implements INewActionAdapter
{

	@Override
	public String getLabel()
	{		
		return "Datei";
	}

	@Override
	public Image getImage()
	{
		return Icon.DIALOG_NEW_FILE.getImage(IconSize._16x16_DefaultIconSize);
	}

	@Override
	public String getCategory()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends Action> getActionClass()
	{		
		return NewFileAction.class;
	}

}
