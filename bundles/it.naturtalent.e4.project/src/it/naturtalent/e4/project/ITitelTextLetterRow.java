package it.naturtalent.e4.project;

/**
 * Eine Struktur die eine 2 dimensionale Datenstruktur in den Office Anwendungen vereinfacht.
 * 
 * z.B. (Datum | 01.01.2001) = (Titel | Text)
 * 
 * @author dieter
 *
 */
public interface ITitelTextLetterRow
{
	public String getTitel();
	public void setTitel(String titel);
	public String getText();
	public void setText(String text);
}
