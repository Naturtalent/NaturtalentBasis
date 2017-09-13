package it.naturtalent.e4.project.ui.datatransfer;

import it.naturtalent.e4.project.model.IMailData;
import it.naturtalent.e4.project.model.IMailTransfer;
import it.naturtalent.e4.project.ui.Activator;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;


/**
 * OS unabhaengige Stellvertreterklasse fuer den Transfer von Maildateien 
 */
public class MailTransfer extends ByteArrayTransfer implements IMailTransfer
{
	
	private static Object obj; 
	
	/**
	 * Konstruktion
	 * 
	 * Returns the singleton gadget transfer instance.
	 */
	public static Transfer getInstance()
	{
		// os-abheangige Instance ueber den Context ermitteln 
		IEclipseContext context = Activator.workbenchContext;
		obj = context.get(IMailData.MSOUTLOOKMAILTRANSFER);
		
		if (obj != null)
			return (Transfer) obj;
		
		// keine Mailtransfer Klasse definiert
		return null;
	}

	/**
	 * 'private' (verhindert explizite Konstruktion)
	 */
	private MailTransfer()
	{
	}

	@Override
	public String[] getTypeNames()
	{	
		/*
		if(mailTransfer != null)
			return mailTransfer.getTypeNames();
			*/
		
		return null;
	}

	@Override
	public int[] getTypeIds()
	{		
		/*
		if(mailTransfer != null)
			return mailTransfer.getTypeIds();
			*/
					
		return null;
	}

	
	public IMailData nativeToJava(IMailTransfer transfer, TransferData transferData)
	{
		return transfer.nativeToJava(transfer, transferData);
	}
	
	
	

	
}