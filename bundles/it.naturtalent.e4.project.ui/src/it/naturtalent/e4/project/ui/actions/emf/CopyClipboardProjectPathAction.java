package it.naturtalent.e4.project.ui.actions.emf;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import it.naturtalent.emf.model.actions.DefaultModelAction;
import it.naturtalent.icons.core.Icon;
import it.naturtalent.icons.core.IconSize;

public class CopyClipboardProjectPathAction extends DefaultModelAction
{

	// der benutzte Zwischenspeicher
	private static final Clipboard clipboard  = new Clipboard(Display.getDefault());
	
	public CopyClipboardProjectPathAction(StructuredViewer viewer)
	{
		super(viewer);
		setImageDescriptor(Icon.COMMAND_COPY.getImageDescriptor(IconSize._16x16_DefaultIconSize));
		setEnabled(false);
	}
	
	@Override
	public boolean canRun()
	{
		IStructuredSelection selection = viewer.getStructuredSelection();
		Object selObject = selection.getFirstElement();
		if (selObject instanceof IResource)
		{
			IResource iResource = (IResource) selObject;			
			if((iResource.getType() & (IResource.FOLDER) | (IResource.PROJECT) | (IResource.FILE)) != 0)
				return true;
		}
		
		return false;
	}
	
	@Override
	public void run()
	{		
		IStructuredSelection selection = viewer.getStructuredSelection();
		Object selObject = selection.getFirstElement();
		if (selObject instanceof IResource)
		{
				IResource iResource = (IResource) selObject;
				clipboard.setContents(new Object[]
						{ iResource.getLocation()
								.toOSString() }, new Transfer[]
						{ TextTransfer.getInstance() });
		}
	}

	

}
