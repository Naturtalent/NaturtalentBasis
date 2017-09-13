package it.naturtalent.e4.project.model;

import org.eclipse.swt.dnd.TransferData;

public interface IMailTransfer
{
	IMailData nativeToJava(IMailTransfer transfer, TransferData transferData);
}
