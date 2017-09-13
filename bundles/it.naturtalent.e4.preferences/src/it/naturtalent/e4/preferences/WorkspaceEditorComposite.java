package it.naturtalent.e4.preferences;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

public class WorkspaceEditorComposite extends ListEditorComposite
{

	public WorkspaceEditorComposite(Composite parent, int style)
	{
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doAdd()
	{
		// TODO Auto-generated method stub
		//super.doAdd();
		
		// Dialog Verzeichnis vorbereiten
		DirectoryDialog dlg = new DirectoryDialog(getShell());
		dlg.setText("Verzeichnis auswählen");
		
		// Dialog oeffnen				
		String dir = dlg.open();
		if(StringUtils.isNotEmpty(dir))
		{			
			String [] items = list.getItems();
			if(!ArrayUtils.contains(items, dir))			
				list.add(dir);
		}
	}
	
	@Override
	public Rectangle getBounds()
	{
		Rectangle bounds = super.getBounds();
		bounds.height = 200;
		
		// TODO Auto-generated method stub
		return bounds;
	}

}
