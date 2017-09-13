package it.naturtalent.e4.project.model;

/**
 * Interface der Datenstruktur fuer den Import von eMails. 
 * 
 * @author dieter
 *
 */
public interface IMailData
{
	
	public static final String MSOUTLOOKMAILTRANSFER = "it.naturtalent.e4.mail.msoutlook.OutlookMailTransfer";
	
	/*
	 * eMail's (Maildateien und Anlagen) werden in separaten temporaeren Dateien gespeichet.
	 * Die Funktion liefert ein Array mit den Pfadangaben zu den zwischengespeicherten Mail-Dateien.
	 */
	public String [] getMailFiles ();

	public String [] getFilenames ();
}
