package it.naturtalent.e4.project.expimp;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

import it.naturtalent.e4.project.IExportAdapter;
import it.naturtalent.e4.project.expimp.actions.BackupAction;
import it.naturtalent.e4.project.expimp.actions.ExportAction;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

/**
 * Mit diesen Adapter wird der Backup von NtProjekten ausgefuehrt.
 * Projekte werden in eine Datei gepackt und in einem Verzeichnis gespeichert.
 * 
 * @author dieter
 *
 */
public class ProjectBackupAdapter implements IExportAdapter
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
		return "Backup";
	}

	/* 
	 * NtProjekte exportieren
	 * @see it.naturtalent.e4.project.IExportAdapter#getExportAction()
	 */
	@Override
	public Action getExportAction()
	{		
		return new BackupAction();
	}

	@Override
	public String getMessage()
	{
		return "Projekte sichern";
	}

}
