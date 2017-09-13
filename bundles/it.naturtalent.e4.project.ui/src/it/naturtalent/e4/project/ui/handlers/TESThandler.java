 
package it.naturtalent.e4.project.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

public class TESThandler
{
	@Execute
	public void execute()
	{
		System.out.println("verhindert das Enablen des Menuitems Öffnen mit");
	}

}